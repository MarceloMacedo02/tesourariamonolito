package br.com.sigest.tesouraria.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.sigest.tesouraria.domain.entity.Rubrica;
import br.com.sigest.tesouraria.domain.enums.TipoRubrica;

@Repository
public interface RubricaRepository extends JpaRepository<Rubrica, Long> {
    Optional<Rubrica> findByNome(String nome);

    List<Rubrica> findByTipo(TipoRubrica tipo);
}