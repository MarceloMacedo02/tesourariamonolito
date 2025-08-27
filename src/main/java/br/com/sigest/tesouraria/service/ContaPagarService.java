package br.com.sigest.tesouraria.service;
import br.com.sigest.tesouraria.domain.entity.*;
import br.com.sigest.tesouraria.domain.enums.*;
import br.com.sigest.tesouraria.dto.PagamentoRequestDto;
import br.com.sigest.tesouraria.exception.RegraNegocioException;
import br.com.sigest.tesouraria.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class ContaPagarService {
    @Autowired private ContaPagarRepository repository;
    @Autowired private ContaFinanceiraRepository contaFinanceiraRepository;
    @Autowired private MovimentoRepository movimentoRepository;
    public ContaPagar save(ContaPagar contaPagar) {
        contaPagar.setStatus(StatusContaPagar.A_PAGAR);
        return repository.save(contaPagar);
    }
    @Transactional
    public void registrarPagamento(Long contaPagarId, PagamentoRequestDto pagamentoDto) {
        ContaPagar contaPagar = repository.findById(contaPagarId)
                .orElseThrow(() -> new RegraNegocioException("Conta a pagar não encontrada."));
        if (contaPagar.getStatus() == StatusContaPagar.PAGA || contaPagar.getStatus() == StatusContaPagar.CANCELADA) {
            throw new RegraNegocioException("Esta conta já foi paga ou cancelada.");
        }
        ContaFinanceira contaFinanceira = contaFinanceiraRepository.findById(pagamentoDto.getContaFinanceiraId())
                .orElseThrow(() -> new RegraNegocioException("Conta financeira não encontrada."));
        if (contaFinanceira.getSaldoAtual() < contaPagar.getValor().floatValue()) {
            throw new RegraNegocioException("Saldo insuficiente na conta financeira.");
        }
        contaFinanceira.setSaldoAtual(contaFinanceira.getSaldoAtual() - contaPagar.getValor().floatValue());
        contaFinanceiraRepository.save(contaFinanceira);
        contaPagar.setStatus(StatusContaPagar.PAGA);
        contaPagar.setDataPagamento(pagamentoDto.getDataPagamento());
        repository.save(contaPagar);
        Movimento movimento = new Movimento();
        movimento.setTipo(TipoMovimento.DEBITO);
        movimento.setValor(contaPagar.getValor().floatValue());
        movimento.setContaFinanceira(contaFinanceira);
        movimento.setRubrica(contaPagar.getRubrica());
        movimento.setCentroCusto(contaPagar.getRubrica().getCentroCusto());
        movimento.setDataHora(pagamentoDto.getDataPagamento().atStartOfDay());
        movimento.setOrigemDestino("Pagamento Fornecedor: " + contaPagar.getFornecedor().getNome());
        movimentoRepository.save(movimento);
    }
}