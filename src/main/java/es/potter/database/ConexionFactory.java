package es.potter.database;

import es.potter.util.Propiedades;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Factory para gestionar conexiones a diferentes bases de datos.
 * Simplifica la creación de conexiones y proporciona un punto centralizado
 * para la configuración y manejo de todas las conexiones.
 *
 * @author Wara Pacheco
 * @version 1.5
 * @since 2025-10-12
 */
public class ConexionFactory {

    private static final Logger logger = LoggerFactory.getLogger(ConexionFactory.class);

    /**
     * Crea una conexión a la base de datos especificada.
     *
     * @param tipo el tipo de base de datos
     * @return la conexión establecida
     * @throws SQLException si ocurre un error al establecer la conexión
     */
    public static Connection crearConexion(TipoBaseDatos tipo) throws SQLException {
        try {
            String url = Propiedades.getValor(tipo.getPrefijo() + ".url");
            String user = Propiedades.getValor(tipo.getPrefijo() + ".user");
            String password = Propiedades.getValor(tipo.getPrefijo() + ".password");

            Connection conexion = DriverManager.getConnection(url, user, password);
            logger.info("Conexión {} establecida en {}", tipo.name(), url);
            return conexion;

        } catch (SQLException e) {
            logger.error("Error al conectar a {}: {}", tipo.name(), e.getMessage());
            throw e;
        }
    }

    /**
     * Cierra una conexión de forma segura.
     *
     * @param conexion la conexión a cerrar
     */
    public static void cerrarConexion(Connection conexion) {
        if (conexion != null) {
            try {
                conexion.close();
                logger.info("Conexión cerrada correctamente");
            } catch (SQLException e) {
                logger.error("Error al cerrar la conexión: {}", e.getMessage());
            }
        }
    }

    /**
     * Verifica si una conexión está activa y abierta.
     *
     * @param conexion la conexión a verificar
     * @return true si la conexión está abierta, false en caso contrario
     */
    public static boolean estaConexionActiva(Connection conexion) {
        try {
            return conexion != null && !conexion.isClosed();
        } catch (SQLException e) {
            logger.error("Error al verificar la conexión: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Método de prueba para verificar la funcionalidad de la factory.
     *
     * @param args argumentos de línea de comandos (no utilizados)
     */
     public static void main(String[] args) {
        Connection conexion = null;

        try {
            conexion = ConexionFactory.crearConexion(TipoBaseDatos.APACHE_DERBY);
            /*conexion = ConexionFactory.crearConexion(TipoBaseDatos.ORACLE);
            conexion = ConexionFactory.crearConexion(TipoBaseDatos.H2);
            conexion = ConexionFactory.crearConexion(TipoBaseDatos.HSQLDB);
            conexion = ConexionFactory.crearConexion(TipoBaseDatos.MARIADB);
            conexion = ConexionFactory.crearConexion(TipoBaseDatos.SQLITE);*/

            if (ConexionFactory.estaConexionActiva(conexion)) {
                System.out.println("Conexión establecida correctamente.");
            } else {
                System.err.println("No se pudo establecer la conexión.");
            }

        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            ConexionFactory.cerrarConexion(conexion);
        }
    }

}
