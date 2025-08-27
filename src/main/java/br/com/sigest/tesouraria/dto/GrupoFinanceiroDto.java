package br.com.sigest.tesouraria.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GrupoFinanceiroDto {
    private Long id;

    @NotBlank(message = "O nome é obrigatório.")
    @Size(max = 255, message = "O nome não pode exceder 255 caracteres.")
    private String nome;

    @Size(max = 255, message = "A descrição não pode exceder 255 caracteres.")
    private String descricao;

    private boolean ativo = true;
}
