# Requirements Document

## Introduction

Este documento define os requisitos para a criação de um Jasper Report para o demonstrativo financeiro mensal agrupado por grupos de rubrica, baseado no template HTML existente. O relatório deve incluir um cabeçalho padrão reutilizável para todos os relatórios do sistema e apresentar os dados financeiros organizados por grupos de rubrica com detalhamento de movimentos.

## Requirements

### Requirement 1

**User Story:** Como usuário do sistema de tesouraria, eu quero gerar um relatório PDF do demonstrativo financeiro mensal agrupado por grupos de rubrica, para que eu possa ter uma visão consolidada e detalhada das movimentações financeiras organizadas por categoria.

#### Acceptance Criteria

1. WHEN o usuário solicitar o relatório THEN o sistema SHALL gerar um arquivo PDF com o demonstrativo financeiro mensal agrupado por grupos de rubrica
2. WHEN o relatório for gerado THEN o sistema SHALL incluir todos os parâmetros necessários: instituição, período, saldos e dados agrupados
3. WHEN o relatório for exibido THEN o sistema SHALL apresentar os dados organizados por grupos de rubrica com seus respectivos movimentos detalhados
4. WHEN o relatório for impresso THEN o sistema SHALL manter a formatação e legibilidade em formato físico

### Requirement 2

**User Story:** Como administrador do sistema, eu quero um cabeçalho padrão reutilizável para todos os relatórios, para que eu possa manter consistência visual e reduzir duplicação de código nos templates de relatório.

#### Acceptance Criteria

1. WHEN um relatório for gerado THEN o sistema SHALL utilizar o cabeçalho padrão com logo, nome e endereço da instituição
2. WHEN o cabeçalho padrão for criado THEN o sistema SHALL permitir reutilização em outros relatórios do sistema
3. WHEN o cabeçalho for renderizado THEN o sistema SHALL posicionar corretamente o logo, título e informações da instituição
4. IF o logo da instituição estiver disponível THEN o sistema SHALL exibi-lo no cabeçalho
5. WHEN o cabeçalho for aplicado THEN o sistema SHALL manter consistência visual entre todos os relatórios

### Requirement 3

**User Story:** Como usuário financeiro, eu quero visualizar os grupos de rubrica com seus totais de entrada, saída e saldo, para que eu possa analisar o desempenho financeiro por categoria.

#### Acceptance Criteria

1. WHEN o relatório for gerado THEN o sistema SHALL agrupar os movimentos por grupos de rubrica
2. WHEN cada grupo for exibido THEN o sistema SHALL mostrar o total de entradas, saídas e saldo do grupo
3. WHEN os valores forem apresentados THEN o sistema SHALL formatar corretamente os valores monetários
4. WHEN houver saldo positivo THEN o sistema SHALL destacar visualmente com formatação apropriada
5. WHEN houver saldo negativo THEN o sistema SHALL destacar visualmente com formatação de alerta

### Requirement 4

**User Story:** Como usuário do sistema, eu quero visualizar os movimentos detalhados dentro de cada grupo de rubrica, para que eu possa analisar as transações específicas que compõem cada categoria.

#### Acceptance Criteria

1. WHEN um grupo de rubrica for exibido THEN o sistema SHALL listar todos os movimentos detalhados do grupo
2. WHEN os movimentos forem listados THEN o sistema SHALL incluir data/hora, descrição, tipo de rubrica, rubrica, valor e origem/destino
3. WHEN não houver movimentos em um grupo THEN o sistema SHALL exibir mensagem informativa apropriada
4. WHEN os movimentos forem ordenados THEN o sistema SHALL organizá-los de forma cronológica
5. WHEN os valores forem exibidos THEN o sistema SHALL aplicar formatação monetária consistente

### Requirement 5

**User Story:** Como usuário do sistema, eu quero visualizar o resumo financeiro consolidado no relatório, para que eu possa ter uma visão geral da situação financeira do período.

#### Acceptance Criteria

1. WHEN o relatório for gerado THEN o sistema SHALL incluir saldo do período anterior
2. WHEN o resumo for exibido THEN o sistema SHALL mostrar total de entradas e saídas do período
3. WHEN o saldo operacional for calculado THEN o sistema SHALL apresentar o resultado das operações do período
4. WHEN o saldo final for calculado THEN o sistema SHALL mostrar o saldo consolidado incluindo reconciliação bancária
5. WHEN os totais forem apresentados THEN o sistema SHALL destacar visualmente os valores principais

### Requirement 6

**User Story:** Como desenvolvedor do sistema, eu quero que o relatório utilize os mesmos parâmetros do relatório existente, para que eu possa manter compatibilidade e reutilizar a infraestrutura de geração de relatórios.

#### Acceptance Criteria

1. WHEN o relatório for desenvolvido THEN o sistema SHALL utilizar os parâmetros definidos no template de referência
2. WHEN os parâmetros forem processados THEN o sistema SHALL aceitar dados de instituição, período e valores financeiros
3. WHEN os dados agrupados forem recebidos THEN o sistema SHALL processar as coleções de grupos de rubrica
4. WHEN sub-relatórios forem necessários THEN o sistema SHALL suportar a estrutura de sub-relatórios para detalhamento
5. WHEN a geração for executada THEN o sistema SHALL manter compatibilidade com a infraestrutura existente de Jasper Reports
