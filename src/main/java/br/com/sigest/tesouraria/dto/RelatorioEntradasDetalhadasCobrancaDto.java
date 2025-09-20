package br.com.sigest.tesouraria.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import br.com.sigest.tesouraria.domain.enums.StatusCobranca;
import lombok.Data;

@Data
public class RelatorioEntradasDetalhadasCobrancaDto {

    private Long cobrancaId;
    private BigDecimal valor;
    private LocalDate dataVencimento;
    private LocalDate dataPagamento;
    private StatusCobranca status;
    private List<MovimentoDto> movimentos;

}
