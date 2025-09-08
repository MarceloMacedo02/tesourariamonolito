package br.com.sigest.tesouraria.controller;

import java.time.LocalDateTime;
import java.time.YearMonth;

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

import br.com.sigest.tesouraria.domain.entity.ReconciliacaoMensal;
import br.com.sigest.tesouraria.service.ReconciliacaoService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/reconciliacao")
public class ReconciliacaoController {

    @Autowired
    private ReconciliacaoService reconciliacaoService;

    @GetMapping
    public String listReconciliacoes(Model model) {
        model.addAttribute("reconciliacoes", reconciliacaoService.findAll());
        return "reconciliacao/lista";
    }

    @GetMapping("/novo")
    public String showFormForAdd(Model model) {
        int mes = YearMonth.now().getMonthValue();
        int ano = YearMonth.now().getYear();
        ReconciliacaoMensal reconciliacao = reconciliacaoService.newReconciliacao(mes, ano);
        model.addAttribute("reconciliacao", reconciliacao);
        return "reconciliacao/formulario";
    }

    @GetMapping("/editar/{id}")
    public String showFormForUpdate(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
        reconciliacaoService.findById(id).ifPresentOrElse(
                reconciliacao -> {
                    model.addAttribute("reconciliacao", reconciliacao);
                },
                () -> {
                    ra.addFlashAttribute("errorMessage", "Reconciliação não encontrada.");
                });
        return "reconciliacao/formulario";
    }

    @PostMapping("/salvar")
    public String saveReconciliacao(@Valid @ModelAttribute("reconciliacao") ReconciliacaoMensal reconciliacao,
            BindingResult result,
            RedirectAttributes ra,
            Model model) {
        if (result.hasErrors()) {
            return "reconciliacao/formulario";
        }

        if (reconciliacao.getId() == null || reconciliacao.getDataReconciliacao() == null) {
            reconciliacao.setDataReconciliacao(LocalDateTime.now());
        }

        reconciliacaoService.save(reconciliacao);
        ra.addFlashAttribute("successMessage", "Reconciliação salva com sucesso!");
        return "redirect:/reconciliacao";
    }

    @GetMapping("/excluir/{id}")
    public String deleteReconciliacao(@PathVariable("id") Long id, RedirectAttributes ra) {
        reconciliacaoService.deleteById(id);
        ra.addFlashAttribute("successMessage", "Reconciliação excluída com sucesso!");
        return "redirect:/reconciliacao";
    }
}
