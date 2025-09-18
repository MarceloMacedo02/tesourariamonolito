package br.com.sigest.tesouraria.web.controller;

import br.com.sigest.tesouraria.domain.entity.ReconciliacaoBancaria;
import br.com.sigest.tesouraria.domain.entity.ReconciliacaoMensal;
import br.com.sigest.tesouraria.service.ReconciliacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/reconciliacao")
public class ReconciliacaoController {

    private static final Logger logger = LoggerFactory.getLogger(ReconciliacaoController.class);

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
        
        // Garantir que mes e ano não sejam nulos
        if (reconciliacao.getMes() == null) {
            reconciliacao.setMes(java.time.LocalDate.now().getMonthValue());
        }
        if (reconciliacao.getAno() == null) {
            reconciliacao.setAno(java.time.LocalDate.now().getYear());
        }
        
        // Garantir que as reconciliacoes bancarias tenham mes e ano preenchidos
        if (reconciliacao.getReconciliacoesBancarias() != null) {
            for (ReconciliacaoBancaria rb : reconciliacao.getReconciliacoesBancarias()) {
                if (rb.getMes() == null) {
                    rb.setMes(reconciliacao.getMes());
                }
                if (rb.getAno() == null) {
                    rb.setAno(reconciliacao.getAno());
                }
            }
        }
        
        model.addAttribute("reconciliacao", reconciliacao);
        model.addAttribute("years", getYears());
        model.addAttribute("meses", getMeses());
        return "reconciliacao/formulario";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Long id, Model model) {
        ReconciliacaoMensal reconciliacao = reconciliacaoService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido:" + id));
        
        // Garantir que mes e ano não sejam nulos
        if (reconciliacao.getMes() == null) {
            reconciliacao.setMes(1); // Valor padrão
        }
        if (reconciliacao.getAno() == null) {
            reconciliacao.setAno(java.time.LocalDate.now().getYear()); // Valor padrão
        }
        
        // Garantir que as reconciliacoes bancarias tenham mes e ano preenchidos
        if (reconciliacao.getReconciliacoesBancarias() != null) {
            for (ReconciliacaoBancaria rb : reconciliacao.getReconciliacoesBancarias()) {
                if (rb.getMes() == null) {
                    rb.setMes(reconciliacao.getMes());
                }
                if (rb.getAno() == null) {
                    rb.setAno(reconciliacao.getAno());
                }
            }
        }
        
        model.addAttribute("reconciliacao", reconciliacao);
        model.addAttribute("years", getYears());
        model.addAttribute("meses", getMeses());
        return "reconciliacao/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute ReconciliacaoMensal reconciliacao, BindingResult result, Model model) {
        // Verificar se há erros de validação
        if (result.hasErrors()) {
            model.addAttribute("reconciliacao", reconciliacao);
            model.addAttribute("years", getYears());
            model.addAttribute("meses", getMeses());
            model.addAttribute("error", "Por favor, corrija os erros no formulário.");
            return "reconciliacao/formulario";
        }
        
        // Validar campos obrigatórios
        if (reconciliacao.getMes() == null) {
            model.addAttribute("reconciliacao", reconciliacao);
            model.addAttribute("years", getYears());
            model.addAttribute("meses", getMeses());
            model.addAttribute("error", "O campo mês é obrigatório.");
            return "reconciliacao/formulario";
        }
        
        if (reconciliacao.getAno() == null) {
            model.addAttribute("reconciliacao", reconciliacao);
            model.addAttribute("years", getYears());
            model.addAttribute("meses", getMeses());
            model.addAttribute("error", "O campo ano é obrigatório.");
            return "reconciliacao/formulario";
        }
        
        // Validar que cada reconciliação bancária tem mes e ano preenchidos
        if (reconciliacao.getReconciliacoesBancarias() != null) {
            for (ReconciliacaoBancaria rb : reconciliacao.getReconciliacoesBancarias()) {
                if (rb.getMes() == null) {
                    rb.setMes(reconciliacao.getMes());
                }
                if (rb.getAno() == null) {
                    rb.setAno(reconciliacao.getAno());
                }
            }
        }
        
        logger.info("Salvando reconciliação: mes={}, ano={}, bancos={}", 
            reconciliacao.getMes(), reconciliacao.getAno(), 
            reconciliacao.getReconciliacoesBancarias() != null ? reconciliacao.getReconciliacoesBancarias().size() : 0);
        
        if (reconciliacao.getReconciliacoesBancarias() != null) {
            for (int i = 0; i < reconciliacao.getReconciliacoesBancarias().size(); i++) {
                logger.info("Banco {}: {}", i, reconciliacao.getReconciliacoesBancarias().get(i));
            }
        }
        
        reconciliacaoService.save(reconciliacao);
        logger.info("Reconciliação salva com sucesso");
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

    private java.util.Map<Integer, String> getMeses() {
        java.util.Map<Integer, String> meses = new java.util.LinkedHashMap<>();
        meses.put(1, "Janeiro");
        meses.put(2, "Fevereiro");
        meses.put(3, "Março");
        meses.put(4, "Abril");
        meses.put(5, "Maio");
        meses.put(6, "Junho");
        meses.put(7, "Julho");
        meses.put(8, "Agosto");
        meses.put(9, "Setembro");
        meses.put(10, "Outubro");
        meses.put(11, "Novembro");
        meses.put(12, "Dezembro");
        return meses;
    }
}