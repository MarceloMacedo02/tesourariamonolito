package br.com.sigest.tesouraria.dto;

import java.math.BigDecimal;
import java.util.List;

import br.com.sigest.tesouraria.domain.enums.TipoRubrica;

public class RelatorioDemonstrativoFinanceiroDto {

    private Integer mes;
    private Integer ano;
    private BigDecimal saldoPeriodoAnterior;
    private BigDecimal totalEntradas;
    private BigDecimal totalSaidas;
    private BigDecimal saldoOperacional;
    private BigDecimal saldoFinalCaixaBanco;
    private List<RubricaAgrupadaDto> entradasAgrupadas;
    private List<RubricaAgrupadaDto> saidasAgrupadas;

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public BigDecimal getSaldoPeriodoAnterior() {
        return saldoPeriodoAnterior;
    }

    public void setSaldoPeriodoAnterior(BigDecimal saldoPeriodoAnterior) {
        this.saldoPeriodoAnterior = saldoPeriodoAnterior;
    }

    public BigDecimal getTotalEntradas() {
        return totalEntradas;
    }

    public void setTotalEntradas(BigDecimal totalEntradas) {
        this.totalEntradas = totalEntradas;
    }

    public BigDecimal getTotalSaidas() {
        return totalSaidas;
    }

    public void setTotalSaidas(BigDecimal totalSaidas) {
        this.totalSaidas = totalSaidas;
    }

    public BigDecimal getSaldoOperacional() {
        return saldoOperacional;
    }

    public void setSaldoOperacional(BigDecimal saldoOperacional) {
        this.saldoOperacional = saldoOperacional;
    }

    public BigDecimal getSaldoFinalCaixaBanco() {
        return saldoFinalCaixaBanco;
    }

    public void setSaldoFinalCaixaBanco(BigDecimal saldoFinalCaixaBanco) {
        this.saldoFinalCaixaBanco = saldoFinalCaixaBanco;
    }

    public List<RubricaAgrupadaDto> getEntradasAgrupadas() {
        return entradasAgrupadas;
    }

    public void setEntradasAgrupadas(List<RubricaAgrupadaDto> entradasAgrupadas) {
        this.entradasAgrupadas = entradasAgrupadas;
    }

    public List<RubricaAgrupadaDto> getSaidasAgrupadas() {
        return saidasAgrupadas;
    }

    public void setSaidasAgrupadas(List<RubricaAgrupadaDto> saidasAgrupadas) {
        this.saidasAgrupadas = saidasAgrupadas;
    }

    public static class RubricaAgrupadaDto {
        private TipoRubrica tipoRubrica;
        private List<RubricaDetalheDto> rubricasDetalhe;
        private BigDecimal totalPorTipo;

        public RubricaAgrupadaDto() {
        }

        public RubricaAgrupadaDto(TipoRubrica tipoRubrica, List<RubricaDetalheDto> rubricasDetalhe, BigDecimal totalPorTipo) {
            this.tipoRubrica = tipoRubrica;
            this.rubricasDetalhe = rubricasDetalhe;
            this.totalPorTipo = totalPorTipo;
        }

        public TipoRubrica getTipoRubrica() {
            return tipoRubrica;
        }

        public void setTipoRubrica(TipoRubrica tipoRubrica) {
            this.tipoRubrica = tipoRubrica;
        }

        public List<RubricaDetalheDto> getRubricasDetalhe() {
            return rubricasDetalhe;
        }

        public void setRubricasDetalhe(List<RubricaDetalheDto> rubricasDetalhe) {
            this.rubricasDetalhe = rubricasDetalhe;
        }

        public BigDecimal getTotalPorTipo() {
            return totalPorTipo;
        }

        public void setTotalPorTipo(BigDecimal totalPorTipo) {
            this.totalPorTipo = totalPorTipo;
        }
    }

    public static class RubricaDetalheDto {
        private String nomeRubrica;
        private BigDecimal valor;

        public RubricaDetalheDto() {
        }

        public RubricaDetalheDto(String nomeRubrica, BigDecimal valor) {
            this.nomeRubrica = nomeRubrica;
            this.valor = valor;
        }

        public String getNomeRubrica() {
            return nomeRubrica;
        }

        public void setNomeRubrica(String nomeRubrica) {
            this.nomeRubrica = nomeRubrica;
        }

        public BigDecimal getValor() {
            return valor;
        }

        public void setValor(BigDecimal valor) {
            this.valor = valor;
        }
    }
}

