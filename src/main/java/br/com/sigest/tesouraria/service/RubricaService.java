package br.com.sigest.tesouraria.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.sigest.tesouraria.domain.entity.CentroCusto;
import br.com.sigest.tesouraria.domain.entity.Rubrica;
import br.com.sigest.tesouraria.domain.repository.CentroCustoRepository;
import br.com.sigest.tesouraria.domain.repository.RubricaRepository;
import br.com.sigest.tesouraria.dto.RubricaDto;
import br.com.sigest.tesouraria.exception.RegraNegocioException;

/**
 * Serviço para a entidade Rubrica.
 */
@Service
public class RubricaService {

    @Autowired
    private RubricaRepository repository;
    @Autowired
    private CentroCustoRepository centroCustoRepository;

    public List<Rubrica> findAll() {
        return repository.findAll();
    }

    public Rubrica findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RegraNegocioException("Rubrica não encontrada!"));
    }

    public RubricaDto findByIdAsDto(Long id) {
        Rubrica rubrica = findById(id);
        return toDto(rubrica);
    }

    @Transactional
    public Rubrica save(RubricaDto dto) {
        Rubrica rubrica = toEntity(dto);
        return repository.save(rubrica);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    private Rubrica toEntity(RubricaDto dto) {
        Rubrica rubrica;
        if (dto.getId() != null) {
            rubrica = findById(dto.getId());
        } else {
            rubrica = new Rubrica();
        }

        rubrica.setNome(dto.getNome());
        rubrica.setTipo(dto.getTipo());
        rubrica.setValorPadrao(dto.getValorPadrao());

        if (dto.getCentroCustoId() != null) {
            CentroCusto centroCusto = centroCustoRepository.findById(dto.getCentroCustoId())
                    .orElseThrow(() -> new RegraNegocioException("Centro de Custo não encontrado!"));
            rubrica.setCentroCusto(centroCusto);
        } else {
            throw new RegraNegocioException("É necessário informar um Centro de Custo para a rubrica.");
        }

        return rubrica;
    }

    public RubricaDto toDto(Rubrica rubrica) {
        RubricaDto dto = new RubricaDto();
        dto.setId(rubrica.getId());
        dto.setNome(rubrica.getNome());
        dto.setTipo(rubrica.getTipo());
        dto.setValorPadrao(rubrica.getValorPadrao());
        dto.setCentroCustoId(rubrica.getCentroCusto().getId());
        return dto;
    }

}
