package br.com.sigest.tesouraria.domain.repository;

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

    List<Cobranca> findBySocioIdInAndStatus(List<Long> socioIds, StatusCobranca status);

    List<Cobranca> findByPagadorAndStatus(String pagador, StatusCobranca status);

    List<Cobranca> findByStatus(StatusCobranca status);

    List<Cobranca> findByDataVencimentoBetween(LocalDate inicio, LocalDate fim);

    @Query("SELECT SUM(c.valor) FROM Cobranca c WHERE c.status = 'ABERTA' OR c.status = 'VENCIDA'")
    BigDecimal sumTotalAReceber();

    /**
     * Busca uma cobrança para um sócio específico em um determinado mês e ano.
     * 
     * @param socio O objeto Socio.
     * @param mes   O número do mês (1-12).
     * @param ano   O ano.
     * @return Um Optional contendo a cobrança, se encontrada.
     */
    @Query("SELECT c FROM Cobranca c WHERE c.socio = ?1 AND MONTH(c.dataVencimento) = ?2 AND YEAR(c.dataVencimento) = ?3")
    Optional<Cobranca> findBySocioAndMesAndAno(Socio socio, int mes, int ano);
    
    /**
     * Busca todas as cobranças para um sócio específico em um determinado mês e ano.
     * 
     * @param socio O objeto Socio.
     * @param mes   O número do mês (1-12).
     * @param ano   O ano.
     * @return Uma lista contendo as cobranças encontradas.
     */
    @Query("SELECT c FROM Cobranca c LEFT JOIN FETCH c.grupoMensalidade WHERE c.socio = ?1 AND MONTH(c.dataVencimento) = ?2 AND YEAR(c.dataVencimento) = ?3")
    List<Cobranca> findCobrancasBySocioAndMesAndAno(Socio socio, int mes, int ano);

    @Query("SELECT new br.com.sigest.tesouraria.dto.RelatorioInadimplentesDto(s.nome, s.grau, SUM(c.valor), COUNT(c)) "
            +
            "FROM Cobranca c JOIN c.socio s " +
            "WHERE s.status = br.com.sigest.tesouraria.domain.enums.StatusSocio.FREQUENTE " +
            "AND c.status = br.com.sigest.tesouraria.domain.enums.StatusCobranca.ABERTA " +
            "AND c.dataVencimento < :dataLimite " +
            "GROUP BY s.nome, s.grau")
    List<RelatorioInadimplentesDto> findInadimplentes(@Param("dataLimite") LocalDate dataLimite);

    @Query("SELECT c FROM Cobranca c LEFT JOIN FETCH c.socio s LEFT JOIN FETCH s.dependentes WHERE c.id = :id")
    Optional<Cobranca> findByIdWithDependents(@Param("id") Long id);

    List<Cobranca> findBySocioIdAndStatus(Long id, StatusCobranca aberta);

    // New method added
    List<Cobranca> findBySocioAndStatusIn(Socio socio, List<StatusCobranca> statuses);
    
    // Method to find cobrancas by transacao id
    List<Cobranca> findByTransacaoId(Long transacaoId);
    
    // Method to find cobrancas by transacao id with socio
    @Query("SELECT c FROM Cobranca c LEFT JOIN FETCH c.socio WHERE c.transacao.id = :transacaoId")
    List<Cobranca> findByTransacaoIdWithSocio(@Param("transacaoId") Long transacaoId);
}
