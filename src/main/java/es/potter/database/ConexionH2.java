package es.potter.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionH2 {

    private Connection conexionH2 = null;

    private static final Logger logger = LoggerFactory.getLogger(ConexionH2.class);

    public ConexionH2() throws SQLException {
        try {
            String path = Propiedades.getValor("h2_path"); // Ej: "mem:testdb" o "~/test"
            String user = Propiedades.getValor("user");
            String pass = Propiedades.getValor("pass");

            // Construir URL de conexión JDBC para H2
            String url = "jdbc:h2:" + path;

            conexionH2 = DriverManager.getConnection(url, user, pass);
            logger.info("Conexión H2 establecida en {}", path);

        } catch (SQLException e) {
            logger.error("Error al conectar con H2: {}", e.getMessage());
            throw e;
        }
    }

    public Connection getConnection() {
        return conexionH2;
    }

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
}

