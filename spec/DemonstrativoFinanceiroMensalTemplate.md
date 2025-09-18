# Demonstrativo Financeiro Mensal Template

## Roteiro de Design
O objetivo desta modificação é ajustar o template do demonstrativo financeiro mensal para tornar mais claro que o campo "Saldo do Período Anterior" está utilizando o valor da reconciliação bancária (`reconciliacao.saldoAnterior`).

## Requisitos
- Tornar explícito no template que o "Saldo do Período Anterior" refere-se ao saldo anterior da reconciliação
- Manter a funcionalidade existente intacta
- Melhorar a clareza da informação apresentada

## Tarefas
- [x] Atualizar o título do campo "Saldo do Período Anterior" para incluir "(Reconciliação)"
- [x] Atualizar a referência ao saldo do período anterior na seção "Resultado Operacional" para incluir "(Reconciliação)"

## Opção de Reversão
Para reverter as alterações, basta restaurar o arquivo `demonstrativo-financeiro-mensal.html` para a versão anterior:

```bash
git checkout HEAD~1 -- src/main/resources/templates/relatorios/demonstrativo-financeiro-mensal.html
```