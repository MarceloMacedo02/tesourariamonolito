# Validação de SocioId em Cobranças

## Roteiro de Design
O objetivo é garantir que o `socio_id` seja corretamente preenchido na tabela `cobrancas` quando uma `Cobranca` estiver logicamente associada a um `Socio`. Foi identificado que a coluna `socio_id` já existe na entidade `Cobranca` e na tabela `cobrancas`. A principal mudança será reforçar as validações no `CobrancaService` para garantir que, se um `socioId` for fornecido nos DTOs de entrada, o `Socio` correspondente seja encontrado e associado, lançando uma exceção clara caso contrário. Além disso, serão adicionados comentários nos DTOs para orientar os desenvolvedores de frontend.

**Componentes Envolvidos:**
*   `Cobranca.java` (Entidade)
*   `CobrancaService.java` (Serviço)
*   `CobrancaDTO.java` (DTO de entrada/saída)
*   `ContaReceberDto.java` (DTO de entrada)
*   `SocioRepository.java` (Repositório para buscar Sócios)

**Arquitetura Proposta:**
As alterações serão focadas na camada de serviço (`CobrancaService`) e nos DTOs, sem modificar o esquema do banco de dados, que já está adequado. As validações serão adicionadas nos métodos de criação/atualização de `Cobranca` para garantir a integridade dos dados.

**Possíveis Desafios:**
*   Garantir que as mensagens de erro sejam claras e úteis.
*   Comunicar a necessidade de fornecer `socioId` nos DTOs para o frontend.

## Requisitos
*   **Funcional:**
    *   Quando um `socioId` é fornecido em um DTO para criação ou atualização de uma `Cobranca`, o sistema deve verificar a existência do `Socio` correspondente.
    *   Se o `Socio` não for encontrado, uma `RegraNegocioException` deve ser lançada com uma mensagem clara.
    *   O `socio_id` deve ser corretamente associado à `Cobranca` persistida.
*   **Não-funcional:**
    *   Manter a integridade referencial com a tabela `socio`.
    *   As mensagens de erro devem ser informativas.
    *   O código deve permanecer legível e seguir as convenções existentes.

## Tarefas
- [x] Reforçar a validação do `socioId` no método `criarContaReceber` em `CobrancaService.java`.
- [x] Reforçar a validação do `socioId` no método `gerarCobrancaOutrasRubricas` em `CobrancaService.java`.
- [x] Reforçar a validação do `socioId` no método `criarPreCobranca` em `CobrancaService.java`.
- [x] Reforçar a validação do `socioId` no método `gerarCobrancaManual` em `CobrancaService.java`.
- [x] Reforçar a validação do `socioId` no método `criarNovaDespesa` em `CobrancaService.java`.
- [x] Adicionar um comentário no `CobrancaDTO.java` para desenvolvedores de frontend.
- [x] Adicionar um comentário no `ContaReceberDto.java` para desenvolvedores de frontend.
- [ ] Criar arquivo de especificação `spec/SocioIdCobrancaValidation.md`.

## Opção de Reversão

Para reverter as alterações feitas, siga os passos abaixo:

1.  **Reverter alterações no `CobrancaService.java`:**
    *   Abra o arquivo `E:\tesouraria\tesouraria\src\main\java\br\com\sigest\tesouraria\service\CobrancaService.java`.
    *   Localize e reverta as seguintes modificações:

        *   **Método `criarContaReceber`:**
            ```diff
            -            Socio socio = socioRepository.findById(dto.getSocioId())
            -                    .orElseThrow(() -> new RegraNegocioException("Sócio com ID " + dto.getSocioId() + " não encontrado."));
            +            Socio socio = socioRepository.findById(dto.getSocioId())
            +                    .orElseThrow(() -> new RegraNegocioException("Sócio não encontrado."));
            ```

        *   **Método `gerarCobrancaOutrasRubricas`:**
            ```diff
            -        Socio socio = socioRepository.findById(dto.getSocioId())
            -                .orElseThrow(() -> new RegraNegocioException("Sócio com ID " + dto.getSocioId() + " não encontrado."));
            +        Socio socio = socioRepository.findById(dto.getSocioId())
            +                .orElseThrow(() -> new RegraNegocioException("Sócio não encontrado."));
            ```

        *   **Método `criarPreCobranca`:**
            ```diff
            -            Socio socio = socioRepository.findById(dto.getSocioId())
            -                    .orElseThrow(() -> new RegraNegocioException("Sócio com ID " + dto.getSocioId() + " não encontrado."));
            +            Socio socio = socioRepository.findById(dto.getSocioId())
            +                    .orElseThrow(() -> new RegraNegocioException("Sócio não encontrado."));
            ```

        *   **Método `gerarCobrancaManual`:**
            ```diff
            -        Socio socio = socioRepository.findById(cobranca.getSocio().getId())
            -                .orElseThrow(() -> new RegraNegocioException("Sócio com ID " + cobranca.getSocio().getId() + " não encontrado."));
            +        Socio socio = socioRepository.findById(cobranca.getSocio().getId())
            +                .orElseThrow(() -> new RegraNegocioException("Sócio não encontrado."));
            ```

        *   **Método `criarNovaDespesa`:**
            ```diff
            -            Socio socio = socioRepository.findById(dto.getFornecedorId())
            -                    .orElseThrow(() -> new RegraNegocioException("Sócio com ID " + dto.getFornecedorId() + " não encontrado."));
            +            Socio socio = socioRepository.findById(dto.getFornecedorId())
            +                    .orElseThrow(() -> new RegraNegocioException("Sócio não encontrado."));
            ```

2.  **Reverter alterações no `CobrancaDTO.java`:**
    *   Abra o arquivo `E:\tesouraria\tesouraria\src\main\java\br\com\sigest\tesouraria\dto\CobrancaDTO.java`.
    *   Localize e reverta a seguinte modificação:

        ```diff
        -    // Para criação/edição: É crucial que o socioId seja fornecido no DTO quando uma cobrança estiver
        -    // logicamente associada a um sócio, para garantir que o vínculo seja estabelecido corretamente no backend.
        -    private Long socioId;
        +    // Para criação/edição
        +    private Long socioId;
        ```

3.  **Reverter alterações no `ContaReceberDto.java`:**
    *   Abra o arquivo `E:\tesouraria\tesouraria\src\main\java\br\com\sigest\tesouraria\dto\ContaReceberDto.java`.
    *   Localize e reverta a seguinte modificação:

        ```diff
        -    // É crucial que o socioId seja fornecido no DTO quando uma conta a receber estiver
        -    // logicamente associada a um sócio, para garantir que o vínculo seja estabelecido corretamente no backend.
        -    private Long socioId;
        +    private Long socioId;
        ```

**Comandos Git para Reversão (se estiver em um repositório Git):**

```bash
git restore E:\tesouraria\tesouraria\src\main\java\br\com\sigest\tesouraria\service\CobrancaService.java
git restore E:\tesouraria\tesouraria\src\main\java\br\com\sigest\tesouraria\dto\CobrancaDTO.java
git restore E:\tesouraria\tesouraria\src\main\java\br\com\sigest\tesouraria\dto\ContaReceberDto.java
```

Se as alterações já foram commitadas, você pode usar `git revert <commit-hash>` para criar um novo commit que desfaz as alterações.

**Backup:**
Não foram feitas alterações no esquema do banco de dados, apenas no código da aplicação. Portanto, não há necessidade de backup do banco de dados para esta reversão.
