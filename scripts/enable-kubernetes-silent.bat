@echo off
title Habilitador Silencioso do Kubernetes

echo =====================================================
echo  HABILITADOR SILENCIOSO DO KUBERNETES
echo =====================================================
echo.

echo Verificando se Kubernetes já está habilitado...
kubectl version --short >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] Kubernetes já está habilitado
    kubectl version --short
    echo.
    echo Nenhuma ação necessária.
    exit /b 0
)

echo [INFORMAÇÃO] Kubernetes não está habilitado.

echo.
echo Verificando se Docker Desktop está instalado...
docker --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERRO] Docker Desktop não está instalado
    echo Por favor, instale o Docker Desktop primeiro
    exit /b 1
)

echo [OK] Docker Desktop encontrado

echo.
echo Verificando se é possível habilitar o Kubernetes automaticamente...

rem Check if we can access Docker Desktop settings
docker info >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERRO] Docker Desktop não está em execução
    echo Por favor, inicie o Docker Desktop e tente novamente
    exit /b 1
)

echo.
echo Tentando habilitar Kubernetes automaticamente...
echo Esta operação requer permissões elevadas.

rem Try to enable Kubernetes using Docker CLI
echo Verificando configuração atual do Docker...
docker version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERRO] Não foi possível acessar o Docker
    exit /b 1
)

echo.
echo ATENÇÃO: A habilitação automática do Kubernetes tem limitações.
echo O Docker Desktop não fornece uma interface de linha de comando
echo para habilitar o Kubernetes silenciosamente.

echo.
echo SOLUÇÃO ALTERNATIVA:
echo Criando script de configuração para habilitar Kubernetes...

rem Create a PowerShell script to modify Docker settings
set PS_SCRIPT=%TEMP%\enable-kubernetes.ps1
(
    echo # Script para habilitar Kubernetes no Docker Desktop
    echo # Este script requer execução como administrador
    echo.
    echo $dockerSettingsPath = "$env:APPDATA\Docker\settings.json"
    echo.
    echo if (Test-Path $dockerSettingsPath) {
    echo     $settings = Get-Content $dockerSettingsPath ^| ConvertFrom-Json
    echo     $settings.kubernetesEnabled = $true
    echo     $settings.^| ConvertTo-Json -Depth 10 ^| Set-Content $dockerSettingsPath
    echo     Write-Host "[OK] Kubernetes habilitado na configuração"
    echo } else {
    echo     Write-Host "[ERRO] Arquivo de configuração do Docker não encontrado"
    echo }
) > "%PS_SCRIPT%"

echo.
echo Script de configuração criado em: %PS_SCRIPT%

echo.
echo Tentando executar script de configuração...
powershell -ExecutionPolicy Bypass -File "%PS_SCRIPT%"
if %errorlevel% neq 0 (
    echo [ERRO] Falha ao executar script de configuração
    echo Código de erro: %errorlevel%
) else (
    echo [OK] Script de configuração executado
)

echo.
echo Limpando arquivos temporários...
del "%PS_SCRIPT%" >nul 2>&1

echo.
echo =====================================================
echo TENTATIVA DE HABILITAÇÃO CONCLUÍDA
echo =====================================================
echo.
echo Devido a limitações do Docker Desktop, a habilitação completa
echo do Kubernetes requer interação manual:
echo.
echo PASSOS NECESSÁRIOS:
echo 1. Abra o Docker Desktop
echo 2. Clique no ícone de engrenagem (Settings)
echo 3. Na barra lateral esquerda, clique em "Kubernetes"
echo 4. Marque a caixa "Enable Kubernetes"
echo 5. Clique em "Apply & Restart"
echo 6. Aguarde a inicialização completa (pode levar 5-15 minutos)
echo.
echo Este processo não pode ser totalmente automatizado devido
echo a restrições de segurança do Windows e do Docker Desktop.
exit /b 0