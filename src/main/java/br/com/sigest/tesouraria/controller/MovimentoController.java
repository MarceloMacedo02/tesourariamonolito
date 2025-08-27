package br.com.sigest.tesouraria.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.sigest.tesouraria.domain.entity.Movimento;
import br.com.sigest.tesouraria.service.ContaFinanceiraService;
import br.com.sigest.tesouraria.service.MovimentoService;
import br.com.sigest.tesouraria.service.RubricaService;

@Controller
@RequestMapping("/movimentacoes")
public class MovimentoController {

    @Autowired
    private MovimentoService service;
    @Autowired
    private ContaFinanceiraService contaFinanceiraService;
    @Autowired
    private RubricaService rubricaService;

    @GetMapping
    public String menu() {
        return "movimentacoes/menu";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("movimento", new Movimento());
        model.addAttribute("contas", contaFinanceiraService.findAll());
        model.addAttribute("rubricas", rubricaService.findAll());
        return "movimentacoes/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Movimento movimento, RedirectAttributes redirect) {
        service.salvarMovimento(movimento);
        try {
            service.salvarMovimento(movimento);
            redirect.addFlashAttribute("success", "Movimentação registrada com sucesso!");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Erro ao salvar: " + e.getMessage());
        }
        return "redirect:/movimentacoes/extrato";
    }

    @GetMapping("/extrato")
    public String extrato(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            Model model) {

        if (inicio == null) {
            inicio = LocalDate.now().withDayOfMonth(1);
        }
        if (fim == null) {
            fim = LocalDate.now();
        }

        model.addAttribute("movimentos", service.findByPeriodo(inicio, fim));
        model.addAttribute("inicio", inicio);
        model.addAttribute("fim", fim);
        return "movimentacoes/extrato";
    }
}