# Design Document

## Overview

This design outlines the migration from JasperReports to Thymeleaf + Flying Saucer for PDF report generation. The migration will eliminate complex .jrxml files and JasperReports dependencies while maintaining all existing report functionality using HTML templates that are easier to maintain and debug.

## Architecture

### Current Architecture (JasperReports)

- JasperReports dependencies in pom.xml
- .jrxml template files in /reports directory
- JasperCompileManager for template compilation
- JasperFillManager for data binding
- JasperExportManager for PDF export

### Target Architecture (Thymeleaf + Flying Saucer)

- Thymeleaf for HTML template processing
- Flying Saucer for HTML to PDF conversion
- HTML templates in templates/relatorios/pdf directory
- PdfService for centralized PDF generation
- Consistent data binding using Thymeleaf model attributes

## Components and Interfaces

### 1. PdfService Enhancement

The existing PdfService will be the central component for all PDF generation:

- **Method**: `generatePdf(String templateName, Map<String, Object> variables)`
- **Input**: Template name and data variables
- **Output**: PDF byte array
- **Responsibility**: Convert Thymeleaf HTML templates to PDF using Flying Saucer

### 2. RelatorioController Refactoring

All JasperReports-based methods will be converted to use PdfService:

- Remove JasperReports imports and dependencies
- Replace JasperCompileManager calls with PdfService calls
- Maintain existing endpoint URLs and parameter handling
- Use consistent error handling and response formatting

### 3. Template Structure

HTML templates will follow a consistent structure:

```
templates/relatorios/pdf/
├── demonstrativo-financeiro-pdf.html (existing)
├── financeiro-grupos-rubrica-detalhado-pdf.html (new)
├── demonstrativo-financeiro-mensal-pdf.html (new)
└── shared/
    ├── header.html (common header)
    ├── footer.html (common footer)
    └── styles.css (embedded CSS)
```

### 4. Data Models

Existing DTOs will be used without modification:

- `RelatorioDemonstrativoFinanceiroDto`
- `RelatorioDemonstrativoFinanceiroPorGrupoRubricaDto`
- `RelatorioFinanceiroGruposRubricaDto`
- `RelatorioEntradasDetalhadasDto`

## Data Models

### Template Variables Structure

All templates will receive a consistent set of variables:

```java
Map<String, Object> variables = {
    // Report data
    "demonstrativo": RelatorioDemonstrativoFinanceiroDto,
    "relatorio": RelatorioFinanceiroGruposRubricaDto,

    // Parameters
    "mes": Integer,
    "ano": Integer,
    "mesNome": String,

    // Institution data
    "instituicaoNome": String,
    "instituicaoEndereco": String,
    "instituicaoLogo": String (Base64),

    // Metadata
    "dataGeracao": String,
    "timestamp": String
}
```

### Helper Methods

Existing helper methods will be maintained:

- `preencherDadosInstituicao(Map<String, Object> variables, Instituicao instituicao)`
- `enviarParaDownload(byte[] pdfBytes, String nomeBase)`
- `obterNomeMes(Integer mes)`

## Error Handling

### Template Resolution Errors

- **Issue**: Template not found
- **Handling**: Return HTTP 500 with descriptive error message
- **Logging**: Log template name and search paths

### Data Processing Errors

- **Issue**: Null or invalid data
- **Handling**: Use default values or empty collections
- **Logging**: Log data validation issues

### PDF Generation Errors

- **Issue**: Flying Saucer conversion failures
- **Handling**: Return HTTP 500 with error details
- **Logging**: Log full stack trace for debugging

## Testing Strategy

### Unit Tests

- Test PdfService with mock templates
- Test RelatorioController methods with mock services
- Test helper methods for data preparation

### Integration Tests

- Test complete PDF generation flow
- Test template rendering with real data
- Test error scenarios and edge cases

### Manual Testing

- Verify PDF output quality and formatting
- Test all report endpoints
- Validate download functionality

## Migration Steps

### Phase 1: Remove JasperReports Dependencies

1. Remove jasperreports dependencies from pom.xml
2. Remove all JasperReports imports from Java files
3. Clean up any remaining .jrxml references

### Phase 2: Create Missing Templates

1. Create `financeiro-grupos-rubrica-detalhado-pdf.html`
2. Create `demonstrativo-financeiro-mensal-pdf.html`
3. Create shared template fragments

### Phase 3: Refactor Controller Methods

1. Replace `gerarRelatorioFinanceiroPorGruposRubricaDetalhadoPdf`
2. Replace `gerarDemonstrativoFinanceiroPdf`
3. Update any other JasperReports-based methods

### Phase 4: Testing and Validation

1. Test all report generation endpoints
2. Validate PDF output quality
3. Ensure consistent styling and formatting

## Performance Considerations

### Template Caching

- Thymeleaf templates are cached by default
- No need for manual compilation like JasperReports
- Faster startup time without .jrxml compilation

### Memory Usage

- Flying Saucer uses less memory than JasperReports
- HTML templates are lighter than compiled .jrxml files
- Better garbage collection with simpler object lifecycle

### Scalability

- Stateless PDF generation
- No template compilation overhead
- Better horizontal scaling capabilities
