package br.com.sigest.tesouraria.controller;

import br.com.sigest.tesouraria.domain.entity.ContaFinanceira;
import br.com.sigest.tesouraria.domain.entity.ReconciliacaoMensal;
import br.com.sigest.tesouraria.repository.ContaFinanceiraRepository;
import br.com.sigest.tesouraria.service.ReconciliacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/reconciliacao")
public class ReconciliacaoController {

    @Autowired
    private ReconciliacaoService reconciliacaoService;

    @Autowired
    private ContaFinanceiraRepository contaFinanceiraRepository; // To get accounts for dropdown

    @GetMapping
    public String listReconciliacoes(Model model) {
        model.addAttribute("reconciliacoes", reconciliacaoService.findAll());
        return "reconciliacao/lista";
    }

    @GetMapping("/novo")
    public String showFormForAdd(Model model) {
        ReconciliacaoMensal reconciliacao = new ReconciliacaoMensal();
        model.addAttribute("reconciliacao", reconciliacao);
        populateFormAttributes(model);
        return "reconciliacao/formulario";
    }

    @GetMapping("/editar/{id}")
    public String showFormForUpdate(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
        reconciliacaoService.findById(id).ifPresentOrElse(
                reconciliacao -> {
                    model.addAttribute("reconciliacao", reconciliacao);
                    populateFormAttributes(model);
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
            populateFormAttributes(model); // Repopulate dropdowns if there are errors
            return "reconciliacao/formulario";
        }

        // Set dataReconciliacao if it's a new entry or not set
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

    private void populateFormAttributes(Model model) {
        int currentYear = YearMonth.now().getYear();
        List<Integer> years = new ArrayList<>();
        for (int i = currentYear - 5; i <= currentYear + 1; i++) {
            years.add(i);
        }
        model.addAttribute("years", years);

        List<ContaFinanceira> contasFinanceiras = contaFinanceiraRepository.findAll();
        model.addAttribute("contasFinanceiras", contasFinanceiras);
    }
}
