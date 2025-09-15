package br.com.sigest.tesouraria.service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.sigest.tesouraria.domain.entity.ContaFinanceira;
import br.com.sigest.tesouraria.domain.entity.Movimento;
import br.com.sigest.tesouraria.domain.entity.ReconciliacaoBancaria;
import br.com.sigest.tesouraria.domain.entity.ReconciliacaoMensal;
import br.com.sigest.tesouraria.domain.enums.TipoMovimento;
import br.com.sigest.tesouraria.domain.repository.ContaFinanceiraRepository;
import br.com.sigest.tesouraria.domain.repository.MovimentoRepository;
import br.com.sigest.tesouraria.domain.repository.ReconciliacaoMensalRepository;

@Service
public class ReconciliacaoService {

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
        reconciliacao.setSaldoSugerido(calcularSaldoSugerido(reconciliacao));
        return reconciliacaoMensalRepository.save(reconciliacao);
    }

    @Transactional
    public void deleteById(Long id) {
        reconciliacaoMensalRepository.deleteById(id);
    }

    public ReconciliacaoMensal newReconciliacao(int mes, int ano) {
        ReconciliacaoMensal reconciliacao = new ReconciliacaoMensal();
        reconciliacao.setMes(mes);
        reconciliacao.setAno(ano);
        reconciliacao.setSaldoMesAnterior(BigDecimal.ZERO);
        reconciliacao.setResultadoOperacional(BigDecimal.ZERO);

        List<ContaFinanceira> contas = contaFinanceiraRepository.findAll();
        List<ReconciliacaoBancaria> reconciliacoesBancarias = new ArrayList<>();
        for (ContaFinanceira conta : contas) {
            ReconciliacaoBancaria rb = new ReconciliacaoBancaria();
            rb.setContaFinanceira(conta);
            rb.setSaldo(conta.getSaldoAtual());
            rb.setReconciliacaoMensal(reconciliacao);
            reconciliacoesBancarias.add(rb);
        }
        reconciliacao.setReconciliacoesBancarias(reconciliacoesBancarias);

        reconciliacao.setSaldoSugerido(calcularSaldoSugerido(reconciliacao));
        reconciliacao.setSaldoFinal(reconciliacao.getSaldoSugerido());

        return reconciliacao;
    }

    private BigDecimal calcularSaldoSugerido(ReconciliacaoMensal reconciliacao) {
        BigDecimal saldoInicialTotal = reconciliacao.getReconciliacoesBancarias().stream()
                .map(ReconciliacaoBancaria::getSaldo)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        YearMonth yearMonth = YearMonth.of(reconciliacao.getAno(), reconciliacao.getMes());
        List<Movimento> movimentos = movimentoRepository.findByDataHoraBetween(
                yearMonth.atDay(1).atStartOfDay(),
                yearMonth.atEndOfMonth().atTime(23, 59, 59));

        BigDecimal totalEntradas = movimentos.stream()
                .filter(m -> m.getTipo() == TipoMovimento.ENTRADA)
                // Usando mov.getValor() diretamente em vez de BigDecimal.valueOf(mov.getValor())
                // porque mov.getValor() já retorna um BigDecimal, evitando conversões desnecessárias
                .map(Movimento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSaidas = movimentos.stream()
                .filter(m -> m.getTipo() == TipoMovimento.SAIDA)
                // Usando mov.getValor() diretamente em vez de BigDecimal.valueOf(mov.getValor())
                // porque mov.getValor() já retorna um BigDecimal, evitando conversões desnecessárias
                .map(Movimento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        reconciliacao.setTotalEntradas(totalEntradas);
        reconciliacao.setTotalSaidas(totalSaidas);

        // Calcular o resultado operacional (saldo anterior + entradas - saídas)
        BigDecimal resultadoOperacional = reconciliacao.getSaldoMesAnterior()
                .add(totalEntradas)
                .subtract(totalSaidas);
        
        reconciliacao.setResultadoOperacional(resultadoOperacional);

        return saldoInicialTotal.add(totalEntradas).subtract(totalSaidas);
    }
}
