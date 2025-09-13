package br.com.sigest.tesouraria.dto;

import br.com.sigest.tesouraria.domain.entity.Cobranca; // Import Cobranca
import br.com.sigest.tesouraria.domain.enums.Lancado;
import br.com.sigest.tesouraria.domain.enums.TipoTransacao;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import br.com.sigest.tesouraria.domain.enums.TipoRelacionamento;

@Getter
@Setter
public class TransacaoDto {
    private Long id;
    private LocalDate data;
    private TipoTransacao tipo;
    private BigDecimal valor;
    private String fornecedorOuSocio;
    private String documento;
    private String descricao;
    private Lancado lancado;

    private TipoRelacionamento tipoRelacionamento;
    private Long relacionadoId;

    private boolean manualSelectionNeeded;
    private List<SocioDto> sociosSugeridos;
    private List<FornecedorDto> fornecedoresSugeridos;
    private List<Cobranca> cobrancasPendentes; // Added
    private Long fornecedorId;
}