package es.potter.database;

/**
 * Enum que define los tipos de base de datos soportados por la aplicación.
 * ARQUITECTURA:
 * - MariaDB: Base de datos MASTER (fuente de verdad)
 * - SQLite: Base de datos local (backup)
 * - Casas: Bases de datos SLAVES específicas por casa de Hogwarts
 *
 * @author Wara Pacheco
 * @version 2.0
 * @since 2025-10-12
 */
@Deprecated
public enum TipoBaseDatos {

    /** Base de datos H2 */
    H2("db.h2", null),

    /** Base de datos HSQLDB (HyperSQL) */
    HSQLDB("db.hsqldb", null),

    /** Base de datos Oracle */
    ORACLE("db.oracle", null),

    /** Base de datos Apache Derby */
    APACHE_DERBY("db.derby", null),

    // MASTER Y BACKUP
    /** Base de datos MASTER - MariaDB (fuente de verdad) */
    MARIADB("db.mariadb", null),

    /** Base de datos local - SQLite (backup) */
    SQLITE("db.sqlite", null),

    // CASAS DE HOGWARTS (SLAVES)
    /** Casa Gryffindor - Apache Derby */
    GRYFFINDOR("db.derby", "Gryffindor"),

    /** Casa Slytherin - HSQLDB */
    SLYTHERIN("db.hsqldb", "Slytherin"),

    /** Casa Ravenclaw - Oracle */
    RAVENCLAW("db.oracle", "Ravenclaw"),

    /** Casa Hufflepuff - H2 */
    HUFFLEPUFF("db.h2", "Hufflepuff");

    // ==================== ATRIBUTOS ====================

    /** Prefijo usado en el archivo de propiedades para esta base de datos */
    private final String prefijo;

    /** Nombre de la casa Hogwarts, si aplica; {@code null} si no es una casa */
    private final String nombreCasa;

    // ==================== CONSTRUCTOR ====================

    /**
     * Constructor del enum.
     *
     * @param prefijo prefijo en configuration.properties (ej: "db.mariadb")
     * @param nombreCasa nombre de la casa de Hogwarts (null si no es una casa)
     *
     * @author Wara
     */
    TipoBaseDatos(String prefijo, String nombreCasa) {
        this.prefijo = prefijo;
        this.nombreCasa = nombreCasa;
    }

    // ==================== GETTERS ====================

    /**
     * Obtiene el prefijo de configuración.
     * Usado para buscar propiedades: {prefijo}.url, {prefijo}.user, {prefijo}.password
     *
     * @return prefijo de configuración (ej: "db.mariadb")
     *
     * @author Wara
     */
    public String getPrefijo() {
        return prefijo;
    }

    /**
     * Obtiene el nombre de la casa de Hogwarts.
     *
     * @return nombre de la casa, o null si no es una casa
     *
     * @author Wara
     */
    public String obtenerNombreCasa() {
        return nombreCasa;
    }

    /**
     * Verifica si este tipo corresponde a una casa de Hogwarts.
     *
     * @return true si es una casa (Gryffindor, Slytherin, Ravenclaw, Hufflepuff)
     *
     * @author Wara
     */
    public boolean esCasa() {
        return nombreCasa != null;
    }

    // ==================== MÉTODOS ESTÁTICOS ====================

    /**
     * Obtiene el tipo de base de datos correspondiente a una casa.
     *
     * @param nombreCasa nombre de la casa (case-insensitive)
     * @return tipo de base de datos correspondiente
     * @throws IllegalArgumentException si la casa no existe
     *
     * @author Wara
     */
    public static TipoBaseDatos obtenerTipoBaseDatosPorCasa(String nombreCasa) {
        if (nombreCasa == null || nombreCasa.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la casa no puede ser nulo o vacío");
        }
        return switch (nombreCasa.toLowerCase().trim()) {
            case "gryffindor" -> GRYFFINDOR;
            case "slytherin" -> SLYTHERIN;
            case "ravenclaw" -> RAVENCLAW;
            case "hufflepuff" -> HUFFLEPUFF;
            case "hogwarts" -> MARIADB;
            case "local" -> SQLITE;
            default -> throw new IllegalArgumentException("Casa desconocida: '" + nombreCasa + "'. " +
                            "Casas válidas: Gryffindor, Slytherin, Ravenclaw, Hufflepuff"
            );
        };
    }

    /**
     * Devuelve una representación en cadena del tipo de base de datos.
     *
     * @return representación en {@link String} del tipo de base de datos
     *
     * @author Wara
     */
    @Override
    public String toString() {
        if (esCasa()) {
            return nombreCasa + " (" + prefijo + ")";
        }
        return name() + " (" + prefijo + ")";
    }
}