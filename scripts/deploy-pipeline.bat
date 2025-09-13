@echo off
title Pipeline de Implantação Completa - Tesouraria

echo =====================================================
echo  PIPELINE DE IMPLANTAÇÃO COMPLETA - TESOURARIA
echo =====================================================
echo.
echo Este script irá executar toda a pipeline de implantação:
echo 1. Verificar pré-requisitos
echo 2. Construir a imagem Docker da aplicação
echo 3. Implantar serviços de apoio (PostgreSQL, Redis)
echo 4. Implantar a aplicação com perfil de produção
echo 5. Implantar ferramentas de monitoramento (Prometheus, Grafana, Jaeger)
echo.

echo Pressione qualquer tecla para continuar ou Ctrl+C para cancelar...
pause >nul

echo.
echo =====================================================
echo 1. VERIFICANDO PRÉ-REQUISITOS
echo =====================================================

echo Verificando se Docker está instalado...
docker --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERRO] Docker não está instalado ou não está no PATH
    echo Por favor, instale o Docker Desktop e tente novamente
    exit /b 1
) else (
    echo [OK] Docker encontrado
)

echo Verificando se Kubernetes está habilitado...
kubectl version --short >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERRO] Kubernetes não está habilitado ou kubectl não está no PATH
    echo Por favor, habilite o Kubernetes no Docker Desktop e tente novamente
    exit /b 1
) else (
    echo [OK] Kubernetes encontrado
)

echo Verificando arquivos necessários...
if not exist "Dockerfile" (
    echo [ERRO] Dockerfile não encontrado
    exit /b 1
) else (
    echo [OK] Dockerfile encontrado
)

if not exist "k8s-deployment.yaml" (
    echo [ERRO] k8s-deployment.yaml não encontrado
    exit /b 1
) else (
    echo [OK] k8s-deployment.yaml encontrado
)

if not exist "k8s-service.yaml" (
    echo [ERRO] k8s-service.yaml não encontrado
    exit /b 1
) else (
    echo [OK] k8s-service.yaml encontrado
)

if not exist "k8s-postgresql-service.yaml" (
    echo [ERRO] k8s-postgresql-service.yaml não encontrado
    exit /b 1
) else (
    echo [OK] k8s-postgresql-service.yaml encontrado
)

if not exist "k8s-redis.yaml" (
    echo [ERRO] k8s-redis.yaml não encontrado
    exit /b 1
) else (
    echo [OK] k8s-redis.yaml encontrado
)

if not exist "k8s-prometheus.yaml" (
    echo [ERRO] k8s-prometheus.yaml não encontrado
    exit /b 1
) else (
    echo [OK] k8s-prometheus.yaml encontrado
)

if not exist "k8s-grafana.yaml" (
    echo [ERRO] k8s-grafana.yaml não encontrado
    exit /b 1
) else (
    echo [OK] k8s-grafana.yaml encontrado
)

if not exist "k8s-jaeger.yaml" (
    echo [ERRO] k8s-jaeger.yaml não encontrado
    exit /b 1
) else (
    echo [OK] k8s-jaeger.yaml encontrado
)

echo.
echo =====================================================
echo 2. CONSTRUINDO IMAGEM DOCKER DA APLICAÇÃO
echo =====================================================

echo Construindo imagem Docker...
docker build -t tesouraria .
if %errorlevel% neq 0 (
    echo [ERRO] Falha ao construir a imagem Docker
    exit /b 1
) else (
    echo [OK] Imagem Docker construída com sucesso
)

echo.
echo =====================================================
echo 3. IMPLANTANDO SERVIÇOS DE APOIO
echo =====================================================

echo Implantando serviço PostgreSQL...
kubectl apply -f k8s-postgresql-service.yaml
if %errorlevel% neq 0 (
    echo [ERRO] Falha ao implantar serviço PostgreSQL
    exit /b 1
) else (
    echo [OK] Serviço PostgreSQL implantado
)

echo Implantando Redis...
kubectl apply -f k8s-redis.yaml
if %errorlevel% neq 0 (
    echo [ERRO] Falha ao implantar Redis
    exit /b 1
) else (
    echo [OK] Redis implantado
)

echo.
echo =====================================================
echo 4. IMPLANTANDO APLICAÇÃO COM PERFIL DE PRODUÇÃO
echo =====================================================

echo Implantando aplicação...
kubectl apply -f k8s-deployment.yaml
if %errorlevel% neq 0 (
    echo [ERRO] Falha ao implantar a aplicação
    exit /b 1
) else (
    echo [OK] Aplicação implantada
)

kubectl apply -f k8s-service.yaml
if %errorlevel% neq 0 (
    echo [ERRO] Falha ao implantar o serviço da aplicação
    exit /b 1
) else (
    echo [OK] Serviço da aplicação implantado
)

echo.
echo =====================================================
echo 5. IMPLANTANDO FERRAMENTAS DE MONITORAMENTO
echo =====================================================

echo Implantando Prometheus...
kubectl apply -f k8s-prometheus.yaml
if %errorlevel% neq 0 (
    echo [ERRO] Falha ao implantar Prometheus
    exit /b 1
) else (
    echo [OK] Prometheus implantado
)

echo Implantando Grafana...
kubectl apply -f k8s-grafana.yaml
if %errorlevel% neq 0 (
    echo [ERRO] Falha ao implantar Grafana
    exit /b 1
) else (
    echo [OK] Grafana implantado
)

echo Implantando Jaeger...
kubectl apply -f k8s-jaeger.yaml
if %errorlevel% neq 0 (
    echo [ERRO] Falha ao implantar Jaeger
    exit /b 1
) else (
    echo [OK] Jaeger implantado
)

echo.
echo =====================================================
echo 6. AGUARDANDO PODS FICAREM PRONTOS
echo =====================================================

:check_pods
echo Verificando status dos pods...
kubectl get pods
echo.
echo Aguardando todos os pods ficarem prontos... (Pressione Ctrl+C para cancelar)
timeout /t 10 /nobreak >nul

kubectl get pods | findstr "ContainerCreating" >nul
if %errorlevel% equ 0 (
    goto check_pods
)

kubectl get pods | findstr "Pending" >nul
if %errorlevel% equ 0 (
    goto check_pods
)

echo.
echo =====================================================
echo 7. RESUMO DA IMPLANTAÇÃO
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