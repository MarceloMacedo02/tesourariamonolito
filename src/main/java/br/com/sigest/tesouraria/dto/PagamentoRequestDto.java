package br.com.sigest.tesouraria.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

/**
 * DTO para a requisição de pagamento de uma cobrança.
 */
@Data
@Builder
public class PagamentoRequestDto {
    @NotNull(message = "A conta financeira é obrigatória.")
    private Long contaFinanceiraId;
    
    @NotNull(message = "A data de pagamento é obrigatória.")
    private LocalDate dataPagamento;

    @NotNull(message = "O valor é obrigatório.")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero.")
    private Float valor;
}