package br.com.sigest.tesouraria.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import br.com.sigest.tesouraria.domain.entity.Endereco;
import br.com.sigest.tesouraria.domain.entity.Role;
import br.com.sigest.tesouraria.domain.entity.Socio;
import br.com.sigest.tesouraria.domain.entity.Usuario;
import br.com.sigest.tesouraria.domain.enums.StatusSocio;
import br.com.sigest.tesouraria.repository.RoleRepository;
import br.com.sigest.tesouraria.repository.SocioRepository;
import br.com.sigest.tesouraria.repository.UsuarioRepository;

@Service
public class SocioImportService {

    @Autowired
    private SocioRepository socioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public ImportResult processCsvFile(MultipartFile file) throws Exception {
        int insertedCount = 0;
        int updatedCount = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                // Handle potential commas within fields by using a more robust CSV parser if
                // available,
                // or by carefully splitting and trimming. For now, assuming simple comma split.
                String[] fields = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // Split by comma, ignore commas
                                                                                   // inside quotes

                // Expected 14 fields based on the provided CSV example
                if (fields.length < 14) {
                    System.err.println("Malformed line (less than 14 fields): " + line);
                    continue;
                }

                // Map CSV fields to variables
                String grau = fields[0].trim();
                String nome = fields[1].trim();
                // fields[2] and fields[3] are "Dia Nasc" and "Mes Nasc" - not directly used for
                // LocalDate.parse
                String dataNascimentoStr = fields[4].trim(); // "Nascimento" field
                String cpf = fields[5].trim();
                String statusCsv = fields[6].trim();
                String celular = fields[7].trim();
                String telefoneResidencial = fields[8].trim();
                String email = fields[9].trim(); // "Email" field
                String emailAlternativo = fields[10].trim(); // "Email Alternativo" field
                String enderecoResidencialFull = fields[11].trim(); // "Endereço Residencial" field
                // fields[12] and fields[13] are "Endereço Comercial" and "Endereço Outro" -
                // ignored for now

                Optional<Socio> existingSocio = socioRepository.findByCpf(cpf);

                Socio socio;
                if (existingSocio.isPresent()) {
                    socio = existingSocio.get();
                    updatedCount++;
                } else {
                    // If not found by CPF, try by name (LIKE)
                    Optional<Socio> socioByName = socioRepository.findTopByNomeContainingIgnoreCase(nome);
                    if (socioByName.isPresent()) {
                        socio = socioByName.get();
                        updatedCount++;
                    } else {
                        socio = new Socio();
                        socio.setDataCadastro(LocalDate.now()); // Set registration date for new socios
                        insertedCount++;

                        // Create associated user for new socio
                        Usuario socioUser = new Usuario();
                        // Use emailAlternativo if available, otherwise the primary email, or a default
                        // based on CPF
                        String username = emailAlternativo.isEmpty() ? (email.isEmpty() ? cpf + "@sigest.com" : email)
                                : emailAlternativo;
                        socioUser.setPassword(passwordEncoder.encode("socio123")); // Default password
                        Role socioRole = roleRepository.findByName("ROLE_SOCIO")
                                .orElseThrow(() -> new RuntimeException("ROLE_SOCIO not found"));
                        socioUser.setRoles(Set.of(socioRole));
                        usuarioRepository.save(socioUser);
                        socio.setUsuario(socioUser);
                    }
                }

                socio.setNome(nome);
                socio.setCpf(cpf);
                socio.setGrau(grau);
                socio.setEmailAlternativo(emailAlternativo);
                socio.setCelular(celular);
                socio.setTelefoneResidencial(telefoneResidencial);

                // Parse dataNascimento
                if (!dataNascimentoStr.isEmpty()) {
                    try {
                        socio.setDataNascimento(
                                LocalDate.parse(dataNascimentoStr, DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    } catch (DateTimeParseException e) {
                        System.err.println("Invalid date format for socio " + nome + ": " + dataNascimentoStr
                                + ". Setting to null.");
                        socio.setDataNascimento(null); // Or handle as appropriate
                    }
                }

                // Rule for Status
                if ("FREQUENTE".equalsIgnoreCase(statusCsv)) {
                    socio.setStatus(StatusSocio.FREQUENTE);
                } else {
                    socio.setStatus(StatusSocio.AFASTADO);
                }

                // Handle Endereco parsing from "Endereço Residencial"
                Endereco endereco = new Endereco();
                String cep = "";
                String logradouro = enderecoResidencialFull; // Default to full string

                // Attempt to extract CEP using regex (e.g., XXXXX-XXX or XXXXXXXX)
                Pattern cepPattern = Pattern.compile("(\\d{5}-\\d{3}|\\d{8})");
                Matcher cepMatcher = cepPattern.matcher(enderecoResidencialFull);
                if (cepMatcher.find()) {
                    cep = cepMatcher.group(1);
                    // Remove CEP from the full address string for logradouro
                    logradouro = enderecoResidencialFull.replace(cepMatcher.group(0), "").trim();
                    // Clean up trailing commas or spaces after removing CEP
                    if (logradouro.endsWith(",")) {
                        logradouro = logradouro.substring(0, logradouro.length() - 1).trim();
                    }
                }

                // Further parsing of logradouro, numero, complemento, bairro, cidade, estado
                // from 'logradouro'
                // This is highly dependent on the address format and can be very complex.
                // For simplicity, I'll assign the remaining 'logradouro' to the logradouro
                // field
                // and leave other address fields (numero, complemento, bairro, cidade, estado)
                // as empty.
                // If more granular parsing is needed, a dedicated address parsing library or
                // a more structured CSV format for addresses would be required.

                endereco.setCep(cep);
                endereco.setLogradouro(logradouro);
                endereco.setNumero(""); // Cannot reliably parse from current format
                endereco.setComplemento(""); // Cannot reliably parse from current format
                endereco.setBairro(""); // Cannot reliably parse from current format
                endereco.setCidade(""); // Cannot reliably parse from current format
                endereco.setEstado(""); // Cannot reliably parse from current format

                socioRepository.save(socio);
            }
        }
        return new ImportResult(insertedCount, updatedCount);
    }

    public static class ImportResult {
        private final int insertedCount;
        private final int updatedCount;

        public ImportResult(int insertedCount, int updatedCount) {
            this.insertedCount = insertedCount;
            this.updatedCount = updatedCount;
        }

        public int getInsertedCount() {
            return insertedCount;
        }

        public int getUpdatedCount() {
            return updatedCount;
        }
    }
}
