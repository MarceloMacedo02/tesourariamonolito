package br.com.sigest.tesouraria.service;

import br.com.sigest.tesouraria.domain.entity.ReconciliacaoMensal;
import br.com.sigest.tesouraria.repository.ReconciliacaoMensalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ReconciliacaoService {

    @Autowired
    private ReconciliacaoMensalRepository reconciliacaoMensalRepository;

    public List<ReconciliacaoMensal> findAll() {
        return reconciliacaoMensalRepository.findAll();
    }

    public Optional<ReconciliacaoMensal> findById(Long id) {
        return reconciliacaoMensalRepository.findById(id);
    }

    @Transactional
    public ReconciliacaoMensal save(ReconciliacaoMensal reconciliacao) {
        return reconciliacaoMensalRepository.save(reconciliacao);
    }

    @Transactional
    public void deleteById(Long id) {
        reconciliacaoMensalRepository.deleteById(id);
    }

    // TODO: Add method to calculate saldoFinal based on movements for a given month and ContaFinanceira
    // This would involve querying Movimento table for the specific month and account.
}
