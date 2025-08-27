package br.com.sigest.tesouraria.repository;
import br.com.sigest.tesouraria.domain.entity.Movimento;
import br.com.sigest.tesouraria.domain.enums.TipoMovimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
public interface MovimentoRepository extends JpaRepository<Movimento, Long> {
    @Query("SELECT SUM(m.valor) FROM Movimento m WHERE m.tipo = :tipo AND m.dataHora BETWEEN :inicio AND :fim")
    BigDecimal sumByTipoAndPeriodo(@Param("tipo") TipoMovimento tipo, @Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);
    List<Movimento> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);
}