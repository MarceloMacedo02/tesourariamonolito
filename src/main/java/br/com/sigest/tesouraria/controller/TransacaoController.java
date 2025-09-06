package br.com.sigest.tesouraria.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import br.com.sigest.tesouraria.dto.TransacaoDto;
import br.com.sigest.tesouraria.dto.TransacaoProcessingResult; // Import the new DTO
import br.com.sigest.tesouraria.service.TransacaoService;

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
            TransacaoProcessingResult result = transacaoService.processOfxFile(file); // Get the new result object
            model.addAttribute("creditTransacoes", result.getCreditTransacoes());
            model.addAttribute("debitTransacoes", result.getDebitTransacoes());
            model.addAttribute("success",
                    "Arquivo OFX processado com sucesso! " + (result.getCreditTransacoes().size() + result.getDebitTransacoes().size()) + " transações processadas.");
            return "transacoes/review-transactions"; // Forward to a new review page
        } catch (Exception e) {
            model.addAttribute("error", "Erro ao processar arquivo OFX: " + e.getMessage());
            return "transacoes/upload-ofx"; // Stay on the upload page with error
        }
    }

    @GetMapping("/{id}/detalhes")
    public String showTransactionDetails(@PathVariable("id") Long id, Model model) {
        TransacaoDto transacao = transacaoService.findTransactionById(id);
        model.addAttribute("transacao", transacao);
        return "transacoes/detalhes";
    }

    @PutMapping("/{id}/selecionar-parte")
    public String selecionarParte(
            @PathVariable("id") Long id,
            @RequestParam("selectedParty") String selectedParty) {
        transacaoService.updateTransacaoWithSelectedParty(id, selectedParty);
        return "redirect:/transacoes";
    }

    @PostMapping("/{id}/quitar-cobrancas")
    public String quitarCobrancas(@PathVariable("id") Long id, @RequestParam("cobrancaIds") List<Long> cobrancaIds) {
        transacaoService.quitarCobrancas(id, cobrancaIds);
        return "redirect:/transacoes";
    }
}