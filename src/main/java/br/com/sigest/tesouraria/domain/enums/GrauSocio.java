package br.com.sigest.tesouraria.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GrauSocio {
    QM(1, "Quadro de Mestre"),
    CDC(2, "Corpo do Conselho"),
    CI(3, "Corpo Instrutivo"),
    QS(4, "Quadro de Sócio");

    private final int codigo;
    private final String descricao;

    public static Integer fromCodigo(String descricao) {
        if (descricao == null) {
            return null;
        }

        for (GrauSocio grau : GrauSocio.values()) {
            if (grau.getDescricao().equals(descricao)) {
                return grau.getCodigo();
            }
        }
        throw new IllegalArgumentException("Grau de sócio inválido: " + descricao);
    }

    public static String fromDescricao(int codigo) {
        if (codigo <= 0) {
            return null;
        }

        for (GrauSocio grau : GrauSocio.values()) {
            if (grau.getCodigo() == codigo) {
                return grau.getDescricao();
            }
        }
        throw new IllegalArgumentException("Grau de sócio inválido: " + codigo);

    }

}