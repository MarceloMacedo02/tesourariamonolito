package br.com.sigest.tesouraria.domain.entity;
import br.com.sigest.tesouraria.domain.enums.StatusContaPagar;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
@Entity @Table(name = "contas_a_pagar") @Data
public class ContaPagar {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private String descricao;
    @Column(nullable = false) private BigDecimal valor;
    @Column(nullable = false) private LocalDate dataVencimento;
    private LocalDate dataPagamento;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private StatusContaPagar status;
    @ManyToOne(optional = false) @JoinColumn(name = "fornecedor_id") private Fornecedor fornecedor;
    @ManyToOne(optional = false) @JoinColumn(name = "rubrica_id") private Rubrica rubrica;
}