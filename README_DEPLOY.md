# PIPELINE DE IMPLANTAÇÃO COMPLETA

## Visão Geral

Este projeto inclui scripts automatizados para implantar completamente a aplicação Tesouraria no Kubernetes com todas as suas dependências e ferramentas de monitoramento.

## Scripts Disponíveis

### Windows
- `deploy-pipeline.bat` - Script principal de implantação para Windows
- `deploy-pipeline-enhanced.bat` - Script de implantação com melhor tratamento de erros
- `deploy-pipeline-instructions.bat` - Script de implantação com instruções detalhadas
- `smart-deploy-pipeline.bat` - Script inteligente de implantação com verificação automática
- `diagnose-deployment.bat` - Script de diagnóstico para identificar problemas
- `cleanup-environment.bat` - Script para limpar o ambiente
- `verify-setup.bat` - Script para verificar a configuração do Docker e Kubernetes
- `setup-manager.bat` - Gerenciador completo de setup com instalação automática
- `auto-setup-manager.bat` - Gerenciador totalmente automático (sem interação)
- `install-docker-assistant.bat` - Assistente de instalação do Docker Desktop
- `install-docker-silent.bat` - Instalador silencioso do Docker Desktop
- `install-kubectl.bat` - Instalador automático do kubectl
- `install-kubectl-silent.bat` - Instalador silencioso do kubectl
- `enable-kubernetes-silent.bat` - Habilitador silencioso do Kubernetes

### Linux/Mac
- `deploy-pipeline.sh` - Script principal de implantação para Linux/Mac
- `setup-manager.sh` - Gerenciador completo de setup para Linux/Mac

## Documentação de Suporte
- `docs\TROUBLESHOOTING_PIPELINE.md` - Guia completo de solução de problemas
- `docs\CONFIG_DOCKER_KUBERNETES.md` - Guia completo de configuração do Docker Desktop com Kubernetes
- `docs\REMOCAO_CLASSES_DESCONFIG.md` - Documentação sobre classes de configuração removidas

## Ordem Recomendada de Execução

### Opção 1: Setup Automático Completo (Recomendado)
1. **Setup Automático**: `auto-setup-manager.bat` (Windows) - NENHUMA INTERAÇÃO NECESSÁRIA
2. **Reinicie o computador** se solicitado
3. **Habilite Kubernetes manualmente** (único passo que requer interação)
4. **Implante aplicação**: `smart-deploy-pipeline.bat`

### Opção 2: Setup Semi-Automático
1. **Gerenciador de Setup**: `setup-manager.bat` (Windows) ou `setup-manager.sh` (Linux/Mac)
2. **Escolha a opção 1** para verificação e configuração automática
3. **Siga as instruções** para instalar componentes ausentes

### Opção 3: Verificação Manual
1. **Verificar setup**: `verify-setup.bat`
2. **Instalar componentes necessários** usando os assistentes específicos
3. **Implantar aplicação**: `smart-deploy-pipeline.bat`

### Opção 4: Deploy Direto (se tudo já estiver instalado)
1. **Implantar aplicação**: `deploy-pipeline-instructions.bat`
2. **Diagnosticar problemas** (se necessário): `diagnose-deployment.bat`
3. **Limpar ambiente** (se necessário): `cleanup-environment.bat`

## Recursos do Pipeline Inteligente

O pipeline inteligente (`smart-deploy-pipeline.bat`) e o gerenciador de setup (`setup-manager.bat`) oferecem:

✅ **Verificação Automática de Pré-requisitos** - Detecta componentes ausentes
✅ **Instalação Guiada** - Step-by-step help for missing components
✅ **Self-Healing Capabilities** - Waits for services to be ready
✅ **Cross-Platform Support** - Works on Windows, Linux, and Mac
✅ **Comprehensive Error Handling** - Clear messages and solutions

## Recursos do Setup Automático (SEM INTERAÇÃO)

O setup automático (`auto-setup-manager.bat`) oferece:

✅ **Instalação Silenciosa** - Nenhuma interação necessária
✅ **kubectl Automático** - Instalação automática do kubectl
✅ **Docker Desktop Automático** - Instalação silenciosa do Docker Desktop
✅ **Configuração de Kubernetes** - Preparação automática (habilitação manual necessária)
✅ **Logging Completo** - Registro detalhado de todas as operações
✅ **Verificação Final** - Confirmação de sucesso de todas as etapas

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