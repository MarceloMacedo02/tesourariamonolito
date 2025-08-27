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

@Entity
@Table(name = "movimentos")
@Data
public class Movimento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    @Column(nullable = false)
    private LocalDateTime dataHora = LocalDateTime.now();

    @Column(nullable = false)
    private Float valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMovimento tipo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "conta_financeira_id")
    private ContaFinanceira contaFinanceira;

    @ManyToOne(optional = false)
    @JoinColumn(name = "centro_custo_id")
    private CentroCusto centroCusto;

    @ManyToOne(optional = false)
    @JoinColumn(name = "rubrica_id")
    private Rubrica rubrica;

    private String origemDestino;
}