package br.com.sigest.tesouraria.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.sigest.tesouraria.service.SocioImportService;
import br.com.sigest.tesouraria.service.SocioImportService.ImportResult;

@Controller
public class SocioImportController {

    @Autowired
    private SocioImportService socioImportService;

    @GetMapping("/socios/importar")
    public String showImportForm() {
        return "cadastros/socios/importar";
    }

    @PostMapping("/socios/importar")
    public String importSocios(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Por favor, selecione um arquivo para importar.");
            redirectAttributes.addFlashAttribute("messageType", "danger");
            return "redirect:/socios/importar";
        }

        try {
            ImportResult result = socioImportService.processCsvFile(file);
            redirectAttributes.addFlashAttribute("message",
                    String.format("Importação concluída: %d sócio(s) inserido(s), %d sócio(s) atualizado(s).",
                            result.getInsertedCount(), result.getUpdatedCount()));
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Erro ao importar sócios: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "danger");
        }

        return "redirect:/socios/importar";
    }
}