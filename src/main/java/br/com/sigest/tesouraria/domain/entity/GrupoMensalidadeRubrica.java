package br.com.sigest.tesouraria.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Entidade que representa a relação entre GrupoMensalidade e Rubrica.
 */
@Entity
@Table(name = "grupo_mensalidade_rubrica")
@Data
public class GrupoMensalidadeRubrica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grupo_mensalidade_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private GrupoMensalidade grupoMensalidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rubrica_id", nullable = false)
    private Rubrica rubrica;

    @Column(nullable = false)
    // Usando BigDecimal para evitar problemas de precisão com valores monetários
    private BigDecimal valor = BigDecimal.ZERO;
}