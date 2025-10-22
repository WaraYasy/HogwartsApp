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
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * DaoAlumno gestiona el acceso a datos de alumnos en la base de datos.
 * Todos los métodos son asíncronos usando CompletableFuture.
 *
 * @author Wara Pacheco
 * @version 3.0
 */
public class DaoAlumno {

    private static final Logger logger = LoggerFactory.getLogger(DaoAlumno.class);

    /*-------------------------------------------*/
    /*           MÉTODOS PÚBLICOS CRUD           */
    /*-------------------------------------------*/

    /**
     * Carga todos los alumnos de una base de datos.
     */
    public static CompletableFuture<ObservableList<Alumno>> cargarAlumnos(TipoBaseDatos tipo) {
        String sql = "SELECT id, nombre, apellidos, curso, casa, patronus FROM alumnos";

        return ConexionFactory.getConnectionAsync(tipo).thenApply(conn -> {
            ObservableList<Alumno> lista = FXCollections.observableArrayList();

            try (conn; PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearAlumno(rs));
                }
                logger.info("Cargados {} alumnos desde {}", lista.size(), tipo);
            } catch (SQLException e) {
                logger.error("Error cargando alumnos: {}", e.getMessage());
                throw new RuntimeException(e);
            }
            return lista;
        });
    }

    /**
     * Crea un nuevo alumno con transacción.
     * Genera el ID automáticamente con UUID si no lo tiene.
     */
    public static CompletableFuture<Boolean> nuevoAlumno(Alumno alumno, TipoBaseDatos tipo) {
        String sql = "INSERT INTO alumnos (id, nombre, apellidos, curso, casa, patronus) VALUES (?,?,?,?,?,?)";

        return ConexionFactory.getConnectionAsync(tipo).thenApply(conn -> {
            try {
                conn.setAutoCommit(false); // Iniciar transacción

                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    // Generar ID único con UUID (solo si no tiene)
                    if (alumno.getId() == null || alumno.getId().isEmpty()) {
                        String id = generarId(alumno);
                        alumno.setId(id);
                    }

                    stmt.setString(1, alumno.getId());
                    stmt.setString(2, alumno.getNombre());
                    stmt.setString(3, alumno.getApellidos());
                    stmt.setInt(4, alumno.getCurso());
                    stmt.setString(5, alumno.getCasa());
                    stmt.setString(6, alumno.getPatronus());

                    stmt.executeUpdate();
                    conn.commit(); // Commit transacción

                    logger.info("Alumno {} creado en {}", alumno.getId(), tipo);
                    return true;
                }
            } catch (SQLException e) {
                try {
                    conn.rollback(); // Rollback si hay error
                    logger.error("Rollback en {}: {}", tipo, e.getMessage());
                } catch (SQLException ex) {
                    logger.error("Error en rollback: {}", ex.getMessage());
                }
                return false;
            } finally {
                try {
                    conn.close();
                } catch (SQLException e) {
                    logger.error("Error cerrando conexión: {}", e.getMessage());
                }
            }
        });
    }

    /**
     * Elimina un alumno por su ID con transacción.
     */
    public static CompletableFuture<Boolean> eliminarAlumno(Alumno alumno, TipoBaseDatos tipo) {
        String sql = "DELETE FROM alumnos WHERE id = ?";

        return ConexionFactory.getConnectionAsync(tipo).thenApply(conn -> {
            try {
                conn.setAutoCommit(false); // Iniciar transacción

                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, alumno.getId());

                    stmt.executeUpdate();
                    conn.commit(); // Commit transacción

                    logger.info("Alumno {} eliminado de {}", alumno.getId(), tipo);
                    return true;
                }
            } catch (SQLException e) {
                try {
                    conn.rollback();
                    logger.error("Rollback en {}: {}", tipo, e.getMessage());
                } catch (SQLException ex) {
                    logger.error("Error en rollback: {}", ex.getMessage());
                }
                return false;
            } finally {
                try {
                    conn.close();
                } catch (SQLException e) {
                    logger.error("Error cerrando conexión: {}", e.getMessage());
                }
            }
        });
    }

    /**
     * Modifica un alumno existente con transacción.
     */
    public static CompletableFuture<Boolean> modificarAlumno(String id, Alumno alumno, TipoBaseDatos tipo) {
        String sql = "UPDATE alumnos SET nombre = ?, apellidos = ?, curso = ?, casa = ?, patronus = ? WHERE id = ?";

        return ConexionFactory.getConnectionAsync(tipo).thenApply(conn -> {
            try {
                conn.setAutoCommit(false); // Iniciar transacción

                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, alumno.getNombre());
                    stmt.setString(2, alumno.getApellidos());
                    stmt.setInt(3, alumno.getCurso());
                    stmt.setString(4, alumno.getCasa());
                    stmt.setString(5, alumno.getPatronus());
                    stmt.setString(6, id);

                    stmt.executeUpdate();
                    conn.commit(); // Commit transacción

                    logger.info("Alumno {} modificado en {}", id, tipo);
                    return true;
                }
            } catch (SQLException e) {
                try {
                    conn.rollback();
                    logger.error("Rollback en {}: {}", tipo, e.getMessage());
                } catch (SQLException ex) {
                    logger.error("Error en rollback: {}", ex.getMessage());
                }
                return false;
            } finally {
                try {
                    conn.close();
                } catch (SQLException e) {
                    logger.error("Error cerrando conexión: {}", e.getMessage());
                }
            }
        });
    }

    /*-------------------------------------------*/
    /*            MÉTODOS PRIVADOS               */
    /*-------------------------------------------*/

    /**
     * Convierte una fila de ResultSet a un objeto Alumno.
     */
    private static Alumno mapearAlumno(ResultSet rs) throws SQLException {
        Alumno alumno = new Alumno();
        alumno.setId(rs.getString("id"));
        alumno.setNombre(rs.getString("nombre"));
        alumno.setApellidos(rs.getString("apellidos"));
        alumno.setCurso(rs.getInt("curso"));
        alumno.setCasa(rs.getString("casa"));
        alumno.setPatronus(rs.getString("patronus"));
        return alumno;
    }

    /**
     * Genera un ID único con UUID y prefijo de casa.
     * Formato: GRY-a4f3b2c1 (3 letras mayúsculas + guión + 8 hex minúsculas)
     */
    private static String generarId(Alumno alumno) {
        String prefijo = alumno.getCasa().substring(0, 3).toUpperCase();
        String uuid = UUID.randomUUID().toString().substring(0, 8).toLowerCase();
        return prefijo + "-" + uuid;
    }
}