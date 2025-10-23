@echo off
REM
REM Script para ejecutar HogwartsApp Portable en Windows
REM No requiere instalación ni configuración adicional
REM
REM Autor: Wara Pacheco
REM Fecha: 2025-10-23
REM

echo ==========================================
echo    HogwartsApp - Portable Edition v1.0
echo ==========================================
echo.

REM Verificar que existe el JAR portable
SET JAR_FILE=target\hogwartsApp-1.0-SNAPSHOT-portable.jar

if not exist "%JAR_FILE%" (
    echo ERROR: No se encontro el archivo JAR portable.
    echo.
    echo Por favor, ejecuta primero:
    echo   mvn clean package -DskipTests
    echo.
    pause
    exit /b 1
)

REM Verificar que Java está instalado
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java no esta instalado.
    echo.
    echo Por favor, instala Java 21 o superior:
    echo   https://adoptium.net/
    echo.
    pause
    exit /b 1
)

REM Información de SQLite
echo Base de datos SQLite se creara en:
echo   %%APPDATA%%\HogwartsApp\hogwarts.db
echo.

REM Información de logs
echo Los logs se guardaran en:
echo   %CD%\logs\
echo.

echo Iniciando HogwartsApp...
echo ==========================================
echo.

REM Ejecutar la aplicación con opciones optimizadas
java ^
    --enable-native-access=ALL-UNNAMED ^
    -Xmx512m ^
    -Xms256m ^
    -Dfile.encoding=UTF-8 ^
    -jar "%JAR_FILE%" ^
    %*

REM Capturar código de salida
SET EXIT_CODE=%ERRORLEVEL%

echo.
echo ==========================================
if %EXIT_CODE% EQU 0 (
    echo La aplicacion se cerro correctamente.
) else (
    echo La aplicacion se cerro con errores ^(codigo: %EXIT_CODE%^)
    echo.
    echo Revisa los logs en: %CD%\logs\
)
echo ==========================================
echo.

pause
exit /b %EXIT_CODE%