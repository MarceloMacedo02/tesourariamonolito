package br.com.sigest.tesouraria.controller;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map; // Import Map for parameters

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.sigest.tesouraria.domain.entity.CentroCusto; // Import CentroCusto
import br.com.sigest.tesouraria.domain.entity.Instituicao;
import br.com.sigest.tesouraria.dto.RelatorioDemonstrativoFinanceiroDto;
import br.com.sigest.tesouraria.repository.InstituicaoRepository;
import br.com.sigest.tesouraria.service.CentroCustoService;
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

    @Autowired
    private CentroCustoService centroCustoService;

    @Autowired
    private RelatorioService relatorioService;

    @Autowired
    private InstituicaoRepository instituicaoRepository;

    @GetMapping("/balancete-centro-custos")
    public String relatorioCentroCustos(Model model) {
        List<br.com.sigest.tesouraria.domain.entity.CentroCusto> centrosDeCusto = centroCustoService.findAllEntities();
        model.addAttribute("centrosDeCusto", centrosDeCusto);
        return "relatorios/balancete-centro-custos"; // This will be the Thymeleaf template name
    }

    @GetMapping("/balancete-centro-custos/pdf")
    public ResponseEntity<byte[]> gerarRelatorioCentroCustosPdf() {
        try {
            // Load JRXML file from classpath
            InputStream jasperStream = this.getClass().getResourceAsStream("/reports/centros_de_custo_report.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperStream);

            // Get data
            List<CentroCusto> centrosDeCusto = centroCustoService.findAllEntities();
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(centrosDeCusto);

            // Fill the report
            Map<String, Object> parameters = new java.util.HashMap<>();
            Instituicao instituicao = instituicaoRepository.findAll().stream().findFirst().orElse(null);

            if (instituicao != null) {
                parameters.put("INSTITUICAO_NOME", instituicao.getNome());
                parameters.put("INSTITUICAO_ENDERECO", instituicao.getEndereco());
                // For logo, assuming logoPath in Instituicao entity stores a path resolvable by
                // JasperReports
                // e.g., "/static/assets/img/logo.png" or a full file system path
                if (instituicao.getLogo() != null) {
                    parameters.put("INSTITUICAO_LOGO", new java.io.ByteArrayInputStream(instituicao.getLogo()));
                } else {
                    parameters.put("INSTITUICAO_LOGO", null); // Or a default placeholder image
                }
            } else {
                // Provide default values if no institution is found
                parameters.put("INSTITUICAO_NOME", "Nome da Instituição Não Encontrado");
                parameters.put("INSTITUICAO_ENDERECO", "Endereço Não Encontrado");
                parameters.put("INSTITUICAO_LOGO", null); // No logo if not found
            }
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            // Export to PDF
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, baos);
            byte[] pdfBytes = baos.toByteArray();

            // Set headers for PDF download
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "balancete_centro_custos.pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfBytes, headers, org.springframework.http.HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/demonstrativo-financeiro")
    public String demonstrativoFinanceiro(Model model,
            @RequestParam(value = "mes", required = false) Integer mes,
            @RequestParam(value = "ano", required = false) Integer ano) {
        // If month and year are not provided, use current month and year
        if (mes == null || ano == null) {
            mes = java.time.LocalDate.now().getMonthValue();
            ano = java.time.LocalDate.now().getYear();
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
            mes = java.time.LocalDate.now().getMonthValue();
            ano = java.time.LocalDate.now().getYear();
        }
        RelatorioDemonstrativoFinanceiroDto demonstrativo = relatorioService.gerarDemonstrativoFinanceiro(mes, ano);
        model.addAttribute("demonstrativo", demonstrativo);
        return "relatorios/demonstrativo-financeiro-mensal";
    }

    @GetMapping("/demonstrativo-financeiro/pdf")
    public ResponseEntity<byte[]> gerarDemonstrativoFinanceiroPdf(
            @RequestParam(value = "mes", required = false) Integer mes,
            @RequestParam(value = "ano", required = false) Integer ano) {
        try {
            // If month and year are not provided, use current month and year
            if (mes == null || ano == null) {
                mes = java.time.LocalDate.now().getMonthValue();
                ano = java.time.LocalDate.now().getYear();
            }
            // Load JRXML files from classpath
            InputStream mainReportStream = this.getClass()
                    .getResourceAsStream("/reports/demonstrativo_financeiro_mensal_report.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(mainReportStream);

            InputStream rubricaAgrupadaSubreportStream = this.getClass()
                    .getResourceAsStream("/reports/rubrica_agrupada_subreport.jrxml");
            JasperReport rubricaAgrupadaSubreport = JasperCompileManager.compileReport(rubricaAgrupadaSubreportStream);

            InputStream rubricaDetalheSubreportStream = this.getClass()
                    .getResourceAsStream("/reports/rubrica_detalhe_subreport.jrxml");
            JasperReport rubricaDetalheSubreport = JasperCompileManager.compileReport(rubricaDetalheSubreportStream);

            // Get data
            RelatorioDemonstrativoFinanceiroDto demonstrativo = relatorioService.gerarDemonstrativoFinanceiro(mes, ano);

            // Fill the report
            Map<String, Object> parameters = new java.util.HashMap<>();
            Instituicao instituicao = instituicaoRepository.findAll().stream().findFirst().orElse(null);

            if (instituicao != null) {
                parameters.put("INSTITUICAO_NOME", instituicao.getNome());
                parameters.put("INSTITUICAO_ENDERECO", instituicao.getEndereco());
                if (instituicao.getLogo() != null) {
                    parameters.put("INSTITUICAO_LOGO", new java.io.ByteArrayInputStream(instituicao.getLogo()));
                } else {
                    parameters.put("INSTITUICAO_LOGO", null);
                }
            } else {
                parameters.put("INSTITUICAO_NOME", "Nome da Instituição Não Encontrado");
                parameters.put("INSTITUICAO_ENDERECO", "Endereço Não Encontrado");
                parameters.put("INSTITUICAO_LOGO", null);
            }

            parameters.put("MES", demonstrativo.getMes());
            parameters.put("ANO", demonstrativo.getAno());
            parameters.put("SALDO_PERIODO_ANTERIOR", demonstrativo.getSaldoPeriodoAnterior());
            parameters.put("TOTAL_ENTRADAS", demonstrativo.getTotalEntradas());
            parameters.put("TOTAL_SAIDAS", demonstrativo.getTotalSaidas());
            parameters.put("SALDO_OPERACIONAL", demonstrativo.getSaldoOperacional());
            parameters.put("SALDO_FINAL_CAIXA_BANCO", demonstrativo.getSaldoFinalCaixaBanco());
            parameters.put("ENTRADAS_AGRUPADAS", new JRBeanCollectionDataSource(demonstrativo.getEntradasAgrupadas()));
            parameters.put("SAIDAS_AGRUPADAS", new JRBeanCollectionDataSource(demonstrativo.getSaidasAgrupadas()));

            // Pass compiled subreports as parameters
            parameters.put("RUBRICA_AGRUPADA_SUBREPORT", rubricaAgrupadaSubreport);
            parameters.put("RUBRICA_DETALHE_SUBREPORT", rubricaDetalheSubreport);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,
                    new JRBeanCollectionDataSource(java.util.Collections.singletonList(demonstrativo)));

            // Export to PDF
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, baos);
            byte[] pdfBytes = baos.toByteArray();

            // Set headers for PDF download
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "demonstrativo_financeiro_" + mes + "_" + ano + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfBytes, headers, org.springframework.http.HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}