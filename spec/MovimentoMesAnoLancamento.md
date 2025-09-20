# Adição de Mês e Ano de Lançamento à Tabela Movimentos

## Roteiro de Design
O objetivo é adicionar as colunas `mes_lancamento` e `ano_lancamento` à tabela `movimentos` e garantir que estas sejam preenchidas automaticamente com o mês e ano da `dataHora` do movimento para cada novo registro. Isso permitirá uma melhor organização e filtragem dos dados financeiros por período.

**Componentes Envolvidos:**
*   `Movimento.java` (Entidade - para adicionar os novos campos)
*   Scripts SQL de migração (para alterar o esquema do banco de dados)
*   `CobrancaService.java` (Serviço - onde os objetos `Movimento` são criados e persistidos)

**Arquitetura Proposta:**
As alterações envolverão a modificação da entidade `Movimento` para incluir os novos campos, a criação de um script SQL para atualizar o esquema do banco de dados e a adaptação da lógica de negócio no `CobrancaService` para popular esses campos no momento da criação de um `Movimento`.

**Possíveis Desafios:**
*   Garantir que todos os pontos de criação de `Movimento` sejam identificados e atualizados.
*   Compatibilidade com dados existentes (se houver necessidade de preencher dados retroativamente, isso seria uma tarefa separada).

## Requisitos
*   **Funcional:**
    *   A tabela `movimentos` deve conter as colunas `mes_lancamento` (INT) e `ano_lancamento` (INT).
    *   Para cada novo `Movimento` criado, o campo `mesLancamento` deve ser preenchido com o mês da `dataHora` do movimento.
    *   Para cada novo `Movimento` criado, o campo `anoLancamento` deve ser preenchido com o ano da `dataHora` do movimento.
*   **Não-funcional:**
    *   Manter a integridade dos dados.
    *   O código deve seguir as convenções do projeto.

## Tarefas
- [x] Modificar `Movimento.java`: Adicionar `mesLancamento` e `anoLancamento` à entidade.
- [x] Criar script SQL de migração: Adicionar as colunas `mes_lancamento` e `ano_lancamento` à tabela `movimentos` (`db/add_mes_ano_lancamento_to_movimentos.sql`).
- [x] Atualizar lógica de criação de `Movimento` em `CobrancaService.java` (métodos `registrarMovimentoQuitacao` e `quitarCobrancasEmLote`) para preencher `mesLancamento` e `anoLancamento`.
- [ ] Criar arquivo de especificação `spec/MovimentoMesAnoLancamento.md`.

## Opção de Reversão

Para reverter as alterações feitas, siga os passos abaixo:

1.  **Reverter alterações no `Movimento.java`:**
    *   Abra o arquivo `E:\tesouraria\tesouraria\src\main\java\br\com\sigest\tesouraria\domain\entity\Movimento.java`.
    *   Remova as linhas:
        ```java
            @Column(name = "mes_lancamento")
            private Integer mesLancamento;

            @Column(name = "ano_lancamento")
            private Integer anoLancamento;
        ```

2.  **Reverter script SQL de migração:**
    *   Exclua o arquivo `E:\tesouraria\tesouraria\db\add_mes_ano_lancamento_to_movimentos.sql`.
    *   Se o script já foi executado no banco de dados, você precisará reverter manualmente as colunas (ex: `ALTER TABLE movimentos DROP COLUMN mes_lancamento, DROP COLUMN ano_lancamento;`).

3.  **Reverter alterações no `CobrancaService.java`:**
    *   Abra o arquivo `E:\tesouraria\tesouraria\src\main\java\br\com\sigest\tesouraria\service\CobrancaService.java`.
    *   Localize e remova as linhas `movimento.setMesLancamento(...)` e `movimento.setAnoLancamento(...)` em todos os 5 locais onde foram adicionadas (`registrarMovimentoQuitacao` - 4 ocorrências, e `quitarCobrancasEmLote` - 1 ocorrência).

**Comandos Git para Reversão (se estiver em um repositório Git):**

```bash
git restore E:\tesouraria\tesouraria\src\main\java\br\com\sigest\tesouraria\domain\entity\Movimento.java
git restore E:\tesouraria\tesouraria\src\main\java\br\com\sigest\tesouraria\service\CobrancaService.java
git rm E:\tesouraria\tesouraria\db\add_mes_ano_lancamento_to_movimentos.sql
```

Se as alterações já foram commitadas, você pode usar `git revert <commit-hash>` para criar um novo commit que desfaz as alterações.

**Backup:**
Para reverter completamente, é importante garantir que o banco de dados também seja revertido se o script SQL já tiver sido aplicado.

```