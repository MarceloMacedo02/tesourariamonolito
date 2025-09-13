@echo off
title Instalador Silencioso do Docker Desktop

echo =====================================================
echo  INSTALADOR SILENCIOSO DO DOCKER DESKTOP
echo =====================================================
echo.

echo Verificando se Docker Desktop já está instalado...
docker --version >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] Docker Desktop já está instalado
    docker --version
    echo.
    echo Nenhuma ação necessária.
    exit /b 0
)

echo [INFORMAÇÃO] Docker Desktop não encontrado. Iniciando instalação silenciosa...

echo.
echo Detectando arquitetura do sistema...
if "%PROCESSOR_ARCHITECTURE%"=="AMD64" (
    echo [OK] Arquitetura x64 detectada
    set ARCH=amd64
) else (
    echo [ERRO] Arquitetura não suportada
    exit /b 1
)

echo.
echo Baixando Docker Desktop...
set DOWNLOAD_URL=https://desktop.docker.com/win/main/amd64/Docker%20Desktop%20Installer.exe
echo URL de download: %DOWNLOAD_URL%

rem Create temp directory
set TEMP_DIR=%TEMP%\docker_install
if not exist "%TEMP_DIR%" mkdir "%TEMP_DIR%"
cd /d "%TEMP_DIR%"

echo.
echo Iniciando download (isso pode levar alguns minutos)...
powershell -Command "Invoke-WebRequest -Uri '%DOWNLOAD_URL%' -OutFile 'Docker Desktop Installer.exe'"
if %errorlevel% neq 0 (
    echo [ERRO] Falha ao baixar Docker Desktop
    exit /b 1
)

if not exist "Docker Desktop Installer.exe" (
    echo [ERRO] Instalador do Docker Desktop não encontrado após download
    exit /b 1
)

echo [OK] Docker Desktop baixado com sucesso

echo.
echo Instalando Docker Desktop silenciosamente...
echo Esta instalação pode levar vários minutos. Por favor, aguarde...
echo Não feche esta janela até a conclusão.

"Docker Desktop Installer.exe" install --quiet --accept-license
if %errorlevel% neq 0 (
    echo [ERRO] Falha na instalação silenciosa do Docker Desktop
    echo Código de erro: %errorlevel%
    exit /b 1
)

echo [OK] Docker Desktop instalado com sucesso

echo.
echo Verificando instalação...
timeout /t 10 /nobreak >nul
docker --version >nul 2>&1
if %errorlevel% equ 0 (
    echo [SUCESSO] Docker Desktop instalado e funcionando corretamente!
    docker --version
) else (
    echo [AVISO] Docker Desktop foi instalado, mas pode não estar acessível imediatamente
    echo Você precisará reiniciar o computador para completar a instalação
)

echo.
echo Limpando arquivos temporários...
cd /d "%TEMP%"
rmdir /s /q "%TEMP_DIR%" >nul 2>&1

echo.
echo =====================================================
echo INSTALAÇÃO SILENCIOSA CONCLUÍDA
echo =====================================================
echo.
echo Docker Desktop foi instalado automaticamente!
echo.
echo PASSOS IMPORTANTES:
echo 1. Reinicie seu computador para completar a instalação
echo 2. Após reiniciar, inicie o Docker Desktop manualmente
echo 3. Habilite o Kubernetes nas configurações do Docker Desktop
echo.
echo A instalação silenciosa não pode habilitar o Kubernetes automaticamente
echo devido a restrições de segurança do Windows.
exit /b 0