@echo off
title Diagnóstico de Implantação - Tesouraria

echo =====================================================
echo  DIAGNÓSTICO DE IMPLANTAÇÃO - TESOURARIA
echo =====================================================
echo.

echo =====================================================
echo 1. VERIFICANDO STATUS DO CLUSTER
echo =====================================================
echo Verificando cluster-info...
kubectl cluster-info
if %errorlevel% neq 0 (
    echo [ERRO] Não foi possível conectar ao cluster Kubernetes
    exit /b 1
)
echo.

echo Verificando nodes...
kubectl get nodes
echo.

echo =====================================================
echo 2. VERIFICANDO RECURSOS DO SISTEMA
echo =====================================================
echo Verificando todos os pods...
kubectl get pods --all-namespaces
echo.

echo Verificando deployments...
kubectl get deployments --all-namespaces
echo.

echo Verificando services...
kubectl get services --all-namespaces
echo.

echo =====================================================
echo 3. VERIFICANDO RECURSOS DA APLICAÇÃO
echo =====================================================
echo Verificando pods da aplicação...
kubectl get pods -l app=tesouraria
echo.

echo Verificando deployment da aplicação...
kubectl get deployment tesouraria-deployment
echo.

echo Verificando service da aplicação...
kubectl get service tesouraria-service
echo.

echo =====================================================
echo 4. VERIFICANDO PODS COM PROBLEMAS
echo =====================================================
echo Pods com estado diferente de Running:
kubectl get pods --field-selector=status.phase!=Running
echo.

echo Pods em ContainerCreating:
kubectl get pods | findstr "ContainerCreating"
echo.

echo Pods em Pending:
kubectl get pods | findstr "Pending"
echo.

echo Pods em CrashLoopBackOff:
kubectl get pods | findstr "CrashLoopBackOff"
echo.

echo =====================================================
echo 5. VERIFICANDO EVENTOS RECENTES
echo =====================================================
echo Eventos mais recentes:
kubectl get events --sort-by=.metadata.creationTimestamp | findstr -v "Normal" | findstr -v "Successfully"
echo.

echo =====================================================
echo 6. VERIFICANDO LOGS DA APLICAÇÃO
echo =====================================================
echo Obtendo nome do pod da aplicação...
for /f "tokens=1" %%i in ('kubectl get pods -l app=tesouraria -o name') do set APP_POD=%%i

if defined APP_POD (
    echo Verificando logs do pod %APP_POD%...
    echo --- Últimas 20 linhas de log ---
    kubectl logs %APP_POD% --tail=20
    echo.
    echo --- Verificando se há erros nos logs ---
    kubectl logs %APP_POD% | findstr "ERROR\|Exception\|error\|exception"
    if %errorlevel% equ 0 (
        echo [AVISO] Foram encontrados erros nos logs
    ) else (
        echo [OK] Nenhum erro evidente encontrado nos logs
    )
) else (
    echo [AVISO] Nenhum pod da aplicação encontrado
)
echo.

echo =====================================================
echo 7. VERIFICANDO DESCRIÇÃO DETALHADA
echo =====================================================
if defined APP_POD (
    echo Descrição detalhada do pod %APP_POD%...
    kubectl describe pod %APP_POD% | findstr -v "Normal"
) else (
    echo Não foi possível verificar a descrição detalhada - pod não encontrado
)
echo.

echo =====================================================
echo DIAGNÓSTICO CONCLUÍDO
echo =====================================================
echo.
echo Para resolver problemas:
echo 1. Consulte docs\TROUBLESHOOTING_PIPELINE.md
echo 2. Verifique os logs acima para identificar erros específicos
echo 3. Use "kubectl describe" para mais detalhes dos recursos com problemas