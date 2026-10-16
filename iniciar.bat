@echo off
title StackBerry - Sistema de Gerenciamento para Acaiterias
echo ===================================================
echo           Iniciando Servidor StackBerry...
echo ===================================================
echo.
timeout /t 2 >nul
start http://localhost:7070
java -jar "%~dp0target\s_berry-1.0-jar-with-dependencies.jar"
pause
