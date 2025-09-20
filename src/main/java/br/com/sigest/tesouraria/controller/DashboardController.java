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
    
    @Autowired
    private DashboardFinanceiroService dashboardFinanceiroService;

    /**
     * Redireciona para o dashboard apropriado com base na role do usuário.
     *
     * @param userDetails os detalhes do usuário autenticado
     * @return o redirecionamento para o dashboard apropriado
     */
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

    /**
     * Exibe o dashboard do administrador.
     *
     * @param model o modelo para a view
     * @return o nome da view do dashboard do administrador
     */
    @GetMapping("/dashboard/admin")
    public String adminDashboard(Model model) {
        model.addAttribute("resumoAdmin", service.getDashboardTesoureiro());
        return "admin-dashboard";
    }

    /**
     * Exibe o dashboard do tesoureiro.
     *
     * @param model o modelo para a view
     * @return o nome da view do dashboard do tesoureiro
     */
    @GetMapping("/dashboard/tesoureiro")
    public String tesoureiroDashboard(Model model) {
        // Usar o novo serviço de dashboard financeiro
        DashboardFinanceiroDto dashboard = dashboardFinanceiroService.getDadosDashboard();
        model.addAttribute("dashboard", dashboard);
        return "dashboard_tesoureiro";
    }

    /**
     * Exibe o dashboard do sócio.
     *
     * @param userDetails os detalhes do usuário autenticado
     * @param model       o modelo para a view
     * @return o nome da view do dashboard do sócio
     */
    @GetMapping("/dashboard/socio")
    public String socioDashboard(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
        model.addAttribute("resumoSocio", service.getDashboardSocio(userDetails.getUsuario()));
        model.addAttribute("socio", userDetails.getUsuario().getSocio());
        return "dashboard_socio";
    }

    /**
     * Exibe a página de login.
     *
     * @return o nome da view de login
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
