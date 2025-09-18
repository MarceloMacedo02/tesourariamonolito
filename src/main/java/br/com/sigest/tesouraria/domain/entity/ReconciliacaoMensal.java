package br.com.sigest.tesouraria.domain.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Integer mes;

    @Column(nullable = false)
    private Integer ano;

    // Campos para a reconciliação mensal conforme o exemplo:
    // Saldo inicial: R$ 10.457,22
    // Entradas: R$ 15.615,00
    // Saídas: R$ 10.330,53
    // Saldo final: R$ 15.741,69

    @Column(name = "saldo_inicial")
    private BigDecimal saldoInicial = BigDecimal.ZERO;

    @Column(name = "total_entradas")
    private BigDecimal totalEntradas = BigDecimal.ZERO;

    @Column(name = "total_saidas")
    private BigDecimal totalSaidas = BigDecimal.ZERO;

    @Column(name = "saldo_final")
    private BigDecimal saldoFinal = BigDecimal.ZERO;

    // Método para calcular o saldo final com base nos outros valores
    public BigDecimal getSaldoFinal() {
        BigDecimal saldoInicial = this.saldoInicial != null ? this.saldoInicial : BigDecimal.ZERO;
        BigDecimal entradas = this.totalEntradas != null ? this.totalEntradas : BigDecimal.ZERO;
        BigDecimal saidas = this.totalSaidas != null ? this.totalSaidas : BigDecimal.ZERO;
        return saldoInicial.add(entradas).subtract(saidas);
    }

    // Método para definir o saldo final
    public void setSaldoFinal(BigDecimal saldoFinal) {
        this.saldoFinal = saldoFinal;
    }

    // Método para calcular o resultado operacional (entradas - saídas)
    public BigDecimal getResultadoOperacional() {
        BigDecimal entradas = this.totalEntradas != null ? this.totalEntradas : BigDecimal.ZERO;
        BigDecimal saidas = this.totalSaidas != null ? this.totalSaidas : BigDecimal.ZERO;
        return entradas.subtract(saidas);
    }
}
