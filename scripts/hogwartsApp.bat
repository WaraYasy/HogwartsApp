@echo off
setlocal

REM --- Verificar Java ---
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java no esta instalado o no esta en el PATH.
    pause
    exit /b
) else (
    echo -----Java encontrado-----.
)

REM ---  Verificar Tailscale ---
tasklist /FI "IMAGENAME eq tailscaled.exe" 2>NUL | find /I "tailscaled.exe" >NUL
if %errorlevel% neq 0 (
    echo ERROR: Tailscale no esta habilitado.
    pause
    exit /b
) else (
    echo ------Tailscale esta funcionando.--------
)

REM --- Hacer ping al servidor ---
ping -n 1 100.104.4.128 >nul
if %errorlevel% neq 0 (
    echo ERROR: No te has podido conectar con el servidor de HogawartsApp
    pause
    exit /b
) else (
    echo Conectado al servidor de HogawartsApp por Tailscale correctamente
)

REM --- Ejecutar fat JAR ---
echo Todo OK, ejecutando la aplicacion...
start "" javaw -jar "hogwartsApp-1.0-SNAPSHOT-portable.jar"

endlocal
exit /b
