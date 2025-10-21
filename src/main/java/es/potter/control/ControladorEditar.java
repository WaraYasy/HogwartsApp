package es.potter.control;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import es.potter.model.Alumno;
import es.potter.servicio.ServicioHogwarts;

public class ControladorEditar {

    private Alumno alumnoActual;

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnGuardar;

    @FXML
    private ComboBox<Integer> cmbxCurso;

    @FXML
    private TextField txtApellido;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtPatronus;

    @FXML
    void actionCancelar(ActionEvent event) {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    // Método para pasar el alumno que se va a editar
    public void setAlumno(Alumno alumno) {
        this.alumnoActual = alumno;
        txtNombre.setText(alumno.getNombre());
        txtApellido.setText(alumno.getApellidos());
        txtPatronus.setText(alumno.getPatronus());
        cmbxCurso.setValue(alumno.getCurso()); // ya es Integer
        // Si tienes combo de casas editable:
        // cmbxCasa.setValue(alumno.getCasa());
    }

    @FXML
    void actionGuardar(ActionEvent event) {
        if (alumnoActual == null) return; // seguridad

        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String patronus = txtPatronus.getText().trim();
        Integer curso = cmbxCurso.getValue();

        if (nombre.isEmpty() || apellido.isEmpty() || patronus.isEmpty() || curso == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Campos incompletos");
            alert.setHeaderText("Debe completar todos los campos");
            alert.showAndWait();
            return;
        }

        String idAlumno = alumnoActual.getId();

        Alumno alumno = new Alumno(
                nombre,
                apellido,
                curso,
                alumnoActual.getCasa(), // usar la casa actual
                patronus
        );

        ServicioHogwarts.modificarAlumno(idAlumno, alumno)
                .thenAccept(exito -> {
                    javafx.application.Platform.runLater(() -> {
                        if (exito) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Alumno modificado");
                            alert.setHeaderText("El alumno se actualizó correctamente en Hogwarts");
                            alert.showAndWait();
                            Stage stage = (Stage) btnCancelar.getScene().getWindow();
                            stage.close();
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText("No se pudo modificar el alumno en todas las bases de datos");
                            alert.showAndWait();
                        }
                    });
                });
    }
}
