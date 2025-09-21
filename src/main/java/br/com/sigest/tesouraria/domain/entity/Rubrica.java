package br.com.sigest.tesouraria.domain.entity;

import java.math.BigDecimal;

import br.com.sigest.tesouraria.domain.enums.TipoRubrica;
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
 * Entidade que representa uma Rubrica Financeira.
 */
@Entity
@Table(name = "rubricas")
@Data
@NoArgsConstructor
public class Rubrica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoRubrica tipo;

    @Column(nullable = false)
    // Usando BigDecimal para evitar problemas de precisão com valores monetários
    private BigDecimal valorPadrao = BigDecimal.ZERO;

    @ManyToOne(optional = false)
    @JoinColumn(name = "grupo_rubrica_id")
    private GrupoRubrica grupoRubrica;

}