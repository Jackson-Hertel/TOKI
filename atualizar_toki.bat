@echo off
title Atualizar TOKI no GitHub
color 0a

echo ===========================================
echo       Atualizando projeto TOKI no GitHub
echo ===========================================
echo.

:: Navegar para a pasta do projeto
cd /d C:\Projeto_TOKI

:: Pede mensagem do commit
set /p msg=Digite a mensagem do commit (ou deixe vazio para padrão): 
if "%msg%"=="" set msg=Atualização automática TOKI

:: Preparar ambiente para variáveis dentro do FOR
setlocal enabledelayedexpansion

echo.
echo Verificando arquivos grandes...
echo.

:: Filtrar arquivos maiores que 90MB
for /f "delims=" %%F in ('git ls-files') do (
    for %%I in ("%%F") do (
        set size=%%~zI
        if !size! GTR 94371840 (
            echo Ignorando arquivo grande: %%F (!size! bytes)
            git reset "%%F" >nul 2>&1
        )
    )
)

endlocal

echo.
echo Adicionando arquivos...
git add --all

echo.
echo Criando commit...
git commit -m "%msg%" 2>nul
if errorlevel 1 (
    echo Nenhuma alteracao para commitar.
) else (
    echo Commit realizado com sucesso!
)

echo.
set /p enviar=Deseja enviar para o GitHub (push)? (S/N): 
if /i "%enviar%"=="S" (

    echo.
    echo Sincronizando com o GitHub...
    git pull --rebase

    echo.
    echo Enviando...
    git push origin main

    if errorlevel 1 (
        echo ERRO ao enviar ao GitHub!
    ) else (
        echo Projeto atualizado com sucesso no GitHub!
    )
) else (
    echo Push cancelado.
)

echo.
pause
