package br.com.sigest.tesouraria.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardTesoureiroDto {
    private BigDecimal saldoConsolidado;

    private BigDecimal totalAReceber;

    private BigDecimal totalAPagar;

    private BigDecimal resultadoDoMes;
}