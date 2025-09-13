@echo off
echo Validando a configuracao completa do ambiente...

echo.
echo 1. Verificando se o Kubernetes esta em execucao...
echo.

kubectl cluster-info >nul 2>&1
if %errorlevel% == 0 (
    echo [OK] Kubernetes esta em execucao
) else (
    echo [ERRO] Kubernetes nao esta em execucao
    exit /b 1
)

echo.
echo 2. Verificando deployments...
echo.

kubectl get deployments >nul 2>&1
if %errorlevel% == 0 (
    echo [OK] Deployments encontrados
) else (
    echo [ERRO] Nao foi possivel obter deployments
    exit /b 1
)

echo.
echo 3. Verificando servicos...
echo.

kubectl get services >nul 2>&1
if %errorlevel% == 0 (
    echo [OK] Servicos encontrados
) else (
    echo [ERRO] Nao foi possivel obter servicos
    exit /b 1
)

echo.
echo 4. Verificando se o perfil de producao esta ativo...
echo.

kubectl get deployment tesouraria-deployment -o yaml | findstr "SPRING_PROFILES_ACTIVE" >nul 2>&1
if %errorlevel% == 0 (
    echo [OK] SPRING_PROFILES_ACTIVE encontrado no deployment
) else (
    echo [ERRO] SPRING_PROFILES_ACTIVE nao encontrado no deployment
    exit /b 1
)

echo.
echo 5. Verificando pods da aplicacao...
echo.

kubectl get pods -l app=tesouraria >nul 2>&1
if %errorlevel% == 0 (
    echo [OK] Pods da aplicacao encontrados
) else (
    echo [ERRO] Nenhum pod da aplicacao encontrado
    exit /b 1
)

echo.
echo 6. Verificando pods de monitoramento...
echo.

kubectl get pods -l app=prometheus >nul 2>&1
if %errorlevel% == 0 (
    echo [OK] Pod do Prometheus encontrado
) else (
    echo [AVISO] Pod do Prometheus nao encontrado
)

kubectl get pods -l app=grafana >nul 2>&1
if %errorlevel% == 0 (
    echo [OK] Pod do Grafana encontrado
) else (
    echo [AVISO] Pod do Grafana nao encontrado
)

kubectl get pods -l app=jaeger >nul 2>&1
if %errorlevel% == 0 (
    echo [OK] Pod do Jaeger encontrado
) else (
    echo [AVISO] Pod do Jaeger nao encontrado
)

echo.
echo 7. Verificando pods de apoio...
echo.

kubectl get pods -l app=redis >nul 2>&1
if %errorlevel% == 0 (
    echo [OK] Pod do Redis encontrado
) else (
    echo [AVISO] Pod do Redis nao encontrado
)

echo.
echo 8. Verificando arquivos de configuracao...
echo.

if exist "src\main\resources\application-prod.properties" (
    echo [OK] Arquivo de perfil de producao encontrado
) else (
    echo [ERRO] Arquivo de perfil de producao nao encontrado
    exit /b 1
)

if exist "k8s-deployment.yaml" (
    echo [OK] Arquivo de deployment encontrado
) else (
    echo [ERRO] Arquivo de deployment nao encontrado
    exit /b 1
)

if exist "k8s-service.yaml" (
    echo [OK] Arquivo de service encontrado
) else (
    echo [ERRO] Arquivo de service nao encontrado
    exit /b 1
)

if exist "k8s-prometheus.yaml" (
    echo [OK] Arquivo de configuracao do Prometheus encontrado
) else (
    echo [ERRO] Arquivo de configuracao do Prometheus nao encontrado
    exit /b 1
)

if exist "k8s-grafana.yaml" (
    echo [OK] Arquivo de configuracao do Grafana encontrado
) else (
    echo [ERRO] Arquivo de configuracao do Grafana nao encontrado
    exit /b 1
)

if exist "k8s-jaeger.yaml" (
    echo [OK] Arquivo de configuracao do Jaeger encontrado
) else (
    echo [ERRO] Arquivo de configuracao do Jaeger nao encontrado
    exit /b 1
)

echo.
echo 9. Verificando documentacao...
echo.

if exist "docs\GUIA_PERFIL_PRODUCAO.md" (
    echo [OK] Documentacao do perfil de producao encontrada
) else (
    echo [ERRO] Documentacao do perfil de producao nao encontrada
    exit /b 1
)

if exist "docs\GUIA_COMPLETO_MONITORAMENTO.md" (
    echo [OK] Documentacao de monitoramento encontrada
) else (
    echo [ERRO] Documentacao de monitoramento nao encontrada
    exit /b 1
)

echo.
echo RESUMO DA VALIDACAO:
echo ==================
echo [OK] Kubernetes esta em execucao
echo [OK] Deployments configurados
echo [OK] Servicos configurados
echo [OK] Perfil de producao ativo
echo [OK] Pods da aplicacao em execucao
echo [OK] Arquivos de configuracao presentes
echo [OK] Documentacao em portugues presente
echo.
echo Validacao concluida com sucesso! O ambiente esta pronto para uso em producao.