package es.potter.control;

import es.potter.model.Alumno;
import es.potter.servicio.ServicioHogwarts;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

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

    private final ResourceBundle bundle = ResourceBundle.getBundle(
            "es.potter.mensajes", Locale.getDefault()
    );

    @FXML
    public void initialize() {
        // Inicializar cursos del 1 al 7
        cmbxCurso.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7));
    }

    @FXML
    void actionCancelar() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    // Cargar datos del alumno que se va a editar
    public void setAlumno(Alumno alumno) {
        this.alumnoActual = alumno;
        txtNombre.setText(alumno.getNombre());
        txtApellido.setText(alumno.getApellidos());
        txtPatronus.setText(alumno.getPatronus());
        cmbxCurso.setValue(alumno.getCurso());
    }

    @FXML
    void actionGuardar() {
        if (alumnoActual == null) return;

        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String patronus = txtPatronus.getText().trim().isEmpty() ? null : txtPatronus.getText().trim();
        Integer curso = cmbxCurso.getValue();

        if (nombre.isEmpty() || apellido.isEmpty() || curso == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(bundle.getString("camposIncompletos"));
            alert.setHeaderText(bundle.getString("debeCompletarCampos"));
            alert.showAndWait();
            return;
        }

        Alumno alumnoEditado = new Alumno(nombre, apellido, curso, alumnoActual.getCasa(), patronus);
        alumnoEditado.setId(alumnoActual.getId());

        ServicioHogwarts.modificarAlumno(alumnoActual.getId(), alumnoEditado)
                .thenAccept(exito -> Platform.runLater(() -> {
                    if (exito) {
                        // Actualizar la lista observable directamente si es accesible
                        alumnoActual.setNombre(nombre);
                        alumnoActual.setApellidos(apellido);
                        alumnoActual.setCurso(curso);
                        alumnoActual.setPatronus(patronus);

                        // Mostrar mensaje de éxito
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle(bundle.getString("alumnoModificado"));
                        alert.setHeaderText(bundle.getString("alumnoModificadoHeader"));
                        alert.showAndWait();

                        // Cerrar modal
                        Stage stage = (Stage) btnGuardar.getScene().getWindow();
                        stage.close();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle(bundle.getString("error"));
                        alert.setHeaderText(bundle.getString("alumnoNoModificado"));
                        alert.showAndWait();
                    }
                }));
    }
}
