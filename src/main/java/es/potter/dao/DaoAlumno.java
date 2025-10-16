package es.potter.dao;

import es.potter.database.ConexionFactory;
import es.potter.database.TipoBaseDatos;
import es.potter.model.Alumno;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class DaoAlumno {

    private static final Logger logger = LoggerFactory.getLogger(DaoAlumno.class);

    public static CompletableFuture<ObservableList<Alumno>> cargarAlumnos(TipoBaseDatos tipoBaseDatos) {
        String consulta = "SELECT id, nombre, apellidos, curso, casa, patronus FROM alumnos";
        return ConexionFactory.getConnectionAsync(tipoBaseDatos).thenApply(conexion -> {
            ObservableList<Alumno> listaAlumnos = FXCollections.observableArrayList();
            try (conexion;PreparedStatement stmt = conexion.prepareStatement(consulta);
                 ResultSet rs = stmt.executeQuery()) {

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
            } catch (SQLException e) {
               throw new RuntimeException (e);
            }
            return listaAlumnos;
        });
    }

    public static CompletableFuture<Boolean> nuevoAlumno(Alumno alumno) throws SQLException {
        TipoBaseDatos tipoConexion = TipoBaseDatos.obtenerTipoBaseDatosPorCasa(alumno.getCasa());
        String consulta = "INSERT INTO alumnos (id, nombre, apellidos, curso, casa, patronus) VALUES (?,?,?,?,?,?)";

        return ConexionFactory.getConnectionAsync(tipoConexion).thenApply(conexion -> {
            try (conexion;PreparedStatement pstmt = conexion.prepareStatement(consulta)){
                try{
                    alumno.setId(generarId(alumno));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException(e);
                }

                pstmt.setString(1, alumno.getId());
                pstmt.setString(2, alumno.getNombre());
                pstmt.setString(3, alumno.getApellidos());
                pstmt.setInt(4, alumno.getCurso());
                pstmt.setString(5, alumno.getCasa());
                pstmt.setString(6, alumno.getPatronus());

                int rows = pstmt.executeUpdate();
                return rows > 0;
            } catch (SQLException e) {
                logger.error("No he podido crearlo {}",e.getMessage());
                return false;
            }
        });
    }

    public static CompletableFuture<Boolean> eliminarAlumno(Alumno alumnoEliminar) {
        TipoBaseDatos tipoConexion = TipoBaseDatos.obtenerTipoBaseDatosPorCasa(alumnoEliminar.getCasa());
        String consulta = "DELETE FROM alumnos WHERE id = ?";

        return ConexionFactory.getConnectionAsync(tipoConexion).thenApply(conexion -> {
            try (conexion;PreparedStatement pstmt = conexion.prepareStatement(consulta)){
                pstmt.setString(1, alumnoEliminar.getId());
                int rows = pstmt.executeUpdate();
                return rows > 0;
            } catch (SQLException e) {
                logger.error("No he podido eliminar a alumno {}",e.getMessage());
                return false;
            }
        });
    }

    public static CompletableFuture<Boolean> modificarAlumno(String id, Alumno nuevo) {
        TipoBaseDatos tipoConexion = TipoBaseDatos.obtenerTipoBaseDatosPorCasa(nuevo.getCasa());
        String consulta = "UPDATE alumnos SET nombre = ?, apellidos = ?, curso = ?, casa = ?, patronus = ? WHERE id = ?";

        return ConexionFactory.getConnectionAsync(tipoConexion).thenApply(conexion -> {
            try (conexion;PreparedStatement pstmt = conexion.prepareStatement(consulta)){
                pstmt.setString(1, nuevo.getNombre());
                pstmt.setString(2, nuevo.getApellidos());
                pstmt.setInt(3, nuevo.getCurso());
                pstmt.setString(4, nuevo.getCasa());
                pstmt.setString(5, nuevo.getPatronus());
                pstmt.setString(6, id);
                int rows = pstmt.executeUpdate();
                return rows > 0;
            } catch (SQLException e) {
                logger.error("No he podido modificar alumno {}", e.getMessage());
                return false;
            }
        });
    }




    private static String generarId(Alumno alumno) {
        String casa = alumno.getCasa();
        String prefijo = casa.substring(0, 3).toUpperCase();
        String ultimoId;
        try {
            ultimoId = getUltimoId(casa);
        } catch (RuntimeException e) {
            logger.error("Error generando el ID para la casa {}: {}", casa, e.getMessage());
            throw new IllegalArgumentException("No se pudo generar el ID para la casa: " + casa, e);
        }
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
        String consulta = "SELECT id FROM alumnos WHERE id LIKE ? ORDER BY id DESC LIMIT 1";

        try (Connection conexion = ConexionFactory.getConnectionAsync(tipoConexion).join();
             PreparedStatement pstmt = conexion.prepareStatement(consulta)) {
            pstmt.setString(1, prefijo + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    ultimoId = rs.getString("id");
                }
            }
        } catch (java.util.concurrent.CompletionException ce) {
            logger.error("Error de concurrencia obteniendo el último ID de la casa {}: {}", casa, ce.getMessage());
            throw new RuntimeException("Error de concurrencia al obtener el último ID de la casa: " + casa, ce);
        } catch (SQLException se) {
            logger.error("Error SQL obteniendo el último ID de la casa {}: {}", casa, se.getMessage());
            throw new RuntimeException("Error SQL al obtener el último ID de la casa: " + casa, se);
        } catch (Exception e) {
            logger.error("Error inesperado obteniendo el último ID de la casa {}: {}", casa, e.getMessage());
            throw new RuntimeException("Error inesperado al obtener el último ID de la casa: " + casa, e);
        }
        return ultimoId;
    }
}
