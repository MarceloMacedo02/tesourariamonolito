package br.com.sigest.tesouraria.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.sigest.tesouraria.domain.entity.ContaPagar;
import br.com.sigest.tesouraria.dto.ContaPagarDto;
import br.com.sigest.tesouraria.dto.PagamentoRequestDto;
import br.com.sigest.tesouraria.repository.FornecedorRepository;
import br.com.sigest.tesouraria.repository.RubricaRepository;
import br.com.sigest.tesouraria.service.ContaFinanceiraService;
import br.com.sigest.tesouraria.service.ContaPagarService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/contas-a-pagar")
public class ContaPagarController {

    @Autowired
    private ContaPagarService contaPagarService;

    @Autowired
    private RubricaRepository rubricaRepository;

    @Autowired
    private FornecedorRepository fornecedorRepository;

    @Autowired
    private ContaFinanceiraService contaFinanceiraService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("contas", contaPagarService.findAll());
        return "contas-a-pagar/listar";
    }

    @GetMapping("/nova")
    public String nova(Model model) {
        model.addAttribute("contaPagarDto", new ContaPagarDto());
        model.addAttribute("rubricas", rubricaRepository.findAll());
        model.addAttribute("fornecedores", fornecedorRepository.findAll());
        return "contas-a-pagar/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("contaPagarDto") ContaPagarDto dto, BindingResult result,
            RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("rubricas", rubricaRepository.findAll());
            model.addAttribute("fornecedores", fornecedorRepository.findAll());
            return "contas-a-pagar/formulario";
        }
        contaPagarService.criarContaPagar(dto);
        redirectAttributes.addFlashAttribute("success", "Conta a pagar criada com sucesso!");
        return "redirect:/contas-a-pagar";
    }

    @GetMapping("/pagar/{id}")
    public String pagar(@PathVariable Long id, Model model) {
        ContaPagar contaPagar = contaPagarService.findById(id);
        model.addAttribute("contaPagar", contaPagar);
        model.addAttribute("contasFinanceiras", contaFinanceiraService.findAll());
        model.addAttribute("pagamentoDto", PagamentoRequestDto.builder()
                .dataPagamento(LocalDate.now())
                .valor(contaPagar.getValor())
                .build());
        return "contas-a-pagar/form-pagamento";
    }

    @PostMapping("/registrar-pagamento/{id}")
    public String registrarPagamento(@PathVariable Long id, @Valid PagamentoRequestDto pagamentoDto,
            BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Erro de validação no pagamento.");
            return "redirect:/contas-a-pagar/pagar/" + id;
        }
        try {
            contaPagarService.liquidarContaPagar(id, pagamentoDto);
            redirectAttributes.addFlashAttribute("success", "Pagamento registrado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao registrar pagamento: " + e.getMessage());
        }
        return "redirect:/contas-a-pagar";
    }
}
