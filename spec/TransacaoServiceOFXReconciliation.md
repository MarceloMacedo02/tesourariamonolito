# TransacaoService - OFX Reconciliation Integration

## Roteiro de Design
O objetivo desta modificação é integrar a funcionalidade de reconciliação bancária com o processamento de arquivos OFX. Sempre que um arquivo OFX for processado, o sistema deve criar ou atualizar automaticamente os itens de reconciliação bancária para o período correspondente.

## Requisitos
- Ao processar um arquivo OFX, o sistema deve identificar o período das transações
- Para cada conta financeira, deve ser criado ou atualizado um item de reconciliação bancária
- Só deve haver uma inclusão por conta, mês e ano
- Os campos devem ser preenchidos conforme:
  - saldoAnterior = "Saldo inicial do período"
  - saldoAtual = "Saldo final do período"
  - receitas = "Total de entradas"
  - despesas = "Total de saídas"

## Tarefas
- [x] Adicionar dependências necessárias no construtor do TransacaoService
- [x] Criar método updateOrCreateReconciliationItem para gerenciar a criação/atualização de itens de reconciliação
- [x] Atualizar o método processOfxFile para chamar a atualização de reconciliação
- [x] Adicionar método de busca personalizada no ReconciliacaoBancariaRepository

## Opção de Reversão
Para reverter as alterações, siga estes passos:

1. Reverter as alterações no TransacaoService.java:
```bash
git checkout HEAD~1 -- src/main/java/br/com/sigest/tesouraria/service/TransacaoService.java
```

2. Reverter as alterações no ReconciliacaoBancariaRepository.java:
```bash
git checkout HEAD~1 -- src/main/java/br/com/sigest/tesouraria/domain/repository/ReconciliacaoBancariaRepository.java
```