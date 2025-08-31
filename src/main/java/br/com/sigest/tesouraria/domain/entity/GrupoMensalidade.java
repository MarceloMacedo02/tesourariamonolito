package br.com.sigest.tesouraria.domain.entity;

import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

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

    @OneToMany(mappedBy = "grupoMensalidade", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<GrupoMensalidadeRubrica> rubricas;

    public Float getValor() {
        return (float) rubricas.stream().mapToDouble(r -> r.getValor()).sum();
    }
}