package br.com.sigest.tesouraria.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.sigest.tesouraria.domain.entity.TransacaoPendente;

@Repository
public interface TransacaoPendenteRepository extends JpaRepository<TransacaoPendente, Long> {

    /**
     * Busca todas as transações pendentes que ainda não foram processadas
     */
    List<TransacaoPendente> findByProcessadoFalseOrderByDataImportacaoDesc();

    /**
     * Busca transações pendentes por arquivo de origem
     */
    List<TransacaoPendente> findByArquivoOrigemOrderByDataDesc(String arquivoOrigem);

    /**
     * Conta quantas transações pendentes existem
     */
    @Query("SELECT COUNT(tp) FROM TransacaoPendente tp WHERE tp.processado = false")
    long countPendentes();
}