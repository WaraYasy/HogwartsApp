# HogwartsApp - Guía Rápida de Uso

Bienvenido a HogwartsApp, la aplicación de gestión de estudiantes de Hogwarts.

## Cómo Ejecutar la Aplicación

### En Windows

1. **Localiza el archivo** `hogwartsApp.bat` en la carpeta de la aplicación
2. **Haz doble clic** sobre él
3. La aplicación se abrirá automáticamente

### En macOS/Linux

1. **Abre una terminal** en la carpeta de la aplicación
2. **Ejecuta** el siguiente comando:
   ```bash
   ./scripts/ejecutar.sh
   ```

## Requisitos

- **Java 21 o superior** debe estar instalado en tu ordenador
  - Si no lo tienes, descárgalo desde: https://adoptium.net/

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

**Solución:** Verifica que tienes Java 21 o superior instalado:
```bash
java -version
```

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

Si necesitas ayuda adicional, consulta la **Guía de Ayuda** completa dentro de la aplicación (menú Ayuda → Sobre HogwartsApp → Guía de Ayuda).

---

**Equipo Potter**