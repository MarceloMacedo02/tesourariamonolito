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

import br.com.sigest.tesouraria.domain.enums.TipoRubrica;
import br.com.sigest.tesouraria.dto.RubricaDto;
import br.com.sigest.tesouraria.service.CentroCustoService;
import br.com.sigest.tesouraria.service.RubricaService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/cadastros/rubricas")
public class RubricaController {
    @Autowired
    private RubricaService service;
    @Autowired
    private CentroCustoService centroCustoService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("rubricas", service.findAll());
        return "cadastros/rubricas/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("rubricaDto", new RubricaDto());
        model.addAttribute("centrosCusto", centroCustoService.findAll());
        model.addAttribute("tipos", TipoRubrica.values());
        return "cadastros/rubricas/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute RubricaDto rubricaDto, BindingResult result,
            RedirectAttributes redirect) {
        if (result.hasErrors()) {
            redirect.addFlashAttribute("warning", "Verifique os campos obrigatórios.");
            return "cadastros/rubricas/formulario";
        }
        try {
            service.save(rubricaDto);
            redirect.addFlashAttribute("success", "Rubrica salva com sucesso!");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Erro ao salvar: " + e.getMessage());
        }
        return "redirect:/cadastros/rubricas";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("rubricaDto", service.findByIdAsDto(id));
        model.addAttribute("centrosCusto", centroCustoService.findAll());
        model.addAttribute("tipos", TipoRubrica.values());
        return "cadastros/rubricas/formulario";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            service.delete(id);
            redirect.addFlashAttribute("success", "Rubrica excluída com sucesso!");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Erro ao excluir: " + e.getMessage());
        }
        return "redirect:/cadastros/rubricas";
    }
}