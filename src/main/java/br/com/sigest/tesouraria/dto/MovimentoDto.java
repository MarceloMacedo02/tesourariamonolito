package br.com.sigest.tesouraria.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import br.com.sigest.tesouraria.domain.enums.TipoMovimento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class MovimentoDto {

    private Long id;

    @NotNull(message = "O tipo de movimento é obrigatório.")
    private TipoMovimento tipo;

    @NotNull(message = "O valor é obrigatório.")
    @Positive(message = "O valor deve ser positivo.")
    // Usando BigDecimal para evitar problemas de precisão com valores monetários
    private BigDecimal valor;

    @NotNull(message = "A conta financeira é obrigatória.")
    private Long contaFinanceiraId;

    @NotNull(message = "A rubrica é obrigatória.")
    private Long rubricaId;

    @NotBlank(message = "A descrição é obrigatória.")
    private String origemDestino;

    @NotNull(message = "A data é obrigatória.")
    private LocalDate data;

    private Long fornecedorId;

}
