package br.com.sigest.tesouraria.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import br.com.sigest.tesouraria.security.UserDetailsImpl;
import br.com.sigest.tesouraria.service.DashboardService;

@Controller
public class DashboardController {

    @Autowired
    private DashboardService service;

    @GetMapping("/")
    public String home(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {

        // Extrai a role principal do utilizador para a decisão
        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("");

        try {
            switch (role) {
                case "ROLE_ADMIN":
                    model.addAttribute("resumoAdmin", service.getDashboardTesoureiro());
                    // Adicione aqui outros dados que o dashboard do admin precise, como
                    // 'ultimosUsuarios'
                    // model.addAttribute("ultimosUsuarios", service.getUltimosUsuarios());
                    return "admin-dashboard"; // Retorna a view do admin

                case "ROLE_TESOUREIRO":
                    model.addAttribute("resumo", service.getDashboardTesoureiro());
                    // Adicione aqui outros dados que o dashboard do tesoureiro precise, como
                    // 'ultimosLancamentos'
                    // model.addAttribute("ultimosLancamentos", service.getUltimosLancamentos());
                    return "dashboard_tesoureiro"; // CORRIGIDO: O nome do template estava errado

                case "ROLE_SOCIO":
                    model.addAttribute("resumoSocio", service.getDashboardSocio(userDetails.getUsuario()));
                    model.addAttribute("socio", userDetails.getUsuario().getSocio());
                    // Adicione aqui outros dados que o dashboard do sócio precise, como
                    // 'cobrancasAbertas'
                    // model.addAttribute("cobrancasAbertas",
                    // service.getCobrancasAbertas(userDetails.getUsuario()));
                    return "socio-dashboard"; // Retorna a view do sócio

                default:
                    // Uma página de erro ou acesso negado seria ideal aqui
                    return "redirect:/login?error";
            }
        } catch (Exception e) {
            // Logar o erro é crucial para depuração
            e.printStackTrace();
            // Retornar uma página de erro genérica para o utilizador
            return "error";
        }
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/login?logout";
    }
}
