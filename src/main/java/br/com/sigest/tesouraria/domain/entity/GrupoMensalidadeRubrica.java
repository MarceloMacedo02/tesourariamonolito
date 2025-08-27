package br.com.sigest.tesouraria.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "grupo_mensalidade_rubrica")
@Data
public class GrupoMensalidadeRubrica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grupo_mensalidade_id", nullable = false)
    private GrupoMensalidade grupoMensalidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rubrica_id", nullable = false)
    private Rubrica rubrica;

    @Column(nullable = false)
    private Float valor = 0.0F;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        GrupoMensalidadeRubrica that = (GrupoMensalidadeRubrica) o;
        // Considera iguais se grupoMensalidade e rubrica forem iguais
        return (grupoMensalidade != null && rubrica != null &&
                grupoMensalidade.equals(that.grupoMensalidade) &&
                rubrica.equals(that.rubrica));
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (grupoMensalidade != null ? grupoMensalidade.hashCode() : 0);
        result = 31 * result + (rubrica != null ? rubrica.hashCode() : 0);
        return result;
    }
}
