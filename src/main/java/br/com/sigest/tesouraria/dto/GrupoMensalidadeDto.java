package br.com.sigest.tesouraria.dto;

import java.util.List;

import lombok.Data;

@Data
public class GrupoMensalidadeDto {
    private Long id;
    private String nome;
    private List<GrupoMensalidadeRubricaDto> rubricas;
    private Float valor;

    public Float getValor() {
        return valor;
    }

    public void setValor(Float valor) {
        this.valor = valor;
    }
}
