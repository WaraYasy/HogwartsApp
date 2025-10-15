package es.potter.database;

/**
 * Enum que define los tipos de base de datos soportados por la aplicación.
 * Cada tipo contiene el prefijo de configuración necesario para establecer
 * la conexión correspondiente desde el archivo de propiedades.
 * 
 * <p>Ejemplo de uso:</p>
 * <pre>
 * Connection conn = ConexionFactory.crearConexion(TipoBaseDatos.H2);
 * </pre>
 *
 * @author Wara Pacheco
 * @version 1.0
 * @since 2025-10-12
 */
public enum TipoBaseDatos {

    /** Base de datos MariaDB */
    MARIADB("db.mariadb"),
    
    /** Base de datos SQLite */
    SQLITE("db.sqlite"),

    /** Base de datos H2 */
    H2("db.h2"),
    
    /** Base de datos HSQLDB (HyperSQL) */
    HSQLDB("db.hsqldb"),

    /** Base de datos Oracle */
    ORACLE("db.oracle"),
    
    /** Base de datos Apache Derby */
    APACHE_DERBY("db.derby");


    private final String prefijo;

    /**
     * Constructor del enum.
     *
     * @param prefijo el prefijo utilizado en el archivo de configuración
     *                para las propiedades de esta base de datos (url, user, password)
     */
    TipoBaseDatos(String prefijo) {
        this.prefijo = prefijo;
    }

    /**
     * Obtiene el prefijo de configuración asociado a este tipo de base de datos.
     * Este prefijo se usa para buscar las propiedades en configuration.properties.
     *
     * @return el prefijo de configuración (ej: "db.h2", "db.mariadb")
     */
    public String getPrefijo() {
        return prefijo;
    }

    //Método que devuelve la base de datos según la casa
    public static TipoBaseDatos obtenerTipoBaseDatosPorCasa(String casa) {
        switch (casa.toLowerCase()) {
            case "gryffindor":
                return TipoBaseDatos.APACHE_DERBY;
            case "slytherin":
                return TipoBaseDatos.HSQLDB;
            case "ravenclaw":
                return TipoBaseDatos.ORACLE;
            case "hufflepuff":
                return TipoBaseDatos.H2;
            case "central":
                return TipoBaseDatos.SQLITE;
            default:
                return TipoBaseDatos.MARIADB; // Por defecto
        }
    }
}
