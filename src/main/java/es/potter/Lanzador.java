package es.potter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Lanzador extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Cargar el archivo FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/es/potter/fxml/visualizar.fxml"));
        Parent root = loader.load();

        // Configurar la escena
        Scene scene = new Scene(root);

        // Configurar el stage
        primaryStage.setTitle("HogwartsApp");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
