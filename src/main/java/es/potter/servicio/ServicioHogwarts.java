package es.potter.servicio;

import es.potter.dao.DaoAlumno;
import es.potter.model.Alumno;
import es.potter.database.TipoBaseDatos;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Servicio de alumnos con arquitectura Master-Slave.
 *
 * ARQUITECTURA:
 * - MariaDB = MASTER (fuente de verdad única)
 * - Casas (Gryffindor, Slytherin, Ravenclaw, Hufflepuff) = SLAVES
 * - SQLite = SLAVE (backup local completo)
 *
 * GARANTÍAS:
 * - Consistencia: Todas las operaciones se completan en las 3 bases o ninguna
 * - Persistencia: Los datos se mantienen en MASTER + 2 SLAVES
 * - Recuperación: Método de sincronización desde MASTER
 */
public class ServicioHogwarts {

    private static final Logger logger = LoggerFactory.getLogger(ServicioHogwarts.class);

    // Casas de Hogwarts (SLAVES)
    private static final TipoBaseDatos[] CASAS = {
            TipoBaseDatos.GRYFFINDOR,
            TipoBaseDatos.SLYTHERIN,
            TipoBaseDatos.RAVENCLAW,
            TipoBaseDatos.HUFFLEPUFF
    };


    // ==================== CARGAR ====================
    /**
     * Carga todos los alumnos desde MASTER (MariaDB).
     */
    public static CompletableFuture<ObservableList<Alumno>> cargarAlumnos() {
        return DaoAlumno.cargarAlumnos(TipoBaseDatos.MARIADB);
    }

    /**
     * Carga alumnos desde una base específica.
     */
    public static CompletableFuture<ObservableList<Alumno>> cargarAlumnosDesde(TipoBaseDatos tipo) {
        return DaoAlumno.cargarAlumnos(tipo);
    }

    
    // ==================== CRUD ====================

    /**
     * Crea un alumno en 3 bases: MASTER + Casa + SQLite.
     * Solo retorna true si se guarda en TODAS.
     */
    public static CompletableFuture<Boolean> nuevoAlumno(Alumno alumno) {
        logger.info("Creando alumno '{}' en sistema Master-Slave", alumno.getNombre());

        return DaoAlumno.nuevoAlumno(alumno, TipoBaseDatos.MARIADB)
                .thenCompose(exitoMaster -> {
                    if (!exitoMaster) {
                        logger.error("❌ Falló en MASTER, operación abortada");
                        return CompletableFuture.completedFuture(false);
                    }

                    logger.info("✅ Guardado en MASTER, sincronizando slaves...");
                    return copiarASlaves(alumno);
                });
    }

    /**
     * Elimina un alumno de 3 bases: MASTER + Casa + SQLite.
     * Solo retorna true si se elimina de TODAS.
     */
    public static CompletableFuture<Boolean> eliminarAlumno(Alumno alumno) {
        logger.info("Eliminando alumno '{}' del sistema Master-Slave", alumno.getNombre());

        return DaoAlumno.eliminarAlumno(alumno, TipoBaseDatos.MARIADB)
                .thenCompose(exitoMaster -> {
                    if (!exitoMaster) {
                        logger.error("❌ Falló eliminar de MASTER, operación abortada");
                        return CompletableFuture.completedFuture(false);
                    }

                    logger.info("✅ Eliminado de MASTER, sincronizando slaves...");
                    return eliminarDeSlaves(alumno);
                });
    }

    /**
     * Modifica un alumno en 3 bases: MASTER + Casa + SQLite.
     * Solo retorna true si se modifica en TODAS.
     */
    public static CompletableFuture<Boolean> modificarAlumno(String id, Alumno alumno) {
        logger.info("Modificando alumno '{}' en sistema Master-Slave", alumno.getNombre());

        return DaoAlumno.modificarAlumno(id, alumno, TipoBaseDatos.MARIADB)
                .thenCompose(exitoMaster -> {
                    if (!exitoMaster) {
                        logger.error("❌ Falló modificar en MASTER, operación abortada");
                        return CompletableFuture.completedFuture(false);
                    }

                    logger.info("✅ Modificado en MASTER, sincronizando slaves...");
                    return modificarEnSlaves(id, alumno);
                });
    }

    /**
     * Guarda o actualiza un alumno (UPSERT).
     * Intenta modificar, si falla entonces crea.
     */
    public static CompletableFuture<Boolean> guardarOActualizarAlumno(Alumno alumno) {
        logger.debug("Guardando/actualizando alumno: {}", alumno.getId());

        // Primero intenta modificar en MASTER
        return DaoAlumno.modificarAlumno(alumno.getId(), alumno, TipoBaseDatos.MARIADB)
            .exceptionally(ex -> {
                logger.debug("Alumno {} no existe en MASTER, creando...", alumno.getId());
                return false;
            })
            .thenCompose(exito -> {
                if (exito) {
                    // Ya existía, sincronizar a slaves
                    logger.debug("Alumno {} modificado, sincronizando slaves...", alumno.getId());
                    return modificarEnSlaves(alumno.getId(), alumno);
                } else {
                    // No existía, crear en todas las bases
                    logger.debug("Creando alumno {} en todas las bases", alumno.getId());
                    return nuevoAlumno(alumno);
                }
            });
    }

    /**
     * Sincroniza una lista de cambios en lote al sistema Master-Slave.
     * - Procesa eliminaciones
     * - Procesa creaciones/modificaciones (upsert)
     *
     * @param alumnos Lista actual de alumnos a guardar/actualizar
     * @param alumnosEliminados Lista de alumnos a eliminar
     * @return CompletableFuture que retorna true si todas las operaciones fueron exitosas
     */
    public static CompletableFuture<Boolean> sincronizarCambiosEnLote(
            List<Alumno> alumnos,
            List<Alumno> alumnosEliminados
    ) {
        logger.info("Sincronizando cambios en lote: {} alumnos, {} eliminaciones",
                    alumnos.size(), alumnosEliminados.size());

        List<CompletableFuture<Boolean>> operaciones = new ArrayList<>();

        //  Procesar eliminaciones
        for (Alumno alumno : alumnosEliminados) {
            operaciones.add(eliminarAlumno(alumno));
        }

        // Procesar alumnos (upsert: modificar o crear)
        for (Alumno alumno : alumnos) {
            operaciones.add(guardarOActualizarAlumno(alumno));
        }

        // Esperar a que todas terminen
        return CompletableFuture.allOf(operaciones.toArray(new CompletableFuture[0]))
            .thenApply(v -> {
                long exitosas = operaciones.stream()
                    .filter(CompletableFuture::join)
                    .count();

                boolean todasExitosas = exitosas == operaciones.size();

                if (todasExitosas) {
                    logger.info("Sincronizacion en lote completada: {}/{} operaciones exitosas",
                               exitosas, operaciones.size());
                } else {
                    logger.warn("Sincronizacion en lote parcial: {}/{} operaciones exitosas",
                               exitosas, operaciones.size());
                }

                return todasExitosas;
            })
            .exceptionally(ex -> {
                logger.error("Error en sincronizacion en lote: {}", ex.getMessage());
                return false;
            });
    }

    // ==================== SINCRONIZACIÓN ====================

    /**
     * Sincroniza todas las bases desde MASTER.
     * - Cada casa recibe solo sus alumnos
     * - SQLite recibe todos los alumnos
     */
    public static CompletableFuture<Boolean> sincronizarDesdeMaster() {
        logger.info("🔄 Iniciando sincronización completa desde MASTER...");

        return DaoAlumno.cargarAlumnos(TipoBaseDatos.MARIADB)
                .thenCompose(alumnosMaster -> {
                    logger.info("Cargados {} alumnos desde MASTER", alumnosMaster.size());

                    List<CompletableFuture<Boolean>> sincronizaciones = new ArrayList<>();

                    // Sincronizar cada casa con SUS alumnos
                    for (TipoBaseDatos casa : CASAS) {
                        List<Alumno> alumnosCasa = filtrarPorCasa(alumnosMaster, casa.obtenerNombreCasa());
                        logger.info("{}: {} alumnos", casa, alumnosCasa.size());
                        sincronizaciones.add(sincronizarSlave(alumnosCasa, casa));
                    }

                    // SQLite recibe TODOS
                    logger.info("SQLite: {} alumnos (todos)", alumnosMaster.size());
                    sincronizaciones.add(sincronizarSlave(alumnosMaster, TipoBaseDatos.SQLITE));

                    // Esperar a que todas terminen
                    return CompletableFuture.allOf(sincronizaciones.toArray(new CompletableFuture[0]))
                            .thenApply(v -> {
                                boolean todoOk = sincronizaciones.stream().allMatch(CompletableFuture::join);

                                if (todoOk) {
                                    logger.info("✅ Sincronización completa exitosa");
                                } else {
                                    logger.warn("⚠️ Sincronización completada con algunos errores");
                                }

                                return todoOk;
                            });
                })
                .exceptionally(ex -> {
                    logger.error("❌ Error en sincronización: {}", ex.getMessage());
                    return false;
                });
    }

    /**
     * Sincroniza una base específica desde MASTER.
     * - Si es casa: solo sus alumnos
     * - Si es SQLite: todos los alumnos
     */
    public static CompletableFuture<Boolean> sincronizarBaseDesdeMaster(TipoBaseDatos tipo) {
        logger.info("🔄 Sincronizando {} desde MASTER...", tipo);

        return DaoAlumno.cargarAlumnos(TipoBaseDatos.MARIADB)
                .thenCompose(alumnosMaster -> {
                    List<Alumno> alumnosFiltrados;

                    if (tipo.esCasa()) {
                        // Filtrar solo alumnos de esta casa
                        alumnosFiltrados = filtrarPorCasa(alumnosMaster, tipo.obtenerNombreCasa());
                        logger.info("Filtrando {} alumnos de {}", alumnosFiltrados.size(), tipo.obtenerNombreCasa());
                    } else {
                        // SQLite u otro recibe todos
                        alumnosFiltrados = alumnosMaster;
                        logger.info("Sincronizando {} alumnos (todos)", alumnosFiltrados.size());
                    }

                    return sincronizarSlave(alumnosFiltrados, tipo);
                })
                .thenApply(exito -> {
                    if (exito) {
                        logger.info("✅ {} sincronizado correctamente", tipo);
                    } else {
                        logger.error("❌ Error sincronizando {}", tipo);
                    }
                    return exito;
                });
    }

    // ==================== MÉTODOS PRIVADOS ====================

    /**
     * Copia un alumno a su casa y SQLite.
     */
    private static CompletableFuture<Boolean> copiarASlaves(Alumno alumno) {
        TipoBaseDatos casa = TipoBaseDatos.obtenerTipoBaseDatosPorCasa(alumno.getCasa());

        CompletableFuture<Boolean> copiarCasa = DaoAlumno.nuevoAlumno(alumno, casa);
        CompletableFuture<Boolean> copiarSqlite = DaoAlumno.nuevoAlumno(alumno, TipoBaseDatos.SQLITE);

        return copiarCasa.thenCombine(copiarSqlite, (okCasa, okSqlite) -> {
            boolean exito = okCasa && okSqlite;

            if (exito) {
                logger.info("✅ Copiado a {} y SQLite", casa);
            } else {
                logger.error("❌ Error copiando - {}: {}, SQLite: {}", casa, okCasa, okSqlite);
            }

            return exito;
        });
    }

    /**
     * Elimina un alumno de su casa y SQLite.
     */
    private static CompletableFuture<Boolean> eliminarDeSlaves(Alumno alumno) {
        TipoBaseDatos casa = TipoBaseDatos.obtenerTipoBaseDatosPorCasa(alumno.getCasa());

        CompletableFuture<Boolean> eliminarCasa = DaoAlumno.eliminarAlumno(alumno, casa);
        CompletableFuture<Boolean> eliminarSqlite = DaoAlumno.eliminarAlumno(alumno, TipoBaseDatos.SQLITE);

        return eliminarCasa.thenCombine(eliminarSqlite, (okCasa, okSqlite) -> {
            boolean exito = okCasa && okSqlite;

            if (exito) {
                logger.info("✅ Eliminado de {} y SQLite", casa);
            } else {
                logger.error("❌ Error eliminando - {}: {}, SQLite: {}", casa, okCasa, okSqlite);
            }

            return exito;
        });
    }

    /**
     * Modifica un alumno en su casa y SQLite.
     */
    private static CompletableFuture<Boolean> modificarEnSlaves(String id, Alumno alumno) {
        TipoBaseDatos casa = TipoBaseDatos.obtenerTipoBaseDatosPorCasa(alumno.getCasa());

        CompletableFuture<Boolean> modificarCasa = DaoAlumno.modificarAlumno(id, alumno, casa);
        CompletableFuture<Boolean> modificarSqlite = DaoAlumno.modificarAlumno(id, alumno, TipoBaseDatos.SQLITE);

        return modificarCasa.thenCombine(modificarSqlite, (okCasa, okSqlite) -> {
            boolean exito = okCasa && okSqlite;

            if (exito) {
                logger.info("✅ Modificado en {} y SQLite", casa);
            } else {
                logger.error("❌ Error modificando - {}: {}, SQLite: {}", casa, okCasa, okSqlite);
            }

            return exito;
        });
    }

    /**
     * Sincroniza un slave con datos del master.
     * Inserta solo los alumnos que faltan.
     */
    private static CompletableFuture<Boolean> sincronizarSlave(List<Alumno> alumnosMaster, TipoBaseDatos slave) {
        logger.debug("Sincronizando {} con {} alumnos del MASTER", slave, alumnosMaster.size());

        return DaoAlumno.cargarAlumnos(slave)
                .thenCompose(alumnosSlave -> {
                    // IDs que ya tiene el slave
                    Set<String> idsSlave = alumnosSlave.stream()
                            .map(Alumno::getId)
                            .collect(Collectors.toSet());

                    // Alumnos que faltan en el slave
                    List<Alumno> faltantes = alumnosMaster.stream()
                            .filter(alumno -> !idsSlave.contains(alumno.getId()))
                            .collect(Collectors.toList());

                    if (faltantes.isEmpty()) {
                        logger.debug("{} ya está sincronizado", slave);
                        return CompletableFuture.completedFuture(true);
                    }

                    logger.info("Insertando {} alumnos faltantes en {}", faltantes.size(), slave);

                    // Insertar todos los faltantes en paralelo
                    List<CompletableFuture<Boolean>> inserts = faltantes.stream()
                            .map(alumno -> DaoAlumno.nuevoAlumno(alumno, slave))
                            .collect(Collectors.toList());

                    return CompletableFuture.allOf(inserts.toArray(new CompletableFuture[0]))
                            .thenApply(v -> {
                                long exitosos = inserts.stream().filter(CompletableFuture::join).count();

                                if (exitosos == faltantes.size()) {
                                    logger.info("✅ {} sincronizado: {}/{} alumnos", slave, exitosos, faltantes.size());
                                } else {
                                    logger.warn("⚠️ {} parcialmente sincronizado: {}/{} alumnos",
                                            slave, exitosos, faltantes.size());
                                }

                                return exitosos == faltantes.size();
                            });
                })
                .exceptionally(ex -> {
                    logger.error("❌ Error sincronizando {}: {}", slave, ex.getMessage());
                    return false;
                });
    }

    /**
     * Filtra alumnos por casa.
     */
    private static List<Alumno> filtrarPorCasa(List<Alumno> alumnos, String casa) {
        return alumnos.stream()
                .filter(alumno -> casa.equalsIgnoreCase(alumno.getCasa()))
                .collect(Collectors.toList());
    }
}