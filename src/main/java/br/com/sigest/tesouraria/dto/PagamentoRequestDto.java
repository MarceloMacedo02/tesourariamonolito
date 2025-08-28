package br.com.sigest.tesouraria.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO para a requisição de pagamento de uma cobrança.
 */
@Data
public class PagamentoRequestDto {
    @NotNull(message = "A conta financeira é obrigatória.")
    private Long contaFinanceiraId;
    
    @NotNull(message = "A data de pagamento é obrigatória.")
    private LocalDate dataPagamento;
}