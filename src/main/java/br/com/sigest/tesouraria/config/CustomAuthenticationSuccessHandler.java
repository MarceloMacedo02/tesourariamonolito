package br.com.sigest.tesouraria.config;

import java.io.IOException;
import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    /**
     * Chamado quando um usuário é autenticado com sucesso.
     *
     * @param request        a requisição
     * @param response       a resposta
     * @param authentication a autenticação
     * @throws IOException      se ocorrer um erro de I/O
     * @throws ServletException se ocorrer um erro de servlet
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(authentication);

        if (response.isCommitted()) {
            return;
        }

        response.sendRedirect(targetUrl);
        response.flushBuffer();
    }

    /**
     * Determina a URL de destino com base nas roles do usuário.
     *
     * @param authentication a autenticação
     * @return a URL de destino
     */
    protected String determineTargetUrl(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "/dashboard/admin";
        } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_TESOUREIRO"))) {
            return "/dashboard/tesoureiro";
        } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_SOCIO"))) {
            return "/dashboard/socio";
        } else {
            return "/";
        }
    }
}
