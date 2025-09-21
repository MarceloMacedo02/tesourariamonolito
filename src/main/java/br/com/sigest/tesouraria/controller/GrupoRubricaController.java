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

import br.com.sigest.tesouraria.dto.GrupoRubricaDto;
import br.com.sigest.tesouraria.service.GrupoRubricaService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/cadastros/grupos-de-rubrica")
public class GrupoRubricaController {

    @Autowired
    private GrupoRubricaService service;

    private static final String VIEW_PATH = "cadastros/gruposderubrica/";

    private static final String REDIRECT_URL = "redirect:/cadastros/grupos-de-rubrica";

    /**
     * Lista todos os grupos de rubrica.
     *
     * @param model o modelo para a view
     * @return o nome da view de lista
     */
    @GetMapping
    public String listarGruposDeRubrica(Model model) {
        model.addAttribute("gruposDeRubrica", service.findAll());
        return VIEW_PATH + "lista";
    }

    /**
     * Retorna todos os grupos de rubrica como uma lista de DTOs.
     *
     * @return a lista de GrupoRubricaDto
     */
    @GetMapping("/all")
    @ResponseBody
    public List<GrupoRubricaDto> getAllGruposRubrica() {
        return service.findAll();
    }

    /**
     * Exibe o formulário para um novo grupo de rubrica.
     *
     * @param model o modelo para a view
     * @return o nome da view de formulário
     */
    @GetMapping("/novo")
    public String novoGrupoDeRubrica(Model model) {
        model.addAttribute("grupoRubricaDto", new GrupoRubricaDto());
        return VIEW_PATH + "formulario";
    }

    /**
     * Exibe o formulário para editar um grupo de rubrica.
     *
     * @param id                 o ID do grupo de rubrica
     * @param model              o modelo para a view
     * @param redirectAttributes os atributos de redirecionamento
     * @return o nome da view de formulário ou o redirecionamento para a lista
     */
    @GetMapping("/editar/{id}")
    public String editarGrupoDeRubrica(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return service.findById(id)
                .map(grupoRubrica -> {
                    model.addAttribute("grupoRubricaDto", grupoRubrica);
                    return VIEW_PATH + "formulario";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Grupo de Rubrica não encontrado.");
                    return REDIRECT_URL;
                });
    }

    /**
     * Salva um grupo de rubrica.
     *
     * @param grupoRubricaDto     o DTO do grupo de rubrica
     * @param result             o resultado da validação
     * @param redirectAttributes os atributos de redirecionamento
     * @return o redirecionamento para a lista
     */
    @PostMapping("/salvar")
    public String salvarGrupoDeRubrica(@Valid @ModelAttribute("grupoRubricaDto") GrupoRubricaDto grupoRubricaDto,
            BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("warning", "Verifique os campos obrigatórios.");
            return VIEW_PATH + "formulario";
        }
        try {
            service.save(grupoRubricaDto);
            redirectAttributes.addFlashAttribute("success", "Grupo de Rubrica salvo com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao salvar: " + e.getMessage());
        }
        return REDIRECT_URL;
    }

    /**
     * Exclui um grupo de rubrica.
     *
     * @param id                 o ID do grupo de rubrica
     * @param redirectAttributes os atributos de redirecionamento
     * @return o redirecionamento para a lista
     */
    @GetMapping("/excluir/{id}")
    public String excluirGrupoDeRubrica(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Grupo de Rubrica excluído com sucesso.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao excluir Grupo de Rubrica. Pode estar em uso.");
        }
        return REDIRECT_URL;
    }
}