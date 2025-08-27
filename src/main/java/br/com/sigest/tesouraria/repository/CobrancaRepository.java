package br.com.sigest.tesouraria.repository;
import br.com.sigest.tesouraria.domain.entity.Cobranca;
import br.com.sigest.tesouraria.domain.enums.StatusCobranca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
public interface CobrancaRepository extends JpaRepository<Cobranca, Long> {
    List<Cobranca> findByStatusAndDataVencimentoBefore(StatusCobranca status, LocalDate date);
    List<Cobranca> findBySocioIdAndStatus(Long socioId, StatusCobranca status);
    List<Cobranca> findByStatus(StatusCobranca status);
    @Query("SELECT SUM(c.valor) FROM Cobranca c WHERE c.status = 'ABERTA' OR c.status = 'VENCIDA'")
    BigDecimal sumTotalAReceber();
}