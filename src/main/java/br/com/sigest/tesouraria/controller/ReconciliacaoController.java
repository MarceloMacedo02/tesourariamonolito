package br.com.sigest.tesouraria.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
                java.time.LocalDate.now().getYear());

        // Garantir que mes e ano não sejam nulos
        if (reconciliacao.getMes() == null) {
            reconciliacao.setMes(java.time.LocalDate.now().getMonthValue());
        }
        if (reconciliacao.getAno() == null) {
            reconciliacao.setAno(java.time.LocalDate.now().getYear());
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

        model.addAttribute("reconciliacao", reconciliacao);
        model.addAttribute("years", getYears());
        model.addAttribute("meses", getMeses());
        return "reconciliacao/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute ReconciliacaoMensal reconciliacao, BindingResult result, Model model,
            RedirectAttributes redirectAttributes) {

        logger.info("Recebendo requisição para salvar reconciliação: mes={}, ano={}, id={}",
                reconciliacao.getMes(), reconciliacao.getAno(), reconciliacao.getId());

        try {
            // Verificar se há erros de validação
            if (result.hasErrors()) {
                logger.warn("Erros de validação encontrados: {}", result.getAllErrors());
                model.addAttribute("reconciliacao", reconciliacao);
                model.addAttribute("years", getYears());
                model.addAttribute("meses", getMeses());
                model.addAttribute("error", "Por favor, corrija os erros no formulário.");
                return "reconciliacao/formulario";
            }

            // Validar campos obrigatórios
            if (reconciliacao.getMes() == null) {
                logger.warn("Campo mês é obrigatório");
                model.addAttribute("reconciliacao", reconciliacao);
                model.addAttribute("years", getYears());
                model.addAttribute("meses", getMeses());
                model.addAttribute("error", "O campo mês é obrigatório.");
                return "reconciliacao/formulario";
            }

            if (reconciliacao.getAno() == null) {
                logger.warn("Campo ano é obrigatório");
                model.addAttribute("reconciliacao", reconciliacao);
                model.addAttribute("years", getYears());
                model.addAttribute("meses", getMeses());
                model.addAttribute("error", "O campo ano é obrigatório.");
                return "reconciliacao/formulario";
            }

            // Garantir que valores nulos sejam tratados como zero
            if (reconciliacao.getSaldoInicial() == null) {
                reconciliacao.setSaldoInicial(java.math.BigDecimal.ZERO);
            }
            if (reconciliacao.getTotalEntradas() == null) {
                reconciliacao.setTotalEntradas(java.math.BigDecimal.ZERO);
            }
            if (reconciliacao.getTotalSaidas() == null) {
                reconciliacao.setTotalSaidas(java.math.BigDecimal.ZERO);
            }
            if (reconciliacao.getSaldoFinal() == null) {
                reconciliacao.setSaldoFinal(java.math.BigDecimal.ZERO);
            }

            logger.info(
                    "Salvando reconciliação: mes={}, ano={}, saldoInicial={}, totalEntradas={}, totalSaidas={}, saldoFinal={}",
                    reconciliacao.getMes(), reconciliacao.getAno(), reconciliacao.getSaldoInicial(),
                    reconciliacao.getTotalEntradas(), reconciliacao.getTotalSaidas(), reconciliacao.getSaldoFinal());

            ReconciliacaoMensal savedReconciliacao = reconciliacaoService.save(reconciliacao);
            logger.info("Reconciliação salva com sucesso com ID: {}", savedReconciliacao.getId());

            redirectAttributes.addFlashAttribute("success", "Reconciliação salva com sucesso!");
            return "redirect:/reconciliacao";

        } catch (Exception e) {
            logger.error("Erro ao salvar reconciliação", e);
            model.addAttribute("reconciliacao", reconciliacao);
            model.addAttribute("years", getYears());
            model.addAttribute("meses", getMeses());
            model.addAttribute("error", "Erro interno do servidor: " + e.getMessage());
            return "reconciliacao/formulario";
        }
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        reconciliacaoService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Reconciliação excluída com sucesso!");
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

    private Map<Integer, String> getMeses() {
        Map<Integer, String> meses = new java.util.LinkedHashMap<>();
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