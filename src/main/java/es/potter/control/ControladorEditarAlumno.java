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

/**
 * Controlador para la ventana modal de edición de un alumno.
 * Gestiona la carga de datos del alumno, validación y guardado de cambios.
 * Actualiza la vista principal mediante la modificación directa del objeto Alumno.
 *
 * @author Marco, Erlantz
 * @version 1.0
 * @since 2025-10-23
 */
public class ControladorEditarAlumno {

    /** Alumno actualmente editado */
    private Alumno alumnoActual;

    /** Botón para cancelar y cerrar la ventana */
    @FXML
    private Button btnCancelar;

    /** Botón para guardar los cambios del alumno */
    @FXML
    private Button btnGuardar;

    /** ComboBox para seleccionar curso (1-7) */
    @FXML
    private ComboBox<Integer> cmbxCurso;

    /** Campo de texto para el apellido del alumno */
    @FXML
    private TextField txtApellido;

    /** Campo de texto para el nombre del alumno */
    @FXML
    private TextField txtNombre;

    /** Campo de texto para el patronus del alumno (opcional) */
    @FXML
    private TextField txtPatronus;

    /** Bundle de recursos para mensajes internacionalizados */
    private final ResourceBundle bundle = ResourceBundle.getBundle("es.potter.mensajes", Locale.getDefault());

    /**
     * Inicializa la vista configurando el ComboBox de curso con valores del 1 al 7.
     *
     * @author Marco
     */
    @FXML
    public void initialize() {
        // Inicializar cursos del 1 al 7
        cmbxCurso.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7));
    }

    /**
     * Acción para cancelar la edición y cerrar la ventana modal.
     *
     * @author Marco
     */
    @FXML
    void actionCancelar() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    /**
     * Carga los datos del alumno a editar en los controles gráficos.
     *
     * @param alumno Objeto Alumno con los datos a editar
     *
     * @author Marco
     */
    public void setAlumno(Alumno alumno) {
        this.alumnoActual = alumno;
        txtNombre.setText(alumno.getNombre());
        txtApellido.setText(alumno.getApellidos());
        txtPatronus.setText(alumno.getPatronus());
        cmbxCurso.setValue(alumno.getCurso());
    }

    /**
     * Acción para validar y guardar los cambios realizados en el alumno.
     * Válida que los campos obligatorios tienen valor no vacío.
     * Actualiza los datos a través del servicio y modifica el objeto alumno localmente.
     * Muestra alertas informativas o de error según el resultado.
     * Cierra la ventana al completar la operación con éxito.
     *
     * @author Marco
     */
    @FXML
    void actionGuardar() {
        if (alumnoActual == null) return;

        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String patronus = txtPatronus.getText().trim().isEmpty() ? null : txtPatronus.getText().trim();
        Integer curso = cmbxCurso.getValue();

        if (nombre.isEmpty() || apellido.isEmpty() || curso == null) {
            mandarAlertas(Alert.AlertType.WARNING, bundle.getString("camposIncompletos"), bundle.getString("debeCompletarCampos"));
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
                        mandarAlertas(Alert.AlertType.INFORMATION, bundle.getString("alumnoModificado"), bundle.getString("alumnoModificadoHeader"));

                        // Cerrar modal
                        Stage stage = (Stage) btnGuardar.getScene().getWindow();
                        stage.close();
                    } else {
                        mandarAlertas(Alert.AlertType.ERROR, bundle.getString("error"), bundle.getString("alumnoNoModificado"));
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
