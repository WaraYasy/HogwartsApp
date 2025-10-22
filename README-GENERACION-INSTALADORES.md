# Guía para Generar Instaladores - HogwartsApp

## 📋 Resumen

Este documento explica cómo generar los instaladores y versiones portable de HogwartsApp para entregar al cliente.

**⚠️ MUY IMPORTANTE**:
- **TODAS las distribuciones** (instaladores Y portable) deben generarse **LOCALMENTE**
- **NO se pueden generar con GitHub Actions** porque requieren `configuration.properties` con credenciales de BD
- `configuration.properties` NO debe subirse al repositorio por seguridad
- Por tanto, **SOLO puedes generar en tu máquina local** donde tienes las credenciales configuradas

---

## 🎯 Opciones de Distribución

### Opción 1: Instalador (Recomendado)
- **Ventajas**: Java incluido, instalación profesional, no requiere nada del cliente
- **Tamaño**: ~100 MB
- **Genera**: `.dmg` (Mac), `.exe` (Windows), `.deb` (Linux)

### Opción 2: Portable
- **Ventajas**: Sin instalación, portable (USB), más ligero
- **Tamaño**: ~50 MB
- **Requiere**: Java 21+ instalado en el cliente

---

## 🍎 GENERAR EN macOS

### Instalador (.dmg):

```bash
# 1. Abrir Terminal en la carpeta del proyecto
cd /ruta/a/HogwartsApp

# 2. Ejecutar script
./crear-instalador.sh

# 3. El instalador se genera en:
# instalador-output/HogwartsApp-1.0.dmg
```

**Requisitos**:
- macOS 10.14 o superior
- Java 22 con jpackage
- Maven

**Tiempo**: ~3-5 minutos

### Portable (.zip):

```bash
# 1. Ejecutar script
./crear-portable.sh

# 2. El ZIP se genera en:
# portable/HogwartsApp-Portable-macos.zip
```

**Tiempo**: ~1 minuto

---

## 💻 GENERAR EN WINDOWS

### Instalador (.exe):

```batch
REM 1. Abrir CMD o PowerShell en la carpeta del proyecto
cd C:\ruta\a\HogwartsApp

REM 2. Ejecutar script
crear-instalador-windows.bat

REM 3. El instalador se genera en:
REM instalador-output\HogwartsApp-1.0.exe
```

**Requisitos**:
- Windows 10 o superior
- Java 22 con jpackage
- Maven
- WiX Toolset (para .exe) - se descarga automáticamente

**Tiempo**: ~5-10 minutos

### Portable (.zip):

```batch
REM 1. Ejecutar script
crear-portable-windows.bat

REM 2. El ZIP se genera en:
REM portable\HogwartsApp-Portable-windows.zip
```

**Tiempo**: ~1-2 minutos

---

## 🐧 GENERAR EN LINUX

### Instalador (.deb):

```bash
# 1. Abrir Terminal
cd /ruta/a/HogwartsApp

# 2. Modificar crear-instalador.sh para Linux:
# Cambiar: --type dmg
# Por:     --type deb

# 3. Ejecutar
./crear-instalador.sh

# 4. El instalador se genera en:
# instalador-output/hogwartsapp_1.0_amd64.deb
```

**Requisitos**:
- Ubuntu/Debian (para .deb)
- Java 22 con jpackage
- Maven

### Portable (.zip):

```bash
./crear-portable.sh
# Se genera: portable/HogwartsApp-Portable-linux.zip
```

---

## 📦 Qué Incluyen los Instaladores

### Instaladores (.dmg/.exe/.deb):
✅ Java Runtime embebido (no requiere instalación)
✅ HogwartsApp.jar
✅ Todas las librerías (libs/)
✅ configuration.properties (con credenciales)
✅ Carpeta logs/
✅ Icono de aplicación
✅ Integración con el sistema operativo

### Portable (.zip):
✅ HogwartsApp.jar
✅ Todas las librerías (libs/)
✅ configuration.properties (con credenciales)
✅ ejecutar.bat (Windows)
✅ ejecutar.sh (Mac/Linux)
✅ LEEME.txt (instrucciones)
✅ Carpeta logs/

⚠️ **NO incluye Java** - El cliente debe tener Java 21+ instalado

---

## 🚀 Proceso Completo de Entrega

### Paso 1: Generar Instaladores

**En tu Mac**:
```bash
./crear-instalador.sh        # Genera .dmg
./crear-portable.sh          # Genera portable Mac
```

**En Windows** (si tienes acceso):
```batch
crear-instalador-windows.bat  # Genera .exe
crear-portable-windows.bat    # Genera portable Windows
```

**En Linux** (opcional):
```bash
# Modificar crear-instalador.sh (cambiar dmg por deb)
./crear-instalador.sh        # Genera .deb
```

### Paso 2: Reunir Archivos

Crear carpeta `Entrega-Cliente/`:

```
Entrega-Cliente/
├── Instaladores/
│   ├── HogwartsApp-1.0.dmg           (Mac)
│   ├── HogwartsApp-1.0.exe           (Windows)
│   └── hogwartsapp_1.0_amd64.deb     (Linux - opcional)
├── Portable/
│   ├── HogwartsApp-Portable-macos.zip
│   └── HogwartsApp-Portable-windows.zip
└── INSTRUCCIONES-CLIENTE.md
```

### Paso 3: Entregar al Cliente

**Por email**:
- Adjuntar los ZIP/instaladores
- Incluir INSTRUCCIONES-CLIENTE.md

**Por Google Drive/Dropbox**:
- Subir la carpeta Entrega-Cliente/
- Compartir enlace con el cliente

**Por USB**:
- Copiar carpeta Entrega-Cliente/
- Entregar físicamente

---

## ⚠️ Seguridad y Credenciales

### ❌ NO HACER:
- NO subir `configuration.properties` a GitHub
- NO usar GitHub Actions para generar distribuciones (no funcionarán sin credenciales)
- NO compartir públicamente los instaladores/portable generados

### ✅ HACER:
- Generar TODO localmente (instaladores Y portable)
- Mantener `configuration.properties` en `.gitignore`
- Entregar archivos directamente al cliente
- Usar canal seguro para transferencia (email corporativo, drive privado, USB)

---

## 🔧 Solución de Problemas

### Error: "jpackage: command not found"

**Causa**: Java no tiene jpackage o versión antigua

**Solución**:
```bash
# Verifica tu versión de Java
java -version

# Debe ser Java 14 o superior
# Si no, descarga desde: https://adoptium.net/
```

### Error: "BUILD FAILURE" al compilar

**Causa**: Maven no configurado o error en el código

**Solución**:
```bash
# Verifica Maven
mvn -version

# Intenta limpiar y recompilar
mvn clean install
```

### El instalador no se abre en Windows

**Causa**: Windows Defender bloqueó el archivo

**Solución**:
1. Click derecho en el .exe
2. Propiedades
3. Desbloquear (si aparece)
4. Ejecutar como Administrador

### El instalador no se abre en Mac

**Causa**: macOS bloqueó app de desarrollador no identificado

**Solución**:
1. Click derecho en el .dmg
2. "Abrir"
3. Confirmar "Abrir" de nuevo

O desde terminal:
```bash
xattr -d com.apple.quarantine HogwartsApp-1.0.dmg
```

### La app no se conecta a la BD

**Causa**: `configuration.properties` no está incluido

**Solución**:
- Verifica que `configuration.properties` existe en:
  `src/main/resources/es/potter/configuration.properties`
- Recompila: `mvn clean package`
- Regenera el instalador

---

## 📊 Comparación de Opciones

| Característica | Instalador | Portable |
|----------------|-----------|----------|
| **Java incluido** | ✅ Sí | ❌ No (requiere instalación) |
| **Tamaño** | ~100 MB | ~50 MB |
| **Instalación** | Sí (simple) | No |
| **Permisos admin** | Sí (Windows/Mac) | No |
| **Profesionalidad** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ |
| **Portabilidad** | ❌ | ✅ (USB, red) |
| **Tiempo generación** | 3-10 min | 1-2 min |
| **Desinstalación** | Panel control | Borrar carpeta |

---

## 📝 Checklist de Entrega

Antes de entregar al cliente:

- [ ] Instalador Mac generado y probado
- [ ] Instalador Windows generado y probado (si aplica)
- [ ] Portable generada y probada
- [ ] configuration.properties incluido en todos
- [ ] Conexión a BDs verificada
- [ ] INSTRUCCIONES-CLIENTE.md actualizado
- [ ] Archivos organizados en carpeta Entrega-Cliente/
- [ ] Canal de entrega decidido (email/drive/USB)
- [ ] Información de soporte técnico añadida

---

## 💡 Recomendación Final

**Para la mayoría de clientes:**

1. **Opción Principal**: Instalador nativo
   - Mac: HogwartsApp-1.0.dmg
   - Windows: HogwartsApp-1.0.exe

2. **Opción Alternativa**: Portable
   - Para usuarios avanzados
   - Para equipos sin permisos de admin
   - Para ejecución desde USB

**Genera ambas opciones** y deja que el cliente elija según su caso.

---

## 📞 Soporte

Si tienes problemas generando los instaladores:

1. Verifica los requisitos (Java 22, Maven)
2. Lee la sección "Solución de Problemas"
3. Revisa los logs de error
4. Consulta la documentación oficial de jpackage

---

**Última actualización**: Octubre 2024
**Versión**: 1.0