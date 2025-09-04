package br.com.sigest.tesouraria.dto;

import br.com.sigest.tesouraria.domain.enums.Lancado;
import br.com.sigest.tesouraria.domain.enums.TipoTransacao;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransacaoDto {
    private Long id;
    private LocalDate data;
    private TipoTransacao tipo;
    private BigDecimal valor;
    private String fornecedorOuSocio;
    private String documento;
    private String descricao;
    private Lancado lancado;
}
