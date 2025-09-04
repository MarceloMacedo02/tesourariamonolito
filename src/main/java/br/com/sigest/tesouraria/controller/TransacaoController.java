package br.com.sigest.tesouraria.controller;

import br.com.sigest.tesouraria.dto.TransacaoDto;
import br.com.sigest.tesouraria.service.TransacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/transacoes")
public class TransacaoController {

    @Autowired
    private TransacaoService transacaoService;

    @GetMapping
    public String listTransacoes(
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "year", required = false) Integer year,
            Model model) {

        List<TransacaoDto> transacoes = transacaoService.findAllTransactions(month, year);
        model.addAttribute("transacoes", transacoes);

        Map<Integer, List<Integer>> availableDates = transacaoService.getAvailableMonthsAndYears();
        model.addAttribute("availableYears", availableDates.keySet());
        model.addAttribute("availableMonths", availableDates);

        // Set currentMonth and currentYear for pre-selection in dropdowns
        if (year == null && !availableDates.isEmpty()) {
            year = availableDates.keySet().stream().findFirst().orElse(LocalDate.now().getYear());
        }
        if (month == null && year != null && availableDates.containsKey(year) && !availableDates.get(year).isEmpty()) {
            month = availableDates.get(year).get(0);
        }

        model.addAttribute("currentMonth", month != null ? month : LocalDate.now().getMonthValue());
        model.addAttribute("currentYear", year != null ? year : LocalDate.now().getYear());

        return "transacoes/upload-ofx";
    }

    @GetMapping("/upload") // This will now redirect to the list view
    public String showUploadFormRedirect() {
        return "redirect:/transacoes";
    }

    @PostMapping("/upload")
    public String uploadOfxFile(@RequestParam("file") MultipartFile file, Model model) {
        try {
            List<TransacaoDto> processedTransacoes = transacaoService.processOfxFile(file);
            model.addAttribute("success", "Arquivo OFX processado com sucesso! " + processedTransacoes.size() + " transações processadas.");
        } catch (Exception e) {
            model.addAttribute("error", "Erro ao processar arquivo OFX: " + e.getMessage());
        }
        // After upload, redirect to the list view to show updated data
        return "redirect:/transacoes";
    }
}
