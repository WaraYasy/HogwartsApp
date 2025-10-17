package es.potter.control;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ControladorAniadir {

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnGuardar;

    @FXML
    private ComboBox<?> cmbxCasa;

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


    @FXML
    void actionGuardar(ActionEvent event) {

    }

}