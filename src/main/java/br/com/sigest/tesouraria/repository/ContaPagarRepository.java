package br.com.sigest.tesouraria.repository;
import br.com.sigest.tesouraria.domain.entity.ContaPagar;
import br.com.sigest.tesouraria.domain.enums.StatusContaPagar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.util.List;
public interface ContaPagarRepository extends JpaRepository<ContaPagar, Long> {
    List<ContaPagar> findByStatus(StatusContaPagar status);
    @Query("SELECT SUM(cp.valor) FROM ContaPagar cp WHERE cp.status = 'A_PAGAR'")
    BigDecimal sumTotalAPagar();
}