package br.com.sigest.tesouraria.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO para dados de entrada/saída de cobrança.
 */
@Data
public class CobrancaDTO {
    private Long id;

    private String rubrica;
    private String descricao;
    private String tipo;

    @NotNull(message = "O valor é obrigatório.")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero.")
    private Float valor;

    @NotNull(message = "A data de vencimento é obrigatória.")
    private LocalDate dataVencimento;

    // Para cobrança manual individual
    private Long socioId;
    
    // Para cobrança manual coletiva
    private List<Long> sociosIds;

    // Para paginação e filtros
    private LocalDate inicio;
    private LocalDate fim;
    private String status;
    private String nomeSocio;
}