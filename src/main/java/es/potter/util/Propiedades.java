package es.potter.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Propiedades {
    private static final Properties props = new Properties();

    static {
        try (InputStream input = Propiedades.class.getClassLoader().getResourceAsStream("configuration.properties")) {
            if (input == null) {
                System.err.println("No se pudo cargar el archivo configuration.properties desde el classpath.");
            } else {
                props.load(input);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String getValor(String clave) {
        return props.getProperty(clave);
    }
}