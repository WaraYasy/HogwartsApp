
@echo off
REM ============================================
REM Script para crear INSTALADOR en Windows
REM ============================================

echo =========================================
echo   HogwartsApp - Crear Instalador Windows
echo =========================================

REM Verificar que jpackage esta disponible
where jpackage >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: jpackage no esta disponible
    echo Necesitas Java 14 o superior con jpackage incluido
    echo Descarga desde: https://adoptium.net/
    pause
    exit /b 1
)

echo Java/jpackage encontrado
java -version

echo.
echo [1/4] Compilando proyecto...
call mvn clean package -DskipTests

if %errorlevel% neq 0 (
    echo ERROR al compilar
    pause
    exit /b 1
)

echo.
echo [2/4] Preparando archivos...
if exist instalador-temp rmdir /s /q instalador-temp
if exist instalador-output rmdir /s /q instalador-output
mkdir instalador-temp\input

REM Copiar JAR principal y todas las librerias al mismo directorio
copy target\hogwartsApp-1.0-SNAPSHOT.jar instalador-temp\input\hogwartsApp.jar
xcopy /Y target\libs\* instalador-temp\input\

echo.
echo [3/4] Generando instalador .exe...
echo Esto puede tardar varios minutos...

jpackage ^
  --input instalador-temp\input ^
  --name "HogwartsApp" ^
  --main-jar hogwartsApp.jar ^
  --main-class es.potter.Lanzador ^
  --type exe ^
  --app-version "1.0" ^
  --vendor "Hogwarts School" ^
  --description "Sistema de gestion de Hogwarts" ^
  --copyright "Copyright 2024" ^
  --java-options "--enable-native-access=ALL-UNNAMED" ^
  --win-dir-chooser ^
  --win-menu ^
  --win-shortcut ^
  --dest instalador-output

if %errorlevel% neq 0 (
    echo ERROR al crear instalador
    pause
    exit /b 1
)

REM Limpiar temporal
rmdir /s /q instalador-temp

echo.
echo =========================================
echo INSTALADOR WINDOWS CREADO EXITOSAMENTE
echo =========================================
echo.
echo Ubicacion:
dir instalador-output\*.exe

echo.
echo Como entregarlo al cliente:
echo   1. Envia el archivo .exe al cliente
echo   2. Cliente: Doble click en el instalador
echo   3. Seguir el asistente de instalacion
echo   4. Listo! Icono en el escritorio/menu inicio
echo.
echo Ventajas para el cliente:
echo   - NO necesita instalar Java
echo   - Instalacion simple (doble click)
echo   - Aplicacion nativa de Windows
echo   - Desinstalacion desde Panel de Control
echo.
echo =========================================

pause