package br.com.sigest.tesouraria.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.sigest.tesouraria.domain.entity.Cobranca;
import br.com.sigest.tesouraria.domain.entity.ContaFinanceira;
import br.com.sigest.tesouraria.domain.entity.GrupoMensalidade;
import br.com.sigest.tesouraria.domain.entity.GrupoMensalidadeRubrica;
import br.com.sigest.tesouraria.domain.entity.Movimento;
import br.com.sigest.tesouraria.domain.entity.Rubrica;
import br.com.sigest.tesouraria.domain.entity.Socio;
import br.com.sigest.tesouraria.domain.enums.GrauSocio;
import br.com.sigest.tesouraria.domain.enums.StatusCobranca;
import br.com.sigest.tesouraria.domain.enums.StatusSocio;
import br.com.sigest.tesouraria.domain.enums.TipoCobranca;
import br.com.sigest.tesouraria.domain.enums.TipoMovimento;
import br.com.sigest.tesouraria.dto.CobrancaDTO;
import br.com.sigest.tesouraria.dto.ContaReceberDto;
import br.com.sigest.tesouraria.dto.PagamentoLoteRequestDto;
import br.com.sigest.tesouraria.dto.PagamentoRequestDto;
import br.com.sigest.tesouraria.dto.RelatorioInadimplentesDto;
import br.com.sigest.tesouraria.exception.RegraNegocioException;
import br.com.sigest.tesouraria.repository.CobrancaRepository;
import br.com.sigest.tesouraria.repository.ContaFinanceiraRepository;
import br.com.sigest.tesouraria.repository.MovimentoRepository;
import br.com.sigest.tesouraria.repository.RubricaRepository;
import br.com.sigest.tesouraria.repository.SocioRepository;

/**
 * Serviço que gerencia as regras de negócio para as cobranças.
 */
@Service
public class CobrancaService {

    private static final Logger logger = LoggerFactory.getLogger(CobrancaService.class);

    @Autowired
    private CobrancaRepository cobrancaRepository;
    @Autowired
    private SocioRepository socioRepository;
    @Autowired
    private ContaFinanceiraRepository contaFinanceiraRepository;
    @Autowired
    private MovimentoRepository movimentoRepository;
    @Autowired
    private RubricaRepository rubricaRepository;

    public List<Cobranca> filtrar(CobrancaDTO filtro) {
        logger.info("Filtrando cobranças com os critérios: {}", filtro);
        List<Cobranca> cobrancas = cobrancaRepository.findAll();

        return cobrancas.stream()
                .filter(cobranca -> {
                    boolean match = true;

                    // Filtro por Sócio
                    if (filtro.getNomeSocio() != null && !filtro.getNomeSocio().isEmpty()) {
                        match = match && cobranca.getSocio().getNome().toLowerCase()
                                .contains(filtro.getNomeSocio().toLowerCase());
                    }

                    // Filtro por Status
                    if (filtro.getStatus() != null) {
                        match = match && cobranca.getStatus() == filtro.getStatus();
                    }

                    // Filtro por Tipo de Cobrança (mensalidade ou rúbricas)
                    if (filtro.getTipoCobranca() != null) {
                        match = match && cobranca.getTipoCobranca() == filtro.getTipoCobranca();
                    }

                    // Filtro por Vencimento (início e fim)
                    if (filtro.getInicio() != null) {
                        match = match && !cobranca.getDataVencimento().isBefore(filtro.getInicio());
                    }
                    if (filtro.getFim() != null) {
                        match = match && !cobranca.getDataVencimento().isAfter(filtro.getFim());
                    }

                    // Filtro por Data de Pagamento (início e fim)
                    if (filtro.getDataPagamentoInicio() != null) {
                        match = match && (cobranca.getDataPagamento() != null
                                && !cobranca.getDataPagamento().isBefore(filtro.getDataPagamentoInicio()));
                    }
                    if (filtro.getDataPagamentoFim() != null) {
                        match = match && (cobranca.getDataPagamento() != null
                                && !cobranca.getDataPagamento().isAfter(filtro.getDataPagamentoFim()));
                    }
                    return match;
                })
                .collect(Collectors.toList());
    }

    public Cobranca findById(Long id) {
        return cobrancaRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Cobrança não encontrada."));
    }

    public Cobranca findByIdWithDependents(Long id) {
        return cobrancaRepository.findByIdWithDependents(id)
                .orElseThrow(() -> new RegraNegocioException("Cobrança não encontrada."));
    }

    public List<Cobranca> findBySocioIdAndStatus(Long socioId, StatusCobranca status) {
        Socio socio = socioRepository.findById(socioId)
                .orElseThrow(() -> new RegraNegocioException("Sócio não encontrado!"));
        List<Long> socioIds = new ArrayList<>();
        socioIds.add(socioId);
        if (socio.getDependentes() != null && !socio.getDependentes().isEmpty()) {
            socioIds.addAll(socio.getDependentes().stream().map(Socio::getId).collect(Collectors.toList()));
        }
        return cobrancaRepository.findBySocioIdInAndStatus(socioIds, status);
    }

    public List<Cobranca> findOpenCobrancasBySocioAndDependents(Long socioId) {
        Socio socio = socioRepository.findById(socioId)
                .orElseThrow(() -> new RegraNegocioException("Sócio não encontrado!"));

        List<Long> socioAndDependentIds = new ArrayList<>();
        socioAndDependentIds.add(socio.getId()); // Add the main socio's ID

        // Add dependents' IDs
        if (socio.getDependentes() != null && !socio.getDependentes().isEmpty()) {
            socioAndDependentIds.addAll(socio.getDependentes().stream()
                    .map(Socio::getId)
                    .collect(Collectors.toList()));
        }

        // Find all open cobrancas for the socio and their dependents
        return cobrancaRepository.findBySocioIdInAndStatus(socioAndDependentIds, StatusCobranca.ABERTA);
    }

    public List<Cobranca> findByPagadorAndStatus(String pagador, StatusCobranca status) {
        return cobrancaRepository.findByPagadorAndStatus(pagador, status);
    }

    @Transactional
    public Cobranca gerarCobrancaManual(Cobranca cobranca) {
        Socio socio = socioRepository.findById(cobranca.getSocio().getId())
                .orElseThrow(() -> new RegraNegocioException("Sócio não encontrado."));
        if (socio.getStatus() != StatusSocio.FREQUENTE) {
            throw new RegraNegocioException("Só é possível gerar cobranças para sócios com status 'FREQUENTE'.");
        }
        cobranca.setStatus(StatusCobranca.ABERTA);
        logger.info("Cobrança manual gerada para o sócio: {}", socio.getId());
        return cobrancaRepository.save(cobranca);
    }

    @Transactional
    public void registrarRecebimento(Long cobrancaId, PagamentoRequestDto pagamentoDto) {
        Cobranca cobranca = cobrancaRepository.findById(cobrancaId)
                .orElseThrow(() -> new RegraNegocioException("Cobrança não encontrada."));
        if (cobranca.getStatus() == StatusCobranca.PAGA || cobranca.getStatus() == StatusCobranca.CANCELADA) {
            throw new RegraNegocioException("Esta cobrança já foi paga ou cancelada.");
        }
        ContaFinanceira contaFinanceira = contaFinanceiraRepository.findById(pagamentoDto.getContaFinanceiraId())
                .orElseThrow(() -> new RegraNegocioException("Conta financeira não encontrada."));

        contaFinanceira.setSaldoAtual(contaFinanceira.getSaldoAtual() + cobranca.getValor());
        contaFinanceiraRepository.save(contaFinanceira);

        cobranca.setStatus(StatusCobranca.PAGA);
        cobranca.setDataPagamento(pagamentoDto.getDataPagamento());
        cobrancaRepository.save(cobranca);

        GrupoMensalidade grupo = cobranca.getSocio().getGrupoMensalidade();
        if (grupo != null && grupo.getRubricas() != null && !grupo.getRubricas().isEmpty()) {
            for (GrupoMensalidadeRubrica grupoMensalidadeRubrica : grupo.getRubricas()) {
                Movimento movimento = new Movimento();
                movimento.setTipo(TipoMovimento.CREDITO);
                movimento.setValor(grupoMensalidadeRubrica.getValor());
                movimento.setContaFinanceira(contaFinanceira);
                movimento.setRubrica(grupoMensalidadeRubrica.getRubrica());
                movimento.setCentroCusto(grupoMensalidadeRubrica.getRubrica().getCentroCusto());
                movimento.setDataHora(pagamentoDto.getDataPagamento().atStartOfDay());
                movimento.setOrigemDestino("Recebimento Mensalidade Sócio: " + cobranca.getSocio().getNome() + " - "
                        + grupoMensalidadeRubrica.getRubrica().getNome());
                movimentoRepository.save(movimento);
                logger.info("Movimento de crédito criado para a rubrica '{}' do sócio {}",
                        grupoMensalidadeRubrica.getRubrica().getNome(), cobranca.getSocio().getNome());
            }
        } else {
            Movimento movimento = new Movimento();
            movimento.setTipo(TipoMovimento.CREDITO);
            movimento.setValor(cobranca.getValor());
            movimento.setContaFinanceira(contaFinanceira);
            movimento.setRubrica(cobranca.getRubrica());
            movimento.setCentroCusto(cobranca.getRubrica().getCentroCusto());
            movimento.setDataHora(pagamentoDto.getDataPagamento().atStartOfDay());
            String origem = cobranca.getSocio() != null ? cobranca.getSocio().getNome() : cobranca.getPagador();
            movimento.setOrigemDestino("Recebimento de cobrança: " + origem);
            movimentoRepository.save(movimento);
            logger.info("Pagamento de cobrança {} registrado com sucesso. Movimento financeiro criado.",
                    cobranca.getId());
        }
    }

    @Transactional
    public void quitarCobrancasEmLote(PagamentoLoteRequestDto pagamentoDto) {
        logger.info("Quitanto cobranças em lote para {} cobranças.", pagamentoDto.getCobrancaIds().size());

        ContaFinanceira contaFinanceira = contaFinanceiraRepository.findById(pagamentoDto.getContaFinanceiraId())
                .orElseThrow(() -> new RegraNegocioException("Conta financeira não encontrada."));

        for (Long cobrancaId : pagamentoDto.getCobrancaIds()) {
            Cobranca cobranca = cobrancaRepository.findById(cobrancaId)
                    .orElseThrow(() -> new RegraNegocioException("Cobrança não encontrada: " + cobrancaId));

            if (cobranca.getStatus() == StatusCobranca.PAGA || cobranca.getStatus() == StatusCobranca.CANCELADA) {
                logger.warn("Cobrança {} já foi paga ou cancelada. Pulando.", cobrancaId);
                continue;
            }

            // Atualiza o saldo da conta financeira
            contaFinanceira.setSaldoAtual(contaFinanceira.getSaldoAtual() + cobranca.getValor());

            // Atualiza o status e data de pagamento da cobrança
            cobranca.setStatus(StatusCobranca.PAGA);
            cobranca.setDataPagamento(pagamentoDto.getDataPagamento());
            cobrancaRepository.save(cobranca);

            // Lógica de criação de movimento financeiro baseada no tipo de cobrança
            if (cobranca.getTipoCobranca() == TipoCobranca.MENSALIDADE &&
                    cobranca.getSocio() != null &&
                    cobranca.getSocio().getGrupoMensalidade() != null &&
                    cobranca.getSocio().getGrupoMensalidade().getRubricas() != null &&
                    !cobranca.getSocio().getGrupoMensalidade().getRubricas().isEmpty()) {

                for (GrupoMensalidadeRubrica grupoMensalidadeRubrica : cobranca.getSocio().getGrupoMensalidade()
                        .getRubricas()) {
                    Movimento movimento = new Movimento();
                    movimento.setTipo(TipoMovimento.CREDITO);
                    movimento.setValor(grupoMensalidadeRubrica.getValor());
                    movimento.setContaFinanceira(contaFinanceira);
                    movimento.setRubrica(grupoMensalidadeRubrica.getRubrica());
                    movimento.setCentroCusto(grupoMensalidadeRubrica.getRubrica().getCentroCusto());
                    movimento.setDataHora(pagamentoDto.getDataPagamento().atStartOfDay());
                    movimento.setOrigemDestino("Recebimento Mensalidade Sócio: " + cobranca.getSocio().getNome() + " - "
                            + grupoMensalidadeRubrica.getRubrica().getNome());
                    movimentoRepository.save(movimento);
                    logger.info("Movimento de crédito criado para a rubrica '{}' do sócio {} (lote)",
                            grupoMensalidadeRubrica.getRubrica().getNome(), cobranca.getSocio().getNome());
                }
            } else {
                Movimento movimento = new Movimento();
                movimento.setTipo(TipoMovimento.CREDITO);
                movimento.setValor(cobranca.getValor());
                movimento.setContaFinanceira(contaFinanceira);
                movimento.setRubrica(cobranca.getRubrica());
                movimento.setCentroCusto(cobranca.getRubrica().getCentroCusto());
                movimento.setDataHora(pagamentoDto.getDataPagamento().atStartOfDay());
                String origem = cobranca.getSocio() != null ? cobranca.getSocio().getNome() : cobranca.getPagador();
                movimento.setOrigemDestino(
                        "Recebimento de cobrança em lote: " + origem + " - " + cobranca.getDescricao());
                movimentoRepository.save(movimento);
                logger.info("Cobrança {} quitada com sucesso. Movimento financeiro criado (lote).", cobrancaId);
            }
        }
        contaFinanceiraRepository.save(contaFinanceira);
    }

    @Transactional
    public void gerarCobrancaMensalidade(List<Long> sociosIds, int mes, int ano) {
        if (sociosIds == null || sociosIds.isEmpty()) {
            throw new RegraNegocioException("Nenhum sócio selecionado para gerar cobrança.");
        }

        List<Socio> socios = socioRepository.findAllById(sociosIds);

        for (Socio socio : socios) {
            if (socio.getStatus() != StatusSocio.FREQUENTE) {
                logger.warn("Sócio {} não está com status 'FREQUENTE'. Cobrança não gerada.", socio.getId());
                continue;
            }

            if (socio.getGrupoMensalidade() != null && !socio.getGrupoMensalidade().getRubricas().isEmpty()) {
                Cobranca cobrancaExistente = cobrancaRepository.findBySocioAndMesAndAno(socio, mes, ano).orElse(null);

                float valorTotal = (float) socio.getGrupoMensalidade().getRubricas().stream()
                        .mapToDouble(GrupoMensalidadeRubrica::getValor)
                        .sum();

                Cobranca cobranca;
                if (cobrancaExistente != null) {
                    cobranca = cobrancaExistente;
                    cobranca.setValor(valorTotal);
                    cobranca.setDescricao("Mensalidade referente ao mês de " + mes + "/" + ano);
                    logger.info("Cobrança de mensalidade para o sócio {} sobrescrita para o mês {}/{}", socio.getId(),
                            mes, ano);
                } else {
                    cobranca = new Cobranca();
                    cobranca.setSocio(socio);
                    cobranca.setValor(valorTotal);
                    cobranca.setGrupoMensalidade(socio.getGrupoMensalidade());
                    cobranca.setDescricao("Mensalidade referente ao mês de " + mes + "/" + ano);
                    cobranca.setDataVencimento(LocalDate.of(ano, mes, 10)); // Vencimento no dia 10 do mês
                    cobranca.setStatus(StatusCobranca.ABERTA);
                    cobranca.setTipoCobranca(TipoCobranca.MENSALIDADE);
                    logger.info("Nova cobrança de mensalidade gerada para o sócio {} para o mês {}/{}", socio.getId(),
                            mes, ano);
                }

                cobrancaRepository.save(cobranca);
            } else {
                logger.error("Sócio {} não possui grupo de mensalidade ou rubricas para gerar cobrança.",
                        socio.getId());
            }
        }
    }

    @Transactional
    public List<Cobranca> gerarCobrancaMensalidadeManual(List<Long> sociosIds) {
        logger.info("Gerando cobranças de mensalidade manualmente para {} sócios.", sociosIds.size());
        return sociosIds.stream()
                .map(id -> socioRepository.findById(id)
                        .orElseThrow(() -> new RegraNegocioException("Sócio não encontrado.")))
                .filter(socio -> socio.getStatus() == StatusSocio.FREQUENTE)
                .map(this::gerarCobrancaMensalidade)
                .collect(Collectors.toList());
    }

    public Cobranca gerarCobrancaMensalidade(Socio socio) {
        if (socio.getGrupoMensalidade() != null && socio.getGrupoMensalidade().getRubricas() != null
                && !socio.getGrupoMensalidade().getRubricas().isEmpty()) {
            // Corrige o cálculo do valor para somar os valores das rubricas
            float valorTotal = (float) socio.getGrupoMensalidade().getRubricas().stream()
                    .mapToDouble(GrupoMensalidadeRubrica::getValor)
                    .sum();

            Cobranca cobranca = new Cobranca();
            cobranca.setSocio(socio);
            cobranca.setValor(valorTotal);
            // Salva o GrupoMensalidade na cobrança
            cobranca.setGrupoMensalidade(socio.getGrupoMensalidade());
            // cobranca.setRubrica("Mensalidade");
            cobranca.setDescricao("Mensalidade referente ao mês de " + LocalDate.now().getMonthValue());
            cobranca.setDataVencimento(LocalDate.now().withDayOfMonth(10));
            cobranca.setStatus(StatusCobranca.ABERTA);
            cobranca.setTipoCobranca(TipoCobranca.MENSALIDADE);
            logger.info("Cobrança de mensalidade gerada para o sócio: {}", socio.getId());
            return cobrancaRepository.save(cobranca);
        }
        logger.error("Sócio {} não possui grupo de mensalidade ou rubricas para gerar cobrança.", socio.getId());
        throw new RegraNegocioException("Sócio não possui grupo de mensalidade ou rubricas para gerar cobrança.");
    }

    @Transactional
    public Cobranca gerarCobrancaOutrasRubricas(CobrancaDTO dto) {
        Socio socio = socioRepository.findById(dto.getSocioId())
                .orElseThrow(() -> new RegraNegocioException("Sócio não encontrado."));
        if (socio.getStatus() != StatusSocio.FREQUENTE) {
            throw new RegraNegocioException("Só é possível gerar cobranças para sócios com status 'FREQUENTE'.");
        }

        // Busca a Rubrica pelo nome e salva o código (ID) como string
        Rubrica rubrica = rubricaRepository.findByNome(dto.getRubrica())
                .orElseThrow(() -> new RegraNegocioException("Rubrica não encontrada."));

        Cobranca cobranca = new Cobranca();
        cobranca.setSocio(socio);
        cobranca.setRubrica(rubrica); // Salva o objeto Rubrica
        cobranca.setDescricao(dto.getDescricao());
        cobranca.setValor(dto.getValor());
        cobranca.setDataVencimento(dto.getDataVencimento());
        cobranca.setStatus(StatusCobranca.ABERTA);
        cobranca.setTipoCobranca(TipoCobranca.OUTRAS_RUBRICAS);
        logger.info("Cobrança de outras rubricas gerada para o sócio: {}", socio.getId());
        return cobrancaRepository.save(cobranca);
    }

    @Transactional
    public Cobranca criarContaReceber(ContaReceberDto dto) {
        logger.info("Criando nova conta a receber para o pagador: {}", dto.getPagador());

        Rubrica rubrica = rubricaRepository.findById(dto.getRubricaId())
                .orElseThrow(() -> new RegraNegocioException("Rubrica não encontrada."));

        Cobranca cobranca = new Cobranca();
        cobranca.setSocio(null); // Não é associado a um sócio
        cobranca.setPagador(dto.getPagador());
        cobranca.setDescricao(dto.getDescricao());
        cobranca.setValor(dto.getValor());
        cobranca.setDataVencimento(dto.getDataVencimento());
        cobranca.setRubrica(rubrica); // Salva o objeto Rubrica
        cobranca.setStatus(StatusCobranca.ABERTA);
        cobranca.setTipoCobranca(TipoCobranca.AVULSA);

        logger.info("Conta a receber criada com sucesso para o pagador: {}", dto.getPagador());
        return cobrancaRepository.save(cobranca);
    }

    public List<Cobranca> findContasAReceber() {
        return cobrancaRepository.findAll().stream()
                .filter(c -> c.getTipoCobranca() == TipoCobranca.AVULSA)
                .collect(Collectors.toList());
    }

    public void excluir(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'excluir'");
    }

    public List<RelatorioInadimplentesDto> gerarRelatorioInadimplentes() {
        LocalDate dataLimite = LocalDate.now().withDayOfMonth(10);
        List<RelatorioInadimplentesDto> inadimplentes = cobrancaRepository.findInadimplentes(dataLimite);

        // Ordenar por GrauSocio na ordem QM, SCS, CI, QS
        inadimplentes.sort(Comparator.comparing(RelatorioInadimplentesDto::getGrauSocio, (g1, g2) -> {
            List<GrauSocio> ordem = List.of(GrauSocio.QM, GrauSocio.CDC, GrauSocio.CI, GrauSocio.QS);
            return Integer.compare(ordem.indexOf(g1), ordem.indexOf(g2));
        }));

        return inadimplentes;
    }
}