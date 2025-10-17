package es.potter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Clase principal de la aplicación JavaFX.
 * Se encarga de inicializar y mostrar la ventana principal con su FXML y CSS.
 *
 * @author Telmo
 */
public class App extends Application {

    /**
     * Metodo que se ejecuta al iniciar la aplicación JavaFX.
     * Carga el archivo FXML, aplica la hoja de estilo CSS si está disponible,
     * configura el escenario (stage) y muestra la ventana principal.
     * En caso de error, muestra un mensaje por consola.
     *
     * @param stage Stage principal proporcionado por el sistema JavaFX.
     */
    @Override
    public void start(Stage stage) {
        try {
            // Cargar el archivo FXML
            FXMLLoader loaded = new FXMLLoader(getClass().getResource("/es/potter/fxml/visualizar.fxml"));
            Scene scene = new Scene(loaded.load());

            // Comprobar que el archivo CSS existe; si no, mostrar advertencia en consola
            var archivoCSS = getClass().getResource("/es/potter/css/estilos.css");
            if (archivoCSS != null) {
                scene.getStylesheets().add(archivoCSS.toExternalForm());
            } else {
                System.out.println("No se ha podido cargar el CSS.");
            }

            // Configurar y mostrar la ventana principal
            stage.setTitle("Adding/Deleting Rows in a TableViews");
            stage.setScene(scene);
            stage.setResizable(true);
            stage.show();

        } catch (Exception e) {
            System.out.println("No se ha podido abrir la ventana.");
            e.printStackTrace();
        }
    }

    /**
     * Metodo principal que lanza la aplicación JavaFX.
     *
     * @param args Argumentos de línea de comandos (no utilizados en esta aplicación).
     */
    public static void main(String[] args) {

        Application.launch();
    }
}