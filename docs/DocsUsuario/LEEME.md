# HogwartsApp - Guía Rápida de Uso

Bienvenido a HogwartsApp, la aplicación de gestión de estudiantes de Hogwarts.

## Requisitos Previos

### Tailscale (Obligatorio)
Esta aplicación requiere **Tailscale** para conectarse a las bases de datos remotas.

**¿Qué es Tailscale?**
Es una VPN que permite conectar tu ordenador de forma segura a la red del proyecto.

**Instalación:**
1. Descarga Tailscale desde: https://tailscale.com/download
2. Instala el programa en tu ordenador
3. Inicia sesión con la cuenta proporcionada por el administrador
4. Asegúrate de que Tailscale esté activo antes de usar la aplicación

Para más detalles, consulta la **Guía de Instalación** completa en el README del proyecto.

### Java 21 o superior
La aplicación necesita Java 21 o superior para funcionar.
- Descarga desde: https://adoptium.net/

## Cómo Ejecutar la Aplicación

### En Windows

1. **Asegúrate de que Tailscale esté activo** (icono verde en la barra de tareas)
2. **Haz doble clic** en el archivo `hogwartsApp.bat`
3. La aplicación se abrirá automáticamente

**Nota:** El archivo `.bat` debe estar en la misma carpeta que el `.jar` de la aplicación.

### En macOS/Linux

Para sistemas macOS/Linux, consulta las instrucciones de ejecución en el README del proyecto o contacta al administrador.

## Guía de Ayuda

Para aprender a usar todas las funcionalidades de HogwartsApp:

1. Abre la aplicación
2. Ve al menú **Ayuda**
3. Selecciona **Sobre HogwartsApp**
4. Haz clic en **Guía de Ayuda**

Allí encontrarás información detallada sobre:
- Cómo gestionar alumnos
- Cómo usar las diferentes casas
- Funciones avanzadas
- Y mucho más

## Problemas Comunes

### La aplicación no arranca

**Solución 1:** Verifica que Tailscale esté activo
```bash
# macOS/Linux
tailscale status

# Windows: busca el icono en la barra de tareas
```

**Solución 2:** Verifica que tienes Java 21 o superior instalado:
```bash
java -version
```

### Error de conexión a la base de datos

**Causa:** Tailscale no está activo o no tienes acceso a la red.

**Solución:**
1. Activa Tailscale
2. Verifica que estás conectado a la red del proyecto
3. Contacta al administrador si no tienes acceso

### Aparece un error de permisos

**Solución (Windows):** Ejecuta el archivo `.bat` como administrador (clic derecho → Ejecutar como administrador)

**Solución (macOS/Linux):** Asegúrate de que el script tiene permisos de ejecución:
```bash
chmod +x scripts/ejecutar.sh
```

## Ubicación de los Datos

Tus datos se guardan automáticamente en:

- **Windows:** `%APPDATA%\HogwartsApp\hogwarts.db`
- **macOS:** `~/Library/Application Support/HogwartsApp/hogwarts.db`
- **Linux:** `~/.config/HogwartsApp/hogwarts.db`

**Importante:** Tus datos NO se borran al cerrar la aplicación. Se guardan de forma permanente.

## Soporte

Si necesitas ayuda adicional:
- Consulta la **Guía de Ayuda** completa dentro de la aplicación (menú Ayuda → Sobre HogwartsApp → Guía de Ayuda)
- Revisa el archivo **README.md** del proyecto para información técnica
- Contacta al equipo de desarrollo

---

**Equipo Potter**
