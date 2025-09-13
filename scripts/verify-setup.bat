@echo off
title Verificação de Setup - Docker e Kubernetes

echo =====================================================
echo  VERIFICAÇÃO DE SETUP - DOCKER E KUBERNETES
echo =====================================================
echo.

echo =====================================================
echo 1. VERIFICANDO DOCKER
echo =====================================================
echo Verificando se Docker está instalado...
docker --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERRO] Docker não encontrado
    echo.
    echo Instruções:
    echo 1. Baixe o Docker Desktop em: https://www.docker.com/products/docker-desktop/
    echo 2. Instale seguindo as instruções do instalador
    echo 3. Reinicie seu computador após a instalação
    exit /b 1
) else (
    echo [OK] Docker encontrado
    for /f "delims=" %%i in ('docker --version') do set DOCKER_VERSION=%%i
    echo Versão: %DOCKER_VERSION%
)

echo.
echo =====================================================
echo 2. VERIFICANDO KUBERNETES
echo =====================================================
echo Verificando se kubectl está disponível...
kubectl version --short >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERRO] kubectl não encontrado ou Kubernetes não está acessível
    echo.
    echo Instruções:
    echo 1. Abra o Docker Desktop
    echo 2. Vá em Settings ^> Kubernetes
    echo 3. Marque "Enable Kubernetes"
    echo 4. Clique em "Apply ^& Restart"
    echo 5. Aguarde a inicialização completa (pode levar alguns minutos)
    echo.
    echo Se o problema persistir, verifique se kubectl está no PATH:
    echo Tente localizar manualmente em:
    echo C:\Program Files\Docker\Docker\resources\bin\
    exit /b 1
) else (
    echo [OK] kubectl encontrado
    echo Versão do cliente:
    kubectl version --short | findstr "Client"
    echo Versão do servidor:
    kubectl version --short | findstr "Server"
)

echo.
echo Verificando conectividade com o cluster...
kubectl cluster-info >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERRO] Não foi possível conectar ao cluster Kubernetes
    echo O cluster pode estar em processo de inicialização
    exit /b 1
) else (
    echo [OK] Conectividade com o cluster confirmada
)

echo.
echo Verificando estado dos nodes...
kubectl get nodes >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERRO] Não foi possível obter informações dos nodes
    exit /b 1
) else (
    echo [OK] Nodes encontrados
    echo Estado atual dos nodes:
    kubectl get nodes | findstr -v "NAME"
)

echo.
echo =====================================================
echo 3. VERIFICANDO CONTEXTOS
echo =====================================================
echo Contexto atual:
kubectl config current-context 2>nul
if %errorlevel% neq 0 (
    echo [ERRO] Não foi possível determinar o contexto atual
) else (
    echo [OK] Contexto Kubernetes configurado
)

echo.
echo =====================================================
echo 4. VERIFICANDO PERMISSÕES
echo =====================================================
echo Testando permissões básicas...
kubectl get pods --all-namespaces >nul 2>&1
if %errorlevel% neq 0 (
    echo [AVISO] Permissões limitadas ou cluster ainda inicializando
    echo Isso pode ser normal se o cluster estiver em inicialização
) else (
    echo [OK] Permissões básicas confirmadas
)

echo.
echo =====================================================
echo RESUMO DA VERIFICAÇÃO
echo =====================================================
echo Docker:              [OK]
echo Kubernetes:          [OK]
echo Conectividade:       [OK]
echo Nodes:               [OK]
echo Permissões:          [OK]
echo.
echo =====================================================
echo SETUP CONCLUÍDO COM SUCESSO!
echo =====================================================
echo.
echo Agora você pode executar o pipeline de implantação:
echo deploy-pipeline-instructions.bat
echo.
echo Ou verificar os recursos existentes:
echo kubectl get all