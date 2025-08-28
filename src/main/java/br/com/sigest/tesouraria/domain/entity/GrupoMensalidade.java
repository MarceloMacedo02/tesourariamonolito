package br.com.sigest.tesouraria.domain.entity;

import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade que representa um Grupo de Mensalidade.
 */
@Entity
@Table(name = "grupo_mensalidade")
@Data
@NoArgsConstructor
public class GrupoMensalidade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private Float valor;

    @OneToMany(mappedBy = "grupoMensalidade")
    private Set<GrupoMensalidadeRubrica> rubricas;
}