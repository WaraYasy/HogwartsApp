package es.potter.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDerby {

    // Constructor privado para evitar instanciación
    private ConexionDerby() { }

    /**
     * Retorna una conexión a la base de datos Derby usando propiedades externas.
     *
     * @return Connection activa
     * @throws SQLException si hay error en la conexión
     * @throws ClassNotFoundException si no se encuentra el driver de Derby
     */
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        // Leer propiedades

        String host = Propiedades.getValor("host");       // ej. "localhost"
        String port = Propiedades.getValor("port");       // ej. "1527"
        String database = Propiedades.getValor("database"); // ej. "miDB"
        String user = Propiedades.getValor("user");       // ej. "app"
        String pass = Propiedades.getValor("pass");       // ej. "1234"

        // Construir URL para network server
        String url = "jdbc:derby://" + host + ":" + port + "/" + database + ";create=true";

        // Cargar driver client
        Class.forName("org.apache.derby.jdbc.ClientDriver");

        // Retornar la conexión
        return DriverManager.getConnection(url, user, pass);
    }
}
