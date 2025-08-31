package br.com.sigest.tesouraria.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reconciliacao_mensal")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReconciliacaoMensal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int mes;

    @Column(nullable = false)
    private int ano;

    @ManyToOne
    @JoinColumn(name = "conta_financeira_id", nullable = false)
    private ContaFinanceira contaFinanceira;

    @Column(nullable = false)
    private BigDecimal totalEntradas;

    @Column(nullable = false)
    private BigDecimal totalSaidas;

    @Column(nullable = false)
    private BigDecimal saldoFinal;

    @Column(nullable = false)
    private LocalDateTime dataReconciliacao;

    @Column(length = 500)
    private String observacoes;

}
