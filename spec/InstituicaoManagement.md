# Instituição Management Feature

## Roteiro de Design
O objetivo desta modificação é ajustar a funcionalidade de gerenciamento da instituição para funcionar como um único elemento editável, conforme solicitado. As alterações incluem a consolidação das páginas de cadastro e detalhes em uma única interface de gerenciamento.

## Requisitos
- A instituição deve ser tratada como um único elemento único no sistema
- Deve haver uma única página para visualizar e editar os dados da instituição
- A funcionalidade de upload de logo deve estar integrada ao formulário principal
- Os cargos da instituição devem ser gerenciáveis na mesma interface

## Tarefas
- [x] Atualizar o template detalhes.html para incluir o formulário de edição completo
- [x] Atualizar o template cadastro.html para manter consistência com a interface de detalhes
- [x] Ajustar o controller para lidar corretamente com o upload de arquivos
- [x] Corrigir o erro de Thymeleaf no componente toast
- [x] Manter a funcionalidade de cargos da instituição

## Opção de Reversão
Para reverter as alterações, siga estes passos:

1. Restaurar os arquivos de template originais:
```bash
git checkout HEAD~1 -- src/main/resources/templates/instituicoes/detalhes.html
git checkout HEAD~1 -- src/main/resources/templates/instituicoes/cadastro.html
```

2. Restaurar o controller original:
```bash
git checkout HEAD~1 -- src/main/java/br/com/sigest/tesouraria/controller/InstituicaoController.java
```