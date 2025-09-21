package br.com.sigest.tesouraria.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import br.com.sigest.tesouraria.domain.enums.TipoMovimento;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class ExtratoFiltroDto {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataInicio;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataFim;

    private Long contaFinanceiraId;

    private TipoMovimento tipoMovimento;

    private Long rubricaId;

    private Long grupoFinanceiroId;

}
