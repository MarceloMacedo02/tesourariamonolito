package br.com.sigest.tesouraria.repository;
import br.com.sigest.tesouraria.domain.entity.GrupoMensalidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GrupoMensalidadeRepository extends JpaRepository<GrupoMensalidade, Long> {
    Optional<GrupoMensalidade> findByNome(String nome);
}