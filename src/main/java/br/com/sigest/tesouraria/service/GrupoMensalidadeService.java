package br.com.sigest.tesouraria.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.sigest.tesouraria.domain.entity.GrupoMensalidade;
import br.com.sigest.tesouraria.domain.entity.GrupoMensalidadeRubrica;
import br.com.sigest.tesouraria.domain.entity.Rubrica;
import br.com.sigest.tesouraria.domain.repository.GrupoMensalidadeRepository;
import br.com.sigest.tesouraria.domain.repository.GrupoMensalidadeRubricaRepository;
import br.com.sigest.tesouraria.domain.repository.RubricaRepository;
import br.com.sigest.tesouraria.dto.GrupoMensalidadeDto;
import br.com.sigest.tesouraria.dto.GrupoMensalidadeRubricaDto;
import br.com.sigest.tesouraria.exception.RegraNegocioException;

@Service
public class GrupoMensalidadeService {
    @Autowired
    private GrupoMensalidadeRepository repository;
    @Autowired
    private GrupoMensalidadeRubricaRepository rubricaRepository;
    @Autowired
    private RubricaRepository rubricasBaseRepository;

    public List<GrupoMensalidadeDto> findAllDtos() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    public GrupoMensalidadeDto findDtoById(Long id) {
        GrupoMensalidade grupo = findById(id);
        return toDto(grupo);
    }

    public GrupoMensalidade findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Grupo de Mensalidade não encontrado!"));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public GrupoMensalidadeDto cloneGrupoMensalidade(Long id) {
        GrupoMensalidade grupoOriginal = findById(id);
        
        // Criar novo grupo com nome modificado
        GrupoMensalidade grupoClone = new GrupoMensalidade();
        grupoClone.setNome(grupoOriginal.getNome() + " - Cópia");
        
        // Copiar rubricas
        if (grupoOriginal.getRubricas() != null) {
            Set<GrupoMensalidadeRubrica> rubricasClonadas = grupoOriginal.getRubricas().stream()
                .map(rubricaOriginal -> {
                    GrupoMensalidadeRubrica rubricaClone = new GrupoMensalidadeRubrica();
                    rubricaClone.setGrupoMensalidade(grupoClone);
                    rubricaClone.setRubrica(rubricaOriginal.getRubrica());
                    rubricaClone.setValor(rubricaOriginal.getValor());
                    return rubricaClone;
                })
                .collect(Collectors.toSet());
            grupoClone.setRubricas(rubricasClonadas);
        }
        
        // Salvar o grupo clonado
        GrupoMensalidade grupoSalvo = repository.save(grupoClone);
        
        return toDto(grupoSalvo);
    }

    public void saveDto(GrupoMensalidadeDto dto) {
        GrupoMensalidade grupo = (dto.getId() != null) ? findById(dto.getId()) : new GrupoMensalidade();
        grupo.setNome(dto.getNome());

        if (dto.getRubricas() != null) {
            Set<GrupoMensalidadeRubrica> novasRubricas = dto.getRubricas().stream()
                    .filter(r -> r.getRubricaId() != null)
                    .map(r -> {
                        GrupoMensalidadeRubrica item = new GrupoMensalidadeRubrica();
                        item.setGrupoMensalidade(grupo);
                        Rubrica rubrica = rubricasBaseRepository.findById(r.getRubricaId())
                                .orElseThrow(() -> new RegraNegocioException("Rubrica não encontrada!"));
                        item.setRubrica(rubrica);
                        item.setGrupoMensalidade(grupo);
                        // Converter valor formatado para BigDecimal
                        BigDecimal valorBigDecimal = r.getValor() != null ? BigDecimal.valueOf(r.getValor()) : BigDecimal.ZERO;

                        item.setValor(valorBigDecimal);
                        return item;
                    })
                    .collect(Collectors.toSet());
            if (grupo.getRubricas() == null) {
                grupo.setRubricas(new java.util.HashSet<>());
            }
            grupo.getRubricas().clear();
            grupo.getRubricas().addAll(novasRubricas);
        }

        repository.save(grupo);
    }

    private GrupoMensalidadeDto toDto(GrupoMensalidade grupo) {
        GrupoMensalidadeDto dto = new GrupoMensalidadeDto();
        dto.setId(grupo.getId());
        dto.setNome(grupo.getNome());
        // Set BigDecimal value directly
        dto.setValor(grupo.getValor());
        if (grupo.getRubricas() != null) {
            List<GrupoMensalidadeRubricaDto> rubricasDto = grupo.getRubricas().stream().map(r -> {
                GrupoMensalidadeRubricaDto rDto = new GrupoMensalidadeRubricaDto();
                rDto.setId(r.getId());
                rDto.setRubricaId(r.getRubrica().getId());
                rDto.setRubricaNome(r.getRubrica().getNome());
                // Convert BigDecimal to Float for DTO
                BigDecimal valor = r.getValor();
                rDto.setValor(valor != null ? valor.floatValue() : 0.0F);
                return rDto;
            }).toList();
            dto.setRubricas(rubricasDto);
        }
        return dto;
    }
}
