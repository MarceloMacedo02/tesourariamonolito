@echo off
title Teste de Instalação Oficial do kubectl

echo =====================================================
echo  TESTE DE INSTALAÇÃO OFICIAL DO KUBECTL
echo =====================================================
echo.

echo Este script testará a instalação oficial do kubectl
echo seguindo as instruções da documentação do Kubernetes.

echo.
echo Passo 1: Verificando kubectl atual...
kubectl version --client >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] kubectl encontrado:
    kubectl version --client
) else (
    echo [INFO] kubectl não encontrado (esperado para teste)
)

echo.
echo Passo 2: Executando instalação oficial silenciosa...
echo Isso pode levar alguns minutos...

call install-kubectl-official.bat

echo.
echo Passo 3: Verificando resultado da instalação...
kubectl version --client >nul 2>&1
if %errorlevel% equ 0 (
    echo [SUCESSO] kubectl instalado com sucesso!
    kubectl version --client
    echo.
    echo Teste concluído com sucesso!
) else (
    echo [FALHA] kubectl não foi instalado corretamente
    echo Verifique o log de instalação para detalhes
)

echo.
echo Pressione qualquer tecla para sair...
pause >nul