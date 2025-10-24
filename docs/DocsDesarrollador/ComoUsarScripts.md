# Scripts de HogwartsApp

Scripts para ejecutar HogwartsApp en diferentes sistemas operativos.

## Scripts Disponibles

### `ejecutar.sh` - Ejecutar en macOS/Linux

Script principal para ejecutar HogwartsApp en sistemas Unix.

**Cómo usar:**
```bash
./scripts/ejecutar.sh
```

**Qué hace:**
- Verifica que exista el JAR portable en `target/`
- Comprueba que Java 21+ esté instalado
- Muestra dónde se guardarán la BD y los logs
- Ejecuta la app con configuración optimizada de JVM
- Informa del resultado al cerrar

**Ubicaciones:**
- Base de datos: `~/Library/Application Support/HogwartsApp/hogwarts.db`
- Logs: `./logs/`

---

### `ejecutar.bat` - Ejecutar en Windows

Script principal para ejecutar HogwartsApp en Windows.

**Cómo usar:**
```cmd
scripts\ejecutar.bat
```

**Qué hace:**
- Verifica que exista el JAR portable en `target\`
- Comprueba que Java esté instalado
- Muestra dónde se guardarán la BD y los logs
- Ejecuta la app con configuración optimizada de JVM
- Informa del resultado al cerrar

**Ubicaciones:**
- Base de datos: `%APPDATA%\HogwartsApp\hogwarts.db`
- Logs: `.\logs\`

---

### `hogwartsApp.bat` - Cliente con Tailscale (Windows)

Script especial para ejecutar HogwartsApp como cliente conectado vía Tailscale.

**Cómo usar:**
```cmd
hogwartsApp.bat
```

**Requisitos:**
- Tailscale instalado y activo
- Conexión al servidor en `100.104.4.128`
- JAR portable en la misma carpeta que el script

**Qué hace:**
1. Verifica que Java esté instalado
2. Comprueba que Tailscale esté activo
3. Hace ping al servidor (100.104.4.128)
4. Si todo está OK, ejecuta la app en background

**Nota:** Este script es para despliegues cliente-servidor con VPN.

---

## Opciones de JVM

Los scripts principales usan estas opciones optimizadas:
- `--enable-native-access=ALL-UNNAMED` - Permite acceso nativo (JavaFX)
- `-Xmx512m` - Máximo 512MB de RAM
- `-Xms256m` - Mínimo 256MB de RAM
- `-Dfile.encoding=UTF-8` - Codificación UTF-8

## Compilar el JAR Portable

Si el JAR no existe, compílalo primero:

```bash
mvn clean package -DskipTests
```

Esto genera: `target/hogwartsApp-1.0-SNAPSHOT-portable.jar`

---

## Autor
Equipo Potter