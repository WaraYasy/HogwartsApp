package es.potter.database;

import es.potter.util.Propiedades;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase para gestionar la conexión a la base de datos HSQLDB.
 * Proporciona métodos para establecer, obtener y cerrar conexiones a HSQLDB
 * utilizando configuraciones definidas en archivos de propiedades.
 * 
 * @author Wara Pacheco
 * @version 1.0
 * @since 2025-10-11
 */
public class ConexionHSQLDB {
        /** Conexión activa a la base de datos HSQLDB. */
    private Connection conexionHSQL = null;

    /**
     * Logger para registrar eventos, errores y mensajes de depuración durante el ciclo de vida de la aplicación.
     */
    private static final Logger logger = LoggerFactory.getLogger(ConexionHSQLDB.class);

    /**
     * Constructor que establece automáticamente la conexión a la base de datos HSQLDB.
     * <p>
     * Carga la configuración desde el archivo {@code configuration.properties},
     * construye la URL de conexión JDBC y establece la conexión con HSQLDB.
     * </p>
     *
     * @throws SQLException si ocurre un error durante el establecimiento de la conexión
     */
    public ConexionHSQLDB() throws SQLException{
        try {
            String url = Propiedades.getValor("db.hsqldb.url");
            String user = Propiedades.getValor("db.hsqldb.user");
            String pass = Propiedades.getValor("db.hsqldb.password");

            conexionHSQL = DriverManager.getConnection(url, user, pass);
            // Log de conexión exitosa
            logger.info("Conexión establecida con {}", url);

        } catch (SQLException e) {
            logger.error("Conexión a BD fallida: " + e.getMessage());
        }
    }

    /**
     * Obtiene la conexión activa a la base de datos.
     * <p>
     * Retorna la instancia de {@link Connection} establecida en el constructor.
     * </p>
     *
     * @return la conexión activa a la base de datos, o {@code null} si la conexión falló
     * @see java.sql.Connection
     */
    public Connection getConnection() {
        return conexionHSQL;
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
     */
    public void closeConnection() {
        if (conexionHSQL != null) {
            try {
                conexionHSQL.close();
                logger.info("Conexión cerrada");
            } catch (SQLException e) {
                logger.error("Error al cerrar conexión: " + e.getMessage());
            }
        }
    }

    /**
     * Método principal para probar la funcionalidad de la conexión a HSQLDB.
     * Crea una instancia de la clase, verifica la conexión y la cierra correctamente.
     * Utiliza manejo de excepciones y bloques try-catch-finally para garantizar
     * la liberación de recursos.
     * 
     * @param args argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        ConexionHSQLDB conexion = null;

        try {
            // Crear la conexión
            conexion = new ConexionHSQLDB();

            // Verificar si la conexión fue exitosa
            if (conexion.getConnection() != null && !conexion.getConnection().isClosed()) {
                System.out.println("Conexión establecida correctamente con la base de datos HSQLDB.");
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
