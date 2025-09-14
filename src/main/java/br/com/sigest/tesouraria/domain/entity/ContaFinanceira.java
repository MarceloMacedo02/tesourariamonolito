package br.com.sigest.tesouraria.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * Entidade que representa uma Conta Financeira.
 */
@Entity
@Table(name = "conta_financeira")
@Data
@NoArgsConstructor
public class ContaFinanceira {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(nullable = false)
    private BigDecimal saldoAtual;
}