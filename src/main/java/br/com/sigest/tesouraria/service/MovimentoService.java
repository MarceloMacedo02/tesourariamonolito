package br.com.sigest.tesouraria.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.sigest.tesouraria.domain.entity.ContaFinanceira;
import br.com.sigest.tesouraria.domain.entity.Movimento;
import br.com.sigest.tesouraria.domain.enums.TipoMovimento;
import br.com.sigest.tesouraria.exception.RegraNegocioException;
import br.com.sigest.tesouraria.repository.ContaFinanceiraRepository;
import br.com.sigest.tesouraria.repository.MovimentoRepository;

@Service
public class MovimentoService {

    @Autowired
    private MovimentoRepository movimentoRepository;

    @Autowired
    private ContaFinanceiraRepository contaFinanceiraRepository;

    @Transactional
    public void salvarMovimento(Movimento movimento) {
        ContaFinanceira conta = contaFinanceiraRepository.findById(movimento.getContaFinanceira().getId())
                .orElseThrow(() -> new RegraNegocioException("Conta Financeira não encontrada!"));

        if (movimento.getTipo() == TipoMovimento.CREDITO) {
            conta.setSaldoAtual(conta.getSaldoAtual() + movimento.getValor());
        } else { // DEBITO
            if (conta.getSaldoAtual() < movimento.getValor()) {
                throw new RegraNegocioException("Saldo insuficiente na conta " + conta.getNome());
            }
            conta.setSaldoAtual(conta.getSaldoAtual() - movimento.getValor());
        }
        movimento.setCentroCusto(movimento.getRubrica().getCentroCusto());
        contaFinanceiraRepository.save(conta);
        movimentoRepository.save(movimento);
    }

    public List<Movimento> findByPeriodo(LocalDate inicio, LocalDate fim) {
        LocalDateTime inicioDt = inicio.atStartOfDay();
        LocalDateTime fimDt = fim.plusDays(1).atStartOfDay();
        return movimentoRepository.findByDataHoraBetween(inicioDt, fimDt);
    }
}