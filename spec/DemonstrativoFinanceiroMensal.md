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
- Correção da quebra de linha em títulos longos usando `isStretchWithOverflow="true"`
- Ajustes finos na apresentação visual dos itens:
  1. Remoção de bordas nos títulos de receita e despesa
  2. Redução do espaçamento entre os itens de receita e despesas
  3. Unificação das bordas de cada item para formar uma única linha circulada por borda
  4. Alinhamento do valor de cada item à direita
  5. Correção da palavra "null" que estava aparecendo nos itens
  6. Separação de receitas e despesas em páginas diferentes
  7. Movimentação dos resumos para a última página
  8. Aprimoramento completo do layout para torná-lo mais profissional e elegante

## Requisitos

1. O relatório deve filtrar os dados com base na `ReconciliacaoMensal` do mês/ano solicitado
2. O campo "Saldo do Período Anterior" deve exibir o valor de `ReconciliacaoMensal.saldoInicial`
3. O campo "Saldo Final Consolidado" deve exibir o valor de `ReconciliacaoMensal.saldoFinal`
4. O relatório deve continuar exibindo as entradas e saídas detalhadas do período
5. O PDF do relatório deve manter a mesma formatação e informações
6. Os relatórios JasperReports devem funcionar corretamente sem erros de campos inexistentes
7. Os itens de receitas e despesas devem ter bordas pontilhadas para melhorar a apresentação visual
8. Os títulos longos devem ser quebrados em várias linhas quando necessário
9. Os títulos de categoria (receita/despesa) não devem ter bordas
10. Deve haver menos espaçamento entre os itens
11. Cada item deve ter uma única borda circulando todo o conteúdo
12. O valor de cada item deve estar alinhado à direita
13. Não deve aparecer a palavra "null" nos itens
14. Receitas devem aparecer na primeira página
15. Despesas devem aparecer na segunda página
16. Resumos devem aparecer na última página
17. O layout deve ser profissional e elegante

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
- [x] Corrigir quebra de linha em títulos longos
- [x] Remover bordas dos títulos de receita e despesa
- [x] Reduzir espaçamento entre os itens de receita e despesas
- [x] Unificar as bordas de cada item para formar uma única linha circulada por borda
- [x] Corrigir elementos `<pen>` inválidos dentro de elementos `<rectangle>`
- [x] Substituir elementos `<rectangle>` problemáticos por abordagem alternativa com `<box>`
- [x] Alinhar o valor de cada item à direita
- [x] Corrigir caracteres XML inválidos (escape HTML)
- [x] Corrigir a palavra "null" que estava aparecendo nos itens
- [x] Separar receitas e despesas em páginas diferentes
- [x] Mover os resumos para a última página
- [x] Aprimorar completamente o layout para torná-lo mais profissional e elegante

## Opção de Reversão

Para reverter as alterações, basta restaurar os arquivos modificados para as versões anteriores:

```bash
git checkout HEAD~1 -- src/main/java/br/com/sigest/tesouraria/service/RelatorioService.java
git checkout HEAD~1 -- src/main/resources/reports/rubrica_agrupada_subreport.jrxml
git checkout HEAD~1 -- src/main/resources/reports/rubrica_detalhe_subreport.jrxml
git checkout HEAD~1 -- src/main/resources/reports/demonstrativo_financeiro_mensal_report.jrxml
git checkout HEAD~1 -- src/main/resources/reports/centros_de_custo_report.jrxml
git checkout HEAD~1 -- src/main/resources/reports/relatorio_inadimplentes.jrxml
```