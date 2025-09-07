package br.com.sigest.tesouraria.controller;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
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

import br.com.sigest.tesouraria.domain.entity.CentroCusto; // Import CentroCusto
import br.com.sigest.tesouraria.domain.entity.Instituicao;
import br.com.sigest.tesouraria.dto.RelatorioDemonstrativoFinanceiroDto;
import br.com.sigest.tesouraria.repository.InstituicaoRepository;
import br.com.sigest.tesouraria.service.CentroCustoService;
import br.com.sigest.tesouraria.service.CobrancaService;
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

    @GetMapping("/balancete-centro-custos")
    public String relatorioCentroCustos(Model model) {
        List<br.com.sigest.tesouraria.domain.entity.CentroCusto> centrosDeCusto = centroCustoService.findAllEntities();
        model.addAttribute("centrosDeCusto", centrosDeCusto);
        return "relatorios/balancete-centro-custos";
    }

    @GetMapping("/balancete-centro-custos/pdf")
    public ResponseEntity<byte[]> gerarRelatorioCentroCustosPdf() {
        try {
            InputStream jasperStream = this.getClass().getResourceAsStream("/reports/centros_de_custo_report.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperStream);

            List<CentroCusto> centrosDeCusto = centroCustoService.findAllEntities();
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(centrosDeCusto);

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

    private void preencherCabecalho(Map<String, Object> parameters) {
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
    }

    private void preencherRodape(Map<String, Object> parameters) {
        // Parâmetros comuns do rodapé
        parameters.put("DATA_GERACAO", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
        // O número da página é geralmente tratado no próprio JRXML com $V{PAGE_NUMBER}
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
}
