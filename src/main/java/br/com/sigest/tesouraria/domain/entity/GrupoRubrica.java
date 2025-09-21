package br.com.sigest.tesouraria.domain.entity;

import java.math.BigDecimal;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade que representa um Grupo de Rubrica.
 */
@Entity
@Table(name = "grupo_rubrica")
@Data
@NoArgsConstructor
public class GrupoRubrica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(nullable = false)
    private boolean ativo = true;

    // Novos campos para o relatório de centro de custos - persistentes
    @Column(nullable = false, columnDefinition = "numeric(38,2)")
    @ColumnDefault("0.00")
    // Usando BigDecimal para evitar problemas de precisão com valores monetários
    private BigDecimal entradas = BigDecimal.ZERO;

    @Column(nullable = false, columnDefinition = "numeric(38,2)")
    @ColumnDefault("0.00")
    // Usando BigDecimal para evitar problemas de precisão com valores monetários
    private BigDecimal saidas = BigDecimal.ZERO;

    // Saldo calculado dinamicamente
    public BigDecimal getSaldo() {
        return entradas.subtract(saidas);
    }
}