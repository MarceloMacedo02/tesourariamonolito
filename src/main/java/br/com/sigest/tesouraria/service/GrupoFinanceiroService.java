package br.com.sigest.tesouraria.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.sigest.tesouraria.domain.entity.GrupoFinanceiro;
import br.com.sigest.tesouraria.dto.GrupoFinanceiroDto;
import br.com.sigest.tesouraria.exception.RegraNegocioException;
import br.com.sigest.tesouraria.domain.repository.GrupoFinanceiroRepository;

@Service
public class GrupoFinanceiroService {
    @Autowired
    private GrupoFinanceiroRepository repository;

    @Cacheable("gruposFinanceiros")
    public List<GrupoFinanceiro> findAll() {
        return repository.findAll();
    }

    @Cacheable(value = "grupoFinanceiro", key = "#id")
    public GrupoFinanceiro findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RegraNegocioException("Grupo Financeiro não encontrado!"));
    }

    public GrupoFinanceiroDto findByIdAsDto(Long id) {
        return toDto(findById(id));
    }

    @Transactional
    @CachePut(value = "grupoFinanceiro", key = "#result.id")
    @CacheEvict(value = "gruposFinanceiros", allEntries = true)
    public GrupoFinanceiro save(GrupoFinanceiroDto dto) {
        repository.findByNome(dto.getNome()).ifPresent(existente -> {
            if (!existente.getId().equals(dto.getId())) {
                throw new RegraNegocioException("Já existe um grupo financeiro cadastrado com este nome.");
            }
        });
        GrupoFinanceiro grupo = toEntity(dto);
        return repository.save(grupo);
    }

    @Transactional
    @CacheEvict(value = { "grupoFinanceiro", "gruposFinanceiros" }, allEntries = true)
    public void delete(Long id) {
        repository.delete(findById(id));
    }

    private GrupoFinanceiro toEntity(GrupoFinanceiroDto dto) {
        GrupoFinanceiro grupo = (dto.getId() != null) ? findById(dto.getId()) : new GrupoFinanceiro();
        grupo.setNome(dto.getNome());
        grupo.setDescricao(dto.getDescricao());
        grupo.setAtivo(dto.isAtivo());
        return grupo;
    }

    private GrupoFinanceiroDto toDto(GrupoFinanceiro grupo) {
        GrupoFinanceiroDto dto = new GrupoFinanceiroDto();
        dto.setId(grupo.getId());
        dto.setNome(grupo.getNome());
        dto.setDescricao(grupo.getDescricao());
        dto.setAtivo(grupo.isAtivo());
        return dto;
    }
}
