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
import br.com.sigest.tesouraria.dto.ContaReceberDto;
import br.com.sigest.tesouraria.repository.RubricaRepository;
import br.com.sigest.tesouraria.service.CobrancaService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/contas-a-receber")
public class ContaReceberController {

    @Autowired
    private CobrancaService cobrancaService;

    @Autowired
    private RubricaRepository rubricaRepository;

    @GetMapping
    public String listar(Model model) {
        List<Cobranca> contas = cobrancaService.findContasAReceber();
        model.addAttribute("contas", contas);
        return "contas-a-receber/listar";
    }

    @GetMapping("/nova")
    public String nova(Model model) {
        model.addAttribute("contaReceberDto", new ContaReceberDto());
        List<Rubrica> rubricas = rubricaRepository.findAll();
        model.addAttribute("rubricas", rubricas);
        return "contas-a-receber/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid ContaReceberDto dto, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            // This is a simplified error handling. Ideally, we return to the form with
            // errors.
            redirectAttributes.addFlashAttribute("error", "Erro de validação ao criar conta a receber.");
            return "redirect:/contas-a-receber/nova";
        }

        try {
            cobrancaService.criarContaReceber(dto);
            redirectAttributes.addFlashAttribute("success", "Conta a receber criada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao criar conta a receber: " + e.getMessage());
        }

        return "redirect:/contas-a-receber"; // Redirect to the list page (which I will create soon)
    }

}
