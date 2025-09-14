package br.com.sigest.tesouraria.domain.repository;

import java.math.BigDecimal;

import br.com.sigest.tesouraria.domain.entity.ContaFinanceira;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ContaFinanceiraRepository extends JpaRepository<ContaFinanceira, Long> {
    @Query("SELECT SUM(cf.saldoAtual) FROM ContaFinanceira cf")
    BigDecimal sumTotalSaldo();
}