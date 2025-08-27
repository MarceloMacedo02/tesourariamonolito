package br.com.sigest.tesouraria.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import br.com.sigest.tesouraria.domain.entity.Fornecedor;
import java.util.Optional;

/**
 * Repositório para a entidade Fornecedor.
 */
public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {
    Optional<Fornecedor> findByCnpj(String cnpj);
}