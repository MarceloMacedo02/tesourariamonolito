package br.com.sigest.tesouraria.domain.entity;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Locale;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat.Style;

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
    @org.springframework.format.annotation.NumberFormat(style = Style.NUMBER, pattern = "#,##0.00")
    private BigDecimal saldoInicial = BigDecimal.ZERO;

    @Column(name = "total_entradas")
    @org.springframework.format.annotation.NumberFormat(style = Style.NUMBER, pattern = "#,##0.00")
    private BigDecimal totalEntradas = BigDecimal.ZERO;

    @Column(name = "total_saidas")
    @org.springframework.format.annotation.NumberFormat(style = Style.NUMBER, pattern = "#,##0.00")
    private BigDecimal totalSaidas = BigDecimal.ZERO;

    @Column(name = "saldo_final")
    @org.springframework.format.annotation.NumberFormat(style = Style.NUMBER, pattern = "#,##0.00")
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

    /**
     * Define o saldo final.
     *
     * @param saldoFinal o saldo final a ser definido
     */
    public void setSaldoFinal(BigDecimal saldoFinal) {
        this.saldoFinal = saldoFinal;
    }

    /**
     * Calcula o resultado operacional (entradas - saídas).
     *
     * @return o resultado operacional calculado
     */
    public BigDecimal getResultadoOperacional() {
        BigDecimal entradas = this.totalEntradas != null ? this.totalEntradas : BigDecimal.ZERO;
        BigDecimal saidas = this.totalSaidas != null ? this.totalSaidas : BigDecimal.ZERO;
        return entradas.subtract(saidas);
    }
}
