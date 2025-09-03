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
    public String home(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("");

        switch (role) {
            case "ROLE_ADMIN":
                return "redirect:/dashboard/admin";
            case "ROLE_TESOUREIRO":
                return "redirect:/dashboard/tesoureiro";
            case "ROLE_SOCIO":
                return "redirect:/dashboard/socio";
            default:
                return "redirect:/login?error";
        }
    }

    @GetMapping("/dashboard/admin")
    public String adminDashboard(Model model) {
        model.addAttribute("resumoAdmin", service.getDashboardTesoureiro());
        return "admin-dashboard";
    }

    @GetMapping("/dashboard/tesoureiro")
    public String tesoureiroDashboard(Model model) {
        model.addAttribute("resumo", service.getDashboardTesoureiro());
        return "dashboard_tesoureiro";
    }

    @GetMapping("/dashboard/socio")
    public String socioDashboard(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
        model.addAttribute("resumoSocio", service.getDashboardSocio(userDetails.getUsuario()));
        model.addAttribute("socio", userDetails.getUsuario().getSocio());
        return "dashboard_socio";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
