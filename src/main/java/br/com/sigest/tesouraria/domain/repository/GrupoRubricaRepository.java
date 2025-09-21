package br.com.sigest.tesouraria.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.sigest.tesouraria.domain.entity.GrupoRubrica;

/**
 * Repositório para a entidade GrupoRubrica.
 */
public interface GrupoRubricaRepository extends JpaRepository<GrupoRubrica, Long> {
    Optional<GrupoRubrica> findByNome(String nome);
}