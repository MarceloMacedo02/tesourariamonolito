package br.com.sigest.tesouraria.controller;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.sigest.tesouraria.domain.entity.Cobranca;
import br.com.sigest.tesouraria.domain.entity.ContaFinanceira;
import br.com.sigest.tesouraria.domain.entity.Socio;
import br.com.sigest.tesouraria.domain.enums.StatusSocio;
import br.com.sigest.tesouraria.domain.enums.TipoCobranca;
import br.com.sigest.tesouraria.dto.CobrancaDTO;
import br.com.sigest.tesouraria.dto.ContaReceberDto;
import br.com.sigest.tesouraria.dto.PagamentoLoteRequestDto;
import br.com.sigest.tesouraria.dto.PagamentoRequestDto;
import br.com.sigest.tesouraria.exception.RegraNegocioException;
import br.com.sigest.tesouraria.service.CobrancaService;
import br.com.sigest.tesouraria.service.ContaFinanceiraService;
import br.com.sigest.tesouraria.service.RubricaService;
import br.com.sigest.tesouraria.service.SocioService;
import jakarta.validation.Valid;

/**
 * Controlador para o módulo de Cobrança.
 */
@Controller
@RequestMapping("/cobrancas")
public class CobrancaController {

    private static final Logger logger = LoggerFactory.getLogger(CobrancaController.class);

    @Autowired
    private CobrancaService cobrancaService;

    @Autowired
    private SocioService socioService;

    @Autowired
    private RubricaService rubricaService;

    @Autowired
    private ContaFinanceiraService contaFinanceiraService;

    /**
     * Renderiza a página de registro de pagamento.
     *
     * @return o nome da view de detalhes da transação
     */
    @GetMapping("/transacoes/registrar-pagamento")
    public String registrarPagamentoPage() {
        return "transacoes/detalhes"; // This now points to the repurposed HTML
    }

    /**
     * Retorna todos os sócios.
     *
     * @return uma lista de todos os sócios
     */
    @GetMapping("/api/socios/all")
    @ResponseBody
    public List<Socio> getAllSocios() {
        return socioService.findAll();
    }

    /**
     * Retorna todas as contas financeiras.
     *
     * @return uma lista de todas as contas financeiras
     */
    @GetMapping("/api/contas-financeiras/all")
    @ResponseBody
    public List<ContaFinanceira> getAllContaFinanceiras() {
        return contaFinanceiraService.findAll();
    }

    /**
     * Retorna as cobranças em aberto de um sócio e seus dependentes.
     *
     * @param socioId o ID do sócio
     * @return uma lista de cobranças em aberto
     */
    @GetMapping("/api/cobrancas/aberto-por-socio/{socioId}")
    @ResponseBody
    public List<Cobranca> getOpenCobrancasBySocio(@PathVariable Long socioId) {
        return cobrancaService.findOpenCobrancasBySocioAndDependents(socioId);
    }

    /**
     * Exibe o formulário para gerar mensalidades.
     *
     * @param model o modelo para a view
     * @return o nome da view do formulário de mensalidade
     */
    @GetMapping("/gerar-mensalidade")
    public String gerarMensalidadeForm(Model model) {
        // Busca todos os sócios com status "FREQUENTE" e adiciona ao modelo
        model.addAttribute("sociosFrequentes", socioService.findSociosByStatus(StatusSocio.FREQUENTE));
        return "cobrancas/form-mensalidade";
    }

    /**
     * Salva as mensalidades geradas.
     *
     * @param sociosIds a string com os IDs dos sócios
     * @param mes       o mês da mensalidade
     * @param ano       o ano da mensalidade
     * @param redirect  os atributos de redirecionamento
     * @return o redirecionamento para a lista de cobranças
     */
    @PostMapping("/salvar-mensalidade")
    public String salvarMensalidade(@RequestParam("sociosIds") String sociosIds,
            @RequestParam("mes") int mes,
            @RequestParam("ano") int ano,
            RedirectAttributes redirect) {
        try {
            // Converte a string de IDs para uma lista de Long
            List<Long> ids = Arrays.stream(sociosIds.split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());

            cobrancaService.gerarCobrancaMensalidade(ids, mes, ano);
            redirect.addFlashAttribute("success", "Cobranças geradas com sucesso!");
        } catch (Exception e) {
            logger.error("Erro ao gerar cobranças: {}", e.getMessage());
            redirect.addFlashAttribute("error", "Erro ao gerar cobranças: " + e.getMessage());
        }
        return "redirect:/cobrancas";
    }

    /**
     * Lista as cobranças com base em um filtro.
     *
     * @param filtro o DTO com os filtros
     * @param model  o modelo para a view
     * @return o nome da view de lista de cobranças
     */
    @GetMapping
    public String listar(@ModelAttribute("filtro") CobrancaDTO filtro, Model model) {
        logger.info("Acessando a página de listagem de cobranças com filtros: {}", filtro);

        var cobrancasFiltradas = cobrancaService.filtrar(filtro);

        model.addAttribute("cobrancasMensalidade", cobrancasFiltradas.stream()
                .filter(c -> c.getTipoCobranca() == TipoCobranca.MENSALIDADE)
                .toList());

        model.addAttribute("cobrancasRubricas", cobrancasFiltradas.stream()
                .filter(c -> c.getTipoCobranca() == TipoCobranca.OUTRAS_RUBRICAS)
                .toList());

        model.addAttribute("filtro", filtro);
        return "cobrancas/lista";
    }

    /**
     * Exibe o formulário para nova cobrança de mensalidade.
     *
     * @param model o modelo para a view
     * @return o nome da view do formulário de mensalidade
     */
    @GetMapping("/novo/mensalidade")
    public String formMensalidade(Model model) {
        logger.info("Acessando a página de criação de nova cobrança de mensalidade.");

        // Agora, este método carrega a lista de sócios frequentes, como a lógica exige.
        model.addAttribute("sociosFrequentes", socioService.findSociosByStatus(StatusSocio.FREQUENTE));

        model.addAttribute("cobrancaDto", CobrancaDTO.builder()
                .tipoCobranca(TipoCobranca.MENSALIDADE)
                .status(br.com.sigest.tesouraria.domain.enums.StatusCobranca.ABERTA)
                .dataVencimento(LocalDate.now())
                .dataPagamento(null)
                .sociosIds(null)
                .socioId(null)
                .nomeSocio(null)
                .inicio(null)
                .fim(null)
                .valor(0.0f)
                .build());
        return "cobrancas/form-mensalidade";
    }

    /**
     * Exibe o formulário para nova cobrança de rubrica.
     *
     * @param model o modelo para a view
     * @return o nome da view do formulário de rubrica
     */
    @GetMapping("/novo/rubrica")
    public String formRubrica(Model model) {
        logger.info("Acessando a página de criação de nova cobrança por rubrica.");
        model.addAttribute("cobrancaDto", CobrancaDTO.builder()
                .tipoCobranca(TipoCobranca.OUTRAS_RUBRICAS)
                .status(br.com.sigest.tesouraria.domain.enums.StatusCobranca.ABERTA)
                .dataVencimento(LocalDate.now())
                .dataPagamento(null)
                .sociosIds(null)
                .socioId(null)
                .nomeSocio(null)
                .inicio(null)
                .fim(null)
                .valor(0.0f)
                .build());
        model.addAttribute("socios", socioService.findAll());
        model.addAttribute("rubricas", rubricaService.findAll());
        return "cobrancas/form-rubrica";
    }

    /**
     * Salva uma cobrança de mensalidade a partir de um DTO.
     *
     * @param dto      o DTO da cobrança
     * @param result   o resultado da validação
     * @param redirect os atributos de redirecionamento
     * @return o redirecionamento para a lista de cobranças
     */
    @PostMapping("/salvar-mensalidade-dto")
    public String salvarMensalidadeDto(@Valid @ModelAttribute("cobrancaDto") CobrancaDTO dto, BindingResult result,
            RedirectAttributes redirect) {
        // Validação adicional para a lista de sócios
        if (dto.getSociosIds() == null || dto.getSociosIds().isEmpty()) {
            result.rejectValue("sociosIds", "error.cobrancaDto", "Selecione pelo menos um sócio.");
        }

        if (result.hasErrors()) {
            logger.warn("Tentativa de salvar mensalidade com erros de validação.");
            redirect.addFlashAttribute("warning", "Verifique os campos obrigatórios.");
            // O retorno deve ser para a URL que carrega os dados corretamente, ou seja,
            // /novo/mensalidade
            return "redirect:/cobrancas/novo/mensalidade";
        }
        try {
            cobrancaService.gerarCobrancaMensalidadeManual(dto.getSociosIds());
            logger.info("Mensalidade salva com sucesso para os sócios com ID's: {}", dto.getSociosIds());
            redirect.addFlashAttribute("success", "Cobrança de mensalidade gerada com sucesso!");
        } catch (Exception e) {
            logger.error("Erro ao salvar a mensalidade: {}", e.getMessage());
            redirect.addFlashAttribute("error", "Erro ao gerar cobrança: " + e.getMessage());
        }
        return "redirect:/cobrancas";
    }

    /**
     * Salva uma cobrança de rubrica.
     *
     * @param dto      o DTO da cobrança
     * @param result   o resultado da validação
     * @param redirect os atributos de redirecionamento
     * @return o redirecionamento para a lista de cobranças
     */
    @PostMapping("/salvar-rubrica")
    public String salvarRubrica(@Valid @ModelAttribute("cobrancaDto") CobrancaDTO dto, BindingResult result,
            RedirectAttributes redirect) {
        if (result.hasErrors()) {
            logger.warn("Tentativa de salvar rubrica com erros de validação.");
            redirect.addFlashAttribute("warning", "Verifique os campos obrigatórios.");
            return "redirect:/cobrancas/novo/rubrica";
        }
        try {
            cobrancaService.gerarCobrancaOutrasRubricas(dto);
            logger.info("Cobrança por rubrica salva com sucesso para o sócio com ID: {}", dto.getSocioId());
            redirect.addFlashAttribute("success", "Cobrança por rubrica gerada com sucesso!");
        } catch (Exception e) {
            logger.error("Erro ao salvar a cobrança por rubrica: {}", e.getMessage());
            redirect.addFlashAttribute("error", "Erro ao gerar cobrança: " + e.getMessage());
        }
        return "redirect:/cobrancas";
    }

    /**
     * Cria uma nova cobrança.
     *
     * @param dto o DTO da cobrança
     * @return uma resposta com o status da operação
     */
    @PostMapping("/criar")
    @ResponseBody
    public ResponseEntity<?> criarCobranca(@RequestBody CobrancaDTO dto) {
        try {
            if (dto.getTipoCobranca() == TipoCobranca.OUTRAS_RUBRICAS) {
                cobrancaService.gerarCobrancaOutrasRubricas(dto);
            } else {
                ContaReceberDto contaReceberDto = new ContaReceberDto();
                contaReceberDto.setPagador(dto.getPagador());
                contaReceberDto.setDescricao(dto.getDescricao());
                contaReceberDto.setValor(dto.getValor() != null ? dto.getValor().floatValue() : 0.0f);
                contaReceberDto.setDataVencimento(dto.getDataVencimento());
                contaReceberDto.setRubricaId(dto.getRubricaId());
                contaReceberDto.setSocioId(dto.getSocioId()); // Add socioId
                cobrancaService.criarContaReceber(contaReceberDto);
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Cria uma pré-cobrança.
     *
     * @param dto o DTO da cobrança
     * @return uma resposta com a pré-cobrança criada
     */
    @PostMapping("/pre-criar")
    @ResponseBody
    public ResponseEntity<?> criarPreCobranca(@RequestBody CobrancaDTO dto) {
        try {
            Cobranca preCobranca = cobrancaService.criarPreCobranca(dto);
            // Return the created preCobranca with socioName if available
            CobrancaDTO responseDto = CobrancaDTO.builder()
                    .id(preCobranca.getId())
                    .descricao(preCobranca.getDescricao())
                    .dataVencimento(preCobranca.getDataVencimento())
                    .valor(preCobranca.getValor() != null ? preCobranca.getValor().floatValue() : 0.0F)
                    .tipoCobranca(preCobranca.getTipoCobranca())
                    .socioId(preCobranca.getSocio() != null ? preCobranca.getSocio().getId() : null)
                    .nomeSocio(preCobranca.getSocio() != null ? preCobranca.getSocio().getNome() : null)
                    .build();
            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Exibe o formulário para pagar mensalidades de um sócio.
     *
     * @param socioId  o ID do sócio
     * @param model    o modelo para a view
     * @param redirect os atributos de redirecionamento
     * @return o nome da view do formulário de pagamento
     */
    @GetMapping("/pagar-mensalidades/{socioId}")
    public String pagarMensalidades(@PathVariable Long socioId, Model model, RedirectAttributes redirect) {
        logger.info("Acessando a página de registro de pagamento de mensalidades para o sócio com ID: {}", socioId);

        Socio socio = socioService.findByIdWithDependentes(socioId);
        if (socio == null) {
            redirect.addFlashAttribute("error", "Sócio não encontrado.");
            return "redirect:/cobrancas";
        }

        List<Cobranca> allOpenCobrancas = cobrancaService.findOpenCobrancasBySocioAndDependents(socioId);

        model.addAttribute("socio", socio);
        model.addAttribute("allOpenCobrancas", allOpenCobrancas);
        model.addAttribute("contas", contaFinanceiraService.findAll());
        model.addAttribute("pagamentoDto", PagamentoLoteRequestDto.builder()
                .dataPagamento(LocalDate.now())
                .build());

        return "cobrancas/form-pagamento";
    }

    /**
     * Exclui uma cobrança.
     *
     * @param id o ID da cobrança
     * @return uma resposta com o status da operação
     */
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        logger.info("Tentativa de exclusão da cobrança com ID: {}", id);
        try {
            cobrancaService.excluir(id);
            logger.info("Cobrança com ID {} excluída com sucesso.", id);
            return ResponseEntity.ok().body(Map.of("message", "Cobrança excluída com sucesso!"));
        } catch (Exception e) {
            logger.error("Erro ao excluir a cobrança com ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", "Erro ao excluir a cobrança: " + e.getMessage()));
        }
    }

    /**
     * Exclui uma cobrança.
     *
     * @param id       o ID da cobrança
     * @param redirect os atributos de redirecionamento
     * @return o redirecionamento para a lista de cobranças
     */
    @GetMapping("/excluir/{id}")
    public String excluirCobranca(@PathVariable Long id, RedirectAttributes redirect) {
        logger.info("Tentativa de exclusão da cobrança com ID: {}", id);
        try {
            cobrancaService.excluir(id);
            logger.info("Cobrança com ID {} excluída com sucesso.", id);
            redirect.addFlashAttribute("success", "Cobrança excluída com sucesso!");
        } catch (Exception e) {
            logger.error("Erro ao excluir a cobrança com ID {}: {}", id, e.getMessage());
            redirect.addFlashAttribute("error", "Erro ao excluir a cobrança: " + e.getMessage());
        }
        return "redirect:/cobrancas";
    }

    /**
     * Registra o pagamento de uma cobrança.
     *
     * @param id           o ID da cobrança
     * @param pagamentoDto o DTO do pagamento
     * @param result       o resultado da validação
     * @param redirect     os atributos de redirecionamento
     * @return o redirecionamento para a lista de cobranças
     */
    @PostMapping("/registrar-pagamento/{id}")
    public String registrarPagamento(@PathVariable Long id,
            @Valid @ModelAttribute("pagamentoDto") PagamentoRequestDto pagamentoDto,
            BindingResult result, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            logger.warn("Tentativa de registrar pagamento com erros de validação para a cobrança com ID: {}", id);
            redirect.addFlashAttribute("warning", "Verifique os campos obrigatórios.");
            return "redirect:/cobrancas/pagar/" + id;
        }
        try {
            cobrancaService.registrarRecebimento(id, pagamentoDto);
            logger.info("Pagamento registrado com sucesso para a cobrança com ID: {}", id);
            redirect.addFlashAttribute("success", "Pagamento registrado com sucesso!");
        } catch (Exception e) {
            logger.error("Erro ao registrar pagamento para a cobrança com ID {}: {}", id, e.getMessage());
            redirect.addFlashAttribute("error", "Erro ao registrar pagamento: " + e.getMessage());
        }
        return "redirect:/cobrancas";
    }

    /**
     * Exibe os detalhes de uma cobrança.
     *
     * @param id    o ID da cobrança
     * @param model o modelo para a view
     * @return o nome da view de detalhes da cobrança
     */
    @GetMapping("/detalhe/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        logger.info("Acessando a página de detalhes da cobrança com ID: {}", id);
        model.addAttribute("cobranca", cobrancaService.findById(id));
        return "cobrancas/detalhe";
    }

    /**
     * Exibe o formulário para pagamento em lote.
     *
     * @param cobrancaIds a lista de IDs das cobranças
     * @param model       o modelo para a view
     * @param redirect    os atributos de redirecionamento
     * @return o nome da view do formulário de pagamento em lote
     */
    @GetMapping("/pagar-lote")
    public String pagarLoteForm(@RequestParam(value = "cobrancaIds", required = false) List<Long> cobrancaIds,
            Model model, RedirectAttributes redirect) {
        if (cobrancaIds == null || cobrancaIds.isEmpty()) {
            redirect.addFlashAttribute("error", "Nenhuma cobrança selecionada para pagamento em lote.");
            return "redirect:/cobrancas";
        }
        logger.info("Acessando a página de pagamento em lote para as cobranças com IDs: {}", cobrancaIds);

        // 1. Retrieve Cobranca objects
        List<Cobranca> cobrancas = cobrancaIds.stream()
                .map(id -> cobrancaService.findById(id))
                .collect(Collectors.toList());

        // 2. Calculate total
        double total = cobrancas.stream()
                .mapToDouble(c -> c.getValor() != null ? c.getValor().doubleValue() : 0.0)
                .sum();

        // 3. Populate pagamentoLoteDto
        PagamentoLoteRequestDto pagamentoLoteDto = PagamentoLoteRequestDto.builder().build();
        pagamentoLoteDto.setCobrancaIds(cobrancaIds); // Set the cobrancaIds in the DTO

        // 4. Add to Model
        model.addAttribute("cobrancas", cobrancas); // Add the list of Cobranca objects
        model.addAttribute("total", total); // Add the calculated total
        model.addAttribute("contas", contaFinanceiraService.findAll());
        model.addAttribute("pagamentoLoteDto", pagamentoLoteDto); // Use the populated DTO

        return "cobrancas/form-pagamento-lote";
    }

    /**
     * Registra o pagamento em lote.
     *
     * @param pagamentoDto o DTO do pagamento em lote
     * @return uma resposta com o status da operação
     */
    @PostMapping("/registrar-pagamento-lote")
    @ResponseBody
    public ResponseEntity<?> registrarPagamentoLote(@RequestBody PagamentoLoteRequestDto pagamentoDto) {
        try {
            cobrancaService.quitarMensalidadesEmLote(pagamentoDto);
            return ResponseEntity.ok().build();
        } catch (RegraNegocioException e) {
            logger.error("Erro de regra de negócio ao quitar mensalidades em lote: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Erro inesperado ao quitar mensalidades em lote: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Ocorreu um erro interno ao quitar as mensalidades."));
        }
    }

    /**
     * Quita cobranças em lote.
     *
     * @param pagamentoDto o DTO do pagamento em lote
     * @return uma resposta com o status da operação
     */
    @PostMapping("/quitarEmLote")
    @ResponseBody
    public ResponseEntity<?> quitarCobrancasEmLote(@RequestBody PagamentoLoteRequestDto pagamentoDto) {
        try {
            cobrancaService.quitarCobrancasEmLote(pagamentoDto);
            return ResponseEntity.ok().body(Map.of("message", "Cobranças quitadas com sucesso!"));
        } catch (RegraNegocioException e) {
            logger.error("Erro de regra de negócio ao quitar cobranças em lote: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Erro inesperado ao quitar cobranças em lote: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Ocorreu um erro interno ao quitar as cobranças."));
        }
    }

    /**
     * Cria uma nova despesa.
     *
     * @param dto o DTO da cobrança
     * @return uma resposta com o status da operação
     */
    @PostMapping("/criarNovaDespesa")
    @ResponseBody
    public ResponseEntity<?> criarNovaDespesa(@RequestParam("comprovante") MultipartFile comprovante,
            @ModelAttribute CobrancaDTO dto) {
        try {
            // Verifica se o DTO foi enviado corretamente
            if (dto == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Dados da cobrança não foram enviados."));
            }

            // Verifica se os campos obrigatórios estão presentes
            if (dto.getTransacaoId() == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "ID da transação é obrigatório."));
            }

            if (dto.getFornecedorId() == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "ID do fornecedor é obrigatório."));
            }

            if (dto.getDescricao() == null || dto.getDescricao().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Descrição é obrigatória."));
            }

            if (dto.getValor() == null || dto.getValor() <= 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Valor é obrigatório e deve ser maior que zero."));
            }

            if (dto.getDataVencimento() == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Data de vencimento é obrigatória."));
            }

            if (dto.getRubricaId() == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "ID da rubrica é obrigatório."));
            }

            // Cria uma nova cobrança de despesa com status ABERTA e tipo OUTRAS_RUBRICAS
            cobrancaService.criarNovaDespesa(dto, comprovante);
            return ResponseEntity.ok().body(Map.of("message", "Nova cobrança de despesa criada com sucesso!"));
        } catch (RegraNegocioException e) {
            logger.error("Erro de regra de negócio ao criar nova cobrança de despesa: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Erro inesperado ao criar nova cobrança de despesa: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Ocorreu um erro interno ao criar a nova cobrança de despesa."));
        }
    }
}
