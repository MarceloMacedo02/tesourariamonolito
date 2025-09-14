package br.com.sigest.tesouraria.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GrupoMensalidadeDto {
    private Long id;
    private String nome;
    private List<GrupoMensalidadeRubricaDto> rubricas;
    private BigDecimal valor;

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
