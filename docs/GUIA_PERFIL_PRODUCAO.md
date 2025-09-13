# Guia Completo do Perfil de Produção

## Índice
1. [Introdução](#introdução)
2. [Configuração do Perfil de Produção](#configuração-do-perfil-de-produção)
3. [Implantação no Kubernetes](#implantação-no-kubernetes)
4. [Serviços de Apoio](#serviços-de-apoio)
5. [Verificação](#verificação)
6. [Personalização](#personalização)
7. [Melhores Práticas](#melhores-práticas)

## Introdução

O perfil de produção é uma configuração especializada da aplicação Tesouraria otimizada para ambientes de produção. Este perfil inclui configurações de segurança, performance, e confiabilidade adequadas para um ambiente de produção real.

## Configuração do Perfil de Produção

### Arquivo de Propriedades

O perfil de produção é definido no arquivo `application-prod.properties`, que inclui:

1. **Configurações de Banco de Dados:**
   - Conexão com PostgreSQL através de serviço Kubernetes
   - Validação de schema (não altera estrutura)
   - Desativação de logs SQL

2. **Configurações de Cache:**
   - Uso de Redis para cache distribuído
   - Ativação de cache em produção

3. **Configurações de Segurança:**
   - Ocultação de detalhes de erro
   - Configurações adequadas para ambiente de produção

4. **Configurações de Performance:**
   - Otimizações para produção
   - Níveis de log apropriados

### Classes de Configuração

- `ProductionConfig.java` - Configurações específicas para produção
- `DatabaseConfig.java` - Configurações específicas do banco de dados

## Implantação no Kubernetes

### Ativação do Perfil

O perfil de produção é ativado através da variável de ambiente no deployment do Kubernetes:

```yaml
env:
- name: SPRING_PROFILES_ACTIVE
  value: "prod"
```

### Recursos do Container

O deployment define recursos específicos para produção:

```yaml
resources:
  requests:
    memory: "512Mi"
    cpu: "250m"
  limits:
    memory: "1Gi"
    cpu: "500m"
```

### Java Options

Opções específicas do Java para otimização em produção:

```yaml
env:
- name: JAVA_OPTS
  value: "-Xmx512m -Xms256m"
```

## Serviços de Apoio

### PostgreSQL

Serviço dedicado para o banco de dados PostgreSQL:
- Nome do serviço: `postgresql-service`
- Porta: 5432

### Redis

Deployment e serviço para cache distribuído:
- Nome do serviço: `redis-service`
- Porta: 6379

Para implantar os serviços de apoio:

```bash
kubectl apply -f k8s-postgresql-service.yaml
kubectl apply -f k8s-redis.yaml
```

## Verificação

### Script de Verificação

Use o script `verify-production-profile.bat` para verificar se o perfil de produção está corretamente configurado:

```bash
verify-production-profile.bat
```

### Verificação Manual

1. **Verificar variáveis de ambiente:**
   ```bash
   kubectl get deployment tesouraria-deployment -o yaml | grep SPRING_PROFILES_ACTIVE
   ```

2. **Verificar perfil ativo nos logs:**
   ```bash
   kubectl logs deployment/tesouraria-deployment | grep "The following profiles are active"
   ```

3. **Verificar variáveis de ambiente no pod:**
   ```bash
   kubectl exec -it <nome-do-pod> -- env | grep SPRING_PROFILES_ACTIVE
   ```

## Personalização

### Modificando Configurações

Para personalizar o perfil de produção:

1. Edite o arquivo `application-prod.properties`
2. Modifique as configurações conforme necessário
3. Reconstrua a imagem Docker
4. Reaplique o deployment

### Adicionando Configurações Específicas

Você pode adicionar configurações específicas na classe `ProductionConfig.java`:

```java
@Configuration
@Profile("prod")
public class ProductionConfig {
    
    @Bean
    public SomeProductionBean someProductionBean() {
        // Configurações específicas de produção
        return new SomeProductionBean();
    }
}
```

## Melhores Práticas

### Segurança

1. **Use Secrets do Kubernetes** para credenciais sensíveis
2. **Configure TLS/SSL** para todas as comunicações
3. **Implemente Network Policies** para controle de tráfego
4. **Use RBAC** para controle de acesso

### Performance

1. **Defina recursos adequados** (requests e limits)
2. **Configure HPA** para autoscaling
3. **Use Persistent Volumes** para dados persistentes
4. **Implemente estratégias de deployment** adequadas

### Monitoramento

1. **Configure alertas** para condições críticas
2. **Implemente health checks** detalhados
3. **Use métricas** para tomada de decisão
4. **Implemente logging** estruturado

### Backup e Recuperação

1. **Automatize backups** do banco de dados
2. **Teste procedimentos de recuperação**
3. **Implemente estratégias de rollback**
4. **Documente procedimentos de recuperação**

### Compliance

1. **Verifique requisitos regulatórios**
2. **Implemente auditoria de acesso**
3. **Configure retenção de dados**
4. **Verifique requisitos de privacidade**

## Troubleshooting

### Problemas Comuns

1. **Perfil não ativado:**
   - Verifique se a variável de ambiente está correta
   - Confirme se o deployment foi atualizado
   - Verifique os logs da aplicação

2. **Conexão com banco de dados:**
   - Verifique se o serviço PostgreSQL está em execução
   - Confirme as credenciais do banco de dados
   - Verifique as políticas de rede

3. **Cache Redis não funcionando:**
   - Verifique se o serviço Redis está em execução
   - Confirme as configurações de conexão
   - Verifique se as dependências estão corretas

### Comandos Úteis

```bash
# Ver todos os recursos
kubectl get all

# Ver logs detalhados
kubectl logs -f deployment/tesouraria-deployment

# Descrever deployment
kubectl describe deployment tesouraria-deployment

# Executar comando no pod
kubectl exec -it <nome-do-pod> -- /bin/sh

# Ver variáveis de ambiente
kubectl exec -it <nome-do-pod> -- env
```

## Conclusão

O perfil de produção configurado fornece uma base sólida para implantar a aplicação Tesouraria em um ambiente de produção real. Com as configurações adequadas de segurança, performance e confiabilidade, a aplicação está pronta para atender às demandas de um ambiente de produção.