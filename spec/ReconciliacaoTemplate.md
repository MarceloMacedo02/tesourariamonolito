# Reconciliacao Template (formulario.html)

## Roteiro de Design
O objetivo desta modificação é atualizar o template de formulário de reconciliação bancária para incluir os novos campos adicionados à entidade ReconciliacaoBancaria. A interface agora permitirá que o usuário insira informações detalhadas sobre o período, saldos e movimentações financeiras.

## Requisitos
- Adicionar campos para mês e ano da reconciliação
- Adicionar campos para saldo anterior e saldo atual
- Adicionar campos para receitas e despesas
- Manter a funcionalidade existente de edição e salvamento
- Garantir que os campos sejam digitáveis conforme solicitado

## Tarefas
- [x] Atualizar a tabela de bancos no formulário para incluir os novos campos
- [x] Adicionar campos de entrada para mês e ano
- [x] Adicionar campos de entrada para saldoAnterior e saldoAtual
- [x] Adicionar campos de entrada para receitas e despesas
- [x] Manter a formatação monetária nos campos apropriados

## Opção de Reversão
Para reverter as alterações, basta restaurar o arquivo `formulario.html` para a versão anterior, removendo as colunas e campos adicionados:

Comando git para reverter:
```bash
git checkout HEAD~1 -- src/main/resources/templates/reconciliacao/formulario.html
```