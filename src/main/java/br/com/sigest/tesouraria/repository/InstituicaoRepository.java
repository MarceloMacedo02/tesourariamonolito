package br.com.sigest.tesouraria.repository;

import br.com.sigest.tesouraria.domain.entity.Instituicao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InstituicaoRepository extends JpaRepository<Instituicao, Long> {

    Optional<Instituicao> findByFixedId(Long fixedId);
}
