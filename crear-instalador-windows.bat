
@echo off
REM ============================================
REM Script para crear APP-IMAGE en Windows
REM ============================================

echo =========================================
echo   HogwartsApp - Generar app ejecutable
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

echo Java/jpackage encontrado:
java -version

echo.
echo [1/4] Compilando proyecto con Maven...
call mvn clean package -DskipTests

if %errorlevel% neq 0 (
    echo ERROR al compilar el proyecto.
    pause
    exit /b 1
)

echo.
echo [2/4] Preparando archivos...
if exist instalador-temp rmdir /s /q instalador-temp
if exist instalador-output rmdir /s /q instalador-output

mkdir instalador-temp\input

REM Copiar JAR principal
copy target\hogwartsApp-1.0-SNAPSHOT.jar instalador-temp\input\hogwartsApp.jar

REM Copiar librerias dependientes (OBLIGATORIO para JavaFX)
if exist target\libs (
    mkdir instalador-temp\input\libs
    xcopy /Y /E target\libs instalador-temp\input\libs\
) else (
    echo ERROR: No se encontraron las librerias en target\libs
    echo Ejecuta primero: mvn clean package
    pause
    exit /b 1
)

echo.
echo [3/4] Generando app-image ejecutable...
echo Esto puede tardar varios minutos...

REM IMPORTANTE: app-image NO requiere WiX Tools
REM Genera una carpeta portable con todo incluido

jpackage ^
  --input instalador-temp\input ^
  --name "HogwartsApp" ^
  --main-jar hogwartsApp.jar ^
  --main-class es.potter.Lanzador ^
  --type app-image ^
  --app-version "1.0" ^
  --vendor "Hogwarts School" ^
  --description "Sistema de gestion de Hogwarts" ^
  --copyright "Copyright 2024" ^
  --win-console ^
  --java-options "-Dfile.encoding=UTF-8" ^
  --java-options "--enable-native-access=ALL-UNNAMED" ^
  --dest instalador-output

if %errorlevel% neq 0 (
    echo ERROR al crear la app-image.
    pause
    exit /b 1
)

REM Limpiar temporales
rmdir /s /q instalador-temp

echo.
echo =========================================
echo APP-IMAGE CREADA EXITOSAMENTE
echo =========================================
echo.
echo Ubicacion:
dir instalador-output\HogwartsApp\*.exe

echo.
echo Verificando estructura del ejecutable...
echo.
if exist instalador-output\HogwartsApp\app (
    echo [OK] Carpeta app encontrada
    dir instalador-output\HogwartsApp\app\*.jar
) else (
    echo [ADVERTENCIA] No se encontro carpeta app
)

if exist instalador-output\HogwartsApp\runtime (
    echo [OK] JVM runtime incluido
) else (
    echo [ERROR] No se encontro runtime de Java
)

echo.
echo Como entregarlo:
echo   1. Comprime la carpeta "HogwartsApp" dentro de instalador-output
echo   2. Envia el ZIP al cliente
echo   3. El cliente descomprime y ejecuta HogwartsApp.exe
echo   4. No necesita tener Java instalado
echo.
echo IMPORTANTE - Para probar el ejecutable:
echo   1. Ve a: instalador-output\HogwartsApp
echo   2. Ejecuta HogwartsApp.exe
echo   3. Si hay error, revisa el log en la misma carpeta
echo.
echo =========================================

pause
