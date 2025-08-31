package br.com.sigest.tesouraria.domain.enums;

public enum StatusContaPagar {
    ABERTA("Aberta"),
    PAGA("Paga"),
    CANCELADA("Cancelada");

    private final String descricao;

    StatusContaPagar(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
