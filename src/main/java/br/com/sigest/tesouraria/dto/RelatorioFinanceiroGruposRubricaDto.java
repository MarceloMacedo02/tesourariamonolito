package br.com.sigest.tesouraria.dto;

import java.math.BigDecimal;
import java.util.List;

public class RelatorioFinanceiroGruposRubricaDto {
    private Integer mes;
    private Integer ano;
    private BigDecimal totalEntradas;
    private BigDecimal totalSaidas;
    private BigDecimal saldoOperacional;
    private List<GrupoRubricaDto> gruposRubricaAgrupados;

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

    public List<GrupoRubricaDto> getGruposRubricaAgrupados() {
        return gruposRubricaAgrupados;
    }

    public void setGruposRubricaAgrupados(List<GrupoRubricaDto> gruposRubricaAgrupados) {
        this.gruposRubricaAgrupados = gruposRubricaAgrupados;
    }

    public static class GrupoRubricaDto {
        private Long idGrupoRubrica;
        private String nomeGrupoRubrica;
        private BigDecimal totalEntradas;
        private BigDecimal totalSaidas;
        private BigDecimal saldo;
        private List<MovimentoDto> movimentos;

        // Getters e Setters
        public Long getIdGrupoRubrica() {
            return idGrupoRubrica;
        }

        public void setIdGrupoRubrica(Long idGrupoRubrica) {
            this.idGrupoRubrica = idGrupoRubrica;
        }

        public String getNomeGrupoRubrica() {
            return nomeGrupoRubrica;
        }

        public void setNomeGrupoRubrica(String nomeGrupoRubrica) {
            this.nomeGrupoRubrica = nomeGrupoRubrica;
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

        public BigDecimal getSaldo() {
            return saldo;
        }

        public void setSaldo(BigDecimal saldo) {
            this.saldo = saldo;
        }

        public List<MovimentoDto> getMovimentos() {
            return movimentos;
        }

        public void setMovimentos(List<MovimentoDto> movimentos) {
            this.movimentos = movimentos;
        }
    }

    public static class MovimentoDto {
        private Long id;
        private String descricao;
        private br.com.sigest.tesouraria.domain.enums.TipoRubrica tipoRubrica;
        private String nomeRubrica;
        private String origemDestino;
        private BigDecimal valor;
        private java.time.LocalDateTime data;

        // Getters e Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }

        public br.com.sigest.tesouraria.domain.enums.TipoRubrica getTipoRubrica() {
            return tipoRubrica;
        }

        public void setTipoRubrica(br.com.sigest.tesouraria.domain.enums.TipoRubrica tipoRubrica) {
            this.tipoRubrica = tipoRubrica;
        }

        public String getNomeRubrica() {
            return nomeRubrica;
        }

        public void setNomeRubrica(String nomeRubrica) {
            this.nomeRubrica = nomeRubrica;
        }

        public String getOrigemDestino() {
            return origemDestino;
        }

        public void setOrigemDestino(String origemDestino) {
            this.origemDestino = origemDestino;
        }

        public BigDecimal getValor() {
            return valor;
        }

        public void setValor(BigDecimal valor) {
            this.valor = valor;
        }

        public java.time.LocalDateTime getData() {
            return data;
        }

        public void setData(java.time.LocalDateTime data) {
            this.data = data;
        }
    }
}