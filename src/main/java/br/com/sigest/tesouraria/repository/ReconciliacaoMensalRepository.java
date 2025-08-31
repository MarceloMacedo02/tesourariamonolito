package br.com.sigest.tesouraria.repository;

import br.com.sigest.tesouraria.domain.entity.ReconciliacaoMensal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReconciliacaoMensalRepository extends JpaRepository<ReconciliacaoMensal, Long> {
    List<ReconciliacaoMensal> findByMesAndAno(int mes, int ano);
}
