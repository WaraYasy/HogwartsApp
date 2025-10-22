# GitHub Actions - Build Releases

## ¿Qué hace este workflow?

El workflow `build-releases.yml` **compila automáticamente** tu aplicación HogwartsApp para **Windows, Mac y Linux** creando:

1. **Instaladores nativos** (.exe, .dmg, .deb) con Java incluido
2. **Versiones portable** (.zip) que requieren Java 21+

## ¿Cuándo se ejecuta?

El workflow se ejecuta automáticamente en estos casos:

### 1. Push a ramas específicas
```bash
git push origin pruebas-despliegue
# o
git push origin main
```

### 2. Al crear un tag/release
```bash
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin v1.0.0
```

### 3. Manualmente desde GitHub
- Ve a: Actions → Build Release Installers → Run workflow

## ¿Dónde descargar los instaladores?

### Después de un push normal:

1. Ve a tu repositorio en GitHub
2. Click en **Actions**
3. Click en el workflow ejecutado
4. Scroll abajo a **Artifacts**
5. Descarga:
   - `HogwartsApp-Windows-Installer` (instalador .exe)
   - `HogwartsApp-Windows-Portable` (portable .zip)
   - `HogwartsApp-macOS-Installer` (instalador .dmg)
   - `HogwartsApp-macOS-Portable` (portable .zip)
   - `HogwartsApp-Linux-Installer` (instalador .deb)
   - `HogwartsApp-Linux-Portable` (portable .zip)

### Después de crear un release/tag:

1. Ve a tu repositorio en GitHub
2. Click en **Releases**
3. Todos los instaladores están adjuntos al release

## Crear un Release (Recomendado para entrega al cliente)

```bash
# 1. Asegúrate de estar en la rama correcta
git checkout pruebas-despliegue

# 2. Crea un tag
git tag -a v1.0.0 -m "Release 1.0.0 - Primera entrega al cliente"

# 3. Sube el tag
git push origin v1.0.0

# 4. Espera 10-15 minutos (GitHub Actions compilará todo)

# 5. Ve a GitHub → Releases → Descarga todos los instaladores
```

## ¿Qué se genera?

### Para Windows:
- ✅ `HogwartsApp-1.0.exe` (~100 MB) - Instalador con Java incluido
- ✅ `HogwartsApp-Portable-windows.zip` (~50 MB) - Versión portable

### Para macOS:
- ✅ `HogwartsApp-1.0.dmg` (~100 MB) - Instalador con Java incluido
- ✅ `HogwartsApp-Portable-macos.zip` (~50 MB) - Versión portable

### Para Linux:
- ✅ `HogwartsApp_1.0_amd64.deb` (~100 MB) - Paquete Debian con Java incluido
- ✅ `HogwartsApp-Portable-linux.zip` (~50 MB) - Versión portable

## Ventajas de usar GitHub Actions

✅ **Compilación automática** - No necesitas Windows/Linux para generar sus instaladores
✅ **Multiplataforma** - Crea instaladores para los 3 sistemas operativos
✅ **Gratis** - GitHub Actions es gratis para repositorios públicos
✅ **Consistente** - Mismo proceso cada vez
✅ **Profesional** - Releases organizados con notas de versión

## Tiempo de compilación

- **Windows**: ~10-12 minutos
- **macOS**: ~10-12 minutos
- **Linux**: ~8-10 minutos

**Total**: ~15 minutos para los 3 sistemas operativos (se ejecutan en paralelo)

## Ejemplo de uso completo

### Escenario: Entregar versión 1.0 al cliente

```bash
# 1. Asegurarte de que todo funciona localmente
mvn clean package
./crear-instalador.sh  # Probar en tu Mac

# 2. Commit y push
git add .
git commit -m "RELEASE: Versión 1.0 lista para cliente"
git push origin pruebas-despliegue

# 3. Crear release
git tag -a v1.0.0 -m "Release 1.0.0 - Entrega al cliente"
git push origin v1.0.0

# 4. Esperar a que GitHub Actions termine (~15 min)

# 5. Ir a GitHub → Releases → v1.0.0

# 6. Descargar todos los instaladores

# 7. Entregar al cliente:
#    - HogwartsApp-1.0.exe (Windows)
#    - HogwartsApp-1.0.dmg (macOS)
#    - HogwartsApp_1.0_amd64.deb (Linux)
#    - Las versiones portable como alternativa
#    - INSTRUCCIONES-CLIENTE.md
```

## Ver el progreso

1. Ve a GitHub → tu repositorio
2. Click en **Actions**
3. Verás el workflow en ejecución con barras de progreso
4. Click en el workflow para ver los logs detallados

## Troubleshooting

### El workflow falla

1. Ve a Actions → Click en el workflow fallido
2. Lee los logs para ver el error
3. Errores comunes:
   - Falta Maven (no debería pasar)
   - Error en la compilación (verifica que compila localmente)
   - Falta jpackage (no debería pasar con Java 22)

### Los artifacts no aparecen

- Espera a que el workflow termine completamente (check verde ✓)
- Scroll abajo en la página del workflow hasta "Artifacts"

### Quiero cambiar la versión

Edita `.github/workflows/build-releases.yml`:
```yaml
--app-version "1.0"  # Cambiar aquí
```

## Notas importantes

- Los artifacts se guardan por **30 días** por defecto
- Los releases son **permanentes** (a menos que los borres)
- Para entregar al cliente, usa **releases** (más profesional)
- Puedes ejecutar el workflow manualmente sin hacer push

## Próximos pasos

Después de subir esto:

1. Haz push:
   ```bash
   git add .github/
   git commit -m "CI: Añadir GitHub Actions para builds multiplataforma"
   git push origin pruebas-despliegue
   ```

2. Ve a Actions y observa cómo se compilan los instaladores automáticamente

3. Cuando esté listo para release:
   ```bash
   git tag -a v1.0.0 -m "Primera versión estable"
   git push origin v1.0.0
   ```

4. Espera 15 minutos y tendrás todos los instaladores en Releases

¡Listo para producción! 🚀