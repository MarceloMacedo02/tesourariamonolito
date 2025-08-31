package br.com.sigest.tesouraria.controller;

import br.com.sigest.tesouraria.dto.DemonstrativoFinanceiroMensalDto;
import br.com.sigest.tesouraria.service.MovimentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/relatorios")
public class RelatorioController {

    @Autowired
    private MovimentoService movimentoService;

    @GetMapping("/demonstrativo-financeiro-mensal")
    public String showDemonstrativoForm(Model model) {
        int currentYear = YearMonth.now().getYear();
        int currentMonth = YearMonth.now().getMonthValue();

        List<Integer> years = new ArrayList<>();
        for (int i = currentYear - 5; i <= currentYear + 1; i++) { // Last 5 years, current, next
            years.add(i);
        }

        model.addAttribute("years", years);
        model.addAttribute("currentYear", currentYear);
        model.addAttribute("currentMonth", currentMonth);
        return "relatorios/demonstrativo-financeiro-mensal";
    }

    @PostMapping("/demonstrativo-financeiro-mensal")
    public String generateDemonstrativo(
            @RequestParam("mes") int mes,
            @RequestParam("ano") int ano,
            Model model) {

        DemonstrativoFinanceiroMensalDto demonstrativo = movimentoService.gerarDemonstrativoFinanceiroMensal(mes, ano);
        model.addAttribute("demonstrativo", demonstrativo);
        return "relatorios/demonstrativo-financeiro-mensal";
    }
}
