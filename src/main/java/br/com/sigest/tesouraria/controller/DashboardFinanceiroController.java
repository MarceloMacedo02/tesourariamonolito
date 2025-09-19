package br.com.sigest.tesouraria.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import br.com.sigest.tesouraria.dto.DashboardFinanceiroDto;
import br.com.sigest.tesouraria.service.DashboardFinanceiroService;

@Controller
@RequestMapping("/dashboard/financeiro")
public class DashboardFinanceiroController {
    
    @Autowired
    private DashboardFinanceiroService dashboardFinanceiroService;
    
    @GetMapping
    public String exibirDashboard(Model model) {
        // Adiciona dados iniciais ao modelo, se necessário
        // Os dados reais serão carregados via AJAX
        return "dashboard";
    }
    
    @GetMapping("/dados")
    @ResponseBody
    public ResponseEntity<DashboardFinanceiroDto> obterDadosDashboard() {
        DashboardFinanceiroDto dados = dashboardFinanceiroService.getDadosDashboard();
        return ResponseEntity.ok(dados);
    }
    
    @GetMapping("/dados/mensais")
    @ResponseBody
    public ResponseEntity<DashboardFinanceiroDto> obterDadosMensaisPorAno(@RequestParam int ano) {
        DashboardFinanceiroDto dados = dashboardFinanceiroService.getDadosMensaisPorAno(ano);
        return ResponseEntity.ok(dados);
    }
}