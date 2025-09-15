#!/bin/bash

# Script para implantar automaticamente a aplicacao Tesouraria no Kubernetes

echo "[INFO] Iniciando o processo de implantacao automatica da aplicacao Tesouraria..."

# Verificar se o Kubernetes esta acessivel
echo "[INFO] Verificando conexao com o cluster Kubernetes..."
if ! kubectl cluster-info > /dev/null 2>&1; then
    echo "[ERROR] Nao foi possivel conectar ao cluster Kubernetes. Verifique se o cluster esta em execucao."
    exit 1
fi

echo "[INFO] Cluster Kubernetes esta acessivel."

# Construir a imagem Docker
echo "[INFO] Construindo a imagem Docker..."
if ! docker build -t tesouraria:latest -f ../Dockerfile ..; then
    echo "[ERROR] Falha ao construir a imagem Docker."
    exit 1
fi

echo "[INFO] Imagem Docker construida com sucesso."

# Aplicar a configuracao de deployment
echo "[INFO] Aplicando a configuracao de deployment..."
if ! kubectl apply -f ../k8s/deployment.yaml; then
    echo "[ERROR] Falha ao aplicar a configuracao de deployment."
    exit 1
fi

echo "[INFO] Deployment aplicado com sucesso."

# Aguardar o pod estar pronto
echo "[INFO] Aguardando o pod estar pronto..."
while true; do
    if kubectl get pods -l app=tesouraria | grep -q "Running"; then
        echo "[INFO] Pod esta em execucao."
        break
    else
        echo "[INFO] Aguardando o pod iniciar... (tentando novamente em 5 segundos)"
        sleep 5
    fi
done

echo "[INFO] Aplicacao Tesouraria implantada com sucesso!"
echo "[INFO] A aplicacao estara disponivel em http://localhost:8080 assim que o servico for iniciado."

# Exibir informacoes do servico
echo "[INFO] Informacoes do servico:"
kubectl get service tesouraria-service

echo "[INFO] Processo de implantacao automatica concluido."