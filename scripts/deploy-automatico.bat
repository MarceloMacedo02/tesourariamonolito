@echo off
REM Script para implantar automaticamente a aplicacao Tesouraria no Kubernetes

pushd %~dp0..

echo [INFO] Iniciando o processo de implantacao automatica da aplicacao Tesouraria...

REM Verificar se o Kubernetes esta acessivel
echo [INFO] Verificando conexao com o cluster Kubernetes...
kubectl cluster-info >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Nao foi possivel conectar ao cluster Kubernetes. Verifique se o cluster esta em execucao.
    popd
    exit /b 1
)

echo [INFO] Cluster Kubernetes esta acessivel.

REM Construir a imagem Docker
echo [INFO] Construindo a imagem Docker...
docker build -t tesouraria:latest --no-cache .
if %errorlevel% neq 0 (
    echo [ERROR] Falha ao construir a imagem Docker.
    popd
    exit /b 1
)

echo [INFO] Imagem Docker construida com sucesso.

REM Aplicar a configuracao de deployment
echo [INFO] Aplicando a configuracao de deployment...
kubectl apply -f k8s/deployment.yaml
if %errorlevel% neq 0 (
    echo [ERROR] Falha ao aplicar a configuracao de deployment.
    popd
    exit /b 1
)

echo [INFO] Deployment aplicado com sucesso.

REM Aguardar o pod estar pronto
echo [INFO] Aguardando o pod estar pronto...
:loop
kubectl get pods -l app=tesouraria | findstr "Running" >nul
if %errorlevel% equ 0 (
    echo [INFO] Pod esta em execucao.
    goto :deployed
) else (
    echo [INFO] Aguardando o pod iniciar... (tentando novamente em 5 segundos)
    timeout /t 5 /nobreak >nul
    goto :loop
)

:deployed
echo [INFO] Aplicacao Tesouraria implantada com sucesso!
echo [INFO] A aplicacao estara disponivel em http://localhost:8080 assim que o servico for iniciado.

REM Exibir informacoes do servico
echo [INFO] Informacoes do servico:
kubectl get service tesouraria-service

echo [INFO] Processo de implantacao automatica concluido.

popd