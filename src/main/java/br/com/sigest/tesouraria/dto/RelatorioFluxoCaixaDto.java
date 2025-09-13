package br.com.sigest.tesouraria.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class RelatorioFluxoCaixaDto {
    private BigDecimal saldoAtual;
    private BigDecimal projecaoRecebimentos;
    private BigDecimal projecaoPagamentos;
    private BigDecimal saldoProjetado;
}