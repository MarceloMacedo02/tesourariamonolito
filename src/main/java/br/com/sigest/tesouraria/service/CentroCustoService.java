package br.com.sigest.tesouraria.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.sigest.tesouraria.domain.entity.CentroCusto;
import br.com.sigest.tesouraria.dto.CentroCustoDto;
import br.com.sigest.tesouraria.repository.CentroCustoRepository;

/**
 * Serviço para gerenciar a lógica de negócio de Centros de Custo.
 */
@Service
public class CentroCustoService {

    @Autowired
    private CentroCustoRepository repository;

    /**
     * Busca todos os centros de custo.
     * 
     * @return Uma lista de CentroCustoDto.
     */
    @Transactional(readOnly = true)
    public List<CentroCustoDto> findAll() {
        return repository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    /**
     * Busca um centro de custo pelo ID.
     * 
     * @param id O ID do centro de custo.
     * @return Um Optional contendo o CentroCustoDto se encontrado.
     */
    @Transactional(readOnly = true)
    public Optional<CentroCustoDto> findById(Long id) {
        return repository.findById(id).map(this::toDto);
    }

    /**
     * Salva um novo centro de custo ou atualiza um existente.
     * 
     * @param dto O DTO do centro de custo a ser salvo.
     * @return O CentroCustoDto salvo.
     */
    @Transactional
    public CentroCustoDto save(CentroCustoDto dto) {
        CentroCusto entity = toEntity(dto);
        entity = repository.save(entity);
        return toDto(entity);
    }

    /**
     * Exclui um centro de custo pelo ID.
     * 
     * @param id O ID do centro de custo a ser excluído.
     */
    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    // Métodos de conversão
    private CentroCustoDto toDto(CentroCusto entity) {
        CentroCustoDto dto = new CentroCustoDto();
        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setAtivo(entity.isAtivo());
        return dto;
    }

    private CentroCusto toEntity(CentroCustoDto dto) {
        CentroCusto entity = new CentroCusto();
        entity.setId(dto.getId());
        entity.setNome(dto.getNome());
        entity.setAtivo(dto.isAtivo());
        return entity;
    }
}
