package es.potter;

/**
 * Clase lanzadora de la aplicación principal de Hogwarts.
 * <p>
 * Actúa como punto de entrada convencional de una aplicación Java estándar,
 * delegando la inicialización a la clase principal de JavaFX {@link HogwartsApp}.
 * Esto permite separar la lógica de arranque de la interfaz gráfica, facilitando
 * la ejecución, el empaquetado y la integración con herramientas externas.
 * </p>
 *
 * @author Wara
 * @version 1.0
 * @since 2025-10-23
 */
public class Lanzador{

    /**
     * Punto de entrada principal de la aplicación.
     *
     * <p>
     * Este metodo actúa como un proxy que permite añadir logging y
     * potencialmente otras operaciones de inicialización antes de
     * lanzar la interfaz gráfica.
     * </p>
     *
     * @param args Argumentos de línea de comandos que se pasan a la aplicación JavaFX
     *
     * @author Wara
     */
    public static void main(String[] args) {
        HogwartsApp.main(args);
    }
}
