package es.potter.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final String RUTA_CONFIG = "es/potter/configuration.properties";
    private static final Properties props = new Properties();
    private static final Logger logger = LoggerFactory.getLogger(Propiedades.class);

    // Bloque estático: carga el archivo de configuración al iniciar la clase
    static {
        InputStream input = null;
        
        try {
            // Intento 1: Usando ClassLoader (sin barra inicial)
            input = Propiedades.class.getClassLoader().getResourceAsStream(RUTA_CONFIG);
            
            // Intento 2: Usando Class.getResourceAsStream (con barra inicial)
            if (input == null) {
                input = Propiedades.class.getResourceAsStream("/" + RUTA_CONFIG);
            }
            
            if (input == null) {
                throw new RuntimeException("No se encontró " + RUTA_CONFIG + " en el classpath");
            }
            
            props.load(input);
            logger.info("Archivo configuration.properties cargado correctamente");
            
        } catch (Exception e) {
            logger.error("Error al cargar configuration.properties: {}", e.getMessage());
            throw new RuntimeException("Error crítico al cargar configuration.properties", e);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (Exception e) {
                    logger.warn("Error al cerrar el stream: {}", e.getMessage());
                }
            }
        }
    }

    /**
     * Obtiene el valor de una propiedad del archivo de configuración.
     *
     * @param clave La clave de la propiedad a buscar
     * @return El valor de la propiedad (puede ser vacío si la propiedad existe sin valor)
     * @throws RuntimeException Si la clave no existe en el archivo
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