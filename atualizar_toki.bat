@echo off
title Atualizar TOKI no GitHub
color 0a

echo ===========================================
echo       Atualizando projeto TOKI no GitHub
echo ===========================================
echo.

:: Navegar para a pasta do projeto
cd /d C:\Projeto_TOKI

:: Pedir mensagem do commit
set /p msg=Digite a mensagem do commit (ou deixe vazio para padrão): 
if "%msg%"=="" set msg=Atualização automática TOKI

:: Filtrar arquivos grandes (maiores que 90MB)
for /f "delims=" %%F in ('git ls-files') do (
    for %%I in ("%%F") do (
        set "size=%%~zI"
        setlocal enabledelayedexpansion
        if !size! GTR 94371840 (
            echo Ignorando arquivo grande: %%F (!size! bytes)
            git reset HEAD "%%F" >nul 2>&1
        )
        endlocal
    )
)

:: Adicionar todos os arquivos restantes
git add --all

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

