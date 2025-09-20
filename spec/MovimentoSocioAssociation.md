# Associação de Socio a Movimento em Quitação de Crédito

## Roteiro de Design
O objetivo é garantir que, ao quitar cobranças em lote a partir de uma transação de crédito, o `socio_id` da `Cobranca` que está sendo quitada seja corretamente associado aos `Movimento`s financeiros gerados. A `detalhes-creditos.html` exibe a `transacao.relacionadoId` quando o `tipoRelacionamento` é `SOCIO`, mas a fonte primária do `Socio` para o `Movimento` deve ser a `Cobranca` em si, que já possui essa associação. A lógica será implementada no método `quitarCobrancasEmLote` do `CobrancaService` para atribuir o `Socio` diretamente da `Cobranca` a cada `Movimento` criado.

**Componentes Envolvidos:**
*   `detalhes-creditos.html` (Frontend - ponto de partida da ação)
*   `CobrancaService.java` (Serviço - onde a lógica de quitação e criação de movimentos reside)
*   `Cobranca.java` (Entidade - contém a associação com `Socio`)
*   `Movimento.java` (Entidade - receberá o `Socio` associado)

**Arquitetura Proposta:**
As alterações serão realizadas na camada de serviço (`CobrancaService`). Dentro do método `quitarCobrancasEmLote`, durante a iteração sobre as `Cobranca`s a serem quitadas, o `Socio` já associado a cada `Cobranca` será diretamente atribuído ao `Movimento` financeiro correspondente antes de ser salvo.

**Possíveis Desafios:**
*   Garantir que o `Socio` da `Cobranca` esteja corretamente preenchido (o que já foi abordado na tarefa anterior de validação de `socioId`).

## Requisitos
*   **Funcional:**
    *   Quando o método `quitarCobrancasEmLote` é executado para quitar `Cobranca`s:
        *   Cada `Movimento` financeiro gerado como resultado da quitação de uma `Cobranca` deve ter o `Socio` associado a essa `Cobranca` (via `movimento.setSocio(cobranca.getSocio())`).
*   **Não-funcional:**
    *   Manter a integridade referencial entre `Movimento` e `Socio`.
    *   O código deve permanecer legível e seguir as convenções existentes.

## Tarefas
- [x] Reverter alterações anteriores no `CobrancaService.java`.
- [x] No método `quitarCobrancasEmLote` do `CobrancaService`, dentro do loop de criação de `Movimento`, atribuir o `Socio` da `Cobranca` ao `Movimento` antes de salvá-lo.
- [x] No método `registrarMovimentoQuitacao` do `CobrancaService`, em todos os pontos onde um `Movimento` de `ENTRADA` é criado, atribuir `cobranca.getSocio()` ao `Movimento`.
- [ ] Atualizar arquivo de especificação `spec/MovimentoSocioAssociation.md`.

## Opção de Reversão

Para reverter as alterações feitas no `CobrancaService.java`, siga os passos abaixo:

1.  **Abra o arquivo:** `E:\tesouraria\tesouraria\src\main\java\br\com\sigest\tesouraria\service\CobrancaService.java`

2.  **Reverta as modificações (atribuição do Socio ao Movimento) em `quitarCobrancasEmLote`:**
    *   Localize o bloco de código dentro do loop `for (Cobranca cobranca : cobrancasToSettle)`:
        ```java
                // Atribuir o Socio da Cobrança ao Movimento
                movimento.setSocio(cobranca.getSocio());
        ```
    *   Remova este bloco de código.

3.  **Reverta as modificações (atribuição do Socio ao Movimento) em `registrarMovimentoQuitacao`:**
    *   Localize e remova a linha `movimento.setSocio(cobranca.getSocio()); // Associar o sócio da cobrança ao movimento` em cada um dos quatro blocos onde foi adicionada.

**Comandos Git para Reversão (se estiver em um repositório Git):**

```bash
git restore E:\tesouraria\tesouraria\src\main\java\br\com\sigest\tesouraria\service\CobrancaService.java
```

Se as alterações já foram commitadas, você pode usar `git revert <commit-hash>` para criar um novo commit que desfaz as alterações.

**Backup:**
Não foram feitas alterações no esquema do banco de dados, apenas no código da aplicação. Portanto, não há necessidade de backup do banco de dados para esta reversão.
