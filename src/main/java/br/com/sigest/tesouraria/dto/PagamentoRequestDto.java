package br.com.sigest.tesouraria.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

/**
 * DTO para a requisição de pagamento de uma cobrança.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagamentoRequestDto {
    @NotNull(message = "A conta financeira é obrigatória.")
    private Long contaFinanceiraId;
    
    @NotNull(message = "A data de pagamento é obrigatória.")
    private LocalDate dataPagamento;

    @NotNull(message = "O valor é obrigatório.")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero.")
    private Float valor;
}