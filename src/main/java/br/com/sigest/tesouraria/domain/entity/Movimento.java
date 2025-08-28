package br.com.sigest.tesouraria.domain.entity;

import java.time.LocalDateTime;

import br.com.sigest.tesouraria.domain.enums.TipoMovimento;
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
 * Entidade que representa um Movimento Financeiro (entrada ou saída).
 */
@Entity
@Table(name = "movimentos")
@Data
@NoArgsConstructor
public class Movimento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMovimento tipo;

    @Column(nullable = false)
    private Float valor;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    private String origemDestino;

    @ManyToOne(optional = false)
    @JoinColumn(name = "conta_id")
    private ContaFinanceira contaFinanceira;

    @ManyToOne(optional = false)
    @JoinColumn(name = "rubrica_id")
    private Rubrica rubrica;

    @ManyToOne(optional = false)
    @JoinColumn(name = "centro_custo_id")
    private CentroCusto centroCusto;
}
