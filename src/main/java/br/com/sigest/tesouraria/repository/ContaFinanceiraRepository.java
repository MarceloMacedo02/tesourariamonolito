package br.com.sigest.tesouraria.repository;

import br.com.sigest.tesouraria.domain.entity.ContaFinanceira;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;

@Repository
public interface ContaFinanceiraRepository extends JpaRepository<ContaFinanceira, Long> {
    @Query("SELECT SUM(cf.saldoAtual) FROM ContaFinanceira cf")
    BigDecimal sumTotalSaldo();
}