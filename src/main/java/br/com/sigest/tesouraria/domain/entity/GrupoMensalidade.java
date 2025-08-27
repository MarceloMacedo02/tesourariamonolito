package br.com.sigest.tesouraria.domain.entity;

import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "grupos_mensalidade")
@Data
public class GrupoMensalidade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @OneToMany(mappedBy = "grupoMensalidade", cascade = { CascadeType.PERSIST,
            CascadeType.MERGE }, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<GrupoMensalidadeRubrica> rubricas;

    public Float getValor() {
        if (rubricas == null)
            return 0.0F;
        return rubricas.stream()
                .map(GrupoMensalidadeRubrica::getValor)
                .reduce(0.0F, Float::sum);
    }
}