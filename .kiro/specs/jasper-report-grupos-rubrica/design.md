# Design Document

## Overview

Este documento descreve o design técnico para implementação do relatório Jasper "Demonstrativo Financeiro Mensal por Grupos e Rubricas" com cabeçalho reutilizável. O sistema será composto por um cabeçalho padrão modular e um relatório principal que utiliza sub-relatórios para organizar os dados por grupos de rubrica.

## Architecture

### Estrutura de Arquivos

```
src/main/resources/reports/
├── cabecalho_padrao.jrxml                    # Cabeçalho reutilizável
├── demonstrativo_financeiro_mensal_grupos_rubrica.jrxml  # Relatório principal
└── rubrica_agrupada_subreport.jrxml         # Sub-relatório para grupos de rubrica
```

### Fluxo de Dados

1. **Controller** → Recebe parâmetros do usuário (mês, ano, filtros)
2. **Service** → Processa dados e agrupa por grupos de rubrica
3. **Jasper Engine** → Compila e executa relatórios com sub-relatórios
4. **Output** → Gera PDF/Excel/HTML conforme solicitado

## Components and Interfaces

### 1. Cabeçalho Padrão (cabecalho_padrao.jrxml)

**Parâmetros de Entrada:**

- `INSTITUICAO_NOME` (String): Nome da instituição
- `INSTITUICAO_ENDERECO` (String): Endereço completo
- `INSTITUICAO_LOGO` (InputStream): Logo da instituição
- `DATA_GERACAO` (String): Data/hora de geração do relatório
- `TITULO_RELATORIO` (String): Título específico do relatório
- `SUBTITULO_RELATORIO` (String): Subtítulo opcional

**Layout:**

- Logo posicionado à esquerda (80x80px)
- Nome da instituição em destaque (fonte 18pt, negrito)
- Endereço abaixo do nome (fonte 12pt)
- Data de geração alinhada à direita
- Título centralizado com linha separadora
- Altura total: 120px

### 2. Relatório Principal (demonstrativo_financeiro_mensal_grupos_rubrica.jrxml)

**Parâmetros Específicos:**

- `MES` (Integer): Mês do relatório (1-12)
- `ANO` (Integer): Ano do relatório
- `SALDO_PERIODO_ANTERIOR` (BigDecimal): Saldo inicial
- `TOTAL_ENTRADAS` (BigDecimal): Total de entradas do período
- `TOTAL_SAIDAS` (BigDecimal): Total de saídas do período
- `SALDO_OPERACIONAL` (BigDecimal): Resultado operacional
- `SALDO_FINAL_CAIXA_BANCO` (BigDecimal): Saldo final consolidado

**Coleções de Dados:**

- `ENTRADAS_AGRUPADAS` (JRBeanCollectionDataSource): Grupos de entrada
- `SAIDAS_AGRUPADAS` (JRBeanCollectionDataSource): Grupos de saída

**Estrutura de Bandas:**

- **Title**: Integração com cabeçalho padrão via sub-relatório
- **Page Header**: Resumo financeiro consolidado
- **Detail**: Seções de entradas e saídas com sub-relatórios
- **Page Footer**: Informações de rodapé e paginação

### 3. Sub-relatório de Grupos (rubrica_agrupada_subreport.jrxml)

**Campos do Bean:**

- `grupoNome` (String): Nome do grupo de mensalidade
- `rubricaNome` (String): Nome da rubrica específica
- `valor` (BigDecimal): Valor da movimentação
- `quantidade` (Integer): Quantidade de transações

**Agrupamento:**

- Agrupado por `grupoNome`
- Totalização por grupo e geral
- Formatação zebrada para legibilidade

## Data Models

### Bean para Dados Agrupados

```java
public class DadosGrupoRubrica {
    private String grupoNome;
    private String rubricaNome;
    private BigDecimal valor;
    private Integer quantidade;

    // getters e setters
}
```

### Parâmetros do Relatório

```java
public class ParametrosRelatorioFinanceiro {
    private Integer mes;
    private Integer ano;
    private BigDecimal saldoPeriodoAnterior;
    private BigDecimal totalEntradas;
    private BigDecimal totalSaidas;
    private BigDecimal saldoOperacional;
    private BigDecimal saldoFinalCaixaBanco;
    private List<DadosGrupoRubrica> entradasAgrupadas;
    private List<DadosGrupoRubrica> saidasAgrupadas;

    // getters e setters
}
```

## Error Handling

### Tratamento de Erros no Jasper

1. **Parâmetros Nulos**: Utilizar expressões condicionais para valores padrão
2. **Imagens Ausentes**: Placeholder quando logo não estiver disponível
3. **Dados Vazios**: Mensagens informativas quando não houver movimentação
4. **Formatação**: Tratamento de valores nulos em formatações monetárias

### Validações no Controller

1. Validar período (mês/ano) antes da geração
2. Verificar disponibilidade de dados para o período
3. Tratar exceções de compilação/execução do Jasper
4. Log de erros para auditoria

## Testing Strategy

### Testes Unitários

1. **Service**: Testar agrupamento de dados por grupos de rubrica
2. **Controller**: Validar parâmetros e tratamento de erros
3. **Jasper Compilation**: Verificar compilação dos templates

### Testes de Integração

1. **Geração Completa**: Testar fluxo completo de geração
2. **Formatos**: Validar saída em PDF, Excel e HTML
3. **Sub-relatórios**: Verificar integração entre relatórios
4. **Performance**: Testar com volumes grandes de dados

### Testes de Layout

1. **Cabeçalho**: Verificar posicionamento e formatação
2. **Quebras de Página**: Testar comportamento com muitos grupos
3. **Formatação Monetária**: Validar apresentação de valores
4. **Responsividade**: Testar em diferentes tamanhos de página

### Cenários de Teste

1. **Período com Dados**: Mês com movimentações normais
2. **Período Vazio**: Mês sem movimentações
3. **Muitos Grupos**: Teste com grande quantidade de grupos
4. **Valores Extremos**: Testar com valores muito altos/baixos
5. **Logo Ausente**: Testar sem logo da instituição

## Implementation Notes

### Reutilização do Cabeçalho

- O cabeçalho será um sub-relatório independente
- Outros relatórios podem reutilizar o mesmo cabeçalho
- Parâmetros padronizados para facilitar integração

### Performance

- Utilizar agrupamento no nível do banco de dados quando possível
- Limitar quantidade de registros por página nos sub-relatórios
- Cache de compilação dos templates Jasper

### Manutenibilidade

- Separação clara entre cabeçalho, conteúdo e sub-relatórios
- Documentação inline nos templates JRXML
- Nomenclatura consistente de parâmetros e campos
