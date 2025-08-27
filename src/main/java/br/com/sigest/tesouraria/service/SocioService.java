package br.com.sigest.tesouraria.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.sigest.tesouraria.domain.entity.Endereco;
import br.com.sigest.tesouraria.domain.entity.GrupoMensalidade;
import br.com.sigest.tesouraria.domain.entity.Socio;
import br.com.sigest.tesouraria.domain.enums.StatusSocio;
import br.com.sigest.tesouraria.dto.SocioDto;
import br.com.sigest.tesouraria.exception.RegraNegocioException;
import br.com.sigest.tesouraria.repository.GrupoMensalidadeRepository;
import br.com.sigest.tesouraria.repository.SocioRepository;

@Service
public class SocioService {

    @Autowired
    private SocioRepository repository;

    @Autowired
    private GrupoMensalidadeRepository grupoMensalidadeRepository;

    public List<Socio> findAll() {
        return repository.findAll();
    }

    public Socio findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RegraNegocioException("Sócio não encontrado!"));
    }

    public SocioDto findByIdAsDto(Long id) {
        Socio socio = findById(id);
        return toDto(socio);
    }

    @Transactional
    public Socio save(SocioDto dto) {
        Socio socio = toEntity(dto);
        return repository.save(socio);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    private Socio toEntity(SocioDto dto) {
        Socio socio;
        if (dto.getId() != null) {
            socio = findById(dto.getId());
        } else {
            socio = new Socio();
            socio.setDataCadastro(LocalDate.now());
            socio.setStatus(StatusSocio.FREQUENTE);
        }

        socio.setNome(dto.getNome());

        socio.setCpf(dto.getCpf());
        socio.setGrau(dto.getGrau());
        socio.setDataNascimento(dto.getDataNascimento());
        socio.setEmailAlternativo(dto.getEmailAlternativo());

        if (dto.getGrupoMensalidadeId() != null) {
            GrupoMensalidade grupo = grupoMensalidadeRepository.findById(dto.getGrupoMensalidadeId())
                    .orElseThrow(() -> new RegraNegocioException("Grupo de Mensalidade não encontrado!"));
            socio.setGrupoMensalidade(grupo);
        }

        // Lógica para Endereço
        Endereco endereco;
        if (socio.getEnderecos().isEmpty()) {
            endereco = new Endereco();
            socio.getEnderecos().add(endereco);
        } else {
            endereco = socio.getEnderecos().get(0);
        }
        endereco.setCep(dto.getCep());
        endereco.setLogradouro(dto.getLogradouro());
        endereco.setNumero(dto.getNumero());
        endereco.setComplemento(dto.getComplemento());
        endereco.setBairro(dto.getBairro());
        endereco.setCidade(dto.getCidade());
        endereco.setEstado(dto.getEstado());

        return socio;
    }

    private SocioDto toDto(Socio socio) {
        SocioDto dto = new SocioDto();
        dto.setId(socio.getId());
        dto.setNome(socio.getNome());
        dto.setCpf(socio.getCpf());
        dto.setGrau(socio.getGrau());
        dto.setDataNascimento(socio.getDataNascimento());
        dto.setCelular(socio.getCelular());
        dto.setTelefoneResidencial(socio.getTelefoneResidencial());

        dto.setEmailAlternativo(socio.getEmailAlternativo());
        dto.setStatus(socio.getStatus());

        if (socio.getGrupoMensalidade() != null) {
            dto.setGrupoMensalidadeId(socio.getGrupoMensalidade().getId());
        }

        if (!socio.getEnderecos().isEmpty()) {
            Endereco endereco = socio.getEnderecos().get(0);
            dto.setCep(endereco.getCep());
            dto.setLogradouro(endereco.getLogradouro());
            dto.setNumero(endereco.getNumero());
            dto.setComplemento(endereco.getComplemento());
            dto.setBairro(endereco.getBairro());
            dto.setCidade(endereco.getCidade());
            dto.setEstado(endereco.getEstado());
        }
        return dto;
    }
}
