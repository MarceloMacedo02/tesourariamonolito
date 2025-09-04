package br.com.sigest.tesouraria.domain.enums;

public enum Lancado {
    LANCADO("Lançado"), NAOLANCADO("Não Lançado");

    private final String descricao;

    Lancado(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
