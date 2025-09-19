package br.com.sigest.tesouraria.domain.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.sigest.tesouraria.domain.entity.Movimento;
import br.com.sigest.tesouraria.domain.enums.TipoMovimento;

public interface MovimentoRepository extends JpaRepository<Movimento, Long> {
    @Query("SELECT SUM(m.valor) FROM Movimento m WHERE m.tipo = :tipo AND m.dataHora BETWEEN :inicio AND :fim")
    BigDecimal sumByTipoAndPeriodo(@Param("tipo") TipoMovimento tipo, @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);

    List<Movimento> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);
    
    List<Movimento> findByTipoAndDataHoraBetween(TipoMovimento tipo, LocalDateTime inicio, LocalDateTime fim);
    
    @Query("SELECT DISTINCT MONTH(m.dataHora) as mes, YEAR(m.dataHora) as ano FROM Movimento m ORDER BY ano DESC, mes DESC")
    List<Object[]> findDistinctYearsAndMonths();
}