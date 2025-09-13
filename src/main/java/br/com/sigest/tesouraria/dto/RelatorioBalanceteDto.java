package br.com.sigest.tesouraria.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class RelatorioBalanceteDto {
    private BigDecimal totalReceitas;
    private BigDecimal totalDespesas;
    private BigDecimal resultado;
}