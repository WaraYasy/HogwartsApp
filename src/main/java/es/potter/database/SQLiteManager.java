package es.potter.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Gestiona la ubicación e inicialización de la base de datos SQLite.
 * PROBLEMA: Los archivos dentro de un JAR son de solo lectura, pero SQLite necesita escribir.
 * SOLUCIÓN: Copia la BD desde el JAR a una carpeta del usuario donde sí se puede escribir.
 * UBICACIONES según el sistema operativo:
 * - macOS:~/Library/Application Support/HogwartsApp/hogwarts.db
 * - Windows: %APPDATA%\HogwartsApp\hogwarts.db
 * - Linux:~/.config/HogwartsApp/hogwarts.db
 *
 * @author Wara Pacheco
 * @version 2.0
 * @since 2025-10-23
 */
public class SQLiteManager {

    /** Logger para registrar eventos y errores */
    private static final Logger logger = LoggerFactory.getLogger(SQLiteManager.class);

    /** Nombre del archivo de base de datos */
    private static final String DB_FILENAME = "hogwarts.db";

    /** Ruta del recurso embebido en el JAR */
    private static final String RESOURCE_PATH = "/es/potter/db/" + DB_FILENAME;

    /** Directorio de datos de la aplicación */
    private static final String APP_DATA_DIR = "HogwartsApp";

    /** Instancia única de la ruta de la base de datos (Singleton) */
    private static Path databasePath = null;

    /**
     * Obtiene la ruta donde está guardada la base de datos.
     * CÓMO FUNCIONA:
     * 1. Si ya se calculó antes, devuelve la ruta guardada (caché)
     * 2. Busca la carpeta de datos de la app según el SO
     * 3. Crea la carpeta si no existe
     * 4. Si es la primera vez, copia hogwarts.db desde el JAR
     * 5. Guarda y devuelve la ruta
     *
     * @return Ruta absoluta al archivo hogwarts.db
     * @throws RuntimeException si hay problemas al crear/copiar la BD
     */
    public static Path getDatabasePath() {
        // Si ya tenemos la ruta guardada, la devolvemos directamente
        if (databasePath != null) {
            return databasePath;
        }

        try {
            // Obtener la carpeta donde guardaremos la BD (depende del SO)
            Path carpetaDatos = getApplicationDataDirectory();

            // Crear la carpeta si no existe
            if (!Files.exists(carpetaDatos)) {
                Files.createDirectories(carpetaDatos);
                logger.info("Directorio de datos creado: {}", carpetaDatos);
            }

            // Construir la ruta completa al archivo hogwarts.db
            Path archivoBD = carpetaDatos.resolve(DB_FILENAME);

            // Si es la primera vez, copiar la BD desde el JAR
            if (!Files.exists(archivoBD)) {
                logger.info("Base de datos no encontrada. Copiando desde el JAR...");
                copyDatabaseFromResources(archivoBD);
                logger.info("Base de datos inicializada en: {}", archivoBD);
            } else {
                logger.info("Base de datos encontrada en: {}", archivoBD);
            }

            // Guardar la ruta para no tener que calcularla de nuevo
            databasePath = archivoBD;
            return databasePath;

        } catch (IOException e) {
            String errorMsg = "Error al inicializar la base de datos: " + e.getMessage();
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    /**
     * Decide dónde guardar los datos según el sistema operativo.
     * Cada SO tiene su propia convención:
     * - macOS:   ~/Library/Application Support/HogwartsApp
     * - Windows: %APPDATA%\HogwartsApp
     * - Linux:   ~/.config/HogwartsApp
     *
     * @return Ruta al directorio de datos de la aplicación
     */
    private static Path getApplicationDataDirectory() {
        String carpetaUsuario = System.getProperty("user.home");
        String sistemaOperativo = System.getProperty("os.name").toLowerCase();

        if (sistemaOperativo.contains("mac")) {
            return Path.of(carpetaUsuario, "Library", "Application Support", APP_DATA_DIR);

        } else if (sistemaOperativo.contains("win")) {
            String appData = System.getenv("APPDATA");
            if (appData != null) {
                return Path.of(appData, APP_DATA_DIR);
            }
            // Si APPDATA no existe, usar la ruta por defecto
            return Path.of(carpetaUsuario, "AppData", "Roaming", APP_DATA_DIR);

        } else {
            // Linux: usar XDG_CONFIG_HOME si está definido, sino ~/.config
            String xdgConfig = System.getenv("XDG_CONFIG_HOME");
            if (xdgConfig != null) {
                return Path.of(xdgConfig, APP_DATA_DIR);
            }
            return Path.of(carpetaUsuario, ".config", APP_DATA_DIR);
        }
    }

    /**
     * Copia hogwarts.db desde el JAR a una ubicación donde se pueda escribir.
     * Extrae el archivo /es/potter/db/hogwarts.db del JAR y lo copia al
     * directorio de datos del usuario.
     *
     * @param destino Dónde guardar la copia de la base de datos
     * @throws IOException si no se puede leer o copiar el archivo
     */
    private static void copyDatabaseFromResources(Path destino) throws IOException {
        // Leer el archivo desde dentro del JAR
        try (InputStream archivoEnJAR = SQLiteManager.class.getResourceAsStream(RESOURCE_PATH)) {
            if (archivoEnJAR == null) {
                throw new IOException("No se encontró el recurso: " + RESOURCE_PATH);
            }

            // Copiarlo a la ubicación de destino
            Files.copy(archivoEnJAR, destino, StandardCopyOption.REPLACE_EXISTING);
            logger.debug("BD copiada desde {} a {}", RESOURCE_PATH, destino);
        }
    }

    /**
     * Genera la URL para conectarse a la base de datos.
     * Devuelve algo como: jdbc:sqlite:/ruta/completa/a/hogwarts.db
     * Ejemplo de uso:
     * String url = SQLiteManager.getJdbcUrl();
     * Connection conn = DriverManager.getConnection(url);
     *
     * @return URL JDBC para conectar con SQLite
     */
    public static String getJdbcUrl() {
        Path rutaBD = getDatabasePath();
        String urlJdbc = "jdbc:sqlite:" + rutaBD.toAbsolutePath();
        logger.debug("URL JDBC: {}", urlJdbc);
        return urlJdbc;
    }

}