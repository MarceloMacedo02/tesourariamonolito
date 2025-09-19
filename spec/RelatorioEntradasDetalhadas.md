# Relatório de Entradas Detalhadas

## Roteiro de Design

Esta feature visa criar um novo relatório que detalhe as entradas financeiras do mês, organizadas por sócios e rubricas de pagamento. O relatório mostrará:

1. **Entradas por Sócios**: Lista com nome do sócio, data do crédito e valor
2. **Rubricas de Pagamento**: Resumo das rubricas com quantidade e valor total

O relatório deve permitir filtragem por mês e ano, exibindo apenas períodos com movimento registrado.

## Requisitos

1. O relatório deve exibir entradas por sócios com:
   - Nome do sócio
   - Data do crédito
   - Rubrica
   - Valor creditado

2. O relatório deve exibir um resumo por rubricas com:
   - Nome da rubrica
   - Quantidade de entradas
   - Valor total por rubrica

3. O filtro deve mostrar apenas meses/anos com movimento registrado

4. O relatório deve estar disponível tanto em formato HTML quanto PDF

5. O relatório deve ser acessado pelo menu de relatórios

## Tarefas

- [x] Criar DTO para representar os dados do relatório de entradas detalhadas
- [x] Criar novo método no RelatorioService para gerar relatório de entradas detalhadas
- [x] Criar novo endpoint no RelatorioController para o relatório de entradas detalhadas
- [x] Criar template HTML para o relatório de entradas detalhadas
- [x] Atualizar sidebar/menu para incluir o novo relatório
- [x] Implementar filtro por meses/anos com movimento
- [ ] Criar arquivo JRXML para o relatório PDF de entradas detalhadas (opcional)

## Opção de Reversão

Para reverter as alterações, basta restaurar os arquivos modificados para as versões anteriores:

```bash
git checkout HEAD~1 -- src/main/java/br/com/sigest/tesouraria/dto/RelatorioEntradasDetalhadasDto.java
git checkout HEAD~1 -- src/main/java/br/com/sigest/tesouraria/service/RelatorioService.java
git checkout HEAD~1 -- src/main/java/br/com/sigest/tesouraria/controller/RelatorioController.java
git checkout HEAD~1 -- src/main/resources/templates/relatorios/entradas-detalhadas.html
git checkout HEAD~1 -- src/main/resources/templates/fragments/sidebar.html
git checkout HEAD~1 -- src/main/java/br/com/sigest/tesouraria/domain/repository/MovimentoRepository.java
```