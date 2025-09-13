package br.com.sigest.tesouraria.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DemonstrativoFinanceiroMensalDto {
    private List<RubricaGroupDto> entradasPorRubrica;
    private List<RubricaGroupDto> saidasPorRubrica;
    private BigDecimal saldoMesAnterior;
    private BigDecimal totalReceitas;
    private BigDecimal totalDespesas;
    private BigDecimal resultadoOperacional;
    private int mes;
    private int ano;
}
