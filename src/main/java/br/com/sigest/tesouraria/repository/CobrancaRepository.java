package br.com.sigest.tesouraria.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.sigest.tesouraria.domain.entity.Cobranca;
import br.com.sigest.tesouraria.domain.entity.Socio;
import br.com.sigest.tesouraria.domain.enums.StatusCobranca;
import br.com.sigest.tesouraria.dto.RelatorioInadimplentesDto;

public interface CobrancaRepository extends JpaRepository<Cobranca, Long> {
    List<Cobranca> findByStatusAndDataVencimentoBefore(StatusCobranca status, LocalDate date);

    List<Cobranca> findBySocioIdAndStatus(Long socioId, StatusCobranca status);

    List<Cobranca> findByStatus(StatusCobranca status);

    List<Cobranca> findByDataVencimentoBetween(LocalDate inicio, LocalDate fim);

    @Query("SELECT SUM(c.valor) FROM Cobranca c WHERE c.status = 'ABERTA' OR c.status = 'VENCIDA'")
    BigDecimal sumTotalAReceber();

    /**
     * Busca uma cobrança para um sócio específico em um determinado mês e ano.
     * @param socio O objeto Socio.
     * @param mes O número do mês (1-12).
     * @param ano O ano.
     * @return Um Optional contendo a cobrança, se encontrada.
     */
    @Query("SELECT c FROM Cobranca c WHERE c.socio = ?1 AND MONTH(c.dataVencimento) = ?2 AND YEAR(c.dataVencimento) = ?3")
    Optional<Cobranca> findBySocioAndMesAndAno(Socio socio, int mes, int ano);

    @Query("SELECT new br.com.sigest.tesouraria.dto.RelatorioInadimplentesDto(s.nome, s.grau, SUM(c.valor), COUNT(c)) " +
           "FROM Cobranca c JOIN c.socio s " +
           "WHERE s.status = br.com.sigest.tesouraria.domain.enums.StatusSocio.FREQUENTE " +
           "AND c.status = br.com.sigest.tesouraria.domain.enums.StatusCobranca.ABERTA " +
           "AND c.dataVencimento < :dataLimite " +
           "GROUP BY s.nome, s.grau")
    List<RelatorioInadimplentesDto> findInadimplentes(@Param("dataLimite") LocalDate dataLimite);
}