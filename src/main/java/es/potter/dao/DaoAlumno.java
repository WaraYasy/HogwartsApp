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
 * Implementa operaciones CRUD asíncronas para manipular alumnos en distintas bases de datos.
 * Utiliza transacciones para garantizar la integridad de los datos.
 * Emplea UUID para generar identificadores únicos de alumnos.
 * Los métodos retornan CompletableFuture para operaciones no bloqueantes.
 * Se recomienda manejar las excepciones verificadas con logging adecuado.
 *
 * @author Wara Pacheco
 * @version 3.0
 */
public class DaoAlumno {

    /** Logger para registrar eventos y errores de la conexión */
    private static final Logger logger = LoggerFactory.getLogger(DaoAlumno.class);

    /*-------------------------------------------*/
    /*           MÉTODOS PÚBLICOS CRUD           */
    /*-------------------------------------------*/

    /**
     * Carga todos los alumnos disponibles en una base de datos específica.
     *
     * @param tipo Tipo de base de datos desde donde cargar los datos
     * @return CompletableFuture con una lista observable de alumnos cargados
     *
     * @author Wara
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
     * Inserta un nuevo alumno en la base de datos con control transaccional.
     * Si el alumno no tiene ID, se genera uno único basado en UUID y prefijo de casa.
     *
     * @param alumno Alumno a insertar
     * @param tipo Tipo de base de datos destino
     * @return CompletableFuture con true si la operación fue exitosa, false en caso contrario
     *
     * @author Wara
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

                    logger.info("Alumno con ID {} creado correctamente en {}", alumno.getId(), tipo);
                    return true;
                }
            } catch (SQLException e) {
                try {
                    conn.rollback(); // Rollback sí hay error
                    logger.error("Rollback en {} al crear alumno con ID {}: {}", tipo, alumno.getId(), e.getMessage());
                } catch (SQLException ex) {
                    logger.error("Error en rollback al crear alumno: {}", ex.getMessage());
                }
                return false;
            } finally {
                try {
                    conn.close();
                } catch (SQLException e) {
                    logger.error("Error cerrando conexión tras creación de alumno: {}", e.getMessage());
                }
            }
        });
    }

    /**
     * Elimina un alumno específico identificado por su ID en la base de datos.
     * Ejecuta la operación dentro de una transacción para seguridad y consistencia.
     *
     * @param alumno Alumno a eliminar
     * @param tipo Tipo de base de datos donde se eliminará
     * @return CompletableFuture con true si la eliminación fue exitosa, false en caso contrario
     *
     * @author Wara
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

                    logger.info("Alumno con ID {} eliminado correctamente de {}", alumno.getId(), tipo);
                    return true;
                }
            } catch (SQLException e) {
                try {
                    conn.rollback();
                    logger.error("Rollback en {} al eliminar alumno con ID {}: {}", tipo, alumno.getId(), e.getMessage());
                } catch (SQLException ex) {
                    logger.error("Error en rollback al eliminar alumno: {}", ex.getMessage());
                }
                return false;
            } finally {
                try {
                    conn.close();
                } catch (SQLException e) {
                    logger.error("Error cerrando conexión tras eliminación de alumno: {}", e.getMessage());
                }
            }
        });
    }

    /**
     * Actualiza los datos de un alumno existente identificado por su ID.
     * Realiza la actualización en una transacción para mantener la integridad.
     *
     * @param id ID del alumno a modificar
     * @param alumno Objeto Alumno con los datos modificados
     * @param tipo Tipo de base de datos donde se modificará el registro
     * @return CompletableFuture con true si la actualización fue exitosa, false en caso contrario
     *
     * @author Wara
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

                    logger.info("Alumno con ID {} modificado correctamente en {}", id, tipo);
                    return true;
                }
            } catch (SQLException e) {
                try {
                    conn.rollback();
                    logger.error("Rollback en {} al modificar alumno con ID {}: {}", tipo, id, e.getMessage());
                } catch (SQLException ex) {
                    logger.error("Error en rollback: {}", ex.getMessage());
                }
                return false;
            } finally {
                try {
                    conn.close();
                } catch (SQLException e) {
                    logger.error("Error cerrando conexión tras modificación de alumno: {}", e.getMessage());
                }
            }
        });
    }

    /*-------------------------------------------*/
    /*            MÉTODOS PRIVADOS               */
    /*-------------------------------------------*/

    /**
     * Crea un objeto Alumno a partir de la fila actual del ResultSet.
     *
     * @param rs ResultSet con datos de la fila del alumno
     * @return Objeto Alumno mapeado
     * @throws SQLException Sí ocurre error leyendo datos
     *
     * @author Wara
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
     * Genera un identificador único para un alumno.
     * El formato es el prefijo de la casa en mayúsculas,
     * un guion y las primeras 8 letras de un UUID en minúsculas.
     * Ejemplo: GRY-a4f3b2c1
     *
     * @param alumno Alumno para obtener la casa y generar el ID
     * @return Identificador único generado
     *
     * @author Wara
     */
    private static String generarId(Alumno alumno) {
        String prefijo = alumno.getCasa().substring(0, 3).toUpperCase();
        String uuid = UUID.randomUUID().toString().substring(0, 8).toLowerCase();
        return prefijo + "-" + uuid;
    }
}