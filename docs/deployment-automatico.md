# Deployment Automático da Aplicação Tesouraria

Este diretório contém scripts para implantar automaticamente a aplicação Tesouraria no Kubernetes sem interação humana.

## Scripts Disponíveis

1. `deploy-automatico.bat` - Script para Windows
2. `deploy-automatico.sh` - Script para Linux/Mac

## Pré-requisitos

Antes de executar os scripts, certifique-se de que você tem:

1. Docker instalado e em execução
2. Kubernetes configurado e em execução (Docker Desktop Kubernetes, Minikube, etc.)
3. kubectl instalado e configurado
4. Acesso ao código-fonte da aplicação

## Como Usar

### No Windows:

```cmd
cd scripts
deploy-automatico.bat
```

### No Linux/Mac:

```bash
cd scripts
chmod +x deploy-automatico.sh
./deploy-automatico.sh
```

## O que os scripts fazem

1. Verificam se o cluster Kubernetes está acessível
2. Constroem a imagem Docker da aplicação
3. Aplicam a configuração de deployment e service no Kubernetes
4. Aguardam até que o pod esteja em execução
5. Exibem informações sobre o serviço implantado

## Personalização

Você pode modificar os seguintes arquivos para personalizar a implantação:

- `k8s/deployment.yaml` - Configuração do deployment e service
- `src/main/resources/application-prod.properties` - Configurações da aplicação em produção

## Solução de Problemas

Se encontrar problemas durante a execução:

1. Verifique se o Docker está em execução
2. Verifique se o Kubernetes está em execução com `kubectl cluster-info`
3. Verifique se há pods com problemas com `kubectl get pods`
4. Verifique os logs dos pods com `kubectl logs <nome-do-pod>`