package br.com.sigest.tesouraria.repository;

import br.com.sigest.tesouraria.domain.entity.Rubrica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RubricaRepository extends JpaRepository<Rubrica, Long> {
}