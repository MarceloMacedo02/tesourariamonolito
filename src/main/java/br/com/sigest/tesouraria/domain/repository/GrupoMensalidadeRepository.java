package br.com.sigest.tesouraria.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.sigest.tesouraria.domain.entity.GrupoMensalidade;

public interface GrupoMensalidadeRepository extends JpaRepository<GrupoMensalidade, Long> {
    Optional<GrupoMensalidade> findByNome(String nome);
}