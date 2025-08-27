package br.com.sigest.tesouraria.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para a entidade Centro de Custo.
 * Usado para transferência de dados entre a camada de visão e o controller.
 */
@Data
@NoArgsConstructor
public class CentroCustoDto {

    private Long id;

    @NotBlank(message = "O nome do centro de custo é obrigatório.")
    @Size(max = 255, message = "O nome não pode exceder 255 caracteres.")
    private String nome;

    private boolean ativo = true;
}
