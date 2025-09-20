package br.com.sigest.tesouraria.dto;

import java.util.List;

import lombok.Data;

@Data
public class RelatorioEntradasDetalhadasSocioDto {

    private Long socioId;
    private String socioNome;
    private List<RelatorioEntradasDetalhadasCobrancaDto> cobrancas;

}
