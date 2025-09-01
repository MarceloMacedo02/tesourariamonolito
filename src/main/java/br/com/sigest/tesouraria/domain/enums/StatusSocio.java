package br.com.sigest.tesouraria.domain.enums;

public enum StatusSocio {
    FREQUENTE("Frequente"),
    INATIVO("Inativo"),
    AFASTADO("Afastado"),
    CANCELADO("Cancelado");

    private final String descricao;

    StatusSocio(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}