package br.com.sigest.tesouraria.config;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalControllerAdvice {

    /**
     * Adiciona atributos globais ao modelo.
     *
     * @param model   o modelo para a view
     * @param request a requisição HTTP
     */
    @ModelAttribute
    public void addAttributes(Model model, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());
    }

}
