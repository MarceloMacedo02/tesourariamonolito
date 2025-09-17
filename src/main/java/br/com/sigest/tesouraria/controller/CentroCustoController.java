package br.com.sigest.tesouraria.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

import br.com.sigest.tesouraria.dto.CentroCustoDto;
import br.com.sigest.tesouraria.service.CentroCustoService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/cadastros/centros-de-custo")
public class CentroCustoController {

    @Autowired
    private CentroCustoService service;

    private static final String VIEW_PATH = "cadastros/centrosdecusto/";

    private static final String REDIRECT_URL = "redirect:/cadastros/centros-de-custo";

    @GetMapping
    public String listarCentrosDeCusto(Model model) {
        model.addAttribute("centrosDeCusto", service.findAll());
        return VIEW_PATH + "lista";
    }

    @GetMapping("/all")
    @ResponseBody
    public List<CentroCustoDto> getAllCentrosCusto() {
        return service.findAll();
    }

    @GetMapping("/novo")
    public String novoCentroDeCusto(Model model) {
        model.addAttribute("centroCustoDto", new CentroCustoDto());
        return VIEW_PATH + "formulario";
    }

    @GetMapping("/editar/{id}")
    public String editarCentroDeCusto(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return service.findById(id)
                .map(centroCusto -> {
                    model.addAttribute("centroCustoDto", centroCusto);
                    return VIEW_PATH + "formulario";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Centro de Custo não encontrado.");
                    return REDIRECT_URL;
                });
    }

    @PostMapping("/salvar")
    public String salvarCentroDeCusto(@Valid @ModelAttribute("centroCustoDto") CentroCustoDto centroCustoDto,
            BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("warning", "Verifique os campos obrigatórios.");
            return VIEW_PATH + "formulario";
        }
        try {
            service.save(centroCustoDto);
            redirectAttributes.addFlashAttribute("success", "Centro de Custo salvo com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao salvar: " + e.getMessage());
        }
        return REDIRECT_URL;
    }

    @GetMapping("/excluir/{id}")
    public String excluirCentroDeCusto(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Centro de Custo excluído com sucesso.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao excluir Centro de Custo. Pode estar em uso.");
        }
        return REDIRECT_URL;
    }
}
