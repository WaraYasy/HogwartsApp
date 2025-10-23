#!/bin/bash

# ============================================
# Script para crear VERSIÓN PORTABLE
# (JAR + Java Runtime incluido)
# ============================================

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

echo "========================================="
echo "  HogwartsApp - Crear Versión Portable"
echo "========================================="

# Detectar sistema operativo
OS_TYPE=""
if [[ "$OSTYPE" == "darwin"* ]]; then
    OS_TYPE="macos"
    echo -e "${GREEN}✓ Sistema operativo: macOS${NC}"
elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
    OS_TYPE="linux"
    echo -e "${GREEN}✓ Sistema operativo: Linux${NC}"
elif [[ "$OSTYPE" == "msys" || "$OSTYPE" == "win32" ]]; then
    OS_TYPE="windows"
    echo -e "${GREEN}✓ Sistema operativo: Windows${NC}"
else
    echo -e "${RED}❌ Sistema operativo no soportado: $OSTYPE${NC}"
    exit 1
fi

# 1. Compilar proyecto
echo ""
echo -e "${YELLOW}[1/5] Compilando proyecto...${NC}"
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Error al compilar${NC}"
    exit 1
fi

echo -e "${GREEN}✓ Compilación exitosa${NC}"

# 2. Crear estructura de directorios
echo ""
echo -e "${YELLOW}[2/5] Creando estructura portable...${NC}"
rm -rf portable
mkdir -p portable/HogwartsApp-Portable
cd portable/HogwartsApp-Portable

# 3. Copiar aplicación
echo ""
echo -e "${YELLOW}[3/5] Copiando aplicación...${NC}"
cp ../../target/hogwartsApp-1.0-SNAPSHOT.jar ./hogwartsApp.jar
cp -r ../../target/libs ./
mkdir -p logs

echo -e "${GREEN}✓ Aplicación copiada${NC}"

# 4. Crear Java Runtime personalizado con jlink
echo ""
echo -e "${YELLOW}[4/5] Creando Java Runtime embebido...${NC}"
echo "Esto puede tardar varios minutos..."

# Verificar que jlink está disponible
if ! command -v jlink &> /dev/null; then
    echo -e "${RED}❌ ERROR: jlink no está disponible${NC}"
    echo "Necesitas Java 11 o superior"
    exit 1
fi

# Crear runtime mínimo con los módulos necesarios
jlink \
  --add-modules java.base,java.desktop,java.sql,java.naming,java.xml,javafx.controls,javafx.fxml,javafx.graphics \
  --strip-debug \
  --no-header-files \
  --no-man-pages \
  --compress=2 \
  --output jre

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Error al crear runtime${NC}"
    echo "Nota: Asegúrate de tener los módulos JavaFX en el module path"
    echo "Creando versión sin jlink (requiere Java instalado)..."
    rm -rf jre
fi

if [ -d "jre" ]; then
    echo -e "${GREEN}✓ Java Runtime creado (incluido en paquete)${NC}"
    JAVA_INCLUDED=true
else
    echo -e "${YELLOW}⚠ Runtime no incluido (requiere Java en el sistema)${NC}"
    JAVA_INCLUDED=false
fi

# 5. Crear scripts de ejecución
echo ""
echo -e "${YELLOW}[5/5] Creando scripts de ejecución...${NC}"

# Script para Windows
cat > ejecutar.bat << 'EOF'
@echo off
echo =========================================
echo    HogwartsApp - Portable Edition
echo =========================================
echo.

REM Verificar si el runtime está incluido
if exist "jre\bin\java.exe" (
    echo Usando Java incluido...
    jre\bin\java.exe --enable-native-access=ALL-UNNAMED -jar hogwartsApp.jar
) else (
    echo Usando Java del sistema...
    java --enable-native-access=ALL-UNNAMED -jar hogwartsApp.jar
)

if %errorlevel% neq 0 (
    echo.
    echo ERROR: No se pudo ejecutar la aplicacion
    echo Asegurate de tener Java 21+ instalado si no esta incluido
    pause
)
EOF

# Script para Mac/Linux
cat > ejecutar.sh << 'EOF'
#!/bin/bash

echo "========================================="
echo "    HogwartsApp - Portable Edition"
echo "========================================="
echo ""

# Verificar si el runtime está incluido
if [ -f "jre/bin/java" ]; then
    echo "Usando Java incluido..."
    ./jre/bin/java --enable-native-access=ALL-UNNAMED -jar hogwartsApp.jar
else
    echo "Usando Java del sistema..."
    java --enable-native-access=ALL-UNNAMED -jar hogwartsApp.jar
fi

if [ $? -ne 0 ]; then
    echo ""
    echo "❌ ERROR: No se pudo ejecutar la aplicación"
    echo "Asegúrate de tener Java 21+ instalado si no está incluido"
    read -p "Presiona Enter para salir..."
fi
EOF

chmod +x ejecutar.sh

# Crear README
cat > LEEME.txt << EOF
========================================
    HogwartsApp - Versión Portable
========================================

INSTRUCCIONES:
--------------
1. Descomprime esta carpeta en cualquier ubicación

2. Ejecuta el archivo correspondiente:
   • Windows: Doble click en "ejecutar.bat"
   • Mac/Linux: Doble click en "ejecutar.sh"
                (o desde terminal: ./ejecutar.sh)

3. ¡La aplicación se iniciará automáticamente!

CARACTERÍSTICAS:
----------------
✅ NO requiere instalación
✅ NO modifica el sistema
✅ Funciona desde USB/cualquier carpeta
✅ Java incluido (no requiere instalación previa)
✅ Logs se guardan en la carpeta "logs/"

REQUISITOS:
-----------
EOF

if [ "$JAVA_INCLUDED" = true ]; then
cat >> LEEME.txt << EOF
✅ Java Runtime: INCLUIDO (no requiere nada más)
EOF
else
cat >> LEEME.txt << EOF
⚠️  Java Runtime: NO incluido
   Requiere Java 21+ instalado en el sistema
   Descargar desde: https://adoptium.net/
EOF
fi

cat >> LEEME.txt << EOF

CONEXIÓN A BASE DE DATOS:
--------------------------
La aplicación se conecta a:
• Oracle: 100.104.4.128:1521
• MariaDB: 100.104.4.128:3306
• H2: 100.104.4.128:9092
• Derby: 100.104.4.128:1527
• HSQLDB: 100.104.4.128:9001
• SQLite: Base de datos local embebida

Asegúrate de tener acceso de red a estos servidores.

SOLUCIÓN DE PROBLEMAS:
----------------------
❌ No se abre nada al ejecutar:
   → Verifica que tienes Java instalado
   → Abre terminal y ejecuta manualmente

❌ Error "cannot connect to database":
   → Verifica conexión de red a 100.104.4.128
   → Verifica que los puertos no estén bloqueados

❌ La ventana no se muestra:
   → Asegúrate de estar en un entorno con interfaz gráfica
   → No ejecutar desde SSH remoto sin X11

CONTENIDO:
----------
hogwartsApp.jar     → Aplicación principal
libs/               → Librerías necesarias
jre/                → Java Runtime embebido
logs/               → Registros de la aplicación
ejecutar.bat        → Ejecutar en Windows
ejecutar.sh         → Ejecutar en Mac/Linux
LEEME.txt           → Este archivo

SOPORTE:
--------
Para soporte técnico, contacta con el equipo de desarrollo.

Copyright © 2024 - Hogwarts School
EOF

cd ../..

# 6. Comprimir para distribución
echo ""
echo -e "${YELLOW}Comprimiendo para distribución...${NC}"

cd portable
zip -r HogwartsApp-Portable-${OS_TYPE}.zip HogwartsApp-Portable/ > /dev/null 2>&1
cd ..

echo ""
echo -e "${GREEN}=========================================${NC}"
echo -e "${GREEN}✅ VERSIÓN PORTABLE CREADA${NC}"
echo -e "${GREEN}=========================================${NC}"
echo ""
echo "Ubicación:"
ls -lh portable/HogwartsApp-Portable-${OS_TYPE}.zip

if [ "$JAVA_INCLUDED" = true ]; then
    echo ""
    echo -e "${GREEN}✅ Java Runtime: INCLUIDO${NC}"
    echo "   El cliente NO necesita instalar Java"
    echo "   Tamaño aproximado: ~150-200 MB"
else
    echo ""
    echo -e "${YELLOW}⚠️  Java Runtime: NO INCLUIDO${NC}"
    echo "   El cliente necesita Java 21+ instalado"
    echo "   Tamaño aproximado: ~10-20 MB"
fi

echo ""
echo -e "${YELLOW}Cómo entregarlo al cliente:${NC}"
echo "  1. Envía el archivo .zip al cliente"
echo "  2. Cliente: Descomprime el archivo"
echo "  3. Cliente: Ejecuta 'ejecutar.bat' (Windows) o 'ejecutar.sh' (Mac/Linux)"
echo "  4. ¡Listo! La aplicación funciona"

echo ""
echo -e "${GREEN}Ventajas para el cliente:${NC}"
echo "  ✅ NO requiere instalación"
echo "  ✅ NO modifica el sistema"
echo "  ✅ Funciona desde USB o cualquier carpeta"
echo "  ✅ Fácil de mover o eliminar (solo borrar carpeta)"

if [ "$JAVA_INCLUDED" = true ]; then
    echo "  ✅ Java incluido (no necesita instalarlo)"
fi

echo ""
echo "========================================="
