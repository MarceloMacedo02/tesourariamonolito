package br.com.sigest.tesouraria.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import br.com.sigest.tesouraria.domain.enums.TipoRubrica;

public class RelatorioDemonstrativoFinanceiroPorGrupoRubricaDto extends RelatorioDemonstrativoFinanceiroDto {
    
    private List<GrupoRubricaAgrupadoDto> gruposRubricaAgrupados;

    public List<GrupoRubricaAgrupadoDto> getGruposRubricaAgrupados() {
        return gruposRubricaAgrupados;
    }

    public void setGruposRubricaAgrupados(List<GrupoRubricaAgrupadoDto> gruposRubricaAgrupados) {
        this.gruposRubricaAgrupados = gruposRubricaAgrupados;
    }

    public static class GrupoRubricaAgrupadoDto {
        private Long idGrupoRubrica;
        private String nomeGrupoRubrica;
        private BigDecimal totalEntradas;
        private BigDecimal totalSaidas;
        private BigDecimal saldo;
        private List<MovimentoDetalheDto> movimentos;

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

        public List<MovimentoDetalheDto> getMovimentos() {
            return movimentos;
        }

        public void setMovimentos(List<MovimentoDetalheDto> movimentos) {
            this.movimentos = movimentos;
        }
    }

    public static class MovimentoDetalheDto {
        private Long idMovimento;
        private LocalDateTime data;
        private String descricao;
        private TipoRubrica tipoRubrica;
        private String nomeRubrica;
        private BigDecimal valor;
        private String origemDestino;

        public Long getIdMovimento() {
            return idMovimento;
        }

        public void setIdMovimento(Long idMovimento) {
            this.idMovimento = idMovimento;
        }

        public LocalDateTime getData() {
            return data;
        }

        public void setData(LocalDateTime data) {
            this.data = data;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }

        public TipoRubrica getTipoRubrica() {
            return tipoRubrica;
        }

        public void setTipoRubrica(TipoRubrica tipoRubrica) {
            this.tipoRubrica = tipoRubrica;
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

        public String getOrigemDestino() {
            return origemDestino;
        }

        public void setOrigemDestino(String origemDestino) {
            this.origemDestino = origemDestino;
        }
    }
}