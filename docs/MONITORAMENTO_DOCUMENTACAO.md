# Documentação de Monitoramento e Rastreamento

## Visão Geral

Esta documentação explica como configurar e usar as ferramentas de monitoramento e rastreamento para a aplicação Tesouraria no Kubernetes. As ferramentas incluem:

1. Prometheus para coleta de métricas
2. Grafana para visualização de métricas
3. Jaeger para rastreamento distribuído

## Pré-requisitos

- Docker Desktop com Kubernetes habilitado
- kubectl instalado e configurado
- Aplicação Tesouraria já implantada no Kubernetes

## Implantação das Ferramentas de Monitoramento

### 1. Prometheus

Prometheus é uma ferramenta de coleta de métricas. Para implantá-lo:

```bash
kubectl apply -f k8s-prometheus.yaml
```

O Prometheus será configurado para coletar métricas do Actuator do Spring Boot na aplicação Tesouraria.

### 2. Grafana

Grafana é uma ferramenta de visualização de métricas. Para implantá-lo:

```bash
kubectl apply -f k8s-grafana.yaml
```

Após a implantação, você pode acessar o Grafana em `http://localhost:3000` com as credenciais:
- Usuário: admin
- Senha: admin

### 3. Jaeger

Jaeger é uma ferramenta de rastreamento distribuído. Para implantá-lo:

```bash
kubectl apply -f k8s-jaeger.yaml
```

Após a implantação, você pode acessar a interface do Jaeger em `http://localhost:16686`.

## Configuração da Aplicação

A aplicação Tesouraria já foi configurada com os agentes necessários para:

1. Expor métricas no formato Prometheus através do Actuator do Spring Boot
2. Enviar traces para o Jaeger

As dependências necessárias foram adicionadas ao arquivo `pom.xml`:
- `micrometer-registry-prometheus` para métricas
- `opentelemetry-spring-boot-starter` para rastreamento

## Acesso às Ferramentas

Após implantar todas as ferramentas, você pode acessá-las usando:

1. **Prometheus**: `http://localhost:9090`
2. **Grafana**: `http://localhost:3000`
3. **Jaeger**: `http://localhost:16686`

## Configuração do Grafana

Para configurar o Grafana para usar o Prometheus como fonte de dados:

1. Acesse o Grafana em `http://localhost:3000`
2. Faça login com usuário "admin" e senha "admin"
3. Altere a senha quando solicitado
4. Clique em "Data Sources" e depois "Add data source"
5. Selecione "Prometheus"
6. Configure a URL como `http://prometheus-service:9090`
7. Clique em "Save & Test"

## Verificação

Para verificar se tudo está funcionando corretamente:

1. Verifique se os pods estão em execução:
   ```bash
   kubectl get pods
   ```

2. Verifique se os serviços estão em execução:
   ```bash
   kubectl get services
   ```

3. Verifique se o endpoint de métricas da aplicação está acessível:
   ```bash
   kubectl port-forward service/tesouraria-service 8080:8080
   ```
   Em seguida, acesse `http://localhost:8080/actuator/prometheus`

## Solução de Problemas

### Se as métricas não aparecerem no Prometheus:

1. Verifique se o endpoint do Actuator está acessível
2. Verifique se a configuração do Prometheus está correta
3. Verifique os logs do Prometheus:
   ```bash
   kubectl logs deployment/prometheus-deployment
   ```

### Se os traces não aparecerem no Jaeger:

1. Verifique se a aplicação está enviando traces corretamente
2. Verifique se o serviço do Jaeger está acessível
3. Verifique os logs da aplicação:
   ```bash
   kubectl logs deployment/tesouraria-deployment
   ```

## Personalização

Você pode personalizar as configurações modificando os arquivos YAML correspondentes:
- `prometheus.yml` para configurações do Prometheus
- `k8s-prometheus.yaml` para implantação do Prometheus
- `k8s-grafana.yaml` para implantação do Grafana
- `k8s-jaeger.yaml` para implantação do Jaeger