package br.com.sigest.tesouraria.domain.enums;

/**
 * Enumeração para os possíveis status de uma cobrança.
 */
public enum StatusCobranca {
    ABERTA("Aberto"),
    PAGA("Pago"),
    VENCIDA("Vencido"),
    CANCELADA ("Cancelado");

    private final String descricao;

    StatusCobranca(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}