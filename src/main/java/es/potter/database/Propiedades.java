package es.potter.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Propiedades {
    private static final Properties props = new Properties();

    static {
        try (InputStream input = new FileInputStream("config.properties")) {
            props.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
            // En un entorno real, mejor loggear o lanzar excepción
        }
    }

    public static String getValor(String clave) {
        return props.getProperty(clave);
    }
