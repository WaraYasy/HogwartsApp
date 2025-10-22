# HogwartsApp - Instrucciones para el Cliente

## ¡Su aplicación está lista!

Le entregamos **2 versiones** de HogwartsApp para que elija la que mejor se adapte a sus necesidades:

---

## Opción 1: Instalador (RECOMENDADO) ⭐

### Archivo: `HogwartsApp-1.0.dmg` (97 MB)

### Para Mac:

1. **Abra el archivo** `HogwartsApp-1.0.dmg`
2. **Arrastre** el ícono de HogwartsApp a la carpeta Aplicaciones
3. **Abra** la aplicación desde Launchpad o desde Aplicaciones
4. Si aparece un aviso de seguridad:
   - Click derecho en la app → "Abrir"
   - O vaya a Preferencias del Sistema → Seguridad y Privacidad → "Abrir de todos modos"

### Ventajas:
✅ **Java incluido** - No necesita instalar nada más
✅ **Instalación profesional** - Como cualquier programa de Mac
✅ **Fácil acceso** - Aparece en Launchpad y Aplicaciones
✅ **Lista para usar** - Doble click y funciona

---

## Opción 2: Versión Portable 💼

### Archivo: `HogwartsApp-Portable-macos.zip` (49 MB)

### Instrucciones:

1. **Descomprima** el archivo ZIP en cualquier ubicación
2. **Entre** a la carpeta `HogwartsApp-Portable`
3. **Ejecute**:
   - Mac: Doble click en `ejecutar.sh`
   - Windows: Doble click en `ejecutar.bat`

### Ventajas:
✅ **Sin instalación** - Solo descomprimir y ejecutar
✅ **Portable** - Funciona desde USB o cualquier carpeta
✅ **No modifica el sistema** - Todo autocontenido

### Nota:
⚠️ Esta versión **requiere Java 21+ instalado**
- Si no tiene Java, descárguelo desde: https://adoptium.net/

---

## Requisitos del Sistema

### Hardware mínimo:
- Procesador: Dual-core 2.0 GHz o superior
- RAM: 4 GB (recomendado 8 GB)
- Disco: 500 MB libres
- Pantalla: 1280x720 o superior

### Software:
- **Opción 1 (Instalador)**: ✅ No requiere nada más
- **Opción 2 (Portable)**: Requiere Java 21+

### Red:
La aplicación necesita conexión al servidor de bases de datos:
- Servidor: `100.104.4.128`
- Puertos: 3306, 1521, 9092, 1527, 9001

---

## Primeros Pasos

1. **Instale o descomprima** la aplicación (según la opción elegida)
2. **Ejecute** la aplicación
3. **Verifique conexión** - La aplicación se conectará automáticamente a las bases de datos
4. **Empiece a trabajar** - ¡Todo listo!

---

## Bases de Datos Configuradas

La aplicación se conecta automáticamente a:

| Base de Datos | Dirección | Puerto |
|---------------|-----------|--------|
| Oracle | 100.104.4.128 | 1521 |
| MariaDB | 100.104.4.128 | 3306 |
| H2 | 100.104.4.128 | 9092 |
| Apache Derby | 100.104.4.128 | 1527 |
| HSQLDB | 100.104.4.128 | 9001 |
| SQLite | Local (embebida) | - |

No necesita configurar nada, la conexión es automática.

---

## Solución de Problemas

### ❌ "La aplicación no se abre"

**Mac - Aviso de seguridad:**
1. Click derecho en la app
2. Seleccionar "Abrir"
3. Confirmar "Abrir"

O:
1. Ir a Preferencias del Sistema
2. Seguridad y Privacidad
3. Click en "Abrir de todos modos"

**Portable - Falta Java:**
- Instale Java 21+ desde https://adoptium.net/

### ❌ "Cannot connect to database"

**Causa**: No hay conexión al servidor de bases de datos

**Solución**:
1. Verifique su conexión de red
2. Verifique que puede acceder a `100.104.4.128`
3. Verifique que los puertos no estén bloqueados por firewall
4. Contacte con soporte técnico si persiste

### ❌ La aplicación se cierra inmediatamente

**Posibles causas**:
- Problema con la configuración de bases de datos
- Java no instalado (solo versión portable)

**Solución**:
1. Revise los logs en la carpeta `logs/`
2. Contacte con soporte técnico con el archivo de log

---

## Logs y Diagnóstico

La aplicación genera archivos de log automáticamente:

**Ubicación de los logs:**
- **Instalador**: `/Users/[su-usuario]/Library/Application Support/HogwartsApp/logs/`
- **Portable**: Carpeta `logs/` dentro de `HogwartsApp-Portable/`

Si experimenta problemas, estos archivos contienen información útil para diagnóstico.

---

## Desinstalación

### Instalador:
1. Arrastre HogwartsApp desde Aplicaciones a la Papelera
2. Vacíe la Papelera

### Portable:
1. Elimine la carpeta `HogwartsApp-Portable`
2. Listo

---

## Soporte Técnico

Para asistencia técnica, contacte con:

- **Email**: [su-email-de-soporte]
- **Teléfono**: [su-teléfono]
- **Horario**: [su-horario-de-atención]

Al contactar, por favor tenga a mano:
- Versión de la aplicación: 1.0
- Sistema operativo y versión
- Descripción del problema
- Archivos de log (si es posible)

---

## Información del Producto

- **Nombre**: HogwartsApp
- **Versión**: 1.0
- **Fecha de entrega**: Octubre 2024
- **Desarrollado por**: [Su Nombre/Empresa]
- **Copyright**: © 2024 Hogwarts School

---

## Recomendación

Para la mayoría de usuarios, recomendamos **Opción 1: Instalador**
- Más fácil de usar
- No requiere Java instalado
- Experiencia profesional

Use **Opción 2: Portable** solo si:
- No tiene permisos de administrador
- Quiere ejecutar desde USB
- Prefiere no instalar nada en el sistema

---

¡Gracias por confiar en nosotros!

Si tiene alguna duda, no dude en contactarnos.
