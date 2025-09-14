package br.com.sigest.tesouraria.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.sigest.tesouraria.domain.entity.GrupoFinanceiro;

public interface GrupoFinanceiroRepository extends JpaRepository<GrupoFinanceiro, Long> {
    Optional<GrupoFinanceiro> findByNome(String nome);
}
