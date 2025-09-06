package br.com.sigest.tesouraria.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.sigest.tesouraria.domain.entity.Endereco;
import br.com.sigest.tesouraria.domain.entity.Fornecedor;
import br.com.sigest.tesouraria.dto.EnderecoDto;
import br.com.sigest.tesouraria.dto.FornecedorDto;
import br.com.sigest.tesouraria.exception.RegraNegocioException;
import br.com.sigest.tesouraria.repository.FornecedorRepository;

/**
 * Serviço para a lógica de negócio de Fornecedores, com cache e conversão de
 * DTOs.
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

    public Optional<Fornecedor> findByCnpj(String cnpj) {
        return repository.findByCnpj(cnpj);
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
    @CacheEvict(value = { "fornecedor", "fornecedores" }, allEntries = true)
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
                    .forEach(e -> fornecedor.getEnderecos().add(convertToEndereco(e)));
        }
        return fornecedor;
    }

    public FornecedorDto toDto(Fornecedor fornecedor) {
        FornecedorDto dto = new FornecedorDto();
        dto.setId(fornecedor.getId());
        dto.setNome(fornecedor.getNome());
        dto.setCnpj(fornecedor.getCnpj());
        dto.setEmail(fornecedor.getEmail());
        dto.setCelular(fornecedor.getCelular());
        dto.setTelefoneComercial(fornecedor.getTelefoneComercial());
        dto.setAtivo(fornecedor.isAtivo());
        dto.setEnderecos(
                fornecedor.getEnderecos().stream().map(this::convertToEnderecoDto).collect(Collectors.toList()));
        return dto;
    }

    private Endereco convertToEndereco(EnderecoDto dto) {
        Endereco endereco = new Endereco();
        endereco.setLogradouro(dto.getLogradouro());
        endereco.setNumero(dto.getNumero());
        endereco.setComplemento(dto.getComplemento());
        endereco.setBairro(dto.getBairro());
        endereco.setCidade(dto.getCidade());
        endereco.setEstado(dto.getEstado());
        endereco.setCep(dto.getCep());
        return endereco;
    }

    private EnderecoDto convertToEnderecoDto(Endereco endereco) {
        EnderecoDto dto = new EnderecoDto();
        dto.setLogradouro(endereco.getLogradouro());
        dto.setNumero(endereco.getNumero());
        dto.setComplemento(endereco.getComplemento());
        dto.setBairro(endereco.getBairro());
        dto.setCidade(endereco.getCidade());
        dto.setEstado(endereco.getEstado());
        dto.setCep(endereco.getCep());
        return dto;
    }
}
