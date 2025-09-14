package br.com.sigest.tesouraria.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.sigest.tesouraria.domain.entity.Instituicao;

@Repository
public interface InstituicaoRepository extends JpaRepository<Instituicao, Long> {

    Optional<Instituicao> findByFixedId(long l);
}