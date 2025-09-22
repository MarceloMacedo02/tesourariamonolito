# Implementation Plan

- [ ] 1. Criar estrutura base dos templates Jasper

  - Criar o arquivo de cabeçalho padrão reutilizável com parâmetros padronizados
  - Definir layout, posicionamento de elementos e formatação do cabeçalho
  - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5_

- [ ] 2. Implementar cabeçalho padrão (cabecalho_padrao.jrxml)

  - Criar template JRXML com banda title contendo logo, nome e endereço da instituição
  - Implementar parâmetros para INSTITUICAO_NOME, INSTITUICAO_ENDERECO, INSTITUICAO_LOGO, DATA_GERACAO, TITULO_RELATORIO
  - Definir layout responsivo com posicionamento correto dos elementos
  - _Requirements: 2.1, 2.2, 2.3, 2.4_

- [ ] 3. Criar relatório principal (demonstrativo_financeiro_mensal_grupos_rubrica.jrxml)

  - Implementar estrutura base do relatório com integração do cabeçalho padrão via sub-relatório
  - Definir parâmetros específicos: MES, ANO, saldos financeiros e coleções de dados agrupados
  - Criar bandas title, pageHeader, detail e pageFooter com layout apropriado
  - _Requirements: 1.1, 1.2, 1.3, 6.1, 6.2_

- [ ] 4. Implementar seção de resumo financeiro

  - Criar pageHeader com resumo consolidado dos valores financeiros
  - Implementar formatação monetária e destacar visualmente saldos positivos/negativos
  - Adicionar cálculos e exibição de saldo período anterior, entradas, saídas e saldo final
  - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5_

- [ ] 5. Criar sub-relatório para grupos de rubrica (rubrica_agrupada_subreport.jrxml)

  - Implementar template para exibir dados agrupados por grupos de mensalidade
  - Definir campos do bean: grupoNome, rubricaNome, valor, quantidade
  - Criar agrupamento por grupoNome com totalizações por grupo e geral
  - _Requirements: 3.1, 3.2, 3.3, 4.1, 4.2_

- [ ] 6. Implementar formatação e layout do sub-relatório

  - Adicionar formatação zebrada para melhor legibilidade
  - Implementar cabeçalhos de coluna e rodapés com totalizações
  - Aplicar formatação monetária consistente e destacar totais
  - _Requirements: 3.4, 3.5, 4.5_

- [ ] 7. Integrar sub-relatórios no relatório principal

  - Adicionar chamadas para sub-relatório de grupos na banda detail
  - Configurar passagem de parâmetros e datasources para os sub-relatórios
  - Implementar seções separadas para entradas e saídas com títulos apropriados
  - _Requirements: 1.3, 6.3, 6.4_

- [ ] 8. Implementar tratamento de dados vazios e casos especiais

  - Adicionar mensagens informativas quando não houver movimentos em grupos
  - Implementar tratamento para parâmetros nulos e valores ausentes
  - Criar placeholder para logo quando não estiver disponível
  - _Requirements: 4.3_

- [ ] 9. Criar classe de modelo para dados agrupados

  - Implementar DadosGrupoRubrica com campos grupoNome, rubricaNome, valor, quantidade
  - Adicionar getters, setters e métodos auxiliares necessários
  - Criar classe ParametrosRelatorioFinanceiro para encapsular parâmetros do relatório
  - _Requirements: 6.1, 6.2_

- [ ] 10. Implementar service para processamento de dados

  - Criar método para agrupar movimentações por grupos de mensalidade e rubricas
  - Implementar cálculos de totais de entradas, saídas e saldos
  - Adicionar lógica para separar entradas e saídas em coleções distintas
  - _Requirements: 3.1, 5.1, 5.2, 5.3_

- [ ] 11. Criar endpoint no controller para geração do relatório

  - Implementar método para receber parâmetros de mês, ano e filtros opcionais
  - Adicionar validação de parâmetros e tratamento de erros
  - Integrar com service de dados e engine do Jasper Reports
  - _Requirements: 1.1, 6.5_

- [ ] 12. Implementar geração em múltiplos formatos

  - Adicionar suporte para geração em PDF, Excel e HTML
  - Configurar headers HTTP apropriados para cada formato
  - Implementar tratamento de exceções específicas do Jasper
  - _Requirements: 1.4_

- [ ] 13. Criar testes unitários para o service

  - Testar agrupamento de dados por grupos de rubrica
  - Validar cálculos de totais e saldos
  - Testar cenários com dados vazios e valores extremos
  - _Requirements: 3.1, 3.2, 5.1, 5.2_

- [ ] 14. Criar testes de integração para geração do relatório

  - Testar fluxo completo de geração do relatório
  - Validar integração entre relatório principal e sub-relatórios
  - Testar geração em diferentes formatos (PDF, Excel, HTML)
  - _Requirements: 1.1, 1.2, 1.3, 1.4_

- [ ] 15. Implementar template HTML para interface do usuário

  - Criar formulário para seleção de parâmetros (mês, ano, formato)
  - Adicionar filtros opcionais para grupos de mensalidade e rubricas
  - Implementar preview de dados via AJAX antes da geração
  - _Requirements: 1.1, 1.2_

- [ ] 16. Adicionar validações e melhorias de UX
  - Implementar validação de período no frontend
  - Adicionar loading indicators durante geração do relatório
  - Criar mensagens de erro amigáveis para casos de falha
  - _Requirements: 1.1, 1.2_
