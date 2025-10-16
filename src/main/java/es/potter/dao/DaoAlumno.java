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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * DaoAlumno gestiona el acceso a datos de alumnos en la base de datos.
 * Todos los métodos son asíncronos usando CompletableFuture.
 *
 * @author WaraYasy
 * @version 3.0
 */
public class DaoAlumno {

    private static final Logger logger = LoggerFactory.getLogger(DaoAlumno.class);

    /**
     * Contadores atómicos por casa para generar IDs únicos.
     * Evita race conditions al generar IDs concurrentemente.
     */
    private static final ConcurrentHashMap<String, AtomicInteger> contadoresPorCasa = new ConcurrentHashMap<>();

    /*-------------------------------------------*/
    /*           MÉTODOS PÚBLICOS CRUD           */
    /*-------------------------------------------*/

    /**
     * Carga todos los alumnos de una base de datos.
     */
    public static CompletableFuture<ObservableList<Alumno>> cargarAlumnos(TipoBaseDatos tipoBaseDatos) {
        String sql = "SELECT id, nombre, apellidos, curso, casa, patronus FROM alumnos";

        return ConexionFactory.getConnectionAsync(tipoBaseDatos)
                .thenApply(conexion -> {
                    ObservableList<Alumno> lista = FXCollections.observableArrayList();

                    try (conexion;
                         PreparedStatement stmt = conexion.prepareStatement(sql);
                         ResultSet rs = stmt.executeQuery()) {

                        while (rs.next()) {
                            lista.add(mapearAlumno(rs));
                        }

                        logger.info("Cargados {} alumnos desde {}", lista.size(), tipoBaseDatos);
                        return lista;

                    } catch (SQLException e) {
                        logger.error("Error cargando alumnos: {}", e.getMessage());
                        throw new RuntimeException("Error al cargar alumnos", e);
                    }
                });
    }

    /**
     * Inserta un nuevo alumno. El ID se genera automáticamente.
     */
    public static CompletableFuture<Boolean> nuevoAlumno(Alumno alumno, TipoBaseDatos base) {
        String sql = "INSERT INTO alumnos (id, nombre, apellidos, curso, casa, patronus) VALUES (?,?,?,?,?,?)";

        return generarIdAsync(alumno)
                .thenCompose(idGenerado -> {
                    alumno.setId(idGenerado);

                    return ConexionFactory.getConnectionAsync(base)
                            .thenApply(conexion -> {
                                try (conexion; PreparedStatement stmt = conexion.prepareStatement(sql)) {

                                    stmt.setString(1, alumno.getId());
                                    stmt.setString(2, alumno.getNombre());
                                    stmt.setString(3, alumno.getApellidos());
                                    stmt.setInt(4, alumno.getCurso());
                                    stmt.setString(5, alumno.getCasa());
                                    stmt.setString(6, alumno.getPatronus());

                                    int filas = stmt.executeUpdate();

                                    if (filas > 0) {
                                        logger.info("Alumno {} creado en {}", alumno.getId(), base);
                                        return true;
                                    } else {
                                        logger.warn("No se insertó ningún alumno en {}", base);
                                        return false;
                                    }

                                } catch (SQLException e) {
                                    logger.error("Error insertando alumno: {}", e.getMessage());
                                    throw new RuntimeException("Error al crear alumno", e);
                                }
                            });
                });
    }

    /**
     * Elimina un alumno por su ID.
     */
    public static CompletableFuture<Boolean> eliminarAlumno(Alumno alumno, TipoBaseDatos base) {
        String sql = "DELETE FROM alumnos WHERE id = ?";

        return ConexionFactory.getConnectionAsync(base)
                .thenApply(conexion -> {
                    try (conexion; PreparedStatement stmt = conexion.prepareStatement(sql)) {

                        stmt.setString(1, alumno.getId());
                        int filas = stmt.executeUpdate();

                        if (filas > 0) {
                            logger.info("Alumno {} eliminado de {}", alumno.getId(), base);
                            return true;
                        } else {
                            logger.warn("Alumno {} no encontrado en {}", alumno.getId(), base);
                            return false;
                        }

                    } catch (SQLException e) {
                        logger.error("Error eliminando alumno: {}", e.getMessage());
                        throw new RuntimeException("Error al eliminar alumno", e);
                    }
                });
    }

    /**
     * Modifica los datos de un alumno existente.
     */
    public static CompletableFuture<Boolean> modificarAlumno(String id, Alumno nuevo, TipoBaseDatos base) {
        String sql = "UPDATE alumnos SET nombre = ?, apellidos = ?, curso = ?, casa = ?, patronus = ? WHERE id = ?";

        return ConexionFactory.getConnectionAsync(base)
                .thenApply(conexion -> {
                    try (conexion; PreparedStatement stmt = conexion.prepareStatement(sql)) {

                        stmt.setString(1, nuevo.getNombre());
                        stmt.setString(2, nuevo.getApellidos());
                        stmt.setInt(3, nuevo.getCurso());
                        stmt.setString(4, nuevo.getCasa());
                        stmt.setString(5, nuevo.getPatronus());
                        stmt.setString(6, id);

                        int filas = stmt.executeUpdate();

                        if (filas > 0) {
                            logger.info("Alumno {} modificado en {}", id, base);
                            return true;
                        } else {
                            logger.warn("Alumno {} no encontrado en {}", id, base);
                            return false;
                        }

                    } catch (SQLException e) {
                        logger.error("Error modificando alumno: {}", e.getMessage());
                        throw new RuntimeException("Error al modificar alumno", e);
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
     * Genera un ID único para un alumno usando AtomicInteger (GRY00001, SLY00001, etc).
     * Thread-safe sin necesidad de locks - usa contadores atómicos en memoria.
     */
    private static CompletableFuture<String> generarIdAsync(Alumno alumno) {
        String casa = alumno.getCasa();

        // Validar casa
        if (casa == null || casa.length() < 3) {
            return CompletableFuture.failedFuture(
                new IllegalArgumentException("Casa inválida: " + casa)
            );
        }

        String prefijo = casa.substring(0, 3).toUpperCase();

        // Obtener o inicializar contador para esta casa
        AtomicInteger contador = contadoresPorCasa.computeIfAbsent(casa, k -> {
            // Primera vez para esta casa - obtener último ID de BD
            logger.debug("Inicializando contador para casa: {}", casa);
            return new AtomicInteger(obtenerUltimoNumero(casa));
        });

        // Incrementar de forma atómica (thread-safe)
        int nuevoNumero = contador.incrementAndGet();
        String nuevoId = prefijo + String.format("%05d", nuevoNumero);

        logger.debug("ID generado: {} (contador: {})", nuevoId, nuevoNumero);
        return CompletableFuture.completedFuture(nuevoId);
    }

    /**
     * Obtiene el último número de ID usado para una casa desde la BD.
     * Solo se llama una vez por casa al inicializar el contador.
     */
    private static int obtenerUltimoNumero(String casa) {
        String prefijo = casa.substring(0, 3).toUpperCase();
        TipoBaseDatos baseCasa = TipoBaseDatos.obtenerTipoBaseDatosPorCasa(casa);

        // Derby usa FETCH FIRST, otros usan LIMIT
        String sql;
        if (baseCasa == TipoBaseDatos.APACHE_DERBY) {
            sql = "SELECT id FROM alumnos WHERE id LIKE ? ORDER BY id DESC FETCH FIRST 1 ROWS ONLY";
        } else {
            sql = "SELECT id FROM alumnos WHERE id LIKE ? ORDER BY id DESC LIMIT 1";
        }

        try {
            // Llamada bloqueante para inicialización
            return ConexionFactory.getConnectionAsync(baseCasa)
                    .thenApply(conexion -> {
                        try (conexion; PreparedStatement stmt = conexion.prepareStatement(sql)) {
                            stmt.setString(1, prefijo + "%");

                            try (ResultSet rs = stmt.executeQuery()) {
                                if (rs.next()) {
                                    String ultimoId = rs.getString("id");
                                    int numero = Integer.parseInt(ultimoId.substring(3));
                                    logger.debug("Último ID en BD para {}: {} (número: {})", casa, ultimoId, numero);
                                    return numero;
                                }
                                logger.debug("No hay IDs previos para {}, comenzando en 0", casa);
                                return 0; // No hay IDs previos
                            }
                        } catch (SQLException e) {
                            logger.error("Error obteniendo último número para {}: {}", casa, e.getMessage());
                            return 0; // En caso de error, empezar en 0
                        }
                    })
                    .join(); // Esperar (bloqueante) - solo pasa una vez por casa
        } catch (Exception e) {
            logger.error("Error inicializando contador para {}: {}", casa, e.getMessage());
            return 0;
        }
    }

}
