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
    private String codigo;

    @Column(nullable = false)
    private String nome;

    @Column(length = 500)
    private String descricao;
}