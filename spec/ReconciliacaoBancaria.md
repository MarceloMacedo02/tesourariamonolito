# ReconciliacaoBancaria

## Roteiro de Design
O objetivo desta modificação é ajustar a entidade de ReconciliacaoBancaria para incluir campos adicionais necessários para a realização completa da reconciliação bancária. Os novos campos permitirão armazenar informações detalhadas sobre o período, saldos e movimentações financeiras.

## Requisitos
- Adicionar campos para mês e ano da reconciliação
- Adicionar campos para saldo anterior e saldo atual
- Adicionar campos para receitas e despesas (digitáveis)
- Manter a relação com a conta financeira
- Manter a relação com a reconciliação mensal

## Tarefas
- [x] Adicionar campo mes (Integer)
- [x] Adicionar campo ano (Integer)
- [x] Adicionar campo saldoAnterior (BigDecimal)
- [x] Adicionar campo saldoAtual (BigDecimal)
- [x] Adicionar campo receitas (BigDecimal)
- [x] Adicionar campo despesas (BigDecimal)
- [x] Atualizar a entidade ReconciliacaoBancaria com os novos campos

## Opção de Reversão
Para reverter as alterações, basta restaurar o arquivo `ReconciliacaoBancaria.java` para a versão anterior, que continha apenas os campos:
- id
- reconciliacaoMensal
- contaFinanceira
- saldo

Comando git para reverter:
```bash
git checkout HEAD~1 -- src/main/java/br/com/sigest/tesouraria/domain/entity/ReconciliacaoBancaria.java
```