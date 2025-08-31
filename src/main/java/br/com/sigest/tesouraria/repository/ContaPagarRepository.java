package br.com.sigest.tesouraria.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.sigest.tesouraria.domain.entity.ContaPagar;

@Repository
public interface ContaPagarRepository extends JpaRepository<ContaPagar, Long> {

    @Query("SELECT SUM(c.valor) FROM ContaPagar c WHERE c.status = 'ABERTA'")
    BigDecimal sumTotalAPagar();
}
