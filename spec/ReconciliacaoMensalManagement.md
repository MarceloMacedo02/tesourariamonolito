# Gerenciamento de Reconciliação Mensal

## Roteiro de Design

Este roteiro descreve a implementação das funcionalidades de listagem, criação, edição e exclusão para a entidade `ReconciliacaoMensal`. O objetivo é fornecer uma interface de usuário completa para gerenciar as reconciliações mensais, utilizando a arquitetura Spring MVC e Thymeleaf.

### Componentes Envolvidos:

*   **Entidades**: `ReconciliacaoMensal`, `ReconciliacaoBancaria`, `ContaFinanceira`.
*   **Repositórios**: `ReconciliacaoMensalRepository`, `ContaFinanceiraRepository`, `MovimentoRepository`.
*   **Serviços**: `ReconciliacaoService` (será estendido/revisado).
*   **Controladores**: `ReconciliacaoController` (será estendido/revisado).
*   **Templates Thymeleaf**: `reconciliacao/lista.html`, `reconciliacao/formulario.html` (serão revisados/atualizados).

### Arquitetura Proposta:

Será mantida a arquitetura Spring MVC existente. As requisições HTTP serão tratadas pelo `ReconciliacaoController`, que interagirá com o `ReconciliacaoService` para a lógica de negócio e o `ReconciliacaoMensalRepository` para a persistência de dados. As views serão renderizadas usando Thymeleaf, garantindo a consistência com o restante da aplicação.

### Possíveis Desafios:

*   Garantir a correta exibição e manipulação de todos os campos da `ReconciliacaoMensal` (incluindo `totalReceitas`, `totalDespesas`, `saldoFinal`) nos templates.
*   Gerenciar a coleção de `ReconciliacaoBancaria` dentro do formulário de `ReconciliacaoMensal`, permitindo a adição/edição de itens bancários associados.
*   Implementar validações robustas para os dados de entrada.
*   Assegurar que a lógica de cálculo no `ReconciliacaoService` esteja atualizada e correta para os novos campos.

## Requisitos

### Requisitos Funcionais:

1.  **Listagem**: Exibir uma tabela paginada com todas as `ReconciliacaoMensal` existentes, mostrando os campos relevantes (Mês/Ano, Saldo Anterior, Receitas, Despesas, Saldo Atual, etc.).
2.  **Criação**: Fornecer um formulário para que o usuário possa criar uma nova `ReconciliacaoMensal`, preenchendo Mês, Ano, Saldo do Mês Anterior e adicionando as `ReconciliacaoBancaria` associadas.
3.  **Edição**: Permitir que o usuário edite uma `ReconciliacaoMensal` existente através de um formulário pré-preenchido com os dados atuais.
4.  **Visualização de Detalhes**: Ao clicar em uma reconciliação na lista, o usuário deve ser capaz de ver todos os detalhes, incluindo as `ReconciliacaoBancaria` associadas.
5.  **Exclusão**: Permitir a exclusão de uma `ReconciliacaoMensal` da lista.

### Requisitos Não-Funcionais:

*   **Consistência**: Manter o estilo visual e os padrões de código existentes na aplicação.
*   **Tecnologia**: Utilizar Spring Boot, Spring MVC, Spring Data JPA e Thymeleaf.
*   **Usabilidade**: O formulário deve ser intuitivo e fácil de usar.
*   **Performance**: As operações de listagem e manipulação devem ser responsivas.

### Dependências:

*   `ReconciliacaoMensal` (entidade)
*   `ReconciliacaoBancaria` (entidade)
*   `ContaFinanceira` (entidade)
*   `ReconciliacaoMensalRepository`
*   `ReconciliacaoService`
*   `ReconciliacaoController`

### Critérios de Aceitação:

*   A página de listagem (`/reconciliacao`) deve carregar e exibir todas as reconciliações mensais sem erros.
*   O formulário de criação (`/reconciliacao/novo`) deve permitir o preenchimento e salvamento de uma nova reconciliação mensal.
*   O formulário de edição (`/reconciliacao/editar/{id}`) deve carregar os dados existentes e permitir a atualização.
*   A exclusão de uma reconciliação mensal deve remover o registro do banco de dados e da lista.
*   Todos os campos da `ReconciliacaoMensal` devem ser corretamente exibidos e persistidos.

## Tarefas

- [ ] **Revisar `ReconciliacaoController`**: Garantir que os métodos `listar`, `novoForm`, `editarForm`, `salvar` e `excluir` estejam completos e corretos para a `ReconciliacaoMensal`.
- [ ] **Revisar `ReconciliacaoService`**: Confirmar que a lógica de negócio para `save`, `findById`, `deleteById` e `newReconciliacao` está alinhada com os novos campos da `ReconciliacaoMensal`.
- [ ] **Revisar `reconciliacao/lista.html`**: Verificar se todos os campos da `ReconciliacaoMensal` estão sendo exibidos corretamente na tabela.
- [ ] **Revisar `reconciliacao/formulario.html`**: Assegurar que o formulário permite a entrada de dados para `mes`, `ano`, `saldoMesAnterior` e a manipulação da lista de `ReconciliacaoBancaria`.

## Opção de Reversão

Para reverter as alterações feitas para esta funcionalidade, siga os passos abaixo:

1.  **Reverter alterações no código**: Utilize `git revert <commit_hash>` para cada commit relacionado a esta funcionalidade. Se as alterações ainda não foram commitadas, use `git restore .` para descartar as modificações nos arquivos.
2.  **Reverter migração de banco de dados**: Se a migração `V202509181000__Add_Financial_Fields_To_Reconciliacao_Mensal.sql` já foi aplicada, será necessário criar uma nova migração Flyway para remover as colunas `total_receitas`, `total_despesas` e `saldo_final` da tabela `reconciliacao_mensal`.
    *   Crie um novo arquivo de migração, por exemplo, `V20250918XXXX__Remove_Financial_Fields_From_Reconciliacao_Mensal.sql`.
    *   Adicione o seguinte conteúdo ao arquivo:
        ```sql
        ALTER TABLE reconciliacao_mensal
        DROP COLUMN total_receitas,
        DROP COLUMN total_despesas,
        DROP COLUMN saldo_final;
        ```
    *   Reinicie a aplicação para que a migração seja aplicada.
3.  **Remover `docker-compose.yml`**: Se o arquivo `docker-compose.yml` foi criado para o SonarQube, você pode removê-lo e, opcionalmente, derrubar os contêineres com `docker-compose down -v` para remover também os volumes de dados.