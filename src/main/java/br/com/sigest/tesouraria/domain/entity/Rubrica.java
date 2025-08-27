package br.com.sigest.tesouraria.domain.entity;

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

@Entity
@Table(name = "rubricas")
@Data
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
    private Float valorPadrao = 0.0F;

    @ManyToOne(optional = false)
    @JoinColumn(name = "centro_custo_id")
    private CentroCusto centroCusto;
}