package br.com.sigest.tesouraria.service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import br.com.sigest.tesouraria.domain.entity.Instituicao;
import br.com.sigest.tesouraria.domain.entity.Socio;
import br.com.sigest.tesouraria.domain.enums.StatusSocio;
import br.com.sigest.tesouraria.domain.repository.InstituicaoRepository;
import br.com.sigest.tesouraria.domain.repository.SocioRepository;
import br.com.sigest.tesouraria.exception.RegraNegocioException;

@Service
public class InstituicaoService {

    @Autowired
    private InstituicaoRepository instituicaoRepository;

    @Autowired
    private SocioRepository socioRepository;

    private static final long MAX_LOGO_SIZE_BYTES = 5 * 1024 * 1024; // 5MB

    @Transactional
    public Instituicao criarOuAtualizarInstituicao(Instituicao instituicao) {
        Optional<Instituicao> existingInstituicao = instituicaoRepository.findByFixedId(1L);

        if (existingInstituicao.isPresent()) {
            Instituicao instituicaoToUpdate = existingInstituicao.get();
            instituicaoToUpdate.setNome(instituicao.getNome());
            instituicaoToUpdate.setCnpj(instituicao.getCnpj());
            instituicaoToUpdate.setEndereco(instituicao.getEndereco());
            // Update cargos if they are part of the form submission
            // This assumes cargos are handled separately or are part of a more complex DTO
            // For now, only basic fields are updated here.
            return instituicaoRepository.save(instituicaoToUpdate);
        } else {
            instituicao.setFixedId(1L); // Ensure the fixed ID is set for new creation
            return instituicaoRepository.save(instituicao);
        }
    }

    public Instituicao buscarInstituicao() {
        return instituicaoRepository.findByFixedId(1L)
                .orElse(null); // Or throw an exception if no institution is found
    }

    @Transactional
    public void atribuirCargo(Long socioId, String cargo) {
        Socio socio = socioRepository.findById(socioId)
                .orElseThrow(() -> new RegraNegocioException("Sócio não encontrado com ID: " + socioId));

        // Rule 3: Apenas sócios ativos (StatusSocio = FREQUENTE) podem ser atribuídos a
        // cargos.
        if (socio.getStatus() != StatusSocio.FREQUENTE) {
            throw new IllegalArgumentException("Sócio não está ativo (FREQUENTE) e não pode ser atribuído a um cargo.");
        }

        Instituicao instituicao = buscarInstituicao();
        if (instituicao == null) {
            throw new IllegalStateException("Nenhuma instituição cadastrada. Cadastre a instituição primeiro.");
        }

        // Rule 2: Um sócio só pode ocupar um cargo por vez.
        // Check if the socio already holds any other position
        if (isSocioAlreadyAssignedToAnyRole(instituicao, socio)) {
            throw new IllegalArgumentException("Sócio já ocupa outro cargo na instituição.");
        }

        // Assign the socio to the specified cargo using reflection
        try {
            Field cargoField = Instituicao.class.getDeclaredField(cargo);
            cargoField.setAccessible(true);
            cargoField.set(instituicao, socio);
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("Cargo inválido: " + cargo);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Erro ao atribuir cargo: " + e.getMessage(), e);
        }

        instituicaoRepository.save(instituicao);
    }

    private boolean isSocioAlreadyAssignedToAnyRole(Instituicao instituicao, Socio socio) {
        List<Field> cargoFields = Arrays.asList(Instituicao.class.getDeclaredFields());
        for (Field field : cargoFields) {
            if (field.getType().equals(Socio.class)) {
                try {
                    field.setAccessible(true);
                    Socio assignedSocio = (Socio) field.get(instituicao);
                    if (assignedSocio != null && assignedSocio.getId().equals(socio.getId())) {
                        return true;
                    }
                } catch (IllegalAccessException e) {
                    // Log error or handle appropriately
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    @Transactional
    public void uploadLogo(Long id, MultipartFile file) throws IOException {
        Instituicao instituicao = instituicaoRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Instituição não encontrada com ID: " + id));

        // Rule 4: Upload da logo deve aceitar apenas imagens e limitar o tamanho máximo
        // (ex.: 5MB).
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Arquivo de logo vazio.");
        }
        if (file.getSize() > MAX_LOGO_SIZE_BYTES) {
            throw new IllegalArgumentException("Tamanho da logo excede o limite de 5MB.");
        }
        if (!file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Tipo de arquivo inválido. Apenas imagens são permitidas.");
        }

        instituicao.setLogo(file.getBytes());
        instituicaoRepository.save(instituicao);
    }

    public byte[] downloadLogo(Long id) {
        Instituicao instituicao = instituicaoRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Instituição não encontrada com ID: " + id));
        return instituicao.getLogo();
    }

    // Helper method to get all possible cargo names
    public List<String> getAllCargoNames() {
        return Arrays.asList(
                "presidente", "vicePresidente", "tesoureiro", "segundoTesoureiro",
                "secretario", "segundoSecretario", "orador",
                "presidenteConselhoFiscal", "segundoConselhoFiscal", "terceiroConselhoFiscal",
                "primeiroSuplenteConselhoFiscal", "segundoSuplenteConselhoFiscal", "terceiroSuplenteConselhoFiscal");
    }

    // Helper method to remove a socio from a specific cargo
    @Transactional
    public void removerCargo(Long socioId, String cargo) {
        Instituicao instituicao = buscarInstituicao();
        if (instituicao == null) {
            throw new IllegalStateException("Nenhuma instituição cadastrada.");
        }

        try {
            Field cargoField = Instituicao.class.getDeclaredField(cargo);
            cargoField.setAccessible(true);
            Socio assignedSocio = (Socio) cargoField.get(instituicao);

            if (assignedSocio != null && assignedSocio.getId().equals(socioId)) {
                cargoField.set(instituicao, null); // Set the cargo to null
                instituicaoRepository.save(instituicao);
            } else {
                throw new IllegalArgumentException("Sócio não ocupa o cargo especificado.");
            }
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("Cargo inválido: " + cargo);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Erro ao remover cargo: " + e.getMessage(), e);
        }
    }
}
