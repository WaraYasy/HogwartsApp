
package es.potter.control;

import es.potter.control.ControladorEditarAlumno;
import es.potter.control.ControladorNuevoAlumno;
import es.potter.database.TipoBaseDatos;
import es.potter.model.Alumno;
import es.potter.servicio.ServicioHogwarts;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Controlador principal de la aplicación HogwartsApp.
 * Gestiona la interacción con la tabla de alumnos, botones y cargas de datos
 * desde las diferentes bases de datos (casas).
 * Proporciona funcionalidades CRUD (crear, leer, actualizar, eliminar)
 * y opciones de sincronización, filtrado y ayuda.
 *
 * @author Marco, Arantxa, Erlantz
 * @version 1.0
 * @since 2025-10-23
 */
public class ControladorPrincipal {

    /** Lista observable con todos los alumnos cargados actualmente */
    private final ObservableList<Alumno> listaAlumnos = FXCollections.observableArrayList();

    /** Mapa que relaciona cada alumno con su CheckBox correspondiente */
    private final Map<Alumno, CheckBox> checkBoxMap = new HashMap<>();

    /** Tipo de base de datos actual seleccionada */
    private TipoBaseDatos baseDatosActual = TipoBaseDatos.MARIADB;

    /** Panel raíz de la aplicación para aplicar estilos dinámicos */
    @FXML
    private BorderPane rootPane;

    /** Botones de las casas para cambiar la vista de la base de datos */
    @FXML
    private Button btnHogwarts, btnGryffindor, btnSlytherin, btnRavenclaw, btnHufflepuff;

    /** ImageView que muestra el escudo de la casa actual */
    @FXML
    private ImageView escudoImageView;

    /** Botones principales para la gestión de alumnos en la interfaz: editar, eliminar y nuevo. */
    @FXML
    private Button btnEditar, btnEliminar, btnNuevo;

    /** Icono del botón de modo oscuro (luna/sol) */
    @FXML
    private FontIcon iconoTema;

    /** Columnas con checkboxes para seleccionar múltiples alumnos en la tabla. */
    @FXML
    private TableColumn<Alumno, Void> checkBox;

    /** Columna que muestra el ID del alumno. */
    @FXML
    private TableColumn<Alumno, String> colId;

    /** Columna que muestra el nombre del alumno. */
    @FXML
    private TableColumn<Alumno, String> colNombre;

    /** Columna que muestra los apellidos del alumno. */
    @FXML
    private TableColumn<Alumno, String> colApellidos;

    /** Columna que muestra el curso del alumno (número entero). */
    @FXML
    private TableColumn<Alumno, Integer> colCurso;

    /** Columna que muestra la casa asignada al alumno. */
    @FXML
    private TableColumn<Alumno, String> colCasa;

    /** Columna que muestra el patronus del alumno. */
    @FXML
    private TableColumn<Alumno, String> colPatronus;

    /** Tabla que muestra la lista completa de alumnos. */
    @FXML
    private TableView<Alumno> tablaAlumnos;

    /** Campo de texto para ingresar filtros o búsquedas sobre la tabla */
    @FXML
    private TextField txtBusqueda;

    /** ImageView para mostrar la animación de carga con el caldero */
    @FXML
    private ImageView loadingImageView;

    /** Bundle del sistema de internacionalización */
    private ResourceBundle bundle;

    /** Imágenes para la animación del caldero */
    private Image calderoImage1;
    private Image calderoImage2;

    /** Timeline para la animación del caldero */
    private Timeline loadingAnimation;

    /** Lista filtrada utilizada para búsquedas en tabla */
    private FilteredList<Alumno> filteredList;

    /** Indica si el modo oscuro está activo */
    private boolean modoOscuroActivo = false;

    /** Logger para registrar eventos y errores de la conexión */
    private static final Logger logger = LoggerFactory.getLogger(ControladorPrincipal.class);

    /**
     * Inicializa todos los elementos visuales, listeners y bindings en la interfaz.
     * Se ejecuta automáticamente al cargar la vista principal.
     *
     * @author Marco, Arantxa
     */
    @FXML
    public void initialize() {
        bundle = ResourceBundle.getBundle("es.potter.mensajes", Locale.getDefault());

        configurarColumnas();
        configurarSeleccionMultiple();
        configurarFiltrado();
        configurarAnchosColumnas();
        inicializarBotones();
        configurarAnimacionCarga();

        // Inicializar con modo claro
        rootPane.getStyleClass().add("modo-claro");

        // Cargar datos iniciales desde la base de datos master
        cargarAlumnosPorCasa(baseDatosActual, true);
    }

    /**
     * Configura el mapeo de propiedades con las columnas de la tabla.
     *
     * @author Arantxa
     */
    private void configurarColumnas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        colCurso.setCellValueFactory(new PropertyValueFactory<>("curso"));
        colCasa.setCellValueFactory(new PropertyValueFactory<>("casa"));
        colPatronus.setCellValueFactory(new PropertyValueFactory<>("patronus"));

        // Habilitar ordenamiento en todas las columnas
        colId.setSortable(true);
        colNombre.setSortable(true);
        colApellidos.setSortable(true);
        colCurso.setSortable(true);
        colCasa.setSortable(true);
        colPatronus.setSortable(true);
    }

    /**
     * Configura la columnas de selección múltiple y los checkboxes individuales.
     *
     * @author Arantxa
     */
    private void configurarSeleccionMultiple() {
        CheckBox seleccionarTodosCheckBox = new CheckBox();
        checkBox.setGraphic(seleccionarTodosCheckBox);

        checkBox.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Alumno, Void> call(TableColumn<Alumno, Void> param) {
                return new TableCell<>() {
                    private final CheckBox checkBox = new CheckBox();

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                            setGraphic(null);
                            checkBox.setSelected(false);
                        } else {
                            Alumno alumno = getTableRow().getItem();
                            checkBoxMap.put(alumno, checkBox);
                            setGraphic(checkBox);

                            // Listener por fila para actualizar botones
                            checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> actualizarEstadoBotones());
                        }
                    }
                };
            }
        });

        seleccionarTodosCheckBox.setOnAction(event -> {
            boolean seleccionado = seleccionarTodosCheckBox.isSelected();
            for (CheckBox cb : checkBoxMap.values()) {
                cb.setSelected(seleccionado);
            }
        });
    }

    /**
     * Configura el comportamiento de búsqueda y filtrado en la tabla.
     *
     * @author Marco
     */
    private void configurarFiltrado() {
        // Configurar filtrado y ordenamiento de tabla
        filteredList = new FilteredList<>(listaAlumnos, p -> true);
        SortedList<Alumno> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(tablaAlumnos.comparatorProperty());
        tablaAlumnos.setItems(sortedList);
        txtBusqueda.textProperty().addListener((obs, oldValue, newValue) -> filtrarTabla(newValue));

        // Quitar mensaje "Tabla sin contenido"
        tablaAlumnos.setPlaceholder(new Label(""));
    }

    /**
     * Define los anchos proporcionales de las columnas respecto al tamaño de la tabla.
     *
     * @author Arantxa
     */
    private void configurarAnchosColumnas() {
        // Configurar anchos de columnas proporcionales
        tablaAlumnos.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double tableWidth = newWidth.doubleValue();
            // Proporciones basadas en los anchos originales (774px total)
            checkBox.setPrefWidth(tableWidth * 0.065);      // 50/774 = 6.5%
            colId.setPrefWidth(tableWidth * 0.194);         // 150/774 = 19.4%
            colNombre.setPrefWidth(tableWidth * 0.222);     // 172/774 = 22.2%
            colApellidos.setPrefWidth(tableWidth * 0.222);  // 172/774 = 22.2%
            colCurso.setPrefWidth(tableWidth * 0.079);      // 61/774 = 7.9%
            colCasa.setPrefWidth(tableWidth * 0.107);       // 83/774 = 10.7%
            colPatronus.setPrefWidth(tableWidth * 0.111);   // 86/774 = 11.1%
        });
    }

    /**
     * Inicializa los botones con los estados por defecto (deshabilitados)
     *
     * @author Marco
     */
    private void inicializarBotones() {
        // Inicializar botones deshabilitados
        btnEditar.setDisable(true);
        btnEliminar.setDisable(true);
    }

    /**
     * Configura la animación de carga usando las imágenes del caldero.
     * Alterna entre caldero.png y caldero2.png para crear un efecto de burbujeo.
     *
     * @author Arantxa
     */
    private void configurarAnimacionCarga() {
        try {
            // Cargar las imágenes del caldero
            calderoImage1 = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream("/es/potter/img/caldero.png")
            ));
            calderoImage2 = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream("/es/potter/img/caldero2.png")
            ));

            // Crear animación que alterna entre las dos imágenes
            loadingAnimation = new Timeline(
                    new KeyFrame(Duration.ZERO, e -> loadingImageView.setImage(calderoImage1)),
                    new KeyFrame(Duration.millis(400), e -> loadingImageView.setImage(calderoImage2)),
                    new KeyFrame(Duration.millis(800), e -> loadingImageView.setImage(calderoImage1))
            );
            loadingAnimation.setCycleCount(Timeline.INDEFINITE);
            loadingAnimation.setAutoReverse(false);
        } catch (Exception e) {
            logger.error("Error al cargar las imágenes del caldero para la animación de carga", e);
        }
    }

    /**
     * Filtra el contenido de la tabla en función del texto introducido en el campo de búsqueda.
     *
     * @param texto Texto de búsqueda introducido por el usuario.
     *
     * @author Marco
     */
    private void filtrarTabla(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            filteredList.setPredicate(a -> true);
        } else {
            String filtro = texto.toLowerCase();
            filteredList.setPredicate(a ->
                    a.getNombre().toLowerCase().contains(filtro) ||
                            a.getApellidos().toLowerCase().contains(filtro) ||
                            a.getCasa().toLowerCase().contains(filtro) ||
                            a.getId().toLowerCase().contains(filtro)
            );
        }
    }

    /**
     * Actualiza el estado (habilitado/deshabilitado) de los botones
     * según los alumnos seleccionados.
     *
     * @author Marco
     */
    private void actualizarEstadoBotones() {
        // Asegurarse de que el checkBoxMap esté actualizado antes de contar
        Platform.runLater(() -> {
            long seleccionados = checkBoxMap.values().stream()
                    .filter(CheckBox::isSelected)
                    .count();
            btnEditar.setDisable(seleccionados != 1);
            btnEliminar.setDisable(seleccionados == 0);
        });
    }

    /**
     * Actualiza los estilos visuales de los botones de casa, del panel raíz
     * y del escudo según la base de datos actualmente seleccionada.
     * Aplica la clase CSS correspondiente a la casa activa y cambia la imagen del escudo.
     *También considera el modo oscuro para aplicar las clases de casa correctas.
     * @author Arantxa
     */
    private void actualizarEstilosCasa() {
        // Remover la clase 'selected' de todos los botones
        btnHogwarts.getStyleClass().remove("selected");
        btnGryffindor.getStyleClass().remove("selected");
        btnSlytherin.getStyleClass().remove("selected");
        btnRavenclaw.getStyleClass().remove("selected");
        btnHufflepuff.getStyleClass().remove("selected");

        // Remover todas las clases de casa del rootPane (normales y oscuras)
        rootPane.getStyleClass().removeAll(
                "hogwarts", "gryffindor", "slytherin", "ravenclaw", "hufflepuff",
                "hogwarts-oscuro", "gryffindor-oscuro", "slytherin-oscuro", "ravenclaw-oscuro", "hufflepuff-oscuro"
        );

        // Añadir la clase 'selected' al botón activo, la clase de casa al rootPane y actualizar escudo
        String rutaEscudo;
        String claseCasa;

        switch (baseDatosActual) {
            case MARIADB -> {
                btnHogwarts.getStyleClass().add("selected");
                claseCasa = modoOscuroActivo ? "hogwarts-oscuro" : "hogwarts";
                rutaEscudo = modoOscuroActivo ? "/es/potter/img/hogwarts-oscuro.png" : "/es/potter/img/hogwarts.png";
            }
            case GRYFFINDOR -> {
                btnGryffindor.getStyleClass().add("selected");
                claseCasa = modoOscuroActivo ? "gryffindor-oscuro" : "gryffindor";
                rutaEscudo = modoOscuroActivo ? "/es/potter/img/gryffindor-oscuro.png" : "/es/potter/img/gryffindor.png";
            }
            case SLYTHERIN -> {
                btnSlytherin.getStyleClass().add("selected");
                claseCasa = modoOscuroActivo ? "slytherin-oscuro" : "slytherin";
                rutaEscudo = modoOscuroActivo ? "/es/potter/img/slytherin-oscuro.png" : "/es/potter/img/slytherin.png";
            }
            case RAVENCLAW -> {
                btnRavenclaw.getStyleClass().add("selected");
                claseCasa = modoOscuroActivo ? "ravenclaw-oscuro" : "ravenclaw";
                rutaEscudo = modoOscuroActivo ? "/es/potter/img/ravenclaw-oscuro.png" : "/es/potter/img/ravenclaw.png";
            }
            case HUFFLEPUFF -> {
                btnHufflepuff.getStyleClass().add("selected");
                claseCasa = modoOscuroActivo ? "hufflepuff-oscuro" : "hufflepuff";
                rutaEscudo = modoOscuroActivo ? "/es/potter/img/hufflepuff-oscuro.png" : "/es/potter/img/hufflepuff.png";
            }
            default -> {
                claseCasa = "hogwarts";
                rutaEscudo = "/es/potter/img/hogwarts.png";
            }
        }

        // Aplicar la clase de casa correspondiente
        rootPane.getStyleClass().add(claseCasa);

        // Actualizar la imagen del escudo
        try {
            logger.info("Cargando escudo desde: {}", rutaEscudo);
            // Cargar la imagen en su resolución original con alta calidad
            Image nuevaImagen = new Image(Objects.requireNonNull(getClass().getResourceAsStream(rutaEscudo)),
                    0,
                    0,
                    true,
                    true);
            escudoImageView.setImage(nuevaImagen);
            logger.info("Escudo actualizado correctamente");
        } catch (Exception e) {
            logger.error("Error al cargar la imagen del escudo: {}", rutaEscudo, e);
        }
    }

    /**
     * Elimina los alumnos seleccionados tras mostrar un mensaje de confirmación.
     * Esta función:
     * 1. Muestra una alerta al usuario para confirmar la eliminación.
     * 2. Ejecuta las eliminaciones de forma asíncrona utilizando CompletableFuture.
     * 3. Actualiza la interfaz (tabla y botones) en el hilo de JavaFX una vez finalizado el proceso.
     * Se eliminan los alumnos de todas las bases de datos (master y slaves).
     * Si ocurre un error, se muestra una alerta de tipo ERROR.
     *
     * @author Marco, Wara
     */
    @FXML
    void actionEliminar() {
        List<Alumno> alumnosSeleccionados = obtenerAlumnosSeleccionados();
        if (alumnosSeleccionados.isEmpty()) return;

        // Mostrar alerta de confirmación antes de eliminar
        Alert confirmacion = new Alert(Alert.AlertType.WARNING);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Estás seguro de eliminar " + alumnosSeleccionados.size() + " alumno(s)?");
        confirmacion.setContentText("Esta acción eliminará los alumnos de todas las bases de datos y no se puede deshacer.");

        ButtonType botonSi = new ButtonType("Eliminar", ButtonBar.ButtonData.OK_DONE);
        ButtonType botonNo = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        confirmacion.getButtonTypes().setAll(botonSi, botonNo);

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isEmpty() || resultado.get() != botonSi) {
            return; // Usuario canceló
        }

        // Mostrar animación de carga
        loadingImageView.setVisible(true);
        loadingAnimation.play();

        // Eliminar cada alumno del sistema Master-Slave
        List<CompletableFuture<Boolean>> eliminaciones = new ArrayList<>();
        for (Alumno alumno : alumnosSeleccionados) {
            eliminaciones.add(ServicioHogwarts.eliminarAlumno(alumno));
        }

        // Esperar a que todas las eliminaciones terminen
        CompletableFuture.allOf(eliminaciones.toArray(new CompletableFuture[0]))
                .thenApply(v -> eliminaciones.stream()
                        .allMatch(CompletableFuture::join))
                .thenAccept(todasExitosas -> Platform.runLater(() -> {
                    loadingAnimation.stop();
                    loadingImageView.setVisible(false);

                    if (todasExitosas) {
                        // Eliminar de la lista local y del map de checkboxes
                        listaAlumnos.removeAll(alumnosSeleccionados);
                        for (Alumno alumno : alumnosSeleccionados) checkBoxMap.remove(alumno);

                        // Deseleccionar todos los checkboxes y refrescar la tabla
                        checkBoxMap.values().forEach(cb -> cb.setSelected(false));
                        tablaAlumnos.refresh();
                        actualizarEstadoBotones();
                        mandarAlertas(Alert.AlertType.INFORMATION,  bundle.getString("eliminacionExitosa"), bundle.getString("alumnosEliminados"), alumnosSeleccionados.size() + " " + bundle.getString("contenidoEliminadoCorrectamente"));
                    } else {
                        mandarAlertas(Alert.AlertType.WARNING, bundle.getString("eliminacionParcial"), bundle.getString("noSePuedenEliminar"), bundle.getString("contenidoRevisarLogs"));
                    }
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        loadingAnimation.stop();
                        loadingImageView.setVisible(false);

                        mandarAlertas(Alert.AlertType.ERROR, bundle.getString("error"), bundle.getString("errorEliminarAlumno"), bundle.getString("contenidoNoSePuedeEliminar") + " " + ex.getMessage());
                    });
                    return null;
                });
    }

    /**
     * Obtiene los alumnos actualmente seleccionados en la tabla.
     *
     * @return Lista de alumnos seleccionados
     *
     * @author Marco
     */
    private List<Alumno> obtenerAlumnosSeleccionados() {
        List<Alumno> seleccionados = new ArrayList<>();
        for (Map.Entry<Alumno, CheckBox> entry : checkBoxMap.entrySet()) {
            if (entry.getValue().isSelected()) {
                seleccionados.add(entry.getKey());
            }
        }
        return seleccionados;
    }

    /**
     * Cierra la aplicación con confirmación del usuario.
     *
     * @author Marco
     */
    @FXML
    void actionCerrar() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar salida");
        alert.setHeaderText("¿Desea cerrar la aplicación?");
        alert.setContentText("Se perderán los cambios no guardados.");

        ButtonType botonSi = new ButtonType("Salir", ButtonBar.ButtonData.OK_DONE);
        ButtonType botonNo = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(botonSi, botonNo);

        Optional<ButtonType> resultado = alert.showAndWait();
        if (resultado.isPresent() && resultado.get() == botonSi) {
            Platform.exit();
        }
    }

    /**
     * Sincroniza las bases de datos "slave" con la base de datos "master" a través de un proceso asíncrono.
     * Si el proceso de sincronización tiene éxito, se muestra un mensaje informativo y se recargan los alumnos
     * de la base de datos actualmente seleccionada.
     * Si ocurre un error, se muestra una alerta con el mensaje de error detallado.
     *
     * @author Marco, Arantxa
     */
    @FXML
    void actionRecargar() {
        // Mostrar alerta de confirmación antes de sincronizar
        Alert confirmacion = new Alert(Alert.AlertType.WARNING);
        confirmacion.setTitle(bundle.getString("confirmarActualizacion"));
        confirmacion.setHeaderText(bundle.getString("advertenciaActualizacion"));
        confirmacion.setContentText(bundle.getString("mensajeAdvertenciaActualizacion"));

        ButtonType botonSi = new ButtonType(bundle.getString("continuar"), ButtonBar.ButtonData.OK_DONE);
        ButtonType botonNo = new ButtonType(bundle.getString("cancelar"), ButtonBar.ButtonData.CANCEL_CLOSE);

        confirmacion.getButtonTypes().setAll(botonSi, botonNo);

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isEmpty() || resultado.get() != botonSi) {
            return; // Usuario canceló
        }

        // Mostrar animación de carga
        loadingImageView.setVisible(true);
        loadingAnimation.play();

        // Sincronizar desde Master (reconstruye todas las BDs slave)
        ServicioHogwarts.sincronizarDesdeMaster()
                .thenAccept(exito -> Platform.runLater(() -> {
                    loadingAnimation.stop();
                    loadingImageView.setVisible(false);

                    if (exito) {
                        mandarAlertas(Alert.AlertType.INFORMATION, bundle.getString("sincronizacion"), bundle.getString("sincronizacionCompletada"),bundle.getString("contenidoSincronizacion"));

                        // Recargar vista actual
                        cargarAlumnosPorCasa(baseDatosActual);
                    } else {
                        mandarAlertas(Alert.AlertType.WARNING, bundle.getString("sincronizacion"), bundle.getString("sincronizacionParcial"), bundle.getString("contenidoSincronizacionParcial"));
                    }
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        loadingAnimation.stop();
                        loadingImageView.setVisible(false);

                        mandarAlertas(Alert.AlertType.ERROR, bundle.getString("error"), bundle.getString("errorSincronizacion"), bundle.getString("contenidoErrorSincronizar") + " " + ex.getMessage());
                    });
                    return null;
                });
    }

    /**
     * Muestra una ventana modal con información sobre los desarrolladores de la aplicación HogwartsApp
     * y un enlace a la guía rápida en línea.
     * Utiliza un componente {@link javafx.scene.control.Hyperlink Hyperlink} para abrir una URL externa
     * en el navegador predeterminado.
     *
     * @author Marco, Arantxa
     */
    @FXML
    void actionAyuda() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sobre HogwartsApp");
        alert.setHeaderText("HogwartsApp");

        // Crear contenido personalizado con enlace
        VBox content = new javafx.scene.layout.VBox(10);
        content.getChildren().add(new Label("Aplicación desarrollada por el Equipo Potter.\n"));
        content.getChildren().add(new Label("• Curso: DM2"));
        content.getChildren().add(new Label("• Año: 2025\n"));

        Hyperlink link = new Hyperlink("Ver Guía Rápida");
        link.setOnAction(event -> {
            try {
                Desktop.getDesktop().browse(
                        new URI("https://drive.google.com/file/d/1NNKw8lqIA9fLnGyaAIynb3-7H5PA6mqZ/view?usp=sharing")
                );
            } catch (Exception ex) {
                logger.error("Error al abrir el enlace de la Guía Rápida en el navegador", ex);
            }
        });
        content.getChildren().add(link);

        alert.getDialogPane().setContent(content);
        alert.showAndWait();
    }

    /**
     * Abre una ventana modal que permite editar la información de un único alumno seleccionado.
     * Si hay más de un alumno marcado o ninguno, no hace nada.
     * Carga el archivo FXML correspondiente al editor, inicializa su controlador
     * y muestra el diálogo de manera modal.
     * Al cerrarse, actualiza la tabla principal y reestablece los checkboxes.
     *
     * @author Marco
     */
    @FXML
    void actionEditar() {
        List<Alumno> seleccionados = obtenerAlumnosSeleccionados();
        if (seleccionados.size() != 1) return;

        Alumno alumnoSeleccionado = seleccionados.getFirst();

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/es/potter/fxml/modalEditarAlumno.fxml"),
                    bundle
            );
            Parent root = loader.load();

            ControladorEditarAlumno controladorEditar = loader.getController();
            controladorEditar.setAlumno(alumnoSeleccionado);

            // Aplicar el tema actual al modal
            if (modoOscuroActivo) {
                root.getStyleClass().add("modo-oscuro");
            } else {
                root.getStyleClass().add("modo-claro");
            }

            Stage modalStage = new Stage();
            modalStage.setTitle(bundle.getString("editarAlumno"));
            modalStage.setScene(new Scene(root));
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(btnEditar.getScene().getWindow());
            modalStage.setResizable(false);
            modalStage.showAndWait();

            // Deseleccionar todos los checkboxes y refrescar la tabla
            checkBoxMap.values().forEach(cb -> cb.setSelected(false));
            tablaAlumnos.refresh(); // <--- importante para que se vea
            actualizarEstadoBotones();

        } catch (IOException ex) {
            logger.error("Error al cargar modal para editar alumno con ID {}", alumnoSeleccionado.getId(), ex);
            mandarAlertas(Alert.AlertType.ERROR, bundle.getString("error"), bundle.getString("ficheroNoCargado"), bundle.getString("verificaRuta"));
        }
    }

    /**
     * Abre una ventana modal para registrar un nuevo alumno en la base de datos.
     * Carga el formulario definido en el archivo FXML "modalNuevoAlumno.fxml",
     * configura el controlador hijo con los datos compartidos
     * y muestra la ventana de manera modal sobre la principal.
     * Una vez cerrada la ventana, actualiza la tabla principal para reflejar los cambios.
     *
     * @author Marco
     */
    @FXML
    void actionNuevo() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/es/potter/fxml/modalNuevoAlumno.fxml"),
                    bundle
            );
            Parent root = loader.load();

            ControladorNuevoAlumno controladorAniadir = loader.getController();
            // Callback actualizado para limpiar correctamente el checkbox "seleccionar todos"
            controladorAniadir.setParentData(
                    listaAlumnos,
                    checkBoxMap,
                    this::actualizarEstadoBotones,
                    () -> {
                        tablaAlumnos.refresh();
                        // Limpiar checkbox de "seleccionar todos" al cerrar modal
                        if (checkBox.getGraphic() != null && checkBox.getGraphic() instanceof CheckBox) {
                            CheckBox seleccionarTodosCheckBox = (CheckBox) checkBox.getGraphic();
                            seleccionarTodosCheckBox.setSelected(false);
                        }
                    },
                    baseDatosActual.name() // <-- quinto argumento: la casa visible
            );

            // Aplicar el tema actual al modal
            if (modoOscuroActivo) {
                root.getStyleClass().add("modo-oscuro");
            } else {
                root.getStyleClass().add("modo-claro");
            }

            Stage modalStage = new Stage();
            modalStage.setTitle(bundle.getString("nuevoAlumno"));
            modalStage.setScene(new Scene(root));
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(btnNuevo.getScene().getWindow());
            modalStage.setResizable(false);
            modalStage.showAndWait();

        } catch (IOException ex) {
            logger.error("Error al cargar modal para nuevo alumno", ex);
            mandarAlertas(Alert.AlertType.ERROR, bundle.getString("error"), bundle.getString("ficheroNoCargado"), bundle.getString("verificaRuta"));
        }
    }

    /**
     * Carga y muestra los alumnos según la casa o base de datos indicada.
     *
     * @param tipoBase Tipo de base de datos a cargar (MariaDB, Gryffindor...)
     *
     * @author Marco
     */
    private void cargarAlumnosPorCasa(TipoBaseDatos tipoBase) {
        cargarAlumnosPorCasa(tipoBase, false);
    }

    /**
     * Carga y muestra los alumnos según la casa o base de datos indicada.
     *
     * @param tipoBase Tipo de base de datos a cargar (MariaDB, Gryffindor...)
     * @param esCargaInicial Se inicia la primera vez o se recarga la lista
     *
     * @author Marco, Arantxa
     */
    private void cargarAlumnosPorCasa(TipoBaseDatos tipoBase, boolean esCargaInicial) {
        baseDatosActual = tipoBase;
        listaAlumnos.clear();

        // Mostrar animación de carga
        loadingImageView.setVisible(true);
        loadingAnimation.play();

        ServicioHogwarts.cargarAlumnosDesde(tipoBase)
                .thenAccept(alumnos -> Platform.runLater(() -> {
                    listaAlumnos.setAll(alumnos);
                    // Limpiar checkboxes seleccionados después de cargar
                    checkBoxMap.clear();
                    if (checkBox.getGraphic() instanceof CheckBox seleccionarTodosCheckBox) {
                        seleccionarTodosCheckBox.setSelected(false);
                    }
                    actualizarEstadoBotones();

                    // Actualizar estilos de la casa activa
                    actualizarEstilosCasa();

                    // Ocultar animación de carga
                    loadingAnimation.stop();
                    loadingImageView.setVisible(false);
                }))
                .exceptionally(ex -> {
                    logger.error("Error cargando alumnos de la base de datos {}: {}", tipoBase, ex.getMessage());
                    Platform.runLater(() -> {
                        // Ocultar animación de carga en caso de error
                        loadingAnimation.stop();
                        loadingImageView.setVisible(false);

                        mandarAlertas(Alert.AlertType.ERROR, bundle.getString("errorCargarAlumnos"), bundle.getString("noPuedeCargarAlumnos") + " " + tipoBase, ex.getMessage());

                        // Si es la carga inicial, cerrar la aplicación
                        if (esCargaInicial) {
                            logger.error("Error crítico en la carga inicial de alumnos. Cerrando aplicación...");
                            Platform.exit();
                            System.exit(1);
                        }
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
     * @param mensaje Contenido del mensaje
     *
     * @author Erlantz
     */
    private void mandarAlertas(Alert.AlertType tipo, String titulo, String mensajeTitulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(mensajeTitulo);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    /**
     * Carga los alumnos de la casa Gryffindor.
     *
     * @author Marco
     */
    @FXML
    void actionGryffindor() { cargarAlumnosPorCasa(TipoBaseDatos.GRYFFINDOR); }

    /**
     * Carga los alumnos de la casa Slytherin.
     *
     * @author Marco
     */
    @FXML
    void actionSlytherin() { cargarAlumnosPorCasa(TipoBaseDatos.SLYTHERIN); }

    /**
     * Carga los alumnos de la casa Ravenclaw.
     *
     * @author Marco
     */
    @FXML
    void actionRavenclaw() { cargarAlumnosPorCasa(TipoBaseDatos.RAVENCLAW); }

    /**
     * Carga los alumnos de la casa Hufflepuff.
     *
     * @author Marco
     */
    @FXML
    void actionHufflepuff() { cargarAlumnosPorCasa(TipoBaseDatos.HUFFLEPUFF); }

    /**
     * Carga todos los alumnos de Hogwarts (base de datos principal).
     *
     * @author Marco
     */
    @FXML
    void actionHogwarts() { cargarAlumnosPorCasa(TipoBaseDatos.MARIADB); }

    /**
     * Evento para detectar clic en un elemento de la tabla.
     * Actualmente sin implementación.
     *
     * @author Marco
     */
    @FXML void clickElementoTabla() {}

    /**
     * Evento disparado al realizar una acción de búsqueda.
     * Actualmente sin implementación.
     *
     * @author Marco
     */
    @FXML void actionBusqueda() {}

    /**
     * Cambia entre el modo claro y oscuro de la aplicación.
     * Intercambia las clases CSS "modo-claro" y "modo-oscuro" en el rootPane
     * y cambia el icono del botón entre luna (modo claro) y sol (modo oscuro).
     * También actualiza la clase de casa para usar los colores correspondientes al tema.
     *
     * @author Arantxa
     */
    @FXML
    void actionCambiarTema() {
        modoOscuroActivo = !modoOscuroActivo;

        if (modoOscuroActivo) {
            // Activar modo oscuro
            rootPane.getStyleClass().remove("modo-claro");
            rootPane.getStyleClass().add("modo-oscuro");
            iconoTema.setIconLiteral("fas-sun");
        } else {
            // Activar modo claro
            rootPane.getStyleClass().remove("modo-oscuro");
            rootPane.getStyleClass().add("modo-claro");
            iconoTema.setIconLiteral("fas-moon");
        }

        // Actualizar la clase de casa para aplicar los colores del tema actual
        actualizarEstilosCasa();

        logger.info("Tema cambiado a: {}", modoOscuroActivo ? "Modo Oscuro" : "Modo Claro");
    }
}