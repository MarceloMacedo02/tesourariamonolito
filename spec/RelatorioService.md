# Correção de Erros no Relatório Service

## Roteiro de Design
O problema identificado era que a classe `ReconciliacaoMensal` não possuía os métodos e campos necessários para armazenar todas as informações da reconciliação mensal, incluindo saldo inicial, entradas, saídas e saldo final. Além disso, faltavam os métodos `getResultadoOperacional()` e `setResultadoOperacional()`, que eram necessários para o funcionamento correto do serviço de relatórios. A solução implementada foi:

1. Garantir que a classe tenha todos os campos necessários para armazenar as informações da reconciliação mensal
2. Adicionar métodos `getResultadoOperacional()` e `setResultadoOperacional()` à classe
3. Adicionar métodos `getSaldoFinal()` e `setSaldoFinal()` para calcular o saldo final
4. Tratar os valores nulos nos métodos de cálculo
5. Criar testes para validar o funcionamento correto dos métodos

## Requisitos
- Corrigir o erro de compilação na classe `RelatorioService.java` na linha 95
- Adicionar campos necessários na classe `ReconciliacaoMensal`:
  - `saldoMesAnterior` (saldo inicial)
  - `totalReceitas` (entradas)
  - `totalDespesas` (saídas)
  - `saldoFinal` (saldo final)
  - `resultadoOperacional` (resultado operacional)
- Adicionar métodos `getResultadoOperacional()` e `setResultadoOperacional()` na classe `ReconciliacaoMensal`
- Adicionar métodos `getSaldoFinal()` e `setSaldoFinal()` na classe `ReconciliacaoMensal`
- Garantir que os métodos de cálculo tratem valores nulos corretamente
- Garantir que todos os testes continuem passando
- Manter a compatibilidade com as classes existentes (`TransacaoService` e `ReconciliacaoService`)
- Criar testes unitários para validar o funcionamento dos métodos

## Tarefas
- [x] Adicionar campo `resultadoOperacional` à classe `ReconciliacaoMensal`
- [x] Adicionar método `getResultadoOperacional()` à classe `ReconciliacaoMensal`
- [x] Adicionar método `setResultadoOperacional()` à classe `ReconciliacaoMensal`
- [x] Adicionar método `getSaldoFinal()` à classe `ReconciliacaoMensal`
- [x] Adicionar método `setSaldoFinal()` à classe `ReconciliacaoMensal`
- [x] Corrigir método `getResultadoOperacional()` para tratar valores nulos
- [x] Corrigir método `getSaldoFinal()` para tratar valores nulos
- [x] Verificar compilação do projeto
- [x] Executar testes para garantir que não houve regressão
- [x] Criar testes unitários para a classe `ReconciliacaoMensal`

## Exemplo de Uso
Com as alterações implementadas, a classe `ReconciliacaoMensal` agora pode ser usada da seguinte forma:

```java
ReconciliacaoMensal reconciliacao = new ReconciliacaoMensal();
reconciliacao.setSaldoMesAnterior(new BigDecimal("10457.22"));
reconciliacao.setTotalReceitas(new BigDecimal("15615.00"));
reconciliacao.setTotalDespesas(new BigDecimal("10330.53"));

// O saldo final será calculado automaticamente como R$ 15.741,69
BigDecimal saldoFinal = reconciliacao.getSaldoFinal();

// O resultado operacional será calculado como R$ 5.284,47 (receitas - despesas)
BigDecimal resultadoOperacional = reconciliacao.getResultadoOperacional();
```

## Opção de Reversão
Caso seja necessário reverter as alterações, siga os passos abaixo:

1. Remover os campos `resultadoOperacional` e métodos `getSaldoFinal()` e `setSaldoFinal()` da classe `ReconciliacaoMensal`
2. Remover os métodos `getResultadoOperacional()` e `setResultadoOperacional()` da classe `ReconciliacaoMensal`
3. Remover o arquivo de teste `ReconciliacaoMensalTest.java`
4. Reverter a linha 95 do arquivo `RelatorioService.java` para usar um cálculo direto ao invés de chamar o método `getResultadoOperacional()`

Para reverter via Git, execute:
```bash
git checkout HEAD -- src/main/java/br/com/sigest/tesouraria/domain/entity/ReconciliacaoMensal.java
git checkout HEAD -- src/main/java/br/com/sigest/tesouraria/service/RelatorioService.java
git checkout HEAD -- src/test/java/br/com/sigest/tesouraria/domain/entity/ReconciliacaoMensalTest.java
```