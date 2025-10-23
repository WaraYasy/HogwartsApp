@echo off
REM ============================================
REM Script para crear VERSION PORTABLE en Windows
REM ============================================

echo =========================================
echo   HogwartsApp - Crear Version Portable
echo =========================================

REM 1. Compilar proyecto
echo.
echo [1/4] Compilando proyecto...
call mvn clean package -DskipTests

if %errorlevel% neq 0 (
    echo ERROR al compilar
    pause
    exit /b 1
)

echo.
echo [2/4] Creando estructura portable...
if exist portable\HogwartsApp-Portable rmdir /s /q portable\HogwartsApp-Portable
mkdir portable\HogwartsApp-Portable
mkdir portable\HogwartsApp-Portable\logs

REM 3. Copiar archivos
echo.
echo [3/4] Copiando archivos...
copy target\hogwartsApp-1.0-SNAPSHOT.jar portable\HogwartsApp-Portable\hogwartsApp.jar
xcopy /E /I target\libs portable\HogwartsApp-Portable\libs

REM 4. Crear scripts de ejecucion
echo.
echo [4/4] Creando scripts de ejecucion...

REM Script Windows
echo @echo off > portable\HogwartsApp-Portable\ejecutar.bat
echo echo ========================================= >> portable\HogwartsApp-Portable\ejecutar.bat
echo echo    HogwartsApp - Version Portable >> portable\HogwartsApp-Portable\ejecutar.bat
echo echo ========================================= >> portable\HogwartsApp-Portable\ejecutar.bat
echo echo. >> portable\HogwartsApp-Portable\ejecutar.bat
echo java -version ^>nul 2^>^&1 >> portable\HogwartsApp-Portable\ejecutar.bat
echo if %%errorlevel%% neq 0 ( >> portable\HogwartsApp-Portable\ejecutar.bat
echo     echo ERROR: Java no esta instalado >> portable\HogwartsApp-Portable\ejecutar.bat
echo     echo Descarga Java 21+ desde: https://adoptium.net/ >> portable\HogwartsApp-Portable\ejecutar.bat
echo     pause >> portable\HogwartsApp-Portable\ejecutar.bat
echo     exit /b 1 >> portable\HogwartsApp-Portable\ejecutar.bat
echo ^) >> portable\HogwartsApp-Portable\ejecutar.bat
echo echo Java encontrado >> portable\HogwartsApp-Portable\ejecutar.bat
echo java -version 2^>^&1 ^| findstr "version" >> portable\HogwartsApp-Portable\ejecutar.bat
echo echo. >> portable\HogwartsApp-Portable\ejecutar.bat
echo echo Iniciando HogwartsApp... >> portable\HogwartsApp-Portable\ejecutar.bat
echo java --enable-native-access=ALL-UNNAMED -jar hogwartsApp.jar >> portable\HogwartsApp-Portable\ejecutar.bat
echo pause >> portable\HogwartsApp-Portable\ejecutar.bat

REM Script Linux/Mac
echo #!/bin/bash > portable\HogwartsApp-Portable\ejecutar.sh
echo echo "=========================================" >> portable\HogwartsApp-Portable\ejecutar.sh
echo echo "    HogwartsApp - Version Portable" >> portable\HogwartsApp-Portable\ejecutar.sh
echo echo "=========================================" >> portable\HogwartsApp-Portable\ejecutar.sh
echo echo "" >> portable\HogwartsApp-Portable\ejecutar.sh
echo if ! command -v java ^&^> /dev/null; then >> portable\HogwartsApp-Portable\ejecutar.sh
echo     echo "ERROR: Java no esta instalado" >> portable\HogwartsApp-Portable\ejecutar.sh
echo     echo "Descarga Java 21+ desde: https://adoptium.net/" >> portable\HogwartsApp-Portable\ejecutar.sh
echo     exit 1 >> portable\HogwartsApp-Portable\ejecutar.sh
echo fi >> portable\HogwartsApp-Portable\ejecutar.sh
echo echo "Java encontrado:" >> portable\HogwartsApp-Portable\ejecutar.sh
echo java -version 2^>^&1 ^| head -1 >> portable\HogwartsApp-Portable\ejecutar.sh
echo echo "" >> portable\HogwartsApp-Portable\ejecutar.sh
echo echo "Iniciando HogwartsApp..." >> portable\HogwartsApp-Portable\ejecutar.sh
echo java --enable-native-access=ALL-UNNAMED -jar hogwartsApp.jar >> portable\HogwartsApp-Portable\ejecutar.sh

REM Crear README
echo ======================================== > portable\HogwartsApp-Portable\LEEME.txt
echo     HogwartsApp - Version Portable >> portable\HogwartsApp-Portable\LEEME.txt
echo ======================================== >> portable\HogwartsApp-Portable\LEEME.txt
echo. >> portable\HogwartsApp-Portable\LEEME.txt
echo INSTRUCCIONES: >> portable\HogwartsApp-Portable\LEEME.txt
echo -------------- >> portable\HogwartsApp-Portable\LEEME.txt
echo 1. Asegurate de tener Java 21+ instalado >> portable\HogwartsApp-Portable\LEEME.txt
echo    Descargar desde: https://adoptium.net/ >> portable\HogwartsApp-Portable\LEEME.txt
echo. >> portable\HogwartsApp-Portable\LEEME.txt
echo 2. Ejecuta: >> portable\HogwartsApp-Portable\LEEME.txt
echo    - Windows: Doble click en "ejecutar.bat" >> portable\HogwartsApp-Portable\LEEME.txt
echo    - Mac/Linux: Doble click en "ejecutar.sh" >> portable\HogwartsApp-Portable\LEEME.txt
echo. >> portable\HogwartsApp-Portable\LEEME.txt
echo 3. La aplicacion se conectara a las bases de datos >> portable\HogwartsApp-Portable\LEEME.txt
echo    configuradas automaticamente. >> portable\HogwartsApp-Portable\LEEME.txt
echo. >> portable\HogwartsApp-Portable\LEEME.txt
echo Copyright 2024 - Hogwarts School >> portable\HogwartsApp-Portable\LEEME.txt

REM Comprimir
echo.
echo Comprimiendo version portable...
powershell Compress-Archive -Force -Path portable\HogwartsApp-Portable -DestinationPath portable\HogwartsApp-Portable-windows.zip

echo.
echo =========================================
echo VERSION PORTABLE CREADA EXITOSAMENTE
echo =========================================
echo.
echo Ubicacion: portable\HogwartsApp-Portable-windows.zip
dir portable\*.zip
echo.
echo Contenido:
echo   - hogwartsApp.jar (aplicacion)
echo   - libs\ (librerias)
echo   - ejecutar.bat (Windows)
echo   - ejecutar.sh (Mac/Linux)
echo   - LEEME.txt (instrucciones)
echo   - logs\ (carpeta para logs)
echo.
echo IMPORTANTE:
echo   El cliente necesita Java 21+ instalado
echo   Descarga desde: https://adoptium.net/
echo.
echo =========================================

pause