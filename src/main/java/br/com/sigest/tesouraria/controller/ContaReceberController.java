package br.com.sigest.tesouraria.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.sigest.tesouraria.domain.entity.Cobranca;
import br.com.sigest.tesouraria.domain.entity.Rubrica;
import br.com.sigest.tesouraria.domain.repository.RubricaRepository;
import br.com.sigest.tesouraria.domain.repository.SocioRepository;
import br.com.sigest.tesouraria.dto.ContaReceberDto;
import br.com.sigest.tesouraria.service.CobrancaService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/contas-a-receber")
public class ContaReceberController {

    @Autowired
    private CobrancaService cobrancaService;

    @Autowired
    private RubricaRepository rubricaRepository;

    @Autowired
    private SocioRepository socioRepository;

    /**
     * Lista todas as contas a receber.
     *
     * @param model o modelo para a view
     * @return o nome da view de lista
     */
    @GetMapping
    public String listar(Model model) {
        List<Cobranca> contas = cobrancaService.findContasAReceber();
        model.addAttribute("contas", contas);
        return "contas-a-receber/listar";
    }

    /**
     * Exibe o formulário para uma nova conta a receber.
     *
     * @param model o modelo para a view
     * @return o nome da view de formulário
     */
    @GetMapping("/nova")
    public String nova(Model model) {
        model.addAttribute("contaReceberDto", new ContaReceberDto());
        List<Rubrica> rubricas = rubricaRepository.findAll();
        model.addAttribute("rubricas", rubricas);
        return "contas-a-receber/formulario";
    }

    /**
     * Salva uma nova conta a receber.
     *
     * @param dto                o DTO da conta a receber
     * @param result             o resultado da validação
     * @param redirectAttributes os atributos de redirecionamento
     * @param model              o modelo para a view
     * @return o redirecionamento para a lista de contas a receber
     */
    @PostMapping("/salvar")
    public String salvar(@Valid ContaReceberDto dto, BindingResult result, RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            // Repopulate the form with necessary data
            List<Rubrica> rubricas = rubricaRepository.findAll();
            model.addAttribute("rubricas", rubricas);
            return "contas-a-receber/formulario";
        }

        try {
            cobrancaService.criarContaReceber(dto);
            redirectAttributes.addFlashAttribute("success", "Conta a receber criada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao criar conta a receber: " + e.getMessage());
            return "redirect:/contas-a-receber/nova";
        }

        return "redirect:/contas-a-receber"; // Redirect to the list page (which I will create soon)
    }

}
