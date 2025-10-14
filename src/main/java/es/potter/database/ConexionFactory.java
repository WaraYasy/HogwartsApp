package es.potter.database;

import es.potter.util.Propiedades;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Factory para gestionar conexiones a diferentes bases de datos.
 * Proporciona un punto centralizado para la creación de conexiones.
 * Las excepciones deben ser capturadas en la capa superior (DAO/Repository).
 *
 * @author Wara Pacheco
 * @version 2.5
 * @since 2025-10-12
 */
public class ConexionFactory implements AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(ConexionFactory.class);

    private Connection conexion;

    /**
     * Constructor que establece la conexión a la base de datos.
     * Lanza SQLException si la conexión falla.
     *
     * @param tipo el tipo de base de datos
     * @throws SQLException si ocurre un error al establecer la conexión
     */
    public ConexionFactory(TipoBaseDatos tipo) throws SQLException {
        String url = Propiedades.getValor(tipo.getPrefijo() + ".url");
        String user = Propiedades.getValor(tipo.getPrefijo() + ".user");
        String password = Propiedades.getValor(tipo.getPrefijo() + ".password");

        conexion = DriverManager.getConnection(url, user, password);
        //Comentar en caso de querer manejar transacciones
        conexion.setAutoCommit(true);

        logger.debug("Conexión {} establecida", tipo.name());
    }

    /**
     * Obtiene la conexión activa a la base de datos.
     *
     * @return la conexión
     */
    public Connection getConnection() {
        return conexion;
    }

    /**
     * Cierra la conexión de forma segura.
     */
    @Override
    public void close() {
        if (conexion != null) {
            try {
                conexion.close();
                logger.debug("Conexión cerrada");
            } catch (SQLException e) {
                logger.error("Error al cerrar la conexión: {}", e.getMessage());
            }
        }
    }
}