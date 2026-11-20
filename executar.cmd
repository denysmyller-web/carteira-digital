@echo off
setlocal
cd /d "%~dp0"
rem Seleciona o JDK 25 instalado nesta maquina, sem alterar variaveis do sistema.
for /d %%J in ("%ProgramFiles%\Eclipse Adoptium\jdk-25*") do if exist "%%~J\bin\java.exe" set "JAVA_HOME=%%~J"
if "%~1"=="iniciar" goto iniciar
if "%~1"=="interface" goto interface
if "%~1"=="visivel" goto visivel
call "%~dp0mvnw.cmd" clean verify
exit /b %errorlevel%
:iniciar
call "%~dp0mvnw.cmd" spring-boot:run
exit /b %errorlevel%
:interface
call "%~dp0mvnw.cmd" -Pselenium clean verify
exit /b %errorlevel%
:visivel
call "%~dp0mvnw.cmd" -Pselenium -Dselenium.visivel=true clean verify
exit /b %errorlevel%
