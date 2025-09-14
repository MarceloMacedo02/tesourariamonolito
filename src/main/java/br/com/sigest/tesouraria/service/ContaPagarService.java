package br.com.sigest.tesouraria.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.sigest.tesouraria.domain.entity.ContaFinanceira;
import br.com.sigest.tesouraria.domain.entity.ContaPagar;
import br.com.sigest.tesouraria.domain.entity.Fornecedor;
import br.com.sigest.tesouraria.domain.entity.Movimento;
import br.com.sigest.tesouraria.domain.entity.Rubrica;
import br.com.sigest.tesouraria.domain.enums.StatusContaPagar;
import br.com.sigest.tesouraria.domain.enums.TipoMovimento;
import br.com.sigest.tesouraria.dto.ContaPagarDto;
import br.com.sigest.tesouraria.dto.PagamentoRequestDto;
import br.com.sigest.tesouraria.exception.RegraNegocioException;
import br.com.sigest.tesouraria.domain.repository.ContaFinanceiraRepository;
import br.com.sigest.tesouraria.domain.repository.ContaPagarRepository;
import br.com.sigest.tesouraria.domain.repository.FornecedorRepository;
import br.com.sigest.tesouraria.domain.repository.MovimentoRepository;
import br.com.sigest.tesouraria.domain.repository.RubricaRepository;

@Service
public class ContaPagarService {

    @Autowired
    private ContaPagarRepository contaPagarRepository;

    @Autowired
    private RubricaRepository rubricaRepository;

    @Autowired
    private FornecedorRepository fornecedorRepository;

    @Autowired
    private MovimentoRepository movimentoRepository;

    @Autowired
    private ContaFinanceiraRepository contaFinanceiraRepository;

    public List<ContaPagar> findAll() {
        return contaPagarRepository.findAll();
    }

    public ContaPagar findById(Long id) {
        return contaPagarRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Conta a pagar não encontrada."));
    }

    @Transactional
    public ContaPagar criarContaPagar(ContaPagarDto dto) {
        Rubrica rubrica = rubricaRepository.findById(dto.getRubricaId())
                .orElseThrow(() -> new RegraNegocioException("Rubrica não encontrada."));

        Fornecedor fornecedor = null;
        if (dto.getFornecedorId() != null) {
            fornecedor = fornecedorRepository.findById(dto.getFornecedorId())
                    .orElseThrow(() -> new RegraNegocioException("Fornecedor não encontrado."));
        }

        ContaPagar conta = new ContaPagar();
        conta.setDescricao(dto.getDescricao());
        conta.setValor(dto.getValor() != null ? BigDecimal.valueOf(dto.getValor()) : BigDecimal.ZERO);
        conta.setDataVencimento(dto.getDataVencimento());
        conta.setRubrica(rubrica);
        conta.setFornecedor(fornecedor);
        conta.setStatus(StatusContaPagar.ABERTA);

        return contaPagarRepository.save(conta);
    }

    @Transactional
    public void liquidarContaPagar(Long contaPagarId, PagamentoRequestDto pagamentoDto) {
        ContaPagar contaPagar = findById(contaPagarId);
        if (contaPagar.getStatus() != StatusContaPagar.ABERTA) {
            throw new RegraNegocioException("Esta conta já foi paga ou cancelada.");
        }

        ContaFinanceira contaFinanceira = contaFinanceiraRepository.findById(pagamentoDto.getContaFinanceiraId())
                .orElseThrow(() -> new RegraNegocioException("Conta financeira não encontrada."));

        // Debita o valor do saldo da conta
        contaFinanceira.setSaldoAtual(contaFinanceira.getSaldoAtual().subtract(new java.math.BigDecimal(contaPagar.getValor().toString())));
        contaFinanceiraRepository.save(contaFinanceira);

        // Atualiza o status da conta a pagar
        contaPagar.setStatus(StatusContaPagar.PAGA);
        contaPagar.setDataPagamento(pagamentoDto.getDataPagamento());
        contaPagarRepository.save(contaPagar);

        // Cria o movimento de débito
        Movimento movimento = new Movimento();
        movimento.setTipo(TipoMovimento.SAIDA);
        movimento.setValor(new java.math.BigDecimal(contaPagar.getValor().toString()));
        movimento.setContaFinanceira(contaFinanceira);
        movimento.setRubrica(contaPagar.getRubrica());
        movimento.setCentroCusto(contaPagar.getRubrica().getCentroCusto());
        movimento.setDataHora(pagamentoDto.getDataPagamento().atStartOfDay());
        String origemDestino = contaPagar.getFornecedor() != null ? contaPagar.getFornecedor().getNome()
                : "Pagamento diverso";
        movimento.setOrigemDestino("Pagamento conta: " + contaPagar.getDescricao() + " - " + origemDestino);
        movimentoRepository.save(movimento);
    }

}
