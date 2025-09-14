package br.com.sigest.tesouraria.web.controller;

import br.com.sigest.tesouraria.domain.entity.ReconciliacaoMensal;
import br.com.sigest.tesouraria.service.ReconciliacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/reconciliacao")
public class ReconciliacaoController {

    @Autowired
    private ReconciliacaoService reconciliacaoService;

    @GetMapping
    public String listar(Model model) {
        List<ReconciliacaoMensal> reconciliacoes = reconciliacaoService.findAll();
        model.addAttribute("reconciliacoes", reconciliacoes);
        return "reconciliacao/lista";
    }

    @GetMapping("/novo")
    public String novoForm(Model model) {
        ReconciliacaoMensal reconciliacao = reconciliacaoService.newReconciliacao(
            java.time.LocalDate.now().getMonthValue(),
            java.time.LocalDate.now().getYear()
        );
        
        model.addAttribute("reconciliacao", reconciliacao);
        model.addAttribute("years", getYears());
        return "reconciliacao/formulario";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Long id, Model model) {
        ReconciliacaoMensal reconciliacao = reconciliacaoService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido:" + id));
        
        model.addAttribute("reconciliacao", reconciliacao);
        model.addAttribute("years", getYears());
        return "reconciliacao/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute ReconciliacaoMensal reconciliacao) {
        reconciliacaoService.save(reconciliacao);
        return "redirect:/reconciliacao";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        reconciliacaoService.deleteById(id);
        return "redirect:/reconciliacao";
    }

    private List<Integer> getYears() {
        List<Integer> years = new java.util.ArrayList<>();
        int currentYear = java.time.LocalDate.now().getYear();
        for (int i = currentYear - 5; i <= currentYear + 5; i++) {
            years.add(i);
        }
        return years;
    }
}