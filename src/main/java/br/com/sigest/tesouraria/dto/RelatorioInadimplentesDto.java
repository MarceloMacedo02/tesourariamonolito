package br.com.sigest.tesouraria.dto;

import java.math.BigDecimal;

import br.com.sigest.tesouraria.domain.enums.GrauSocio;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RelatorioInadimplentesDto {
    private String nomeSocio;
    private GrauSocio grauSocio;
    private BigDecimal valorIndividual;
    private BigDecimal valorTotalAberto;
    private Long mesesAberto;

    public RelatorioInadimplentesDto(String nomeSocio, String grauSocio, Double valorTotalAberto, Long mesesAberto) {
        this.nomeSocio = nomeSocio;
        this.grauSocio = GrauSocio.getByDescricao(grauSocio);
        if (valorTotalAberto != null) {
            this.valorTotalAberto = BigDecimal.valueOf(valorTotalAberto);
        }
        this.mesesAberto = mesesAberto;
    }

    public BigDecimal getValorTotalAberto() {
        return valorTotalAberto;
    }
}
