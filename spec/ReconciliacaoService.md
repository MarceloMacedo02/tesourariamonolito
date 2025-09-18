# ReconciliacaoService

## Roteiro de Design
O objetivo desta modificação é ajustar o serviço de reconciliação para inicializar corretamente os novos campos adicionados à entidade ReconciliacaoBancaria. O serviço agora precisa preencher os campos de mês, ano, saldos e movimentações ao criar novas reconciliações bancárias.

## Requisitos
- Inicializar corretamente os novos campos da entidade ReconciliacaoBancaria
- Manter a compatibilidade com as funcionalidades existentes
- Garantir que os valores iniciais sejam apropriados para os novos campos

## Tarefas
- [x] Atualizar método newReconciliacao para definir os novos campos
- [x] Inicializar mes e ano com os valores da reconciliação mensal
- [x] Inicializar saldoAnterior e saldoAtual com o saldo atual da conta
- [x] Inicializar receitas e despesas com zero como valor padrão

## Opção de Reversão
Para reverter as alterações, basta restaurar o arquivo `ReconciliacaoService.java` para a versão anterior, removendo as linhas que definem os novos campos:

```java
rb.setMes(mes);
rb.setAno(ano);
rb.setSaldoAnterior(conta.getSaldoAtual());
rb.setSaldoAtual(conta.getSaldoAtual());
rb.setReceitas(BigDecimal.ZERO);
rb.setDespesas(BigDecimal.ZERO);
```

Comando git para reverter:
```bash
git checkout HEAD~1 -- src/main/java/br/com/sigest/tesouraria/service/ReconciliacaoService.java
```