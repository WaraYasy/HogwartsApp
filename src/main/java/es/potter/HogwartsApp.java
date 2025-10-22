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

        // Configurar idioma y resource bundle
        //Locale locale = Locale.forLanguageTag("en");
        Locale locale = Locale.forLanguageTag("es");

        logger.debug("Configurando idioma: {}", locale.getLanguage());
        ResourceBundle bundle = ResourceBundle.getBundle("es.potter.mensajes", locale);
        logger.debug("Resource bundle cargado para locale: {}", locale);

        // Cargar archivo FXML con la definición de la interfaz
        logger.debug("Cargando archivo FXML: fxml/tableView.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/es/potter/fxml/visualizar.fxml"));
        scene = new Scene(fxmlLoader.load());
        logger.info("Archivo FXML cargado exitosamente");


        // Configurar el stage
        primaryStage.setTitle("HogwartsApp");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public static void main(String[] args) {
        logger.info("=== INICIO DE PEOPLE VIEW APP ===");
        launch(args);
    }
}
