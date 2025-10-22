package es.potter.control;

import es.potter.model.Alumno;
import es.potter.servicio.ServicioHogwarts;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class ControladorNuevoAlumno {

    @FXML private Button btnCancelar;
    @FXML private Button btnGuardar;
    @FXML private ComboBox<Integer> cmbxCurso;
    @FXML private ComboBox<String> cmbxCasa;
    @FXML private TextField txtApellido;
    @FXML private TextField txtNombre;
    @FXML private TextField txtPatronus;

    private final ResourceBundle bundle = ResourceBundle.getBundle(
            "es.potter.mensajes", Locale.getDefault()
    );

    // Referencias al controlador principal
    private ObservableList<Alumno> listaAlumnosPrincipal;
    private Map<Alumno, CheckBox> checkBoxMapPrincipal;
    private Runnable actualizarBotonesCallback;
    private Runnable refrescarTablaCallback;

    @FXML
    public void initialize() {
        cmbxCurso.setItems(javafx.collections.FXCollections.observableArrayList(1,2,3,4,5,6,7));
        cmbxCasa.setItems(javafx.collections.FXCollections.observableArrayList(
                "Gryffindor", "Slytherin", "Hufflepuff", "Ravenclaw"
        ));
    }

    // Método para pasar referencias del controlador principal
    public void setParentData(ObservableList<Alumno> listaAlumnos,
                              Map<Alumno, CheckBox> checkBoxMap,
                              Runnable actualizarBotones,
                              Runnable refrescarTabla) {
        this.listaAlumnosPrincipal = listaAlumnos;
        this.checkBoxMapPrincipal = checkBoxMap;
        this.actualizarBotonesCallback = actualizarBotones;
        this.refrescarTablaCallback = refrescarTabla;
    }

    @FXML
    void actionCancelar() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    @FXML
    void actionGuardar() {
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String patronus = txtPatronus.getText().trim().isEmpty() ? null : txtPatronus.getText().trim();
        Integer curso = cmbxCurso.getValue();
        String casa = cmbxCasa.getValue();

        if (nombre.isEmpty() || apellido.isEmpty() || curso == null || casa == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(bundle.getString("camposIncompletos"));
            alert.setHeaderText(bundle.getString("debeCompletarCampos"));
            alert.showAndWait();
            return;
        }

        Alumno alumno = new Alumno(nombre, apellido, curso, casa, patronus);

        ServicioHogwarts.nuevoAlumno(alumno)
                .thenAccept(exito -> Platform.runLater(() -> {
                    if (exito) {
                        // Añadir a la lista del principal
                        if (listaAlumnosPrincipal != null) listaAlumnosPrincipal.add(alumno);

                        // Crear checkbox para la fila nueva
                        if (checkBoxMapPrincipal != null) {
                            CheckBox cb = new CheckBox();
                            cb.selectedProperty().addListener((obs, oldVal, newVal) -> {
                                if (actualizarBotonesCallback != null) actualizarBotonesCallback.run();
                            });
                            checkBoxMapPrincipal.put(alumno, cb);
                        }

                        // Actualizar botones
                        if (actualizarBotonesCallback != null) actualizarBotonesCallback.run();

                        // Refrescar tabla para mostrar el nuevo alumno
                        if (refrescarTablaCallback != null) refrescarTablaCallback.run();

                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle(bundle.getString("alumnoGuardado"));
                        alert.setHeaderText(bundle.getString("alumnoGuardadoHeader"));
                        alert.showAndWait();

                        // Cerrar modal
                        Stage stage = (Stage) btnGuardar.getScene().getWindow();
                        stage.close();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle(bundle.getString("error"));
                        alert.setHeaderText(bundle.getString("alumnoNoGuardado"));
                        alert.showAndWait();
                    }
                }));
    }
}
