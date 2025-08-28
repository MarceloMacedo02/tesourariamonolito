package br.com.sigest.tesouraria.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.sigest.tesouraria.domain.entity.Rubrica;

@Repository
public interface RubricaRepository extends JpaRepository<Rubrica, Long> {
    Optional<Rubrica> findByNome(String nome);
}