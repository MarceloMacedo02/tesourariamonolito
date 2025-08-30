package br.com.sigest.tesouraria.domain.enums;
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