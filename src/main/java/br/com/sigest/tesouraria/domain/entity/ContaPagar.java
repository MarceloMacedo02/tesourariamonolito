package br.com.sigest.tesouraria.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.com.sigest.tesouraria.domain.enums.StatusContaPagar;
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

@Entity
@Table(name = "contas_pagar")
@Data
@NoArgsConstructor
public class ContaPagar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false)
    // Usando BigDecimal para evitar problemas de precisão com valores monetários
    private BigDecimal valor;

    @Column(nullable = false)
    private LocalDate dataVencimento;

    private LocalDate dataPagamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusContaPagar status;

    @ManyToOne(optional = true) // Can be a payment without a registered supplier
    @JoinColumn(name = "fornecedor_id", nullable = true)
    private Fornecedor fornecedor;

    @ManyToOne(optional = false)
    @JoinColumn(name = "rubrica_id", nullable = false)
    private Rubrica rubrica;

}
