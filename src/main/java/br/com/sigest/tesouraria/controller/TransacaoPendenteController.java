package br.com.sigest.tesouraria.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.sigest.tesouraria.domain.entity.Socio;
import br.com.sigest.tesouraria.domain.entity.TransacaoPendente;
import br.com.sigest.tesouraria.service.SocioService;
import br.com.sigest.tesouraria.service.TransacaoPendenteService;

@Controller
@RequestMapping("/transacoes-pendentes")
public class TransacaoPendenteController {

    @Autowired
    private TransacaoPendenteService transacaoPendenteService;

    @Autowired
    private SocioService socioService;

    @GetMapping
    public String listar(Model model) {
        List<TransacaoPendente> transacoesPendentes = transacaoPendenteService.findAllPendentes();
        model.addAttribute("transacoesPendentes", transacoesPendentes);
        return "transacoes/pendentes";
    }

    @GetMapping("/associar/{id}")
    public String mostrarFormularioAssociacao(@PathVariable Long id, Model model) {
        TransacaoPendente transacaoPendente = transacaoPendenteService.findById(id);
        List<Socio> socios = socioService.findAll();

        model.addAttribute("transacaoPendente", transacaoPendente);
        model.addAttribute("socios", socios);
        return "transacoes/associar-socio";
    }

    @PostMapping("/associar/{id}")
    public String associarSocio(@PathVariable Long id,
            @RequestParam Long socioId,
            RedirectAttributes redirectAttributes) {
        try {
            transacaoPendenteService.associarSocio(id, socioId);
            redirectAttributes.addFlashAttribute("success", "Transação associada ao sócio com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao associar transação: " + e.getMessage());
        }
        return "redirect:/transacoes-pendentes";
    }

    @PostMapping("/descartar/{id}")
    public String descartarTransacao(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            transacaoPendenteService.descartarTransacao(id);
            redirectAttributes.addFlashAttribute("success", "Transação descartada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao descartar transação: " + e.getMessage());
        }
        return "redirect:/transacoes-pendentes";
    }
}