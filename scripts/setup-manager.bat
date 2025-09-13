@echo off
title Gerenciador de Setup Completo - Tesouraria

echo =====================================================
echo  GERENCIADOR DE SETUP COMPLETO - TESOURARIA
echo =====================================================
echo.
echo Este gerenciador irá verificar e configurar automaticamente:
echo 1. Docker Desktop
echo 2. Kubernetes
echo 3. kubectl
echo 4. Todos os pré-requisitos para implantação
echo.

echo Escolha uma opção:
echo 1. Verificar e configurar tudo automaticamente
echo 2. Verificar apenas o setup atual
echo 3. Instalar/configurar Docker Desktop
echo 4. Instalar/configurar kubectl
echo 5. Habilitar Kubernetes
echo 6. Executar pipeline de implantação
echo 7. Sair
echo.
echo Opção:
set /p SETUP_OPTION=

if "%SETUP_OPTION%"=="1" (
    goto full_setup
) else if "%SETUP_OPTION%"=="2" (
    goto verify_setup
) else if "%SETUP_OPTION%"=="3" (
    goto install_docker
) else if "%SETUP_OPTION%"=="4" (
    goto install_kubectl
) else if "%SETUP_OPTION%"=="5" (
    goto enable_kubernetes
) else if "%SETUP_OPTION%"=="6" (
    goto deploy_pipeline
) else (
    echo Saindo...
    exit /b 0
)

:full_setup
echo.
echo =====================================================
echo CONFIGURAÇÃO COMPLETA AUTOMÁTICA
echo =====================================================

call :check_and_install_docker
if %errorlevel% neq 0 (
    echo [ERRO] Falha na configuração do Docker Desktop
    exit /b 1
)

call :check_and_enable_kubernetes
if %errorlevel% neq 0 (
    echo [ERRO] Falha na configuração do Kubernetes
    exit /b 1
)

call :check_and_install_kubectl
if %errorlevel% neq 0 (
    echo [ERRO] Falha na configuração do kubectl
    exit /b 1
)

echo.
echo =====================================================
echo TODOS OS PRÉ-REQUISITOS CONFIGURADOS!
echo =====================================================
echo.
echo Deseja executar o pipeline de implantação agora? (S/N)
set /p RUN_DEPLOY=
if /i "%RUN_DEPLOY%"=="S" (
    goto deploy_pipeline
) else (
    echo Setup completo. Execute o pipeline quando estiver pronto.
    exit /b 0
)

:verify_setup
echo.
echo =====================================================
echo VERIFICAÇÃO DE SETUP
echo =====================================================
call verify-setup.bat
echo.
echo Pressione qualquer tecla para continuar...
pause >nul
goto :eof

:install_docker
echo.
echo =====================================================
echo INSTALAÇÃO DO DOCKER DESKTOP
echo =====================================================
call install-docker-assistant.bat
echo.
echo Pressione qualquer tecla para continuar...
pause >nul
goto :eof

:install_kubectl
echo.
echo =====================================================
echo INSTALAÇÃO DO KUBECTL
echo =====================================================
call install-kubectl.bat
echo.
echo Pressione qualquer tecla para continuar...
pause >nul
goto :eof

:enable_kubernetes
echo.
echo =====================================================
echo HABILITAÇÃO DO KUBERNETES
echo =====================================================
echo Para habilitar o Kubernetes:
echo 1. Abra o Docker Desktop
echo 2. Clique no ícone de engrenagem (Settings)
echo 3. Na barra lateral esquerda, clique em "Kubernetes"
echo 4. Marque a caixa "Enable Kubernetes"
echo 5. Clique em "Apply & Restart"
echo 6. Aguarde a inicialização completa (pode levar alguns minutos)
echo.
echo Abrindo Docker Desktop Settings...
start "" "docker-desktop://settings"
echo.
echo Pressione qualquer tecla após habilitar o Kubernetes...
pause >nul

echo Verificando Kubernetes...
kubectl version --short >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] Kubernetes habilitado com sucesso!
) else (
    echo [ATENÇÃO] Kubernetes ainda não está acessível
    echo Por favor, verifique se está habilitado e aguarde a inicialização completa
)
goto :eof

:deploy_pipeline
echo.
echo =====================================================
echo EXECUTANDO PIPELINE DE IMPLANTAÇÃO
echo =====================================================
call smart-deploy-pipeline.bat
goto :eof

:check_and_install_docker
echo Verificando Docker Desktop...
docker --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [NECESSÁRIO] Docker Desktop não encontrado
    echo Iniciando assistente de instalação...
    call install-docker-assistant.bat
    echo.
    echo Verificando instalação...
    docker --version >nul 2>&1
    if %errorlevel% neq 0 (
        echo [ERRO] Docker Desktop ainda não instalado
        echo Por favor, instale manualmente e tente novamente
        exit /b 1
    ) else (
        echo [OK] Docker Desktop instalado
    )
) else (
    echo [OK] Docker Desktop já instalado
)
exit /b 0

:check_and_enable_kubernetes
echo Verificando Kubernetes...
kubectl version --short >nul 2>&1
if %errorlevel% neq 0 (
    echo [NECESSÁRIO] Kubernetes não está habilitado
    echo.
    echo Para habilitar o Kubernetes:
    echo 1. Abra o Docker Desktop
    echo 2. Clique no ícone de engrenagem (Settings)
    echo 3. Na barra lateral esquerda, clique em "Kubernetes"
    echo 4. Marque a caixa "Enable Kubernetes"
    echo 5. Clique em "Apply & Restart"
    echo 6. Aguarde a inicialização completa
    echo.
    echo Abrindo Docker Desktop Settings...
    start "" "docker-desktop://settings"
    echo.
    echo Pressione qualquer tecla após habilitar o Kubernetes...
    pause >nul
    
    echo Verificando novamente...
    kubectl version --short >nul 2>&1
    if %errorlevel% neq 0 (
        echo [ERRO] Kubernetes ainda não acessível
        echo Por favor, habilite manualmente e tente novamente
        exit /b 1
    ) else (
        echo [OK] Kubernetes habilitado
    )
) else (
    echo [OK] Kubernetes já habilitado
)
exit /b 0

:check_and_install_kubectl
echo Verificando kubectl...
kubectl version --short >nul 2>&1
if %errorlevel% neq 0 (
    echo [NECESSÁRIO] kubectl não encontrado
    echo Iniciando instalador...
    call install-kubectl.bat
    echo.
    echo Verificando instalação...
    kubectl version --short >nul 2>&1
    if %errorlevel% neq 0 (
        echo [ERRO] kubectl ainda não disponível
        echo Por favor, instale manualmente e tente novamente
        exit /b 1
    ) else (
        echo [OK] kubectl instalado
    )
) else (
    echo [OK] kubectl já instalado
)
exit /b 0