package br.com.sigest.tesouraria.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.sigest.tesouraria.domain.enums.GrauSocio;
import br.com.sigest.tesouraria.dto.SocioDto;
import br.com.sigest.tesouraria.dto.SocioImportResultDTO;
import br.com.sigest.tesouraria.service.GrupoMensalidadeService;
import br.com.sigest.tesouraria.service.SocioService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/cadastros/socios")
public class SocioController {

    @Autowired
    private SocioService socioService;

    @Autowired
    private GrupoMensalidadeService grupoMensalidadeService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("socios", socioService.findAll());
        return "cadastros/socios/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("socioDto", new SocioDto());
        model.addAttribute("graus", GrauSocio.values());
        model.addAttribute("gruposMensalidade", grupoMensalidadeService.findAllDtos());
        return "cadastros/socios/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("socioDto") SocioDto socioDto, RedirectAttributes redirect) {
        try {
            socioService.save(socioDto);
            redirect.addFlashAttribute("success", "Sócio salvo com sucesso!");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Erro ao salvar: " + e.getMessage());
        }
        return "redirect:/cadastros/socios";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("socioDto", socioService.findByIdAsDto(id));
        model.addAttribute("graus", GrauSocio.values());
        model.addAttribute("gruposMensalidade", grupoMensalidadeService.findAllDtos());
        return "cadastros/socios/formulario";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            socioService.delete(id);
            redirect.addFlashAttribute("success", "Sócio excluído com sucesso!");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Erro ao excluir: " + e.getMessage());
        }
        return "redirect:/cadastros/socios";
    }

    @GetMapping("/grid")
    public String gridListar(Model model) {
        model.addAttribute("socios", socioService.findAll());
        return "cadastros/socios/grid";
    }

    @GetMapping("/importar")
    public String importarForm() {
        return "cadastros/socios/importar";
    }

    @PostMapping("/importar")
    public String importarCsv(@RequestParam("file") MultipartFile file, RedirectAttributes redirect) {
        if (file.isEmpty()) {
            redirect.addFlashAttribute("error", "Por favor, selecione um arquivo para importar.");
            return "redirect:/cadastros/socios/importar";
        }

        try {
            SocioImportResultDTO result = socioService.importSociosFromCsv(file);
            String message = String.format(
                    "Importação concluída: %d sócios inseridos, %d sócios atualizados, %d erros.",
                    result.getInsertedCount(), result.getUpdatedCount(), result.getErrorCount());
            redirect.addFlashAttribute("success", message);
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Erro ao importar o arquivo: " + e.getMessage());
        }
        return "redirect:/cadastros/socios";
    }
}