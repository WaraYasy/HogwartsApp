package es.potter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
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
 * @author Wara
 * @version 1.0
 * @since 2025-10-23
 */
public class HogwartsApp extends Application {

    /** Logger SLF4J para la clase HogwartsApp */
    private static final Logger logger = LoggerFactory.getLogger(HogwartsApp.class);

    /**
     * Metodo de inicio de la aplicación JavaFX.
     * <p>
     * Es llamado automáticamente por el sistema de lanzamiento de JavaFX tras invocar {@link #launch(String...)}.
     * Se encarga de crear y configurar la ventana principal, establecer idioma, cargar el FXML de la vista
     * y aplicar la hoja de estilos CSS.
     * </p>
     *
     * @param primaryStage la ventana principal (stage) proporcionada por el sistema JavaFX.
     * @throws Exception si ocurre un error durante la inicialización de la interfaz gráfica.
     *
     * @author Wara
     */
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

            Scene scene = new Scene(fxmlLoader.load());
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
            // Mostrar ventana principal
            primaryStage.show();

        } catch (Exception e) {
            logger.error("Error al iniciar la aplicación", e);
            System.err.println("No se ha podido abrir la ventana.");
            throw e;
        }
    }

    /**
     * Metodo principal de inicio de la aplicación.
     * <p>
     * Este punto de entrada invoca el metodo {@link #launch(String...)} de {@link Application},
     * iniciando el ciclo de vida estándar de una aplicación JavaFX.
     * </p>
     *
     * @param args argumentos de línea de comandos, si los hubiera.
     *
     * @author Wara
     */
    public static void main(String[] args) {
        logger.info("=== INICIO DE HOGWARTS APP ===");
        launch(args);
    }
}
