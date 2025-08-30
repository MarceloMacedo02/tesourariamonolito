package br.com.sigest.tesouraria.controller;

import java.time.LocalDate;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.sigest.tesouraria.dto.CobrancaDTO;
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

    @GetMapping
    public String listar(Model model) {
        logger.info("Acessando a página de listagem de cobranças.");
        model.addAttribute("filtro", new CobrancaDTO());
        model.addAttribute("cobrancas", cobrancaService.listarCobrancas(new CobrancaDTO()));
        return "cobrancas/lista";
    }

    @GetMapping("/novo/mensalidade")
    public String novaMensalidade(Model model) {
        logger.info("Acessando o formulário para gerar nova mensalidade.");
        model.addAttribute("cobrancaDto", CobrancaDTO.builder()
                .tipoCobranca(br.com.sigest.tesouraria.domain.enums.TipoCobranca.MENSALIDADE)
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
        return "cobrancas/form-mensalidade";
    }

    @GetMapping("/novo/rubrica")
    public String novaRubrica(Model model) {
        logger.info("Acessando o formulário para gerar nova cobrança por rubrica.");
        model.addAttribute("cobrancaDto", CobrancaDTO.builder()
                .tipoCobranca(br.com.sigest.tesouraria.domain.enums.TipoCobranca.OUTRAS_RUBRICAS)
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

    @GetMapping("/pagar/{id}")
    public String pagar(@PathVariable Long id, Model model) {
        logger.info("Acessando o formulário de pagamento para a cobrança com ID: {}", id);
        model.addAttribute("cobranca", cobrancaService.findById(id));
        PagamentoRequestDto pagamentoDto = PagamentoRequestDto.builder()
                .dataPagamento(LocalDate.now())
                .valor(cobrancaService.findById(id).getValor())
                .build();
        model.addAttribute("pagamentoDto", pagamentoDto);
        model.addAttribute("contas", contaFinanceiraService.findAll());
        return "cobrancas/form-pagamento";
    }

    @PostMapping("/salvar-mensalidade")
    public String salvarMensalidade(@Valid @ModelAttribute("cobrancaDto") CobrancaDTO cobrancaDto,
            BindingResult result, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            logger.warn("Tentativa de salvar mensalidade com erros de validação.");
            redirect.addFlashAttribute("warning", "Verifique os campos obrigatórios.");
            return "redirect:/cobrancas/novo/mensalidade";
        }
        try {
            cobrancaService.gerarCobrancaMensalidadeManual(cobrancaDto.getSociosIds());
            logger.info("Cobrança(s) de mensalidade gerada(s) com sucesso!");
            redirect.addFlashAttribute("success", "Cobrança(s) de mensalidade gerada(s) com sucesso!");
        } catch (Exception e) {
            logger.error("Erro ao gerar cobrança(s): {}", e.getMessage());
            redirect.addFlashAttribute("error", "Erro ao gerar cobrança(s): " + e.getMessage());
        }
        return "redirect:/cobrancas";
    }

    @PostMapping("/salvar-rubrica")
    public String salvarRubrica(@Valid @ModelAttribute("cobrancaDto") CobrancaDTO cobrancaDto,
            BindingResult result, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            logger.warn("Tentativa de salvar cobrança por rubrica com erros de validação.");
            redirect.addFlashAttribute("warning", "Verifique os campos obrigatórios.");
            return "redirect:/cobrancas/novo/rubrica";
        }
        try {
            cobrancaService.gerarCobrancaOutrasRubricas(cobrancaDto);
            logger.info("Cobrança por rubrica gerada com sucesso!");
            redirect.addFlashAttribute("success", "Cobrança gerada com sucesso!");
        } catch (Exception e) {
            logger.error("Erro ao gerar cobrança por rubrica: {}", e.getMessage());
            redirect.addFlashAttribute("error", "Erro ao gerar cobrança: " + e.getMessage());
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
}