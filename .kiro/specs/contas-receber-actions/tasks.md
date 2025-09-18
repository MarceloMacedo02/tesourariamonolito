# Plano de Implementação

- [x] 1. Criar DTOs e estruturas de dados para edição e exclusão

  - Implementar `EditarContaReceberDto` com validações
  - Criar `ValidationResponse` para respostas de erro
  - Adicionar imports necessários no controller
  - _Requisitos: 1.4, 2.6, 4.1, 4.3_

- [x] 2. Implementar endpoints no TransacaoController

  - Adicionar endpoint PUT para editar conta a receber
  - Adicionar endpoint DELETE para excluir conta a receber
  - Adicionar endpoint GET para obter detalhes da conta
  - Implementar tratamento de exceções e validações
  - _Requisitos: 1.4, 2.4, 4.1, 4.2_

- [x] 3. Implementar lógica de negócio no service

  - Criar métodos para editar conta a receber com validações
  - Criar métodos para excluir conta com verificações de integridade
  - Implementar validações de negócio (conta já paga, etc.)
  - Adicionar logs de auditoria para operações
  - _Requisitos: 1.5, 2.5, 2.6, 4.4_

- [x] 4. Modificar template HTML para adicionar coluna de ações

  - Adicionar coluna "Ações" na tabela de contas a receber
  - Implementar botões de editar e excluir com ícones
  - Adicionar tooltips para os botões de ação
  - Manter consistência visual com o design existente
  - _Requisitos: 3.1, 3.2, 3.3_

- [x] 5. Criar modal de edição de conta a receber

  - Implementar modal Bootstrap com formulário de edição
  - Adicionar campos para descrição, vencimento, valor e rubrica
  - Implementar validação client-side nos campos
  - Adicionar máscaras de entrada para valor e data
  - _Requisitos: 1.2, 1.3, 1.5_

- [x] 6. Criar modal de confirmação de exclusão

  - Implementar modal de confirmação com detalhes da conta
  - Exibir descrição, valor e vencimento para verificação
  - Adicionar botões de cancelar e confirmar exclusão
  - Implementar design responsivo e acessível
  - _Requisitos: 2.2, 2.3, 2.5_

- [x] 7. Implementar JavaScript para manipular eventos de edição

  - Criar função `editarContaReceber()` para abrir modal de edição
  - Implementar `carregarDadosContaParaEdicao()` para popular formulário
  - Criar `salvarEdicaoContaReceber()` para submeter alterações
  - Adicionar tratamento de erros e validações
  - _Requisitos: 1.1, 1.4, 4.1, 4.3_

- [x] 8. Implementar JavaScript para manipular eventos de exclusão

  - Criar função `excluirContaReceber()` para abrir modal de confirmação
  - Implementar `confirmarExclusao()` para executar exclusão
  - Adicionar tratamento de erros específicos para exclusão
  - Implementar atualização da tabela após exclusão
  - _Requisitos: 2.1, 2.4, 4.2, 4.5_

- [ ] 9. Integrar sistema de notificações e feedback

  - Implementar notificações de sucesso usando toast existente
  - Adicionar tratamento de erros com mensagens específicas
  - Implementar feedback visual durante operações (loading)
  - Manter estado da página após operações
  - _Requisitos: 3.5, 4.1, 4.2, 4.3_

- [ ] 10. Implementar testes unitários para os novos endpoints

  - Criar testes para endpoint de edição com dados válidos e inválidos
  - Criar testes para endpoint de exclusão com cenários de sucesso e erro
  - Testar validações de negócio e tratamento de exceções
  - Verificar logs de auditoria e segurança
  - _Requisitos: 1.5, 2.6, 4.4_

- [ ] 11. Implementar testes de integração frontend

  - Testar abertura e fechamento dos modais
  - Verificar validação de formulários e máscaras de entrada
  - Testar chamadas AJAX e tratamento de respostas
  - Validar atualização da interface após operações
  - _Requisitos: 3.4, 3.5, 4.1_

- [ ] 12. Integrar e testar fluxo completo end-to-end
  - Testar fluxo completo de edição desde clique até atualização
  - Testar fluxo completo de exclusão com confirmação
  - Verificar comportamento com múltiplas operações simultâneas
  - Validar experiência do usuário e responsividade
  - _Requisitos: 3.4, 3.5, 4.4_
