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

import br.com.sigest.tesouraria.dto.GrupoMensalidadeDto;
import br.com.sigest.tesouraria.service.GrupoMensalidadeService;
import br.com.sigest.tesouraria.service.RubricaService;

@Controller
@RequestMapping("/cadastros/grupos-mensalidade")
public class GrupoMensalidadeController {
    @Autowired
    private GrupoMensalidadeService service;
    @Autowired
    private RubricaService rubricaService;

    /**
     * Lista todos os grupos de mensalidade.
     *
     * @param model o modelo para a view
     * @return o nome da view de lista
     */
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("grupos", service.findAllDtos());
        return "cadastros/grupos-mensalidade/lista";
    }

    /**
     * Exibe o formulário para um novo grupo de mensalidade.
     *
     * @param model o modelo para a view
     * @return o nome da view de formulário
     */
    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("grupoMensalidade", new GrupoMensalidadeDto());
        model.addAttribute("allRubricas", rubricaService.findAll());
        return "cadastros/grupos-mensalidade/formulario";
    }

    /**
     * Salva um grupo de mensalidade.
     *
     * @param grupoDto o DTO do grupo de mensalidade
     * @param redirect os atributos de redirecionamento
     * @return o redirecionamento para a lista de grupos de mensalidade
     */
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute GrupoMensalidadeDto grupoDto, RedirectAttributes redirect) {
        try {
            service.saveDto(grupoDto);
            redirect.addFlashAttribute("success", "Grupo de Mensalidade salvo com sucesso!");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Erro ao salvar: " + e.getMessage());
        }
        return "redirect:/cadastros/grupos-mensalidade";
    }

    /**
     * Exibe o formulário para editar um grupo de mensalidade.
     *
     * @param id    o ID do grupo de mensalidade
     * @param model o modelo para a view
     * @return o nome da view de formulário
     */
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("grupoMensalidade", service.findDtoById(id));
        model.addAttribute("allRubricas", rubricaService.findAll());
        return "cadastros/grupos-mensalidade/formulario";
    }

    /**
     * Exclui um grupo de mensalidade.
     *
     * @param id       o ID do grupo de mensalidade
     * @param redirect os atributos de redirecionamento
     * @return o redirecionamento para a lista de grupos de mensalidade
     */
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            service.delete(id);
            redirect.addFlashAttribute("success", "Grupo de Mensalidade excluído com sucesso!");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Erro ao excluir: " + e.getMessage());
        }
        return "redirect:/cadastros/grupos-mensalidade";
    }

    /**
     * Clona um grupo de mensalidade.
     *
     * @param id       o ID do grupo de mensalidade
     * @param redirect os atributos de redirecionamento
     * @return o redirecionamento para a edição do grupo clonado
     */
    @GetMapping("/clone/{id}")
    public String cloneGrupo(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            GrupoMensalidadeDto clonedGrupo = service.cloneGrupoMensalidade(id);
            redirect.addFlashAttribute("success", "Grupo de Mensalidade clonado com sucesso!");
            return "redirect:/cadastros/grupos-mensalidade/editar/" + clonedGrupo.getId();
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Erro ao clonar: " + e.getMessage());
            return "redirect:/cadastros/grupos-mensalidade";
        }
    }
}