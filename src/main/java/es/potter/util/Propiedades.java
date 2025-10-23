package es.potter.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Clase utilitaria para cargar y acceder a propiedades de configuración.
 * <p>
 * Carga automáticamente el archivo {@code configuration.properties} desde
 * {@code src/main/resources/es/potter/configuration.properties}.
 * </p>
 *
 * @author Wara
 * @version 3.0
 * @since 2025-10-02
 */
public abstract class Propiedades {

    /** Ruta del archivo de configuración dentro del classpath. */
    private static final String RUTA_CONFIG = "es/potter/configuration.properties";

    /** Contenedor estático de las propiedades cargadas. */
    private static final Properties props = new Properties();

    /** Logger para el registro de eventos y errores. */
    private static final Logger logger = LoggerFactory.getLogger(Propiedades.class);

    // Bloque estático: carga el archivo de configuración al iniciar la clase
    static {
        try (InputStream input = obtenerInputStream()) {
            if (input == null) {
                throw new RuntimeException("No se encontró " + RUTA_CONFIG + " en el classpath");
            }

            props.load(input);
            logger.info("Archivo configuration.properties cargado correctamente.");

        } catch (IOException e) {
            logger.error("Error al cargar configuration.properties: {}", e.getMessage());
            throw new RuntimeException("Error crítico al cargar configuration.properties", e);
        }
    }

    /**
     * Intenta localizar el archivo de configuración usando diferentes estrategias de carga.
     *
     * @return Un {@link InputStream} del archivo, o {@code null} si no fue encontrado.
     *
     * @author Wara
     */
    private static InputStream obtenerInputStream() {
        InputStream input = Propiedades.class.getClassLoader().getResourceAsStream(RUTA_CONFIG);

        if (input == null) {
            input = Propiedades.class.getResourceAsStream("/" + RUTA_CONFIG);
        }

        return input;
    }

    /**
     * Obtiene el valor de una propiedad del archivo de configuración.
     *
     * @param clave La clave de la propiedad a buscar
     * @return El valor de la propiedad (puede ser vacío si la propiedad existe sin valor)
     * @throws RuntimeException Si la clave no existe en el archivo
     *
     * @author Wara
     */
    public static String getValor(String clave) {
        if (!props.containsKey(clave)) {
            logger.error("La clave '{}' no existe en configuration.properties", clave);
            logger.error("Claves disponibles: {}", props.keySet());
            throw new RuntimeException("La clave '" + clave + "' no está disponible en configuration.properties");
        }
        return props.getProperty(clave).trim();
    }
}