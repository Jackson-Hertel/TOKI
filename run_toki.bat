@echo off
title Iniciando TOKI...
color 0a

echo ===========================================
echo      TOKI - Sistema Web de Gerenciamento
echo ===========================================
echo.
echo Iniciando o servidor Back-end...
echo.

:: Abre o back-end em um novo terminal
start cmd /k "cd Back-end\Toki-backend\target && java -jar toki-backend-1.0-SNAPSHOT-jar-with-dependencies.jar"

:: Aguarda alguns segundos para o servidor subir
timeout /t 5 >nul

echo.
echo Servidor iniciado com sucesso!
echo Abrindo o site TOKI...
echo.

:: Abre o site no navegador padrão
start http://127.0.0.1:5500/login.cadastro/login.html

echo Tudo pronto! Feche esta janela se desejar.
pause >nul
