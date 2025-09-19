package br.com.sigest.tesouraria.controller;

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

import br.com.sigest.tesouraria.domain.entity.ContaFinanceira;
import br.com.sigest.tesouraria.service.ContaFinanceiraService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/cadastros/contas-financeiras")
public class ContaFinanceiraController {
    @Autowired
    private ContaFinanceiraService service;

    /**
     * Lista todas as contas financeiras.
     *
     * @param model o modelo para a view
     * @return o nome da view de lista
     */
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("contas", service.findAll());
        return "cadastros/contas-financeiras/lista";
    }

    /**
     * Exibe o formulário para uma nova conta financeira.
     *
     * @param model o modelo para a view
     * @return o nome da view de formulário
     */
    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("conta", new ContaFinanceira());
        return "cadastros/contas-financeiras/formulario";
    }

    /**
     * Salva uma conta financeira.
     *
     * @param conta    a conta financeira a ser salva
     * @param result   o resultado da validação
     * @param redirect os atributos de redirecionamento
     * @return o redirecionamento para a lista de contas financeiras
     */
    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute ContaFinanceira conta, BindingResult result,
            RedirectAttributes redirect) {
        if (result.hasErrors()) {
            redirect.addFlashAttribute("warning", "Erro ao salvar conta. Verifique os campos.");
            return "redirect:/cadastros/contas-financeiras/novo";
        }
        try {
            service.save(conta);
            redirect.addFlashAttribute("success", "Conta salva com sucesso!");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Erro ao salvar: " + e.getMessage());
        }
        return "redirect:/cadastros/contas-financeiras";
    }

    /**
     * Exibe o formulário para editar uma conta financeira.
     *
     * @param id    o ID da conta financeira
     * @param model o modelo para a view
     * @return o nome da view de formulário
     */
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("conta", service.findById(id));
        return "cadastros/contas-financeiras/formulario";
    }

    /**
     * Exclui uma conta financeira.
     *
     * @param id       o ID da conta financeira
     * @param redirect os atributos de redirecionamento
     * @return o redirecionamento para a lista de contas financeiras
     */
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            service.delete(id);
            redirect.addFlashAttribute("success", "Conta excluída com sucesso!");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Erro ao excluir: " + e.getMessage());
        }
        return "redirect:/cadastros/contas-financeiras";
    }
}