@echo off
title Limpeza de Ambiente - Tesouraria

echo =====================================================
echo  LIMPEZA DE AMBIENTE - TESOURARIA
echo =====================================================
echo.
echo Este script irá remover todos os recursos da aplicação do Kubernetes.
echo.

echo Tem certeza que deseja continuar? (S/N)
set /p CONFIRM=
if /i not "%CONFIRM%"=="S" (
    echo Operação cancelada.
    exit /b 0
)

echo.
echo =====================================================
echo REMOVENDO RECURSOS DA APLICAÇÃO
echo =====================================================

echo Removendo deployment da aplicação...
kubectl delete -f k8s-deployment.yaml 2>nul
if %errorlevel% equ 0 (
    echo [OK] Deployment da aplicação removido
) else (
    echo [INFO] Deployment da aplicação não encontrado ou já removido
)

echo Removendo service da aplicação...
kubectl delete -f k8s-service.yaml 2>nul
if %errorlevel% equ 0 (
    echo [OK] Service da aplicação removido
) else (
    echo [INFO] Service da aplicação não encontrado ou já removido
)

echo Removendo serviço PostgreSQL...
kubectl delete -f k8s-postgresql-service.yaml 2>nul
if %errorlevel% equ 0 (
    echo [OK] Serviço PostgreSQL removido
) else (
    echo [INFO] Serviço PostgreSQL não encontrado ou já removido
)

echo Removendo Redis...
kubectl delete -f k8s-redis.yaml 2>nul
if %errorlevel% equ 0 (
    echo [OK] Redis removido
) else (
    echo [INFO] Redis não encontrado ou já removido
)

echo Removendo Prometheus...
kubectl delete -f k8s-prometheus.yaml 2>nul
if %errorlevel% equ 0 (
    echo [OK] Prometheus removido
) else (
    echo [INFO] Prometheus não encontrado ou já removido
)

echo Removendo Grafana...
kubectl delete -f k8s-grafana.yaml 2>nul
if %errorlevel% equ 0 (
    echo [OK] Grafana removido
) else (
    echo [INFO] Grafana não encontrado ou já removido
)

echo Removendo Jaeger...
kubectl delete -f k8s-jaeger.yaml 2>nul
if %errorlevel% equ 0 (
    echo [OK] Jaeger removido
) else (
    echo [INFO] Jaeger não encontrado ou já removido
)

echo.
echo =====================================================
echo VERIFICANDO REMOÇÃO
echo =====================================================
echo Verificando pods restantes...
kubectl get pods -l app=tesouraria 2>nul | findstr "tesouraria"
if %errorlevel% neq 0 (
    echo [OK] Nenhum pod da aplicação encontrado
) else (
    echo [INFO] Ainda existem pods da aplicação em execução
    echo Forçando remoção de pods...
    kubectl delete pods -l app=tesouraria --force --grace-period=0 2>nul
)

echo.
echo =====================================================
echo LIMPANDO IMAGENS DOCKER (OPCIONAL)
echo =====================================================
echo Deseja remover a imagem Docker da aplicação? (S/N)
set /p REMOVE_IMAGE=
if /i "%REMOVE_IMAGE%"=="S" (
    echo Removendo imagem Docker...
    docker rmi tesouraria 2>nul
    if %errorlevel% equ 0 (
        echo [OK] Imagem Docker removida
    ) else (
        echo [INFO] Imagem Docker não encontrada ou já removida
    )
) else (
    echo Imagem Docker mantida
)

echo.
echo =====================================================
echo LIMPEZA CONCLUÍDA
echo =====================================================
echo.
echo Todos os recursos da aplicação foram removidos do Kubernetes.
echo.
echo Para reinstalar, execute:
echo deploy-pipeline.bat
echo.
echo Para verificar o status atual do cluster:
echo kubectl get all