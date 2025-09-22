# Implementation Plan

- [x] 1. Remove JasperReports dependencies and imports

  - Remove jasperreports and jasperreports-fonts dependencies from pom.xml
  - Remove all JasperReports imports from RelatorioController.java
  - Clean up any remaining JasperReports references in the codebase
  - _Requirements: 1.1, 1.2, 1.3_

- [x] 2. Create missing Thymeleaf templates for detailed financial reports

  - Create financeiro-grupos-rubrica-detalhado-pdf.html template
  - Include proper styling and layout for financial data display
  - Add support for grouped financial data presentation
  - _Requirements: 2.1, 2.2, 4.1, 4.2_

- [x] 3. Create Thymeleaf template for standard financial statements

  - Create demonstrativo-financeiro-mensal-pdf.html template
  - Include sections for entries, exits, and operational balance
  - Add proper formatting for financial amounts and dates
  - _Requirements: 3.1, 3.2, 4.1, 4.2_

- [x] 4. Refactor gerarRelatorioFinanceiroPorGruposRubricaDetalhadoPdf method

  - Replace JasperReports code with PdfService calls
  - Update method to use Thymeleaf template
  - Maintain existing parameter handling and data preparation
  - Ensure proper error handling and response formatting
  - _Requirements: 2.1, 2.3, 2.4, 4.3, 4.4, 5.2_

- [x] 5. Refactor gerarDemonstrativoFinanceiroPdf method

  - Replace JasperReports implementation with Thymeleaf + Flying Saucer
  - Update data binding to use template variables
  - Maintain existing endpoint behavior and response format
  - _Requirements: 3.1, 3.3, 3.4, 4.3, 4.4_

- [x] 6. Update helper methods for consistent template data preparation

  - Ensure preencherDadosInstituicao works with all templates
  - Verify enviarParaDownload method handles all report types
  - Update obterNomeMes method if needed for template compatibility
  - _Requirements: 4.3, 4.4_

- [x] 7. Test and validate all report generation endpoints

  - Test financeiro-grupos-rubrica-detalhado/pdf endpoint
  - Test demonstrativo-financeiro/pdf endpoint
  - Verify PDF output quality and formatting
  - Ensure no JasperReports errors occur during application startup
  - _Requirements: 2.3, 3.2, 5.1, 5.2, 5.3, 5.4_

- [x] 8. Clean up and optimize the migration

  - Remove any unused JasperReports related code
  - Verify application builds and starts without errors
  - Update any remaining references to old report system
  - _Requirements: 1.1, 5.3, 5.4_
