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
        
        // Associar as reconciliações bancárias à reconciliação mensal
        if (reconciliacao.getReconciliacoesBancarias() != null) {
            logger.info("Número de reconciliações bancárias: {}", reconciliacao.getReconciliacoesBancarias().size());
            for (ReconciliacaoBancaria rb : reconciliacao.getReconciliacoesBancarias()) {
                // Associar a reconciliação mensal a cada reconciliação bancária
                rb.setReconciliacaoMensal(reconciliacao);
                
                // Garantir que os valores não sejam nulos
                if (rb.getSaldoAnterior() == null) rb.setSaldoAnterior(BigDecimal.ZERO);
                if (rb.getSaldoAtual() == null) rb.setSaldoAtual(BigDecimal.ZERO);
                if (rb.getReceitas() == null) rb.setReceitas(BigDecimal.ZERO);
                if (rb.getDespesas() == null) rb.setDespesas(BigDecimal.ZERO);
                
                // Garantir que mes e ano sejam preenchidos
                if (rb.getMes() == null) rb.setMes(reconciliacao.getMes());
                if (rb.getAno() == null) rb.setAno(reconciliacao.getAno());
                
                // Verificar se a conta financeira foi corretamente vinculada
                if (rb.getContaFinanceira() != null && rb.getContaFinanceira().getId() != null && rb.getContaFinanceira().getNome() == null) {
                    // Se a conta financeira tem ID mas não tem nome, buscar do banco de dados
                    ContaFinanceira conta = contaFinanceiraRepository.findById(rb.getContaFinanceira().getId()).orElse(null);
                    if (conta != null) {
                        rb.setContaFinanceira(conta);
                    }
                }
                
                // Calcular o saldo antes de salvar apenas se não foi preenchido no formulário ou é zero
                if (rb.getSaldo() == null || rb.getSaldo().compareTo(BigDecimal.ZERO) == 0) {
                    BigDecimal saldo = rb.getSaldoAnterior().add(rb.getReceitas()).subtract(rb.getDespesas());
                    rb.setSaldo(saldo);
                }
                
                logger.info("ReconciliacaoBancaria: conta={}, saldoAnterior={}, saldoAtual={}, receitas={}, despesas={}, saldo={}",
                    rb.getContaFinanceira() != null ? rb.getContaFinanceira().getNome() : "null",
                    rb.getSaldoAnterior(), rb.getSaldoAtual(), rb.getReceitas(), rb.getDespesas(), rb.getSaldo());
            }
        } else {
            logger.info("Nenhuma reconciliação bancária encontrada");
        }
        
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
        reconciliacao.setSaldoMesAnterior(BigDecimal.ZERO);
        reconciliacao.setResultadoOperacional(BigDecimal.ZERO);

        List<ContaFinanceira> contas = contaFinanceiraRepository.findAll();
        List<ReconciliacaoBancaria> reconciliacoesBancarias = new ArrayList<>();
        for (ContaFinanceira conta : contas) {
            ReconciliacaoBancaria rb = new ReconciliacaoBancaria();
            rb.setContaFinanceira(conta);
            // Verifica se saldoAtual é nulo antes de usar
            BigDecimal saldoAtual = conta.getSaldoAtual() != null ? conta.getSaldoAtual() : BigDecimal.ZERO;
            rb.setSaldo(saldoAtual);
            rb.setReconciliacaoMensal(reconciliacao);
            rb.setMes(mes);
            rb.setAno(ano);
            rb.setSaldoAnterior(saldoAtual);
            rb.setSaldoAtual(saldoAtual);
            rb.setReceitas(BigDecimal.ZERO);
            rb.setDespesas(BigDecimal.ZERO);
            reconciliacoesBancarias.add(rb);
        }
        reconciliacao.setReconciliacoesBancarias(reconciliacoesBancarias);

        return reconciliacao;
    }

    private BigDecimal calcularSaldoSugerido(ReconciliacaoMensal reconciliacao) {
        BigDecimal saldoInicialTotal = reconciliacao.getReconciliacoesBancarias().stream()
                .map(rb -> rb.getSaldo() != null ? rb.getSaldo() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        YearMonth yearMonth = YearMonth.of(reconciliacao.getAno(), reconciliacao.getMes());
        List<Movimento> movimentos = movimentoRepository.findByDataHoraBetween(
                yearMonth.atDay(1).atStartOfDay(),
                yearMonth.atEndOfMonth().atTime(23, 59, 59));

        BigDecimal totalEntradas = movimentos.stream()
                .filter(m -> m.getTipo() == TipoMovimento.ENTRADA)
                // Usando mov.getValor() diretamente em vez de
                // BigDecimal.valueOf(mov.getValor())
                // porque mov.getValor() já retorna um BigDecimal, evitando conversões
                // desnecessárias
                .map(m -> m.getValor() != null ? m.getValor() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSaidas = movimentos.stream()
                .filter(m -> m.getTipo() == TipoMovimento.SAIDA)
                // Usando mov.getValor() diretamente em vez de
                // BigDecimal.valueOf(mov.getValor())
                // porque mov.getValor() já retorna um BigDecimal, evitando conversões
                // desnecessárias
                .map(m -> m.getValor() != null ? m.getValor() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calcular o resultado operacional (saldo anterior + entradas - saídas)
        BigDecimal saldoMesAnterior = reconciliacao.getSaldoMesAnterior() != null ? 
            reconciliacao.getSaldoMesAnterior() : BigDecimal.ZERO;
            
        BigDecimal resultadoOperacional = saldoMesAnterior
                .add(totalEntradas)
                .subtract(totalSaidas);

        reconciliacao.setResultadoOperacional(resultadoOperacional);

        return saldoInicialTotal.add(totalEntradas).subtract(totalSaidas);
    }
}
