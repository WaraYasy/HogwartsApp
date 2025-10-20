package es.potter.control;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ControladorEditar {

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnCancelar1;

    @FXML
    private ComboBox<?> cmbxCurso;

    @FXML
    private TextField txtApellido;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtPatronus;

    @FXML
    void actionCancelar(ActionEvent event) {
        // Obtener la ventana actual a partir del botón
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close(); // Cierra la ventana
    }

}