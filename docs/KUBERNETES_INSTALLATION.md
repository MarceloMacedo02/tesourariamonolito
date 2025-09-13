# Instalação e Configuração Completa do Kubernetes com Monitoramento e Perfil de Produção

## Índice
1. [Instalação do Docker Desktop com Kubernetes](#instalação-do-docker-desktop-com-kubernetes)
2. [Verificação da Instalação do Kubernetes](#verificação-da-instalação-do-kubernetes)
3. [Implantação da Aplicação com Perfil de Produção](#implantação-da-aplicação-com-perfil-de-produção)
4. [Implantação das Ferramentas de Monitoramento](#implantação-das-ferramentas-de-monitoramento)
5. [Verificação da Implantação Completa](#verificação-da-implantação-completa)
6. [Acesso às Aplicações](#acesso-às-aplicações)

## Instalação do Docker Desktop com Kubernetes

1. Faça o download do Docker Desktop para Windows em: https://www.docker.com/products/docker-desktop/
2. Execute o instalador e siga o assistente de instalação
3. Durante a instalação, certifique-se de selecionar:
   - Enable Hyper-V Windows Features
   - Install required Windows components for WSL 2
   - Add shortcut to desktop (opcional)

## Habilitar Kubernetes no Docker Desktop

1. Após a instalação do Docker Desktop, clique no ícone do Docker na bandeja do sistema
2. Selecione "Settings"
3. Na barra lateral esquerda, clique em "Kubernetes"
4. Marque a caixa "Enable Kubernetes"
5. Clique em "Apply & Restart"
6. Aguarde o Docker Desktop reiniciar e inicializar o Kubernetes (isso pode levar vários minutos)

## Verificação da Instalação do Kubernetes

Abra um prompt de comando ou PowerShell e execute:
```bash
kubectl version
kubectl cluster-info
kubectl get nodes
```

Se esses comandos retornarem informações sem erros, o Kubernetes está configurado corretamente.

## Implantação da Aplicação com Perfil de Produção

1. Construa a imagem Docker:
   ```bash
   cd E:\\tesouraria\\tesouraria
   docker build -t tesouraria .
   ```

2. Implante os serviços de apoio (banco de dados e cache):
   ```bash
   kubectl apply -f k8s-postgresql-service.yaml
   kubectl apply -f k8s-redis.yaml
   ```

3. Implante a aplicação com o perfil de produção:
   ```bash
   kubectl apply -f k8s-deployment.yaml
   kubectl apply -f k8s-service.yaml
   ```

## Implantação das Ferramentas de Monitoramento

Implante as ferramentas de monitoramento e rastreamento:

```bash
kubectl apply -f k8s-prometheus.yaml
kubectl apply -f k8s-grafana.yaml
kubectl apply -f k8s-jaeger.yaml
```

Ou use o script de implantação automatizada:
```bash
deploy-monitoring.bat
```

## Verificação da Implantação Completa

Verifique se todos os deployments estão funcionando:
```bash
kubectl get deployments
```

Verifique se todos os pods estão em execução:
```bash
kubectl get pods
```

Verifique se todos os serviços estão ativos:
```bash
kubectl get services
```

Verifique se o perfil de produção está ativo:
```bash
verify-production-profile.bat
```

## Acesso às Aplicações

Após a implantação completa, você pode acessar as seguintes aplicações:

1. **Aplicação Tesouraria:** http://localhost:8080
2. **Prometheus (Métricas):** http://localhost:9090
3. **Grafana (Visualização de Métricas):** http://localhost:3000
   - Usuário: admin
   - Senha: admin
4. **Jaeger (Rastreamento Distribuído):** http://localhost:16686

## Configuração Adicional do Grafana

Para configurar o Grafana para usar o Prometheus como fonte de dados:

1. Acesse http://localhost:3000 e faça login
2. Clique em "Data Sources" e depois "Add data source"
3. Selecione "Prometheus"
4. Configure a URL como `http://prometheus-service:9090`
5. Clique em "Save & Test"

## Solução de Problemas

### Se os pods não estiverem iniciando:
```bash
kubectl describe pod <nome-do-pod>
kubectl logs <nome-do-pod>
```

### Se os serviços não estiverem acessíveis:
```bash
kubectl describe service <nome-do-serviço>
```

### Para reiniciar a implantação:
```bash
kubectl delete -f k8s-deployment.yaml
kubectl apply -f k8s-deployment.yaml
```

## Documentação Adicional

Para mais informações detalhadas, consulte os seguintes documentos:
- `docs\\PERFIL_PRODUCAO.md` - Configuração do perfil de produção
- `docs\\GUIA_PERFIL_PRODUCAO.md` - Guia completo do perfil de produção
- `docs\\MONITORAMENTO_DOCUMENTACAO.md` - Documentação das ferramentas de monitoramento
- `docs\\GUIA_COMPLETO_MONITORAMENTO.md` - Guia completo de monitoramento e rastreamento
- `docs\\CHECKLIST_PRODUCAO.md` - Checklist de prontidão para produção