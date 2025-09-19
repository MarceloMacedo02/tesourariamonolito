# Dashboard Financeiro

## Roteiro de Design

O dashboard financeiro será uma nova funcionalidade que permitirá aos usuários visualizar informações financeiras importantes de forma clara e intuitiva. Ele será composto por vários gráficos que mostrarão:

1.  Entradas e saídas mensais e anuais
2.  Saldos mensais e anuais
3.  Quantidade de sócios, sócios frequentes e não frequentes
4.  Gráfico de inadimplências
5.  Gráfico de movimentações e valores a receber

A interface será responsiva e fácil de usar, com filtros para períodos específicos.

## Requisitos

### Requisitos Funcionais

- RF01: Exibir gráfico de entradas mensais e anuais
- RF02: Exibir gráfico de saídas mensais e anuais
- RF03: Exibir gráfico de saldos mensais e anuais
- RF04: Exibir gráfico de quantidade de sócios
- RF05: Exibir gráfico de sócios frequentes e não frequentes
- RF06: Exibir gráfico de inadimplências
- RF07: Exibir gráfico de movimentações
- RF08: Exibir gráfico de valores a receber
- RF09: Permitir filtragem por período (mensal/anual)

### Requisitos Não-Funcionais

- RNF01: Os dados devem ser carregados de forma eficiente
- RNF02: A interface deve ser responsiva
- RNF03: Os gráficos devem ser interativos

## Tarefas

- [ ] Criar entidades e repositórios para coleta de dados
- [ ] Implementar serviços para cálculo dos dados dos gráficos
- [ ] Criar endpoints REST para fornecer dados aos gráficos
- [ ] Desenvolver frontend com biblioteca de gráficos
- [ ] Integrar frontend com backend
- [ ] Testar funcionalidade

## Opção de Reversão

Para reverter as alterações, remova os arquivos criados relacionados ao dashboard:
- Controller
- Service
- DTOs
- Templates HTML
- Arquivos JavaScript/CSS relacionados