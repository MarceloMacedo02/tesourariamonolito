package br.com.sigest.tesouraria.domain.repository;

import br.com.sigest.tesouraria.domain.entity.ReconciliacaoBancaria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReconciliacaoBancariaRepository extends JpaRepository<ReconciliacaoBancaria, Long> {
}