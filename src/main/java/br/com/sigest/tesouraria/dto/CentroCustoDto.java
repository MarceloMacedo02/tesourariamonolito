package br.com.sigest.tesouraria.dto;

import lombok.Data;

/**
 * DTO para dados de Centro de Custo.
 */
@Data
public class CentroCustoDto {
    private Long id;
    private String nome;
    private boolean ativo;
}