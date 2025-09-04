package br.com.sigest.tesouraria.domain.enums;

public enum TipoTransacao {
    CREDITO("Crédito"), DEBITO("Débito");

    private final String descricao;

    TipoTransacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
