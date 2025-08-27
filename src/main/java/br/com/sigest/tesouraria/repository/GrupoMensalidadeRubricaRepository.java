package br.com.sigest.tesouraria.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.sigest.tesouraria.domain.entity.GrupoMensalidadeRubrica;

public interface GrupoMensalidadeRubricaRepository extends JpaRepository<GrupoMensalidadeRubrica, Long> {
    List<GrupoMensalidadeRubrica> findByGrupoMensalidadeId(Long grupoMensalidadeId);
}
