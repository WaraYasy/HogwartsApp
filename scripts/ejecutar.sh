#!/bin/bash
#
# Script para ejecutar HogwartsApp Portable
# No requiere instalación ni configuración adicional
#
# Autor: Wara Pacheco
# Fecha: 2025-10-23
#

echo "=========================================="
echo "   HogwartsApp - Portable Edition v1.0"
echo "=========================================="
echo ""

# Verificar que existe el JAR portable
JAR_FILE="target/hogwartsApp-1.0-SNAPSHOT-portable.jar"

if [ ! -f "$JAR_FILE" ]; then
    echo "ERROR: No se encontró el archivo JAR portable."
    echo ""
    echo "Por favor, ejecuta primero:"
    echo "  mvn clean package -DskipTests"
    echo ""
    exit 1
fi

# Verificar que Java está instalado
if ! command -v java &> /dev/null; then
    echo "ERROR: Java no está instalado."
    echo ""
    echo "Por favor, instala Java 21 o superior:"
    echo "  https://adoptium.net/"
    echo ""
    exit 1
fi

# Verificar versión de Java
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | awk -F '"' '{print $2}' | awk -F '.' '{print $1}')
if [ "$JAVA_VERSION" -lt 21 ]; then
    echo "ADVERTENCIA: Se requiere Java 21 o superior."
    echo "Versión actual: $(java -version 2>&1 | head -n 1)"
    echo ""
    read -p "¿Deseas continuar de todas formas? (s/N): " -n 1 -r
    echo ""
    if [[ ! $REPLY =~ ^[Ss]$ ]]; then
        exit 1
    fi
fi

# Información de SQLite
echo "Base de datos SQLite se creará en:"
echo "  ~/Library/Application Support/HogwartsApp/hogwarts.db"
echo ""

# Información de logs
echo "Los logs se guardarán en:"
echo "  $(pwd)/logs/"
echo ""

echo "Iniciando HogwartsApp..."
echo "=========================================="
echo ""

# Ejecutar la aplicación con opciones optimizadas
java \
    --enable-native-access=ALL-UNNAMED \
    -Xmx512m \
    -Xms256m \
    -Dfile.encoding=UTF-8 \
    -jar "$JAR_FILE" \
    "$@"

# Capturar código de salida
EXIT_CODE=$?

echo ""
echo "=========================================="
if [ $EXIT_CODE -eq 0 ]; then
    echo "La aplicación se cerró correctamente."
else
    echo "La aplicación se cerró con errores (código: $EXIT_CODE)"
    echo ""
    echo "Revisa los logs en: $(pwd)/logs/"
fi
echo "=========================================="

exit $EXIT_CODE