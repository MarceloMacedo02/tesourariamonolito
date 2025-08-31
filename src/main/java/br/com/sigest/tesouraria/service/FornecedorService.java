package br.com.sigest.tesouraria.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.sigest.tesouraria.domain.entity.Fornecedor;
import br.com.sigest.tesouraria.dto.FornecedorDto;
import br.com.sigest.tesouraria.exception.RegraNegocioException;
import br.com.sigest.tesouraria.repository.FornecedorRepository; 

/**
 * Serviço para a lógica de negócio de Fornecedores, com cache e conversão de DTOs.
 */
@Service
public class FornecedorService {

    @Autowired
    private FornecedorRepository repository;

    @Cacheable("fornecedores")
    public List<Fornecedor> findAll() {
        return repository.findAll();
    }

    @Cacheable(value = "fornecedor", key = "#id")
    public Fornecedor findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RegraNegocioException("Fornecedor não encontrado!"));
    }

    public FornecedorDto findByIdAsDto(Long id) {
        return toDto(findById(id));
    }

    @Transactional
    @CachePut(value = "fornecedor", key = "#result.id")
    @CacheEvict(value = "fornecedores", allEntries = true)
    public Fornecedor save(FornecedorDto dto) {
        repository.findByCnpj(dto.getCnpj()).ifPresent(existente -> {
            if (!existente.getId().equals(dto.getId())) {
                throw new RegraNegocioException("Já existe um fornecedor cadastrado com este CNPJ/CPF.");
            }
        });
        Fornecedor fornecedor = toEntity(dto);
        return repository.save(fornecedor);
    }

    @Transactional
    @CacheEvict(value = {"fornecedor", "fornecedores"}, allEntries = true)
    public void delete(Long id) {
        repository.delete(findById(id));
    }

    private Fornecedor toEntity(FornecedorDto dto) {
        Fornecedor fornecedor = (dto.getId() != null) ? findById(dto.getId()) : new Fornecedor();
        if (dto.getId() == null) {
            fornecedor.setDataCadastro(LocalDate.now());
        }
        fornecedor.setNome(dto.getNome());
        fornecedor.setCnpj(dto.getCnpj());
        fornecedor.setEmail(dto.getEmail());
        fornecedor.setCelular(dto.getCelular());
        fornecedor.setTelefoneComercial(dto.getTelefoneComercial());
        fornecedor.setAtivo(dto.isAtivo());
        fornecedor.getEnderecos().clear();
        if (dto.getEnderecos() != null) {
            dto.getEnderecos().stream()
                .filter(e -> e.getCep() != null && !e.getCep().trim().isEmpty())
                .forEach(e -> fornecedor.getEnderecos().add(e));
        }
        return fornecedor;
    }

    private FornecedorDto toDto(Fornecedor fornecedor) {
        FornecedorDto dto = new FornecedorDto();
        dto.setId(fornecedor.getId());
        dto.setNome(fornecedor.getNome());
        dto.setCnpj(fornecedor.getCnpj());
        dto.setEmail(fornecedor.getEmail());
        dto.setCelular(fornecedor.getCelular());
        dto.setTelefoneComercial(fornecedor.getTelefoneComercial());
        dto.setAtivo(fornecedor.isAtivo());
        dto.setEnderecos(new ArrayList<>(fornecedor.getEnderecos()));
        return dto;
    }
}