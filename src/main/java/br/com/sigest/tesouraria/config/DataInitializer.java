package br.com.sigest.tesouraria.config;

import java.time.LocalDate;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import br.com.sigest.tesouraria.domain.entity.Role;
import br.com.sigest.tesouraria.domain.entity.Socio;
import br.com.sigest.tesouraria.domain.entity.Usuario;
import br.com.sigest.tesouraria.domain.enums.GrauSocio;
import br.com.sigest.tesouraria.domain.enums.StatusSocio;
import br.com.sigest.tesouraria.repository.RoleRepository;
import br.com.sigest.tesouraria.repository.SocioRepository;
import br.com.sigest.tesouraria.repository.UsuarioRepository;

// @Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final SocioRepository socioRepository;

    public DataInitializer(UsuarioRepository usuarioRepository, RoleRepository roleRepository,
            PasswordEncoder passwordEncoder, SocioRepository socioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.socioRepository = socioRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Criar Roles se não existirem
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));
        Role tesoureiroRole = roleRepository.findByName("ROLE_TESOUREIRO")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_TESOUREIRO")));
        Role socioRole = roleRepository.findByName("ROLE_SOCIO")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_SOCIO")));

        // Criar usuário Admin se não existir
        if (usuarioRepository.findByUsername("admin@sigest.com").isEmpty()) {
            Usuario admin = new Usuario();
            admin.setUsername("admin@sigest.com");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRoles(Set.of(adminRole, tesoureiroRole));
            usuarioRepository.save(admin);
        }

        // Criar usuário Tesoureiro se não existir
        if (usuarioRepository.findByUsername("tesoureiro@sigest.com").isEmpty()) {
            Usuario tesoureiro = new Usuario();
            tesoureiro.setUsername("tesoureiro");
            tesoureiro.setPassword(passwordEncoder.encode("tesoureiro"));
            tesoureiro.setRoles(Set.of(tesoureiroRole));
            usuarioRepository.save(tesoureiro);
        }

        // Criar Sócio e usuário Sócio para teste
        // if (socioRepository.findByCpf("111.222.333-44").isEmpty())
        {
            // Cria o usuário primeiro
            Usuario socioUser = new Usuario();
            socioUser.setUsername("socio@teste.com"); // Email de login
            socioUser.setPassword(passwordEncoder.encode("socio"));
            socioUser.setRoles(Set.of(socioRole));
            usuarioRepository.save(socioUser);

            // Cria o sócio e associa ao usuário
            Socio socio = new Socio();
            socio.setNome("Sócio de Teste Completo");
            socio.setCpf("111.222.333-44");
            socio.setGrau(GrauSocio.QS.getDescricao());
            socio.setStatus(StatusSocio.FREQUENTE);
            socio.setDataCadastro(LocalDate.now());

            // Preenchendo os novos campos
            socio.setDataNascimento(LocalDate.of(1985, 5, 15));
            socio.setEmailAlternativo("contato.secundario@email.com");
            socio.setCelular("(86) 99999-8888");
            socio.setTelefoneResidencial("(86) 3322-7777");

            // Associa o usuário ao sócio
            socio.setUsuario(socioUser);

            socioRepository.save(socio);
        }
    }
}
