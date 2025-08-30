package br.com.sigest.tesouraria.domain.enums;

public enum TipoCobranca {
    MENSALIDADE("Mensalidade"),
    OUTRAS_RUBRICAS("Outras Rubricas");

    private final String descricao;

    TipoCobranca(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}