package br.com.sigest.tesouraria.domain.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column(name = "mes", nullable = false)
    private Integer mes;

    @Column(name = "ano", nullable = false)
    private Integer ano;

    @Column(name = "saldo_anterior", nullable = false)
    private BigDecimal saldoAnterior = BigDecimal.ZERO;

    @Column(name = "saldo_atual", nullable = false)
    private BigDecimal saldoAtual = BigDecimal.ZERO;

    // Novos campos adicionados para atender às necessidades dos serviços
    @Column(name = "receitas")
    private BigDecimal receitas = BigDecimal.ZERO;

    @Column(name = "despesas")
    private BigDecimal despesas = BigDecimal.ZERO;

    @Column(name = "saldo")
    private BigDecimal saldo = BigDecimal.ZERO;

    // Getters e Setters para os novos campos
    public BigDecimal getReceitas() {
        return receitas;
    }

    public void setReceitas(BigDecimal receitas) {
        this.receitas = receitas;
    }

    public BigDecimal getDespesas() {
        return despesas;
    }

    public void setDespesas(BigDecimal despesas) {
        this.despesas = despesas;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }
}
