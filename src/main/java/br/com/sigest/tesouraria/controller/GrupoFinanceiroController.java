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

import br.com.sigest.tesouraria.dto.GrupoFinanceiroDto;
import br.com.sigest.tesouraria.service.GrupoFinanceiroService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/cadastros/grupofinanceiro")
public class GrupoFinanceiroController {
    @Autowired
    private GrupoFinanceiroService service;

    /**
     * Lista todos os grupos financeiros.
     *
     * @param model o modelo para a view
     * @return o nome da view de lista
     */
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("gruposFinanceiros", service.findAll());
        return "cadastros/grupofinanceiro/lista";
    }

    /**
     * Exibe o formulário para um novo grupo financeiro.
     *
     * @param model o modelo para a view
     * @return o nome da view de formulário
     */
    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("grupoFinanceiroDto", new GrupoFinanceiroDto());
        return "cadastros/grupofinanceiro/formulario";
    }

    /**
     * Salva um grupo financeiro.
     *
     * @param grupoFinanceiroDto o DTO do grupo financeiro
     * @param result             o resultado da validação
     * @param redirect           os atributos de redirecionamento
     * @return o redirecionamento para a lista de grupos financeiros
     */
    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("grupoFinanceiroDto") GrupoFinanceiroDto grupoFinanceiroDto,
            BindingResult result, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            redirect.addFlashAttribute("warning", "Verifique os campos obrigatórios.");
            return "cadastros/grupofinanceiro/formulario";
        }
        try {
            service.save(grupoFinanceiroDto);
            redirect.addFlashAttribute("success", "Grupo Financeiro salvo com sucesso!");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Erro ao salvar: " + e.getMessage());
        }
        return "redirect:/cadastros/grupofinanceiro";
    }

    /**
     * Exibe o formulário para editar um grupo financeiro.
     *
     * @param id    o ID do grupo financeiro
     * @param model o modelo para a view
     * @return o nome da view de formulário
     */
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("grupoFinanceiroDto", service.findByIdAsDto(id));
        return "cadastros/grupofinanceiro/formulario";
    }

    /**
     * Exclui um grupo financeiro.
     *
     * @param id       o ID do grupo financeiro
     * @param redirect os atributos de redirecionamento
     * @return o redirecionamento para a lista de grupos financeiros
     */
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            service.delete(id);
            redirect.addFlashAttribute("success", "Grupo Financeiro excluído com sucesso!");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Erro ao excluir: " + e.getMessage());
        }
        return "redirect:/cadastros/grupofinanceiro";
    }
}
