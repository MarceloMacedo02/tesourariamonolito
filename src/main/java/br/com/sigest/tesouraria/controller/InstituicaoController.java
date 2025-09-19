package br.com.sigest.tesouraria.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.sigest.tesouraria.domain.entity.Instituicao;
import br.com.sigest.tesouraria.domain.repository.SocioRepository;
import br.com.sigest.tesouraria.exception.RegraNegocioException;
import br.com.sigest.tesouraria.service.InstituicaoService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/instituicoes")
public class InstituicaoController {

    @Autowired
    private InstituicaoService instituicaoService;

    @Autowired
    private SocioRepository socioRepository; // To fetch socios for dropdowns

    /**
     * Exibe o formulário de cadastro de instituição.
     *
     * @param model o modelo para a view
     * @return o nome da view do formulário de cadastro
     */
    @GetMapping("/cadastro")
    public String showCadastroForm(Model model) {
        Instituicao instituicao = instituicaoService.buscarInstituicao();
        if (instituicao == null) {
            instituicao = new Instituicao(); // New form for creation
        }
        model.addAttribute("instituicao", instituicao);
        model.addAttribute("cargos", instituicaoService.getAllCargoNames());
        model.addAttribute("sociosAtivos",
                socioRepository.findByStatus(br.com.sigest.tesouraria.domain.enums.StatusSocio.FREQUENTE));
        return "instituicoes/cadastro";
    }

    /**
     * Salva ou atualiza uma instituição.
     *
     * @param instituicao a instituição a ser salva
     * @param result      o resultado da validação
     * @param file        o arquivo de logo (opcional)
     * @param ra          os atributos de redirecionamento
     * @return o redirecionamento para a página de detalhes
     */
    @PostMapping("/salvar")
    public String salvarInstituicao(@Valid @ModelAttribute("instituicao") Instituicao instituicao, BindingResult result,
            @RequestParam(value = "file", required = false) MultipartFile file,
            RedirectAttributes ra) {
        if (result.hasErrors()) {
            ra.addFlashAttribute("org.springframework.validation.BindingResult.instituicao", result);
            ra.addFlashAttribute("instituicao", instituicao);
            return "redirect:/instituicoes/cadastro";
        }
        try {
            // Save the institution first
            Instituicao savedInstituicao = instituicaoService.criarOuAtualizarInstituicao(instituicao);

            // Handle file upload if present
            if (file != null && !file.isEmpty()) {
                instituicaoService.uploadLogo(savedInstituicao.getId(), file);
            }

            ra.addFlashAttribute("success", "Instituição salva com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Erro ao salvar instituição: " + e.getMessage());
            return "redirect:/instituicoes/cadastro";
        }
        return "redirect:/instituicoes/detalhes";
    }

    /**
     * Exibe os detalhes da instituição.
     *
     * @param model o modelo para a view
     * @return o nome da view de detalhes
     */
    @GetMapping("/detalhes")
    public String showDetalhes(Model model) {
        Instituicao instituicao = instituicaoService.buscarInstituicao();
        if (instituicao == null) {
            return "redirect:/instituicoes/cadastro"; // Redirect to cadastro if no institution exists
        }
        model.addAttribute("instituicao", instituicao);
        model.addAttribute("cargos", instituicaoService.getAllCargoNames());
        model.addAttribute("sociosAtivos",
                socioRepository.findByStatus(br.com.sigest.tesouraria.domain.enums.StatusSocio.FREQUENTE));
        return "instituicoes/detalhes";
    }

    /**
     * Faz o upload da logo da instituição.
     *
     * @param file o arquivo de logo
     * @param ra   os atributos de redirecionamento
     * @return o redirecionamento para a página de detalhes
     */
    @PostMapping("/logo")
    public String uploadLogo(@RequestParam("file") MultipartFile file, RedirectAttributes ra) {
        System.out.println("File received: " + (file != null ? file.getOriginalFilename() : "null") + ", isEmpty: "
                + (file != null ? file.isEmpty() : "N/A"));
        try {
            Instituicao instituicao = instituicaoService.buscarInstituicao();
            if (instituicao == null) {
                throw new RegraNegocioException("Nenhuma instituição cadastrada para fazer upload da logo.");
            }
            instituicaoService.uploadLogo(instituicao.getId(), file);
            ra.addFlashAttribute("success", "Logo enviada com sucesso!");
        } catch (RegraNegocioException | IOException | IllegalArgumentException e) {
            ra.addFlashAttribute("error", "Erro ao enviar logo: " + e.getMessage());
        }
        return "redirect:/instituicoes/detalhes";
    }

    /**
     * Faz o download da logo da instituição.
     *
     * @return a logo da instituição como um array de bytes
     */
    @GetMapping("/logo")
    public ResponseEntity<byte[]> downloadLogo() {
        try {
            Instituicao instituicao = instituicaoService.buscarInstituicao();
            if (instituicao == null || instituicao.getLogo() == null) {
                return ResponseEntity.notFound().build();
            }
            byte[] logo = instituicao.getLogo();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG); // Assuming PNG, adjust if needed
            headers.setContentLength(logo.length);
            return new ResponseEntity<>(logo, headers, HttpStatus.OK);
        } catch (RegraNegocioException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Or handle differently
        }
    }

    /**
     * Atribui um cargo a um sócio.
     *
     * @param cargo   o cargo a ser atribuído
     * @param socioId o ID do sócio
     * @param ra      os atributos de redirecionamento
     * @return o redirecionamento para a página de detalhes
     */
    @PostMapping("/cargo/{cargo}")
    public String atribuirCargo(@PathVariable("cargo") String cargo, @RequestParam("socioId") Long socioId,
            RedirectAttributes ra) {
        try {
            instituicaoService.atribuirCargo(socioId, cargo);
            ra.addFlashAttribute("success", "Cargo atribuído com sucesso!");
        } catch (RegraNegocioException | IllegalArgumentException | IllegalStateException e) {
            ra.addFlashAttribute("error", "Erro ao atribuir cargo: " + e.getMessage());
        }
        return "redirect:/instituicoes/detalhes";
    }

    /**
     * Remove um cargo de um sócio.
     *
     * @param cargo   o cargo a ser removido
     * @param socioId o ID do sócio
     * @param ra      os atributos de redirecionamento
     * @return o redirecionamento para a página de detalhes
     */
    @PostMapping("/cargo/{cargo}/remover")
    public String removerCargo(@PathVariable("cargo") String cargo, @RequestParam("socioId") Long socioId,
            RedirectAttributes ra) {
        try {
            instituicaoService.removerCargo(socioId, cargo);
            ra.addFlashAttribute("success", "Cargo removido com sucesso!");
        } catch (RegraNegocioException | IllegalArgumentException | IllegalStateException e) {
            ra.addFlashAttribute("error", "Erro ao remover cargo: " + e.getMessage());
        }
        return "redirect:/instituicoes/detalhes";
    }
}
