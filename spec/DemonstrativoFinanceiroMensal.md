# Demonstrativo Financeiro Mensal

## Roteiro de Design

Esta feature visa atualizar o relatório de demonstrativo financeiro mensal para utilizar diretamente os dados da entidade `ReconciliacaoMensal`, especificamente:
- `ReconciliacaoMensal.saldoInicial` -> valor em "Saldo do Período Anterior"
- `ReconciliacaoMensal.saldoFinal` -> "Saldo Final Consolidado"

A implementação envolve a modificação do serviço `RelatorioService` para obter esses valores diretamente da reconciliação mensal correspondente ao período solicitado, em vez de calcular o saldo anterior com base na reconciliação do mês anterior.

Além disso, foram corrigidos erros nos relatórios JasperReports relacionados a incompatibilidades entre os nomes dos campos nos arquivos JRXML e os nomes dos campos nas classes DTO:
- No arquivo `rubrica_agrupada_subreport.jrxml`, o campo `rubricas` foi renomeado para `rubricasDetalhe` e `totalCategoria` para `totalPorTipo`
- No arquivo `rubrica_detalhe_subreport.jrxml`, o campo `valorRubrica` foi renomeado para `valor`

Foram adicionadas melhorias visuais nos relatórios JasperReports:
- Correção de elementos `<pen>` inválidos que causavam erro de compilação
- Adição de bordas pontilhadas aos itens de receitas e despesas usando elementos `<box>` apropriados

## Requisitos

1. O relatório deve filtrar os dados com base na `ReconciliacaoMensal` do mês/ano solicitado
2. O campo "Saldo do Período Anterior" deve exibir o valor de `ReconciliacaoMensal.saldoInicial`
3. O campo "Saldo Final Consolidado" deve exibir o valor de `ReconciliacaoMensal.saldoFinal`
4. O relatório deve continuar exibindo as entradas e saídas detalhadas do período
5. O PDF do relatório deve manter a mesma formatação e informações
6. Os relatórios JasperReports devem funcionar corretamente sem erros de campos inexistentes
7. Os itens de receitas e despesas devem ter bordas pontilhadas para melhorar a apresentação visual

## Tarefas

- [x] Analisar o template do relatório demonstrativo-financeiro-mensal.html
- [x] Analisar a entidade ReconciliacaoMensal.java
- [x] Identificar onde são definidos os campos 'Saldo do Período Anterior' e 'Saldo Final Consolidado' no relatório
- [x] Modificar o template para filtrar os dados pelo mês da ReconciliacaoMensal
- [x] Atualizar os campos do relatório para usar saldoInicial e saldoFinal da ReconciliacaoMensal
- [x] Verificar se há controladores ou serviços que precisam ser atualizados para suportar o filtro
- [x] Corrigir incompatibilidades nos arquivos JRXML dos relatórios
- [x] Remover elementos `<pen>` inválidos que causavam erro de compilação
- [x] Adicionar bordas pontilhadas aos itens de receitas e despesas usando elementos `<box>` apropriados

## Opção de Reversão

Para reverter as alterações, basta restaurar os arquivos modificados para as versões anteriores:

```bash
git checkout HEAD~1 -- src/main/java/br/com/sigest/tesouraria/service/RelatorioService.java
git checkout HEAD~1 -- src/main/resources/reports/rubrica_agrupada_subreport.jrxml
git checkout HEAD~1 -- src/main/resources/reports/rubrica_detalhe_subreport.jrxml
git checkout HEAD~1 -- src/main/resources/reports/demonstrativo_financeiro_mensal_report.jrxml
```