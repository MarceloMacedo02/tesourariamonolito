package br.com.sigest.tesouraria.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import br.com.sigest.tesouraria.validation.ValidPagador;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@ValidPagador
public class ContaReceberDto {

    private Long id;

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

    // É crucial que o socioId seja fornecido no DTO quando uma conta a receber estiver
    // logicamente associada a um sócio, para garantir que o vínculo seja estabelecido corretamente no backend.
    private Long socioId;

    public Long getSocioId() {
        return socioId;
    }

    public String getPagador() {
        return pagador;
    }

}
