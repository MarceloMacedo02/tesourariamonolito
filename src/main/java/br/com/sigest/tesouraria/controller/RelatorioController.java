package br.com.sigest.tesouraria.controller;

import br.com.sigest.tesouraria.service.CentroCustoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/relatorios")
public class RelatorioController {

    @Autowired
    private CentroCustoService centroCustoService;

    @GetMapping("/balancete-centro-custos")
    public String relatorioCentroCustos(Model model) {
        List<br.com.sigest.tesouraria.domain.entity.CentroCusto> centrosDeCusto = centroCustoService.findAllEntities();
        model.addAttribute("centrosDeCusto", centrosDeCusto);
        return "relatorios/balancete-centro-custos"; // This will be the Thymeleaf template name
    }
}