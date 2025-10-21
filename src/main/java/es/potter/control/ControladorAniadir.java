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
        // 1️⃣ Obtener datos del formulario
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String patronus = txtPatronus.getText().trim();
        String curso = cmbxCurso.getValue() != null ? cmbxCurso.getValue().toString() : "";
        String casa = cmbxCasa.getValue() != null ? cmbxCasa.getValue().toString() : "";

        if (nombre.isEmpty() || apellido.isEmpty() || curso.isEmpty() || casa.isEmpty()) {
            // Mostrar alerta si faltan datos
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
            alert.setTitle("Campos incompletos");
            alert.setHeaderText("Debe completar todos los campos");
            alert.showAndWait();
            return;
        }

        // 2️⃣ Crear objeto Alumno
        es.potter.model.Alumno alumno = new es.potter.model.Alumno(nombre, apellido, Integer.parseInt(curso), casa, patronus);

        // 3️⃣ Guardar en MariaDB (MASTER) y SQLite (SLAVE) usando ServicioHogwarts
        es.potter.servicio.ServicioHogwarts.nuevoAlumno(alumno)
                .thenAccept(exito -> {
                    javafx.application.Platform.runLater(() -> {
                        if (exito) {
                            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                            alert.setTitle("Alumno guardado");
                            alert.setHeaderText("El alumno se guardó correctamente en MariaDB y SQLite");
                            alert.showAndWait();

                            // Cerrar ventana
                            Stage stage = (Stage) btnGuardar.getScene().getWindow();
                            stage.close();
                        } else {
                            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText("No se pudo guardar el alumno en todas las bases de datos");
                            alert.showAndWait();
                        }
                    });
                });
    }

}