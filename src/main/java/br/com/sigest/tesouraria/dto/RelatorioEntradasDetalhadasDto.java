package br.com.sigest.tesouraria.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class RelatorioEntradasDetalhadasDto {
    private Integer mes;
    private Integer ano;
    private List<EntradaSocioDto> entradasPorSocio;
    private List<RubricaPagamentoDto> rubricasPagamento;
    private BigDecimal totalEntradas;

    // Getters e Setters
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

    public List<EntradaSocioDto> getEntradasPorSocio() {
        return entradasPorSocio;
    }

    public void setEntradasPorSocio(List<EntradaSocioDto> entradasPorSocio) {
        this.entradasPorSocio = entradasPorSocio;
    }

    public List<RubricaPagamentoDto> getRubricasPagamento() {
        return rubricasPagamento;
    }

    public void setRubricasPagamento(List<RubricaPagamentoDto> rubricasPagamento) {
        this.rubricasPagamento = rubricasPagamento;
    }

    public BigDecimal getTotalEntradas() {
        return totalEntradas;
    }

    public void setTotalEntradas(BigDecimal totalEntradas) {
        this.totalEntradas = totalEntradas;
    }

    public static class EntradaSocioDto {
        private String nomeSocio;
        private LocalDate dataCredito;
        private BigDecimal valor;
        private String rubrica;

        // Getters e Setters
        public String getNomeSocio() {
            return nomeSocio;
        }

        public void setNomeSocio(String nomeSocio) {
            this.nomeSocio = nomeSocio;
        }

        public LocalDate getDataCredito() {
            return dataCredito;
        }

        public void setDataCredito(LocalDate dataCredito) {
            this.dataCredito = dataCredito;
        }

        public BigDecimal getValor() {
            return valor;
        }

        public void setValor(BigDecimal valor) {
            this.valor = valor;
        }

        public String getRubrica() {
            return rubrica;
        }

        public void setRubrica(String rubrica) {
            this.rubrica = rubrica;
        }
    }

    public static class RubricaPagamentoDto {
        private String nomeRubrica;
        private BigDecimal valorTotal;
        private Long quantidade;

        // Getters e Setters
        public String getNomeRubrica() {
            return nomeRubrica;
        }

        public void setNomeRubrica(String nomeRubrica) {
            this.nomeRubrica = nomeRubrica;
        }

        public BigDecimal getValorTotal() {
            return valorTotal;
        }

        public void setValorTotal(BigDecimal valorTotal) {
            this.valorTotal = valorTotal;
        }

        public Long getQuantidade() {
            return quantidade;
        }

        public void setQuantidade(Long quantidade) {
            this.quantidade = quantidade;
        }
    }
}