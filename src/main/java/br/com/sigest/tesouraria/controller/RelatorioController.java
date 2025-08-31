package br.com.sigest.tesouraria.controller;

import br.com.sigest.tesouraria.service.CentroCustoService;
import br.com.sigest.tesouraria.domain.entity.CentroCusto; // Import CentroCusto
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.JasperExportManager;

import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map; // Import Map for parameters

import br.com.sigest.tesouraria.service.RelatorioService;
import br.com.sigest.tesouraria.dto.RelatorioDemonstrativoFinanceiroDto;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/relatorios")
public class RelatorioController {

    @Autowired
    private CentroCustoService centroCustoService;

    @Autowired
    private RelatorioService relatorioService;

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
            Map<String, Object> parameters = new java.util.HashMap<>(); // No parameters for now
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

            // Load JRXML file from classpath
            InputStream jasperStream = this.getClass().getResourceAsStream("/reports/demonstrativo_financeiro_report.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperStream);

            // Get data
            RelatorioDemonstrativoFinanceiroDto demonstrativo = relatorioService.gerarDemonstrativoFinanceiro(mes, ano);
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(java.util.Collections.singletonList(demonstrativo));

            // Fill the report
            Map<String, Object> parameters = new java.util.HashMap<>();
            parameters.put("MES", mes);
            parameters.put("ANO", ano);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

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