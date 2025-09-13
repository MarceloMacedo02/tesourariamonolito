package br.com.sigest.tesouraria.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class ContaReceberDto {

    private Long id;

    @NotBlank(message = "O nome do pagador é obrigatório.")
    private String pagador;

    @NotBlank(message = "A descrição é obrigatória.")
    private String descricao;

    @NotNull(message = "O valor é obrigatório.")
    @Positive(message = "O valor deve ser positivo.")
    private Float valor;

    @NotNull(message = "A data de vencimento é obrigatória.")
    private LocalDate dataVencimento;

    @NotNull(message = "A rubrica é obrigatória.")
    private Long rubricaId;

    private Long socioId;

}
