package br.com.sigest.tesouraria.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
import br.com.sigest.tesouraria.domain.enums.TipoTransacao;
import br.com.sigest.tesouraria.dto.FornecedorDto;
import br.com.sigest.tesouraria.dto.PagamentoRequestDto;
import br.com.sigest.tesouraria.dto.SocioDto;
import br.com.sigest.tesouraria.dto.TransacaoDto;
import br.com.sigest.tesouraria.dto.TransacaoProcessingResult; // Import the new DTO
import br.com.sigest.tesouraria.repository.CobrancaRepository;
import br.com.sigest.tesouraria.repository.ContaFinanceiraRepository;
import br.com.sigest.tesouraria.repository.FornecedorRepository;
import br.com.sigest.tesouraria.repository.SocioRepository;
import br.com.sigest.tesouraria.repository.TransacaoRepository;

@Service
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;
    private final CobrancaRepository cobrancaRepository;
    private final SocioRepository socioRepository;
    private final FornecedorRepository fornecedorRepository;
    private final ContaFinanceiraRepository contaFinanceiraRepository;
    private final CobrancaService cobrancaService;

    public TransacaoService(TransacaoRepository transacaoRepository, CobrancaRepository cobrancaRepository,
            SocioRepository socioRepository, FornecedorRepository fornecedorRepository,
            ContaFinanceiraRepository contaFinanceiraRepository, CobrancaService cobrancaService) {
        this.transacaoRepository = transacaoRepository;
        this.cobrancaRepository = cobrancaRepository;
        this.socioRepository = socioRepository;
        this.fornecedorRepository = fornecedorRepository;
        this.contaFinanceiraRepository = contaFinanceiraRepository;
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

        Socio socio = null; // To capture the socio from the first processed cobranca

        for (Long cobrancaId : cobrancaIds) {
            Cobranca cobranca = cobrancaRepository.findById(cobrancaId)
                    .orElseThrow(() -> new RuntimeException("Cobrança não encontrada com o id: " + cobrancaId));

            // Create PagamentoRequestDto for each cobranca
            PagamentoRequestDto pagamentoDto = new PagamentoRequestDto();
            pagamentoDto.setContaFinanceiraId(contaFinanceiraId);
            pagamentoDto.setDataPagamento(transacao.getData());
            pagamentoDto.setValor(cobranca.getValor()); // Use the cobranca's value

            // Call the existing business logic to register payment for each cobranca
            cobrancaService.registrarRecebimento(cobranca.getId(), pagamentoDto);

            // Capture socio from the first cobranca for transacao update
            if (socio == null && cobranca.getSocio() != null) {
                socio = cobranca.getSocio();
            }
        }

        // Update the Transacao status and associated socio/fornecedor
        transacao.setLancado(Lancado.LANCADO);
        if (socio != null) {
            transacao.setSocio(socio);
            transacao.setFornecedorOuSocio(socio.getNome());
            transacao.setDocumento(socio.getCpf());
        } else if (!cobrancaIds.isEmpty()) {
            // If no socio found but cobrancas were processed, try to get pagador from one
            // of them
            // This might be less accurate if cobrancas have different pagadores, but
            // handles the case
            // where transacao might not have a socio initially.
            // For this specific use case (detalhes-creditos), it's likely all related to
            // one socio.
            Cobranca firstCobranca = cobrancaRepository.findById(cobrancaIds.get(0)).orElse(null);
            if (firstCobranca != null) {
                transacao.setFornecedorOuSocio(firstCobranca.getPagador());
            }
        }

        transacaoRepository.save(transacao);
    }

    @Transactional
    public TransacaoProcessingResult processOfxFile(MultipartFile file) throws IOException {
        List<Transacao> newTransacoes = new ArrayList<>();
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
                    LocalDate data = LocalDate.parse(dataStr.substring(0, 8), DateTimeFormatter.ofPattern("yyyyMMdd"));
                    transacao.setData(data);
                } else if (line.startsWith("<TRNAMT>") && transacao != null) {
                    BigDecimal valor = new BigDecimal(line.replace("<TRNAMT>", "").replace("</TRNAMT>", "").trim());
                    transacao.setValor(valor);
                } else if (line.startsWith("<MEMO>") && transacao != null) {
                    String memo = line.replace("<MEMO>", "").replace("</MEMO>", "").trim();

                    // Extrair CPF ou CNPJ
                    String documento = extrairDocumento(memo);
                    transacao.setDocumento(documento);

                    // Extrair fornecedor/sócio e descrição
                    String tempMemo = memo;
                    String fornecedorOuSocio = null;
                    String descricao = memo;

                    // Remove documento from memo for easier parsing of fornecedorOuSocio and
                    // descricao
                    if (documento != null) {
                        tempMemo = tempMemo.replace(documento, "").trim();
                    }

                    int firstDash = tempMemo.indexOf(" - ");
                    int lastDash = tempMemo.lastIndexOf(" - ");

                    if (firstDash != -1 && lastDash != -1 && firstDash != lastDash) {
                        // Case: "Desc - Fornecedor - Documento"
                        descricao = tempMemo.substring(0, firstDash).trim();
                        fornecedorOuSocio = tempMemo.substring(firstDash + 3, lastDash).trim();
                    } else if (firstDash != -1 && lastDash != -1 && firstDash == lastDash) {
                        // Case: "Desc - Fornecedor" (no document or document already removed)
                        descricao = tempMemo.substring(0, firstDash).trim();
                        fornecedorOuSocio = tempMemo.substring(firstDash + 3).trim();
                    } else {
                        // Case: "Desc" or "Fornecedor" (no dashes or only one part)
                        descricao = tempMemo.trim();
                        fornecedorOuSocio = null; // Or set to memo if no clear distinction
                    }

                    transacao.setFornecedorOuSocio(fornecedorOuSocio);
                    transacao.setDescricao(descricao);
                } else if (line.startsWith("</STMTTRN>") && transacao != null) {
                    // Check for duplicates before adding to the list for saving
                    Transacao existingTransacao = transacaoRepository
                            .findByDataAndTipoAndValorAndFornecedorOuSocioAndDocumentoAndDescricao(
                                    transacao.getData(), transacao.getTipo(), transacao.getValor(),
                                    transacao.getFornecedorOuSocio(), transacao.getDocumento(),
                                    transacao.getDescricao());

                    if (existingTransacao == null) {
                        // Save the transaction immediately to get the generated ID
                        transacao = transacaoRepository.save(transacao);
                    } else {
                        // If it's a duplicate, use the existing one for classification
                        transacao = existingTransacao;
                    }
                    // Classify and match the transaction using the persisted transacao
                    TransacaoDto processedDto = classifyAndMatchTransaction(transacao, allSocios, allFornecedores);
                    if (processedDto.getTipo() == TipoTransacao.CREDITO) {
                        creditTransacoes.add(processedDto);
                    } else {
                        debitTransacoes.add(processedDto);
                    }
                    transacao = null;
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar OFX: " + e.getMessage(), e);
        }

        return new TransacaoProcessingResult(creditTransacoes, debitTransacoes);
    }

    private String extrairDocumento(String memo) {
        Pattern cpfPattern = Pattern.compile("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}");
        Pattern cnpjPattern = Pattern.compile("\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}");

        Matcher cpfMatcher = cpfPattern.matcher(memo);
        if (cpfMatcher.find()) {
            return normalizeDocumento(cpfMatcher.group());
        }

        Matcher cnpjMatcher = cnpjPattern.matcher(memo);
        if (cnpjMatcher.find()) {
            return normalizeDocumento(cnpjMatcher.group());
        }

        return null;
    }

    private String normalizeDocumento(String documento) {
        return documento != null ? documento.replaceAll("[^0-9]", "") : null;
    }

    // New method to normalize strings for comparison (e.g., names, descriptions)
    private String normalizeString(String text) {
        if (text == null) {
            return null;
        }
        return text.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
    }

    public List<Socio> findAllSocios() {
        return socioRepository.findAll();
    }

    public List<Fornecedor> findAllFornecedores() {
        return fornecedorRepository.findAll();
    }

    @Transactional
    public void updateTransacaoWithSelectedParty(Long transacaoId, String selectedParty) {
        Transacao transacao = transacaoRepository.findById(transacaoId)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada com o id: " + transacaoId));

        if (selectedParty.startsWith("socio-")) {
            Long socioId = Long.parseLong(selectedParty.substring("socio-".length()));
            Socio socio = socioRepository.findById(socioId)
                    .orElseThrow(() -> new RuntimeException("Sócio não encontrado com o id: " + socioId));
            transacao.setSocio(socio);
            transacao.setFornecedorOuSocio(socio.getNome());
            transacao.setDocumento(socio.getCpf());
        } else if (selectedParty.startsWith("fornecedor-")) {
            Long fornecedorId = Long.parseLong(selectedParty.substring("fornecedor-".length()));
            Fornecedor fornecedor = fornecedorRepository.findById(fornecedorId)
                    .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado com o id: " + fornecedorId));
            transacao.setFornecedor(fornecedor);
            transacao.setFornecedorOuSocio(fornecedor.getNome());
            transacao.setDocumento(fornecedor.getCnpj());
        }

        transacao.setLancado(Lancado.LANCADO);
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
        if (transacao.getSocio() != null) {
            dto.setSocio(convertToSocioDto(transacao.getSocio()));
        }
        if (transacao.getFornecedor() != null) {
            dto.setFornecedor(convertToFornecedorDto(transacao.getFornecedor()));
        }
        return dto;
    }

    private TransacaoDto classifyAndMatchTransaction(Transacao transacao, List<Socio> allSocios,
            List<Fornecedor> allFornecedores) {
        TransacaoDto dto = convertToDto(transacao); // Start with basic conversion

        String normalizedDocumento = normalizeDocumento(transacao.getDocumento());
        String normalizedDescricao = normalizeString(transacao.getDescricao());
        boolean matched = false;

        // 1. Try to match by Documento (CPF/CNPJ)
        if (normalizedDocumento != null) {
            // Match with Socio CPF
            for (Socio socio : allSocios) {
                if (normalizedDocumento.equals(normalizeDocumento(socio.getCpf()))) {
                    transacao.setSocio(socio);
                    transacao.setFornecedorOuSocio(socio.getNome());
                    matched = true;
                    break;
                }
            }

            // If not matched with Socio, try to match with Fornecedor CNPJ
            if (!matched) {
                for (Fornecedor fornecedor : allFornecedores) {
                    if (normalizedDocumento.equals(normalizeDocumento(fornecedor.getCnpj()))) {
                        transacao.setFornecedor(fornecedor);
                        transacao.setFornecedorOuSocio(fornecedor.getNome());
                        matched = true;
                        break;
                    }
                }
            }
        }

        // 2. If not matched by Documento, try to match by Description (Socio name for
        // CREDIT)
        if (!matched && transacao.getTipo() == TipoTransacao.CREDITO && normalizedDescricao != null) {
            for (Socio socio : allSocios) {
                String normalizedSocioName = normalizeString(socio.getNome());
                // Check for partial similarity
                if (normalizedDescricao.contains(normalizedSocioName)
                        || normalizedSocioName.contains(normalizedDescricao)) {
                    transacao.setSocio(socio);
                    transacao.setFornecedorOuSocio(socio.getNome());
                    matched = true;
                    break;
                }
            }
        }

        // 3. If not matched by Documento, try to match by Description (Fornecedor name
        // for DEBIT)
        if (!matched && transacao.getTipo() == TipoTransacao.DEBITO && normalizedDescricao != null) {
            for (Fornecedor fornecedor : allFornecedores) {
                String normalizedFornecedorName = normalizeString(fornecedor.getNome());
                // Check for partial similarity
                if (normalizedDescricao.contains(normalizedFornecedorName)
                        || normalizedFornecedorName.contains(normalizedDescricao)) {
                    transacao.setFornecedor(fornecedor);
                    transacao.setFornecedorOuSocio(fornecedor.getNome());
                    matched = true;
                    break;
                }
            }
        }

        dto.setManualSelectionNeeded(!matched);
        if (!matched) {
            dto.setSocios(allSocios.stream().map(this::convertToSocioDto).collect(Collectors.toList()));
            if (transacao.getTipo() == TipoTransacao.DEBITO) {
                dto.setFornecedores(
                        allFornecedores.stream().map(this::convertToFornecedorDto).collect(Collectors.toList()));
            }
        } else {
            // If matched, update the DTO with the matched socio/fornecedor
            if (transacao.getSocio() != null) {
                dto.setSocio(convertToSocioDto(transacao.getSocio()));
                // For credit transactions, retrieve open cobrancas for the matched socio and
                // its dependents
                if (transacao.getTipo() == TipoTransacao.CREDITO) {
                    List<Cobranca> cobrancasPendentes = new ArrayList<>();
                    cobrancasPendentes.addAll(cobrancaRepository.findBySocioAndStatusIn(transacao.getSocio(),
                            List.of(StatusCobranca.ABERTA, StatusCobranca.VENCIDA)));

                    // Add cobrancas for dependents
                    if (transacao.getSocio().getDependentes() != null) {
                        for (Socio dependente : transacao.getSocio().getDependentes()) {
                            cobrancasPendentes.addAll(cobrancaRepository.findBySocioAndStatusIn(dependente,
                                    List.of(StatusCobranca.ABERTA, StatusCobranca.VENCIDA)));
                        }
                    }
                    dto.setCobrancasPendentes(cobrancasPendentes); // Assuming TransacaoDto has a field for this
                }
            } else if (transacao.getFornecedor() != null) {
                dto.setFornecedor(convertToFornecedorDto(transacao.getFornecedor()));
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