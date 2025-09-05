package br.com.sigest.tesouraria.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.com.sigest.tesouraria.domain.entity.CentroCusto;
import br.com.sigest.tesouraria.domain.entity.Role;
import br.com.sigest.tesouraria.domain.entity.Rubrica;
import br.com.sigest.tesouraria.domain.entity.Socio;
import br.com.sigest.tesouraria.domain.entity.Usuario;
import br.com.sigest.tesouraria.domain.enums.GrauSocio;
import br.com.sigest.tesouraria.domain.enums.StatusSocio;
import br.com.sigest.tesouraria.domain.enums.TipoRubrica;
import br.com.sigest.tesouraria.repository.CentroCustoRepository;
import br.com.sigest.tesouraria.repository.RoleRepository;
import br.com.sigest.tesouraria.repository.RubricaRepository;
import br.com.sigest.tesouraria.repository.SocioRepository;
import br.com.sigest.tesouraria.repository.UsuarioRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final SocioRepository socioRepository;
    private final CentroCustoRepository centroCustoRepository;
    private final RubricaRepository rubricaRepository;

    public DataInitializer(UsuarioRepository usuarioRepository, RoleRepository roleRepository,
            PasswordEncoder passwordEncoder, SocioRepository socioRepository,
            CentroCustoRepository centroCustoRepository, RubricaRepository rubricaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.socioRepository = socioRepository;
        this.centroCustoRepository = centroCustoRepository;
        this.rubricaRepository = rubricaRepository;
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
        if (usuarioRepository.findByUsername("tesoureiro").isEmpty()) {
            Usuario tesoureiro = new Usuario();
            tesoureiro.setUsername("tesoureiro");
            tesoureiro.setPassword(passwordEncoder.encode("tesoureiro"));
            tesoureiro.setRoles(Set.of(tesoureiroRole));
            usuarioRepository.save(tesoureiro);
        }

        // Criar Sócio e usuário Sócio para teste
        if (socioRepository.findByCpf("111.222.333-44").isEmpty())
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

        // Criar Centros de Custo
        createCentroCustoIfNotExists("TAXA DE ENCONTRO DOS PAIS");
        createCentroCustoIfNotExists("REPASSE REGIONAL");
        createCentroCustoIfNotExists("REPASSE NACIONAL");
        createCentroCustoIfNotExists("TARIFAS BANCÁRIAS - SAQUE");
        createCentroCustoIfNotExists("DESPESAS DE MANUTENÇÃO");
        createCentroCustoIfNotExists("IMPOSTO ISS");
        createCentroCustoIfNotExists("CONCESSIONÁRIA DE ENERGIA ELÉTRICA");
        createCentroCustoIfNotExists("DESPESAS DE CONSTRUÇÃO");
        createCentroCustoIfNotExists("BENEFICÊNCIA - CESTA ALIMENTAÇÃO - ZELADOR");
        createCentroCustoIfNotExists("MANUTENÇÃO UNIDADE");

        // Criar Rubricas
        createRubricaIfNotExists("Fundo Regional", TipoRubrica.DESPESA, 1.60f, "REPASSE REGIONAL");
        createRubricaIfNotExists("Plantio", TipoRubrica.DESPESA, 4.80f, "REPASSE NACIONAL");
        createRubricaIfNotExists("Beneficência", TipoRubrica.DESPESA, 5.30f, "REPASSE NACIONAL");
        createRubricaIfNotExists("Fundo de Reserva", TipoRubrica.DESPESA, 0.30f, "REPASSE NACIONAL");
        createRubricaIfNotExists("Mensalidade", TipoRubrica.DESPESA, 50.00f, "MANUTENÇÃO UNIDADE");
    }

    private void createCentroCustoIfNotExists(String nome) {
        if (centroCustoRepository.findByNome(nome).isEmpty()) {
            CentroCusto centroCusto = new CentroCusto();
            centroCusto.setNome(nome);
            centroCusto.setAtivo(true);
            centroCusto.setEntradas(0.0);
            centroCusto.setSaidas(0.0);
            centroCustoRepository.save(centroCusto);
        }
    }

    private void createRubricaIfNotExists(String nome, TipoRubrica tipo, Float valorPadrao, String centroCustoNome) {
        if (rubricaRepository.findByNome(nome).isEmpty()) {
            CentroCusto centroCusto = centroCustoRepository.findByNome(centroCustoNome)
                    .orElseThrow(() -> new RuntimeException("Centro de custo não encontrado: " + centroCustoNome));
            Rubrica rubrica = new Rubrica();
            rubrica.setNome(nome);
            rubrica.setTipo(tipo);
            rubrica.setValorPadrao(valorPadrao);
            rubrica.setCentroCusto(centroCusto);
            rubricaRepository.save(rubrica);
        }
    }
}
