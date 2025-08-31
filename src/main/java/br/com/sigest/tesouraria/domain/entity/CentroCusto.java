package br.com.sigest.tesouraria.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade que representa um Centro de Custo.
 */
@Entity
@Table(name = "centro_custo")
@Data
@NoArgsConstructor
public class CentroCusto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

        @Column(nullable = false)
    private boolean ativo = true;

    // Novos campos para o relatório de centro de custos - persistentes
        @Column(nullable = false, columnDefinition = "double default 0.0")
    private Double entradas = 0.0;

    @Column(nullable = false, columnDefinition = "double default 0.0")
    private Double saidas = 0.0;


    // Saldo calculado dinamicamente
    public Double getSaldo() {
        return entradas - saidas;
    }
}