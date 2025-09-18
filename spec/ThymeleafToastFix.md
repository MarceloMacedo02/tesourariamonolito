# Thymeleaf Toast Component Fix

## Roteiro de Design
O objetivo desta modificação é corrigir um erro de sintaxe no template Thymeleaf que estava impedindo o processamento correto dos arquivos `instituicoes/cadastro.html` e `instituicoes/detalhes.html` devido ao uso incorreto do componente toast.

## Requisitos
- Corrigir o erro de sintaxe na chamada do componente toast
- Garantir que o componente toast seja chamado com os parâmetros necessários
- Manter a funcionalidade existente intacta

## Tarefas
- [x] Corrigir a chamada do componente toast no arquivo cadastro.html
- [x] Corrigir a chamada do componente toast no arquivo detalhes.html
- [x] Passar os parâmetros necessários (message, header, type) para o componente toast

## Opção de Reversão
Para reverter as alterações, basta restaurar os arquivos para a versão anterior:

```bash
git checkout HEAD~1 -- src/main/resources/templates/instituicoes/cadastro.html
git checkout HEAD~1 -- src/main/resources/templates/instituicoes/detalhes.html
```