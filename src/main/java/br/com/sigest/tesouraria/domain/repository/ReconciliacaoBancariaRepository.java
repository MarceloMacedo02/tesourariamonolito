package br.com.sigest.tesouraria.domain.repository;

import br.com.sigest.tesouraria.domain.entity.ReconciliacaoBancaria;
import br.com.sigest.tesouraria.domain.entity.ContaFinanceira;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReconciliacaoBancariaRepository extends JpaRepository<ReconciliacaoBancaria, Long> {
    @Query("SELECT rb FROM ReconciliacaoBancaria rb WHERE rb.contaFinanceira = ?1 AND rb.mes = ?2 AND rb.ano = ?3")
    List<ReconciliacaoBancaria> findByContaFinanceiraAndMesAndAno(ContaFinanceira contaFinanceira, Integer mes, Integer ano);
}