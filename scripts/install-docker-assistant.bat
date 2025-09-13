@echo off
title Assistente de Instalação do Docker Desktop

echo =====================================================
echo  ASSISTENTE DE INSTALAÇÃO DO DOCKER DESKTOP
echo =====================================================
echo.

echo Este assistente ajudará você a instalar o Docker Desktop
echo e configurar o Kubernetes automaticamente.
echo.

echo Verificando sistema operacional...
ver | findstr "10\." >nul
if %errorlevel% equ 0 (
    echo [OK] Windows 10 ou superior detectado
    set WINDOWS_VERSION=10
) else (
    ver | findstr "6.1" >nul
    if %errorlevel% equ 0 (
        echo [ERRO] Windows 7 não é suportado
        echo Docker Desktop requer Windows 10 ou superior
        exit /b 1
    ) else (
        echo [OK] Windows compatível detectado
        set WINDOWS_VERSION=10
    )
)

echo.
echo Verificando arquitetura do sistema...
if "%PROCESSOR_ARCHITECTURE%"=="AMD64" (
    echo [OK] Arquitetura x64 detectada
    set ARCHITECTURE=x64
) else (
    echo [ERRO] Arquitetura não suportada
    echo Docker Desktop requer arquitetura x64
    exit /b 1
)

echo.
echo Verificando se o Hyper-V está habilitado...
powershell -Command "Get-WindowsOptionalFeature -Online -FeatureName Microsoft-Hyper-V" | findstr "Enabled" >nul
if %errorlevel% equ 0 (
    echo [OK] Hyper-V já está habilitado
) else (
    echo [INFORMAÇÃO] Hyper-V precisa ser habilitado
    echo.
    echo Para habilitar o Hyper-V:
    echo 1. Abra o PowerShell como administrador
    echo 2. Execute o seguinte comando:
    echo    Enable-WindowsOptionalFeature -Online -FeatureName Microsoft-Hyper-V -All
    echo 3. Reinicie o computador quando solicitado
    echo.
    echo Deseja tentar habilitar o Hyper-V automaticamente? (Requer administrador) (S/N)
    set /p ENABLE_HYPERV=
    if /i "%ENABLE_HYPERV%"=="S" (
        echo Tentando habilitar Hyper-V...
        powershell -Command "Start-Process powershell -ArgumentList 'Enable-WindowsOptionalFeature -Online -FeatureName Microsoft-Hyper-V -All -NoRestart' -Verb RunAs"
        echo.
        echo Por favor, reinicie o computador quando solicitado
        echo Após reiniciar, execute este script novamente
        pause
        exit /b 0
    )
)

echo.
echo Baixando Docker Desktop...
echo Abrindo página de download...
start "" "https://desktop.docker.com/win/main/amd64/Docker%20Desktop%20Installer.exe"

echo.
echo Instruções para instalação:
echo 1. Execute o instalador baixado
echo 2. Siga o assistente de instalação
echo 3. Reinicie o computador quando solicitado
echo 4. Após reiniciar, inicie o Docker Desktop
echo 5. Aguarde a inicialização completa
echo.
echo Após a instalação, execute o script de verificação:
echo verify-setup.bat
echo.
echo Pressione qualquer tecla para continuar...
pause >nul

echo.
echo Após instalar o Docker Desktop:
echo 1. Inicie o Docker Desktop
echo 2. Aguarde até que o ícone fique verde e estável
echo 3. Habilite o Kubernetes:
echo    a. Clique no ícone de engrenagem (Settings)
echo    b. Clique em "Kubernetes" na barra lateral
echo    c. Marque "Enable Kubernetes"
echo    d. Clique em "Apply & Restart"
echo    e. Aguarde a inicialização completa
echo.
echo Pressione qualquer tecla após configurar o Docker Desktop...
pause >nul

echo.
echo Verificando instalação...
docker --version >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] Docker Desktop instalado com sucesso
    echo.
    echo Verificando Kubernetes...
    kubectl version --short >nul 2>&1
    if %errorlevel% equ 0 (
        echo [OK] Kubernetes configurado com sucesso
        echo.
        echo Sua instalação está completa!
        echo Agora você pode executar o pipeline de implantação.
    ) else (
        echo [INFORMAÇÃO] Docker Desktop instalado, mas Kubernetes ainda não configurado
        echo Por favor, habilite o Kubernetes nas configurações do Docker Desktop
    )
) else (
    echo [ATENÇÃO] Docker Desktop ainda não está instalado
    echo Por favor, complete a instalação e tente novamente
)

echo.
echo Pressione qualquer tecla para sair...
pause >nul