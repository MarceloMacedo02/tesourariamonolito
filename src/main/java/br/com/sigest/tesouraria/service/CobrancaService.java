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

    public List<Cobranca> listarCobrancas(CobrancaDTO filtro) {
        // Implementar lógica de busca com filtros
        logger.info("Listando cobranças com filtros: {}", filtro);
        if (filtro.getInicio() != null && filtro.getFim() != null) {
            return cobrancaRepository.findByDataVencimentoBetween(filtro.getInicio(), filtro.getFim());
        }
        return cobrancaRepository.findAll();
    }

    public Cobranca findById(Long id) {
        return cobrancaRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Cobrança não encontrada."));
    }

    /**
     * Gera uma cobrança manual para um sócio específico.
     * 
     * @param cobranca A entidade de cobrança a ser gerada.
     * @return A cobrança salva.
     */
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

    /**
     * Registra o recebimento de uma cobrança, atualizando o saldo da conta e
     * criando um movimento financeiro.
     * 
     * @param cobrancaId   O ID da cobrança a ser quitada.
     * @param pagamentoDto DTO com os dados do pagamento.
     */
    @Transactional
    public void registrarRecebimento(Long cobrancaId, PagamentoRequestDto pagamentoDto) {
        Cobranca cobranca = cobrancaRepository.findById(cobrancaId)
                .orElseThrow(() -> new RegraNegocioException("Cobrança não encontrada."));
        if (cobranca.getStatus() == StatusCobranca.PAGA || cobranca.getStatus() == StatusCobranca.CANCELADA) {
            throw new RegraNegocioException("Esta cobrança já foi paga ou cancelada.");
        }
        ContaFinanceira contaFinanceira = contaFinanceiraRepository.findById(pagamentoDto.getContaFinanceiraId())
                .orElseThrow(() -> new RegraNegocioException("Conta financeira não encontrada."));

        // Credita o valor na conta financeira
        contaFinanceira.setSaldoAtual(contaFinanceira.getSaldoAtual() + cobranca.getValor());
        contaFinanceiraRepository.save(contaFinanceira);

        // Atualiza o status e data da cobrança
        cobranca.setStatus(StatusCobranca.PAGA);
        cobranca.setDataPagamento(pagamentoDto.getDataPagamento());
        cobrancaRepository.save(cobranca);

        // Lógica para criar movimentos por rubrica se for mensalidade
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
            // Lógica para criar movimento único para outras rubricas ou mensalidades sem
            // grupo
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

    /**
     * Gera cobranças de mensalidade manualmente para uma lista de sócios.
     * 
     * @param sociosIds Lista de IDs dos sócios.
     * @return Lista de cobranças geradas.
     */
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

    /**
     * Gera uma cobrança de mensalidade para um sócio específico.
     * O valor é calculado somando os valores padrão das rubricas do grupo.
     * 
     * @param socio O sócio para o qual a cobrança será gerada.
     * @return A cobrança salva.
     */
    public Cobranca gerarCobrancaMensalidade(Socio socio) {
        if (socio.getGrupoMensalidade() != null && socio.getGrupoMensalidade().getRubricas() != null
                && !socio.getGrupoMensalidade().getRubricas().isEmpty()) {
            float valorTotal = 0.0F;
            for (GrupoMensalidadeRubrica rubrica : socio.getGrupoMensalidade().getRubricas()) {
                valorTotal += rubrica.getRubrica().getValorPadrao();
            }

            Cobranca cobranca = new Cobranca();
            cobranca.setSocio(socio);
            cobranca.setValor(valorTotal);
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

    
    /**
     * Gera uma cobrança de outras rubricas para um sócio específico.
     * @param dto DTO com os dados da cobrança.
     * @return A cobrança salva.
     */
    @Transactional
    public Cobranca gerarCobrancaOutrasRubricas(CobrancaDTO dto) {
        Socio socio = socioRepository.findById(dto.getSocioId())
                .orElseThrow(() -> new RegraNegocioException("Sócio não encontrado."));
        if (socio.getStatus() != StatusSocio.FREQUENTE) {
            throw new RegraNegocioException("Só é possível gerar cobranças para sócios com status 'FREQUENTE'.");
        }
        Cobranca cobranca = new Cobranca();
        cobranca.setSocio(socio);
        cobranca.setRubrica(dto.getRubrica());
        cobranca.setDescricao(dto.getDescricao());
        cobranca.setValor(dto.getValor());
        cobranca.setDataVencimento(dto.getDataVencimento());
        cobranca.setStatus(StatusCobranca.ABERTA);
        cobranca.setTipoCobranca(TipoCobranca.OUTRAS_RUBRICAS);
        logger.info("Cobrança de outras rubricas gerada para o sócio: {}", socio.getId());
        return cobrancaRepository.save(cobranca);
    }
}