package br.com.sigest.tesouraria.repository;

import br.com.sigest.tesouraria.domain.entity.ReconciliacaoBancaria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReconciliacaoBancariaRepository extends JpaRepository<ReconciliacaoBancaria, Long> {
}
