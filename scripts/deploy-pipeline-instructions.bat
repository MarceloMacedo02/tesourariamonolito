@echo off
title Pipeline de Implantação Completa - Tesouraria (Com Setup Assistente)

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
    echo.
    echo Instruções:
    echo 1. Baixe e instale o Docker Desktop em: https://www.docker.com/products/docker-desktop/
    echo 2. Após a instalação, reinicie seu computador
    echo 3. Execute este script novamente
    exit /b 1
) else (
    echo [OK] Docker encontrado
)

echo Verificando se Kubernetes está habilitado...
kubectl version --short >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERRO] Kubernetes não está habilitado ou kubectl não está no PATH
    echo.
    echo Instruções para configurar Kubernetes no Docker Desktop:
    echo 1. Abra o Docker Desktop
    echo 2. Clique no ícone de engrenagem (Settings) no canto superior direito
    echo 3. Na barra lateral esquerda, clique em "Kubernetes"
    echo 4. Marque a caixa "Enable Kubernetes"
    echo 5. Clique em "Apply & Restart"
    echo 6. Aguarde a inicialização completa do Kubernetes (pode levar alguns minutos)
    echo 7. Verifique se o ícone do Docker Desktop fica verde e estável
    echo.
    echo Se o kubectl ainda não for encontrado após habilitar o Kubernetes:
    echo 1. Verifique se o kubectl está no PATH:
    echo    - Abra um novo prompt de comando
    echo    - Execute: where kubectl
    echo 2. Se não for encontrado, adicione manualmente ao PATH:
    echo    - Localização padrão: C:\Program Files\Docker\Docker\resources\bin\
    echo.
    echo Após configurar o Kubernetes, execute este script novamente
    exit /b 1
) else (
    echo [OK] Kubernetes encontrado
)

echo Verificando conectividade com o cluster...
kubectl cluster-info >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERRO] Não foi possível conectar ao cluster Kubernetes
    echo.
    echo O cluster Kubernetes pode estar em processo de inicialização.
    echo Aguarde alguns minutos e tente novamente.
    exit /b 1
) else (
    echo [OK] Conectividade com o cluster confirmada
)

echo Verificando se o cluster está pronto...
kubectl get nodes | findstr "NotReady" >nul
if %errorlevel% equ 0 (
    echo [ERRO] O cluster Kubernetes não está pronto
    echo.
    echo Aguarde até que todos os nodes estejam no estado "Ready":
    kubectl get nodes
    echo.
    echo Se o problema persistir, reinicie o Docker Desktop e tente novamente.
    exit /b 1
) else (
    echo [OK] Cluster Kubernetes está pronto
)

echo.
echo =====================================================
echo 2. VERIFICANDO ARQUIVOS NECESSÁRIOS
echo =====================================================

set FILES_MISSING=0

if not exist "Dockerfile" (
    echo [ERRO] Dockerfile não encontrado
    set FILES_MISSING=1
)

if not exist "k8s-deployment.yaml" (
    echo [ERRO] k8s-deployment.yaml não encontrado
    set FILES_MISSING=1
)

if not exist "k8s-service.yaml" (
    echo [ERRO] k8s-service.yaml não encontrado
    set FILES_MISSING=1
)

if not exist "k8s-postgresql-service.yaml" (
    echo [ERRO] k8s-postgresql-service.yaml não encontrado
    set FILES_MISSING=1
)

if not exist "k8s-redis.yaml" (
    echo [ERRO] k8s-redis.yaml não encontrado
    set FILES_MISSING=1
)

if not exist "k8s-prometheus.yaml" (
    echo [ERRO] k8s-prometheus.yaml não encontrado
    set FILES_MISSING=1
)

if not exist "k8s-grafana.yaml" (
    echo [ERRO] k8s-grafana.yaml não encontrado
    set FILES_MISSING=1
)

if not exist "k8s-jaeger.yaml" (
    echo [ERRO] k8s-jaeger.yaml não encontrado
    set FILES_MISSING=1
)

if %FILES_MISSING% equ 1 (
    echo.
    echo [ERRO] Alguns arquivos necessários não foram encontrados
    echo Verifique se você está executando este script no diretório raiz do projeto
    echo Diretório atual: %cd%
    exit /b 1
) else (
    echo [OK] Todos os arquivos necessários encontrados
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
    echo.
    echo Tente construir manualmente com:
    echo docker build -t tesouraria .
    exit /b 1
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
    echo Verificando conteúdo do arquivo...
    type k8s-postgresql-service.yaml
    exit /b 1
) else (
    echo [OK] Serviço PostgreSQL implantado
)

echo Implantando Redis...
kubectl apply -f k8s-redis.yaml
if %errorlevel% neq 0 (
    echo [ERRO] Falha ao implantar Redis
    echo Verificando conteúdo do arquivo...
    type k8s-redis.yaml
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
    echo Verificando conteúdo do arquivo...
    type k8s-deployment.yaml
    exit /b 1
) else (
    echo [OK] Aplicação implantada
)

kubectl apply -f k8s-service.yaml
if %errorlevel% neq 0 (
    echo [ERRO] Falha ao implantar o serviço da aplicação
    echo Verificando conteúdo do arquivo...
    type k8s-service.yaml
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
    echo Verificando conteúdo do arquivo...
    type k8s-prometheus.yaml
    exit /b 1
) else (
    echo [OK] Prometheus implantado
)

echo Implantando Grafana...
kubectl apply -f k8s-grafana.yaml
if %errorlevel% neq 0 (
    echo [ERRO] Falha ao implantar Grafana
    echo Verificando conteúdo do arquivo...
    type k8s-grafana.yaml
    exit /b 1
) else (
    echo [OK] Grafana implantado
)

echo Implantando Jaeger...
kubectl apply -f k8s-jaeger.yaml
if %errorlevel% neq 0 (
    echo [ERRO] Falha ao implantar Jaeger
    echo Verificando conteúdo do arquivo...
    type k8s-jaeger.yaml
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