# Documento de Design

## Visão Geral

Este design implementa funcionalidades de edição e exclusão para as contas a receber na página de detalhes de transações de crédito. A solução adiciona uma coluna de ações à tabela existente e implementa modais para edição e confirmação de exclusão, mantendo a consistência com a interface atual.

## Arquitetura

### Componentes Frontend

- **Template HTML**: Modificação do arquivo `detalhes-creditos.html` para adicionar coluna de ações
- **Modais Bootstrap**: Dois novos modais (edição e confirmação de exclusão)
- **JavaScript**: Funções para manipular eventos de edição e exclusão
- **CSS**: Estilos para botões de ação e ícones

### Componentes Backend

- **Controller**: Novos endpoints no `TransacaoController` para editar e excluir contas a receber
- **Service**: Métodos no service para validação e operações de CRUD
- **DTOs**: Objetos para transferência de dados das operações

## Componentes e Interfaces

### 1. Interface do Usuário

#### Tabela de Contas a Receber

```html
<table class="table table-striped">
  <thead>
    <tr>
      <th></th>
      <!-- Checkbox -->
      <th>Descrição</th>
      <th>Vencimento</th>
      <th>Valor</th>
      <th>Tipo</th>
      <th>Ações</th>
      <!-- Nova coluna -->
    </tr>
  </thead>
  <tbody>
    <!-- Cada linha terá botões de editar e excluir -->
  </tbody>
</table>
```

#### Modal de Edição

- Formulário com campos: descrição, data de vencimento, valor, rubrica
- Validação client-side e server-side
- Botões: Cancelar, Salvar

#### Modal de Confirmação de Exclusão

- Exibição dos detalhes da conta (descrição, valor, vencimento)
- Mensagem de confirmação clara
- Botões: Cancelar, Confirmar Exclusão

### 2. Endpoints da API

#### Editar Conta a Receber

```
PUT /transacoes/{transacaoId}/contas-receber/{contaId}
Content-Type: application/json

{
    "descricao": "string",
    "dataVencimento": "yyyy-MM-dd",
    "valor": "decimal",
    "rubricaId": "long"
}
```

#### Excluir Conta a Receber

```
DELETE /transacoes/{transacaoId}/contas-receber/{contaId}
```

#### Obter Detalhes da Conta

```
GET /transacoes/{transacaoId}/contas-receber/{contaId}
```

### 3. Estrutura JavaScript

```javascript
// Funções principais
function editarContaReceber(contaId)
function excluirContaReceber(contaId)
function confirmarExclusao(contaId)
function salvarEdicaoContaReceber()
function carregarDadosContaParaEdicao(contaId)
```

## Modelos de Dados

### DTO para Edição de Conta a Receber

```java
public class EditarContaReceberDto {
    private String descricao;
    private LocalDate dataVencimento;
    private BigDecimal valor;
    private Long rubricaId;
    // getters e setters
}
```

### Resposta de Validação

```java
public class ValidationResponse {
    private boolean success;
    private String message;
    private Map<String, String> fieldErrors;
    // getters e setters
}
```

## Tratamento de Erros

### Validações de Negócio

1. **Conta não encontrada**: HTTP 404 com mensagem específica
2. **Conta já paga**: HTTP 400 - "Não é possível editar/excluir conta já quitada"
3. **Dados inválidos**: HTTP 400 com detalhes dos campos inválidos
4. **Conflito de concorrência**: HTTP 409 - "Conta foi modificada por outro usuário"

### Validações de Entrada

- Descrição: obrigatória, máximo 255 caracteres
- Data de vencimento: obrigatória, formato válido
- Valor: obrigatório, maior que zero
- Rubrica: obrigatória, deve existir no sistema

### Tratamento de Erros no Frontend

```javascript
function handleApiError(xhr, operation) {
  let message = "Erro inesperado";

  switch (xhr.status) {
    case 400:
      message = xhr.responseJSON?.message || "Dados inválidos";
      break;
    case 404:
      message = "Conta não encontrada";
      break;
    case 409:
      message = "Conta foi modificada. Recarregando dados...";
      location.reload();
      return;
    default:
      message = `Erro ao ${operation}. Tente novamente.`;
  }

  showCustomToast(message, "error");
}
```

## Estratégia de Testes

### Testes Unitários (Backend)

- Validação de DTOs
- Lógica de negócio no service
- Tratamento de exceções
- Autorização de acesso

### Testes de Integração

- Endpoints da API
- Transações de banco de dados
- Validações de integridade referencial

### Testes Frontend

- Abertura e fechamento de modais
- Validação de formulários
- Chamadas AJAX
- Atualização da interface após operações

### Cenários de Teste Principais

1. **Edição bem-sucedida**: Modificar dados válidos e verificar atualização
2. **Edição com dados inválidos**: Verificar mensagens de erro
3. **Exclusão bem-sucedida**: Remover conta e verificar atualização da tabela
4. **Exclusão de conta paga**: Verificar bloqueio e mensagem de erro
5. **Operações concorrentes**: Simular modificações simultâneas
6. **Conectividade**: Testar comportamento com falhas de rede

## Considerações de Segurança

### Autorização

- Verificar se o usuário tem permissão para modificar contas do sócio
- Validar se a conta pertence à transação informada
- Implementar CSRF protection nos formulários

### Validação

- Sanitização de entrada para prevenir XSS
- Validação server-side obrigatória
- Limites de taxa para prevenir spam

### Auditoria

- Log de todas as operações de edição e exclusão
- Registro do usuário que executou a ação
- Timestamp das operações

## Fluxo de Implementação

### Fase 1: Backend

1. Criar DTOs para edição
2. Implementar endpoints no controller
3. Adicionar métodos no service
4. Implementar validações
5. Testes unitários

### Fase 2: Frontend

1. Adicionar coluna de ações na tabela
2. Criar modais de edição e exclusão
3. Implementar JavaScript para manipular eventos
4. Integrar com APIs
5. Testes de interface

### Fase 3: Integração

1. Testes end-to-end
2. Validação de fluxos completos
3. Ajustes de UX
4. Documentação
