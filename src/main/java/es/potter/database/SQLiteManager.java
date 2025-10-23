package es.potter.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Gestor de base de datos SQLite embebida para aplicaciones empaquetadas.
 * <p>
 * Esta clase maneja la inicialización y ubicación de la base de datos SQLite,
 * garantizando que funcione tanto en modo desarrollo como en aplicación empaquetada (JAR).
 * </p>
 *
 * <h2>Problema que resuelve:</h2>
 * <ul>
 *   <li>Los recursos dentro de un JAR son de solo lectura</li>
 *   <li>SQLite necesita acceso de escritura al archivo de base de datos</li>
 * </ul>
 *
 * <h2>Solución:</h2>
 * <ol>
 *   <li>Extrae la BD desde resources al directorio de datos de la aplicación</li>
 *   <li>Usa una ubicación escribible en el sistema del usuario</li>
 *   <li>Solo copia la BD si no existe (preserva datos existentes)</li>
 * </ol>
 *
 * <h2>Ubicaciones de la base de datos:</h2>
 * <ul>
 *   <li><b>macOS:</b> ~/Library/Application Support/HogwartsApp/hogwarts.db</li>
 *   <li><b>Windows:</b> %APPDATA%\HogwartsApp\hogwarts.db</li>
 *   <li><b>Linux:</b> ~/.config/HogwartsApp/hogwarts.db</li>
 * </ul>
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
     * Obtiene la ruta absoluta al archivo de base de datos SQLite.
     * <p>
     * Este procedimiento garantiza que:
     * <ol>
     *   <li>El directorio de datos de la aplicación existe</li>
     *   <li>La base de datos se copia desde resources si no existe</li>
     *   <li>Se devuelve una ruta escribible en el sistema de archivos</li>
     * </ol>
     * </p>
     *
     * <h3>Flujo de ejecución:</h3>
     * <pre>
     * 1. ¿Ya se obtuvo la ruta? → Retornar caché
     * 2. ¿No existe? → Obtener directorio de datos del usuario
     * 3. Crear directorio HogwartsApp si no existe
     * 4. ¿Existe hogwarts.db en ese directorio?
     *    - SÍ: Usar la existente (preserva datos)
     *    - NO: Copiar desde resources (primera vez)
     * 5. Retornar ruta absoluta
     * </pre>
     *
     * @return Path absoluto al archivo hogwarts.db en ubicación escribible
     * @throws RuntimeException si no se puede inicializar la base de datos
     *
     * @author Wara Pacheco
     */
    public static Path getDatabasePath() {
        // Patrón Singleton: retornar si ya se calculó
        if (databasePath != null) {
            return databasePath;
        }

        try {
            // 1. Obtener directorio de datos específico del sistema operativo
            Path appDataDir = getApplicationDataDirectory();

            // 2. Crear directorio si no existe
            if (!Files.exists(appDataDir)) {
                Files.createDirectories(appDataDir);
                logger.info("Directorio de datos creado: {}", appDataDir);
            }

            // 3. Ruta completa al archivo de base de datos
            Path dbFile = appDataDir.resolve(DB_FILENAME);

            // 4. Si no existe, copiar desde resources (solo primera vez)
            if (!Files.exists(dbFile)) {
                logger.info("Base de datos no encontrada en {}. Copiando desde resources...", dbFile);
                copyDatabaseFromResources(dbFile);
                logger.info("Base de datos SQLite inicializada correctamente en: {}", dbFile);
            } else {
                logger.info("Base de datos SQLite encontrada en: {}", dbFile);
            }

            // 5. Cachear la ruta y retornarla
            databasePath = dbFile;
            return databasePath;

        } catch (IOException e) {
            String errorMsg = "Error al inicializar la base de datos SQLite: " + e.getMessage();
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    /**
     * Obtiene el directorio de datos de la aplicación según el sistema operativo.
     * <p>
     * Implementa las convenciones estándar de cada plataforma:
     * </p>
     *
     * <table border="1">
     *   <tr>
     *     <th>Sistema</th>
     *     <th>Variable de entorno</th>
     *     <th>Ruta por defecto</th>
     *   </tr>
     *   <tr>
     *     <td>macOS</td>
     *     <td>-</td>
     *     <td>~/Library/Application Support/HogwartsApp</td>
     *   </tr>
     *   <tr>
     *     <td>Windows</td>
     *     <td>APPDATA</td>
     *     <td>%APPDATA%\HogwartsApp</td>
     *   </tr>
     *   <tr>
     *     <td>Linux</td>
     *     <td>XDG_CONFIG_HOME</td>
     *     <td>~/.config/HogwartsApp</td>
     *   </tr>
     * </table>
     *
     * @return Path al directorio de datos de la aplicación
     *
     * @author Wara Pacheco
     */
    private static Path getApplicationDataDirectory() {
        String userHome = System.getProperty("user.home");
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("mac")) {
            // macOS: ~/Library/Application Support/HogwartsApp
            return Path.of(userHome, "Library", "Application Support", APP_DATA_DIR);
        } else if (os.contains("win")) {
            // Windows: %APPDATA%\HogwartsApp
            String appData = System.getenv("APPDATA");
            if (appData != null) {
                return Path.of(appData, APP_DATA_DIR);
            }
            // Fallback si APPDATA no está definido
            return Path.of(userHome, "AppData", "Roaming", APP_DATA_DIR);
        } else {
            // Linux/Unix: ~/.config/HogwartsApp (XDG Base Directory Specification)
            String xdgConfig = System.getenv("XDG_CONFIG_HOME");
            if (xdgConfig != null) {
                return Path.of(xdgConfig, APP_DATA_DIR);
            }
            return Path.of(userHome, ".config", APP_DATA_DIR);
        }
    }

    /**
     * Copia el archivo de base de datos desde los recursos del JAR al sistema de archivos.
     * <p>
     * Esta función extrae el archivo {@code hogwarts.db} embebido en el JAR
     * ({@code /es/potter/db/hogwarts.db}) y lo copia a una ubicación escribible.
     * </p>
     *
     * <h3>Detalles de implementación:</h3>
     * <ul>
     *   <li>Usa {@link Class#getResourceAsStream} para leer desde el JAR</li>
     *   <li>Usa {@link Files#copy} con {@link StandardCopyOption#REPLACE_EXISTING}</li>
     *   <li>Cierra automáticamente el InputStream con try-with-resources</li>
     * </ul>
     *
     * @param destination Path de destino donde copiar la base de datos
     * @throws IOException si no se puede leer el recurso o escribir el archivo
     *
     * @author Wara Pacheco
     */
    private static void copyDatabaseFromResources(Path destination) throws IOException {
        // Obtener el recurso como InputStream (funciona tanto en IDE como en JAR)
        try (InputStream inputStream = SQLiteManager.class.getResourceAsStream(RESOURCE_PATH)) {
            if (inputStream == null) {
                throw new IOException("No se encontró el recurso: " + RESOURCE_PATH);
            }

            // Copiar desde el InputStream al archivo de destino
            Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
            logger.debug("Base de datos copiada desde {} a {}", RESOURCE_PATH, destination);
        }
    }

    /**
     * Obtiene la URL JDBC completa para conectarse a la base de datos SQLite.
     * <p>
     * Genera una URL en el formato: {@code jdbc:sqlite:/ruta/absoluta/al/archivo.db}
     * </p>
     *
     * <h3>Ejemplo de uso:</h3>
     * <pre>{@code
     * String jdbcUrl = SQLiteManager.getJdbcUrl();
     * Connection conn = DriverManager.getConnection(jdbcUrl);
     * }</pre>
     *
     * @return String con la URL JDBC completa (ej: jdbc:sqlite:/Users/usuario/Library/Application Support/HogwartsApp/hogwarts.db)
     *
     * @author Wara Pacheco
     */
    public static String getJdbcUrl() {
        Path dbPath = getDatabasePath();
        String jdbcUrl = "jdbc:sqlite:" + dbPath.toAbsolutePath();
        logger.debug("URL JDBC de SQLite: {}", jdbcUrl);
        return jdbcUrl;
    }

}