package br.com.sigest.tesouraria.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.sigest.tesouraria.domain.enums.TipoMovimento;
import br.com.sigest.tesouraria.dto.ExtratoFiltroDto;
import br.com.sigest.tesouraria.dto.MovimentoDto;
import br.com.sigest.tesouraria.service.ContaFinanceiraService;
import br.com.sigest.tesouraria.service.MovimentoService;
import br.com.sigest.tesouraria.service.RubricaService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/movimentos")
public class MovimentoController {

    @Autowired
    private MovimentoService movimentoService;

    @Autowired
    private ContaFinanceiraService contaFinanceiraService;

    @Autowired
    private RubricaService rubricaService;

    /**
     * Exibe o extrato de movimentos.
     *
     * @param filtro o DTO com os filtros
     * @param model  o modelo para a view
     * @return o nome da view de extrato
     */
    @GetMapping("/extrato")
    public String extrato(@ModelAttribute("filtro") ExtratoFiltroDto filtro, Model model) {
        model.addAttribute("movimentos", movimentoService.filtrarMovimentos(filtro));
        model.addAttribute("contasFinanceiras", contaFinanceiraService.findAll());
        model.addAttribute("rubricas", rubricaService.findAll());
        model.addAttribute("tiposMovimento", TipoMovimento.values());
        return "movimentos/extrato";
    }

    /**
     * Exibe o formulário para um novo movimento.
     *
     * @param model o modelo para a view
     * @return o nome da view de formulário
     */
    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("movimentoDto", new MovimentoDto());
        model.addAttribute("contasFinanceiras", contaFinanceiraService.findAll());
        model.addAttribute("rubricas", rubricaService.findAll());
        model.addAttribute("tiposMovimento", TipoMovimento.values());
        return "movimentos/formulario";
    }

    /**
     * Salva um novo movimento.
     *
     * @param dto                o DTO do movimento
     * @param result             o resultado da validação
     * @param redirectAttributes os atributos de redirecionamento
     * @param model              o modelo para a view
     * @return o redirecionamento para o formulário de novo movimento
     */
    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("movimentoDto") MovimentoDto dto, BindingResult result,
            RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("contasFinanceiras", contaFinanceiraService.findAll());
            model.addAttribute("rubricas", rubricaService.findAll());
            model.addAttribute("tiposMovimento", TipoMovimento.values());
            return "movimentos/formulario";
        }
        try {
            movimentoService.registrarMovimento(dto);
            redirectAttributes.addFlashAttribute("success", "Movimento registrado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao registrar movimento: " + e.getMessage());
        }
        return "redirect:/movimentos/novo"; // Redirect back to the form for new entry
    }

}
