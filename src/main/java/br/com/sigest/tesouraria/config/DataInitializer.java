package br.com.sigest.tesouraria.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import br.com.sigest.tesouraria.domain.entity.CentroCusto;
import br.com.sigest.tesouraria.domain.entity.ContaFinanceira;
import br.com.sigest.tesouraria.domain.entity.GrupoMensalidade;
import br.com.sigest.tesouraria.domain.entity.Role;
import br.com.sigest.tesouraria.domain.entity.Rubrica;
import br.com.sigest.tesouraria.domain.entity.Socio;
import br.com.sigest.tesouraria.domain.entity.Usuario;
import br.com.sigest.tesouraria.domain.enums.GrauSocio;
import br.com.sigest.tesouraria.domain.enums.StatusSocio;
import br.com.sigest.tesouraria.domain.enums.TipoRubrica;
import br.com.sigest.tesouraria.domain.repository.CentroCustoRepository;
import br.com.sigest.tesouraria.domain.repository.ContaFinanceiraRepository;
import br.com.sigest.tesouraria.domain.repository.GrupoMensalidadeRepository;
import br.com.sigest.tesouraria.domain.repository.InstituicaoRepository;
import br.com.sigest.tesouraria.domain.repository.RoleRepository;
import br.com.sigest.tesouraria.domain.repository.RubricaRepository;
import br.com.sigest.tesouraria.domain.repository.SocioRepository;
import br.com.sigest.tesouraria.domain.repository.UsuarioRepository;

//@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final SocioRepository socioRepository;
    private final CentroCustoRepository centroCustoRepository;
    private final RubricaRepository rubricaRepository;
    private final GrupoMensalidadeRepository grupoMensalidadeRepository;
    private final InstituicaoRepository instituicaoRepository;
    private final ContaFinanceiraRepository contaFinanceiraRepository;

    /**
     * Construtor da classe DataInitializer.
     *
     * @param usuarioRepository           o repositório de usuários
     * @param roleRepository              o repositório de roles
     * @param passwordEncoder             o codificador de senhas
     * @param socioRepository             o repositório de sócios
     * @param centroCustoRepository       o repositório de centros de custo
     * @param rubricaRepository           o repositório de rubricas
     * @param grupoMensalidadeRepository  o repositório de grupos de mensalidade
     * @param instituicaoRepository       o repositório de instituições
     * @param contaFinanceiraRepository   o repositório de contas financeiras
     */
    public DataInitializer(UsuarioRepository usuarioRepository, RoleRepository roleRepository,
            PasswordEncoder passwordEncoder, SocioRepository socioRepository,
            CentroCustoRepository centroCustoRepository, RubricaRepository rubricaRepository,
            GrupoMensalidadeRepository grupoMensalidadeRepository,
            InstituicaoRepository instituicaoRepository, ContaFinanceiraRepository contaFinanceiraRepository) {
        this.usuarioRepository = usuarioRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.socioRepository = socioRepository;
        this.centroCustoRepository = centroCustoRepository;
        this.rubricaRepository = rubricaRepository;
        this.grupoMensalidadeRepository = grupoMensalidadeRepository;
        this.instituicaoRepository = instituicaoRepository;
        this.contaFinanceiraRepository = contaFinanceiraRepository;
    }

    /**
     * Executa a inicialização dos dados.
     *
     * @param args os argumentos da linha de comando
     * @throws Exception se ocorrer um erro
     */
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (roleRepository.count() > 0) {
            return; // Dados já foram inicializados
        }

        // 1. Criar Roles
        Role adminRole = roleRepository.save(new Role("ROLE_ADMIN"));
        Role tesoureiroRole = roleRepository.save(new Role("ROLE_TESOUREIRO"));
        Role socioRole = roleRepository.save(new Role("ROLE_SOCIO"));

        // 2. Criar Usuários Iniciais
        Usuario admin = new Usuario();
        admin.setUsername("admin@sigest.com");
        admin.setPassword(passwordEncoder.encode("admin"));
        admin.setRoles(Set.of(adminRole, tesoureiroRole));
        usuarioRepository.save(admin);

        Usuario tesoureiro = new Usuario();
        tesoureiro.setUsername("tesoureiro");
        tesoureiro.setPassword(passwordEncoder.encode("tesoureiro"));
        tesoureiro.setRoles(Set.of(tesoureiroRole));
        usuarioRepository.save(tesoureiro);

        // 3. Criar Conta Financeira
        ContaFinanceira contaCora = new ContaFinanceira();
        contaCora.setNome("BANCO CORA");
        // Usando BigDecimal para evitar problemas de precisão com valores monetários
        contaCora.setSaldoAtual(new java.math.BigDecimal("0.01"));
        contaFinanceiraRepository.save(contaCora);

        // 4. Criar Centros de Custo
        Map<String, CentroCusto> centrosDeCusto = createCentrosDeCusto();

        // 5. Criar Rubricas
        createRubricas(centrosDeCusto);

        // 6. Criar Grupos de Mensalidade
        Map<String, GrupoMensalidade> gruposMensalidade = createGruposDeMensalidade();

        // 7. Associar Rubricas aos Grupos de Mensalidade
        associateRubricasToGrupos(gruposMensalidade);

        // 8. Criar Sócios e Usuários de Sócios
        createSocios(gruposMensalidade, socioRole);
    }

    /**
     * Cria os centros de custo iniciais.
     *
     * @return um mapa com os centros de custo criados
     */
    private Map<String, CentroCusto> createCentrosDeCusto() {
        List<String> nomes = List.of("TAXA DE ENCONTRO DOS PAIS", "REPASSE REGIONAL", "REPASSE NACIONAL",
                "TARIFAS BANCÁRIAS - SAQUE", "DESPESAS DE MANUTENÇÃO", "IMPOSTO ISS",
                "CONCESSIONÁRIA DE ENERGIA ELÉTRICA", "CONSTRUÇÃO", "BENEFICÊNCIA - CESTA ALIMENTAÇÃO - ZELADOR",
                "MANUTENÇÃO UNIDADE", "ORIENTAÇÃO ESPIRITUAL");
        return nomes.stream().map(nome -> {
            CentroCusto cc = new CentroCusto();
            cc.setNome(nome);
            cc.setAtivo(true);
            return centroCustoRepository.save(cc);
        }).collect(Collectors.toMap(CentroCusto::getNome, Function.identity()));
    }

    /**
     * Cria as rubricas iniciais.
     *
     * @param centrosDeCusto um mapa com os centros de custo
     */
    private void createRubricas(Map<String, CentroCusto> centrosDeCusto) {
        List<Rubrica> rubricas = Arrays.asList(
                createRubrica("FUNDO REGIONAL", TipoRubrica.RECEITA, 1.60f, centrosDeCusto.get("REPASSE REGIONAL")),
                createRubrica("PLANTIO", TipoRubrica.RECEITA, 4.80f, centrosDeCusto.get("REPASSE NACIONAL")),
                createRubrica("BENEFICÊNCIA", TipoRubrica.RECEITA, 5.30f, centrosDeCusto.get("REPASSE NACIONAL")),
                createRubrica("FUNDO DE RESERVA", TipoRubrica.RECEITA, 0.30f, centrosDeCusto.get("REPASSE NACIONAL")),
                createRubrica("MENSALIDADE", TipoRubrica.RECEITA, 50.00f, centrosDeCusto.get("MANUTENÇÃO UNIDADE")),
                createRubrica("ORIENTAÇÃO ESPIRITUAL", TipoRubrica.RECEITA, 0.0f,
                        centrosDeCusto.get("ORIENTAÇÃO ESPIRITUAL")),
                createRubrica("TAXA DE ENCONTRO DOS PAIS", TipoRubrica.DESPESA, 0.0f,
                        centrosDeCusto.get("TAXA DE ENCONTRO DOS PAIS")),
                createRubrica("REPASSE REGIONAL", TipoRubrica.DESPESA, 0.0f, centrosDeCusto.get("REPASSE REGIONAL")),
                createRubrica("REPASSE NACIONAL", TipoRubrica.DESPESA, 0.0f, centrosDeCusto.get("REPASSE NACIONAL")),
                createRubrica("TARIFAS BANCÁRIAS - SAQUE", TipoRubrica.DESPESA, 0.0f,
                        centrosDeCusto.get("MANUTENÇÃO UNIDADE")),
                createRubrica("DESPESAS DE MANUTENÇÃO", TipoRubrica.DESPESA, 0.0f,
                        centrosDeCusto.get("MANUTENÇÃO UNIDADE")),
                createRubrica("IMPOSTO ISS", TipoRubrica.DESPESA, 0.0f, centrosDeCusto.get("IMPOSTO ISS")),
                createRubrica("CONCESSIONÁRIA DE ENERGIA ELÉTRICA", TipoRubrica.DESPESA, 0.0f,
                        centrosDeCusto.get("MANUTENÇÃO UNIDADE")),
                createRubrica("DESPESAS DE CONSTRUÇÃO", TipoRubrica.DESPESA, 0.0f, centrosDeCusto.get("CONSTRUÇÃO")),
                createRubrica("BENEFICÊNCIA - CESTA ALIMENTAÇÃO - ZELADOR", TipoRubrica.DESPESA, 0.0f,
                        centrosDeCusto.get("BENEFICÊNCIA - CESTA ALIMENTAÇÃO - ZELADOR")));
        rubricaRepository.saveAll(rubricas);
    }

    /**
     * Cria uma nova rubrica.
     *
     * @param nome        o nome da rubrica
     * @param tipo        o tipo da rubrica
     * @param valor       o valor padrão da rubrica
     * @param centroCusto o centro de custo da rubrica
     * @return a rubrica criada
     */
    private Rubrica createRubrica(String nome, TipoRubrica tipo, Float valor, CentroCusto centroCusto) {
        Rubrica r = new Rubrica();
        r.setNome(nome);
        r.setTipo(tipo);
        r.setValorPadrao(valor != null ? BigDecimal.valueOf(valor) : BigDecimal.ZERO);
        r.setCentroCusto(centroCusto);
        return r;
    }

    /**
     * Cria os grupos de mensalidade iniciais.
     *
     * @return um mapa com os grupos de mensalidade criados
     */
    private Map<String, GrupoMensalidade> createGruposDeMensalidade() {
        List<String> nomes = List.of("GRUPO MENSALIDADE BASICO - 135", "GRUPO MENSALIDADE BASICO OE - 145",
                "GRUPO MENSALIDADE MINIMO - 80", "GRUPO MENSALIDADE 80", "GRUPO MENSALIDADE MINIMO- 70",
                "GRUPO MENSALIDADE BASICO 120", "GRUPO MENSALidade MINIMO- 55",
                "GRUPO MENSALIDADE CESTA BENEFICENCIA - 165",
                "GRUPO MENSALIDADE SUPER MINIMO -30", "GRUPO MENSALIDADE BASICO 50", "GRUPO MENSALIDADE 150",
                "GRUPO MENSALIDADE 90", "tesoureiro");
        return nomes.stream().map(nome -> {
            GrupoMensalidade gm = new GrupoMensalidade();
            gm.setNome(nome);
            return grupoMensalidadeRepository.save(gm);
        }).collect(Collectors.toMap(GrupoMensalidade::getNome, Function.identity()));
    }

    /**
     * Associa as rubricas aos grupos de mensalidade.
     *
     * @param grupos um mapa com os grupos de mensalidade
     */
    private void associateRubricasToGrupos(Map<String, GrupoMensalidade> grupos) {
        // Lógica de associação omitida por complexidade de mapeamento de IDs
    }

    /**
     * Cria os sócios e usuários de sócios iniciais.
     *
     * @param grupos    um mapa com os grupos de mensalidade
     * @param socioRole a role de sócio
     */
    private void createSocios(Map<String, GrupoMensalidade> grupos, Role socioRole) {
        Socio socioTeste = new Socio();
        socioTeste.setNome("Sócio de Teste Completo");
        socioTeste.setCpf("11122233344");
        socioTeste.setDataCadastro(LocalDate.of(2025, 9, 5));
        socioTeste.setDataNascimento(LocalDate.of(1985, 5, 15));
        socioTeste.setGrau(GrauSocio.QM.getDescricao());
        socioTeste.setStatus(StatusSocio.FREQUENTE);
        socioTeste.setGrupoMensalidade(grupos.get("GRUPO MENSALIDADE 90"));
        socioTeste.setCelular("(86) 99999-8888");
        socioTeste.setTelefoneResidencial("(86) 3322-7777");

        Usuario userSocioTeste = new Usuario();
        userSocioTeste.setUsername("socio@teste.com");
        userSocioTeste.setPassword(passwordEncoder.encode("socio"));
        userSocioTeste.setRoles(Set.of(socioRole));
        userSocioTeste.setSocio(socioTeste);
        socioTeste.setUsuario(userSocioTeste);

        usuarioRepository.save(userSocioTeste);
        socioRepository.save(socioTeste);
    }
}
