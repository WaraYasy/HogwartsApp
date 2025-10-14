package es.potter.dao;

import es.potter.database.ConexionFactory;
import es.potter.database.TipoBaseDatos;

import java.sql.Connection;
import java.sql.SQLException;

public class DaoEjemplo {

    public boolean probarConexion(TipoBaseDatos tipo) {
        Connection conn = null;
        
        try {
            // Crear conexión usando la Factory
            conn = ConexionFactory.crearConexion(tipo);
            
            // Simplemente verificar si la conexión es válida (funciona en TODAS las BD)
            if (conn != null && !conn.isClosed()) {
                String bd = conn.getMetaData().getDatabaseProductName();
                System.out.println("✅ " + tipo.name() + ": Conexión exitosa - BD: " + bd);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ " + tipo.name() + ": Error - " + e.getMessage());
            return false;
            
        } finally {
            ConexionFactory.cerrarConexion(conn);
        }
        
        return false;
    }
    
    /**
     * Método main para probar todas las bases de datos.
     */
    public static void main(String[] args) {
        DaoEjemplo dao = new DaoEjemplo();
        
        System.out.println("=== Prueba de Conexiones ===\n");
        
        // Probar todas las bases de datos
        for (TipoBaseDatos tipo : TipoBaseDatos.values()) {
            dao.probarConexion(tipo);
        }
    }
}
