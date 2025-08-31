package br.com.sigest.tesouraria.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ContaPagarDto {

    private Long id;

    @NotBlank(message = "A descrição é obrigatória.")
    private String descricao;

    @NotNull(message = "O valor é obrigatório.")
    @Positive(message = "O valor deve ser positivo.")
    private Float valor;

    @NotNull(message = "A data de vencimento é obrigatória.")
    private LocalDate dataVencimento;

    private Long fornecedorId; // Opcional

    @NotNull(message = "A rubrica é obrigatória.")
    private Long rubricaId;

}
