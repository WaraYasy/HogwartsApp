package es.potter.database;

import es.potter.util.Propiedades;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

/**
 * Factory asíncrona para gestionar conexiones a diferentes bases de datos.
 * Proporciona métodos para conectar y cerrar conexiones de forma no bloqueante.
 *
 * @author Wara Pacheco
 * @version 3.0
 * @since 2025-10-16
 */
public class ConexionFactory {

    /** Logger para registrar eventos y errores de la conexión */
    private static final Logger logger = LoggerFactory.getLogger(ConexionFactory.class);

    /**
     * Obtiene una conexión asíncrona a la base de datos.
     * <p>
     * Para SQLite, utiliza {@link SQLiteManager} para obtener la ruta embebida
     * correctamente, garantizando que funcione tanto en desarrollo como empaquetado.
     * </p>
     *
     * @param tipo el tipo de base de datos
     * @return CompletableFuture con la conexión
     *
     * @author Wara
     */
    public static CompletableFuture<Connection> getConnectionAsync(TipoBaseDatos tipo) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String url;
                String user;
                String password;

                // Caso especial: SQLite embebida
                if (tipo == TipoBaseDatos.SQLITE) {
                    url = SQLiteManager.getJdbcUrl();
                    user = "";
                    password = "";
                    logger.debug("Conectando a SQLite embebida: {}", url);
                } else {
                    // Bases de datos remotas: leer desde properties
                    url = Propiedades.getValor(tipo.getPrefijo() + ".url");
                    user = Propiedades.getValor(tipo.getPrefijo() + ".user");
                    password = Propiedades.getValor(tipo.getPrefijo() + ".password");
                    logger.debug("Conectando a: {} ({})", tipo, url);
                }

                Connection conn = DriverManager.getConnection(url, user, password);
                logger.debug("Conexión establecida exitosamente: {}", tipo);
                return conn;
            } catch (Exception e) {
                logger.error("Error al conectar con la base de datos {}", tipo, e);
                throw new RuntimeException("Error al conectar con " + tipo, e);
            }
        });
    }

    /**
     * Cierra una conexión de forma asíncrona.
     *
     * @param connection la conexión a cerrar
     * @return CompletableFuture que se completa cuando se cierra la conexión
     *
     * @author Wara
     */
    @Deprecated
    public static CompletableFuture<Void> closeConnectionAsync(Connection connection) {
        return CompletableFuture.runAsync(() -> {
            if (connection != null) {
                try {
                    if (!connection.isClosed()) {
                        connection.close();
                        logger.debug("Conexión cerrada correctamente");
                    }
                } catch (SQLException e) {
                    logger.error("Error al cerrar la conexión", e);
                    throw new RuntimeException("Error al cerrar conexión", e);
                }
            }
        });
    }

}