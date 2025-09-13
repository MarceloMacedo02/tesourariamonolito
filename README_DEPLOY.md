# PIPELINE DE IMPLANTAÇÃO COMPLETA

## Visão Geral

Este projeto inclui scripts automatizados para implantar completamente a aplicação Tesouraria no Kubernetes com todas as suas dependências e ferramentas de monitoramento.

## Scripts Disponíveis

### Windows
- `deploy-pipeline.bat` - Script principal de implantação para Windows

### Linux/Mac
- `deploy-pipeline.sh` - Script principal de implantação para Linux/Mac

## O que os scripts fazem

1. **Verificam pré-requisitos:**
   - Docker instalado
   - Kubernetes habilitado
   - kubectl configurado
   - Arquivos de configuração presentes

2. **Constroem a imagem Docker da aplicação:**
   - Utilizam um build multi-stage otimizado
   - Criam uma imagem leve para produção

3. **Implantam serviços de apoio:**
   - PostgreSQL (serviço dedicado)
   - Redis (para cache distribuído)

4. **Implantam a aplicação:**
   - Configurada com perfil de produção
   - Recursos alocados adequadamente
   - Variáveis de ambiente configuradas

5. **Implantam ferramentas de monitoramento:**
   - Prometheus (coleta de métricas)
   - Grafana (visualização de métricas)
   - Jaeger (rastreamento distribuído)

6. **Verificam o status da implantação:**
   - Aguardam todos os pods ficarem prontos
   - Exibem resumo da implantação
   - Fornecem informações de acesso

## Como usar

### No Windows

```cmd
deploy-pipeline.bat
```

### No Linux/Mac

```bash
chmod +x deploy-pipeline.sh
./deploy-pipeline.sh
```

## Acessos após a implantação

Após a execução bem-sucedida do pipeline, as seguintes aplicações estarão disponíveis:

- **Aplicação Tesouraria:** http://localhost:8080
- **Prometheus:** http://localhost:9090
- **Grafana:** http://localhost:3000 (usuário: admin, senha: admin)
- **Jaeger:** http://localhost:16686

## Comandos úteis após a implantação

### Verificar logs da aplicação:
```bash
kubectl logs deployment/tesouraria-deployment
```

### Verificar status dos pods:
```bash
kubectl get pods
```

### Verificar status dos serviços:
```bash
kubectl get services
```

### Verificar status dos deployments:
```bash
kubectl get deployments
```

## Solução de problemas

### Se a implantação falhar:

1. Verifique se o Docker Desktop está em execução
2. Verifique se o Kubernetes está habilitado no Docker Desktop
3. Verifique se todos os arquivos YAML estão presentes no diretório
4. Verifique as permissões do diretório

### Se os pods não ficarem prontos:

1. Verifique os logs dos pods com problemas:
   ```bash
   kubectl logs <nome-do-pod>
   ```

2. Descreva o pod para mais detalhes:
   ```bash
   kubectl describe pod <nome-do-pod>
   ```

### Se precisar reiniciar a implantação:

1. Delete os recursos existentes:
   ```bash
   kubectl delete -f k8s-deployment.yaml
   kubectl delete -f k8s-service.yaml
   kubectl delete -f k8s-postgresql-service.yaml
   kubectl delete -f k8s-redis.yaml
   kubectl delete -f k8s-prometheus.yaml
   kubectl delete -f k8s-grafana.yaml
   kubectl delete -f k8s-jaeger.yaml
   ```

2. Execute o pipeline novamente

## Personalização

Você pode personalizar a implantação modificando os seguintes arquivos:

- `k8s-deployment.yaml` - Configurações do deployment
- `k8s-service.yaml` - Configurações do serviço da aplicação
- `application-prod.properties` - Configurações da aplicação para produção
- Arquivos YAML das ferramentas de monitoramento para ajustes específicos

## Requisitos

- Docker Desktop com Kubernetes habilitado
- kubectl instalado e configurado
- Acesso ao terminal/shell
- Conexão com a internet para baixar imagens do Docker