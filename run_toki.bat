@echo off
title Iniciando TOKI...
color 0a

echo ===========================================
echo      TOKI - Sistema Web de Gerenciamento
echo ===========================================
echo.

:: Caminho do servidor e do site
set SERVER_PATH=C:\Projeto_TOKI\Back-end\Toki-backend\target
set SITE_URL=http://localhost:8080/login_cadastro/login.html

:: Limpeza de cache
echo Limpando cache dos navegadores...
echo.

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

:: Inicia o servidor
echo Iniciando o servidor Back-end...
cd /d "%SERVER_PATH%"
start cmd /k "java -jar toki-backend-1.0-SNAPSHOT-jar-with-dependencies.jar"

:: Aguarda o servidor subir
timeout /t 5 >nul

echo.
echo Servidor iniciado com sucesso!
echo Abrindo o site TOKI...
echo.

:: Abre o site no navegador padrão (pode ser Opera GX se for o padrão)
start %SITE_URL%

echo Tudo pronto! Feche esta janela se desejar.
pause >nul
