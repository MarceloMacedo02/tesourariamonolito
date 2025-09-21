package br.com.sigest.tesouraria.controller;

import java.time.LocalDate;
import java.util.HashMap; // Added import
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity; // Added import
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody; // Added import
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody; // Added import
import org.springframework.web.multipart.MultipartFile;

import br.com.sigest.tesouraria.domain.entity.Cobranca; // Added import
import br.com.sigest.tesouraria.domain.enums.TipoRelacionamento;
import br.com.sigest.tesouraria.domain.enums.TipoRubrica;
import br.com.sigest.tesouraria.domain.enums.TipoTransacao;
import br.com.sigest.tesouraria.domain.repository.ContaFinanceiraRepository;
import br.com.sigest.tesouraria.domain.repository.FornecedorRepository;
import br.com.sigest.tesouraria.domain.repository.RubricaRepository;
import br.com.sigest.tesouraria.domain.repository.SocioRepository;
import br.com.sigest.tesouraria.dto.EditarContaReceberDto;
import br.com.sigest.tesouraria.dto.PagamentoRequestDto; // Added import
import br.com.sigest.tesouraria.dto.TransacaoDto;
import br.com.sigest.tesouraria.dto.TransacaoPagamentoRequestDto; // Added import
import br.com.sigest.tesouraria.dto.TransacaoProcessingResult; // Import the new DTO
import br.com.sigest.tesouraria.dto.ValidationResponse;
import br.com.sigest.tesouraria.service.CobrancaService; // Added import
import br.com.sigest.tesouraria.service.TransacaoService;

@Controller
@RequestMapping("/transacoes")
public class TransacaoController {

    @Autowired
    private TransacaoService transacaoService;

    @Autowired
    private ContaFinanceiraRepository contaFinanceiraRepository; // Added injection

    @Autowired
    private RubricaRepository rubricaRepository; // Added injection

    @Autowired
    private CobrancaService cobrancaService;

    @Autowired
    private FornecedorRepository fornecedorRepository;

    @Autowired
    private SocioRepository socioRepository;

    @Autowired
    private br.com.sigest.tesouraria.service.GrupoRubricaService grupoRubricaService;

    @GetMapping
    public String listTransacoes(
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "year", required = false) Integer year,
            Model model) {

        List<TransacaoDto> transacoes = transacaoService.findFilteredTransactions(month, year);
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
            TransacaoProcessingResult result = transacaoService.processOfxFile(file);

            int totalProcessadas = result.getCreditTransacoes().size() + result.getDebitTransacoes().size();
            int totalPendentes = result.getTransacoesPendentes().size();

            String mensagem = "Arquivo OFX processado com sucesso! " + totalProcessadas + " transações processadas.";

            if (totalPendentes > 0) {
                mensagem += " " + totalPendentes + " transações ficaram pendentes de associação com sócios.";
                return "redirect:/transacoes-pendentes?success=" + mensagem;
            } else {
                return "redirect:/transacoes/review?success=" + mensagem;
            }
        } catch (Exception e) {
            model.addAttribute("error", "Erro ao processar arquivo OFX: " + e.getMessage());
            return "transacoes/upload-ofx";
        }
    }

    @GetMapping("/review")
    public String reviewTransactions(
            @RequestParam(value = "mes", required = false) Integer mes,
            @RequestParam(value = "ano", required = false) Integer ano,
            @RequestParam(value = "success", required = false) String successMessage,
            Model model) {

        List<TransacaoDto> allTransacoes = transacaoService.findFilteredTransactions(mes, ano);

        List<TransacaoDto> creditTransacoes = allTransacoes.stream()
                .filter(t -> t.getTipo() == TipoTransacao.CREDITO)
                .collect(java.util.stream.Collectors.toList());
        List<TransacaoDto> debitTransacoes = allTransacoes.stream()
                .filter(t -> t.getTipo() == TipoTransacao.DEBITO)
                .collect(java.util.stream.Collectors.toList());

        model.addAttribute("creditTransacoes", creditTransacoes);
        model.addAttribute("debitTransacoes", debitTransacoes);
        model.addAttribute("currentMonth", mes != null ? mes : LocalDate.now().getMonthValue());
        model.addAttribute("currentYear", ano != null ? ano : LocalDate.now().getYear());

        Map<Integer, List<Integer>> availableDates = transacaoService.getAvailableMonthsAndYears();
        model.addAttribute("availableYears", availableDates.keySet());
        model.addAttribute("availableMonths", availableDates);

        if (successMessage != null) {
            model.addAttribute("success", successMessage);
        }

        return "transacoes/review-transactions";
    }

    @GetMapping("/despesas")
    public String listarDespesas(
            @RequestParam(value = "mes", required = false) Integer mes,
            @RequestParam(value = "ano", required = false) Integer ano,
            @RequestParam(value = "success", required = false) String successMessage,
            Model model) {

        List<TransacaoDto> allTransacoes = transacaoService.findFilteredTransactions(mes, ano);

        List<TransacaoDto> debitTransacoes = allTransacoes.stream()
                .filter(t -> t.getTipo() == TipoTransacao.DEBITO)
                .collect(java.util.stream.Collectors.toList());

        model.addAttribute("debitTransacoes", debitTransacoes);
        model.addAttribute("currentMonth", mes != null ? mes : LocalDate.now().getMonthValue());
        model.addAttribute("currentYear", ano != null ? ano : LocalDate.now().getYear());

        Map<Integer, List<Integer>> availableDates = transacaoService.getAvailableMonthsAndYears();
        model.addAttribute("availableYears", availableDates.keySet());
        model.addAttribute("availableMonths", availableDates);

        if (successMessage != null) {
            model.addAttribute("success", successMessage);
        }

        return "transacoes/despesas";
    }

    @GetMapping("/receitas")
    public String listarReceitas(
            @RequestParam(value = "mes", required = false) Integer mes,
            @RequestParam(value = "ano", required = false) Integer ano,
            @RequestParam(value = "success", required = false) String successMessage,
            Model model) {

        List<TransacaoDto> allTransacoes = transacaoService.findFilteredTransactions(mes, ano);

        List<TransacaoDto> creditTransacoes = allTransacoes.stream()
                .filter(t -> t.getTipo() == TipoTransacao.CREDITO)
                .collect(java.util.stream.Collectors.toList());

        model.addAttribute("creditTransacoes", creditTransacoes);
        model.addAttribute("currentMonth", mes != null ? mes : LocalDate.now().getMonthValue());
        model.addAttribute("currentYear", ano != null ? ano : LocalDate.now().getYear());

        Map<Integer, List<Integer>> availableDates = transacaoService.getAvailableMonthsAndYears();
        model.addAttribute("availableYears", availableDates.keySet());
        model.addAttribute("availableMonths", availableDates);

        if (successMessage != null) {
            model.addAttribute("success", successMessage);
        }

        return "transacoes/receitas";
    }

    @GetMapping("/{id}/detalhes")
    public String showTransactionDetails(@PathVariable("id") Long id, Model model) {
        TransacaoDto transacao = transacaoService.findTransactionById(id);
        model.addAttribute("transacao", transacao);

        if (transacao.getTipo() == TipoTransacao.CREDITO) {
            model.addAttribute("pagamentoDto", new PagamentoRequestDto());
            model.addAttribute("contasFinanceiras", contaFinanceiraRepository.findAll());
            model.addAttribute("rubricas", rubricaRepository.findAll());
            model.addAttribute("transacaoPagamentoRequestDto", new TransacaoPagamentoRequestDto());

            // Load all open charges for the socio and their dependents
            if (transacao.getTipoRelacionamento() == br.com.sigest.tesouraria.domain.enums.TipoRelacionamento.SOCIO) {
                model.addAttribute("allOpenCobrancas",
                        cobrancaService.findOpenCobrancasBySocioAndDependents(transacao.getRelacionadoId()));
                model.addAttribute("contasAReceber",
                        cobrancaService.findOutrasRubricasCobrancasBySocioAndDependents(transacao.getRelacionadoId()));
            } else {
                model.addAttribute("allOpenCobrancas", java.util.Collections.emptyList());
                model.addAttribute("contasAReceber", java.util.Collections.emptyList());
            }

            // Add all socios for the association modal
            model.addAttribute("socios", socioRepository.findAll());

            return "transacoes/detalhes-creditos";
        } else if (transacao.getTipo() == TipoTransacao.DEBITO) {
            model.addAttribute("contasFinanceiras", contaFinanceiraRepository.findAll());
            model.addAttribute("cobrancasAssociadas", cobrancaService.findAllOpenCobrancas());
            model.addAttribute("rubricasDespesa", rubricaRepository.findByTipo(TipoRubrica.DESPESA));
            model.addAttribute("fornecedores", fornecedorRepository.findAll());
            model.addAttribute("socios", socioRepository.findAll());

            // Attempt to find Fornecedor by name and set ID in DTO
            if (transacao.getRelacionadoId() != null
                    && transacao.getTipoRelacionamento() == TipoRelacionamento.FORNECEDOR) {
                transacao.setFornecedorId(transacao.getRelacionadoId());
            } else if (transacao.getRelacionadoId() != null
                    && transacao.getTipoRelacionamento() == TipoRelacionamento.SOCIO) {
                // Para sócios, não precisamos definir fornecedorId
            } else {
                fornecedorRepository.findByNome(transacao.getFornecedorOuSocio())
                        .ifPresent(fornecedor -> transacao.setFornecedorId(fornecedor.getId()));
            }

            return "transacoes/detalhes-debitos";
        } else {
            // Handle other types or a default case if necessary
            throw new IllegalArgumentException("Tipo de transação desconhecido: " + transacao.getTipo());
        }
    }

    @PostMapping("/{id}/selecionar-parte")
    public String selecionarParte(
            @PathVariable("id") Long id,
            @RequestParam("selectedParty") String selectedParty) {
        transacaoService.updateTransacaoWithSelectedParty(id, selectedParty);
        return "redirect:/transacoes/review";
    }

    @PostMapping("/{id}/quitar-cobrancas")
    @ResponseBody
    public ResponseEntity<?> quitarCobrancas(@PathVariable("id") Long id,
            @RequestBody TransacaoPagamentoRequestDto requestDto) {
        try {
            transacaoService.quitarCobrancas(id, requestDto.getCobrancaIds(), requestDto.getContaFinanceiraId());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao quitar cobranças: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/associar-socio")
    @ResponseBody
    public ResponseEntity<?> associarSocio(@PathVariable("id") Long id,
            @RequestBody Map<String, Long> requestBody) {
        try {
            Long socioId = requestBody.get("socioId");
            transacaoService.associarSocio(id, socioId);

            // Carregar as contas a receber e cobranças após associar o sócio
            TransacaoDto transacao = transacaoService.findTransactionById(id);
            List<Cobranca> allOpenCobrancas = cobrancaService.findOpenCobrancasBySocioAndDependents(socioId);
            List<Cobranca> contasAReceber = cobrancaService.findOutrasRubricasCobrancasBySocioAndDependents(socioId);

            // Criar um objeto de resposta com as informações atualizadas
            Map<String, Object> response = new HashMap<>();
            response.put("transacao", transacao);
            response.put("allOpenCobrancas", allOpenCobrancas);
            response.put("contasAReceber", contasAReceber);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao associar sócio: " + e.getMessage());
        }
    }

    @GetMapping("/{transacaoId}/contas-receber/{contaId}")
    @ResponseBody
    public ResponseEntity<?> obterContaReceber(@PathVariable("transacaoId") Long transacaoId,
            @PathVariable("contaId") Long contaId) {
        try {
            Cobranca conta = cobrancaService.findById(contaId);
            if (conta == null) {
                return ResponseEntity.status(404)
                        .body(ValidationResponse.error("Conta a receber não encontrada"));
            }
            return ResponseEntity.ok(conta);
        } catch (br.com.sigest.tesouraria.exception.RegraNegocioException e) {
            return ResponseEntity.status(404)
                    .body(ValidationResponse.error("Conta a receber não encontrada: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ValidationResponse.error("Erro interno ao buscar conta: " + e.getMessage()));
        }
    }

    @PutMapping("/{transacaoId}/contas-receber/{contaId}")
    @ResponseBody
    public ResponseEntity<?> editarContaReceber(@PathVariable("transacaoId") Long transacaoId,
            @PathVariable("contaId") Long contaId,
            @RequestBody EditarContaReceberDto dto) {
        try {
            cobrancaService.editarContaReceber(contaId, dto);
            return ResponseEntity.ok(ValidationResponse.success("Conta a receber editada com sucesso"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ValidationResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ValidationResponse.error("Erro ao editar conta: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{transacaoId}/contas-receber/{contaId}")
    @ResponseBody
    public ResponseEntity<?> excluirContaReceber(@PathVariable("transacaoId") Long transacaoId,
            @PathVariable("contaId") Long contaId) {
        try {
            cobrancaService.excluirContaReceber(contaId);
            return ResponseEntity.ok(ValidationResponse.success("Conta a receber excluída com sucesso"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ValidationResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ValidationResponse.error("Erro ao excluir conta: " + e.getMessage()));
        }
    }
}