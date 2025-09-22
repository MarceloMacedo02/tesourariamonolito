# Requirements Document

## Introduction

This feature involves migrating the current reporting system from JasperReports to a Thymeleaf + Flying Saucer based solution. The goal is to eliminate JasperReports dependencies and errors while maintaining all existing report functionality using HTML templates that are easier to maintain and debug.

## Requirements

### Requirement 1

**User Story:** As a developer, I want to remove all JasperReports dependencies from the application, so that I can eliminate complex .jrxml files and related compilation errors.

#### Acceptance Criteria

1. WHEN the application starts THEN no JasperReports dependencies SHALL be loaded
2. WHEN I check the pom.xml THEN no jasperreports or jasperreports-fonts dependencies SHALL be present
3. WHEN I search the codebase THEN no JasperReports imports SHALL exist in any Java files

### Requirement 2

**User Story:** As a user, I want to generate the detailed financial report by groups and categories, so that I can view comprehensive financial data in PDF format.

#### Acceptance Criteria

1. WHEN I access the detailed financial report endpoint THEN the system SHALL generate a PDF using Thymeleaf templates
2. WHEN the PDF is generated THEN it SHALL contain all financial data grouped by categories
3. WHEN the report fails to generate THEN the system SHALL provide clear error messages
4. WHEN I download the report THEN it SHALL have a proper filename with timestamp

### Requirement 3

**User Story:** As a user, I want to generate the standard financial statement report, so that I can view monthly financial summaries in PDF format.

#### Acceptance Criteria

1. WHEN I request a financial statement PDF THEN the system SHALL use Thymeleaf + Flying Saucer
2. WHEN the report is generated THEN it SHALL include all required financial data sections
3. WHEN I specify month and year parameters THEN the report SHALL filter data accordingly
4. WHEN no parameters are provided THEN the system SHALL use current month and year as defaults

### Requirement 4

**User Story:** As a developer, I want all report generation to use consistent Thymeleaf templates, so that reports are easier to maintain and customize.

#### Acceptance Criteria

1. WHEN any report is generated THEN it SHALL use HTML templates in the templates/relatorios/pdf directory
2. WHEN I need to modify a report layout THEN I SHALL be able to edit standard HTML/CSS
3. WHEN reports include institution data THEN they SHALL use the preencherDadosInstituicao helper method
4. WHEN reports are downloaded THEN they SHALL use the enviarParaDownload helper method

### Requirement 5

**User Story:** As a system administrator, I want the application to start without JasperReports errors, so that all functionality works reliably.

#### Acceptance Criteria

1. WHEN the application starts THEN no JasperReports related exceptions SHALL occur
2. WHEN I access any report endpoint THEN no "Cannot invoke String.length() because spec is null" errors SHALL occur
3. WHEN the system compiles THEN no missing .jrxml file errors SHALL be reported
4. WHEN I run mvn clean install THEN the build SHALL complete successfully without JasperReports errors
