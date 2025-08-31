package br.com.sigest.tesouraria.dto;

import br.com.sigest.tesouraria.domain.enums.TipoRubrica;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioDemonstrativoFinanceiroDto {
    private int mes;
    private int ano;
    private BigDecimal saldoPeriodoAnterior;
    private BigDecimal totalEntradas;
    private BigDecimal totalSaidas;
    private BigDecimal saldoOperacional;
    private BigDecimal saldoFinalCaixaBanco;
    private List<RubricaAgrupadaDto> entradasAgrupadas;
    private List<RubricaAgrupadaDto> saidasAgrupadas;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RubricaAgrupadaDto {
        private TipoRubrica tipoRubrica; // RECEITA or DESPESA
        private List<RubricaDetalheDto> rubricas;
        private BigDecimal totalCategoria;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RubricaDetalheDto {
        private String nomeRubrica;
        private BigDecimal valorRubrica;
    }
}
