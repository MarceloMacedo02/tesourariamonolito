#!/bin/bash

# Pipeline de Implantação Completa - Tesouraria

echo "====================================================="
echo " PIPELINE DE IMPLANTAÇÃO COMPLETA - TESOURARIA"
echo "====================================================="
echo
echo "Este script irá executar toda a pipeline de implantação:"
echo "1. Verificar pré-requisitos"
echo "2. Construir a imagem Docker da aplicação"
echo "3. Implantar serviços de apoio (PostgreSQL, Redis)"
echo "4. Implantar a aplicação com perfil de produção"
echo "5. Implantar ferramentas de monitoramento (Prometheus, Grafana, Jaeger)"
echo

read -p "Pressione Enter para continuar ou Ctrl+C para cancelar..."

echo
echo "====================================================="
echo "1. VERIFICANDO PRÉ-REQUISITOS"
echo "====================================================="

echo "Verificando se Docker está instalado..."
if ! command -v docker &> /dev/null
then
    echo "[ERRO] Docker não está instalado ou não está no PATH"
    echo "Por favor, instale o Docker e tente novamente"
    exit 1
else
    echo "[OK] Docker encontrado"
fi

echo "Verificando se Kubernetes está habilitado..."
if ! command -v kubectl &> /dev/null
then
    echo "[ERRO] kubectl não está instalado ou não está no PATH"
    echo "Por favor, instale kubectl e tente novamente"
    exit 1
else
    echo "[OK] kubectl encontrado"
fi

# Verificar se o cluster Kubernetes está acessível
if ! kubectl cluster-info &> /dev/null
then
    echo "[ERRO] Kubernetes não está acessível"
    echo "Por favor, verifique sua configuração do Kubernetes"
    exit 1
else
    echo "[OK] Cluster Kubernetes acessível"
fi

echo "Verificando arquivos necessários..."
if [ ! -f "Dockerfile" ]; then
    echo "[ERRO] Dockerfile não encontrado"
    exit 1
else
    echo "[OK] Dockerfile encontrado"
fi

if [ ! -f "k8s-deployment.yaml" ]; then
    echo "[ERRO] k8s-deployment.yaml não encontrado"
    exit 1
else
    echo "[OK] k8s-deployment.yaml encontrado"
fi

if [ ! -f "k8s-service.yaml" ]; then
    echo "[ERRO] k8s-service.yaml não encontrado"
    exit 1
else
    echo "[OK] k8s-service.yaml encontrado"
fi

if [ ! -f "k8s-postgresql-service.yaml" ]; then
    echo "[ERRO] k8s-postgresql-service.yaml não encontrado"
    exit 1
else
    echo "[OK] k8s-postgresql-service.yaml encontrado"
fi

if [ ! -f "k8s-redis.yaml" ]; then
    echo "[ERRO] k8s-redis.yaml não encontrado"
    exit 1
else
    echo "[OK] k8s-redis.yaml encontrado"
fi

if [ ! -f "k8s-prometheus.yaml" ]; then
    echo "[ERRO] k8s-prometheus.yaml não encontrado"
    exit 1
else
    echo "[OK] k8s-prometheus.yaml encontrado"
fi

if [ ! -f "k8s-grafana.yaml" ]; then
    echo "[ERRO] k8s-grafana.yaml não encontrado"
    exit 1
else
    echo "[OK] k8s-grafana.yaml encontrado"
fi

if [ ! -f "k8s-jaeger.yaml" ]; then
    echo "[ERRO] k8s-jaeger.yaml não encontrado"
    exit 1
else
    echo "[OK] k8s-jaeger.yaml encontrado"
fi

echo
echo "====================================================="
echo "2. CONSTRUINDO IMAGEM DOCKER DA APLICAÇÃO"
echo "====================================================="

echo "Construindo imagem Docker..."
docker build -t tesouraria .
if [ $? -ne 0 ]; then
    echo "[ERRO] Falha ao construir a imagem Docker"
    exit 1
else
    echo "[OK] Imagem Docker construída com sucesso"
fi

echo
echo "====================================================="
echo "3. IMPLANTANDO SERVIÇOS DE APOIO"
echo "====================================================="

echo "Implantando serviço PostgreSQL..."
kubectl apply -f k8s-postgresql-service.yaml
if [ $? -ne 0 ]; then
    echo "[ERRO] Falha ao implantar serviço PostgreSQL"
    exit 1
else
    echo "[OK] Serviço PostgreSQL implantado"
fi

echo "Implantando Redis..."
kubectl apply -f k8s-redis.yaml
if [ $? -ne 0 ]; then
    echo "[ERRO] Falha ao implantar Redis"
    exit 1
else
    echo "[OK] Redis implantado"
fi

echo
echo "====================================================="
echo "4. IMPLANTANDO APLICAÇÃO COM PERFIL DE PRODUÇÃO"
echo "====================================================="

echo "Implantando aplicação..."
kubectl apply -f k8s-deployment.yaml
if [ $? -ne 0 ]; then
    echo "[ERRO] Falha ao implantar a aplicação"
    exit 1
else
    echo "[OK] Aplicação implantada"
fi

kubectl apply -f k8s-service.yaml
if [ $? -ne 0 ]; then
    echo "[ERRO] Falha ao implantar o serviço da aplicação"
    exit 1
else
    echo "[OK] Serviço da aplicação implantado"
fi

echo
echo "====================================================="
echo "5. IMPLANTANDO FERRAMENTAS DE MONITORAMENTO"
echo "====================================================="

echo "Implantando Prometheus..."
kubectl apply -f k8s-prometheus.yaml
if [ $? -ne 0 ]; then
    echo "[ERRO] Falha ao implantar Prometheus"
    exit 1
else
    echo "[OK] Prometheus implantado"
fi

echo "Implantando Grafana..."
kubectl apply -f k8s-grafana.yaml
if [ $? -ne 0 ]; then
    echo "[ERRO] Falha ao implantar Grafana"
    exit 1
else
    echo "[OK] Grafana implantado"
fi

echo "Implantando Jaeger..."
kubectl apply -f k8s-jaeger.yaml
if [ $? -ne 0 ]; then
    echo "[ERRO] Falha ao implantar Jaeger"
    exit 1
else
    echo "[OK] Jaeger implantado"
fi

echo
echo "====================================================="
echo "6. AGUARDANDO PODS FICAREM PRONTOS"
echo "====================================================="

while true; do
    echo "Verificando status dos pods..."
    kubectl get pods
    echo
    echo "Aguardando todos os pods ficarem prontos... (Pressione Ctrl+C para cancelar)"
    sleep 10
    
    if ! kubectl get pods | grep -E "(ContainerCreating|Pending)" &> /dev/null; then
        break
    fi
done

echo
echo "====================================================="
echo "7. RESUMO DA IMPLANTAÇÃO"
echo "====================================================="

echo "Status dos deployments:"
kubectl get deployments
echo

echo "Status dos serviços:"
kubectl get services
echo

echo "====================================================="
echo "IMPLANTAÇÃO CONCLUÍDA COM SUCESSO!"
echo "====================================================="
echo
echo "A aplicação está rodando no Kubernetes com:"
echo "- Perfil de produção ativo"
echo "- Monitoramento completo (Prometheus, Grafana, Jaeger)"
echo "- Serviços de apoio (PostgreSQL, Redis)"
echo
echo "Acesse os serviços nos seguintes endereços:"
echo
echo "Aplicação Tesouraria:    http://localhost:8080"
echo "Prometheus:              http://localhost:9090"
echo "Grafana:                 http://localhost:3000  (usuário: admin, senha: admin)"
echo "Jaeger:                  http://localhost:16686"
echo
echo "Para verificar os logs da aplicação:"
echo "kubectl logs deployment/tesouraria-deployment"
echo
echo "Para verificar o status dos pods:"
echo "kubectl get pods"
echo
echo "Pipeline de implantação concluída!"