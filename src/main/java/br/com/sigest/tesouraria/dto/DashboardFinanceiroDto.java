package br.com.sigest.tesouraria.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DashboardFinanceiroDto {
    private List<DadoGraficoDto> entradasMensais;
    private List<DadoGraficoDto> saidasMensais;
    private List<DadoGraficoDto> saldosMensais;
    private List<DadoGraficoDto> entradasAnuais;
    private List<DadoGraficoDto> saidasAnuais;
    private List<DadoGraficoDto> saldosAnuais;
    private long totalSocios;
    private long sociosFrequentes;
    private long sociosNaoFrequentes;
    private long totalInadimplentes;
    private BigDecimal totalAReceber;
    private List<DadoGraficoDto> movimentacoesMensais;
}