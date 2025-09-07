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
import br.com.sigest.tesouraria.domain.enums.TipoRelacionamento;
import br.com.sigest.tesouraria.domain.enums.TipoTransacao;
import br.com.sigest.tesouraria.dto.FornecedorDto;
import br.com.sigest.tesouraria.dto.PagamentoRequestDto;
import br.com.sigest.tesouraria.dto.SocioDto;
import br.com.sigest.tesouraria.dto.TransacaoDto;
import br.com.sigest.tesouraria.dto.TransacaoProcessingResult;
import br.com.sigest.tesouraria.repository.CobrancaRepository;
import br.com.sigest.tesouraria.repository.FornecedorRepository;
import br.com.sigest.tesouraria.repository.SocioRepository;
import br.com.sigest.tesouraria.repository.TransacaoRepository;

@Service
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;
    private final CobrancaRepository cobrancaRepository;
    private final SocioRepository socioRepository;
    private final FornecedorRepository fornecedorRepository;
    private final CobrancaService cobrancaService;

    public TransacaoService(TransacaoRepository transacaoRepository, CobrancaRepository cobrancaRepository,
            SocioRepository socioRepository, FornecedorRepository fornecedorRepository,
            CobrancaService cobrancaService) {
        this.transacaoRepository = transacaoRepository;
        this.cobrancaRepository = cobrancaRepository;
        this.socioRepository = socioRepository;
        this.fornecedorRepository = fornecedorRepository;
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
                    // Transacao existingTransacao =
                    // transacaoRepository.findByDataAndTipoAndValorAndDescricaoAndDocumento(transacao.getData(),
                    // transacao.getTipo(), transacao.getValor(), transacao.getDescricao(),
                    // transacao.getDocumento());

                    // if (existingTransacao == null) {
                    classifyAndSetRelacionamento(transacao, allSocios, allFornecedores);
                    transacao = transacaoRepository.save(transacao);
                    // } else {
                    // transacao = existingTransacao;
                    // }

                    TransacaoDto processedDto = convertToDto(transacao);
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

    private void classifyAndSetRelacionamento(Transacao transacao, List<Socio> allSocios,
            List<Fornecedor> allFornecedores) {
        String normalizedName = normalizeString(transacao.getFornecedorOuSocio());
        String normalizedDoc = normalizeDocumento(transacao.getDocumento());

        if (transacao.getTipo() == TipoTransacao.CREDITO) {
            for (Socio socio : allSocios) {
                if ((normalizedDoc != null && normalizedDoc.equals(normalizeDocumento(socio.getCpf())))
                        || (normalizedName != null && normalizeString(socio.getNome()).contains(normalizedName))) {
                    transacao.setRelacionadoId(socio.getId());
                    transacao.setTipoRelacionamento(TipoRelacionamento.SOCIO);
                    return;
                }
            }
        } else { // DEBITO
            for (Fornecedor fornecedor : allFornecedores) {
                if ((normalizedDoc != null && normalizedDoc.equals(normalizeDocumento(fornecedor.getCnpj())))
                        || (normalizedName != null && normalizeString(fornecedor.getNome()).contains(normalizedName))) {
                    transacao.setRelacionadoId(fornecedor.getId());
                    transacao.setTipoRelacionamento(TipoRelacionamento.FORNECEDOR);
                    return;
                }
            }
            for (Socio socio : allSocios) {
                if ((normalizedDoc != null && normalizedDoc.equals(normalizeDocumento(socio.getCpf())))
                        || (normalizedName != null && normalizeString(socio.getNome()).contains(normalizedName))) {
                    transacao.setRelacionadoId(socio.getId());
                    transacao.setTipoRelacionamento(TipoRelacionamento.SOCIO);
                    return;
                }
            }
        }
        transacao.setTipoRelacionamento(TipoRelacionamento.NAO_ENCONTRADO);
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

    private String normalizeDocumento(String documento) {
        return documento != null ? documento.replaceAll("[^0-9]", "") : null;
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
