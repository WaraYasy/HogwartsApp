package es.potter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Clase principal de la aplicación JavaFX.
 * Se encarga de inicializar y mostrar la ventana principal con su FXML y CSS.
 *
 * @author Telmo
 */
public class HogwartsApp extends Application {

    /**
     * Escena principal de la aplicación JavaFX.
     */
    private static Scene scene;

    /**
     * Logger para registrar eventos, errores y mensajes de depuración durante el ciclo de vida de la aplicación.
     */
    private static final Logger logger = LoggerFactory.getLogger(HogwartsApp.class);

    public void start(Stage primaryStage) throws Exception {

        try {

            // Configurar idioma y resource bundle
            //Locale locale = Locale.forLanguageTag("en");
            Locale locale = Locale.forLanguageTag("es");

            logger.debug("Configurando idioma: {}", locale.getLanguage());
            ResourceBundle bundle = ResourceBundle.getBundle("es.potter.mensajes", locale);
            logger.debug("Resource bundle cargado para locale: {}", locale);

            // Cargar archivo FXML con la definición de la interfaz
            logger.debug("Cargando archivo FXML: fxml/ventanaPrincipal.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/es/potter/fxml/ventanaPrincipal.fxml"), bundle);
            scene = new Scene(fxmlLoader.load());
            logger.info("Archivo FXML cargado exitosamente");

            // Comprobar que el archivo CSS existe; si no, mostrar advertencia en consola
            var archivoCSS = getClass().getResource("/es/potter/css/estilo.css");
            if (archivoCSS != null) {
                scene.getStylesheets().add(archivoCSS.toExternalForm());
            } else {
                System.out.println("No se ha podido cargar el CSS.");
            }

            // Configurar el stage
            primaryStage.setTitle("HogwartsApp");
            primaryStage.setScene(scene);

            // Tamaños mínimos
            primaryStage.setMinWidth(1200);
            primaryStage.setMinHeight(700);

            // Centrar la ventana en la pantalla
            primaryStage.centerOnScreen();

            primaryStage.show();

        } catch (Exception e) {
            logger.error("Error al iniciar la aplicación", e);
            System.err.println("No se ha podido abrir la ventana.");
            e.printStackTrace();
            throw e;
        }
    }

    public static void main(String[] args) {
        logger.info("=== INICIO DE HOGWARTS APP ===");
        launch(args);
    }
}
