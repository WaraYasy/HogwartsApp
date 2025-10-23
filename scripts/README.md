# Scripts de HogwartsApp

Este directorio contiene los scripts de ejecución para HogwartsApp.

## Scripts Disponibles

### `ejecutar.sh` (macOS/Linux)
Script para ejecutar el JAR portable en sistemas Unix.

**Uso:**
```bash
./scripts/ejecutar.sh
```

### `ejecutar.bat` (Windows)
Script para ejecutar el JAR portable en Windows.

**Uso:**
```cmd
scripts\ejecutar.bat
```

## Notas

- Ambos scripts buscan automáticamente el JAR portable en `target/`
- Verifican que Java 21+ esté instalado
- Muestran información sobre ubicación de logs y base de datos
- Configuran opciones óptimas de JVM