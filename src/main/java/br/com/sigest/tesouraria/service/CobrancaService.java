package br.com.sigest.tesouraria.service;

import java.time.LocalDate;
import java.util.List;
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
import br.com.sigest.tesouraria.domain.enums.StatusCobranca;
import br.com.sigest.tesouraria.domain.enums.StatusSocio;
import br.com.sigest.tesouraria.domain.enums.TipoCobranca;
import br.com.sigest.tesouraria.domain.enums.TipoMovimento;
import br.com.sigest.tesouraria.dto.CobrancaDTO;
import br.com.sigest.tesouraria.dto.PagamentoRequestDto;
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
            float valorPorRubrica = cobranca.getValor() / grupo.getRubricas().size();
            valorPorRubrica = Math.round(valorPorRubrica * 100.0f) / 100.0f;

            for (GrupoMensalidadeRubrica grupoMensalidadeRubrica : grupo.getRubricas()) {
                Movimento movimento = new Movimento();
                movimento.setTipo(TipoMovimento.CREDITO);
                movimento.setValor(valorPorRubrica);
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
            Rubrica rubrica = rubricaRepository.findByNome(cobranca.getRubrica())
                    .orElseThrow(() -> new RegraNegocioException(
                            "Rubrica de movimento não encontrada para " + cobranca.getRubrica()));
            movimento.setRubrica(rubrica);
            movimento.setCentroCusto(rubrica.getCentroCusto());
            movimento.setDataHora(pagamentoDto.getDataPagamento().atStartOfDay());
            movimento.setOrigemDestino("Recebimento de cobrança do sócio: " + cobranca.getSocio().getNome());
            movimentoRepository.save(movimento);
            logger.info("Pagamento de cobrança {} registrado com sucesso. Movimento financeiro criado.",
                    cobranca.getId());
        }
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
                    logger.info("Cobrança de mensalidade para o sócio {} sobrescrita para o mês {}/{}", socio.getId(), mes, ano);
                } else {
                    cobranca = new Cobranca();
                    cobranca.setSocio(socio);
                    cobranca.setValor(valorTotal);
                    cobranca.setRubrica("Mensalidade");
                    cobranca.setDescricao("Mensalidade referente ao mês de " + mes + "/" + ano);
                    cobranca.setDataVencimento(LocalDate.of(ano, mes, 10)); // Vencimento no dia 10 do mês
                    cobranca.setStatus(StatusCobranca.ABERTA);
                    cobranca.setTipoCobranca(TipoCobranca.MENSALIDADE);
                    logger.info("Nova cobrança de mensalidade gerada para o sócio {} para o mês {}/{}", socio.getId(), mes, ano);
                }

                cobrancaRepository.save(cobranca);
            } else {
                logger.error("Sócio {} não possui grupo de mensalidade ou rubricas para gerar cobrança.", socio.getId());
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
            cobranca.setRubrica("Mensalidade");
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
        cobranca.setRubrica(String.valueOf(rubrica.getId())); // Salva o ID da rubrica
        cobranca.setDescricao(dto.getDescricao());
        cobranca.setValor(dto.getValor());
        cobranca.setDataVencimento(dto.getDataVencimento());
        cobranca.setStatus(StatusCobranca.ABERTA);
        cobranca.setTipoCobranca(TipoCobranca.OUTRAS_RUBRICAS);
        logger.info("Cobrança de outras rubricas gerada para o sócio: {}", socio.getId());
        return cobrancaRepository.save(cobranca);
    }

    public void excluir(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'excluir'");
    }
}