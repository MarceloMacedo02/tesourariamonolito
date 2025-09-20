package br.com.sigest.tesouraria.dto;

import java.time.LocalDate;
import java.util.List;

import br.com.sigest.tesouraria.domain.enums.StatusCobranca;
import br.com.sigest.tesouraria.domain.enums.TipoCobranca;
import br.com.sigest.tesouraria.domain.enums.TipoRelacionamento;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

/**
 * DTO para dados de entrada/saída de cobrança.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CobrancaDTO {
    private Long id;

    private String rubrica;
    private Long rubricaId;
    private String descricao;
    private TipoCobranca tipoCobranca;

    @NotNull(message = "O valor é obrigatório.")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero.")
    private Float valor;

    @NotNull(message = "A data de vencimento é obrigatória.")
    private LocalDate dataVencimento;

    private LocalDate dataPagamento;

    private StatusCobranca status;

    // Para criação/edição: É crucial que o socioId seja fornecido no DTO quando uma cobrança estiver
    // logicamente associada a um sócio, para garantir que o vínculo seja estabelecido corretamente no backend.
    private Long socioId;
    private List<Long> sociosIds; // Para cobrança manual coletiva
    private String pagador;

    // Para exibição em listas e filtros
    private String nomeSocio;
    private LocalDate inicio;
    private LocalDate fim;
    private LocalDate dataPagamentoInicio;
    private LocalDate dataPagamentoFim;

    private Long transacaoId;
    private Long fornecedorId;
    private TipoRelacionamento tipoRelacionamento;
}

