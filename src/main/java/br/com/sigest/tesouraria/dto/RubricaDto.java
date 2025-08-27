package br.com.sigest.tesouraria.dto;

import br.com.sigest.tesouraria.domain.enums.TipoRubrica;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RubricaDto {
    private Long id;

    @NotBlank(message = "O nome da rubrica é obrigatório.")
    private String nome;

    @NotNull(message = "O tipo da rubrica é obrigatório.")
    private TipoRubrica tipo;

    @NotNull(message = "O centro de custo é obrigatório.")
    private Long centroCustoId;

    @NotNull(message = "O valor padrão é obrigatório.")
    private Float valorPadrao = 0.0F;

}