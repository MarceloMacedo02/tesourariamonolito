package br.com.sigest.tesouraria.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO para dados de Grupo de Rubrica.
 */
@Getter
@Setter
public class GrupoRubricaDto {
    private Long id;
    private String nome;
    private boolean ativo;

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public boolean isAtivo() {
        return ativo;
    }
}