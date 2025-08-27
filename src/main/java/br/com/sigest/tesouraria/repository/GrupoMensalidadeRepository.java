package br.com.sigest.tesouraria.repository;
import br.com.sigest.tesouraria.domain.entity.GrupoMensalidade;
import org.springframework.data.jpa.repository.JpaRepository;
public interface GrupoMensalidadeRepository extends JpaRepository<GrupoMensalidade, Long> {}