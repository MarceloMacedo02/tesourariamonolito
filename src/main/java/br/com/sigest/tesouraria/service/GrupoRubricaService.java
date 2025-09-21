package br.com.sigest.tesouraria.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.sigest.tesouraria.domain.entity.GrupoRubrica;
import br.com.sigest.tesouraria.dto.GrupoRubricaDto;
import br.com.sigest.tesouraria.exception.RegraNegocioException;
import br.com.sigest.tesouraria.domain.repository.GrupoRubricaRepository;

/**
 * * Serviço para gerenciar a lógica de negócio de Grupos de Rubrica.
 * 
 */
@Service
public class GrupoRubricaService {
	private static final Logger logger = LoggerFactory.getLogger(GrupoRubricaService.class);

	@Autowired
	private GrupoRubricaRepository repository;

	/**
	 * * Busca todos os grupos de rubrica.
	 * * * @return Uma lista de GrupoRubricaDto.
	 * 
	 */

	public List<GrupoRubricaDto> findAll() {
		logger.info("Buscando todos os grupos de rubrica.");
		return repository.findAll().stream().map(this::toDto).collect(Collectors.toList());
	}

	/**
	 * * Busca todas as entidades de grupo de rubrica para relatório.
	 * * @return Uma lista de GrupoRubrica.
	 * 
	 */
	@Transactional(readOnly = true)
	public List<GrupoRubrica> findAllEntities() {
	    logger.info("Buscando todas as entidades de grupo de rubrica para relatório.");
	    return repository.findAll();
	}

	/**
	 * * Busca um grupo de rubrica pelo ID.
	 * * * @param id O ID do grupo de rubrica.
	 * * @return Um Optional contendo o GrupoRubricaDto se encontrado.
	 * 
	 */

	@Transactional(readOnly = true)
	public Optional<GrupoRubricaDto> findById(Long id) {
		logger.info("Buscando grupo de rubrica com ID: {}", id);
		return repository.findById(id).map(this::toDto);
	}

	/**
	 * * Salva um novo grupo de rubrica ou atualiza um existente.
	 * * * @param dto O DTO do grupo de rubrica a ser salvo.
	 * * @return O GrupoRubricaDto salvo.
	 * 
	 */

	@Transactional
	public GrupoRubricaDto save(GrupoRubricaDto dto) {
		logger.info("Salvando grupo de rubrica: {}", dto.getNome());
		GrupoRubrica entity = toEntity(dto);
		entity = repository.save(entity);
		return toDto(entity);
	}

	/**
	 * * Exclui um grupo de rubrica pelo ID.
	 * * * @param id O ID do grupo de rubrica a ser excluído.
	 * 
	 */

	@Transactional
	public void deleteById(Long id) {
		logger.info("Excluindo grupo de rubrica com ID: {}", id);
		if (!repository.existsById(id)) {
			logger.warn("Tentativa de exclusão de grupo de rubrica inexistente com ID: {}", id);
			throw new RegraNegocioException("Grupo de rubrica não encontrado.");
		}
		repository.deleteById(id);
	}

	// Métodos de conversão

	private GrupoRubricaDto toDto(GrupoRubrica entity) {
		GrupoRubricaDto dto = new GrupoRubricaDto();
		dto.setId(entity.getId());
		dto.setNome(entity.getNome());
		dto.setAtivo(entity.isAtivo());
		return dto;
	}

	private GrupoRubrica toEntity(GrupoRubricaDto dto) {
		GrupoRubrica entity = new GrupoRubrica();
		entity.setId(dto.getId());
		entity.setNome(dto.getNome());
		entity.setAtivo(dto.isAtivo());
		return entity;
	}
}