package br.com.sigest.tesouraria.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.sigest.tesouraria.domain.entity.Cobranca;
import br.com.sigest.tesouraria.domain.enums.StatusCobranca;

public interface CobrancaRepository extends JpaRepository<Cobranca, Long> {

    List<Cobranca> findByStatusAndDataVencimentoBefore(StatusCobranca status, LocalDate date);

    List<Cobranca> findBySocioIdAndStatus(Long socioId, StatusCobranca status);

    List<Cobranca> findByStatus(StatusCobranca status);

    List<Cobranca> findByDataVencimentoBetween(LocalDate inicio, LocalDate fim);

    @Query("SELECT SUM(c.valor) FROM Cobranca c WHERE c.status = 'ABERTA' OR c.status = 'VENCIDA'")
    BigDecimal sumTotalAReceber();
}