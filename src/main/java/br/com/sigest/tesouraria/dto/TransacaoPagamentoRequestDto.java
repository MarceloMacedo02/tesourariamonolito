package br.com.sigest.tesouraria.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class TransacaoPagamentoRequestDto {
    private Long transacaoId;
    private BigDecimal valor;
    private LocalDate dataPagamento;
    private Long contaFinanceiraId;
    private List<Long> selectedCobrancaIds;
    private Long rubricaId;
}
