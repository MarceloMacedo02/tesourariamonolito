package br.com.sigest.tesouraria.controller;
import br.com.sigest.tesouraria.service.RelatorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDate;
@Controller
@RequestMapping("/relatorios")
public class RelatorioController {
    @Autowired private RelatorioService service;
    @GetMapping
    public String menuRelatorios() {
        return "relatorios/menu";
    }
    @GetMapping("/balancete")
    public String getBalancete(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            Model model) {
        if (inicio != null && fim != null) {
            model.addAttribute("balancete", service.gerarBalancete(inicio, fim));
            model.addAttribute("inicio", inicio);
            model.addAttribute("fim", fim);
        }
        return "relatorios/balancete";
    }
    @GetMapping("/inadimplentes")
    public String getInadimplentes(Model model) {
        model.addAttribute("inadimplentes", service.gerarListaInadimplentes());
        return "relatorios/inadimplentes";
    }
    @GetMapping("/fluxo-caixa")
    public String getFluxoCaixa(Model model) {
        model.addAttribute("fluxo", service.gerarProjecaoFluxoCaixa());
        return "relatorios/fluxo-caixa";
    }
}