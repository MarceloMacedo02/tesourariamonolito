# Guia Completo de Monitoramento e Rastreamento para a Aplicação Tesouraria

## Índice
1. [Introdução](#introdução)
2. [Arquitetura de Monitoramento](#arquitetura-de-monitoramento)
3. [Implantação](#implantação)
4. [Configuração](#configuração)
5. [Uso das Ferramentas](#uso-das-ferramentas)
6. [Visualização de Métricas](#visualização-de-métricas)
7. [Rastreamento Distribuído](#rastreamento-distribuído)
8. [Solução de Problemas](#solução-de-problemas)

## Introdução

Este guia fornece instruções completas para configurar, implantar e usar as ferramentas de monitoramento e rastreamento para a aplicação Tesouraria. O sistema utiliza Prometheus para coleta de métricas, Grafana para visualização e Jaeger para rastreamento distribuído.

## Arquitetura de Monitoramento

```
┌─────────────────┐    ┌──────────────┐    ┌──────────────┐
│   Tesouraria    │───▶│   Jaeger     │───▶│   Interface  │
│   Application   │    │ (Tracing)    │    │   Web do     │
└─────────────────┘    └──────────────┘    │   Jaeger     │
        │                                   └──────────────┘
        │                                            ▲
        ▼                                            │
┌─────────────────┐    ┌──────────────┐             │
│   Actuator      │───▶│  Prometheus  │─────────────┘
│  (Spring Boot)  │    │ (Métricas)   │
└─────────────────┘    └──────────────┘
                                │
                                ▼
                       ┌──────────────┐
                       │   Grafana    │
                       │ (Visualização│
                       │   de Dados)  │
                       └──────────────┘
```

## Implantação

### Pré-requisitos
- Docker Desktop com Kubernetes habilitado
- kubectl instalado e configurado
- Aplicação Tesouraria implantada no Kubernetes

### Passo a Passo

1. **Implantar Prometheus:**
   ```bash
   kubectl apply -f k8s-prometheus.yaml
   ```

2. **Implantar Grafana:**
   ```bash
   kubectl apply -f k8s-grafana.yaml
   ```

3. **Implantar Jaeger:**
   ```bash
   kubectl apply -f k8s-jaeger.yaml
   ```

4. **Reimplantar a aplicação com agentes de monitoramento:**
   ```bash
   # Reconstruir a aplicação com as novas dependências
   docker build -t tesouraria .
   
   # Reaplicar o deployment
   kubectl apply -f k8s-deployment.yaml
   ```

## Configuração

### Prometheus
O Prometheus já está configurado para coletar métricas do Actuator do Spring Boot através do arquivo `prometheus.yml`.

### Grafana
O Grafana é implantado com credenciais padrão:
- Usuário: admin
- Senha: admin

### Jaeger
O Jaeger é implantado em modo "all-in-one" para ambientes de desenvolvimento e teste.

## Uso das Ferramentas

### Prometheus
Acesse o Prometheus em `http://localhost:9090`. Você pode:
- Consultar métricas usando PromQL
- Verificar alvos de coleta
- Visualizar regras de alerta

### Grafana
Acesse o Grafana em `http://localhost:3000`. Após o primeiro login, você precisará:
1. Alterar a senha padrão
2. Configurar Prometheus como fonte de dados
3. Importar ou criar dashboards

### Jaeger
Acesse o Jaeger em `http://localhost:16686`. Você pode:
- Visualizar traces da aplicação
- Analisar latências
- Identificar gargalos de desempenho

## Visualização de Métricas

### Configuração da Fonte de Dados no Grafana

1. Acesse `http://localhost:3000` e faça login
2. No menu lateral, clique em "Configuration" (ícone de engrenagem)
3. Clique em "Data Sources"
4. Clique em "Add data source"
5. Selecione "Prometheus"
6. Configure a URL como `http://prometheus-service:9090`
7. Clique em "Save & Test"

### Dashboards Recomendados

Você pode importar dashboards pré-construídos para Spring Boot:
1. No menu lateral do Grafana, clique em "Create" (ícone +)
2. Selecione "Import"
3. Insira o ID 12900 para um dashboard de Spring Boot completo
4. Selecione sua fonte de dados Prometheus
5. Clique em "Import"

## Rastreamento Distribuído

### Visualizando Traces no Jaeger

1. Acesse `http://localhost:16686`
2. Na página principal, selecione "tesouraria" no dropdown de serviços
3. Clique em "Find Traces" para ver os traces recentes
4. Clique em um trace específico para ver detalhes

### Instrumentação Adicional

Para adicionar mais instrumentação ao seu código, você pode usar o Tracer injetado:

```java
@RestController
public class ExampleController {
    
    @Autowired
    private Tracer tracer;
    
    @GetMapping("/example")
    public String example() {
        Span span = tracer.spanBuilder("example-operation").startSpan();
        try (Scope scope = span.makeCurrent()) {
            // Sua lógica de negócio aqui
            return "Exemplo com tracing";
        } finally {
            span.end();
        }
    }
}
```

## Solução de Problemas

### Problemas Comuns com Prometheus

1. **Métricas não aparecem:**
   - Verifique se o serviço da aplicação está acessível
   - Confirme se o endpoint `/actuator/prometheus` está respondendo
   - Verifique os logs do Prometheus: `kubectl logs deployment/prometheus-deployment`

2. **Erro de conexão com o alvo:**
   - Verifique se o nome do serviço está correto na configuração do Prometheus
   - Confirme se a porta está correta (8080 para a aplicação)

### Problemas Comuns com Grafana

1. **Não é possível adicionar fonte de dados:**
   - Verifique se o serviço do Prometheus está em execução
   - Confirme se a URL da fonte de dados está correta

2. **Dashboards não carregam dados:**
   - Verifique se a fonte de dados está configurada corretamente
   - Confirme se as métricas existem no Prometheus

### Problemas Comuns com Jaeger

1. **Nenhum trace aparece:**
   - Verifique se a aplicação está enviando traces
   - Confirme se as variáveis de ambiente estão configuradas corretamente
   - Verifique os logs da aplicação: `kubectl logs deployment/tesouraria-deployment`

2. **Erro de conexão com o Jaeger:**
   - Verifique se o serviço do Jaeger está em execução
   - Confirme se a porta gRPC (14250) está acessível

### Comandos Úteis para Depuração

```bash
# Ver todos os pods
kubectl get pods

# Ver todos os serviços
kubectl get services

# Ver logs de um pod específico
kubectl logs <nome-do-pod>

# Descrever um deployment
kubectl describe deployment <nome-do-deployment>

# Encaminhar porta local para um serviço
kubectl port-forward service/<nome-do-serviço> <porta-local>:<porta-serviço>
```

## Conclusão

Com essas ferramentas configuradas, você terá uma solução completa de observabilidade para sua aplicação Tesouraria. Você poderá monitorar métricas de desempenho, visualizar dados em dashboards e rastrear requisições através do sistema para identificar problemas e otimizar o desempenho.