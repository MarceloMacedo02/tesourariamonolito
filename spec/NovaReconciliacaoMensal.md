# Nova Feature: Refatoração da Lógica de Reconciliação Mensal

## Roteiro de Design

A nova feature visa simplificar e refatorar a lógica de reconciliação mensal para atender aos seguintes requisitos:

1. A ReconciliacaoMensal deve armazenar os dados conforme o exemplo:
   - Saldo inicial: R$ 10.457,22
   - Entradas: R$ 15.615,00
   - Saídas: R$ 10.330,53
   - Saldo final: R$ 15.741,69

2. A ReconciliacaoBancaria deve ter somente um atributo de valor final para cada conta financeira.

3. Simplificar a lógica de cálculo e manter a integridade dos dados.

## Requisitos

- Refatorar a classe ReconciliacaoMensal para ter apenas os campos essenciais:
  - saldoInicial
  - totalEntradas
  - totalSaidas
  - saldoFinal (calculado)
  
- Refatorar a classe ReconciliacaoBancaria para ter apenas:
  - valorFinal (único atributo por conta financeira)

- Atualizar todos os serviços que utilizam essas classes:
  - ReconciliacaoService
  - TransacaoService
  - RelatorioService

- Atualizar os templates HTML para refletir as novas regras:
  - Formulário de reconciliação com campos para saldo inicial, entradas, saídas e saldo final
  - Lista de reconciliações com as novas colunas

- Manter compatibilidade com os repositórios existentes

- Criar métodos de cálculo na ReconciliacaoMensal:
  - getSaldoFinal() - calcula com base em saldoInicial + totalEntradas - totalSaidas
  - getResultadoOperacional() - calcula com base em totalEntradas - totalSaidas

## Tarefas

1. Criar nova estrutura para ReconciliacaoMensal com campos simplificados
2. Refatorar ReconciliacaoBancaria para ter apenas um atributo de valor final por conta financeira
3. Atualizar ReconciliacaoService para usar a nova estrutura
4. Atualizar TransacaoService para usar a nova estrutura
5. Atualizar RelatorioService para usar a nova estrutura
6. Atualizar repositórios se necessário
7. Atualizar templates HTML para refletir as novas regras
8. Criar testes para validar a nova lógica
9. Documentar as mudanças

## Exemplo de Uso

Com a nova estrutura, podemos criar uma reconciliação mensal da seguinte forma:

```java
ReconciliacaoMensal reconciliacao = new ReconciliacaoMensal();
reconciliacao.setSaldoInicial(new BigDecimal("10457.22"));
reconciliacao.setTotalEntradas(new BigDecimal("15615.00"));
reconciliacao.setTotalSaidas(new BigDecimal("10330.53"));

// O saldo final será calculado automaticamente como R$ 15.741,69
BigDecimal saldoFinal = reconciliacao.getSaldoFinal();

// O resultado operacional será calculado como R$ 5.284,47 (entradas - saídas)
BigDecimal resultadoOperacional = reconciliacao.getResultadoOperacional();
```

Para cada conta financeira, criamos uma reconciliação bancária:

```java
ReconciliacaoBancaria rb = new ReconciliacaoBancaria();
rb.setContaFinanceira(contaFinanceira);
rb.setValorFinal(new BigDecimal("15741.69")); // Valor final da conta
```

## Opção de Reversão

Para reverter as alterações, siga os passos abaixo:

1. Restaurar as classes ReconciliacaoMensal e ReconciliacaoBancaria para a versão anterior
2. Reverter as alterações nos serviços ReconciliacaoService, TransacaoService e RelatorioService
3. Reverter os templates HTML para a versão anterior
4. Executar os testes para garantir que tudo está funcionando corretamente

Comandos Git para reversão:
```bash
git checkout HEAD~1 -- src/main/java/br/com/sigest/tesouraria/domain/entity/ReconciliacaoMensal.java
git checkout HEAD~1 -- src/main/java/br/com/sigest/tesouraria/domain/entity/ReconciliacaoBancaria.java
git checkout HEAD~1 -- src/main/java/br/com/sigest/tesouraria/service/ReconciliacaoService.java
git checkout HEAD~1 -- src/main/java/br/com/sigest/tesouraria/service/TransacaoService.java
git checkout HEAD~1 -- src/main/java/br/com/sigest/tesouraria/service/RelatorioService.java
git checkout HEAD~1 -- src/main/resources/templates/reconciliacao/formulario.html
git checkout HEAD~1 -- src/main/resources/templates/reconciliacao/lista.html
```