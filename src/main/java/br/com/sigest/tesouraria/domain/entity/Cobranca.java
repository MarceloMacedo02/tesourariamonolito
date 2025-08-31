package br.com.sigest.tesouraria.domain.entity;

import java.time.LocalDate;
import br.com.sigest.tesouraria.domain.enums.StatusCobranca;
import br.com.sigest.tesouraria.domain.enums.TipoCobranca;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade que representa uma Cobrança.
 */
@Entity
@Table(name = "cobrancas")
@Data
@NoArgsConstructor
public class Cobranca {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Float valor;

    @ManyToOne
    @JoinColumn(name = "rubrica_id")
    private Rubrica rubrica;

    private String descricao;

    @Column(nullable = false)
    private LocalDate dataVencimento;

    private LocalDate dataPagamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusCobranca status; 

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cobranca", nullable = false)
    private TipoCobranca tipoCobranca;

    @ManyToOne(optional = true)
    @JoinColumn(name = "socio_id", nullable = true)
    private Socio socio;

    private String pagador; // Nome do pagador quando não for sócio

    @ManyToOne
    @JoinColumn(name = "grupo_mensalidade_id")
    private GrupoMensalidade grupoMensalidade;
}