package es.potter.database;

import es.potter.util.Propiedades;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase para gestionar la conexión a la base de datos SQLite.
 * Proporciona métodos para establecer, obtener y cerrar conexiones a SQLite
 * utilizando configuraciones definidas en archivos de propiedades.
 * SQLite es una base de datos ligera, sin servidor y autocontenida,
 * ideal para aplicaciones embebidas y desarrollo.
 *
 * <p><b>DEPRECADA:</b> Esta clase tiene problemas al empaquetar la aplicación en JAR
 * porque usa rutas absolutas del sistema de archivos que no funcionan con recursos embebidos.
 * </p>
 *
 * <h3>Migración:</h3>
 * En lugar de usar esta clase directamente, utilizar:
 * <pre>{@code
 * // FORMA ANTIGUA (NO USAR):
 * ConexionSqlite conexion = new ConexionSqlite();
 * Connection conn = conexion.getConnection();
 *
 * // FORMA NUEVA (USAR ESTA):
 * CompletableFuture<Connection> futureConn = ConexionFactory.getConnectionAsync(TipoBaseDatos.SQLITE);
 * futureConn.thenAccept(conn -> {
 *     // usar conexión
 * });
 * }</pre>
 *
 * <p>La Factory utiliza {@link SQLiteManager} internamente para manejar correctamente
 * la base de datos embebida tanto en desarrollo como en producción empaquetada.</p>
 *
 * @see ConexionFactory#getConnectionAsync(TipoBaseDatos)
 * @see SQLiteManager
 * @author Wara Pacheco
 * @version 1.0
 * @since 2025-10-11
 * @deprecated Use {@link ConexionFactory#getConnectionAsync(TipoBaseDatos)} con {@link TipoBaseDatos#SQLITE} en su lugar
 */
@Deprecated(since = "2.0", forRemoval = true)
public class ConexionSqlite {

    /** Conexión activa a la base de datos SQLite. */
    private Connection conexionSqlite = null;

    /** Logger para registrar eventos y errores de la conexión */
    private static final Logger logger = LoggerFactory.getLogger(ConexionSqlite.class);

    /**
     * Constructor que establece automáticamente la conexión a la base de datos SQLite.
     * <p>
     * Carga la configuración desde el archivo {@code configuration.properties},
     * construye la URL de conexión JDBC y establece la conexión con SQLite.
     * SQLite no requiere servidor y almacena los datos en un archivo local.
     * </p>
     *
     * @throws SQLException si ocurre un error durante el establecimiento de la conexión
     *
     * @author Wara
     */
    public ConexionSqlite() throws SQLException{
        try {
            String url = Propiedades.getValor("db.sqlite.url");
            String user = Propiedades.getValor("db.sqlite.user");
            String pass = Propiedades.getValor("db.sqlite.password");

            conexionSqlite = DriverManager.getConnection(url, user, pass);
            // Log de conexión exitosa
            logger.info("Conexión establecida con {}", url);

        } catch (SQLException e) {
            logger.error("Conexión a BD fallida: {}", e.getMessage());
        }
    }

    /**
     * Obtiene la conexión activa a la base de datos.
     * <p>
     * Retorna la instancia de {@link Connection} establecida en el constructor.
     * Esta conexión puede ser utilizada para ejecutar consultas y transacciones.
     * </p>
     *
     * @return la conexión activa a la base de datos, o {@code null} si la conexión falló
     * @see java.sql.Connection
     *
     * @author Wara
     */
    public Connection getConnection() {
        return conexionSqlite;
    }

    /**
     * Cierra la conexión a la base de datos de forma segura.
     * <p>
     * Verifica que la conexión esté activa antes de cerrarla y registra el evento.
     * Es importante llamar a este metodo cuando se termine de usar la conexión
     * para liberar recursos del sistema.
     * </p>
     *
     * @see java.sql.Connection#close()
     *
     * @author Wara
     */
    public void closeConnection() {
        if (conexionSqlite != null) {
            try {
                conexionSqlite.close();
                logger.info("Conexión cerrada");
            } catch (SQLException e) {
                logger.error("Error al cerrar conexión: {}", e.getMessage());
            }
        }
    }

    /**
     * Metodo principal para probar la funcionalidad de la conexión a SQLite.
     * Crea una instancia de la clase, verifica la conexión y la cierra correctamente.
     * Utiliza manejo de excepciones y bloques try-catch-finally para garantizar
     * la liberación de recursos.
     * 
     * @param args argumentos de línea de comandos (no utilizados)
     *
     * @author Wara
     */
    public static void main(String[] args) {
        ConexionSqlite conexion = null;

        try {
            // Crear la conexión
            conexion = new ConexionSqlite();

            // Verificar si la conexión fue exitosa
            if (conexion.getConnection() != null && !conexion.getConnection().isClosed()) {
                System.out.println("Conexión establecida correctamente con la base de datos Sqlite.");
            } else {
                System.err.println("No se ha podido establecer la conexión con la base de datos.");
            }

        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
        } finally {
            // Cerrar conexión al finalizar
            if (conexion != null) {
                conexion.closeConnection();
            }
        }
    }
}
