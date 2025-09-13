@echo off
title Teste de Instalação Automática do kubectl

echo =====================================================
echo  TESTE DE INSTALAÇÃO AUTOMÁTICA DO KUBECTL
echo =====================================================
echo.

echo Este script testará a instalação automática do kubectl
echo sem interação do usuário.

echo.
echo Passo 1: Verificando kubectl atual...
kubectl version --short >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] kubectl encontrado:
    kubectl version --short | findstr "Client"
) else (
    echo [INFO] kubectl não encontrado (esperado para teste)
)

echo.
echo Passo 2: Executando instalação silenciosa...
echo Isso pode levar alguns minutos...

call install-kubectl-silent.bat

echo.
echo Passo 3: Verificando resultado da instalação...
kubectl version --short >nul 2>&1
if %errorlevel% equ 0 (
    echo [SUCESSO] kubectl instalado com sucesso!
    kubectl version --short | findstr "Client"
    echo.
    echo Teste concluído com sucesso!
) else (
    echo [FALHA] kubectl não foi instalado corretamente
    echo Verifique o log de instalação para detalhes
)

echo.
echo Pressione qualquer tecla para sair...
pause >nul