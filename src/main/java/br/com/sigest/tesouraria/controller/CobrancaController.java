// br/com/sigest/tesouraria/controller/CobrancaController.java
package br.com.sigest.tesouraria.controller;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.sigest.tesouraria.domain.entity.Cobranca;
import br.com.sigest.tesouraria.domain.enums.StatusSocio;
import br.com.sigest.tesouraria.domain.enums.TipoCobranca;
import br.com.sigest.tesouraria.dto.CobrancaDTO;
import br.com.sigest.tesouraria.dto.PagamentoLoteRequestDto;
import br.com.sigest.tesouraria.dto.PagamentoRequestDto;
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
            // O retorno deve ser para a URL que carrega os dados corretamente, ou seja, /novo/mensalidade
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

    @GetMapping("/pagar/{id}")
    public String pagar(@PathVariable Long id, Model model) {
        logger.info("Acessando a página de registro de pagamento para a cobrança com ID: {}", id);
        Cobranca  cobranca = cobrancaService.findById(id);
        model.addAttribute("cobranca", cobranca );
        model.addAttribute("contas", contaFinanceiraService.findAll());
        model.addAttribute("pagamentoDto",   PagamentoRequestDto.builder()
                .dataPagamento(LocalDate.now())
                .valor(cobranca.getValor())
                .build());
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
    public String pagarLoteForm(@RequestParam(value = "cobrancaIds", required = false) List<Long> cobrancaIds, Model model, RedirectAttributes redirect) {
        if (cobrancaIds == null || cobrancaIds.isEmpty()) {
            redirect.addFlashAttribute("error", "Nenhuma cobrança selecionada para pagamento em lote.");
            return "redirect:/cobrancas";
        }
        logger.info("Acessando a página de pagamento em lote para as cobranças com IDs: {}", cobrancaIds);
        model.addAttribute("cobrancaIds", cobrancaIds);
        model.addAttribute("contas", contaFinanceiraService.findAll());
        model.addAttribute("pagamentoDto", new PagamentoLoteRequestDto());
        return "cobrancas/form-pagamento-lote";
    }

    @PostMapping("/pagar-lote")
    public String pagarLote(@ModelAttribute("pagamentoDto") PagamentoLoteRequestDto pagamentoDto, RedirectAttributes redirect) {
        try {
            cobrancaService.quitarCobrancasEmLote(pagamentoDto);
            redirect.addFlashAttribute("success", "Cobranças quitadas com sucesso!");
        } catch (Exception e) {
            logger.error("Erro ao quitar cobranças: {}", e.getMessage());
            redirect.addFlashAttribute("error", "Erro ao quitar cobranças: " + e.getMessage());
        }
        return "redirect:/cobrancas";
    }
}