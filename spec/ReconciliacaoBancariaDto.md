# ReconciliacaoBancariaDto

## Roteiro de Design
O objetivo desta modificação é criar um DTO (Data Transfer Object) para a entidade ReconciliacaoBancaria, incluindo todos os novos campos adicionados à entidade. Este DTO será utilizado para transferir dados entre a camada de serviço e a camada de apresentação.

## Requisitos
- Incluir todos os campos da entidade ReconciliacaoBancaria
- Incluir campos adicionais para facilitar a exibição na interface (como o nome da conta financeira)
- Manter a estrutura simples e otimizada para transferência de dados

## Tarefas
- [x] Criar classe ReconciliacaoBancariaDto com os campos necessários
- [x] Adicionar anotações Lombok para getters, setters e construtores
- [x] Incluir campos: id, reconciliacaoMensalId, contaFinanceiraId, contaFinanceiraNome, mes, ano, saldoAnterior, saldoAtual, receitas, despesas, saldo

## Opção de Reversão
Para reverter as alterações, basta excluir o arquivo `ReconciliacaoBancariaDto.java`.

Comando para remover o arquivo:
```bash
rm src/main/java/br/com/sigest/tesouraria/dto/ReconciliacaoBancariaDto.java
```