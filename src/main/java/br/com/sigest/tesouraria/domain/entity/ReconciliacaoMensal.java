package br.com.sigest.tesouraria.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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

    @OneToMany(mappedBy = "reconciliacaoMensal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReconciliacaoBancaria> reconciliacoesBancarias;

    @Column(nullable = false)
    private BigDecimal totalEntradas;

    @Column(nullable = false)
    private BigDecimal totalSaidas;

    @Column(nullable = false, columnDefinition = "numeric(38,2) default 0.00")
    private BigDecimal saldoSugerido;

    @Column(nullable = false)
    private BigDecimal saldoFinal;

    // Novos campos para a reconciliação mensal
    @Column(name = "saldo_mes_anterior", nullable = false)
    private BigDecimal saldoMesAnterior = BigDecimal.ZERO;

    @Column(name = "resultado_operacional", nullable = false)
    private BigDecimal resultadoOperacional = BigDecimal.ZERO;

    @Column(nullable = false)
    private LocalDateTime dataReconciliacao;

    @Column(length = 500)
    private String observacoes;

}
