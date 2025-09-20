package br.com.sigest.tesouraria.dto;

import java.util.List;

import br.com.sigest.tesouraria.domain.entity.TransacaoPendente;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransacaoProcessingResult {
    private List<TransacaoDto> creditTransacoes;
    private List<TransacaoDto> debitTransacoes;
    private List<TransacaoPendente> transacoesPendentes;

    // Construtor para compatibilidade com código existente
    public TransacaoProcessingResult(List<TransacaoDto> creditTransacoes, List<TransacaoDto> debitTransacoes) {
        this.creditTransacoes = creditTransacoes;
        this.debitTransacoes = debitTransacoes;
        this.transacoesPendentes = List.of();
    }
}
