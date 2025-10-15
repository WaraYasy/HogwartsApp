package es.potter.dao;

import es.potter.database.ConexionFactory;
import es.potter.database.TipoBaseDatos;
import es.potter.model.Alumno;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class DaoAlumno {

    private static final Logger logger = LoggerFactory.getLogger(DaoAlumno.class);

    public static CompletableFuture<ObservableList<Alumno>> cargarAlumnos(TipoBaseDatos tipoBaseDatos) throws SQLException {
        return CompletableFuture.supplyAsync(() -> {
            ConexionFactory conexion = null;
            ObservableList<Alumno> listaAlumnos = FXCollections.observableArrayList();
            try {
                conexion = new ConexionFactory(tipoBaseDatos);
                String consulta = "SELECT id, nombre, apellidos, curso, casa, patronus FROM alumnos";
                PreparedStatement pstmt = conexion.getConnection().prepareStatement(consulta);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    Alumno nuevo = new Alumno();
                    nuevo.setId(rs.getString("id"));
                    nuevo.setNombre(rs.getString("nombre"));
                    nuevo.setApellidos(rs.getString("apellidos"));
                    nuevo.setCurso(rs.getInt("curso"));
                    nuevo.setCasa(rs.getString("casa"));
                    nuevo.setPatronus(rs.getString("patronus"));
                    listaAlumnos.add(nuevo);
                }
                rs.close();
                pstmt.close();
                conexion.close();
            } catch (SQLException e) {
                logger.error("No he podido cargar la tabla {}", e.getMessage());
            }
            return listaAlumnos;
        });
    }

    public static CompletableFuture<Boolean> nuevoAlumno(Alumno alumno) throws SQLException {
        return CompletableFuture.supplyAsync(() -> {
            TipoBaseDatos tipoConexion = TipoBaseDatos.obtenerTipoBaseDatosPorCasa(alumno.getCasa());
            ConexionFactory conexion = null;
            PreparedStatement pstmt;

            try {
                conexion = new ConexionFactory(tipoConexion);
                String consulta = "INSERT INTO alumnos (id, nombre, apellidos, curso, casa, patronus) VALUES (?,?,?,?,?,?)";
                pstmt = conexion.getConnection().prepareStatement(consulta);
                alumno.setId(generarId(alumno));
                pstmt.setString(1, alumno.getId());
                pstmt.setString(2, alumno.getNombre());
                pstmt.setString(3, alumno.getApellidos());
                pstmt.setInt(4, alumno.getCurso());
                pstmt.setString(5, alumno.getCasa());
                pstmt.setString(6, alumno.getPatronus());

                int rows = pstmt.executeUpdate();
                pstmt.close();
                conexion.close();
                return rows > 0;
            } catch (SQLException e) {
                logger.error("No he podido crearlo {}",e.getMessage());
                return false;
            }
        });
    }

    public static CompletableFuture<Boolean> eliminarAlumno(Alumno alumnoEliminar) {
        return CompletableFuture.supplyAsync(() -> {
            TipoBaseDatos tipoConexion = TipoBaseDatos.obtenerTipoBaseDatosPorCasa(alumnoEliminar.getCasa());
            ConexionFactory conexion = null;
            PreparedStatement pstmt;

            try {
                conexion = new ConexionFactory(tipoConexion);
                String consulta = "DELETE FROM alumnos WHERE id like = ?";
                pstmt = conexion.getConnection().prepareStatement(consulta);
                pstmt.setString(1, alumnoEliminar.getId());
                int rows = pstmt.executeUpdate();
                pstmt.close();
                conexion.close();
                return rows > 0;
            } catch (SQLException e) {
                logger.error("No he podido eliminar a alumno {}",e.getMessage());
                return false;
            }
        });
    }
    /*TODO: modificar nombre, apellidos, curso, patronus, casa¿?*/

    private static String generarId(Alumno alumno) {
        String casa = alumno.getCasa();
        String prefijo = casa.substring(0, 3).toUpperCase();
        String ultimoId = getUltimoId(casa);
        int nuevoNumero = 1;
        if (ultimoId != null && ultimoId.startsWith(prefijo)) {
            String numStr = ultimoId.substring(3);
            try {
                nuevoNumero = Integer.parseInt(numStr) + 1;
            } catch (NumberFormatException e) {
                nuevoNumero = 1;
            }
        }
        String sufijo = String.format("%05d", nuevoNumero);
        return prefijo + sufijo;
    }

    private static String getUltimoId(String casa) {
        String prefijo = casa.substring(0, 3).toUpperCase();
        String ultimoId = null;
        TipoBaseDatos tipoConexion = TipoBaseDatos.obtenerTipoBaseDatosPorCasa(casa);
        ConexionFactory conexion = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conexion = new ConexionFactory(tipoConexion);
            String consulta = "SELECT id FROM alumnos WHERE id LIKE ? ORDER BY id DESC LIMIT 1";
            pstmt = conexion.getConnection().prepareStatement(consulta);
            pstmt.setString(1, prefijo + "%");
            rs = pstmt.executeQuery();
            if (rs.next()) {
                ultimoId = rs.getString("id");
            }
        } catch (SQLException e) {
            logger.error("Error obteniendo el último ID de la casa {}: {}", casa, e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conexion != null) conexion.close();
            } catch (SQLException e) {
                logger.error("Error cerrando recursos: {}", e.getMessage());
            }
        }
        return ultimoId;
    }

}
