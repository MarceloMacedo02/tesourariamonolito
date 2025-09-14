package br.com.sigest.tesouraria.service;

import br.com.sigest.tesouraria.domain.entity.ContaFinanceira;
import br.com.sigest.tesouraria.exception.RegraNegocioException;
import br.com.sigest.tesouraria.domain.repository.ContaFinanceiraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ContaFinanceiraService {
    @Autowired private ContaFinanceiraRepository repository;
    
    public List<ContaFinanceira> findAll() {
        return repository.findAll();
    }

    public ContaFinanceira findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RegraNegocioException("Conta Financeira não encontrada!"));
    }
    
    public ContaFinanceira save(ContaFinanceira contaFinanceira) {
        return repository.save(contaFinanceira);
    }
    
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
