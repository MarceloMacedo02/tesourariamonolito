package br.com.sigest.tesouraria.dto;

import br.com.sigest.tesouraria.domain.entity.Cobranca; // Import Cobranca
import br.com.sigest.tesouraria.domain.enums.Lancado;
import br.com.sigest.tesouraria.domain.enums.TipoTransacao;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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
    private boolean manualSelectionNeeded;
    private List<SocioDto> socios;
    private List<FornecedorDto> fornecedores;
    private SocioDto socio; // Added
    private FornecedorDto fornecedor; // Added
    private List<Cobranca> cobrancasPendentes; // Added
}