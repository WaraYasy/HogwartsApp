#!/bin/bash

# ============================================
# Script para crear INSTALADOR con jpackage
# ============================================

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

echo "========================================="
echo "  HogwartsApp - Crear Instalador"
echo "========================================="

# Verificar que jpackage está disponible (Java 14+)
if ! command -v jpackage &> /dev/null; then
    echo -e "${RED}❌ ERROR: jpackage no está disponible${NC}"
    echo "Necesitas Java 14 o superior con jpackage incluido"
    echo "Descarga desde: https://adoptium.net/"
    exit 1
fi

echo -e "${GREEN}✓ jpackage encontrado${NC}"
java -version

# Detectar sistema operativo
OS_TYPE=""
INSTALLER_TYPE=""

if [[ "$OSTYPE" == "darwin"* ]]; then
    OS_TYPE="Mac"
    INSTALLER_TYPE="dmg"
    echo -e "${GREEN}✓ Sistema operativo: macOS${NC}"
elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
    OS_TYPE="Linux"
    INSTALLER_TYPE="deb"
    echo -e "${GREEN}✓ Sistema operativo: Linux${NC}"
elif [[ "$OSTYPE" == "msys" || "$OSTYPE" == "win32" ]]; then
    OS_TYPE="Windows"
    INSTALLER_TYPE="exe"
    echo -e "${GREEN}✓ Sistema operativo: Windows${NC}"
else
    echo -e "${RED}❌ Sistema operativo no soportado: $OSTYPE${NC}"
    exit 1
fi

# 1. Limpiar y compilar
echo ""
echo -e "${YELLOW}[1/4] Compilando proyecto...${NC}"
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Error al compilar${NC}"
    exit 1
fi

echo -e "${GREEN}✓ Compilación exitosa${NC}"

# 2. Crear directorio temporal
echo ""
echo -e "${YELLOW}[2/4] Preparando archivos...${NC}"
rm -rf instalador-temp
mkdir -p instalador-temp/input

# Copiar JAR principal y librerías
cp target/hogwartsApp-1.0-SNAPSHOT.jar instalador-temp/input/
cp -r target/libs instalador-temp/input/

echo -e "${GREEN}✓ Archivos preparados${NC}"

# 3. Crear icono (opcional, puedes personalizarlo después)
echo ""
echo -e "${YELLOW}[3/4] Configurando instalador...${NC}"

# 4. Ejecutar jpackage
echo ""
echo -e "${YELLOW}[4/4] Generando instalador $INSTALLER_TYPE...${NC}"
echo "Esto puede tardar varios minutos..."

jpackage \
  --input instalador-temp/input/libs \
  --name "HogwartsApp" \
  --main-jar hogwartsApp-1.0-SNAPSHOT.jar \
  --main-class es.potter.Lanzador \
  --type $INSTALLER_TYPE \
  --app-version "1.0" \
  --vendor "Hogwarts School" \
  --description "Sistema de gestión de Hogwarts" \
  --copyright "Copyright © 2024" \
  --java-options "--enable-native-access=ALL-UNNAMED" \
  --dest instalador-output

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Error al crear instalador${NC}"
    echo "Verifica que tienes los permisos necesarios"
    exit 1
fi

# Limpiar temporal
rm -rf instalador-temp

echo ""
echo -e "${GREEN}=========================================${NC}"
echo -e "${GREEN}✅ INSTALADOR CREADO EXITOSAMENTE${NC}"
echo -e "${GREEN}=========================================${NC}"
echo ""
echo "Ubicación:"
ls -lh instalador-output/

echo ""
echo -e "${YELLOW}Información del instalador:${NC}"
echo "  Sistema: $OS_TYPE"
echo "  Tipo: $INSTALLER_TYPE"
echo "  Tamaño: ~150-200 MB (incluye Java Runtime)"
echo ""
echo -e "${YELLOW}Cómo entregarlo al cliente:${NC}"

if [[ "$OS_TYPE" == "Mac" ]]; then
    echo "  1. Envía el archivo .dmg al cliente"
    echo "  2. Cliente: Doble click en el .dmg"
    echo "  3. Arrastrar HogwartsApp a Aplicaciones"
    echo "  4. ¡Listo! Se ejecuta como cualquier app Mac"
elif [[ "$OS_TYPE" == "Linux" ]]; then
    echo "  1. Envía el archivo .deb al cliente"
    echo "  2. Cliente: sudo dpkg -i HogwartsApp_1.0_amd64.deb"
    echo "  3. O doble click para instalar con Software Center"
    echo "  4. ¡Listo! Aparece en el menú de aplicaciones"
elif [[ "$OS_TYPE" == "Windows" ]]; then
    echo "  1. Envía el archivo .exe al cliente"
    echo "  2. Cliente: Doble click en el instalador"
    echo "  3. Seguir el asistente de instalación"
    echo "  4. ¡Listo! Ícono en el escritorio/menú inicio"
fi

echo ""
echo -e "${GREEN}Ventajas para el cliente:${NC}"
echo "  ✅ NO necesita instalar Java"
echo "  ✅ Instalación simple (doble click)"
echo "  ✅ Aplicación nativa del sistema operativo"
echo "  ✅ Desinstalación desde panel de control"
echo ""
echo "========================================="