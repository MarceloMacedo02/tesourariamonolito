package br.com.sigest.tesouraria.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import br.com.sigest.tesouraria.domain.entity.GrupoMensalidade;
import br.com.sigest.tesouraria.domain.entity.Role;
import br.com.sigest.tesouraria.domain.entity.Socio;
import br.com.sigest.tesouraria.domain.entity.Usuario;
import br.com.sigest.tesouraria.domain.enums.StatusSocio;
import br.com.sigest.tesouraria.domain.repository.GrupoMensalidadeRepository;
import br.com.sigest.tesouraria.domain.repository.RoleRepository;
import br.com.sigest.tesouraria.domain.repository.SocioRepository;
import br.com.sigest.tesouraria.domain.repository.UsuarioRepository;
import br.com.sigest.tesouraria.dto.SocioDto;
import br.com.sigest.tesouraria.dto.SocioImportResultDTO;
import br.com.sigest.tesouraria.exception.RegraNegocioException;

@Service
public class SocioService {

    @Autowired
    private SocioRepository repository;

    @Autowired
    private GrupoMensalidadeRepository grupoMensalidadeRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public List<Socio> findSociosByStatus(StatusSocio status) {
        return repository.findByStatus(status);
    }

    public List<Socio> findAll() {
        return repository.findAll();
    }

    public Socio findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RegraNegocioException("Sócio não encontrado!"));
    }

    public Socio findByIdWithDependentes(Long id) {
        return repository.findByIdWithDependentes(id).orElseThrow(() -> new RegraNegocioException("Sócio não encontrado com dependentes!"));
    }

    public SocioDto findByIdAsDto(Long id) {
        Socio socio = findById(id);
        return toDto(socio);
    }

    public Optional<Socio> findByCpf(String cpf) {
        return repository.findByCpf(cpf);
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
            socio = new Socio();
            socio.setDataCadastro(LocalDate.now());
        }

        socio.setNome(dto.getNome());

        socio.setCpf(dto.getCpf());
        socio.setGrau(dto.getGrau());
        socio.setDataNascimento(dto.getDataNascimento());
        socio.setEmailAlternativo(dto.getEmailAlternativo());
        if (dto.getStatus() != null && !dto.getStatus().isEmpty()) {
            try {
                socio.setStatus(StatusSocio.valueOf(dto.getStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                // Handle the case where the status string from DTO does not match any enum
                // constant
                // For now, we can log an error or set a default status
                System.err.println(
                        "Invalid StatusSocio value from DTO: " + dto.getStatus() + ". Setting to default (FREQUENTE).");
                socio.setStatus(StatusSocio.FREQUENTE); // Or throw an exception, or set to null
            }
        } else {
            socio.setStatus(StatusSocio.FREQUENTE); // Default status if DTO status is null or empty
        }

        if (dto.getGrupoMensalidadeId() != null) {
            GrupoMensalidade grupo = grupoMensalidadeRepository.findById(dto.getGrupoMensalidadeId())
                    .orElseThrow(() -> new RegraNegocioException("Grupo de Mensalidade não encontrado!"));
            socio.setGrupoMensalidade(grupo);
        }

        if (dto.getSocioTitularId() != null) {
            Socio titular = repository.findById(dto.getSocioTitularId())
                    .orElseThrow(() -> new RegraNegocioException("Sócio Titular não encontrado!"));
            socio.setTitular(titular);
        } else {
            socio.setTitular(null);
        }

        socio.setEnderecoResidencial(dto.getEnderecoResidencial());
        // Lógica para Endereço
        // Endereco endereco;
        // if (socio.getEnderecos().isEmpty()) {
        // endereco = new Endereco();
        // socio.getEnderecos().add(endereco);
        // } else {
        // endereco = socio.getEnderecos().get(0);
        // }
        // endereco.setCep(dto.getCep());
        // endereco.setLogradouro(dto.getLogradouro());
        // endereco.setNumero(dto.getNumero());
        // endereco.setComplemento(dto.getComplemento());
        // endereco.setBairro(dto.getBairro());
        // endereco.setCidade(dto.getCidade());
        // endereco.setEstado(dto.getEstado());

        return socio;
    }

    public SocioDto toDto(Socio socio) {
        SocioDto dto = new SocioDto();
        dto.setId(socio.getId());
        dto.setNome(socio.getNome());
        dto.setCpf(socio.getCpf());
        dto.setGrau(socio.getGrau());
        dto.setDataNascimento(socio.getDataNascimento());
        dto.setCelular(socio.getCelular());
        dto.setTelefoneResidencial(socio.getTelefoneResidencial());

        dto.setEmailAlternativo(socio.getEmailAlternativo());
        dto.setStatus(socio.getStatus().name());
        dto.setEnderecoResidencial(socio.getEnderecoResidencial());

        if (socio.getGrupoMensalidade() != null) {
            dto.setGrupoMensalidadeId(socio.getGrupoMensalidade().getId());
        }

        if (socio.getTitular() != null) {
            dto.setSocioTitularId(socio.getTitular().getId());

        }

        if (socio.getDependentes() != null && !socio.getDependentes().isEmpty()) {
            dto.setDependentes(
                    socio.getDependentes().stream().map(this::toDtoForDependent).collect(Collectors.toList()));
        }

        // if (!socio.getEnderecos().isEmpty()) {
        // Endereco endereco = socio.getEnderecos().get(0);
        // if (endereco != null) {
        // dto.setCep(endereco.getCep());
        // dto.setLogradouro(endereco.getLogradouro());
        // dto.setNumero(endereco.getNumero());
        // dto.setComplemento(endereco.getComplemento());
        // dto.setBairro(endereco.getBairro());
        // dto.setCidade(endereco.getCidade());
        // dto.setEstado(endereco.getEstado());
        // }
        // }
        return dto;
    }

    private SocioDto toDtoForDependent(Socio socio) {
        SocioDto dto = new SocioDto();
        dto.setId(socio.getId());
        dto.setNome(socio.getNome());
        dto.setCpf(socio.getCpf());
        dto.setGrau(socio.getGrau());
        dto.setStatus(socio.getStatus().getDescricao());
        return dto;
    }

    public SocioImportResultDTO importSociosFromCsv(MultipartFile file) {
        int insertedCount = 0;
        int updatedCount = 0;
        int errorCount = 0;
        String message = "Importação concluída.";

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            reader.readLine(); // Skip header row

            while ((line = reader.readLine()) != null) {
                try {
                    String[] fields = line.split(",");
                    if (fields.length < 15) { // Assuming 15 columns:
                                              // nome,cpf,grau,dataNascimento,emailAlternativo,celular,telefoneResidencial,cep,logradouro,numero,complemento,bairro,cidade,estado,status_csv
                        errorCount++;
                        continue;
                    }

                    String grauCsv = fields[0].trim();
                    String grau = mapGrauFromCsv(grauCsv);
                    String nome = fields[1].trim();
                    String diaNasc = fields[2].trim();
                    String mesNasc = fields[3].trim();
                    String dataNascimentoStr = fields[4].trim();
                    String cpf = fields[5].trim().replaceAll("[^0-9]", "");
                    String statusCsv = fields[6].trim();
                    String celular = fields[7].trim();
                    String telefoneResidencial = fields[8].trim();
                    String email = fields[9].trim();
                    String emailAlternativo = fields[10].trim();
                    String enderecoResidencial = fields[11].trim();
                    String enderecoComercial = fields[12].trim();
                    String enderecoOutro = fields[13].trim();

                    Socio socio = new Socio();
                    if (cpf.isEmpty() || cpf == null) {
                        socio = new Socio();
                        cpf = UUID.randomUUID().toString(); // Generate a random CPF to avoid null issues
                        socio.setDataCadastro(LocalDate.now());
                        insertedCount++;
                    } else {

                        Optional<Socio> socioByCpf = repository.findByCpf(cpf);

                        if (socioByCpf.isPresent()) {
                            socio = socioByCpf.get();
                            updatedCount++;
                        } else {
                            socio = new Socio();
                            socio.setDataCadastro(LocalDate.now());
                            insertedCount++;
                        }
                    }
                    socio.setNome(nome);
                    socio.setCpf(cpf);
                    socio.setGrau(grau);

                    try {
                        socio.setDataNascimento(
                                LocalDate.parse(dataNascimentoStr, DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    } catch (DateTimeParseException e) {
                        // Handle invalid date format, e.g., log and skip or set to null
                        System.err.println("Invalid date format for socio " + nome + ": " + dataNascimentoStr);
                        socio.setDataNascimento(null); // Or handle as appropriate
                    }

                    socio.setEmailAlternativo(emailAlternativo);
                    socio.setCelular(celular);
                    socio.setTelefoneResidencial(telefoneResidencial);
                    socio.setEnderecoResidencial(enderecoResidencial);

                    // Set status based on CSV value
                    if (statusCsv.equalsIgnoreCase("FREQUENTE")) {
                        socio.setStatus(StatusSocio.FREQUENTE);
                    } else {
                        socio.setStatus(StatusSocio.AFASTADO);
                    }

                    // Handle Endereco
                    // Endereco endereco;
                    // if (socio.getEnderecos().isEmpty()) {
                    // endereco = new Endereco();
                    // socio.getEnderecos().add(endereco);
                    // } else {
                    // endereco = socio.getEnderecos().get(0);
                    // }
                    // endereco.setCep(cep);
                    // endereco.setLogradouro(logradouro);
                    // endereco.setNumero(numero);
                    // endereco.setComplemento(complemento);
                    // endereco.setBairro(bairro);
                    // endereco.setCidade(cidade);
                    // endereco.setEstado(estado);

                    repository.save(socio);

                    // User management logic
                    Usuario usuario;
                    Optional<Usuario> existingUsuario = usuarioRepository.findByUsername(email);

                    if (existingUsuario.isPresent()) {
                        usuario = existingUsuario.get();
                        // Update existing user if needed (e.g., associate with socio if not already)
                        if (usuario.getSocio() == null) {
                            usuario.setSocio(socio);
                            usuarioRepository.save(usuario);
                        }
                    } else {
                        // If no user found by email, check if socio already has a user associated
                        Optional<Usuario> usuarioBySocio = usuarioRepository.findBySocio(socio);
                        if (usuarioBySocio.isPresent()) {
                            usuario = usuarioBySocio.get();
                            // Update existing user if needed
                        } else {
                            // Create new user
                            usuario = new Usuario();
                            String username;
                            if (email != null && !email.isEmpty()) {
                                username = email;
                            } else {
                                username = UUID.randomUUID().toString(); // Generate UUID if no email
                            }
                            usuario.setUsername(username);
                            usuario.setPassword(passwordEncoder.encode("123456789")); // Encode default password
                            usuario.setSocio(socio);

                            // Assign ROLE_SOCIO
                            Optional<Role> socioRole = roleRepository.findByName("ROLE_SOCIO");
                            if (socioRole.isPresent()) {
                                Set<Role> roles = new HashSet<>();
                                roles.add(socioRole.get());
                                usuario.setRoles(roles);
                            } else {
                                System.err.println(
                                        "Role 'ROLE_SOCIO' not found. Please ensure it exists in the database.");
                                // Handle this error appropriately, e.g., throw an exception or create the role
                            }
                            usuarioRepository.save(usuario);
                        }
                    }

                } catch (Exception e) {
                    errorCount++;
                    System.err.println("Error processing CSV line: " + line + " - " + e.getMessage());
                }
            }
        } catch (IOException e) {
            message = "Erro ao ler o arquivo CSV: " + e.getMessage();
            errorCount = -1; // Indicate a file reading error
            System.err.println(message);
        }

        return new SocioImportResultDTO(insertedCount, updatedCount, errorCount, message);
    }

    private String mapGrauFromCsv(String grauCsv) {
        switch (grauCsv) {
            case "QM":
                return "Quadro de Mestre";
            case "CDC":
                return "Corpo do Conselho";
            case "CI":
                return "Corpo Instrutivo";
            case "QS":
                return "Quadro de Sócio";
            default:
                return "Quadro de Sócio"; // Default to a valid grau if not recognized
        }
    }
}
