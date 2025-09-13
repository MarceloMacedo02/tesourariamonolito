@echo off
title Gerenciador de Setup Automático - Tesouraria

echo =====================================================
echo  GERENCIADOR DE SETUP AUTOMÁTICO - TESOURARIA
echo =====================================================
echo.
echo Este gerenciador irá configurar automaticamente:
echo 1. Docker Desktop (se necessário)
echo 2. kubectl (se necessário)
echo 3. Preparar ambiente para Kubernetes
echo.
echo NENHUMA INTERAÇÃO SERÁ NECESSÁRIA!
echo O processo pode levar 10-20 minutos.
echo.

echo Iniciando setup automático...
echo Log será salvo em: setup-log.txt
echo. > setup-log.txt

echo ===================================================== >> setup-log.txt
echo INICIANDO SETUP AUTOMÁTICO - %date% %time% >> setup-log.txt
echo ===================================================== >> setup-log.txt

echo [1/4] Verificando kubectl...
echo [1/4] Verificando kubectl... >> setup-log.txt
kubectl version --client >nul 2>&1
if %errorlevel% neq 0 (
    echo [NECESSÁRIO] Instalando kubectl...
    echo [NECESSÁRIO] Instalando kubectl... >> setup-log.txt
    call install-kubectl-official.bat >> setup-log.txt 2>&1
    if %errorlevel% neq 0 (
        echo [ERRO] Falha na instalação do kubectl
        echo [ERRO] Falha na instalação do kubectl >> setup-log.txt
    ) else (
        echo [OK] kubectl instalado
        echo [OK] kubectl instalado >> setup-log.txt
    )
) else (
    echo [OK] kubectl já instalado
    echo [OK] kubectl já instalado >> setup-log.txt
)

echo.
echo [2/4] Verificando Docker Desktop...
echo [2/4] Verificando Docker Desktop... >> setup-log.txt
docker --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [NECESSÁRIO] Instalando Docker Desktop...
    echo [NECESSÁRIO] Instalando Docker Desktop... >> setup-log.txt
    call install-docker-silent.bat >> setup-log.txt 2>&1
    if %errorlevel% neq 0 (
        echo [ERRO] Falha na instalação do Docker Desktop
        echo [ERRO] Falha na instalação do Docker Desktop >> setup-log.txt
    ) else (
        echo [OK] Docker Desktop instalado
        echo [OK] Docker Desktop instalado >> setup-log.txt
    )
) else (
    echo [OK] Docker Desktop já instalado
    echo [OK] Docker Desktop já instalado >> setup-log.txt
)

echo.
echo [3/4] Verificando Kubernetes...
echo [3/4] Verificando Kubernetes... >> setup-log.txt
kubectl version --client >nul 2>&1
if %errorlevel% neq 0 (
    echo [NECESSÁRIO] Preparando configuração do Kubernetes...
    echo [NECESSÁRIO] Preparando configuração do Kubernetes... >> setup-log.txt
    call enable-kubernetes-silent.bat >> setup-log.txt 2>&1
    if %errorlevel% neq 0 (
        echo [AVISO] Não foi possível habilitar Kubernetes automaticamente
        echo [AVISO] Não foi possível habilitar Kubernetes automaticamente >> setup-log.txt
    ) else (
        echo [OK] Configuração do Kubernetes preparada
        echo [OK] Configuração do Kubernetes preparada >> setup-log.txt
    )
) else (
    echo [OK] Kubernetes já habilitado
    echo [OK] Kubernetes já habilitado >> setup-log.txt
)

echo.
echo [4/4] Verificação final...
echo [4/4] Verificação final... >> setup-log.txt

echo Verificando Docker...
docker --version >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] Docker: Disponível
    echo [OK] Docker: Disponível >> setup-log.txt
) else (
    echo [ERRO] Docker: Não disponível
    echo [ERRO] Docker: Não disponível >> setup-log.txt
)

echo Verificando kubectl...
kubectl version --client >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] kubectl: Disponível
    echo [OK] kubectl: Disponível >> setup-log.txt
) else (
    echo [ERRO] kubectl: Não disponível
    echo [ERRO] kubectl: Não disponível >> setup-log.txt
)

echo.
echo =====================================================
echo RESUMO DO SETUP AUTOMÁTICO
echo =====================================================
echo.
type setup-log.txt | findstr "OK\|ERRO\|AVISO\|NECESSÁRIO"

echo.
echo ===================================================== >> setup-log.txt
echo SETUP AUTOMÁTICO CONCLUÍDO - %date% %time% >> setup-log.txt
echo ===================================================== >> setup-log.txt

echo.
echo =====================================================
echo SETUP AUTOMÁTICO CONCLUÍDO!
echo =====================================================
echo.
echo RESULTADO:
echo - Docker Desktop: Instalado/Verificado
echo - kubectl: Instalado/Verificado
echo - Kubernetes: Configuração preparada
echo.
echo PRÓXIMOS PASSOS:
echo 1. Reinicie seu computador se o Docker Desktop foi instalado
echo 2. Inicie o Docker Desktop
echo 3. Habilite o Kubernetes manualmente nas configurações
echo 4. Execute o pipeline de implantação
echo.
echo Detalhes completos em: setup-log.txt
echo.
echo Para continuar com a implantação após reiniciar:
echo smart-deploy-pipeline.bat