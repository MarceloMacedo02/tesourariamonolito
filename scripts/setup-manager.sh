#!/bin/bash

# Gerenciador de Setup Completo - Tesouraria

echo "====================================================="
echo " GERENCIADOR DE SETUP COMPLETO - TESOURARIA"
echo "====================================================="
echo
echo "Este gerenciador irá verificar e configurar automaticamente:"
echo "1. Docker"
echo "2. Kubernetes (minikube ou kind)"
echo "3. kubectl"
echo "4. Todos os pré-requisitos para implantação"
echo

echo "Escolha uma opção:"
echo "1. Verificar e configurar tudo automaticamente"
echo "2. Verificar apenas o setup atual"
echo "3. Instalar/configurar Docker"
echo "4. Instalar/configurar kubectl"
echo "5. Instalar/configurar minikube"
echo "6. Executar pipeline de implantação"
echo "7. Sair"
echo
echo "Opção:"
read SETUP_OPTION

case $SETUP_OPTION in
    1)
        full_setup
        ;;
    2)
        verify_setup
        ;;
    3)
        install_docker
        ;;
    4)
        install_kubectl
        ;;
    5)
        install_minikube
        ;;
    6)
        deploy_pipeline
        ;;
    *)
        echo "Saindo..."
        exit 0
        ;;
esac

full_setup() {
    echo
    echo "====================================================="
    echo "CONFIGURAÇÃO COMPLETA AUTOMÁTICA"
    echo "====================================================="

    check_and_install_docker
    if [ $? -ne 0 ]; then
        echo "[ERRO] Falha na configuração do Docker"
        exit 1
    fi

    check_and_install_kubernetes
    if [ $? -ne 0 ]; then
        echo "[ERRO] Falha na configuração do Kubernetes"
        exit 1
    fi

    check_and_install_kubectl
    if [ $? -ne 0 ]; then
        echo "[ERRO] Falha na configuração do kubectl"
        exit 1
    fi

    echo
    echo "====================================================="
    echo "TODOS OS PRÉ-REQUISITOS CONFIGURADOS!"
    echo "====================================================="
    echo
    echo "Deseja executar o pipeline de implantação agora? (S/N)"
    read RUN_DEPLOY
    if [[ $RUN_DEPLOY =~ ^[Ss]$ ]]; then
        deploy_pipeline
    else
        echo "Setup completo. Execute o pipeline quando estiver pronto."
        exit 0
    fi
}

verify_setup() {
    echo
    echo "====================================================="
    echo "VERIFICAÇÃO DE SETUP"
    echo "====================================================="
    echo "Verificando Docker..."
    if ! command -v docker &> /dev/null; then
        echo "[ERRO] Docker não encontrado"
    else
        echo "[OK] Docker encontrado"
        docker --version
    fi

    echo
    echo "Verificando kubectl..."
    if ! command -v kubectl &> /dev/null; then
        echo "[ERRO] kubectl não encontrado"
    else
        echo "[OK] kubectl encontrado"
        kubectl version --short 2>/dev/null || echo "Kubernetes cluster não acessível"
    fi

    echo
    echo "Verificando Kubernetes..."
    if command -v minikube &> /dev/null; then
        echo "[INFO] Minikube encontrado"
        minikube status
    elif command -v kind &> /dev/null; then
        echo "[INFO] Kind encontrado"
        kind get clusters
    else
        echo "[INFO] Nenhum orquestrador Kubernetes local encontrado"
    fi

    echo
    echo "Pressione Enter para continuar..."
    read
}

install_docker() {
    echo
    echo "====================================================="
    echo "INSTALAÇÃO DO DOCKER"
    echo "====================================================="
    
    if [[ "$OSTYPE" == "darwin"* ]]; then
        echo "No macOS, recomendamos instalar o Docker Desktop:"
        echo "1. Acesse https://www.docker.com/products/docker-desktop/"
        echo "2. Baixe e instale o Docker Desktop para Mac"
        echo "3. Inicie o Docker Desktop após a instalação"
    elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
        echo "Instalando Docker no Linux..."
        sudo apt-get update
        sudo apt-get install docker-ce docker-ce-cli containerd.io -y
        sudo usermod -aG docker $USER
        echo "Por favor, faça logout e login novamente para aplicar as mudanças de grupo"
    fi
    
    echo
    echo "Pressione Enter para continuar..."
    read
}

install_kubectl() {
    echo
    echo "====================================================="
    echo "INSTALAÇÃO DO KUBECTL"
    echo "====================================================="
    
    if [[ "$OSTYPE" == "darwin"* ]]; then
        echo "Instalando kubectl no macOS..."
        brew install kubectl
    elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
        echo "Instalando kubectl no Linux..."
        curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
        sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl
        rm kubectl
    fi
    
    echo
    echo "Verificando instalação..."
    if command -v kubectl &> /dev/null; then
        echo "[OK] kubectl instalado com sucesso"
        kubectl version --short 2>/dev/null || echo "Instalado, mas cluster não acessível"
    else
        echo "[ERRO] Falha na instalação do kubectl"
    fi
    
    echo
    echo "Pressione Enter para continuar..."
    read
}

install_minikube() {
    echo
    echo "====================================================="
    echo "INSTALAÇÃO DO MINIKUBE"
    echo "====================================================="
    
    if [[ "$OSTYPE" == "darwin"* ]]; then
        echo "Instalando minikube no macOS..."
        brew install minikube
    elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
        echo "Instalando minikube no Linux..."
        curl -LO https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64
        sudo install minikube-linux-amd64 /usr/local/bin/minikube
        rm minikube-linux-amd64
    fi
    
    echo
    echo "Iniciando minikube..."
    minikube start
    
    echo
    echo "Verificando instalação..."
    if command -v minikube &> /dev/null; then
        echo "[OK] minikube instalado com sucesso"
        minikube status
    else
        echo "[ERRO] Falha na instalação do minikube"
    fi
    
    echo
    echo "Pressione Enter para continuar..."
    read
}

check_and_install_docker() {
    echo "Verificando Docker..."
    if ! command -v docker &> /dev/null; then
        echo "[NECESSÁRIO] Docker não encontrado"
        install_docker
        if ! command -v docker &> /dev/null; then
            echo "[ERRO] Docker ainda não instalado"
            return 1
        fi
    else
        echo "[OK] Docker já instalado"
    fi
    return 0
}

check_and_install_kubernetes() {
    echo "Verificando Kubernetes..."
    if ! command -v minikube &> /dev/null && ! command -v kind &> /dev/null; then
        echo "[NECESSÁRIO] Kubernetes não encontrado"
        install_minikube
        if ! command -v minikube &> /dev/null && ! command -v kind &> /dev/null; then
            echo "[ERRO] Kubernetes ainda não instalado"
            return 1
        fi
    else
        echo "[OK] Kubernetes já instalado"
    fi
    return 0
}

check_and_install_kubectl() {
    echo "Verificando kubectl..."
    if ! command -v kubectl &> /dev/null; then
        echo "[NECESSÁRIO] kubectl não encontrado"
        install_kubectl
        if ! command -v kubectl &> /dev/null; then
            echo "[ERRO] kubectl ainda não disponível"
            return 1
        fi
    else
        echo "[OK] kubectl já instalado"
    fi
    return 0
}

deploy_pipeline() {
    echo
    echo "====================================================="
    echo "EXECUTANDO PIPELINE DE IMPLANTAÇÃO"
    echo "====================================================="
    chmod +x deploy-pipeline.sh
    ./deploy-pipeline.sh
}