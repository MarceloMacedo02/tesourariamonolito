package br.com.sigest.tesouraria.domain.repository;

import java.util.List;

import br.com.sigest.tesouraria.domain.entity.ReconciliacaoMensal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReconciliacaoMensalRepository extends JpaRepository<ReconciliacaoMensal, Long> {
    @Query("SELECT rm FROM ReconciliacaoMensal rm WHERE rm.mes = ?1 AND rm.ano = ?2")
    List<ReconciliacaoMensal> findByMesAndAno(int mes, int ano);
}