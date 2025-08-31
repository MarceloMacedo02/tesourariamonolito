package br.com.sigest.tesouraria.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import br.com.sigest.tesouraria.domain.enums.TipoMovimento;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExtratoFiltroDto {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataInicio;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataFim;

    private Long contaFinanceiraId;

    private TipoMovimento tipoMovimento;

    private Long rubricaId;

}
