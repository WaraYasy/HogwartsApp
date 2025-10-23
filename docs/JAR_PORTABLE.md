# HogwartsApp - JAR Portable

## ¿Qué es el JAR Portable?

Es una versión **completamente autocontenida** de HogwartsApp que incluye todas las dependencias necesarias en un único archivo JAR de ~51MB.

**Ventajas:**
- ✅ **No necesita instalación** - Solo copiar y ejecutar
- ✅ **Incluye todas las dependencias** - No requiere Maven ni configuración
- ✅ **SQLite embebida funciona perfectamente** - Se autoconfigura en el primer arranque
- ✅ **Logs automáticos** - Se guardan en `logs/` donde ejecutes el JAR
- ✅ **Portable entre ordenadores** - Solo necesita Java 21+

---

## Requisitos

### Software Necesario
- **Java 21 o superior** instalado en el sistema
  - Descargar desde: https://adoptium.net/
  - Verificar con: `java -version`

### Sistemas Operativos Soportados
- ✅ **macOS** (Intel y Apple Silicon)
- ✅ **Windows** 10/11
- ✅ **Linux** (Ubuntu, Fedora, etc.)

---

## Cómo Crear el JAR Portable

### Desde el código fuente:

```bash
# 1. Ir al directorio del proyecto
cd HogwartsApp

# 2. Compilar y crear el JAR portable
mvn clean package -DskipTests

# 3. El JAR se genera en:
# target/hogwartsApp-1.0-SNAPSHOT-portable.jar (51 MB)
```

---

## Cómo Ejecutar

### Opción 1: Usando Scripts (Recomendado)

#### En macOS/Linux:
```bash
./scripts/ejecutar.sh
```

#### En Windows:
```cmd
scripts\ejecutar.bat
```

### Opción 2: Comando Manual

#### En todos los sistemas:
```bash
java --enable-native-access=ALL-UNNAMED -jar target/hogwartsApp-1.0-SNAPSHOT-portable.jar
```

#### Con más memoria (si tienes muchos datos):
```bash
java --enable-native-access=ALL-UNNAMED -Xmx1g -jar target/hogwartsApp-1.0-SNAPSHOT-portable.jar
```

---

## Distribución a Otros Ordenadores

### 📦 Pasos para llevar la app a otro ordenador:

1. **Preparar el paquete:**
   ```bash
   # Crear carpeta para distribución
   mkdir HogwartsApp-Portable

   # Copiar el JAR portable
   cp target/hogwartsApp-1.0-SNAPSHOT-portable.jar HogwartsApp-Portable/

   # Copiar scripts de ejecución
   cp scripts/ejecutar.sh scripts/ejecutar.bat HogwartsApp-Portable/

   # Copiar README (opcional)
   cp docs/JAR_PORTABLE.md HogwartsApp-Portable/README.md

   # Crear ZIP para distribución
   zip -r HogwartsApp-Portable.zip HogwartsApp-Portable/
   ```

2. **En el ordenador destino:**
   - Extraer el ZIP
   - Verificar que tiene Java 21+ instalado
   - Ejecutar con el script correspondiente

---

## Estructura de Archivos en Ejecución

```
HogwartsApp-Portable/
├── hogwartsApp-1.0-SNAPSHOT-portable.jar  (51 MB - contiene todo)
├── ejecutar.sh                             (script para macOS/Linux)
├── ejecutar.bat                            (script para Windows)
├── README.md                               (este archivo)
└── logs/                                   (se crea automáticamente)
    ├── HogwartsApp-all.log                (todos los logs)
    └── HogwartsApp-info.log               (solo INFO y superiores)
```

### Ubicación de la Base de Datos SQLite

La base de datos se crea **automáticamente** en el primer arranque en:

- **macOS:** `~/Library/Application Support/HogwartsApp/hogwarts.db`
- **Windows:** `%APPDATA%\HogwartsApp\hogwarts.db`
- **Linux:** `~/.config/HogwartsApp/hogwarts.db`

**Importante:** Los datos **persisten entre ejecuciones** y **NO se borran** al actualizar la aplicación.

---

## Verificación de Funcionamiento

### 1. Verificar Java
```bash
java -version
```

Debe mostrar:
```
openjdk version "21.x.x" ...
OpenJDK Runtime Environment ...
```

### 2. Verificar el JAR
```bash
ls -lh target/hogwartsApp-1.0-SNAPSHOT-portable.jar
```

Debe mostrar un archivo de ~51 MB.

### 3. Probar Ejecución
```bash
./scripts/ejecutar.sh
```

Debe mostrar:
```
==========================================
   HogwartsApp - Portable Edition v1.0
==========================================

Base de datos SQLite se creará en:
  ~/Library/Application Support/HogwartsApp/hogwarts.db

Los logs se guardarán en:
  /ruta/al/proyecto/logs/

Iniciando HogwartsApp...
==========================================
```

---

## Contenido del JAR Portable

El JAR incluye:

### Tu Código
- ✅ Todas las clases de `es.potter.*`
- ✅ SQLiteManager.java (gestor embebido)
- ✅ Base de datos inicial: `hogwarts.db`
- ✅ Recursos: FXML, CSS, imágenes, properties

### Dependencias Incluidas (Total: ~50 MB)
- ✅ **JavaFX 24** (Controls, FXML, Graphics)
- ✅ **JDBC Drivers:**
  - MariaDB 3.5.6
  - SQLite 3.50.3.0
  - Oracle ojdbc11
  - H2 2.2.224
  - Apache Derby 10.17.1.0
  - HSQLDB 2.7.4
- ✅ **Logging:**
  - SLF4J 2.0.13
  - Logback 1.5.13
- ✅ **UI:**
  - Ikonli (iconos FontAwesome5)

---

## Opciones de JVM Recomendadas

### Configuración Básica (Incluida en scripts)
```bash
java \
  --enable-native-access=ALL-UNNAMED \  # Permite acceso nativo para SQLite
  -Xmx512m \                             # Memoria máxima 512 MB
  -Xms256m \                             # Memoria inicial 256 MB
  -Dfile.encoding=UTF-8 \                # Codificación UTF-8
  -jar hogwartsApp-1.0-SNAPSHOT-portable.jar
```

### Para Servidores o Muchos Datos
```bash
java \
  --enable-native-access=ALL-UNNAMED \
  -Xmx2g \           # 2 GB de memoria máxima
  -Xms512m \         # 512 MB de memoria inicial
  -XX:+UseG1GC \     # Recolector de basura G1 (mejor para JavaFX)
  -Dfile.encoding=UTF-8 \
  -jar hogwartsApp-1.0-SNAPSHOT-portable.jar
```

---

## Solución de Problemas

### Problema: "UnsupportedClassVersionError"
**Causa:** Java version demasiado antigua.

**Solución:**
```bash
# Verificar versión
java -version

# Si es menor a 21, actualizar desde:
# https://adoptium.net/
```

### Problema: "No se puede escribir en la base de datos"
**Causa:** Permisos insuficientes en el directorio de datos.

**Solución (macOS/Linux):**
```bash
# Verificar permisos
ls -ld ~/Library/Application\ Support/HogwartsApp/

# Si no existe, la app la creará automáticamente
# Si existe pero no tiene permisos:
chmod 755 ~/Library/Application\ Support/HogwartsApp/
```

**Solución (Windows):**
```cmd
REM Verificar si la carpeta existe
dir %APPDATA%\HogwartsApp

REM Si hay problemas, ejecutar como administrador temporalmente
```

### Problema: "Could not find or load main class es.potter.Lanzador"
**Causa:** JAR corrupto o no se generó correctamente.

**Solución:**
```bash
# Limpiar y recompilar
mvn clean
mvn package -DskipTests

# Verificar que el JAR existe y tiene tamaño correcto
ls -lh target/hogwartsApp-1.0-SNAPSHOT-portable.jar
```

### Problema: "Module javafx.controls not found"
**Causa:** JavaFX no se incluyó en el JAR.

**Solución:**
Esto NO debería pasar con el JAR portable. Si ocurre:
1. Verificar que usas `hogwartsApp-1.0-SNAPSHOT-portable.jar` (NO el normal)
2. Recompilar: `mvn clean package -DskipTests`

### Problema: Los logs no se crean
**Causa:** Falta el archivo `logback.xml` en resources.

**Solución:**
El archivo `logback.xml` debería estar embebido en el JAR:
```bash
# Verificar que existe
jar tf target/hogwartsApp-1.0-SNAPSHOT-portable.jar | grep logback.xml
```

Si no existe, verificar que esté en: `src/main/resources/logback.xml`

---

## Comparación: JAR Normal vs JAR Portable

| Característica | JAR Normal | JAR Portable |
|----------------|------------|--------------|
| **Tamaño** | 186 KB | 51 MB |
| **Dependencias** | Externas (carpeta libs/) | Incluidas |
| **Ejecución** | `java -cp "jar:libs/*" ...` | `java -jar ...` |
| **Distribución** | Compleja (JAR + libs/) | Simple (1 archivo) |
| **Portabilidad** | ❌ Difícil | ✅ Fácil |
| **Velocidad** | Similar | Similar |

---

## Testing del JAR Portable

### Test 1: Verificar Contenido
```bash
# Ver archivos incluidos
jar tf target/hogwartsApp-1.0-SNAPSHOT-portable.jar | grep -E "(potter|sqlite|javafx)" | head -20

# Debe mostrar:
# es/potter/Lanzador.class
# es/potter/database/SQLiteManager.class
# es/potter/db/hogwarts.db
# org/sqlite/...
# javafx/...
```

### Test 2: Ejecutar en Otro Directorio
```bash
# Copiar el JAR a otro lugar
cp target/hogwartsApp-1.0-SNAPSHOT-portable.jar /tmp/
cd /tmp/

# Ejecutar desde ahí
java --enable-native-access=ALL-UNNAMED -jar hogwartsApp-1.0-SNAPSHOT-portable.jar

# Debe funcionar igual
```

### Test 3: Verificar SQLite
```bash
# Después de ejecutar la app, verificar que la BD se creó
ls -lh ~/Library/Application\ Support/HogwartsApp/hogwarts.db

# Debe existir y tener tamaño > 0
```

---

## Preguntas Frecuentes

### ¿Necesito Maven para ejecutar el JAR portable?
**No.** Solo necesitas Java 21+. Maven solo se necesita para **crear** el JAR.

### ¿Puedo renombrar el archivo JAR?
**Sí.** Puedes renombrarlo a, por ejemplo, `HogwartsApp.jar`.

### ¿Los datos se pierden al actualizar?
**No.** Los datos están en `~/Library/Application Support/HogwartsApp/` (macOS) o equivalente en otros sistemas. Persisten entre versiones.

### ¿Puedo ejecutar múltiples instancias a la vez?
**Sí**, pero todas compartirán la misma base de datos SQLite local. Considera posibles conflictos.

### ¿Funciona sin conexión a Internet?
**Sí.** La aplicación funciona completamente offline. Solo necesitas Internet para conectarte a las bases de datos remotas (MariaDB, Oracle, etc.), pero SQLite funciona siempre.

### ¿Cómo actualizo a una nueva versión?
1. Cerrar la app
2. Reemplazar el JAR antiguo con el nuevo
3. Ejecutar normalmente
4. Tus datos (SQLite) se preservan automáticamente

---

## Diferencias con el Instalador Nativo

| Aspecto | JAR Portable | Instalador DMG/MSI |
|---------|-------------|-------------------|
| **Instalación** | No requiere | Sí requiere |
| **Tamaño** | 51 MB | ~150 MB (con JVM incluida) |
| **Java** | Necesita Java instalado | JVM incluida |
| **Accesos directos** | No crea | Crea en menú/escritorio |
| **Desinstalación** | Borrar archivo | Usar desinstalador |
| **Actualización** | Reemplazar JAR | Reinstalar |

---

## Autor
Wara Pacheco

## Versión
1.0 - JAR Portable con SQLite Embebida

## Fecha
2025-10-23

---

## Licencia
Consultar README.md principal del proyecto.