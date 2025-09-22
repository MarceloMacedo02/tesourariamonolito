package br.com.sigest.tesouraria.controller;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map; // Import Map for parameters

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.sigest.tesouraria.domain.entity.GrupoRubrica; // Import GrupoRubrica
import br.com.sigest.tesouraria.domain.entity.Instituicao;
import br.com.sigest.tesouraria.domain.repository.InstituicaoRepository;
import br.com.sigest.tesouraria.domain.repository.MovimentoRepository;
import br.com.sigest.tesouraria.dto.RelatorioDemonstrativoFinanceiroDto;
import br.com.sigest.tesouraria.dto.RelatorioDemonstrativoFinanceiroPorGrupoRubricaDto;
import br.com.sigest.tesouraria.dto.RelatorioEntradasDetalhadasDto;
import br.com.sigest.tesouraria.dto.RelatorioFinanceiroGruposRubricaDto;
import br.com.sigest.tesouraria.service.CobrancaService;
import br.com.sigest.tesouraria.service.GrupoRubricaService;
import br.com.sigest.tesouraria.service.PdfService;
import br.com.sigest.tesouraria.service.RelatorioService;

@Controller
@RequestMapping("/relatorios")
public class RelatorioController {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(RelatorioController.class);

    @Autowired
    private GrupoRubricaService grupoRubricaService;

    @Autowired
    private RelatorioService relatorioService;

    @Autowired
    private MovimentoRepository movimentoRepository;

    @Autowired
    private InstituicaoRepository instituicaoRepository;

    @Autowired
    private CobrancaService cobrancaService;

    @Autowired
    private PdfService pdfService;

    @GetMapping("/inadimplentes")
    public String relatorioInadimplentes(Model model) {
        List<br.com.sigest.tesouraria.dto.RelatorioInadimplentesDto> inadimplentes = cobrancaService
                .gerarRelatorioInadimplentes();
        java.math.BigDecimal totalGeral = inadimplentes.stream()
                .map(br.com.sigest.tesouraria.dto.RelatorioInadimplentesDto::getValorTotalAberto)
                .filter(java.util.Objects::nonNull)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        model.addAttribute("inadimplentes", inadimplentes);
        model.addAttribute("totalGeral", totalGeral);
        return "relatorios/inadimplentes";
    }

    @GetMapping("/balancete-grupos-rubrica")
    public String relatorioCentroCustos(Model model) {
        List<br.com.sigest.tesouraria.domain.entity.GrupoRubrica> gruposDeRubrica = grupoRubricaService
                .findAllEntities();
        model.addAttribute("gruposDeRubrica", gruposDeRubrica);
        return "relatorios/balancete-grupos-rubrica";
    }

    @GetMapping("/balancete-centro-custos/pdf")
    public ResponseEntity<byte[]> gerarRelatorioCentroCustosPdf() {
        try {
            List<GrupoRubrica> gruposDeRubrica = grupoRubricaService.findAllEntities();

            // Buscar informações da instituição
            Instituicao instituicao = instituicaoRepository.findAll().stream().findFirst().orElse(null);

            // Preparar variáveis para o template
            Map<String, Object> variables = new HashMap<>();
            variables.put("gruposDeRubrica", gruposDeRubrica);
            variables.put("dataGeracao", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));

            // Informações da instituição
            preencherDadosInstituicao(variables, instituicao);

            // Gerar PDF usando Thymeleaf + Flying Saucer
            byte[] pdfBytes = pdfService.generatePdf("relatorios/pdf/balancete-centro-custos-pdf", variables);

            return enviarParaDownload(pdfBytes, "balancete_centro_custos");

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/inadimplentes/pdf")
    public ResponseEntity<byte[]> gerarRelatorioInadimplentesPdf() {
        try {
            List<br.com.sigest.tesouraria.dto.RelatorioInadimplentesDto> inadimplentes = cobrancaService
                    .gerarRelatorioInadimplentes();
            java.math.BigDecimal totalGeral = inadimplentes.stream()
                    .map(br.com.sigest.tesouraria.dto.RelatorioInadimplentesDto::getValorTotalAberto)
                    .filter(java.util.Objects::nonNull)
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

            // Buscar informações da instituição
            Instituicao instituicao = instituicaoRepository.findAll().stream().findFirst().orElse(null);

            // Preparar variáveis para o template
            Map<String, Object> variables = new HashMap<>();
            variables.put("inadimplentes", inadimplentes);
            variables.put("totalGeral", totalGeral);
            variables.put("dataGeracao", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));

            // Informações da instituição
            preencherDadosInstituicao(variables, instituicao);

            // Gerar PDF usando Thymeleaf + Flying Saucer
            byte[] pdfBytes = pdfService.generatePdf("relatorios/pdf/inadimplentes-pdf", variables);

            return enviarParaDownload(pdfBytes, "relatorio_inadimplentes");

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/demonstrativo-financeiro")
    public String demonstrativoFinanceiro(Model model,
            @RequestParam(value = "mes", required = false) Integer mes,
            @RequestParam(value = "ano", required = false) Integer ano) {
        if (mes == null || ano == null) {
            mes = LocalDate.now().getMonthValue();
            ano = LocalDate.now().getYear();
        }
        RelatorioDemonstrativoFinanceiroDto demonstrativo = relatorioService.gerarDemonstrativoFinanceiro(mes, ano);
        model.addAttribute("demonstrativo", demonstrativo);
        return "relatorios/demonstrativo-financeiro";
    }

    @GetMapping("/demonstrativo-financeiro-mensal")
    public String demonstrativoFinanceiroMensal(Model model,
            @RequestParam(value = "mes", required = false) Integer mes,
            @RequestParam(value = "ano", required = false) Integer ano) {
        if (mes == null || ano == null) {
            mes = LocalDate.now().getMonthValue();
            ano = LocalDate.now().getYear();
        }
        RelatorioDemonstrativoFinanceiroDto demonstrativo = relatorioService.gerarDemonstrativoFinanceiro(mes, ano);
        model.addAttribute("demonstrativo", demonstrativo);
        return "relatorios/demonstrativo-financeiro-mensal";
    }

    @GetMapping("/demonstrativo-financeiro-mensal-centro-custo")
    public String demonstrativoFinanceiroMensalPorCentroCusto(Model model,
            @RequestParam(value = "mes", required = false) Integer mes,
            @RequestParam(value = "ano", required = false) Integer ano) {
        if (mes == null || ano == null) {
            mes = LocalDate.now().getMonthValue();
            ano = LocalDate.now().getYear();
        }
        RelatorioDemonstrativoFinanceiroPorGrupoRubricaDto demonstrativo = relatorioService
                .gerarDemonstrativoFinanceiroPorGrupoRubrica(mes, ano);
        model.addAttribute("demonstrativo", demonstrativo);
        return "relatorios/demonstrativo-financeiro-mensal-centro-custo";
    }

    @GetMapping("/financeiro-centro-custo")
    public String relatorioFinanceiroPorCentroCusto(Model model,
            @RequestParam(value = "mes", required = false) Integer mes,
            @RequestParam(value = "ano", required = false) Integer ano) {
        if (mes == null || ano == null) {
            mes = LocalDate.now().getMonthValue();
            ano = LocalDate.now().getYear();
        }
        RelatorioDemonstrativoFinanceiroPorGrupoRubricaDto relatorio = relatorioService
                .gerarDemonstrativoFinanceiroPorGrupoRubrica(mes, ano);
        model.addAttribute("relatorio", relatorio);
        return "relatorios/relatorio-financeiro-accordion";
    }

    @GetMapping("/menu")
    public String menuRelatorios() {
        return "relatorios/menu-relatorios";
    }

    @GetMapping("/debug/movimentos")
    public ResponseEntity<String> debugMovimentos(
            @RequestParam(value = "mes", required = false) Integer mes,
            @RequestParam(value = "ano", required = false) Integer ano) {
        if (mes == null || ano == null) {
            mes = LocalDate.now().getMonthValue();
            ano = LocalDate.now().getYear();
        }

        // Definir o período de busca
        java.time.YearMonth yearMonth = java.time.YearMonth.of(ano, mes);
        LocalDateTime inicio = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime fim = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        // Buscar movimentos
        List<br.com.sigest.tesouraria.domain.entity.Movimento> movimentos = movimentoRepository
                .findByDataHoraBetween(inicio, fim);

        StringBuilder debug = new StringBuilder();
        debug.append("Período: ").append(mes).append("/").append(ano).append("\n");
        debug.append("Início: ").append(inicio).append("\n");
        debug.append("Fim: ").append(fim).append("\n");
        debug.append("Total de movimentos encontrados: ").append(movimentos.size()).append("\n\n");

        for (br.com.sigest.tesouraria.domain.entity.Movimento mov : movimentos) {
            debug.append("ID: ").append(mov.getId())
                    .append(", Data: ").append(mov.getDataHora())
                    .append(", Tipo: ").append(mov.getTipo())
                    .append(", Valor: ").append(mov.getValor())
                    .append(", Origem/Destino: ").append(mov.getOrigemDestino())
                    .append("\n");
        }

        return ResponseEntity.ok()
                .header("Content-Type", "text/plain; charset=utf-8")
                .body(debug.toString());
    }

    @GetMapping("/demonstrativo-financeiro-mensal-grupos-rubrica")
    public String demonstrativoFinanceiroMensalGruposRubrica(Model model,
            @RequestParam(value = "mes", required = false) Integer mes,
            @RequestParam(value = "ano", required = false) Integer ano) {
        if (mes == null || ano == null) {
            mes = LocalDate.now().getMonthValue();
            ano = LocalDate.now().getYear();
        }

        // Buscar dados do demonstrativo
        RelatorioDemonstrativoFinanceiroPorGrupoRubricaDto demonstrativo = relatorioService
                .gerarDemonstrativoFinanceiroPorGrupoRubrica(mes, ano);

        // Adicionar anos disponíveis para o select
        List<Integer> years = new ArrayList<>();
        int currentYear = LocalDate.now().getYear();
        for (int i = currentYear - 5; i <= currentYear + 1; i++) {
            years.add(i);
        }

        // Buscar grupos de mensalidade e rubricas para os filtros
        List<br.com.sigest.tesouraria.domain.entity.GrupoRubrica> gruposMensalidade = grupoRubricaService
                .findAllEntities();

        model.addAttribute("demonstrativo", demonstrativo);
        model.addAttribute("years", years);
        model.addAttribute("gruposMensalidade", gruposMensalidade);
        model.addAttribute("rubricas", new ArrayList<>()); // TODO: Implementar busca de rubricas
        model.addAttribute("mes", mes);
        model.addAttribute("ano", ano);

        return "relatorios/demonstrativo-financeiro-mensal-grupos-rubrica";
    }

    @GetMapping("/demonstrativo-financeiro-mensal-grupos-rubrica/gerar")
    public ResponseEntity<byte[]> gerarDemonstrativoFinanceiroMensalGruposRubricaPdf(
            @RequestParam Integer mes,
            @RequestParam Integer ano,
            @RequestParam(defaultValue = "PDF") String formato,
            @RequestParam(required = false) List<Long> gruposMensalidade,
            @RequestParam(required = false) List<Long> rubricas,
            @RequestParam(defaultValue = "false") boolean incluirDetalhes,
            @RequestParam(defaultValue = "true") boolean apenasComMovimento) {
        try {
            // Buscar dados usando o método correto para grupos de rubrica
            RelatorioDemonstrativoFinanceiroPorGrupoRubricaDto demonstrativo = relatorioService
                    .gerarDemonstrativoFinanceiroPorGrupoRubrica(mes, ano);

            // Buscar informações da instituição
            Instituicao instituicao = instituicaoRepository.findAll().stream().findFirst().orElse(null);

            // Preparar variáveis para o template
            Map<String, Object> variables = new HashMap<>();
            variables.put("demonstrativo", demonstrativo);
            variables.put("mes", mes);
            variables.put("ano", ano);
            variables.put("mesNome", obterNomeMes(mes));
            variables.put("dataGeracao", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));

            // Informações da instituição
            preencherDadosInstituicao(variables, instituicao);

            // Gerar PDF usando Thymeleaf + Flying Saucer
            byte[] pdfBytes = pdfService.generatePdf("relatorios/pdf/demonstrativo-financeiro-pdf", variables);

            if ("EXCEL".equalsIgnoreCase(formato)) {
                // Para Excel, retornar erro por enquanto ou implementar conversão
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                        .body("Formato Excel não implementado ainda".getBytes());
            } else if ("HTML".equalsIgnoreCase(formato)) {
                // Para HTML, retornar erro por enquanto ou implementar conversão
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                        .body("Formato HTML não implementado ainda".getBytes());
            } else {
                // PDF por padrão
                return enviarParaDownload(pdfBytes, "demonstrativo_financeiro_grupos_rubrica_" + mes + "_" + ano);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/demonstrativo-financeiro-mensal-grupos-rubrica/resumo")
    public ResponseEntity<Map<String, Object>> obterResumoFinanceiro(
            @RequestParam Integer mes,
            @RequestParam Integer ano,
            @RequestParam(required = false) List<Long> gruposMensalidade,
            @RequestParam(required = false) List<Long> rubricas,
            @RequestParam(defaultValue = "true") boolean apenasComMovimento) {
        try {
            RelatorioDemonstrativoFinanceiroDto demonstrativo = relatorioService.gerarDemonstrativoFinanceiro(mes, ano);

            Map<String, Object> resumo = new java.util.HashMap<>();
            resumo.put("totalEntradas", demonstrativo.getTotalEntradas());
            resumo.put("totalSaidas", demonstrativo.getTotalSaidas());
            resumo.put("saldoOperacional", demonstrativo.getSaldoOperacional());
            resumo.put("gruposComMovimento",
                    demonstrativo.getEntradasAgrupadas().size() + demonstrativo.getSaidasAgrupadas().size());

            return ResponseEntity.ok(resumo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/demonstrativo-financeiro-mensal-grupos-rubrica/pdf-thymeleaf")
    public ResponseEntity<byte[]> gerarDemonstrativoFinanceiroThymeleafPdf(
            @RequestParam Integer mes,
            @RequestParam Integer ano,
            @RequestParam(required = false) List<Long> gruposMensalidade,
            @RequestParam(required = false) List<Long> rubricas,
            @RequestParam(defaultValue = "false") boolean incluirDetalhes,
            @RequestParam(defaultValue = "true") boolean apenasComMovimento) {

        logger.info("=== GERANDO PDF COM THYMELEAF + FLYING SAUCER ===");
        logger.info("Parâmetros: mes={}, ano={}", mes, ano);

        try {
            // Buscar dados do demonstrativo
            RelatorioDemonstrativoFinanceiroPorGrupoRubricaDto demonstrativo = relatorioService
                    .gerarDemonstrativoFinanceiroPorGrupoRubrica(mes, ano);

            // Buscar informações da instituição
            Instituicao instituicao = instituicaoRepository.findAll().stream().findFirst().orElse(null);

            // Preparar variáveis para o template
            Map<String, Object> variables = new HashMap<>();
            variables.put("demonstrativo", demonstrativo);
            variables.put("mes", mes);
            variables.put("ano", ano);
            variables.put("mesNome", obterNomeMes(mes));
            variables.put("dataGeracao", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));

            // Informações da instituição
            if (instituicao != null) {
                variables.put("instituicaoNome", instituicao.getNome());
                variables.put("instituicaoEndereco", instituicao.getEndereco());

                // Converter logo para Base64 se existir
                if (instituicao.getLogo() != null) {
                    String logoBase64 = java.util.Base64.getEncoder().encodeToString(instituicao.getLogo());
                    variables.put("instituicaoLogo", logoBase64);
                }
            } else {
                variables.put("instituicaoNome", "Nome da Instituição Não Encontrado");
                variables.put("instituicaoEndereco", "Endereço Não Encontrado");
            }

            // Gerar PDF usando Thymeleaf + Flying Saucer
            logger.info("Chamando PdfService.generatePdf com template: relatorios/pdf/demonstrativo-financeiro-pdf");
            byte[] pdfBytes = pdfService.generatePdf("relatorios/pdf/demonstrativo-financeiro-pdf", variables);
            logger.info("PDF gerado com sucesso! Tamanho: {} bytes", pdfBytes.length);

            // Configurar headers para download
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String nomeArquivo = "demonstrativo_financeiro_thymeleaf_" + mes + "_" + ano + "_" + timestamp + ".pdf";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", nomeArquivo);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Método auxiliar para obter o nome do mês
     */
    private String obterNomeMes(Integer mes) {
        String[] meses = {
                "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
                "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
        };
        return (mes >= 1 && mes <= 12) ? meses[mes - 1] : "Mês Inválido";
    }

    @GetMapping("/financeiro-grupos-rubrica-detalhado")
    public String relatorioFinanceiroPorGruposRubricaDetalhado(Model model,
            @RequestParam(value = "mes", required = false) Integer mes,
            @RequestParam(value = "ano", required = false) Integer ano) {
        if (mes == null || ano == null) {
            mes = LocalDate.now().getMonthValue();
            ano = LocalDate.now().getYear();
        }
        RelatorioFinanceiroGruposRubricaDto relatorio = relatorioService
                .gerarRelatorioFinanceiroGruposRubrica(mes, ano);
        model.addAttribute("relatorio", relatorio);
        model.addAttribute("mes", mes);
        model.addAttribute("ano", ano);
        return "relatorios/financeiro-grupos-rubrica-detalhado";
    }

    @GetMapping("/financeiro-grupos-rubrica-detalhado/pdf")
    public ResponseEntity<byte[]> gerarRelatorioFinanceiroPorGruposRubricaDetalhadoPdf(
            @RequestParam(value = "mes", required = false) Integer mes,
            @RequestParam(value = "ano", required = false) Integer ano) {
        try {
            if (mes == null || ano == null) {
                mes = LocalDate.now().getMonthValue();
                ano = LocalDate.now().getYear();
            }

            // Buscar os dados do relatório
            RelatorioFinanceiroGruposRubricaDto relatorio = relatorioService
                    .gerarRelatorioFinanceiroGruposRubrica(mes, ano);

            // Buscar informações da instituição
            Instituicao instituicao = instituicaoRepository.findAll().stream().findFirst().orElse(null);

            // Preparar variáveis para o template
            Map<String, Object> variables = new HashMap<>();
            variables.put("relatorio", relatorio);
            variables.put("mes", mes);
            variables.put("ano", ano);
            variables.put("mesNome", obterNomeMes(mes));
            variables.put("dataGeracao", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));

            // Informações da instituição
            preencherDadosInstituicao(variables, instituicao);

            // Gerar PDF usando Thymeleaf + Flying Saucer
            byte[] pdfBytes = pdfService.generatePdf("relatorios/pdf/financeiro-grupos-rubrica-detalhado-pdf",
                    variables);

            return enviarParaDownload(pdfBytes, "relatorio_financeiro_detalhado_" + mes + "_" + ano);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/entradas-detalhadas")
    public String entradasDetalhadas(Model model,
            @RequestParam(value = "mes", required = false) Integer mes,
            @RequestParam(value = "ano", required = false) Integer ano) {
        logger.info("Parâmetros recebidos - Mês: " + mes + ", Ano: " + ano);

        // Se apenas um dos parâmetros foi fornecido, usar valores padrão para ambos
        if ((mes != null && ano == null) || (mes == null && ano != null)) {
            mes = LocalDate.now().getMonthValue();
            ano = LocalDate.now().getYear();
            logger.info("Apenas um parâmetro fornecido, usando valores padrão - Mês: " + mes + ", Ano: " + ano);
        }

        if (mes == null || ano == null) {
            mes = LocalDate.now().getMonthValue();
            ano = LocalDate.now().getYear();
            logger.info("Usando valores padrão - Mês: " + mes + ", Ano: " + ano);
        } else {
            logger.info("Usando valores recebidos - Mês: " + mes + ", Ano: " + ano);
        }

        RelatorioEntradasDetalhadasDto relatorio = relatorioService.gerarRelatorioEntradasDetalhadas(mes, ano);

        // Obter meses/anos com movimento
        List<Object[]> anosMesesComMovimento = movimentoRepository.findDistinctYearsAndMonths();
        logger.info("Anos/Meses com movimento:");
        for (Object[] anoMes : anosMesesComMovimento) {
            logger.info("  Mês: " + anoMes[0] + ", Ano: " + anoMes[1]);
        }

        model.addAttribute("anosMesesComMovimento", anosMesesComMovimento);
        model.addAttribute("relatorio", relatorio);
        return "relatorios/entradas-detalhadas";
    }

    @GetMapping("/demonstrativo-financeiro/pdf")
    public ResponseEntity<byte[]> gerarDemonstrativoFinanceiroPdf(
            @RequestParam(value = "mes", required = false) Integer mes,
            @RequestParam(value = "ano", required = false) Integer ano) {
        try {
            if (mes == null || ano == null) {
                mes = LocalDate.now().getMonthValue();
                ano = LocalDate.now().getYear();
            }

            // Buscar os dados do demonstrativo
            RelatorioDemonstrativoFinanceiroDto demonstrativo = relatorioService.gerarDemonstrativoFinanceiro(mes, ano);

            // Buscar informações da instituição
            Instituicao instituicao = instituicaoRepository.findAll().stream().findFirst().orElse(null);

            // Preparar variáveis para o template
            Map<String, Object> variables = new HashMap<>();
            variables.put("demonstrativo", demonstrativo);
            variables.put("mes", mes);
            variables.put("ano", ano);
            variables.put("mesNome", obterNomeMes(mes));
            variables.put("dataGeracao", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));

            // Informações da instituição
            preencherDadosInstituicao(variables, instituicao);

            // Gerar PDF usando Thymeleaf + Flying Saucer
            byte[] pdfBytes = pdfService.generatePdf("relatorios/pdf/demonstrativo-financeiro-mensal-pdf", variables);

            return enviarParaDownload(pdfBytes, "demonstrativo_financeiro_" + mes + "_" + ano);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<byte[]> enviarParaDownload(byte[] pdfBytes, String nomeBase) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nomeArquivo = nomeBase + "_" + timestamp + ".pdf";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", nomeArquivo);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    private ResponseEntity<byte[]> enviarParaDownloadExcel(byte[] excelBytes, String nomeBase) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nomeArquivo = nomeBase + "_" + timestamp + ".xlsx";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(
                MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("filename", nomeArquivo);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    }

    private ResponseEntity<byte[]> enviarParaDownloadHtml(byte[] htmlBytes, String nomeBase) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nomeArquivo = nomeBase + "_" + timestamp + ".html";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        headers.setContentDispositionFormData("filename", nomeArquivo);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(htmlBytes, headers, HttpStatus.OK);
    }

    private void preencherDadosInstituicao(Map<String, Object> variables, Instituicao instituicao) {
        if (instituicao != null) {
            variables.put("instituicaoNome", instituicao.getNome());
            variables.put("instituicaoEndereco", instituicao.getEndereco());

            // Converter logo para Base64 se existir
            if (instituicao.getLogo() != null) {
                String logoBase64 = java.util.Base64.getEncoder().encodeToString(instituicao.getLogo());
                variables.put("instituicaoLogo", logoBase64);
            }
        } else {
            variables.put("instituicaoNome", "Nome da Instituição Não Encontrado");
            variables.put("instituicaoEndereco", "Endereço Não Encontrado");
        }
    }
}