# GUIA DE SOLUÇÃO DE PROBLEMAS - PIPELINE DE IMPLANTAÇÃO

## Erros Comuns e Soluções

### 1. Erros de Docker

#### "Cannot connect to the Docker daemon"
**Causa**: Docker Desktop não está em execução
**Solução**: 
1. Inicie o Docker Desktop
2. Aguarde até que o ícone fique verde e estável
3. Tente executar o pipeline novamente

#### "The command 'docker' could not be found"
**Causa**: Docker não está no PATH do sistema
**Solução**:
1. Verifique se o Docker Desktop está instalado
2. Reinicie o terminal/PowerShell
3. Adicione o Docker ao PATH manualmente se necessário

#### "failed to solve: rpc error"
**Causa**: Problemas na construção da imagem Docker
**Solução**:
1. Verifique o arquivo Dockerfile
2. Tente construir a imagem manualmente:
   ```cmd
   docker build -t tesouraria .
   ```
3. Verifique as permissões do diretório

### 2. Erros de Kubernetes

#### "Unable to connect to the server"
**Causa**: Kubernetes não está habilitado ou acessível
**Solução**:
1. Abra o Docker Desktop
2. Vá em Settings > Kubernetes
3. Certifique-se de que "Enable Kubernetes" está marcado
4. Clique em "Apply & Restart"
5. Aguarde a inicialização completa

#### "kubectl: command not found"
**Causa**: kubectl não está instalado ou no PATH
**Solução**:
1. Verifique se o Docker Desktop está instalado (inclui kubectl)
2. Reinicie o terminal
3. Ou instale o kubectl manualmente

### 3. Erros de YAML

#### "error: error parsing ..."
**Causa**: Arquivo YAML mal formatado
**Solução**:
1. Valide o arquivo YAML usando um validador online
2. Verifique indentação (use espaços, não tabs)
3. Verifique se todos os campos obrigatórios estão presentes

#### "error: unable to recognize ..."
**Causa**: API version ou kind incorreto no YAML
**Solução**:
1. Verifique se apiVersion e kind estão corretos
2. Confirme se a versão do Kubernetes suporta os recursos

### 4. Erros de Implantação

#### "ImagePullBackOff" ou "ErrImagePull"
**Causa**: Imagem Docker não encontrada ou inacessível
**Solução**:
1. Verifique se a imagem foi construída:
   ```cmd
   docker images | findstr tesouraria
   ```
2. Reconstrua a imagem se necessário
3. Certifique-se de que o Docker Desktop está em execução

#### "CrashLoopBackOff"
**Causa**: Aplicação está iniciando mas falhando repetidamente
**Solução**:
1. Verifique os logs:
   ```cmd
   kubectl logs deployment/tesouraria-deployment
   ```
2. Verifique os eventos:
   ```cmd
   kubectl describe pod -l app=tesouraria
   ```

#### "ContainerCreating" por muito tempo
**Causa**: Problemas na criação do container
**Solução**:
1. Verifique os eventos do pod:
   ```cmd
   kubectl describe pod -l app=tesouraria
   ```
2. Verifique se há recursos suficientes
3. Verifique as configurações de volume se aplicável

### 5. Erros de Conexão

#### "Connection refused" ao acessar serviços
**Causa**: Serviço não está pronto ou porta incorreta
**Solução**:
1. Verifique se o serviço está rodando:
   ```cmd
   kubectl get services
   ```
2. Verifique se o pod está pronto:
   ```cmd
   kubectl get pods
   ```
3. Verifique as portas no arquivo de serviço

## Comandos de Diagnóstico Úteis

### Verificar status geral:
```cmd
kubectl cluster-info
kubectl get nodes
kubectl get pods --all-namespaces
```

### Verificar pods com problemas:
```cmd
kubectl get pods --field-selector=status.phase!=Running
```

### Verificar logs de um pod:
```cmd
kubectl logs <nome-do-pod>
```

### Verificar eventos:
```cmd
kubectl get events --sort-by=.metadata.creationTimestamp
```

### Verificar descrição detalhada de um recurso:
```cmd
kubectl describe pod <nome-do-pod>
kubectl describe deployment <nome-do-deployment>
kubectl describe service <nome-do-service>
```

## Scripts de Diagnóstico

### Verificar todos os recursos:
```cmd
@echo off
echo Verificando status do cluster...
kubectl cluster-info
echo.

echo Verificando nodes...
kubectl get nodes
echo.

echo Verificando pods...
kubectl get pods
echo.

echo Verificando deployments...
kubectl get deployments
echo.

echo Verificando services...
kubectl get services
echo.

echo Verificando eventos recentes...
kubectl get events --sort-by=.metadata.creationTimestamp
```

### Verificar pods com problemas:
```cmd
@echo off
echo Pods com problemas:
kubectl get pods --field-selector=status.phase!=Running
echo.

echo Pods em estado ContainerCreating:
kubectl get pods | findstr "ContainerCreating"
echo.

echo Pods em estado Pending:
kubectl get pods | findstr "Pending"
echo.

echo Pods em estado CrashLoopBackOff:
kubectl get pods | findstr "CrashLoopBackOff"
```

## Reinicialização Completa

Se os problemas persistirem, tente uma reinicialização completa:

1. **Limpar recursos existentes**:
   ```cmd
   kubectl delete -f k8s-deployment.yaml
   kubectl delete -f k8s-service.yaml
   kubectl delete -f k8s-postgresql-service.yaml
   kubectl delete -f k8s-redis.yaml
   kubectl delete -f k8s-prometheus.yaml
   kubectl delete -f k8s-grafana.yaml
   kubectl delete -f k8s-jaeger.yaml
   ```

2. **Reiniciar Docker Desktop**:
   - Feche o Docker Desktop completamente
   - Aguarde 30 segundos
   - Inicie o Docker Desktop novamente
   - Aguarde a inicialização do Kubernetes

3. **Reexecutar o pipeline**:
   ```cmd
   deploy-pipeline.bat
   ```

## Suporte Adicional

Se os problemas persistirem após tentar estas soluções:

1. **Colete informações de diagnóstico**:
   - Saída completa do pipeline
   - Saída de `kubectl get all`
   - Saída de `kubectl describe` dos recursos com problemas
   - Logs relevantes

2. **Verifique os arquivos de configuração**:
   - Todos os arquivos YAML
   - application-prod.properties
   - Dockerfile

3. **Consulte a documentação**:
   - `docs\GUIA_PERFIL_PRODUCAO.md`
   - `docs\GUIA_COMPLETO_MONITORAMENTO.md`
   - `README_DEPLOY.md`