# RELATÓRIO DE BUGS E PROBLEMAS IDENTIFICADOS

## 1. PROBLEMAS DE SEGURANÇA

### 1.1 Credenciais em Texto Simples
**Arquivos afetados:**
- `src/main/resources/application.properties`
- `src/main/resources/application-local.properties`
- `src/main/resources/application-prod.properties`

**Problema:**
As credenciais do banco de dados estão armazenadas em texto simples nos arquivos de configuração:
```
spring.datasource.username=tesourario
spring.datasource.password=masterkey
```

**Solução recomendada:**
- Utilizar Secrets do Kubernetes para gerenciar credenciais
- Implementar Spring Cloud Config Server com criptografia
- Utilizar variáveis de ambiente para credenciais sensíveis

### 1.2 Exposição de Informações de Erro
**Arquivo afetado:**
- `src/main/resources/application-local.properties`

**Problema:**
Configurações perigosas para ambiente de produção:
```
server.error.include-exception=true
server.error.include-message=always
```

**Solução recomendada:**
- Remover estas configurações do perfil de produção
- Manter apenas no perfil de desenvolvimento

## 2. PROBLEMAS DE CONFIGURAÇÃO

### 2.1 Inconsistência nos Perfis
**Problema:**
O perfil de produção (`application-prod.properties`) define:
```
spring.cache.type=redis
```

Mas o arquivo de configuração do Redis (`application-tracing.properties`) define:
```
spring.redis.host=redis-service
```

**Solução recomendada:**
- Mover as configurações do Redis para o arquivo de perfil correto
- Remover o arquivo `application-tracing.properties` ou integrá-lo corretamente

### 2.2 Configuração do Quartz Scheduler
**Arquivo afetado:**
- `src/main/resources/application-local.properties`

**Problema:**
```
spring.quartz.jdbc.initialize-schema=always
# CUIDADO: Não use em produção!
```

**Solução recomendada:**
- Configurar corretamente para ambientes diferentes
- Remover do perfil de produção

## 3. PROBLEMAS DE CÓDIGO

### 3.1 Potencial NullPointerException
**Arquivo afetado:**
- `src/main/java/br/com/sigest/tesouraria/controller/DashboardController.java`

**Problema:**
```java
String role = userDetails.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .findFirst()
        .orElse("");
```

Se `userDetails.getAuthorities()` for null ou vazio, a role será uma string vazia, o que pode causar problemas na verificação de autorização.

**Solução recomendada:**
```java
if (userDetails != null && userDetails.getAuthorities() != null) {
    String role = userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .findFirst()
            .orElse("ROLE_ANONYMOUS");
    // continuar com a lógica
} else {
    return "redirect:/login";
}
```

### 3.2 Inconsistência nos Nomes de Perfis
**Arquivo afetado:**
- `k8s-deployment.yaml`

**Problema:**
```yaml
env:
- name: SPRING_PROFILES_ACTIVE
  value: "prod"
```

Mas nos scripts de verificação (`verify-production-profile.bat`), procura-se por "local" em alguns casos.

**Solução recomendada:**
- Padronizar o uso do perfil "prod" em todos os lugares
- Corrigir os scripts de verificação

## 4. PROBLEMAS DE DEPENDÊNCIAS

### 4.1 Dependências de Monitoramento Não Configuradas
**Arquivo afetado:**
- `pom.xml`

**Problema:**
As dependências do OpenTelemetry foram adicionadas, mas:
1. Não há configuração adequada no código para utilizá-las
2. As propriedades estão em um arquivo separado (`application-tracing.properties`)

**Solução recomendada:**
- Configurar adequadamente o OpenTelemetry no código
- Integrar as propriedades com os perfis corretos

### 4.2 Versões de Dependências
**Arquivo afetado:**
- `pom.xml`

**Problema:**
Versões fixas de dependências podem causar problemas de compatibilidade:
```xml
<dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-api</artifactId>
    <version>1.16.0</version>
</dependency>
```

**Solução recomendada:**
- Utilizar gerenciamento de versões do Spring Boot
- Remover versões explícitas quando possível

## 5. PROBLEMAS DE DOCKER E KUBERNETES

### 5.1 Imagem Docker não Versionada
**Arquivo afetado:**
- `k8s-deployment.yaml`

**Problema:**
```yaml
image: tesouraria:latest
```

Usar `latest` em produção é uma prática perigosa que pode causar instabilidade.

**Solução recomendada:**
- Utilizar tags de versão específicas
- Implementar processo de build com versionamento

### 5.2 Recursos de Container
**Arquivo afetado:**
- `k8s-deployment.yaml`

**Problema:**
Limites de memória podem ser muito restritivos para uma aplicação Spring Boot:
```yaml
resources:
  requests:
    memory: "512Mi"
    cpu: "250m"
  limits:
    memory: "1Gi"
    cpu: "500m"
```

**Solução recomendada:**
- Ajustar os limites com base no uso real da aplicação
- Monitorar o consumo de recursos após o deploy

## 6. PROBLEMAS DE BANCO DE DADOS

### 6.1 Estratégia de DDL
**Arquivo afetado:**
- `src/main/resources/application-local.properties`

**Problema:**
```
spring.jpa.hibernate.ddl-auto=update
```

Em ambientes de produção, `update` pode causar problemas inesperados.

**Solução recomendada:**
- Utilizar `validate` em produção
- Implementar migrações controladas com Flyway ou Liquibase

### 6.2 Dialeto do Banco de Dados
**Problema:**
Configuração duplicada do dialeto:
```
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

**Solução recomendada:**
- Manter apenas uma configuração

## 7. PROBLEMAS DE ARQUITETURA

### 7.1 Valores Fixos em Código
**Arquivo afetado:**
- `src/main/java/br/com/sigest/tesouraria/service/DashboardService.java`

**Problema:**
```java
BigDecimal resultadoDoMes = BigDecimal.valueOf(40000); // Valor de exemplo
```

Valores fixos em código são difíceis de manter e configurar.

**Solução recomendada:**
- Mover para propriedades de configuração
- Implementar cálculo real ou serviço de configuração

## 8. RECOMENDAÇÕES GERAIS

### 8.1 Testes
- Implementar testes unitários e de integração
- Adicionar cobertura de testes para os controllers e services

### 8.2 Monitoramento
- Configurar adequadamente o Actuator
- Adicionar health checks específicos para as dependências

### 8.3 Documentação
- Atualizar a documentação para refletir a estrutura atual
- Adicionar documentação de API (Swagger/OpenAPI)

### 8.4 CI/CD
- Implementar pipeline de CI/CD
- Adicionar etapas de teste automatizado
- Configurar versionamento automático