package es.potter.database;

import es.potter.util.Propiedades;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionMariaDB {

    /** Conexión activa a la base de datos MariaDB. */
    private Connection conexionMDB = null;

    /**
     * Logger para registrar eventos, errores y mensajes de depuración durante el ciclo de vida de la aplicación.
     */
    private static final Logger logger = LoggerFactory.getLogger(ConexionMariaDB.class);

    /**
     * Constructor que establece automáticamente la conexión a la base de datos.
     * <p>
     * Carga la configuración desde el archivo {@code configuration.properties},
     * construye la URL de conexión JDBC y establece la conexión con MariaDB.
     * </p>
     *
     * @throws SQLException si ocurre un error durante el establecimiento de la conexión
     */
    public ConexionMariaDB() throws SQLException{
        try {
            String url = Propiedades.getValor("db.mariadb.url");
            String user = Propiedades.getValor("db.mariadb.user");
            String password = Propiedades.getValor("db.mariadb.password");

            conexionMDB = DriverManager.getConnection(url, user, password);
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
     * Esta conexión puede ser utilizada para ejecutar consultas y transacciones.
     * </p>
     *
     * @return la conexión activa a la base de datos, o {@code null} si la conexión falló
     * @see java.sql.Connection
     */
    public Connection getConnection() {
        return conexionMDB;
    }

    /**
     * Cierra la conexión a la base de datos de forma segura.
     * <p>
     * Verifica que la conexión esté activa antes de cerrarla y registra el evento.
     * Es importante llamar a este método cuando se termine de usar la conexión
     * para liberar recursos del sistema.
     * </p>
     *
     * @see java.sql.Connection#close()
     */
    public void closeConnection() {
        if (conexionMDB != null) {
            try {
                conexionMDB.close();
            } catch (SQLException e) {
                logger.error("Error al cerrar la conexión MariaDB: {}", e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        ConexionMariaDB conexion = null;

        try {
            // Crear la conexión
            conexion = new ConexionMariaDB();

            // Verificar si la conexión fue exitosa
            if (conexion.getConnection() != null && !conexion.getConnection().isClosed()) {
                System.out.println("Conexión establecida correctamente con la base de datos MariaDB.");
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