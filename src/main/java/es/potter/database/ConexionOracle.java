package es.potter.database;

import es.potter.util.Propiedades;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase para gestionar la conexión a la base de datos Oracle.
 * Proporciona métodos para establecer, obtener y cerrar conexiones a Oracle
 * utilizando configuraciones definidas en archivos de propiedades.
 *
 * @author Erlantz
 * @version 1.0
 * @since 2025-10-10
 */
@Deprecated
public class ConexionOracle {

    /** Conexión activa a la base de datos MariaDB. */
    private Connection conexionOracle = null;

    /** Logger para registrar eventos y errores de la conexión */
    private static final Logger logger = LoggerFactory.getLogger(ConexionOracle.class);

    /**
     * Constructor que establece automáticamente la conexión a la base de datos.
     * <p>
     * Carga la configuración desde el archivo {@code configuration.properties},
     * construye la URL de conexión JDBC y establece la conexión con MariaDB.
     * </p>
     *
     * @throws SQLException si ocurre un error durante el establecimiento de la conexión
     *
     * @author Erlantz
     */
    public ConexionOracle() throws SQLException{
        try {
            String url = Propiedades.getValor("db.oracle.url");
            String user = Propiedades.getValor("db.oracle.user");
            String pass = Propiedades.getValor("db.oracle.password");

            conexionOracle = DriverManager.getConnection(url, user, pass);
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
     * @author Erlantz
     */
    public Connection getConnection() {
        return conexionOracle;
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
     * @author Erlantz
     */
    public void closeConnection() {
        if (conexionOracle != null) {
            try {
                conexionOracle.close();
                logger.info("Conexión cerrada");
            } catch (SQLException e) {
                logger.error("Error al cerrar conexión: {}", e.getMessage());
            }
        }
    }

    /**
     * Metodo principal para probar la funcionalidad de la conexión a Oracle.
     * Crea una instancia de la clase, verifica la conexión y la cierra correctamente.
     * Utiliza manejo de excepciones y bloques try-catch-finally para garantizar
     * la liberación de recursos.
     *
     * @param args argumentos de línea de comandos (no utilizados)
     *
     * @author Erlantz
     */
    public static void main(String[] args) {
        ConexionOracle conexion = null;

        try {
            // Crear la conexión
            conexion = new ConexionOracle();

            // Verificar si la conexión fue exitosa
            if (conexion.getConnection() != null && !conexion.getConnection().isClosed()) {
                System.out.println("Conexión establecida correctamente con la base de datos Oracle.");
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