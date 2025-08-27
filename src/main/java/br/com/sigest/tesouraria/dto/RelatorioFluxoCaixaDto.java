package br.com.sigest.tesouraria.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
@Data @AllArgsConstructor @NoArgsConstructor
public class RelatorioFluxoCaixaDto {
    private BigDecimal saldoAtual;
    private BigDecimal projecaoRecebimentos;
    private BigDecimal projecaoPagamentos;
    private BigDecimal saldoProjetado;
}