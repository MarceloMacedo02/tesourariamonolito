package br.com.sigest.tesouraria.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
@Data @AllArgsConstructor @NoArgsConstructor
public class RelatorioBalanceteDto {
    private BigDecimal totalReceitas;
    private BigDecimal totalDespesas;
    private BigDecimal resultado;
}