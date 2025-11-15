@echo off
title Iniciando TOKI...
color 0a

:: ======================================================
:: TOKI - Sistema Web de Gerenciamento (Versão Portátil)
:: ======================================================

:: Define o diretório base (mesmo local do .bat)
set "BASE_DIR=%~dp0"

:: Configura o Java portátil local
set "JAVA_HOME=%BASE_DIR%jdk-25"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo ===========================================
echo       TOKI - Sistema Web de Gerenciamento
echo ===========================================
echo.
echo Verificando Java portátil em: %JAVA_HOME%
echo.

:: Caminho do servidor e do site
set "SERVER_PATH=%BASE_DIR%Back-end\Toki-backend\target"
set "SITE_URL=http://localhost:8080/login_cadastro/login.html"

:: Testa se o Java existe
if not exist "%JAVA_HOME%\bin\java.exe" (
    echo ERRO: Java não encontrado em %JAVA_HOME%
    echo Verifique se a pasta jdk-25 está dentro de %BASE_DIR%
    pause
    exit /b
)

:: Testa se o arquivo JAR existe
if not exist "%SERVER_PATH%\toki-backend-1.0-SNAPSHOT-jar-with-dependencies.jar" (
    echo ERRO: Arquivo JAR não encontrado!
    echo Verifique se o projeto foi compilado corretamente.
    pause
    exit /b
)

:: Pergunta se deseja limpar o cache
set /p limpar_cache=Deseja limpar cache dos navegadores? (S/N): 
if /i "%limpar_cache%"=="S" (
    echo Limpando cache dos navegadores...
    
    :: Microsoft Edge
    if exist "%LOCALAPPDATA%\Microsoft\Edge\User Data\Default\Cache" (
        rmdir /s /q "%LOCALAPPDATA%\Microsoft\Edge\User Data\Default\Cache"
    )

    :: Google Chrome
    if exist "%LOCALAPPDATA%\Google\Chrome\User Data\Default\Cache" (
        rmdir /s /q "%LOCALAPPDATA%\Google\Chrome\User Data\Default\Cache"
    )

    :: Opera GX
    if exist "%APPDATA%\Opera Software\Opera GX Stable\Cache" (
        rmdir /s /q "%APPDATA%\Opera Software\Opera GX Stable\Cache"
    )
    echo Cache limpo com sucesso!
    echo.
)

:: Inicia o servidor em uma nova janela
echo Iniciando o servidor Back-end...
start "TOKI Servidor" /B "%JAVA_HOME%\bin\java.exe" -jar "%SERVER_PATH%\toki-backend-1.0-SNAPSHOT-jar-with-dependencies.jar"

:: Aguarda o servidor ficar disponível
echo Aguardando o servidor subir...
:CHECK_SERVER
timeout /t 1 >nul
powershell -Command "$r = try { iwr -UseBasicParsing -TimeoutSec 1 http://localhost:8080 -ErrorAction Stop; $true } catch { $false }; exit [int](!$r)"
if errorlevel 1 (
    goto CHECK_SERVER
)

echo Servidor iniciado com sucesso!
echo Abrindo o site TOKI...
start "" "%SITE_URL%"

echo.
echo ===========================================
echo TOKI iniciado com sucesso!
echo Feche esta janela se desejar encerrar o script.
echo ===========================================
pause >nul


