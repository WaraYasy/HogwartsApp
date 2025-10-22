package es.potter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Lanzador{

    /**
     * Punto de entrada principal de la aplicación.
     * Registra el evento de lanzamiento y delega la ejecución a la clase
     * principal de JavaFX {@link HogwartsApp}.
     *
     * <p>Este método actúa como un proxy que permite añadir logging y
     * potencialmente otras operaciones de inicialización antes de
     * lanzar la interfaz gráfica.</p>
     *
     * @param args Argumentos de línea de comandos que se pasan a la aplicación JavaFX
     */
    public static void main(String[] args) {
        HogwartsApp.main(args);
    }
}
