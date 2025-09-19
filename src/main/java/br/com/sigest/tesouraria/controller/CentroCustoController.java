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

    /**
     * Lista todos os centros de custo.
     *
     * @param model o modelo para a view
     * @return o nome da view de lista
     */
    @GetMapping
    public String listarCentrosDeCusto(Model model) {
        model.addAttribute("centrosDeCusto", service.findAll());
        return VIEW_PATH + "lista";
    }

    /**
     * Retorna todos os centros de custo como uma lista de DTOs.
     *
     * @return a lista de CentroCustoDto
     */
    @GetMapping("/all")
    @ResponseBody
    public List<CentroCustoDto> getAllCentrosCusto() {
        return service.findAll();
    }

    /**
     * Exibe o formulário para um novo centro de custo.
     *
     * @param model o modelo para a view
     * @return o nome da view de formulário
     */
    @GetMapping("/novo")
    public String novoCentroDeCusto(Model model) {
        model.addAttribute("centroCustoDto", new CentroCustoDto());
        return VIEW_PATH + "formulario";
    }

    /**
     * Exibe o formulário para editar um centro de custo.
     *
     * @param id                 o ID do centro de custo
     * @param model              o modelo para a view
     * @param redirectAttributes os atributos de redirecionamento
     * @return o nome da view de formulário ou o redirecionamento para a lista
     */
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

    /**
     * Salva um centro de custo.
     *
     * @param centroCustoDto     o DTO do centro de custo
     * @param result             o resultado da validação
     * @param redirectAttributes os atributos de redirecionamento
     * @return o redirecionamento para a lista
     */
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

    /**
     * Exclui um centro de custo.
     *
     * @param id                 o ID do centro de custo
     * @param redirectAttributes os atributos de redirecionamento
     * @return o redirecionamento para a lista
     */
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
