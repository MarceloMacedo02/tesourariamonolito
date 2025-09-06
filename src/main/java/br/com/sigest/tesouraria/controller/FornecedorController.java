package br.com.sigest.tesouraria.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.sigest.tesouraria.dto.FornecedorDto;
import br.com.sigest.tesouraria.service.FornecedorService;
import jakarta.validation.Valid;

/**
 * Controlador MVC para Fornecedores.
 */
@Controller
@RequestMapping("/cadastros/fornecedores")
public class FornecedorController {

    @Autowired
    private FornecedorService service;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("fornecedores", service.findAll());
        return "cadastros/fornecedores/lista";
    }

    @GetMapping("/all")
    @ResponseBody
    public List<FornecedorDto> getAllFornecedores() {
        return service.findAll().stream().map(fornecedor -> service.toDto(fornecedor)).collect(Collectors.toList());
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("fornecedorDto", new FornecedorDto());
        return "cadastros/fornecedores/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("fornecedorDto") FornecedorDto fornecedorDto, BindingResult result,
            RedirectAttributes redirect) {
        if (result.hasErrors()) {
            redirect.addFlashAttribute("warning", "Verifique os campos obrigatórios.");
            return "cadastros/fornecedores/formulario";
        }
        try {
            service.save(fornecedorDto);
            redirect.addFlashAttribute("success", "Fornecedor salvo com sucesso!");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Erro ao salvar fornecedor: " + e.getMessage());
            return "redirect:/cadastros/fornecedores/novo";
        }
        return "redirect:/cadastros/fornecedores";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("fornecedorDto", service.findByIdAsDto(id));
        return "cadastros/fornecedores/formulario";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            service.delete(id);
            redirect.addFlashAttribute("success", "Fornecedor excluído com sucesso!");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Não foi possível excluir o fornecedor.");
        }
        return "redirect:/cadastros/fornecedores";
    }
}
