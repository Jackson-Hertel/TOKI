@echo off
title Atualizar TOKI no GitHub
color 0a

echo ===========================================
echo       Atualizando projeto TOKI no GitHub
echo ===========================================
echo.

:: Navegar para a pasta do projeto (modifique se necessário)
cd /d C:\Projeto_TOKI

:: Pedir mensagem do commit
set /p msg=Digite a mensagem do commit (ou deixe vazio para padrão): 

:: Usar mensagem padrão se estiver vazio
if "%msg%"=="" set msg=Atualização automática TOKI

:: Adicionar todos os arquivos
git add .

:: Fazer commit (evita erro se não houver alterações)
git commit -m "%msg%" 2>nul
if errorlevel 1 (
    echo Nenhuma alteração para commitar.
) else (
    echo Commit realizado com sucesso!
)

:: Perguntar antes de enviar para o GitHub
set /p enviar=Deseja enviar para o GitHub? (S/N): 
if /i "%enviar%"=="S" (
    git push origin main
    if errorlevel 1 (
        echo Erro ao enviar para o GitHub.
    ) else (
        echo Projeto atualizado com sucesso no GitHub!
    )
) else (
    echo Operação de push cancelada pelo usuário.
)

echo.
pause
