c# Guía de Distribución - HogwartsApp

## Resumen: Cómo entregar la aplicación al cliente

Tienes **2 opciones profesionales** para entregar tu aplicación JavaFX al cliente:

---

## Opción 1: Instalador Nativo ⭐ (RECOMENDADO)

### Crear el instalador:

```bash
./crear-instalador.sh
```

### Qué genera:

- **Mac**: `HogwartsApp.dmg` (~150 MB)
- **Windows**: `HogwartsApp.exe` (~150 MB)
- **Linux**: `HogwartsApp.deb` (~150 MB)

### Ubicación:
```
instalador-output/HogwartsApp.[dmg|exe|deb]
```

### Ventajas:

✅ **Java incluido** - Cliente NO necesita instalar Java
✅ **Instalación profesional** - Como cualquier programa comercial
✅ **Doble click y funciona** - Experiencia nativa del SO
✅ **Ícono en menú/escritorio** - Integración completa
✅ **Desinstalación fácil** - Desde panel de control

### Para el cliente:

**Windows:**
1. Doble click en `HogwartsApp.exe`
2. Seguir asistente de instalación
3. Ejecutar desde menú Inicio o escritorio

**Mac:**
1. Doble click en `HogwartsApp.dmg`
2. Arrastrar a carpeta Aplicaciones
3. Ejecutar desde Launchpad o Aplicaciones

**Linux:**
1. `sudo dpkg -i HogwartsApp.deb`
2. O doble click (Software Center)
3. Ejecutar desde menú de aplicaciones

### Cuándo usar:

- ✅ Cliente no técnico
- ✅ Instalación permanente en el equipo
- ✅ Máxima profesionalidad
- ✅ Cliente no tiene/no quiere instalar Java

---

## Opción 2: Versión Portable 💼

### Crear la versión portable:

```bash
./crear-portable.sh
```

### Qué genera:

- `HogwartsApp-Portable-[sistema].zip` (~150 MB con Java incluido)

### Ubicación:
```
portable/HogwartsApp-Portable-[sistema].zip
```

### Ventajas:

✅ **Java incluido** - Cliente NO necesita instalar Java
✅ **Sin instalación** - Descomprimir y ejecutar
✅ **Portable** - Funciona desde USB, cualquier carpeta
✅ **No modifica el sistema** - Todo autocontenido
✅ **Fácil de eliminar** - Solo borrar carpeta

### Para el cliente:

1. Descomprimir el ZIP en cualquier ubicación
2. Abrir la carpeta `HogwartsApp-Portable`
3. Ejecutar:
   - **Windows**: Doble click en `ejecutar.bat`
   - **Mac/Linux**: Doble click en `ejecutar.sh`

### Cuándo usar:

- ✅ Cliente sin permisos de administrador
- ✅ Ejecución desde USB/red compartida
- ✅ Cliente quiere "probar" sin instalar
- ✅ Múltiples versiones en el mismo equipo

---

## Comparación Rápida

| Característica | Instalador | Portable |
|----------------|-----------|----------|
| **Requiere instalación** | Sí (simple) | No |
| **Permisos admin necesarios** | Sí | No |
| **Java incluido** | ✅ Sí | ✅ Sí |
| **Profesionalidad** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ |
| **Facilidad cliente** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ |
| **Tamaño** | ~150 MB | ~150 MB |
| **Ícono escritorio/menú** | ✅ Sí | ❌ No |
| **Desinstalación** | Panel control | Borrar carpeta |
| **Funciona desde USB** | ❌ No | ✅ Sí |

---

## Mi Recomendación

### Para entrega formal al cliente:

1. **Crea AMBAS versiones**
2. **Entrega el instalador como opción principal**
3. **Incluye la portable como alternativa**

### Ejemplo de entrega:

```
Entrega-HogwartsApp/
├── HogwartsApp-Instalador.dmg         (Opción 1 - Recomendada)
├── HogwartsApp-Portable.zip           (Opción 2 - Alternativa)
├── Manual-Instalacion.pdf             (Instrucciones)
└── LEEME.txt                          (Notas importantes)
```

---

## Requisitos del Sistema (para el cliente)

### Hardware mínimo:

- **Procesador**: Dual-core 2.0 GHz o superior
- **RAM**: 4 GB (recomendado 8 GB)
- **Disco**: 500 MB libres
- **Pantalla**: 1280x720 o superior

### Software:

- ✅ **NO requiere Java** (incluido en ambas versiones)
- ✅ Sistema operativo:
  - Windows 10 o superior
  - macOS 10.14 o superior
  - Linux (Ubuntu 20.04+ o equivalente)

### Red:

- ⚠️ **Conexión al servidor de bases de datos**: `100.104.4.128`
- Puertos necesarios:
  - Oracle: 1521
  - MariaDB: 3306
  - H2: 9092
  - Derby: 1527
  - HSQLDB: 9001

---

## Solución de Problemas

### Error: "Cannot connect to database"

**Causa**: No hay conexión al servidor `100.104.4.128`

**Solución**:
1. Verificar conexión de red
2. Verificar que los puertos no estén bloqueados (firewall)
3. Verificar que el servidor esté activo

### Error: En Mac "La aplicación no se puede abrir porque proviene de un desarrollador no identificado"

**Solución**:
1. Click derecho en la app
2. Seleccionar "Abrir"
3. Confirmar "Abrir" en el diálogo
4. O en Preferencias del Sistema > Seguridad y Privacidad > "Abrir de todos modos"

### La aplicación se cierra inmediatamente

**Causa**: Problema con JavaFX o falta de display

**Solución**:
1. Verificar que se ejecuta en entorno con interfaz gráfica
2. No ejecutar desde SSH sin X11 forwarding
3. Ver logs en carpeta `logs/`

---

## Notas para Docker

### ¿Y Docker?

Docker **NO es recomendado** para esta aplicación porque:

- ❌ JavaFX con GUI en Docker es muy complicado
- ❌ Requiere X11/XQuartz (complejo para el cliente)
- ❌ Rendimiento gráfico pobre
- ❌ No es práctico para apps de escritorio

**Docker es excelente para**:
- ✅ Bases de datos (ya lo tienes en `100.104.4.128`)
- ✅ APIs REST / microservicios
- ✅ Backend sin interfaz gráfica

Los archivos Docker están incluidos en el proyecto como **referencia educativa**, pero no para distribución al cliente.

---

## Checklist de Entrega

Antes de entregar al cliente, verifica:

- [ ] Has probado el instalador/portable en el sistema operativo objetivo
- [ ] La aplicación se conecta correctamente a las bases de datos
- [ ] Los logs se generan correctamente
- [ ] Incluyes manual de usuario
- [ ] Incluyes información de contacto para soporte
- [ ] Has creado backup del proyecto
- [ ] Has documentado las credenciales de BD (si es necesario)

---

## Scripts Disponibles

En el proyecto tienes:

### Para crear distribuciones:

```bash
./crear-instalador.sh    # Generar instalador nativo
./crear-portable.sh      # Generar versión portable
```

### Desarrollo:

```bash
mvn clean javafx:run     # Ejecutar desde código fuente
mvn clean package        # Compilar JAR
```

---

## Contacto y Soporte

Documenta esta información para el cliente:

- **Soporte técnico**: [Tu email/teléfono]
- **Documentación**: [URL o ubicación]
- **Repositorio** (si aplica): [URL Git]
- **Versión actual**: 1.0
- **Fecha de entrega**: [Fecha]

---

## Resumen Ejecutivo

**Para entregar la aplicación al cliente de forma profesional:**

1. ✅ **Ejecuta**: `./crear-instalador.sh`
2. ✅ **Ejecuta**: `./crear-portable.sh`
3. ✅ **Entrega ambas versiones** al cliente
4. ✅ **Recomienda el instalador** como opción principal
5. ✅ **Incluye la portable** como alternativa

**El cliente NO necesita:**
- ❌ Instalar Java
- ❌ Conocimientos técnicos
- ❌ Configurar nada
- ❌ Docker

**El cliente SOLO necesita:**
- ✅ Conexión de red a `100.104.4.128`
- ✅ Doble click en el instalador o ejecutar el portable

¡Listo para entregar! 🚀