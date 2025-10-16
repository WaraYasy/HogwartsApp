package es.potter.servicio;

import es.potter.dao.DaoAlumno;
import es.potter.model.Alumno;
import es.potter.database.TipoBaseDatos;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

/**
 * ServicioAlumnos gestiona operaciones CRUD con sincronización multi-base de datos.
 * Implementa rollback automático para garantizar consistencia.
 *
 * @author Wara Pacheco
 * @version 3.0
 */
public class ServicioAlumnos {

    private static final Logger logger = LoggerFactory.getLogger(ServicioAlumnos.class);

    /*-------------------------------------------*/
    /*           MÉTODOS PÚBLICOS CRUD           */
    /*-------------------------------------------*/

    /**
     * Carga todos los alumnos de una base de datos.
     */
    public static CompletableFuture<ObservableList<Alumno>> cargarAlumnos(TipoBaseDatos tipoBaseDatos) {
        return DaoAlumno.cargarAlumnos(tipoBaseDatos);
    }

    /**
     * Crea un alumno con rollback automático si falla sincronización.
     * <p>
     * Flujo: Casa → Comunes → Si falla: Rollback (eliminar de Casa)
     * </p>
     */
    public static CompletableFuture<Boolean> nuevoAlumno(Alumno alumno) {
        TipoBaseDatos baseCasa = TipoBaseDatos.obtenerTipoBaseDatosPorCasa(alumno.getCasa());

        return DaoAlumno.nuevoAlumno(alumno, baseCasa)
                .thenCompose(exitoCasa -> {
                    if (!exitoCasa) {
                        logger.error("✗ Falló crear alumno en {}", baseCasa);
                        return CompletableFuture.completedFuture(false);
                    }

                    logger.info("→ Alumno {} creado en {}, sincronizando...", alumno.getId(), baseCasa);

                    // Intenta sincronizar
                    return sincronizarConComunes(alumno)
                            .thenCompose(exitoSync -> {
                                if (exitoSync) {
                                    logger.info("✓ Alumno {} creado en TODAS las bases", alumno.getId());
                                    return CompletableFuture.completedFuture(true);
                                } else {
                                    // ROLLBACK: Esperar a que termine
                                    logger.warn("⚠ Sincronización falló, ejecutando ROLLBACK...");
                                    return ejecutarRollbackCreacion(alumno, baseCasa);
                                }
                            });
                });
    }

    /**
     * Elimina un alumno con rollback automático si falla sincronización.
     * <p>
     * Flujo: Casa → Comunes → Si falla: Rollback (re-insertar en Casa)
     * </p>
     */
    public static CompletableFuture<Boolean> eliminarAlumno(Alumno alumno, TipoBaseDatos baseCasa) {
        return DaoAlumno.eliminarAlumno(alumno, baseCasa)
                .thenCompose(exitoCasa -> {
                    if (!exitoCasa) {
                        logger.error("✗ Falló eliminar alumno de {}", baseCasa);
                        return CompletableFuture.completedFuture(false);
                    }

                    logger.info("→ Alumno {} eliminado de {}, sincronizando...", alumno.getId(), baseCasa);

                    // Intenta eliminar de comunes
                    return eliminarDeComunes(alumno)
                            .thenCompose(exitoSync -> {
                                if (exitoSync) {
                                    logger.info("✓ Alumno {} eliminado de TODAS las bases", alumno.getId());
                                    return CompletableFuture.completedFuture(true);
                                } else {
                                    // ROLLBACK: Esperar a que termine
                                    logger.warn("⚠ Eliminación falló, ejecutando ROLLBACK...");
                                    return ejecutarRollbackEliminacion(alumno, baseCasa);
                                }
                            });
                });
    }

    /**
     * Modifica un alumno (sin rollback por ahora).
     */
    public static CompletableFuture<Boolean> modificarAlumno(String id, Alumno nuevo, TipoBaseDatos baseCasa) {
        return DaoAlumno.modificarAlumno(id, nuevo, baseCasa)
                .thenCompose(exitoCasa -> {
                    if (!exitoCasa) {
                        logger.error("✗ Falló modificar alumno en {}", baseCasa);
                        return CompletableFuture.completedFuture(false);
                    }

                    logger.info("→ Alumno {} modificado en {}, sincronizando...", id, baseCasa);

                    return modificarEnComunes(id, nuevo)
                            .thenApply(exitoSync -> {
                                if (exitoSync) {
                                    logger.info("✓ Alumno {} modificado en TODAS las bases", id);
                                    return true;
                                } else {
                                    logger.error("✗ Modificación parcial. Alumno {} INCONSISTENTE", id);
                                    logger.warn("⚠ Acción manual requerida: Verificar alumno {}", id);
                                    return false;
                                }
                            });
                });
    }

    /*-------------------------------------------*/
    /*            MÉTODOS DE ROLLBACK            */
    /*-------------------------------------------*/

    /**
     * ROLLBACK: Elimina alumno de la casa (porque falló sincronización).
     * Retorna CompletableFuture para esperar a que termine.
     */
    private static CompletableFuture<Boolean> ejecutarRollbackCreacion(Alumno alumno, TipoBaseDatos baseCasa) {
        logger.warn("═══════════════════════════════════════════");
        logger.warn("  ROLLBACK: Eliminando de {}", baseCasa);
        logger.warn("═══════════════════════════════════════════");

        return DaoAlumno.eliminarAlumno(alumno, baseCasa)
                .thenApply(exitoRollback -> {
                    if (exitoRollback) {
                        logger.info("ROLLBACK EXITOSO");
                        logger.info("Estado: Consistente (alumno no existe)");
                        return false; // La operación original falló
                    } else {
                        logger.error(" ROLLBACK FALLÓ");
                        logger.error("  Alumno {} existe solo en {}", alumno.getId(), baseCasa);
                        logger.error("  Acción: Eliminar manualmente ID {} de {}", alumno.getId(), baseCasa);
                        return false;
                    }
                })
                .exceptionally(ex -> {
                    logger.error("ERROR CRÍTICO en ROLLBACK: {}", ex.getMessage());
                    logger.error("Estado: INCONSISTENTE - Verificar alumno {}", alumno.getId());
                    return false;
                });
    }

    /**
     * ROLLBACK: Re-inserta alumno en la casa (porque falló eliminación de comunes).
     * Retorna CompletableFuture para esperar a que termine.
     */
    private static CompletableFuture<Boolean> ejecutarRollbackEliminacion(Alumno alumno, TipoBaseDatos baseCasa) {
        logger.warn("  ROLLBACK: Re-insertando en {}", baseCasa);

        return DaoAlumno.nuevoAlumno(alumno, baseCasa)
                .thenApply(exitoRollback -> {
                    if (exitoRollback) {
                        logger.info("ROLLBACK EXITOSO");
                        logger.info("Estado: Consistente (alumno existe en todas)");
                        return false; // La operación original falló
                    } else {
                        logger.error("ROLLBACK FALLÓ");
                        logger.error("Alumno {} NO existe en {}", alumno.getId(), baseCasa);
                        logger.error("Acción: Re-insertar manualmente {} en {}", alumno.getId(), baseCasa);
                        return false;
                    }
                })
                .exceptionally(ex -> {
                    logger.error("ERROR CRÍTICO en ROLLBACK: {}", ex.getMessage());
                    logger.error("Estado: INCONSISTENTE - Verificar alumno {}", alumno.getId());
                    return false;
                });
    }

    /*-------------------------------------------*/
    /*          MÉTODOS DE SINCRONIZACIÓN        */
    /*-------------------------------------------*/

    /**
     * Sincroniza creación en MariaDB y SQLite (paralelo).
     */
    private static CompletableFuture<Boolean> sincronizarConComunes(Alumno alumno) {
        CompletableFuture<Boolean> mariaDB = DaoAlumno.nuevoAlumno(alumno, TipoBaseDatos.MARIADB);
        CompletableFuture<Boolean> sqlite = DaoAlumno.nuevoAlumno(alumno, TipoBaseDatos.SQLITE);

        return mariaDB.thenCombine(sqlite, (exitoMariaDB, exitoSQLite) -> {
            if (exitoMariaDB && exitoSQLite) {
                logger.info(" MariaDB | SQLite");
                return true;
            } else {
                logger.error("Sincronización parcial:");
                logger.error("MariaDB: {}", exitoMariaDB ? "SI" : "NO");
                logger.error("SQLite:  {}", exitoSQLite ? "SI" : "NO");
                return false;
            }
        });
    }

    /**
     * Elimina de MariaDB y SQLite (paralelo).
     */
    private static CompletableFuture<Boolean> eliminarDeComunes(Alumno alumno) {
        CompletableFuture<Boolean> mariaDB = DaoAlumno.eliminarAlumno(alumno, TipoBaseDatos.MARIADB);
        CompletableFuture<Boolean> sqlite = DaoAlumno.eliminarAlumno(alumno, TipoBaseDatos.SQLITE);

        return mariaDB.thenCombine(sqlite, (exitoMariaDB, exitoSQLite) -> {
            if (exitoMariaDB && exitoSQLite) {
                logger.info(" MariaDB | SQLite");
                return true;
            } else {
                logger.error(" Eliminación parcial:");
                logger.error(" MariaDB: {}", exitoMariaDB ? "SI" : "NO");
                logger.error(" SQLite:  {}", exitoSQLite ? "SI" : "NO");
                return false;
            }
        });
    }

    /**
     * Modifica en MariaDB y SQLite (paralelo).
     */
    private static CompletableFuture<Boolean> modificarEnComunes(String id, Alumno alumno) {
        CompletableFuture<Boolean> mariaDB = DaoAlumno.modificarAlumno(id, alumno, TipoBaseDatos.MARIADB);
        CompletableFuture<Boolean> sqlite = DaoAlumno.modificarAlumno(id, alumno, TipoBaseDatos.SQLITE);

        return mariaDB.thenCombine(sqlite, (exitoMariaDB, exitoSQLite) -> {
            if (exitoMariaDB && exitoSQLite) {
                logger.info(" MariaDB | SQLite");
                return true;
            } else {
                logger.error(" Modificación parcial:");
                logger.error(" MariaDB: {}", exitoMariaDB ? "SI" : "NO");
                logger.error(" SQLite:  {}", exitoSQLite ? "SI" : "NO");
                return false;
            }
        });
    }
}
