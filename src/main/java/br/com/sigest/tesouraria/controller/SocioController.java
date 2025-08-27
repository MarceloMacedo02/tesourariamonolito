package br.com.sigest.tesouraria.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.sigest.tesouraria.domain.enums.GrauSocio;
import br.com.sigest.tesouraria.dto.SocioDto;
import br.com.sigest.tesouraria.service.SocioService;

@Controller
@RequestMapping("/cadastros/socios")
public class SocioController {

    @Autowired
    private SocioService service;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("socios", service.findAll());
        return "cadastros/socios/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("socioDto", new SocioDto());
        model.addAttribute("graus", GrauSocio.values());
        return "cadastros/socios/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute SocioDto socioDto, RedirectAttributes redirect) {
        service.save(socioDto);
        try {
            service.save(socioDto);
            redirect.addFlashAttribute("success", "Sócio salvo com sucesso!");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Erro ao salvar: " + e.getMessage());
        }
        return "redirect:/cadastros/socios";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("socioDto", service.findByIdAsDto(id));
        model.addAttribute("graus", GrauSocio.values());
        return "cadastros/socios/formulario";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        service.delete(id);
        try {
            service.delete(id);
            redirect.addFlashAttribute("success", "Sócio excluído com sucesso!");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Erro ao excluir: " + e.getMessage());
        }
        return "redirect:/cadastros/socios";
    }

    @GetMapping("/grid")
    public String gridListar(Model model) {
        model.addAttribute("socios", service.findAll());
        return "cadastros/socios/grid";

    }
}
