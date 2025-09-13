# GUIA COMPLETO: CONFIGURAÇÃO DO DOCKER DESKTOP COM KUBERNETES

## Índice
1. [Pré-requisitos](#pré-requisitos)
2. [Instalação do Docker Desktop](#instalação-do-docker-desktop)
3. [Habilitação do Kubernetes](#habilitação-do-kubernetes)
4. [Verificação da Instalação](#verificação-da-instalação)
5. [Configuração do PATH](#configuração-do-path)
6. [Solução de Problemas](#solução-de-problemas)

## Pré-requisitos

Antes de começar, certifique-se de que seu sistema atende aos seguintes requisitos:

### Windows
- Windows 10 Pro, Enterprise ou Education (Build 15063 ou superior)
- Recursos Hyper-V e Containers habilitados
- Processador de 64 bits
- Mínimo de 4GB de RAM (recomendado 8GB ou mais)

### Mac
- macOS 10.14 ou superior
- Processador Intel ou Apple Silicon (M1/M2)
- Mínimo de 4GB de RAM (recomendado 8GB ou mais)

## Instalação do Docker Desktop

### Passo 1: Download
1. Acesse https://www.docker.com/products/docker-desktop/
2. Clique em "Download Docker Desktop"
3. Selecione a versão apropriada para seu sistema operacional

### Passo 2: Instalação no Windows
1. Execute o instalador baixado
2. Siga o assistente de instalação
3. Durante a instalação, certifique-se de selecionar:
   - **Enable Hyper-V Windows Features** (marcado por padrão)
   - **Install required Windows components for WSL 2** (marcado por padrão)
   - **Add shortcut to desktop** (opcional)

### Passo 3: Primeira Inicialização
1. Após a instalação, o Docker Desktop iniciará automaticamente
2. Aguarde a inicialização completa (pode levar alguns minutos)
3. O ícone do Docker aparecerá na bandeja do sistema (canto inferior direito)

## Habilitação do Kubernetes

### Passo 1: Acessar Configurações
1. Clique no ícone do Docker na bandeja do sistema
2. Selecione "Settings" (Engrenagem)

### Passo 2: Configurar Kubernetes
1. Na barra lateral esquerda, clique em "Kubernetes"
2. Marque a caixa **"Enable Kubernetes"**
3. Opcionalmente, marque **"Show system containers"** para visualizar containers do sistema
4. Clique em **"Apply & Restart"**

### Passo 3: Aguardar Inicialização
1. O Docker Desktop reiniciará automaticamente
2. A barra de status mostrará "Kubernetes starting..."
3. Aguarde até que o status mude para "Kubernetes running"
4. O processo pode levar de 5 a 15 minutos na primeira vez

## Verificação da Instalação

### Verificar Docker
Abra um prompt de comando ou PowerShell e execute:
```cmd
docker --version
```
Você deve ver uma saída similar a:
```
Docker version 24.0.5, build ced0996
```

### Verificar Kubernetes
Execute os seguintes comandos:
```cmd
kubectl version --short
kubectl cluster-info
kubectl get nodes
```

Você deve ver uma saída similar a:
```
Flag --short has been deprecated, and will be removed in the future. The --short output will become the default.
Client Version: v1.27.4
Kustomize Version: v5.0.1
Server Version: v1.27.4
```

E para `kubectl get nodes`:
```
NAME             STATUS   ROLES           AGE   VERSION
docker-desktop   Ready    control-plane   10m   v1.27.4
```

## Configuração do PATH

### Windows

Se o comando `kubectl` não for reconhecido, você precisa adicioná-lo ao PATH:

#### Método 1: Verificar Localização Padrão
1. Abra um prompt de comando e execute:
   ```cmd
   where kubectl
   ```
2. Se não encontrar, o kubectl provavelmente está em:
   ```
   C:\Program Files\Docker\Docker\resources\bin\
   ```

#### Método 2: Adicionar ao PATH Manualmente
1. Pressione `Win + X` e selecione "System"
2. Clique em "Advanced system settings"
3. Clique em "Environment Variables"
4. Na seção "System Variables", selecione "Path" e clique "Edit"
5. Clique "New" e adicione:
   ```
   C:\Program Files\Docker\Docker\resources\bin\
   ```
6. Clique "OK" para fechar todas as janelas
7. **Reinicie seu prompt de comando**

#### Método 3: Usar o Docker CLI
Alternativamente, você pode usar o comando do Docker:
```cmd
docker run -it --rm -v "%USERPROFILE%/.kube:/root/.kube" -v "%USERPROFILE%/.config:/root/.config" bitnami/kubectl:latest version
```

### Mac/Linux

No Mac e Linux, o kubectl geralmente é adicionado ao PATH automaticamente. Se não estiver:

1. Verifique a localização:
   ```bash
   which kubectl
   ```

2. Se não encontrar, adicione ao PATH no seu `.bashrc` ou `.zshrc`:
   ```bash
   export PATH="$PATH:/usr/local/bin"
   ```

## Solução de Problemas

### Problema: "kubectl: command not found"

**Solução**:
1. Verifique se o Docker Desktop está em execução
2. Confirme que o Kubernetes está habilitado
3. Adicione o kubectl ao PATH conforme instruções acima
4. Reinicie seu terminal

### Problema: "Unable to connect to the server"

**Solução**:
1. Verifique se o Docker Desktop está em execução
2. Confirme que o Kubernetes está no estado "Running"
3. Reinicie o Docker Desktop
4. Aguarde alguns minutos para o cluster inicializar completamente

### Problema: Nodes em estado "NotReady"

**Solução**:
1. Reinicie o Docker Desktop
2. Aguarde a reinicialização completa
3. Execute:
   ```cmd
   kubectl get nodes
   ```
4. Se persistir, reinicie o computador

### Problema: Kubernetes não inicia (permanece em "Kubernetes starting...")

**Solução**:
1. Reinicie o Docker Desktop
2. Se persistir, reinicie o computador
3. Verifique se os recursos do sistema estão disponíveis (RAM, CPU)
4. Tente desabilitar e reabilitar o Kubernetes nas configurações

### Problema: Erros de permissão ao executar comandos Docker

**Solução (Windows)**:
1. Execute o prompt de comando como administrador
2. Ou adicione seu usuário ao grupo "docker-users":
   - Abra "Computer Management"
   - Vá em "Local Users and Groups" > "Groups"
   - Encontre "docker-users"
   - Adicione seu usuário ao grupo

### Reset Completo do Kubernetes

Se todos os outros métodos falharem:

1. Abra o Docker Desktop
2. Vá em Settings > Kubernetes
3. Clique em "Reset Kubernetes cluster"
4. Aguarde a reinicialização completa
5. Se ainda não funcionar, tente "Reset to factory defaults"

## Verificação Final

Após configurar tudo corretamente, execute estes comandos para verificar:

```cmd
docker --version
kubectl version --short
kubectl cluster-info
kubectl get nodes
```

Todos os comandos devem executar sem erros e mostrar informações sobre o cluster.

## Próximos Passos

Com o Docker Desktop e Kubernetes configurados corretamente, você pode agora executar o pipeline de implantação:

```cmd
deploy-pipeline-instructions.bat
```

O script verificará automaticamente se todas as configurações estão corretas antes de prosseguir com a implantação.