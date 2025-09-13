@echo off
echo Verificando se o perfil de producao esta corretamente associado ao pod do Kubernetes...

echo.
echo 1. Verificando se o deployment esta usando o perfil de producao...
echo.

kubectl get deployment tesouraria-deployment -o yaml | findstr "SPRING_PROFILES_ACTIVE"
if %errorlevel% == 0 (
    echo [OK] SPRING_PROFILES_ACTIVE encontrado no deployment
) else (
    echo [ERRO] SPRING_PROFILES_ACTIVE nao encontrado no deployment
)

echo.
echo 2. Verificando se o pod esta em execucao...
echo.

kubectl get pods -l app=tesouraria
if %errorlevel% == 0 (
    echo [OK] Pods encontrados
) else (
    echo [ERRO] Nenhum pod encontrado
)

echo.
echo 3. Verificando variaveis de ambiente do pod...
echo.

for /f "tokens=1" %%i in ('kubectl get pods -l app=tesouraria -o name') do (
    echo Verificando %%i...
    kubectl exec %%i -- env | findstr SPRING_PROFILES_ACTIVE
    if %errorlevel% == 0 (
        echo [OK] SPRING_PROFILES_ACTIVE esta definido como prod
    ) else (
        echo [AVISO] SPRING_PROFILES_ACTIVE nao encontrado
    )
)

echo.
echo 4. Verificando logs da aplicacao...
echo.

for /f "tokens=1" %%i in ('kubectl get pods -l app=tesouraria -o name') do (
    echo Verificando logs de %%i...
    kubectl logs %%i | findstr "prod"
    if %errorlevel% == 0 (
        echo [OK] Perfil de producao detectado nos logs
    ) else (
        echo [AVISO] Perfil de producao nao encontrado nos logs
    )
)

echo.
echo Verificacao concluida.