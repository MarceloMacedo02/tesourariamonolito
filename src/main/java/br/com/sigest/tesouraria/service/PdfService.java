package br.com.sigest.tesouraria.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.DocumentException;

@Service
public class PdfService {

    @Autowired
    private TemplateEngine templateEngine;

    /**
     * Gera um PDF a partir de um template Thymeleaf
     * 
     * @param templateName Nome do template (sem extensão .html)
     * @param variables    Variáveis para o template
     * @return Array de bytes do PDF gerado
     * @throws DocumentException Se houver erro na geração do PDF
     * @throws IOException       Se houver erro de I/O
     */
    public byte[] generatePdf(String templateName, Map<String, Object> variables)
            throws DocumentException, IOException {

        System.out.println("=== PdfService.generatePdf INICIADO ===");
        System.out.println("Template: " + templateName);
        System.out.println("Variáveis: " + variables.keySet());

        // Criar contexto do Thymeleaf
        Context context = new Context();
        context.setVariables(variables);

        // Processar o template
        System.out.println("Processando template com Thymeleaf...");
        String htmlContent = templateEngine.process(templateName, context);
        System.out.println("HTML gerado com sucesso! Tamanho: " + htmlContent.length() + " caracteres");

        // Gerar PDF usando Flying Saucer
        System.out.println("Gerando PDF com Flying Saucer...");
        byte[] pdfBytes = generatePdfFromHtml(htmlContent);
        System.out.println("PDF gerado com sucesso! Tamanho: " + pdfBytes.length + " bytes");

        return pdfBytes;
    }

    /**
     * Gera PDF a partir de conteúdo HTML
     * 
     * @param htmlContent Conteúdo HTML
     * @return Array de bytes do PDF
     * @throws DocumentException Se houver erro na geração do PDF
     * @throws IOException       Se houver erro de I/O
     */
    private byte[] generatePdfFromHtml(String htmlContent) throws DocumentException, IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();
        renderer.createPDF(outputStream);

        return outputStream.toByteArray();
    }

    /**
     * Gera PDF com configurações customizadas
     * 
     * @param templateName Nome do template
     * @param variables    Variáveis para o template
     * @param baseUrl      URL base para recursos (imagens, CSS)
     * @return Array de bytes do PDF gerado
     * @throws DocumentException Se houver erro na geração do PDF
     * @throws IOException       Se houver erro de I/O
     */
    public byte[] generatePdfWithBaseUrl(String templateName, Map<String, Object> variables, String baseUrl)
            throws DocumentException, IOException {

        Context context = new Context();
        context.setVariables(variables);

        String htmlContent = templateEngine.process(templateName, context);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(htmlContent, baseUrl);
        renderer.layout();
        renderer.createPDF(outputStream);

        return outputStream.toByteArray();
    }
}