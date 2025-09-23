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
import br.com.sigest.tesouraria.domain.entity.ContaFinanceira;
import br.com.sigest.tesouraria.domain.entity.Fornecedor;
import br.com.sigest.tesouraria.domain.entity.Socio;
import br.com.sigest.tesouraria.domain.entity.Transacao;
import br.com.sigest.tesouraria.domain.entity.TransacaoPendente;
import br.com.sigest.tesouraria.domain.enums.Lancado;
import br.com.sigest.tesouraria.domain.enums.StatusCobranca;
import br.com.sigest.tesouraria.domain.enums.TipoRelacionamento;
import br.com.sigest.tesouraria.domain.enums.TipoTransacao;
import br.com.sigest.tesouraria.domain.repository.CobrancaRepository;
import br.com.sigest.tesouraria.domain.repository.ContaFinanceiraRepository;
import br.com.sigest.tesouraria.domain.repository.FornecedorRepository;
import br.com.sigest.tesouraria.domain.repository.ReconciliacaoMensalRepository;
import br.com.sigest.tesouraria.domain.repository.SocioRepository;
import br.com.sigest.tesouraria.domain.repository.TransacaoPendenteRepository;
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
    private final ContaFinanceiraRepository contaFinanceiraRepository;
    private final ReconciliacaoMensalRepository reconciliacaoMensalRepository;
    private final TransacaoPendenteRepository transacaoPendenteRepository;
    private final FornecedorService fornecedorService;
    private final CobrancaService cobrancaService;
    private final ReconciliacaoService reconciliacaoService;

    /**
     * Construtor da classe TransacaoService.
     *
     * @param transacaoRepository           o repositório de transações
     * @param cobrancaRepository            o repositório de cobranças
     * @param socioRepository               o repositório de sócios
     * @param fornecedorRepository          o repositório de fornecedores
     * @param contaFinanceiraRepository     o repositório de contas financeiras
     * @param reconciliacaoMensalRepository o repositório de reconciliações mensais
     * @param fornecedorService             o serviço de fornecedores
     * @param cobrancaService               o serviço de cobranças
     */
    public TransacaoService(TransacaoRepository transacaoRepository, CobrancaRepository cobrancaRepository,
            SocioRepository socioRepository, FornecedorRepository fornecedorRepository,
            ContaFinanceiraRepository contaFinanceiraRepository,
            ReconciliacaoMensalRepository reconciliacaoMensalRepository,
            TransacaoPendenteRepository transacaoPendenteRepository,
            FornecedorService fornecedorService,
            CobrancaService cobrancaService,
            ReconciliacaoService reconciliacaoService) {
        this.transacaoRepository = transacaoRepository;
        this.cobrancaRepository = cobrancaRepository;
        this.socioRepository = socioRepository;
        this.fornecedorRepository = fornecedorRepository;
        this.contaFinanceiraRepository = contaFinanceiraRepository;
        this.reconciliacaoMensalRepository = reconciliacaoMensalRepository;
        this.transacaoPendenteRepository = transacaoPendenteRepository;
        this.fornecedorService = fornecedorService;
        this.cobrancaService = cobrancaService;
        this.reconciliacaoService = reconciliacaoService;
    }

    /**
     * Busca transações filtradas por mês e ano.
     *
     * @param month o mês para filtrar
     * @param year  o ano para filtrar
     * @return uma lista de TransacaoDto
     */
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

    /**
     * Retorna um mapa com os meses e anos disponíveis para filtro.
     *
     * @return um mapa com os meses e anos disponíveis
     */
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

    /**
     * Busca uma transação pelo seu ID.
     *
     * @param id o ID da transação
     * @return o TransacaoDto correspondente
     */
    public TransacaoDto findTransactionById(Long id) {
        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada com o id: " + id));
        return convertToDto(transacao);
    }

    /**
     * Quita cobranças associadas a uma transação.
     *
     * @param transacaoId       o ID da transação
     * @param cobrancaIds       a lista de IDs das cobranças a serem quitadas
     * @param contaFinanceiraId o ID da conta financeira
     */
    @Transactional
    public void quitarCobrancas(Long transacaoId, List<Long> cobrancaIds, Long contaFinanceiraId) {
        Transacao transacao = transacaoRepository.findById(transacaoId)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada com o id: " + transacaoId));

        // Log temporário para verificar quais IDs estão sendo recebidos
        System.out.println("=== QUITAR COBRANCAS ===");
        System.out.println("Transacao ID: " + transacaoId);
        System.out.println("Conta Financeira ID: " + contaFinanceiraId);
        System.out.println("Cobranca IDs recebidos: " + cobrancaIds);

        // Verificar os detalhes de cada cobrança recebida
        for (Long id : cobrancaIds) {
            Cobranca c = cobrancaRepository.findById(id).orElse(null);
            if (c != null) {
                System.out.println(
                        "  Cobranca ID: " + id + ", Tipo: " + c.getTipoCobranca() + ", Descricao: " + c.getDescricao());
            } else {
                System.out.println("  Cobranca ID: " + id + " (NÃO ENCONTRADA)");
            }
        }

        Socio socio = null;

        // Verificar se a conta financeira existe antes de prosseguir
        ContaFinanceira contaFinanceira = contaFinanceiraRepository.findById(contaFinanceiraId)
                .orElseThrow(
                        () -> new RuntimeException("Conta financeira não encontrada com o id: " + contaFinanceiraId));

        // Se a transação já estiver associada a um sócio, usar esse sócio
        if (transacao.getTipoRelacionamento() == TipoRelacionamento.SOCIO && transacao.getRelacionadoId() != null) {
            socio = socioRepository.findById(transacao.getRelacionadoId()).orElse(null);
        }

        for (Long cobrancaId : cobrancaIds) {
            Cobranca cobranca = cobrancaRepository.findById(cobrancaId)
                    .orElseThrow(() -> new RuntimeException("Cobrança não encontrada com o id: " + cobrancaId));

            // Se a cobrança ainda não tiver um sócio associado e a transação estiver
            // associada a um sócio,
            // associar o sócio à cobrança
            if (cobranca.getSocio() == null && socio != null) {
                cobranca.setSocio(socio);
                cobrancaRepository.save(cobranca);
            }

            PagamentoRequestDto pagamentoDto = new PagamentoRequestDto();
            pagamentoDto.setContaFinanceiraId(contaFinanceiraId);
            pagamentoDto.setDataPagamento(transacao.getData());
            pagamentoDto.setValor(cobranca.getValor() != null ? cobranca.getValor().floatValue() : 0.0F);

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

    /**
     * Associa um sócio a uma transação.
     *
     * @param transacaoId o ID da transação
     * @param socioId     o ID do sócio
     */
    @Transactional
    public void associarSocio(Long transacaoId, Long socioId) {
        Transacao transacao = transacaoRepository.findById(transacaoId)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada com o id: " + transacaoId));

        Socio socio = socioRepository.findById(socioId)
                .orElseThrow(() -> new RuntimeException("Sócio não encontrado com o id: " + socioId));

        // Associar o sócio à transação
        transacao.setRelacionadoId(socioId);
        transacao.setTipoRelacionamento(TipoRelacionamento.SOCIO);
        transacao.setFornecedorOuSocio(socio.getNome());
        transacao.setDocumento(socio.getCpf());

        transacaoRepository.save(transacao);

        // Atualizar todas as cobranças relacionadas a esta transação com o socio_id
        List<Cobranca> cobrancas = cobrancaRepository.findByTransacaoIdWithSocio(transacaoId);
        for (Cobranca cobranca : cobrancas) {
            if (cobranca.getSocio() == null) {
                cobranca.setSocio(socio);
                cobrancaRepository.save(cobranca);
            }
        }
    }

    /**
     * Processa um arquivo OFX e importa as transações.
     *
     * @param file o arquivo OFX
     * @return um objeto TransacaoProcessingResult com o resultado do processamento
     * @throws IOException se ocorrer um erro ao ler o arquivo
     */
    @Transactional
    public TransacaoProcessingResult processOfxFile(MultipartFile file) throws IOException {
        List<TransacaoDto> creditTransacoes = new ArrayList<>();
        List<TransacaoDto> debitTransacoes = new ArrayList<>();
        List<TransacaoPendente> transacoesPendentes = new ArrayList<>();

        List<Socio> allSocios = socioRepository.findAll();
        List<Fornecedor> allFornecedores = fornecedorRepository.findAll();
        List<ContaFinanceira> allContas = contaFinanceiraRepository.findAll();

        String nomeArquivo = file.getOriginalFilename();

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
                        boolean classificada = classifyAndSetRelacionamento(transacao, allSocios, allFornecedores);

                        if (classificada) {
                            // Transação foi classificada com sucesso, salvar normalmente
                            transacao = transacaoRepository.save(transacao);

                            TransacaoDto processedDto = convertToDto(transacao);
                            if (processedDto.getTipo() == TipoTransacao.CREDITO) {
                                creditTransacoes.add(processedDto);
                            } else {
                                debitTransacoes.add(processedDto);
                            }
                        } else {
                            // Transação não foi classificada, salvar como pendente
                            TransacaoPendente transacaoPendente = new TransacaoPendente(
                                    transacao.getData(),
                                    transacao.getTipo(),
                                    transacao.getValor(),
                                    transacao.getDescricao(),
                                    transacao.getDocumento(),
                                    transacao.getFornecedorOuSocio(),
                                    nomeArquivo);
                            transacaoPendente = transacaoPendenteRepository.save(transacaoPendente);
                            transacoesPendentes.add(transacaoPendente);
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

        // Update reconciliation items for all accounts and months in the processed
        // transactions
        // Group transactions by month and year
        Map<String, List<Transacao>> transactionsByMonthYear = new java.util.HashMap<>();

        // Combine both lists of transactions
        List<TransacaoDto> allProcessedTransactions = new ArrayList<>();
        allProcessedTransactions.addAll(creditTransacoes);
        allProcessedTransactions.addAll(debitTransacoes);

        // Convert DTOs back to entities for processing
        for (TransacaoDto dto : allProcessedTransactions) {
            Transacao transacao = transacaoRepository.findById(dto.getId()).orElse(null);
            if (transacao != null) {
                String monthYearKey = transacao.getData().getMonthValue() + "-" + transacao.getData().getYear();
                transactionsByMonthYear.computeIfAbsent(monthYearKey, k -> new ArrayList<>()).add(transacao);
            }
        }

        // Update reconciliation for each month/year and account
        for (Map.Entry<String, List<Transacao>> entry : transactionsByMonthYear.entrySet()) {
            String[] parts = entry.getKey().split("-");
            int month = Integer.parseInt(parts[0]);
            int year = Integer.parseInt(parts[1]);

            // For each account, update reconciliation
            for (ContaFinanceira conta : allContas) {
                reconciliacaoService.updateOrCreateReconciliationItem(conta, month, year);
            }
        }

        return new TransacaoProcessingResult(creditTransacoes, debitTransacoes, transacoesPendentes);
    }

    /**
     * Classifica e define o relacionamento da transação com sócio ou fornecedor.
     * Retorna true se a transação foi classificada com sucesso, false se deve ser
     * marcada como pendente.
     */
    private boolean classifyAndSetRelacionamento(Transacao transacao, List<Socio> allSocios,
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

            // Se não encontrar pelo CPF, tentar encontrar por nome (primeiras duas
            // palavras)
            if (socioEncontrado == null && normalizedName != null && !normalizedName.isEmpty()) {
                socioEncontrado = findSocioByName(normalizedName, allSocios);
            }

            // Se não encontrar nenhum sócio, não criar automaticamente
            // A transação será marcada como pendente de revisão
            // Se encontrar pelo nome mas não tiver CPF, atualizar o CPF do sócio
            else if (socioEncontrado != null && cpfSanitizado != null && !cpfSanitizado.isEmpty()
                    && (socioEncontrado.getCpf() == null || socioEncontrado.getCpf().isEmpty())) {
                socioEncontrado.setCpf(formatarCpf(cpfSanitizado));
                socioRepository.save(socioEncontrado);
            }

            if (socioEncontrado != null) {
                // Associar sócio à transação
                transacao.setRelacionadoId(socioEncontrado.getId());
                transacao.setTipoRelacionamento(TipoRelacionamento.SOCIO);
                transacao.setFornecedorOuSocio(socioEncontrado.getNome());
                transacao.setDocumento(socioEncontrado.getCpf());
                transacao
                        .setStatusIdentificacao(br.com.sigest.tesouraria.domain.enums.StatusIdentificacao.IDENTIFICADO);
                return true;
            } else {
                // Se não encontrar o sócio, a transação será salva como pendente
                return false;
            }
        } else { // DEBITO
            // Primeiro, tentar encontrar fornecedor existente
            for (Fornecedor fornecedor : allFornecedores) {
                if ((normalizedDoc != null && normalizedDoc.equals(normalizeDocumento(fornecedor.getCnpj())))
                        || (normalizedName != null
                                && normalizeString(fornecedor.getNome()).contains(normalizedName))) {
                    transacao.setRelacionadoId(fornecedor.getId());
                    transacao.setTipoRelacionamento(TipoRelacionamento.FORNECEDOR);
                    return true;
                }
            }

            // Se não encontrar fornecedor, verificar se é um sócio
            for (Socio socio : allSocios) {
                if ((normalizedDoc != null && normalizedDoc.equals(normalizeDocumento(socio.getCpf())))
                        || (normalizedName != null && normalizeString(socio.getNome()).contains(normalizedName))) {
                    transacao.setRelacionadoId(socio.getId());
                    transacao.setTipoRelacionamento(TipoRelacionamento.SOCIO);
                    return true;
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
                    return true;
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
                    return true;
                }
            } else {
                transacao.setTipoRelacionamento(TipoRelacionamento.NAO_ENCONTRADO);
                return false;
            }
        }
    }

    /**
     * Cria um novo sócio com base nas informações da transação.
     * 
     * @param nomeSocio O nome do sócio
     * @param cpf       O CPF do sócio (pode ser null)
     * @return O sócio criado
     */
    private Socio criarNovoSocio(String nomeSocio, String cpf) {
        Socio novoSocio = new Socio();
        novoSocio.setNome(nomeSocio);
        novoSocio.setCpf(cpf != null ? formatarCpf(cpf) : "");
        novoSocio.setGrau("Quadro de Sócio"); // Valor padrão
        novoSocio.setDataCadastro(LocalDate.now());
        novoSocio.setStatus(br.com.sigest.tesouraria.domain.enums.StatusSocio.FREQUENTE);
        return socioRepository.save(novoSocio);
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
     * Encontra um sócio pelas duas primeiras palavras do nome.
     *
     * @param normalizedName O nome normalizado da transação.
     * @param allSocios      A lista de todos os sócios.
     * @return O sócio encontrado, ou null se nenhum for encontrado.
     */
    private Socio findSocioByName(String normalizedName, List<Socio> allSocios) {
        // Extrair as duas primeiras palavras do nome normalizado
        String[] nameParts = normalizedName.split("[^a-zA-Z0-9]+");
        if (nameParts.length < 2) {
            return null;
        }

        String firstName = nameParts[0].toLowerCase();
        String secondName = nameParts[1].toLowerCase();
        String searchPattern = firstName + " " + secondName;

        // Procurar sócio cujo nome contenha as duas primeiras palavras
        for (Socio socio : allSocios) {
            String normalizedSocioName = normalizeString(socio.getNome());
            if (normalizedSocioName.contains(firstName) && normalizedSocioName.contains(secondName)) {
                return socio;
            }
        }

        return null;
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

    /**
     * Atualiza uma transação com a parte selecionada (sócio ou fornecedor).
     *
     * @param transacaoId   o ID da transação
     * @param selectedParty a parte selecionada
     */
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
                dto.setFornecedoresSugeridos(fornecedorRepository.findAll().stream()
                        .map(this::convertToFornecedorDto)
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

    @Transactional
    public String salvarComprovante(MultipartFile comprovante) throws IOException {
        // Obter o diretório de upload do arquivo de propriedades
        String uploadDir = System.getProperty("file.upload.directory");
        if (uploadDir == null) {
            uploadDir = "e:/uploads/transacoes/debitos"; // Valor padrão
        }

        // Criar o diretório se não existir
        java.nio.file.Path uploadPath = java.nio.file.Paths.get(uploadDir);
        if (!java.nio.file.Files.exists(uploadPath)) {
            java.nio.file.Files.createDirectories(uploadPath);
        }

        // Gerar um nome de arquivo único usando UUID
        String originalFilename = comprovante.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

        // Salvar o arquivo
        java.nio.file.Path filePath = uploadPath.resolve(uniqueFilename);
        java.nio.file.Files.copy(comprovante.getInputStream(), filePath,
                java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        // Retornar o caminho relativo do arquivo
        return uploadDir + "/" + uniqueFilename;
    }
}
