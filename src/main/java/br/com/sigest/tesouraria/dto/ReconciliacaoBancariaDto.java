package br.com.sigest.tesouraria.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReconciliacaoBancariaDto {
    private Long id;
    private Long reconciliacaoMensalId;
    private Long contaFinanceiraId;
    private String contaFinanceiraNome;
    private Integer mes;
    private Integer ano;
    private BigDecimal saldoAnterior;
    private BigDecimal saldoAtual;
    private BigDecimal receitas;
    private BigDecimal despesas;
    private BigDecimal saldo;
}