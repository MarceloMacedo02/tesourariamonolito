package br.com.sigest.tesouraria.service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.sigest.tesouraria.domain.entity.ContaFinanceira;
import br.com.sigest.tesouraria.domain.entity.Movimento;
import br.com.sigest.tesouraria.domain.entity.ReconciliacaoMensal;
import br.com.sigest.tesouraria.domain.enums.TipoMovimento;
import br.com.sigest.tesouraria.domain.repository.ContaFinanceiraRepository;
import br.com.sigest.tesouraria.domain.repository.MovimentoRepository;
import br.com.sigest.tesouraria.domain.repository.ReconciliacaoMensalRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ReconciliacaoService {

    private static final Logger logger = LoggerFactory.getLogger(ReconciliacaoService.class);

    @Autowired
    private ReconciliacaoMensalRepository reconciliacaoMensalRepository;

    @Autowired
    private ContaFinanceiraRepository contaFinanceiraRepository;

    @Autowired
    private MovimentoRepository movimentoRepository;

    public List<ReconciliacaoMensal> findAll() {
        return reconciliacaoMensalRepository.findAll();
    }

    public Optional<ReconciliacaoMensal> findById(Long id) {
        return reconciliacaoMensalRepository.findById(id);
    }

    @Transactional
    public ReconciliacaoMensal save(ReconciliacaoMensal reconciliacao) {
        logger.info("Iniciando salvamento da reconciliação: mes={}, ano={}", 
            reconciliacao.getMes(), reconciliacao.getAno());
        
        calcularSaldoSugerido(reconciliacao);
        ReconciliacaoMensal saved = reconciliacaoMensalRepository.save(reconciliacao);
        logger.info("Reconciliação salva com ID: {}", saved.getId());
        return saved;
    }

    @Transactional
    public void deleteById(Long id) {
        reconciliacaoMensalRepository.deleteById(id);
    }

    public ReconciliacaoMensal newReconciliacao(int mes, int ano) {
        ReconciliacaoMensal reconciliacao = new ReconciliacaoMensal();
        reconciliacao.setMes(mes);
        reconciliacao.setAno(ano);
        reconciliacao.setSaldoInicial(BigDecimal.ZERO);
        reconciliacao.setTotalEntradas(BigDecimal.ZERO);
        reconciliacao.setTotalSaidas(BigDecimal.ZERO);
        reconciliacao.setSaldoFinal(BigDecimal.ZERO);

        return reconciliacao;
    }

    private BigDecimal calcularSaldoSugerido(ReconciliacaoMensal reconciliacao) {
        YearMonth yearMonth = YearMonth.of(reconciliacao.getAno(), reconciliacao.getMes());
        List<Movimento> movimentos = movimentoRepository.findByDataHoraBetween(
                yearMonth.atDay(1).atStartOfDay(),
                yearMonth.atEndOfMonth().atTime(23, 59, 59));

        BigDecimal totalEntradas = movimentos.stream()
                .filter(m -> m.getTipo() == TipoMovimento.ENTRADA)
                .map(m -> m.getValor() != null ? m.getValor() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        reconciliacao.setTotalEntradas(totalEntradas);

        BigDecimal totalSaidas = movimentos.stream()
                .filter(m -> m.getTipo() == TipoMovimento.SAIDA)
                .map(m -> m.getValor() != null ? m.getValor() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        reconciliacao.setTotalSaidas(totalSaidas);

        // Calcular o resultado operacional (saldo inicial + entradas - saídas)
        BigDecimal saldoInicial = reconciliacao.getSaldoInicial() != null ? 
            reconciliacao.getSaldoInicial() : BigDecimal.ZERO;
            
        BigDecimal resultadoOperacional = saldoInicial
                .add(totalEntradas)
                .subtract(totalSaidas);

        reconciliacao.setSaldoFinal(resultadoOperacional);

        return resultadoOperacional;
    }
}
