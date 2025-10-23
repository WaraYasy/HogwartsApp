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

/**
 * Controlador para la ventana modal de creación de un nuevo alumno.
 * Gestiona la interacción con los campos del formulario, validación y
 * la comunicación con el servicio que persiste el nuevo alumno.
 * Actualiza la vista principal mediante callbacks proporcionados.
 *
 * @author Marco
 * @version 1.0
 * @since 2025-10-23
 */
public class ControladorNuevoAlumno {

    /** Botón para cancelar y cerrar la ventana */
    @FXML private Button btnCancelar;

    /** Botón para guardar el nuevo alumno */
    @FXML private Button btnGuardar;

    /** ComboBox para seleccionar curso (1-7 */
    @FXML private ComboBox<Integer> cmbxCurso;

    /** ComboBox para seleccionar casa (Gryffindor, Slytherin, etc.) */
    @FXML private ComboBox<String> cmbxCasa;

    /** Campo de texto para el apellido del alumno */
    @FXML private TextField txtApellido;

    /** Campo de texto para el nombre del alumno */
    @FXML private TextField txtNombre;

    /** Campo de texto para el patronus del alumno (opcional) */
    @FXML private TextField txtPatronus;

    /** Bundle de recursos para mensajes internacionalizados */
    private final ResourceBundle bundle = ResourceBundle.getBundle("es.potter.mensajes", Locale.getDefault());

    /** Lista observable de alumnos en la vista principal */
    private ObservableList<Alumno> listaAlumnosPrincipal;

    /** Mapa de alumnos a checkboxes para selección en la tabla principal */
    private Map<Alumno, CheckBox> checkBoxMapPrincipal;

    /** Callback para actualizar el estado de botones en la vista principal */
    private Runnable actualizarBotonesCallback;

    /** Callback para refrescar la tabla de alumnos en la vista principal */
    private Runnable refrescarTablaCallback;

    /**
     * Inicializa los elementos gráficos luego de cargarse el FXML.
     * Población de ComboBoxes con valores predefinidos.
     *
     * @author Marco
     */
    @FXML
    public void initialize() {
        cmbxCurso.setItems(javafx.collections.FXCollections.observableArrayList(1,2,3,4,5,6,7));
        cmbxCasa.setItems(javafx.collections.FXCollections.observableArrayList("Gryffindor", "Slytherin", "Hufflepuff", "Ravenclaw"));
    }

    /**
     * Configura las referencias y callbacks de la vista principal para
     * permitir actualización del listado y estado de los botones.
     *
     * @param listaAlumnos Lista observable con los alumnos actuales.
     * @param checkBoxMap Mapa que relaciona alumnos y checkboxes.
     * @param actualizarBotones Callback para actualizar botones.
     * @param refrescarTabla Callback para refrescar vista tabla.
     *
     * @author Marco
     */
    public void setParentData(ObservableList<Alumno> listaAlumnos, Map<Alumno, CheckBox> checkBoxMap,
                              Runnable actualizarBotones, Runnable refrescarTabla) {
        this.listaAlumnosPrincipal = listaAlumnos;
        this.checkBoxMapPrincipal = checkBoxMap;
        this.actualizarBotonesCallback = actualizarBotones;
        this.refrescarTablaCallback = refrescarTabla;
    }

    /**
     * Acción para cerrar la ventana modal sin guardar cambios.
     * Se invoca al pulsar el botón Cancelar.
     *
     * @author Marco
     */
    @FXML
    void actionCancelar() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    /**
     * Acción para validar y guardar un nuevo alumno.
     * Válida que los campos obligatorios no estén vacíos.
     * Llama al servicio para persistir el alumno y actualiza la vista principal.
     * Muestra alertas informativas o de error según el resultado.
     *
     * @author Marco
     */
    @FXML
    void actionGuardar() {
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String patronus = txtPatronus.getText().trim().isEmpty() ? null : txtPatronus.getText().trim();
        Integer curso = cmbxCurso.getValue();
        String casa = cmbxCasa.getValue();

        if (nombre.isEmpty() || apellido.isEmpty() || curso == null || casa == null) {
            mandarAlertas(Alert.AlertType.WARNING, bundle.getString("camposIncompletos"), bundle.getString("debeCompletarCampos"));
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

                        mandarAlertas(Alert.AlertType.INFORMATION, bundle.getString("alumnoGuardado"), bundle.getString("alumnoGuardadoHeader"));

                        // Cerrar modal
                        Stage stage = (Stage) btnGuardar.getScene().getWindow();
                        stage.close();
                    } else {
                        mandarAlertas(Alert.AlertType.ERROR, bundle.getString("error"), bundle.getString("alumnoNoGuardado"));
                    }
                }));
    }

    /**
     * Muestra una alerta JavaFX con los datos proporcionados.
     *
     * @param tipo Tipo de alerta (INFO, WARNING, ERROR...)
     * @param titulo Título de la alerta
     * @param mensajeTitulo Encabezado del mensaje
     *
     * @author Erlantz
     */
    private void mandarAlertas(Alert.AlertType tipo, String titulo, String mensajeTitulo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(mensajeTitulo);
        alerta.showAndWait();
    }
}
