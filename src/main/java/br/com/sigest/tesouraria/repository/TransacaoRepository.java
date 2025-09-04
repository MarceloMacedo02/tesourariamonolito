package br.com.sigest.tesouraria.repository;

import br.com.sigest.tesouraria.domain.entity.Transacao;
import br.com.sigest.tesouraria.domain.enums.TipoTransacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    @Query("SELECT t FROM Transacao t WHERE FUNCTION('MONTH', t.data) = :month AND FUNCTION('YEAR', t.data) = :year ORDER BY t.data DESC")
    List<Transacao> findByMonthAndYearOrderByDataDesc(int month, int year);

    List<Transacao> findAllByOrderByDataDesc();

    // Method for duplicate check
    Transacao findByDataAndTipoAndValorAndFornecedorOuSocioAndDocumentoAndDescricao(
        LocalDate data, TipoTransacao tipo, BigDecimal valor, String fornecedorOuSocio, String documento, String descricao);

    @Query("SELECT DISTINCT FUNCTION('YEAR', t.data), FUNCTION('MONTH', t.data) FROM Transacao t ORDER BY FUNCTION('YEAR', t.data) DESC, FUNCTION('MONTH', t.data) DESC")
    List<Object[]> findDistinctYearsAndMonths();
}
