@echo off
setlocal
cd /d "%~dp0"

if not exist "out" mkdir out

javac -encoding UTF-8 -cp "lib\mysql-connector-j-9.6.0.jar" -d out src\com\defender\config\*.java
if errorlevel 1 exit /b 1

java -cp "out;lib\mysql-connector-j-9.6.0.jar" com.defender.config.Main
