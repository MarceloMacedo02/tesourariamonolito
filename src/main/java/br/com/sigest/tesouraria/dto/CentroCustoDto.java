package br.com.sigest.tesouraria.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO para dados de Centro de Custo.
 */
@Getter
@Setter
public class CentroCustoDto {
    private Long id;
    private String nome;
    private boolean ativo;
}