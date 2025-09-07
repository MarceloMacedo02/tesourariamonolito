// br/com/sigest/tesouraria/controller/CobrancaController.java
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.sigest.tesouraria.domain.entity.Cobranca;
import br.com.sigest.tesouraria.domain.entity.ContaFinanceira; // Import ContaFinanceira
import br.com.sigest.tesouraria.domain.entity.Socio; // Import Socio
import br.com.sigest.tesouraria.domain.enums.StatusCobranca;
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

    // New method to render the payment registration page
    @GetMapping("/transacoes/registrar-pagamento")
    public String registrarPagamentoPage() {
        return "transacoes/detalhes"; // This now points to the repurposed HTML
    }

    // New REST endpoint to get all socios
    @GetMapping("/api/socios/all")
    @ResponseBody
    public List<Socio> getAllSocios() {
        return socioService.findAll();
    }

    // New REST endpoint to get all financial accounts
    @GetMapping("/api/contas-financeiras/all")
    @ResponseBody
    public List<ContaFinanceira> getAllContaFinanceiras() {
        return contaFinanceiraService.findAll();
    }

    // New REST endpoint to get open charges for a socio and their dependents
    @GetMapping("/api/cobrancas/aberto-por-socio/{socioId}")
    @ResponseBody
    public List<Cobranca> getOpenCobrancasBySocio(@PathVariable Long socioId) {
        return cobrancaService.findOpenCobrancasBySocioAndDependents(socioId);
    }

    @GetMapping("/gerar-mensalidade")
    public String gerarMensalidadeForm(Model model) {
        // Busca todos os sócios com status "FREQUENTE" e adiciona ao modelo
        model.addAttribute("sociosFrequentes", socioService.findSociosByStatus(StatusSocio.FREQUENTE));
        return "cobrancas/form-mensalidade";
    }

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

    @PostMapping("/criar")
    @ResponseBody
    public ResponseEntity<?> criarCobranca(@RequestBody CobrancaDTO dto) {
        try {
            if (dto.getSocioId() != null) {
                cobrancaService.gerarCobrancaOutrasRubricas(dto);
            } else {
                ContaReceberDto contaReceberDto = new ContaReceberDto();
                contaReceberDto.setPagador(dto.getPagador());
                contaReceberDto.setDescricao(dto.getDescricao());
                contaReceberDto.setValor(dto.getValor());
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
                    .valor(preCobranca.getValor())
                    .tipoCobranca(preCobranca.getTipoCobranca())
                    .socioId(preCobranca.getSocio() != null ? preCobranca.getSocio().getId() : null)
                    .nomeSocio(preCobranca.getSocio() != null ? preCobranca.getSocio().getNome() : null)
                    .build();
            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/pagar/{id}")
    public String pagar(@PathVariable Long id, @RequestParam(required = false) Long fromTitular, Model model) {
        logger.info("Acessando a página de registro de pagamento para a cobrança com ID: {}", id);
        Cobranca cobranca = cobrancaService.findByIdWithDependents(id);
        model.addAttribute("cobranca", cobranca);
        model.addAttribute("contas", contaFinanceiraService.findAll());
        model.addAttribute("pagamentoDto", PagamentoRequestDto.builder()
                .dataPagamento(LocalDate.now())
                .valor(cobranca.getValor())
                .build());
        if (fromTitular != null) {
            model.addAttribute("fromTitular", fromTitular);
        }

        // Carregar cobranças dos dependentes
        if (cobranca.getSocio() != null && cobranca.getSocio().getDependentes() != null) {
            List<Cobranca> cobrancasDependentes = cobranca.getSocio().getDependentes().stream()
                    .flatMap(dependente -> cobrancaService
                            .findBySocioIdAndStatus(dependente.getId(), StatusCobranca.ABERTA).stream())
                    .collect(Collectors.toList());
            model.addAttribute("cobrancasDependentes", cobrancasDependentes);
        }

        return "cobrancas/form-pagamento";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
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

    @GetMapping("/detalhe/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        logger.info("Acessando a página de detalhes da cobrança com ID: {}", id);
        model.addAttribute("cobranca", cobrancaService.findById(id));
        return "cobrancas/detalhe";
    }

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
                .mapToDouble(Cobranca::getValor)
                .sum();

        // 3. Populate pagamentoLoteDto
        PagamentoLoteRequestDto pagamentoLoteDto = new PagamentoLoteRequestDto();
        pagamentoLoteDto.setCobrancaIds(cobrancaIds); // Set the cobrancaIds in the DTO

        // 4. Add to Model
        model.addAttribute("cobrancas", cobrancas); // Add the list of Cobranca objects
        model.addAttribute("total", total); // Add the calculated total
        model.addAttribute("contas", contaFinanceiraService.findAll());
        model.addAttribute("pagamentoLoteDto", pagamentoLoteDto); // Use the populated DTO

        return "cobrancas/form-pagamento-lote";
    }

    @PostMapping("/registrar-pagamento-lote")
    @ResponseBody
    public ResponseEntity<?> pagarLote(@RequestBody PagamentoLoteRequestDto pagamentoDto) {
        try {
            cobrancaService.quitarCobrancasEmLote(pagamentoDto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Erro ao quitar cobranças: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Erro ao quitar cobranças: " + e.getMessage());
        }
    }

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

    @PostMapping("/criarNovaDespesa")
    @ResponseBody
    public ResponseEntity<?> criarNovaDespesa(@RequestBody CobrancaDTO dto) {
        try {
            cobrancaService.gerarCobrancaOutrasRubricas(dto);
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
