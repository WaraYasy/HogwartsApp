package es.potter.database;

import es.potter.util.Propiedades;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase para gestionar la conexión a la base de datos Apache Derby.
 * Proporciona métodos para establecer, obtener y cerrar conexiones a Derby
 * utilizando configuraciones definidas en archivos de propiedades.
 * 
 * @author Salca
 * @version 1.0
 * @since 2025-10-10
 */
public class ConexionApacheDerby {
    /** Conexión activa a la base de datos Derby */
    private Connection conexionDerby = null;

    /** Logger para registrar eventos y errores de la conexión */
    private static final Logger logger = LoggerFactory.getLogger(ConexionApacheDerby.class);

    /**
     * Constructor que establece automáticamente la conexión a la base de datos Derby.
     * Lee la configuración de conexión desde el archivo de propiedades y establece
     * la conexión utilizando DriverManager.
     * 
     * @throws SQLException si ocurre un error al establecer la conexión
     */
    public ConexionApacheDerby() throws SQLException {
        try {
            String url = Propiedades.getValor("db.derby.url");
            String user = Propiedades.getValor("db.derby.user");
            String pass = Propiedades.getValor("db.derby.password");

            conexionDerby = DriverManager.getConnection(url, user, pass);
            logger.info("Conexión Derby establecida en {}", url);

        } catch (SQLException e) {
            logger.error("Error al conectar con Derby: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Obtiene la conexión activa a la base de datos Derby.
     * 
     * @return la conexión a Derby, o null si no está establecida
     */
    public Connection getConnection() {
        return conexionDerby;
    }

    /**
     * Cierra la conexión activa a la base de datos Derby de forma segura.
     * Si la conexión está abierta, la cierra y registra el evento.
     * Si ocurre un error durante el cierre, lo registra en el log.
     */
    public void closeConnection() {
        if (conexionDerby != null) {
            try {
                conexionDerby.close();
                logger.info("Conexión Derby cerrada correctamente.");
            } catch (SQLException e) {
                logger.error("Error al cerrar la conexión Derby: {}", e.getMessage());
            }
        }
    }

    /**
     * Método principal para probar la funcionalidad de la conexión a Derby.
     * Crea una instancia de la clase, verifica la conexión y la cierra correctamente.
     * Utiliza manejo de excepciones y bloques try-catch-finally para garantizar
     * la liberación de recursos.
     * 
     * @param args argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        ConexionApacheDerby conexion = null;

        try {
            // Crear la conexión
            conexion = new ConexionApacheDerby();

            // Verificar si la conexión fue exitosa
            if (conexion.getConnection() != null && !conexion.getConnection().isClosed()) {
                System.out.println("Conexión establecida correctamente con la base de datos Apache Derby.");
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
