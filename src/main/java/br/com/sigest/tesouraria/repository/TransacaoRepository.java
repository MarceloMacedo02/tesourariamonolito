package br.com.sigest.tesouraria.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.sigest.tesouraria.domain.entity.Transacao;
import br.com.sigest.tesouraria.domain.enums.TipoTransacao;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    List<Transacao> findByDataBetweenOrderByDataDesc(LocalDate startDate, LocalDate endDate);

    List<Transacao> findAllByOrderByDataDesc();

    @Query("SELECT DISTINCT YEAR(t.data), MONTH(t.data) FROM Transacao t ORDER BY YEAR(t.data) DESC, MONTH(t.data) DESC")
    List<Object[]> findDistinctYearsAndMonths();

    Optional<Transacao> findByDataAndTipoAndValorAndDescricaoAndDocumento(LocalDate data, TipoTransacao tipo, java.math.BigDecimal valor, String descricao, String documento);
}