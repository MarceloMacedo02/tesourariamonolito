package br.com.sigest.tesouraria.domain.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reconciliacao_mensal")
@Data
@NoArgsConstructor
public class ReconciliacaoMensal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer mes;

    @Column(nullable = false)
    private Integer ano;

    @Column(name = "saldo_inicial")
    @org.springframework.format.annotation.NumberFormat(style = org.springframework.format.annotation.NumberFormat.Style.CURRENCY)
    private BigDecimal saldoInicial = BigDecimal.ZERO;

    @Column(name = "total_entradas")
    @org.springframework.format.annotation.NumberFormat(style = org.springframework.format.annotation.NumberFormat.Style.CURRENCY)
    private BigDecimal totalEntradas = BigDecimal.ZERO;

    @Column(name = "total_saidas")
    @org.springframework.format.annotation.NumberFormat(style = org.springframework.format.annotation.NumberFormat.Style.CURRENCY)
    private BigDecimal totalSaidas = BigDecimal.ZERO;

    @Column(name = "saldo_final")
    @org.springframework.format.annotation.NumberFormat(style = org.springframework.format.annotation.NumberFormat.Style.CURRENCY)
    private BigDecimal saldoFinal = BigDecimal.ZERO;

    /**
     * Calcula o saldo final com base no saldo inicial, entradas e saídas.
     *
     * @return o saldo final calculado
     */
    public BigDecimal getSaldoFinal() {
        BigDecimal saldoInicial = this.saldoInicial != null ? this.saldoInicial : BigDecimal.ZERO;
        BigDecimal entradas = this.totalEntradas != null ? this.totalEntradas : BigDecimal.ZERO;
        BigDecimal saidas = this.totalSaidas != null ? this.totalSaidas : BigDecimal.ZERO;
        return saldoInicial.add(entradas).subtract(saidas);
    }
}
