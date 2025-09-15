# Tesouraria - Deployment Automático

Este projeto inclui scripts para implantar automaticamente a aplicação Tesouraria no Kubernetes.

## Estrutura de Arquivos

- `k8s/` - Configurações do Kubernetes
- `scripts/` - Scripts de deployment automático
- `docs/` - Documentação

## Pré-requisitos

1. Docker instalado e em execução
2. Kubernetes configurado (Docker Desktop, Minikube, etc.)
3. kubectl instalado

## Como Implantar

### Windows

```cmd
cd scripts
deploy-automatico.bat
```

### Linux/Mac

```bash
cd scripts
chmod +x deploy-automatico.sh
./deploy-automatico.sh
```

## O que o script faz

1. Verifica a conexão com o cluster Kubernetes
2. Constrói a imagem Docker da aplicação
3. Implanta a aplicação no Kubernetes
4. Aguarda até que o pod esteja em execução
5. Mostra informações sobre o serviço

Após a implantação, a aplicação estará disponível em http://localhost:8080