package es.potter.database;

import es.potter.util.Propiedades;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase para gestionar la conexión a la base de datos H2.
 * Proporciona métodos para establecer, obtener y cerrar conexiones a H2
 * utilizando configuraciones definidas en archivos de propiedades.
 * 
 * @author Nizam
 * @version 1.0
 * @since 2025-10-10
 */
public class ConexionH2 {

    /** Conexión activa a la base de datos H2 */
    private Connection conexionH2 = null;

    /** Logger para registrar eventos y errores de la conexión */
    private static final Logger logger = LoggerFactory.getLogger(ConexionH2.class);

    /**
     * Constructor que establece automáticamente la conexión a la base de datos H2.
     * Lee la configuración de conexión desde el archivo de propiedades y establece
     * la conexión utilizando DriverManager.
     * 
     * @throws SQLException si ocurre un error al establecer la conexión
     */
    public ConexionH2() throws SQLException {
        try {
            String url = Propiedades.getValor("db.h2.url");
            String user = Propiedades.getValor("db.h2.user");
            String pass = Propiedades.getValor("db.h2.password");

            conexionH2 = DriverManager.getConnection(url, user, pass);
            logger.info("Conexión H2 establecida en {}", url);

        } catch (SQLException e) {
            logger.error("Error al conectar con H2: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Obtiene la conexión activa a la base de datos H2.
     * 
     * @return la conexión a H2, o null si no está establecida
     */
    public Connection getConnection() {
        return conexionH2;
    }

    /**
     * Cierra la conexión activa a la base de datos H2 de forma segura.
     * Si la conexión está abierta, la cierra y registra el evento.
     * Si ocurre un error durante el cierre, lo registra en el log.
     * Es importante cerrar las conexiones H2 para liberar recursos correctamente.
     */
    public void closeConnection() {
        if (conexionH2 != null) {
            try {
                conexionH2.close();
                logger.info("Conexión H2 cerrada correctamente.");
            } catch (SQLException e) {
                logger.error("Error al cerrar la conexión H2: {}", e.getMessage());
            }
        }
    }

    /**
     * Método principal para probar la funcionalidad de la conexión a H2.
     * Crea una instancia de la clase, verifica la conexión y la cierra correctamente.
     * Utiliza manejo de excepciones y bloques try-catch-finally para garantizar
     * la liberación de recursos.
     * 
     * @param args argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        ConexionH2 conexion = null;

        try {
            // Crear la conexión
            conexion = new ConexionH2();

            // Verificar si la conexión fue exitosa
            if (conexion.getConnection() != null && !conexion.getConnection().isClosed()) {
                System.out.println("Conexión establecida correctamente con la base de datos H2.");
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
