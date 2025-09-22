package br.com.sigest.tesouraria.controller;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map; // Import Map for parameters
import java.util.Optional;

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
import br.com.sigest.tesouraria.service.RelatorioService;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

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
            InputStream jasperStream = this.getClass().getResourceAsStream("/reports/grupos_rubrica_report.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperStream);

            List<GrupoRubrica> gruposDeRubrica = grupoRubricaService.findAllEntities();
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(gruposDeRubrica);

            Map<String, Object> parameters = new java.util.HashMap<>();
            preencherCabecalho(parameters);
            preencherRodape(parameters);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, baos);
            byte[] pdfBytes = baos.toByteArray();

            return enviarParaDownload(pdfBytes, "balancete_centro_custos");

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/inadimplentes/pdf")
    public ResponseEntity<byte[]> gerarRelatorioInadimplentesPdf() {
        try {
            InputStream jasperStream = this.getClass().getResourceAsStream("/reports/relatorio_inadimplentes.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperStream);

            List<br.com.sigest.tesouraria.dto.RelatorioInadimplentesDto> inadimplentes = cobrancaService
                    .gerarRelatorioInadimplentes();
            java.math.BigDecimal totalGeral = inadimplentes.stream()
                    .map(br.com.sigest.tesouraria.dto.RelatorioInadimplentesDto::getValorTotalAberto)
                    .filter(java.util.Objects::nonNull)
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(inadimplentes);

            Map<String, Object> parameters = new java.util.HashMap<>();
            preencherCabecalho(parameters);
            parameters.put("TOTAL_GERAL", totalGeral);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, baos);
            byte[] pdfBytes = baos.toByteArray();

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
            // Usar o relatório correto para grupos de rubrica
            InputStream mainReportStream = this.getClass()
                    .getResourceAsStream("/reports/grupos_rubrica_report.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(mainReportStream);

            // Buscar dados usando o método correto para grupos de rubrica
            RelatorioDemonstrativoFinanceiroPorGrupoRubricaDto demonstrativo = relatorioService
                    .gerarDemonstrativoFinanceiroPorGrupoRubrica(mes, ano);

            Map<String, Object> parameters = new java.util.HashMap<>();
            preencherCabecalho(parameters);

            // Converter grupos de rubrica para o formato esperado pelo
            // grupos_rubrica_report.jrxml
            // Este relatório espera campos: nome, entradas, saidas, saldo
            List<Map<String, Object>> dadosRelatorio = new ArrayList<>();

            if (demonstrativo.getGruposRubricaAgrupados() != null) {
                for (RelatorioDemonstrativoFinanceiroPorGrupoRubricaDto.GrupoRubricaAgrupadoDto grupo : demonstrativo
                        .getGruposRubricaAgrupados()) {
                    Map<String, Object> dadosGrupo = new HashMap<>();
                    dadosGrupo.put("nome", grupo.getNomeGrupoRubrica());
                    dadosGrupo.put("entradas",
                            grupo.getTotalEntradas() != null ? grupo.getTotalEntradas().doubleValue() : 0.0);
                    dadosGrupo.put("saidas",
                            grupo.getTotalSaidas() != null ? grupo.getTotalSaidas().doubleValue() : 0.0);
                    dadosGrupo.put("saldo", grupo.getSaldo() != null ? grupo.getSaldo().doubleValue() : 0.0);
                    dadosRelatorio.add(dadosGrupo);
                }
            }

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,
                    new JRBeanCollectionDataSource(dadosRelatorio));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            if ("EXCEL".equalsIgnoreCase(formato)) {
                net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter exporter = new net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter();
                exporter.setExporterInput(new net.sf.jasperreports.export.SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new net.sf.jasperreports.export.SimpleOutputStreamExporterOutput(baos));
                exporter.exportReport();

                return enviarParaDownloadExcel(baos.toByteArray(),
                        "demonstrativo_financeiro_grupos_rubrica_" + mes + "_" + ano);
            } else if ("HTML".equalsIgnoreCase(formato)) {
                net.sf.jasperreports.engine.export.HtmlExporter exporter = new net.sf.jasperreports.engine.export.HtmlExporter();
                exporter.setExporterInput(new net.sf.jasperreports.export.SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new net.sf.jasperreports.export.SimpleHtmlExporterOutput(baos));
                exporter.exportReport();

                return enviarParaDownloadHtml(baos.toByteArray(),
                        "demonstrativo_financeiro_grupos_rubrica_" + mes + "_" + ano);
            } else {
                // PDF por padrão
                JasperExportManager.exportReportToPdfStream(jasperPrint, baos);
                return enviarParaDownload(baos.toByteArray(),
                        "demonstrativo_financeiro_grupos_rubrica_" + mes + "_" + ano);
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

            // Carregar e compilar os relatórios (principal e sub-relatórios)
            InputStream mainReportStream = this.getClass()
                    .getResourceAsStream("/reports/financeiro_grupos_rubrica_detalhado.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(mainReportStream);

            InputStream grupoSubreportStream = this.getClass()
                    .getResourceAsStream("/reports/subreport_grupo_rubrica.jrxml");
            JasperReport grupoSubreport = JasperCompileManager.compileReport(grupoSubreportStream);

            InputStream rubricaSubreportStream = this.getClass()
                    .getResourceAsStream("/reports/subreport_rubrica.jrxml");
            JasperReport rubricaSubreport = JasperCompileManager.compileReport(rubricaSubreportStream);

            // Buscar os dados
            RelatorioFinanceiroGruposRubricaDto relatorio = relatorioService
                    .gerarRelatorioFinanceiroGruposRubrica(mes, ano);

            // Configurar parâmetros
            Map<String, Object> parameters = new HashMap<>();
            preencherCabecalho(parameters);
            parameters.put("MES", mes);
            parameters.put("ANO", ano);
            parameters.put("SALDO_PERIODO_ANTERIOR", relatorio.getSaldoPeriodoAnterior());
            parameters.put("TOTAL_ENTRADAS_GERAL", relatorio.getTotalEntradas());
            parameters.put("TOTAL_SAIDAS_GERAL", relatorio.getTotalSaidas());
            parameters.put("SALDO_OPERACIONAL", relatorio.getSaldoOperacional());
            parameters.put("SALDO_FINAL_CAIXA_BANCO", relatorio.getSaldoFinalCaixaBanco());
            parameters.put("GRUPOS_ENTRADA", new JRBeanCollectionDataSource(relatorio.getGruposRubricaEntrada()));
            parameters.put("GRUPOS_SAIDA", new JRBeanCollectionDataSource(relatorio.getGruposRubricaSaida()));
            parameters.put("SUBREPORT_GRUPO", grupoSubreport);
            parameters.put("SUBREPORT_RUBRICA", rubricaSubreport);

            // Gerar o relatório
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,
                    new JRBeanCollectionDataSource(Collections.singletonList(relatorio)));
            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

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
            InputStream mainReportStream = this.getClass()
                    .getResourceAsStream("/reports/demonstrativo_financeiro_mensal_report.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(mainReportStream);

            InputStream rubricaAgrupadaSubreportStream = this.getClass()
                    .getResourceAsStream("/reports/rubrica_agrupada_subreport.jrxml");
            JasperReport rubricaAgrupadaSubreport = JasperCompileManager.compileReport(rubricaAgrupadaSubreportStream);

            InputStream rubricaDetalheSubreportStream = this.getClass()
                    .getResourceAsStream("/reports/rubrica_detalhe_subreport.jrxml");
            JasperReport rubricaDetalheSubreport = JasperCompileManager.compileReport(rubricaDetalheSubreportStream);

            RelatorioDemonstrativoFinanceiroDto demonstrativo = relatorioService.gerarDemonstrativoFinanceiro(mes, ano);

            Map<String, Object> parameters = new java.util.HashMap<>();
            preencherCabecalho(parameters);
            preencherRodape(parameters);

            parameters.put("MES", demonstrativo.getMes());
            parameters.put("ANO", demonstrativo.getAno());
            parameters.put("SALDO_PERIODO_ANTERIOR", demonstrativo.getSaldoPeriodoAnterior());
            parameters.put("TOTAL_ENTRADAS", demonstrativo.getTotalEntradas());
            parameters.put("TOTAL_SAIDAS", demonstrativo.getTotalSaidas());
            parameters.put("SALDO_OPERACIONAL", demonstrativo.getSaldoOperacional());
            parameters.put("SALDO_FINAL_CAIXA_BANCO", demonstrativo.getSaldoFinalCaixaBanco());
            parameters.put("ENTRADAS_AGRUPADAS", new JRBeanCollectionDataSource(demonstrativo.getEntradasAgrupadas()));
            parameters.put("SAIDAS_AGRUPADAS", new JRBeanCollectionDataSource(demonstrativo.getSaidasAgrupadas()));

            parameters.put("RUBRICA_AGRUPADA_SUBREPORT", rubricaAgrupadaSubreport);
            parameters.put("RUBRICA_DETALHE_SUBREPORT", rubricaDetalheSubreport);
            parameters.put("REPORT_DATA_SOURCE_CLASS", JRBeanCollectionDataSource.class);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,
                    new JRBeanCollectionDataSource(Collections.singletonList(demonstrativo)));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, baos);
            byte[] pdfBytes = baos.toByteArray();

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

    private void preencherCabecalho(Map<String, Object> parameters) {
        Optional<Instituicao> instituicaoOpt = instituicaoRepository.findByFixedId(1L);
        if (instituicaoOpt.isPresent()) {
            Instituicao instituicao = instituicaoOpt.get();
            parameters.put("INSTITUICAO_NOME", instituicao.getNome());
            parameters.put("INSTITUICAO_ENDERECO", instituicao.getEndereco());
            try {
                InputStream logoStream = this.getClass().getResourceAsStream("/static/images/logo.png");
                if (logoStream != null) {
                    parameters.put("INSTITUICAO_LOGO", logoStream);
                }
            } catch (Exception e) {
                logger.error("Erro ao carregar o logo da instituição", e);
            }
        }
        preencherRodape(parameters);
    }

    private void preencherRodape(Map<String, Object> parameters) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        parameters.put("DATA_GERACAO", "Gerado em: " + sdf.format(Calendar.getInstance().getTime()));
    }

}
