# Remoção de Campos da Reconciliação Bancária

## Roteiro de Design
O objetivo desta modificação é remover campos desnecessários da funcionalidade de reconciliação bancária, tanto no frontend quanto no backend. Os campos removidos incluem Total Entradas, Total Saídas, Saldo Sugerido, Saldo Final, Data Reconciliação e Observações.

## Requisitos
- Remover os campos especificados do formulário de reconciliação no frontend
- Remover os campos correspondentes da entidade ReconciliacaoMensal no backend
- Atualizar o serviço de reconciliação para não utilizar mais esses campos
- Criar script de migração para remover as colunas do banco de dados

## Tarefas
- [x] Remover campos do template `reconciliacao/formulario.html`
- [x] Remover campos da entidade `ReconciliacaoMensal`
- [x] Atualizar `ReconciliacaoService` para remover referências aos campos excluídos
- [x] Atualizar `TransacaoService` para remover referências aos campos excluídos
- [x] Atualizar `RelatorioService` para remover referências aos campos excluídos
- [x] Criar script de migração do banco de dados

## Opção de Reversão
Para reverter as alterações, siga estes passos:

1. Restaurar os arquivos modificados:
```bash
git checkout HEAD~1 -- src/main/resources/templates/reconciliacao/formulario.html
git checkout HEAD~1 -- src/main/java/br/com/sigest/tesouraria/domain/entity/ReconciliacaoMensal.java
git checkout HEAD~1 -- src/main/java/br/com/sigest/tesouraria/service/ReconciliacaoService.java
git checkout HEAD~1 -- src/main/java/br/com/sigest/tesouraria/service/TransacaoService.java
git checkout HEAD~1 -- src/main/java/br/com/sigest/tesouraria/service/RelatorioService.java
```

2. Remover o script de migração:
```bash
rm src/main/resources/db/migration/V202509172050__Remove_Unused_Fields_From_Reconciliacao_Mensal.sql
```