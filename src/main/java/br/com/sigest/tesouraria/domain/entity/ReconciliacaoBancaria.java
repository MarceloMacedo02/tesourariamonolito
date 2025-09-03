package br.com.sigest.tesouraria.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "reconciliacao_bancaria")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReconciliacaoBancaria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reconciliacao_mensal_id", nullable = false)
    private ReconciliacaoMensal reconciliacaoMensal;

    @ManyToOne
    @JoinColumn(name = "conta_financeira_id", nullable = false)
    private ContaFinanceira contaFinanceira;

    @Column(nullable = false)
    private BigDecimal saldo;
}
