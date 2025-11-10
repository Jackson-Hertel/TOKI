@echo off
title Atualizar TOKI no GitHub
color 0a

echo ===========================================
echo      Atualizando projeto TOKI no GitHub
echo ===========================================
echo.

:: Navegar para a pasta do projeto (modifique se necessário)
cd /d C:\Projeto_TOKI

:: Pedir mensagem do commit
set /p msg=Digite a mensagem do commit: 

:: Adicionar todos os arquivos
git add .

:: Fazer commit
git commit -m "%msg%"

:: Enviar para o GitHub
git push origin main

echo.
echo Projeto atualizado com sucesso!
pause
