@echo off
title Pipeline de Implantação Otimizada - Tesouraria

echo =====================================================
echo  PIPELINE DE IMPLANTAÇÃO OTIMIZADA - TESOURARIA
echo =====================================================
echo.
echo Este script irá executar uma pipeline de implantação otimizada:
echo 1. Verificar pré-requisitos
echo 2. Construir a imagem Docker da aplicação com tag única
echo 3. Enviar a imagem para o repositório (simulado)
echo 4. Verificar se o banco de dados já está implantado
echo 5. Atualizar e aplicar manifesto de deployment da aplicação
echo.

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
kubectl version >nul 2>&1
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

if not exist "k8s/app-deployment.yaml" (
    echo [ERRO] k8s/app-deployment.yaml não encontrado
    exit /b 1
) else (
    echo [OK] k8s/app-deployment.yaml encontrado
)

echo.
echo =====================================================
echo 2. CONSTRUINDO IMAGEM DOCKER DA APLICAÇÃO
echo =====================================================

REM Gerar tag única para a imagem Docker usando PowerShell
for /f "tokens=* USEBACKQ" %%a in (`powershell -Command "Get-Date -Format 'yyyyMMdd-HHmmss'"`) do set "datestamp=%%a"
set DOCKER_IMAGE_TAG=tesouraria:%datestamp%

echo Construindo imagem Docker com tag: %DOCKER_IMAGE_TAG%
docker build -t %DOCKER_IMAGE_TAG% .
if %errorlevel% neq 0 (
    echo [ERRO] Falha ao construir a imagem Docker
    exit /b 1
) else (
    echo [OK] Imagem Docker construída com sucesso
)

echo.
echo =====================================================
echo 3. ENVIANDO IMAGEM PARA O REPOSITÓRIO
echo =====================================================

echo Enviando imagem %DOCKER_IMAGE_TAG% para o repositório...
echo [INFO] Push da imagem para repositório seria executado aqui com:
echo docker push %DOCKER_IMAGE_TAG%
echo [SIMULADO] Push da imagem não realizado neste script de exemplo
echo [OK] Etapa de push simulada com sucesso

echo.
echo =====================================================
echo 4. VERIFICANDO BANCO DE DADOS
echo =====================================================

echo Verificando se o banco de dados já está implantado...
kubectl get statefulsets postgresql-statefulset >nul 2>&1
if %errorlevel% neq 0 (
    echo [INFO] Banco de dados não encontrado. Implantando StatefulSet e Service do banco de dados...
    if not exist "k8s\db-statefulset.yaml" (
        echo [ERRO] db-statefulset.yaml não encontrado
        exit /b 1
    ) else (
        echo [OK] db-statefulset.yaml encontrado
    )
    
    if not exist "k8s\k8s-postgresql-service.yaml" (
        echo [ERRO] k8s-postgresql-service.yaml não encontrado
        exit /b 1
    ) else (
        echo [OK] k8s-postgresql-service.yaml encontrado
    )
    
    kubectl apply -f k8s\k8s-postgresql-service.yaml
    if %errorlevel% neq 0 (
        echo [ERRO] Falha ao aplicar o manifesto do Service do banco de dados
        exit /b 1
    ) else (
        echo [OK] Manifesto do Service do banco de dados aplicado com sucesso
    )
    
    kubectl apply -f k8s\db-statefulset.yaml
    if %errorlevel% neq 0 (
        echo [ERRO] Falha ao aplicar o manifesto do StatefulSet
        exit /b 1
    ) else (
        echo [OK] Manifesto do StatefulSet aplicado com sucesso
    )
) else (
    echo [INFO] Banco de dados já está implantado. Pulando etapa de implantação do banco de dados.
)

echo.
echo =====================================================
echo 5. ATUALIZANDO E APLICANDO MANIFESTO DE DEPLOYMENT
echo =====================================================

echo Atualizando manifesto de deployment com a tag da nova imagem...
sed "s|DOCKER_IMAGE_TAG|%DOCKER_IMAGE_TAG%|g" k8s/app-deployment.yaml > k8s/app-deployment-updated.yaml
if %errorlevel% neq 0 (
    echo [ERRO] Falha ao atualizar o manifesto de deployment
    exit /b 1
) else (
    echo [OK] Manifesto de deployment atualizado com sucesso
)

echo Aplicando manifesto de deployment atualizado...
kubectl apply -f k8s/app-deployment-updated.yaml
if %errorlevel% neq 0 (
    echo [ERRO] Falha ao aplicar o manifesto de deployment atualizado
    exit /b 1
) else (
    echo [OK] Manifesto de deployment aplicado com sucesso
)

echo Aplicando service da aplicação...
if not exist "k8s\k8s-service.yaml" (
    echo [ERRO] k8s-service.yaml não encontrado
    exit /b 1
) else (
    kubectl apply -f k8s\k8s-service.yaml
    if %errorlevel% neq 0 (
        echo [ERRO] Falha ao aplicar o service da aplicação
        exit /b 1
    ) else (
        echo [OK] Service da aplicação aplicado com sucesso
    )
)

REM Aguardar até que os pods estejam prontos
echo Aguardando os pods da aplicação ficarem prontos...
:wait_for_pods
timeout /t 5 /nobreak >nul
kubectl get pods -l app=tesouraria -o jsonpath="{.items[*].status.containerStatuses[*].ready}" | findstr "false" >nul 2>&1
if %errorlevel% equ 0 (
    echo Pods ainda não estão prontos, aguardando mais...
    goto wait_for_pods
)

REM Remover arquivo temporário
del k8s\app-deployment-updated.yaml >nul 2>&1
if %errorlevel% neq 0 (
    echo [AVISO] Não foi possível remover o arquivo temporário k8s\app-deployment-updated.yaml
) else (
    echo [OK] Arquivo temporário removido com sucesso
)

echo.
echo =====================================================
echo 6. RESUMO DA IMPLANTAÇÃO
echo =====================================================

echo Status dos deployments:
kubectl get deployments
echo.

echo Status dos serviços:
kubectl get services
echo.

echo Status dos statefulsets:
kubectl get statefulsets
echo.

echo Verificando pods da aplicação:
kubectl get pods -l app=tesouraria
echo.

echo =====================================================
echo IMPLANTAÇÃO OTIMIZADA CONCLUÍDA COM SUCESSO!
echo =====================================================
echo.
echo A aplicação foi implantada com a tag: %DOCKER_IMAGE_TAG%
echo Banco de dados foi verificado/configurado com StatefulSet para persistência
echo.
echo Para acessar a aplicação:
echo - Obter o endereço do serviço: kubectl get service tesouraria-service
echo - Se estiver usando Docker Desktop Kubernetes, acesse http://localhost:8080
echo.
echo Pipeline de implantação otimizada concluída!