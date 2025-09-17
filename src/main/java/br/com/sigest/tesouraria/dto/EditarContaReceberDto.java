package br.com.sigest.tesouraria.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class EditarContaReceberDto {

    @NotBlank(message = "Descrição é obrigatória")
    @Size(max = 255, message = "Descrição deve ter no máximo 255 caracteres")
    private String descricao;

    @NotNull(message = "Data de vencimento é obrigatória")
    private LocalDate dataVencimento;

    @NotNull(message = "Valor é obrigatório")
    @Positive(message = "Valor deve ser maior que zero")
    private BigDecimal valor;

    @NotNull(message = "Rubrica é obrigatória")
    private Long rubricaId;

    // Constructors
    public EditarContaReceberDto() {
    }

    public EditarContaReceberDto(String descricao, LocalDate dataVencimento, BigDecimal valor, Long rubricaId) {
        this.descricao = descricao;
        this.dataVencimento = dataVencimento;
        this.valor = valor;
        this.rubricaId = rubricaId;
    }

    // Getters and Setters
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Long getRubricaId() {
        return rubricaId;
    }

    public void setRubricaId(Long rubricaId) {
        this.rubricaId = rubricaId;
    }
}