package es.potter.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {

    public static void main(String[] args) {
        try {
            // Crear la conexión a H2
            ConexionH2 conexionH2 = new ConexionH2();
            Connection conn = conexionH2.getConnection();

            // Crear statement para ejecutar sentencias SQL
            Statement stmt = conn.createStatement();

            // Crear tabla si no existe
            stmt.execute("CREATE TABLE IF NOT EXISTS PERSONA (ID INT PRIMARY KEY, NOMBRE VARCHAR(255))");

            // Insertar o actualizar registro (MERGE evita error si ya existe)
            stmt.execute("MERGE INTO PERSONA KEY(ID) VALUES (1, 'Harry Potter')");

            // Consultar y mostrar datos
            ResultSet rs = stmt.executeQuery("SELECT * FROM PERSONA");
            while (rs.next()) {
                int id = rs.getInt("ID");
                String nombre = rs.getString("NOMBRE");
                System.out.println("ID: " + id + ", Nombre: " + nombre);
            }

            // Cerrar recursos
            rs.close();
            stmt.close();

            // Cerrar conexión
            conexionH2.closeConnection();

            System.out.println("¡Conexión y prueba realizadas con éxito!");

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error durante la conexión o ejecución SQL: " + e.getMessage());
        }
    }
}
