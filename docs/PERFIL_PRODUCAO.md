# Perfil de Produção para a Aplicação Tesouraria

## Visão Geral

Este documento descreve a configuração do perfil de produção para a aplicação Tesouraria quando implantada no Kubernetes. O perfil de produção é otimizado para desempenho, segurança e confiabilidade em um ambiente de produção.

## Configurações Específicas do Perfil de Produção

### Configurações do Banco de Dados

```properties
# Conexão com o PostgreSQL em um serviço Kubernetes dedicado
spring.datasource.url=jdbc:postgresql://postgresql-service:5432/udv-tesouraria
spring.datasource.username=tesourario
spring.datasource.password=masterkey

# Modo de validação do schema (não altera a estrutura do banco)
spring.jpa.hibernate.ddl-auto=validate
```

### Configurações de Cache

```properties
# Uso de Redis para cache distribuído em produção
spring.cache.type=redis
spring.redis.host=redis-service
spring.redis.port=6379
```

### Configurações de Segurança

```properties
# Desativa informações detalhadas de erro
server.error.include-stacktrace=never
server.error.include-message=never
```

### Configurações de Logging

```properties
# Nível de log apropriado para produção
logging.level.root=INFO
```

## Kubernetes Deployment

O deployment do Kubernetes foi configurado para usar o perfil de produção:

```yaml
env:
- name: SPRING_PROFILES_ACTIVE
  value: "prod"
```

### Recursos Alocados

```yaml
resources:
  requests:
    memory: "512Mi"
    cpu: "250m"
  limits:
    memory: "1Gi"
    cpu: "500m"
```

## Serviços de Apoio

### PostgreSQL

Um serviço dedicado para o PostgreSQL foi criado:
- Nome do serviço: `postgresql-service`
- Porta: 5432

### Redis

Um deployment e serviço para Redis foram criados para cache distribuído:
- Nome do serviço: `redis-service`
- Porta: 6379

## Como Ativar o Perfil de Produção

O perfil de produção é ativado automaticamente quando o deployment do Kubernetes é aplicado, pois a variável de ambiente `SPRING_PROFILES_ACTIVE` está definida como "prod".

Para aplicar a configuração:

```bash
kubectl apply -f k8s-deployment.yaml
kubectl apply -f k8s-postgresql-service.yaml
kubectl apply -f k8s-redis.yaml
```

## Verificação

Para verificar se o perfil de produção está ativo:

1. Verifique as variáveis de ambiente do pod:
   ```bash
   kubectl exec -it <nome-do-pod> -- env | grep SPRING_PROFILES_ACTIVE
   ```

2. Verifique os logs da aplicação:
   ```bash
   kubectl logs <nome-do-pod> | grep "The following profiles are active"
   ```

3. Verifique se as configurações específicas de produção estão sendo aplicadas:
   ```bash
   kubectl exec -it <nome-do-pod> -- cat /app/application-prod.properties
   ```

## Personalização

Você pode personalizar ainda mais o perfil de produção modificando o arquivo `application-prod.properties` ou adicionando configurações específicas na classe `ProductionConfig.java`.