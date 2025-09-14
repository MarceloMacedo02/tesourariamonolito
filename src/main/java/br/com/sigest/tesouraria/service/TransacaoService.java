package br.com.sigest.tesouraria.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import br.com.sigest.tesouraria.domain.entity.Cobranca;
import br.com.sigest.tesouraria.domain.entity.Fornecedor;
import br.com.sigest.tesouraria.domain.entity.Socio;
import br.com.sigest.tesouraria.domain.entity.Transacao;
import br.com.sigest.tesouraria.domain.enums.Lancado;
import br.com.sigest.tesouraria.domain.enums.StatusCobranca;
import br.com.sigest.tesouraria.domain.enums.TipoRelacionamento;
import br.com.sigest.tesouraria.domain.enums.TipoTransacao;
import br.com.sigest.tesouraria.domain.repository.CobrancaRepository;
import br.com.sigest.tesouraria.domain.repository.FornecedorRepository;
import br.com.sigest.tesouraria.domain.repository.SocioRepository;
import br.com.sigest.tesouraria.domain.repository.TransacaoRepository;
import br.com.sigest.tesouraria.dto.FornecedorDto;
import br.com.sigest.tesouraria.dto.PagamentoRequestDto;
import br.com.sigest.tesouraria.dto.SocioDto;
import br.com.sigest.tesouraria.dto.TransacaoDto;
import br.com.sigest.tesouraria.dto.TransacaoProcessingResult;

@Service
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;
    private final CobrancaRepository cobrancaRepository;
    private final SocioRepository socioRepository;
    private final FornecedorRepository fornecedorRepository;
    private final FornecedorService fornecedorService;
    private final CobrancaService cobrancaService;

    public TransacaoService(TransacaoRepository transacaoRepository, CobrancaRepository cobrancaRepository,
            SocioRepository socioRepository, FornecedorRepository fornecedorRepository,
            FornecedorService fornecedorService, CobrancaService cobrancaService) {
        this.transacaoRepository = transacaoRepository;
        this.cobrancaRepository = cobrancaRepository;
        this.socioRepository = socioRepository;
        this.fornecedorRepository = fornecedorRepository;
        this.fornecedorService = fornecedorService;
        this.cobrancaService = cobrancaService;
    }

    public List<TransacaoDto> findFilteredTransactions(Integer month, Integer year) {
        List<Transacao> transactions;
        if (month != null && year != null) {
            LocalDate startDate = LocalDate.of(year, month, 1);
            LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
            transactions = transacaoRepository.findByDataBetweenOrderByDataDesc(startDate, endDate);
        } else {
            transactions = transacaoRepository.findAllByOrderByDataDesc();
        }
        return transactions.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public Map<Integer, List<Integer>> getAvailableMonthsAndYears() {
        List<Object[]> results = transacaoRepository.findDistinctYearsAndMonths();
        Map<Integer, List<Integer>> availableDates = new TreeMap<>();

        for (Object[] result : results) {
            Integer year = (Integer) result[0];
            Integer month = (Integer) result[1];
            availableDates.computeIfAbsent(year, k -> new ArrayList<>()).add(month);
        }
        return availableDates;
    }

    public TransacaoDto findTransactionById(Long id) {
        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada com o id: " + id));
        return convertToDto(transacao);
    }

    @Transactional
    public void quitarCobrancas(Long transacaoId, List<Long> cobrancaIds, Long contaFinanceiraId) {
        Transacao transacao = transacaoRepository.findById(transacaoId)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada com o id: " + transacaoId));

        Socio socio = null;

        for (Long cobrancaId : cobrancaIds) {
            Cobranca cobranca = cobrancaRepository.findById(cobrancaId)
                    .orElseThrow(() -> new RuntimeException("Cobrança não encontrada com o id: " + cobrancaId));

            PagamentoRequestDto pagamentoDto = new PagamentoRequestDto();
            pagamentoDto.setContaFinanceiraId(contaFinanceiraId);
            pagamentoDto.setDataPagamento(transacao.getData());
            pagamentoDto.setValor(cobranca.getValor());

            cobrancaService.registrarRecebimento(cobranca.getId(), pagamentoDto);

            if (socio == null && cobranca.getSocio() != null) {
                socio = cobranca.getSocio();
            }
        }

        transacao.setLancado(Lancado.LANCADO);
        if (socio != null) {
            transacao.setRelacionadoId(socio.getId());
            transacao.setTipoRelacionamento(TipoRelacionamento.SOCIO);
            transacao.setFornecedorOuSocio(socio.getNome());
            transacao.setDocumento(socio.getCpf());
        }
        transacaoRepository.save(transacao);
    }

    @Transactional
    public TransacaoProcessingResult processOfxFile(MultipartFile file) throws IOException {
        List<TransacaoDto> creditTransacoes = new ArrayList<>();
        List<TransacaoDto> debitTransacoes = new ArrayList<>();

        List<Socio> allSocios = socioRepository.findAll();
        List<Fornecedor> allFornecedores = fornecedorRepository.findAll();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            Transacao transacao = null;

            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("<STMTTRN>")) {
                    transacao = new Transacao();
                } else if (line.startsWith("<TRNTYPE>") && transacao != null) {
                    String tipo = line.replace("<TRNTYPE>", "").replace("</TRNTYPE>", "").trim();
                    transacao.setTipo("CREDIT".equalsIgnoreCase(tipo) ? TipoTransacao.CREDITO : TipoTransacao.DEBITO);
                } else if (line.startsWith("<DTPOSTED>") && transacao != null) {
                    String dataStr = line.replace("<DTPOSTED>", "").replace("</DTPOSTED>", "").trim();
                    if (dataStr.length() >= 8) {
                        try {
                            LocalDate data = LocalDate.parse(dataStr.substring(0, 8),
                                    DateTimeFormatter.ofPattern("yyyyMMdd"));
                            transacao.setData(data);
                        } catch (java.time.format.DateTimeParseException e) {
                            transacao = null;
                        }
                    } else {
                        transacao = null;
                    }
                } else if (line.startsWith("<TRNAMT>") && transacao != null) {
                    BigDecimal valor = new BigDecimal(line.replace("<TRNAMT>", "").replace("</TRNAMT>", "").trim());
                    transacao.setValor(valor);
                } else if (line.startsWith("<MEMO>") && transacao != null) {
                    String memo = line.replace("<MEMO>", "").replace("</MEMO>", "").trim();
                    String documento = extrairDocumento(memo);
                    transacao.setDocumento(documento);

                    String tempMemo = memo.replace(documento != null ? documento : "", "").trim();
                    int firstDash = tempMemo.indexOf(" - ");
                    int lastDash = tempMemo.lastIndexOf(" - ");
                    String descricao, fornecedorOuSocio;

                    if (firstDash != -1 && firstDash != lastDash) {
                        descricao = tempMemo.substring(0, firstDash).trim();
                        fornecedorOuSocio = tempMemo.substring(firstDash + 3, lastDash).trim();
                    } else if (firstDash != -1) {
                        descricao = tempMemo.substring(0, firstDash).trim();
                        fornecedorOuSocio = tempMemo.substring(firstDash + 3).trim();
                    } else {
                        descricao = tempMemo;
                        fornecedorOuSocio = null;
                    }
                    transacao.setDescricao(descricao);
                    transacao.setFornecedorOuSocio(fornecedorOuSocio);
                } else if (line.startsWith("</STMTTRN>") && transacao != null) {
                    // Verificar se a transação já existe no banco de dados
                    Transacao existingTransacao = transacaoRepository.findByDataAndTipoAndValorAndDescricaoAndDocumento(
                            transacao.getData(),
                            transacao.getTipo(),
                            transacao.getValor(),
                            transacao.getDescricao(),
                            transacao.getDocumento()).orElse(null);

                    if (existingTransacao == null) {
                        classifyAndSetRelacionamento(transacao, allSocios, allFornecedores);
                        transacao = transacaoRepository.save(transacao);

                        TransacaoDto processedDto = convertToDto(transacao);
                        if (processedDto.getTipo() == TipoTransacao.CREDITO) {
                            creditTransacoes.add(processedDto);
                        } else {
                            debitTransacoes.add(processedDto);
                        }
                    } else {
                        // Se a transação já existir, atualizar apenas o status da identificação
                        // Isso é útil para transações que foram processadas anteriormente sem a feature
                        // de identificação
                        if (existingTransacao.getStatusIdentificacao() == null) {
                            classifyAndSetRelacionamento(existingTransacao, allSocios, allFornecedores);
                            transacaoRepository.save(existingTransacao);
                        }
                    }
                    transacao = null;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar OFX: " + e.getMessage(), e);
        }

        return new TransacaoProcessingResult(creditTransacoes, debitTransacoes);
    }

    private void classifyAndSetRelacionamento(Transacao transacao, List<Socio> allSocios,
            List<Fornecedor> allFornecedores) {
        String normalizedName = normalizeString(transacao.getFornecedorOuSocio());
        String normalizedDoc = normalizeDocumento(transacao.getDocumento());

        if (transacao.getTipo() == TipoTransacao.CREDITO) {
            // Sanitizar CPF
            String cpfSanitizado = sanitizeCpf(transacao.getDocumento());

            // Primeiro, tentar encontrar sócio pelo CPF sanitizado
            Socio socioEncontrado = null;
            if (cpfSanitizado != null && !cpfSanitizado.isEmpty()) {
                socioEncontrado = findSocioByCpf(cpfSanitizado, allSocios);
            }

            // Se não encontrar pelo CPF, tentar encontrar por nome com 70% de similaridade
            if (socioEncontrado == null && normalizedName != null && !normalizedName.isEmpty()) {
                socioEncontrado = findSocioByNameSimilarity(normalizedName, allSocios, 0.7);
            }

            if (socioEncontrado != null) {
                // Associar sócio à transação
                transacao.setRelacionadoId(socioEncontrado.getId());
                transacao.setTipoRelacionamento(TipoRelacionamento.SOCIO);
                transacao.setFornecedorOuSocio(socioEncontrado.getNome());
                transacao.setDocumento(socioEncontrado.getCpf());
                transacao
                        .setStatusIdentificacao(br.com.sigest.tesouraria.domain.enums.StatusIdentificacao.IDENTIFICADO);

                // Se o sócio não tiver CPF e a transação tiver documento, atualizar o CPF do
                // sócio
                if ((socioEncontrado.getCpf() == null || socioEncontrado.getCpf().isEmpty())
                        && cpfSanitizado != null && !cpfSanitizado.isEmpty()) {
                    socioEncontrado.setCpf(formatarCpf(cpfSanitizado));
                    socioRepository.save(socioEncontrado);
                }
            } else {
                // Se não encontrar o sócio, definir status como PENDENTE_REVISAO
                transacao.setStatusIdentificacao(
                        br.com.sigest.tesouraria.domain.enums.StatusIdentificacao.PENDENTE_REVISAO);
            }
        } else { // DEBITO
            // Primeiro, tentar encontrar fornecedor existente
            for (Fornecedor fornecedor : allFornecedores) {
                if ((normalizedDoc != null && normalizedDoc.equals(normalizeDocumento(fornecedor.getCnpj())))
                        || (normalizedName != null && normalizeString(fornecedor.getNome()).contains(normalizedName))) {
                    transacao.setRelacionadoId(fornecedor.getId());
                    transacao.setTipoRelacionamento(TipoRelacionamento.FORNECEDOR);
                    return;
                }
            }

            // Se não encontrar fornecedor, verificar se é um sócio
            for (Socio socio : allSocios) {
                if ((normalizedDoc != null && normalizedDoc.equals(normalizeDocumento(socio.getCpf())))
                        || (normalizedName != null && normalizeString(socio.getNome()).contains(normalizedName))) {
                    transacao.setRelacionadoId(socio.getId());
                    transacao.setTipoRelacionamento(TipoRelacionamento.SOCIO);
                    return;
                }
            }

            // Se não encontrar nenhum relacionamento, criar fornecedor automaticamente para
            // débitos
            if (transacao.getTipo() == TipoTransacao.DEBITO && transacao.getFornecedorOuSocio() != null) {
                // Verificar se já existe um fornecedor com o mesmo nome (evitar duplicidade)
                Optional<Fornecedor> fornecedorExistente = fornecedorRepository
                        .findByNome(transacao.getFornecedorOuSocio());
                if (fornecedorExistente.isPresent()) {
                    transacao.setRelacionadoId(fornecedorExistente.get().getId());
                    transacao.setTipoRelacionamento(TipoRelacionamento.FORNECEDOR);
                } else {
                    // Criar novo fornecedor automaticamente
                    FornecedorDto novoFornecedorDto = new FornecedorDto();
                    novoFornecedorDto.setNome(transacao.getFornecedorOuSocio());
                    novoFornecedorDto.setCnpj(gerarDocumentoParaFornecedor(normalizedDoc));
                    novoFornecedorDto.setEmail("");
                    novoFornecedorDto.setCelular("");
                    novoFornecedorDto.setTelefoneComercial("");
                    novoFornecedorDto.setAtivo(true);

                    Fornecedor novoFornecedor = fornecedorService.save(novoFornecedorDto);
                    transacao.setRelacionadoId(novoFornecedor.getId());
                    transacao.setTipoRelacionamento(TipoRelacionamento.FORNECEDOR);

                    // Atualizar a lista de fornecedores para futuras verificações
                    allFornecedores.add(novoFornecedor);
                }
            } else {
                transacao.setTipoRelacionamento(TipoRelacionamento.NAO_ENCONTRADO);
            }
        }
    }

    private String extrairDocumento(String memo) {
        Pattern cpfPattern = Pattern.compile("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}");
        Pattern cnpjPattern = Pattern.compile("\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}");
        Matcher cpfMatcher = cpfPattern.matcher(memo);
        if (cpfMatcher.find())
            return normalizeDocumento(cpfMatcher.group());
        Matcher cnpjMatcher = cnpjPattern.matcher(memo);
        if (cnpjMatcher.find())
            return normalizeDocumento(cnpjMatcher.group());
        return null;
    }

    /**
     * Sanitiza o CPF removendo caracteres não numéricos.
     *
     * @param cpf O CPF a ser sanitizado.
     * @return O CPF contendo apenas números, ou null se o input for null.
     */
    private String sanitizeCpf(String cpf) {
        if (cpf == null) {
            return null;
        }
        return cpf.replaceAll("[^0-9]", "");
    }

    private String normalizeDocumento(String documento) {
        return documento != null ? documento.replaceAll("[^0-9]", "") : null;
    }

    private String formatarCpf(String cpfNumeros) {
        if (cpfNumeros == null || cpfNumeros.length() != 11) {
            return cpfNumeros;
        }
        return cpfNumeros.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }

    private Socio findSocioByCpf(String cpfNumeros, List<Socio> allSocios) {
        // Primeiro tentar encontrar com o CPF exato (apenas números)
        for (Socio socio : allSocios) {
            if (socio.getCpf() != null && normalizeDocumento(socio.getCpf()).equals(cpfNumeros)) {
                return socio;
            }
        }
        return null;
    }

    /**
     * Encontra um sócio por similaridade de nome usando o algoritmo Jaro-Winkler.
     *
     * @param normalizedName O nome normalizado da transação.
     * @param allSocios      A lista de todos os sócios.
     * @param threshold      O limiar mínimo de similaridade (0.0 a 1.0).
     * @return O sócio encontrado, ou null se nenhum for encontrado com a
     *         similaridade mínima.
     */
    private Socio findSocioByNameSimilarity(String normalizedName, List<Socio> allSocios, double threshold) {
        Socio melhorCandidato = null;
        double melhorSimilaridade = 0.0;

        for (Socio socio : allSocios) {
            String normalizedSocioName = normalizeString(socio.getNome());
            double similaridade = calculateJaroWinklerSimilarity(normalizedName, normalizedSocioName);

            if (similaridade >= threshold && similaridade > melhorSimilaridade) {
                melhorSimilaridade = similaridade;
                melhorCandidato = socio;
            }
        }

        return melhorCandidato;
    }

    /**
     * Calcula a similaridade entre duas strings usando o algoritmo Jaro-Winkler.
     * Esta é uma implementação simplificada. Em produção, considere usar uma
     * biblioteca.
     *
     * @param s1 Primeira string.
     * @param s2 Segunda string.
     * @return A similaridade Jaro-Winkler (0.0 a 1.0).
     */
    private double calculateJaroWinklerSimilarity(String s1, String s2) {
        if (s1 == null || s2 == null) {
            return 0.0;
        }

        // Converter para minúsculas para comparação
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int[] mtp = matches(s1, s2);
        float m = mtp[0];
        if (m == 0) {
            return 0f;
        }
        float j = ((m / s1.length() + m / s2.length() + (m - mtp[1]) / m)) / 3;
        float jw = j < 0.7 ? j : j + Math.min(0.1f, 1f / mtp[3]) * mtp[2] * (1 - j);
        return jw;
    }

    /**
     * Calcula os matches para o algoritmo Jaro.
     */
    private int[] matches(String s1, String s2) {
        String max, min;
        if (s1.length() > s2.length()) {
            max = s1;
            min = s2;
        } else {
            max = s2;
            min = s1;
        }
        int range = Math.max(max.length() / 2 - 1, 0);
        int[] matchIndexes = new int[min.length()];
        Arrays.fill(matchIndexes, -1);
        boolean[] matchFlags = new boolean[max.length()];
        int matches = 0;
        for (int mi = 0; mi < min.length(); mi++) {
            char c1 = min.charAt(mi);
            for (int xi = Math.max(mi - range, 0), xn = Math.min(mi + range + 1, max.length()); xi < xn; xi++) {
                if (!matchFlags[xi] && c1 == max.charAt(xi)) {
                    matchIndexes[mi] = xi;
                    matchFlags[xi] = true;
                    matches++;
                    break;
                }
            }
        }
        char[] ms1 = new char[matches];
        char[] ms2 = new char[matches];
        for (int i = 0, si = 0; i < min.length(); i++) {
            if (matchIndexes[i] != -1) {
                ms1[si] = min.charAt(i);
                si++;
            }
        }
        for (int i = 0, si = 0; i < max.length(); i++) {
            if (matchFlags[i]) {
                ms2[si] = max.charAt(i);
                si++;
            }
        }
        int transpositions = 0;
        for (int mi = 0; mi < ms1.length; mi++) {
            if (ms1[mi] != ms2[mi]) {
                transpositions++;
            }
        }
        int prefix = 0;
        for (int mi = 0; mi < min.length(); mi++) {
            if (s1.charAt(mi) == s2.charAt(mi)) {
                prefix++;
            } else {
                break;
            }
        }
        return new int[] { matches, transpositions / 2, prefix, max.length() };
    }

    private String gerarDocumentoParaFornecedor(String documentoNormalizado) {
        if (documentoNormalizado != null && !documentoNormalizado.isEmpty()) {
            return documentoNormalizado;
        }
        // Gerar identificador curto quando não houver documento (máximo 20 caracteres)
        String uuidPart = UUID.randomUUID().toString().substring(0, 16);
        String documento = "SEM_" + uuidPart;
        // Garantir que não ultrapasse o limite de 20 caracteres
        if (documento.length() > 20) {
            documento = documento.substring(0, 20);
        }
        return documento;
    }

    private String normalizeString(String text) {
        return text != null ? text.replaceAll("[^a-zA-Z0-9]", "").toLowerCase() : null;
    }

    @Transactional
    public void updateTransacaoWithSelectedParty(Long transacaoId, String selectedParty) {
        Transacao transacao = transacaoRepository.findById(transacaoId)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada com o id: " + transacaoId));

        if (selectedParty.startsWith("socio-")) {
            Long socioId = Long.parseLong(selectedParty.substring("socio-".length()));
            Socio socio = socioRepository.findById(socioId)
                    .orElseThrow(() -> new RuntimeException("Sócio não encontrado com o id: " + socioId));
            transacao.setRelacionadoId(socioId);
            transacao.setTipoRelacionamento(TipoRelacionamento.SOCIO);
            transacao.setFornecedorOuSocio(socio.getNome());
            transacao.setDocumento(socio.getCpf());
        } else if (selectedParty.startsWith("fornecedor-")) {
            Long fornecedorId = Long.parseLong(selectedParty.substring("fornecedor-".length()));
            Fornecedor fornecedor = fornecedorRepository.findById(fornecedorId)
                    .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado com o id: " + fornecedorId));
            transacao.setRelacionadoId(fornecedorId);
            transacao.setTipoRelacionamento(TipoRelacionamento.FORNECEDOR);
            transacao.setFornecedorOuSocio(fornecedor.getNome());
            transacao.setDocumento(fornecedor.getCnpj());
        }

        // transacao.setLancado(Lancado.LANCADO);
        transacaoRepository.save(transacao);
    }

    private TransacaoDto convertToDto(Transacao transacao) {
        TransacaoDto dto = new TransacaoDto();
        dto.setId(transacao.getId());
        dto.setData(transacao.getData());
        dto.setTipo(transacao.getTipo());
        dto.setValor(transacao.getValor());
        dto.setFornecedorOuSocio(transacao.getFornecedorOuSocio());
        dto.setDocumento(transacao.getDocumento());
        dto.setDescricao(transacao.getDescricao());
        dto.setLancado(transacao.getLancado());
        dto.setRelacionadoId(transacao.getRelacionadoId());
        dto.setTipoRelacionamento(transacao.getTipoRelacionamento());

        // Definir fornecedorId quando a transação for do tipo DEBITO e relacionamento
        // for FORNECEDOR
        if (transacao.getTipo() == TipoTransacao.DEBITO &&
                transacao.getTipoRelacionamento() == TipoRelacionamento.FORNECEDOR) {
            dto.setFornecedorId(transacao.getRelacionadoId());
        }

        if (transacao.getTipoRelacionamento() == TipoRelacionamento.NAO_ENCONTRADO) {
            dto.setManualSelectionNeeded(true);
            if (transacao.getTipo() == TipoTransacao.DEBITO) {
                dto.setSociosSugeridos(
                        socioRepository.findAll().stream().map(this::convertToSocioDto).collect(Collectors.toList()));
                dto.setFornecedoresSugeridos(fornecedorRepository.findAll().stream().map(this::convertToFornecedorDto)
                        .collect(Collectors.toList()));
            } else {
                dto.setSociosSugeridos(
                        socioRepository.findAll().stream().map(this::convertToSocioDto).collect(Collectors.toList()));
            }
        } else if (transacao.getTipoRelacionamento() == TipoRelacionamento.SOCIO
                && transacao.getTipo() == TipoTransacao.CREDITO) {
            Socio socio = socioRepository.findById(transacao.getRelacionadoId()).orElse(null);
            if (socio != null) {
                List<Cobranca> cobrancasPendentes = new ArrayList<>(cobrancaRepository.findBySocioAndStatusIn(socio,
                        List.of(StatusCobranca.ABERTA, StatusCobranca.VENCIDA)));
                if (socio.getDependentes() != null) {
                    for (Socio dependente : socio.getDependentes()) {
                        cobrancasPendentes.addAll(cobrancaRepository.findBySocioAndStatusIn(dependente,
                                List.of(StatusCobranca.ABERTA, StatusCobranca.VENCIDA)));
                    }
                }
                dto.setCobrancasPendentes(cobrancasPendentes);
            }
        }
        return dto;
    }

    private SocioDto convertToSocioDto(Socio socio) {
        SocioDto dto = new SocioDto();
        dto.setId(socio.getId());
        dto.setNome(socio.getNome());
        dto.setCpf(socio.getCpf());
        return dto;
    }

    private FornecedorDto convertToFornecedorDto(Fornecedor fornecedor) {
        FornecedorDto dto = new FornecedorDto();
        dto.setId(fornecedor.getId());
        dto.setNome(fornecedor.getNome());
        dto.setCnpj(fornecedor.getCnpj());
        return dto;
    }
}
