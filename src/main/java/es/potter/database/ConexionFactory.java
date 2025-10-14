package es.potter.database;

import es.potter.util.Propiedades;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * <b>Factory para gestionar conexiones JDBC a diferentes bases de datos.</b>
 * <p>
 * Proporciona un punto centralizado para la creación y cierre de conexiones JDBC.
 * Utiliza la clase {@link Propiedades} para obtener los parámetros de conexión
 * desde el archivo de configuración.
 * <br>
 * Las excepciones deben ser capturadas en la capa superior (DAO/Repository).
 * </p>
 *
 * @author Wara Pacheco
 * @version 2.6
 * @since 2025-10-12
 */
public class ConexionFactory implements AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(ConexionFactory.class);

    /**
     * Instancia de la conexión JDBC activa.
     */
    private Connection conexion;

    /**
     * Constructor que establece la conexión a la base de datos.
     * <p>
     * Obtiene los parámetros de conexión (URL, usuario, contraseña) desde el archivo de propiedades
     * usando el prefijo correspondiente al tipo de base de datos.
     * </p>
     *
     * @param tipo el tipo de base de datos (enum {@link TipoBaseDatos})
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
     * <p>
     * Retorna la instancia de {@link Connection} establecida en el constructor.
     * Esta conexión puede ser utilizada para ejecutar consultas y transacciones.
     * </p>
     *
     * @return la conexión activa a la base de datos, o {@code null} si la conexión falló
     * @see java.sql.Connection
     */
    public Connection getConnection() {
        return conexion;
    }

    /**
     * Cierra la conexión de forma segura.
     * <p>
     * Verifica que la conexión esté activa antes de cerrarla y registra el evento.
     * Es importante llamar a este método cuando se termine de usar la conexión
     * para liberar recursos del sistema.
     * </p>
     *
     * @see java.sql.Connection#close()
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