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
 * Ahora filtra la adición de alumnos según la casa visible en la tabla principal
 * para evitar que se muestren temporalmente en otras casas.
 *
 * @author Marco
 * @version 1.1
 * @since 2025-10-23
 */
public class ControladorNuevoAlumno {

    /** Botón para cancelar y cerrar la ventana */
    @FXML private Button btnCancelar;

    /** Botón para guardar el nuevo alumno */
    @FXML private Button btnGuardar;

    /** ComboBox para seleccionar curso (1-7) */
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

    /** Casa actualmente visible en la tabla principal */
    private String casaVisible;

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

        // Asegurar que el botón esté habilitado cada vez que se abra la ventana
        btnGuardar.setDisable(false);
    }

    /**
     * Configura referencias y callbacks desde el controlador principal.
     * Además recibe la casa actualmente visible para filtrar la lista.
     *
     * @param listaAlumnos Lista observable con los alumnos actuales.
     * @param checkBoxMap Mapa que relaciona alumnos y checkboxes.
     * @param actualizarBotones Callback para actualizar botones.
     * @param refrescarTabla Callback para refrescar vista tabla.
     * @param casaVisible Nombre de la casa actualmente visible en la tabla.
     *
     * @author Marco
     */
    public void setParentData(ObservableList<Alumno> listaAlumnos, Map<Alumno, CheckBox> checkBoxMap,
                              Runnable actualizarBotones, Runnable refrescarTabla, String casaVisible) {
        this.listaAlumnosPrincipal = listaAlumnos;
        this.checkBoxMapPrincipal = checkBoxMap;
        this.actualizarBotonesCallback = actualizarBotones;
        this.refrescarTablaCallback = refrescarTabla;
        this.casaVisible = casaVisible; // Guardamos la casa visible
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
     * Solo añade el alumno a la lista visible si coincide con la casa mostrada.
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

        // Deshabilitar botón mientras se guarda
        btnGuardar.setDisable(true);

        Alumno alumno = new Alumno(nombre, apellido, curso, casa, patronus);

        ServicioHogwarts.nuevoAlumno(alumno)
                .thenAccept(exito -> Platform.runLater(() -> {
                    if (exito) {
                        // Añadir a la lista visible solo si la casa coincide con la que se muestra
                        if (listaAlumnosPrincipal != null) {
                            if (casaVisible.equalsIgnoreCase(casa) || casaVisible.equalsIgnoreCase("Hogwarts")) {
                                listaAlumnosPrincipal.add(alumno);
                            }
                        }

                        if (checkBoxMapPrincipal != null) {
                            CheckBox cb = new CheckBox();
                            cb.selectedProperty().addListener((obs, oldVal, newVal) -> {
                                if (actualizarBotonesCallback != null) actualizarBotonesCallback.run();
                            });
                            checkBoxMapPrincipal.put(alumno, cb);
                        }

                        if (actualizarBotonesCallback != null) actualizarBotonesCallback.run();
                        if (refrescarTablaCallback != null) refrescarTablaCallback.run();

                        mandarAlertas(Alert.AlertType.INFORMATION, bundle.getString("alumnoGuardado"), bundle.getString("alumnoGuardadoHeader"));

                        Stage stage = (Stage) btnGuardar.getScene().getWindow();
                        stage.close();
                    } else {
                        mandarAlertas(Alert.AlertType.ERROR, bundle.getString("error"), bundle.getString("alumnoNoGuardado"));
                        btnGuardar.setDisable(false);
                    }
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        mandarAlertas(Alert.AlertType.ERROR, bundle.getString("error"), bundle.getString("alumnoNoGuardado"));
                        btnGuardar.setDisable(false);
                    });
                    return null;
                });
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
