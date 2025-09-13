@echo off
title Pipeline Inteligente de Implantação - Tesouraria

echo =====================================================
echo  PIPELINE INTELIGENTE DE IMPLANTAÇÃO - TESOURARIA
echo =====================================================
echo.
echo Este script irá verificar e configurar automaticamente:
echo 1. Docker Desktop (se necessário)
echo 2. Kubernetes (se necessário)
echo 3. kubectl (se necessário)
echo 4. Construir e implantar a aplicação
echo.

echo Pressione qualquer tecla para continuar ou Ctrl+C para cancelar...
pause >nul

echo.
echo =====================================================
echo 1. VERIFICANDO E CONFIGURANDO PRÉ-REQUISITOS
echo =====================================================

:check_docker
echo Verificando Docker Desktop...
docker --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ATENÇÃO] Docker Desktop não encontrado
    echo.
    echo Opções disponíveis:
    echo 1. Instalar Docker Desktop automaticamente (requer permissões de administrador)
    echo 2. Baixar instalador manualmente
    echo 3. Cancelar e instalar manualmente
    echo.
    echo Escolha uma opção (1/2/3):
    set /p DOCKER_OPTION=
    
    if "%DOCKER_OPTION%"=="1" (
        echo Iniciando instalação automática do Docker Desktop...
        echo Esta opção requer permissões de administrador.
        echo Por favor, execute este script como administrador e tente novamente.
        echo.
        echo Alternativamente, você pode:
        echo 1. Baixar manualmente em: https://www.docker.com/products/docker-desktop/
        echo 2. Instalar e reiniciar o computador
        echo 3. Executar este script novamente
        echo.
        echo Pressione qualquer tecla para continuar com a instalação manual...
        pause >nul
        goto manual_docker_install
    ) else if "%DOCKER_OPTION%"=="2" (
        :manual_docker_install
        echo Abrindo navegador para download do Docker Desktop...
        start "" "https://www.docker.com/products/docker-desktop/"
        echo.
        echo Por favor:
        echo 1. Instale o Docker Desktop usando o instalador baixado
        echo 2. Reinicie seu computador após a instalação
        echo 3. Execute este script novamente
        echo.
        echo Pressione qualquer tecla após instalar o Docker Desktop...
        pause >nul
        goto check_docker
    ) else (
        echo Instalação cancelada. Por favor instale o Docker Desktop manualmente.
        exit /b 1
    )
) else (
    echo [OK] Docker Desktop encontrado
    for /f "delims=" %%i in ('docker --version') do set DOCKER_VERSION=%%i
    echo Versão: %DOCKER_VERSION%
)

:check_kubectl
echo.
echo Verificando kubectl...
kubectl version --short >nul 2>&1
if %errorlevel% neq 0 (
    echo [ATENÇÃO] kubectl não encontrado ou Kubernetes não acessível
    echo.
    echo Verificando se Kubernetes está habilitado no Docker Desktop...
    
    rem Check if we can enable Kubernetes through Docker Desktop settings
    echo Verificando configuração do Docker Desktop...
    
    rem Try to check Docker Desktop settings
    docker info >nul 2>&1
    if %errorlevel% neq 0 (
        echo [ERRO] Docker Desktop não está em execução
        echo Por favor, inicie o Docker Desktop e tente novamente
        exit /b 1
    )
    
    echo [INFORMAÇÃO] Para habilitar Kubernetes:
    echo 1. Abra o Docker Desktop
    echo 2. Clique no ícone de engrenagem (Settings)
    echo 3. Na barra lateral esquerda, clique em "Kubernetes"
    echo 4. Marque a caixa "Enable Kubernetes"
    echo 5. Clique em "Apply & Restart"
    echo 6. Aguarde a inicialização completa (pode levar alguns minutos)
    echo.
    echo Deseja abrir as configurações do Docker Desktop agora? (S/N)
    set /p OPEN_DOCKER_SETTINGS=
    if /i "%OPEN_DOCKER_SETTINGS%"=="S" (
        echo Abrindo Docker Desktop Settings...
        start "" "docker-desktop://settings"
    )
    
    echo.
    echo Pressione qualquer tecla após habilitar o Kubernetes...
    pause >nul
    goto check_kubectl
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
    echo [ATENÇÃO] Não foi possível conectar ao cluster Kubernetes
    echo O cluster pode estar em processo de inicialização
    echo.
    echo Aguardando inicialização do cluster... (Pressione Ctrl+C para cancelar)
    
    set /a MAX_WAIT=60
    set /a WAIT_COUNT=0
    
    :wait_for_cluster
    set /a WAIT_COUNT+=1
    echo Aguardando... (%WAIT_COUNT%/%MAX_WAIT%)
    timeout /t 10 /nobreak >nul
    
    kubectl cluster-info >nul 2>&1
    if %errorlevel% equ 0 (
        echo [OK] Cluster Kubernetes conectado
        goto cluster_ready
    )
    
    if %WAIT_COUNT% lss %MAX_WAIT% (
        goto wait_for_cluster
    ) else (
        echo [ERRO] Tempo limite excedido para inicialização do cluster
        echo Por favor, verifique o Docker Desktop e tente novamente
        exit /b 1
    )
) else (
    echo [OK] Conectividade com o cluster confirmada
)

:cluster_ready
echo.
echo Verificando estado dos nodes...
kubectl get nodes >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERRO] Não foi possível obter informações dos nodes
    exit /b 1
) else (
    echo [OK] Nodes encontrados
    kubectl get nodes | findstr "NotReady" >nul
    if %errorlevel% equ 0 (
        echo [ATENÇÃO] Alguns nodes não estão prontos
        echo Aguardando nodes ficarem prontos...
        
        set /a MAX_WAIT=30
        set /a WAIT_COUNT=0
        
        :wait_for_nodes
        set /a WAIT_COUNT+=1
        echo Aguardando... (%WAIT_COUNT%/%MAX_WAIT%)
        timeout /t 10 /nobreak >nul
        
        kubectl get nodes | findstr "NotReady" >nul
        if %errorlevel% neq 0 (
            echo [OK] Todos os nodes estão prontos
            goto nodes_ready
        )
        
        if %WAIT_COUNT% lss %MAX_WAIT% (
            goto wait_for_nodes
        ) else (
            echo [AVISO] Tempo limite excedido. Continuando com nodes não prontos...
        )
    ) else (
        echo [OK] Todos os nodes estão prontos
    )
)

:nodes_ready
echo.
echo =====================================================
echo 2. VERIFICANDO ARQUIVOS NECESSÁRIOS
echo =====================================================

set FILES_MISSING=0

echo Verificando arquivos essenciais...

set REQUIRED_FILES=Dockerfile k8s-deployment.yaml k8s-service.yaml k8s-postgresql-service.yaml k8s-redis.yaml k8s-prometheus.yaml k8s-grafana.yaml k8s-jaeger.yaml

for %%f in (%REQUIRED_FILES%) do (
    if not exist "%%f" (
        echo [ERRO] Arquivo não encontrado: %%f
        set FILES_MISSING=1
    ) else (
        echo [OK] %%f encontrado
    )
)

if %FILES_MISSING% equ 1 (
    echo.
    echo [ERRO] Alguns arquivos necessários não foram encontrados
    echo Verifique se você está executando este script no diretório raiz do projeto
    echo Diretório atual: %cd%
    exit /b 1
)

echo.
echo =====================================================
echo 3. CONSTRUINDO IMAGEM DOCKER DA APLICAÇÃO
echo =====================================================

echo Construindo imagem Docker...
docker build -t tesouraria .
if %errorlevel% neq 0 (
    echo [ERRO] Falha ao construir a imagem Docker
    echo.
    echo Possíveis causas:
    echo 1. Problemas no arquivo Dockerfile
    echo 2. Falta de permissões no diretório
    echo 3. Problemas de rede ao baixar dependências
    echo 4. Recursos de sistema insuficientes
    echo.
    echo Deseja tentar novamente? (S/N)
    set /p RETRY_BUILD=
    if /i "%RETRY_BUILD%"=="S" (
        goto retry_build
    ) else (
        echo Construção cancelada
        exit /b 1
    )
    
    :retry_build
    echo Tentando construir novamente...
    docker build -t tesouraria .
    if %errorlevel% neq 0 (
        echo [ERRO] Falha novamente ao construir a imagem Docker
        echo Por favor, verifique o Dockerfile e as dependências
        exit /b 1
    )
) else (
    echo [OK] Imagem Docker construída com sucesso
)

echo.
echo =====================================================
echo 4. IMPLANTANDO SERVIÇOS DE APOIO
echo =====================================================

echo Implantando serviço PostgreSQL...
kubectl apply -f k8s-postgresql-service.yaml
if %errorlevel% neq 0 (
    echo [ERRO] Falha ao implantar serviço PostgreSQL
    echo Detalhes do erro:
    kubectl apply -f k8s-postgresql-service.yaml
    exit /b 1
) else (
    echo [OK] Serviço PostgreSQL implantado
)

echo Implantando Redis...
kubectl apply -f k8s-redis.yaml
if %errorlevel% neq 0 (
    echo [ERRO] Falha ao implantar Redis
    echo Detalhes do erro:
    kubectl apply -f k8s-redis.yaml
    exit /b 1
) else (
    echo [OK] Redis implantado
)

echo.
echo =====================================================
echo 5. IMPLANTANDO APLICAÇÃO COM PERFIL DE PRODUÇÃO
echo =====================================================

echo Implantando aplicação...
kubectl apply -f k8s-deployment.yaml
if %errorlevel% neq 0 (
    echo [ERRO] Falha ao implantar a aplicação
    echo Detalhes do erro:
    kubectl apply -f k8s-deployment.yaml
    exit /b 1
) else (
    echo [OK] Aplicação implantada
)

kubectl apply -f k8s-service.yaml
if %errorlevel% neq 0 (
    echo [ERRO] Falha ao implantar o serviço da aplicação
    echo Detalhes do erro:
    kubectl apply -f k8s-service.yaml
    exit /b 1
) else (
    echo [OK] Serviço da aplicação implantado
)

echo.
echo =====================================================
echo 6. IMPLANTANDO FERRAMENTAS DE MONITORAMENTO
echo =====================================================

echo Implantando Prometheus...
kubectl apply -f k8s-prometheus.yaml
if %errorlevel% neq 0 (
    echo [ERRO] Falha ao implantar Prometheus
    echo Detalhes do erro:
    kubectl apply -f k8s-prometheus.yaml
    exit /b 1
) else (
    echo [OK] Prometheus implantado
)

echo Implantando Grafana...
kubectl apply -f k8s-grafana.yaml
if %errorlevel% neq 0 (
    echo [ERRO] Falha ao implantar Grafana
    echo Detalhes do erro:
    kubectl apply -f k8s-grafana.yaml
    exit /b 1
) else (
    echo [OK] Grafana implantado
)

echo Implantando Jaeger...
kubectl apply -f k8s-jaeger.yaml
if %errorlevel% neq 0 (
    echo [ERRO] Falha ao implantar Jaeger
    echo Detalhes do erro:
    kubectl apply -f k8s-jaeger.yaml
    exit /b 1
) else (
    echo [OK] Jaeger implantado
)

echo.
echo =====================================================
echo 7. AGUARDANDO PODS FICAREM PRONTOS
echo =====================================================

set /a MAX_ATTEMPTS=30
set /a ATTEMPT=0

:check_pods
set /a ATTEMPT+=1
echo Tentativa %ATTEMPT%/%MAX_ATTEMPTS% - Verificando status dos pods...
kubectl get pods
echo.

kubectl get pods | findstr "ContainerCreating\|Pending" >nul
if %errorlevel% equ 0 (
    if %ATTEMPT% lss %MAX_ATTEMPTS% (
        echo Aguardando pods ficarem prontos... (Tentativa %ATTEMPT%/%MAX_ATTEMPTS%)
        timeout /t 10 /nobreak >nul
        goto check_pods
    ) else (
        echo [AVISO] Alguns pods ainda estão em criação após %MAX_ATTEMPTS% tentativas
        echo Continuando com a implantação...
    )
)

echo.
echo =====================================================
echo 8. RESUMO DA IMPLANTAÇÃO
echo =====================================================

echo Status dos deployments:
kubectl get deployments
echo.

echo Status dos serviços:
kubectl get services
echo.

echo =====================================================
echo IMPLANTAÇÃO CONCLUÍDA COM SUCESSO!
echo =====================================================
echo.
echo A aplicação está rodando no Kubernetes com:
echo - Perfil de produção ativo
echo - Monitoramento completo (Prometheus, Grafana, Jaeger)
echo - Serviços de apoio (PostgreSQL, Redis)
echo.
echo Acesse os serviços nos seguintes endereços:
echo.
echo Aplicação Tesouraria:    http://localhost:8080
echo Prometheus:              http://localhost:9090
echo Grafana:                 http://localhost:3000  (usuário: admin, senha: admin)
echo Jaeger:                  http://localhost:16686
echo.
echo Para verificar os logs da aplicação:
echo kubectl logs deployment/tesouraria-deployment
echo.
echo Para verificar o status dos pods:
echo kubectl get pods
echo.
echo Pipeline de implantação concluída!