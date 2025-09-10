package br.com.sigest.tesouraria.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.com.sigest.tesouraria.domain.enums.Lancado;
import br.com.sigest.tesouraria.domain.enums.StatusIdentificacao;
import br.com.sigest.tesouraria.domain.enums.TipoTransacao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "transacoes")
@Data
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate data;

    @Enumerated(EnumType.STRING)
    private TipoTransacao tipo;

    private BigDecimal valor;

    private String fornecedorOuSocio;

    private String documento;

    private String descricao;

    @Enumerated(EnumType.STRING)
    private Lancado lancado = Lancado.NAOLANCADO;

    @Enumerated(EnumType.STRING)
    private br.com.sigest.tesouraria.domain.enums.TipoRelacionamento tipoRelacionamento;

    private Long relacionadoId;
    
    private Long fornecedorId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status_identificacao")
    private StatusIdentificacao statusIdentificacao;
}
