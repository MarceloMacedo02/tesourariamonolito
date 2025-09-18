# Reconciliacao Form Template Fix

## Roteiro de Design
O objetivo desta modificação é corrigir um erro de sintaxe no template de formulário de reconciliação que estava impedindo o processamento correto pelo Thymeleaf.

## Requisitos
- Corrigir o erro de sintaxe na expressão Thymeleaf na linha 158 do arquivo formulario.html
- Garantir que o template seja processado corretamente pelo Thymeleaf
- Manter a funcionalidade existente intacta

## Tarefas
- [x] Corrigir a expressão Thymeleaf `th:src="@{/assets/js/moment.min.js"` para `th:src="@{/assets/js/moment.min.js}"`
- [x] Adicionar o caractere de fechamento `}` que estava faltando

## Opção de Reversão
Para reverter as alterações, basta restaurar o arquivo `formulario.html` para a versão anterior:

```bash
git checkout HEAD~1 -- src/main/resources/templates/reconciliacao/formulario.html
```