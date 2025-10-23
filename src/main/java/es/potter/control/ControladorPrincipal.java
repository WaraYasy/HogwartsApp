package es.potter.control;

import es.potter.database.TipoBaseDatos;
import es.potter.model.Alumno;
import es.potter.servicio.ServicioHogwarts;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ControladorPrincipal {

    private ObservableList<Alumno> listaAlumnos = FXCollections.observableArrayList();
    private Map<Alumno, CheckBox> checkBoxMap = new HashMap<>();

    /** Tipo de base de datos actual */
    private TipoBaseDatos baseDatosActual = TipoBaseDatos.MARIADB;

    @FXML
    private Button btnCerrar, btnEditar, btnEliminar, btnGryffindor,
            btnHogwarts, btnHufflepuff, btnNuevo, btnRavenclaw, btnRecargar, btnSlytherin;

    @FXML
    private TableColumn<Alumno, Void> checkBox;

    @FXML
    private TableColumn<Alumno, String> colId;

    @FXML
    private TableColumn<Alumno, String> colNombre;

    @FXML
    private TableColumn<Alumno, String> colApellidos;

    @FXML
    private TableColumn<Alumno, Integer> colCurso;

    @FXML
    private TableColumn<Alumno, String> colCasa;

    @FXML
    private TableColumn<Alumno, String> colPatronus;

    @FXML
    private TableView<Alumno> tablaAlumnos;

    @FXML
    private TextField txtBusqueda;

    @FXML
    private javafx.scene.control.ProgressIndicator progressIndicator;

    private ResourceBundle bundle;
    private FilteredList<Alumno> filteredList;

    @FXML
    public void initialize() {
        bundle = ResourceBundle.getBundle("es.potter.mensajes", Locale.getDefault());

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

        // Configurar filtrado y ordenamiento de tabla
        filteredList = new FilteredList<>(listaAlumnos, p -> true);
        SortedList<Alumno> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(tablaAlumnos.comparatorProperty());
        tablaAlumnos.setItems(sortedList);
        txtBusqueda.textProperty().addListener((obs, oldValue, newValue) -> filtrarTabla(newValue));

        // Quitar mensaje "Tabla sin contenido"
        tablaAlumnos.setPlaceholder(new javafx.scene.control.Label(""));

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

        // Inicializar botones deshabilitados
        btnEditar.setDisable(true);
        btnEliminar.setDisable(true);

        // Cargar datos iniciales desde la base de datos master
        cargarAlumnosPorCasa(baseDatosActual);
    }

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

    private void actualizarEstadoBotones() {
        long seleccionados = checkBoxMap.values().stream().filter(CheckBox::isSelected).count();
        btnEditar.setDisable(seleccionados != 1);
        btnEliminar.setDisable(seleccionados == 0);
    }

    @FXML
    void actionEliminar(ActionEvent event) {
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

        // Mostrar indicador de progreso
        progressIndicator.setVisible(true);

        // Eliminar cada alumno del sistema Master-Slave
        List<CompletableFuture<Boolean>> eliminaciones = new ArrayList<>();
        for (Alumno alumno : alumnosSeleccionados) {
            eliminaciones.add(ServicioHogwarts.eliminarAlumno(alumno));
        }

        // Esperar a que todas las eliminaciones terminen
        CompletableFuture.allOf(eliminaciones.toArray(new CompletableFuture[0]))
            .thenApply(v -> eliminaciones.stream()
                .map(CompletableFuture::join)
                .allMatch(exito -> exito))
            .thenAccept(todasExitosas -> Platform.runLater(() -> {
                progressIndicator.setVisible(false);

                if (todasExitosas) {
                    // Eliminar de la lista local y del map de checkboxes
                    listaAlumnos.removeAll(alumnosSeleccionados);
                    for (Alumno alumno : alumnosSeleccionados) checkBoxMap.remove(alumno);

                    // Deseleccionar todos los checkboxes y refrescar la tabla
                    checkBoxMap.values().forEach(cb -> cb.setSelected(false));
                    tablaAlumnos.refresh();
                    actualizarEstadoBotones();

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Eliminación exitosa");
                    alert.setHeaderText("Alumnos eliminados");
                    alert.setContentText(alumnosSeleccionados.size() + " alumno(s) eliminado(s) correctamente.");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Eliminación parcial");
                    alert.setHeaderText("Algunos alumnos no se pudieron eliminar");
                    alert.setContentText("Revisa los logs para más detalles.");
                    alert.showAndWait();
                }
            }))
            .exceptionally(ex -> {
                Platform.runLater(() -> {
                    progressIndicator.setVisible(false);

                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Error al eliminar alumnos");
                    alert.setContentText("No se pudieron eliminar: " + ex.getMessage());
                    alert.showAndWait();
                });
                return null;
            });
    }

    private List<Alumno> obtenerAlumnosSeleccionados() {
        List<Alumno> seleccionados = new ArrayList<>();
        for (Map.Entry<Alumno, CheckBox> entry : checkBoxMap.entrySet()) {
            if (entry.getValue().isSelected()) {
                seleccionados.add(entry.getKey());
            }
        }
        return seleccionados;
    }

    @FXML
    void actionCerrar(ActionEvent e) {
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

        // Mostrar indicador de progreso
        progressIndicator.setVisible(true);

        // Sincronizar desde Master (reconstruye todas las BDs slave)
        ServicioHogwarts.sincronizarDesdeMaster()
            .thenAccept(exito -> Platform.runLater(() -> {
                progressIndicator.setVisible(false);

                if (exito) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Sincronización");
                    alert.setHeaderText("Sincronización completada");
                    alert.setContentText("Todas las bases de datos han sido sincronizadas exitosamente desde Master.");
                    alert.showAndWait();

                    // Recargar vista actual
                    cargarAlumnosPorCasa(baseDatosActual);
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Sincronización");
                    alert.setHeaderText("Sincronización parcial");
                    alert.setContentText("Algunas bases de datos no pudieron sincronizarse. Revisa los logs.");
                    alert.showAndWait();
                }
            }))
            .exceptionally(ex -> {
                Platform.runLater(() -> {
                    progressIndicator.setVisible(false);

                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Error en sincronización");
                    alert.setContentText("No se pudo sincronizar: " + ex.getMessage());
                    alert.showAndWait();
                });
                return null;
            });
    }

    @FXML
    void actionArchivo(ActionEvent e) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Archivo");
        alert.setHeaderText("Opciones de archivo");
        alert.setContentText("Funcionalidad de archivo disponible aquí.");
        alert.showAndWait();
    }

    @FXML
    void actionAyuda(ActionEvent e) {
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
                ex.printStackTrace();
            }
        });
        content.getChildren().add(link);

        alert.getDialogPane().setContent(content);
        alert.showAndWait();
    }

    @FXML
    void actionEditar(ActionEvent e) {
        List<Alumno> seleccionados = obtenerAlumnosSeleccionados();
        if (seleccionados.size() != 1) return;

        Alumno alumnoSeleccionado = seleccionados.get(0);

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/es/potter/fxml/modalEditarAlumno.fxml"),
                    bundle
            );
            Parent root = loader.load();

            ControladorEditarAlumno controladorEditar = loader.getController();
            controladorEditar.setAlumno(alumnoSeleccionado);

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
            ex.printStackTrace();
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle(bundle.getString("error"));
            error.setHeaderText(bundle.getString("ficheroNoCargado"));
            error.setContentText(bundle.getString("verificaRuta"));
            error.showAndWait();
        }
    }

    @FXML
    void actionNuevo(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/es/potter/fxml/modalNuevoAlumno.fxml"),
                    bundle
            );
            Parent root = loader.load();

            ControladorNuevoAlumno controladorAniadir = loader.getController();
            controladorAniadir.setParentData(
                    listaAlumnos,
                    checkBoxMap,
                    this::actualizarEstadoBotones,
                    () -> tablaAlumnos.refresh()
            );

            Stage modalStage = new Stage();
            modalStage.setTitle(bundle.getString("nuevoAlumno"));
            modalStage.setScene(new Scene(root));
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(btnNuevo.getScene().getWindow());
            modalStage.setResizable(false);
            modalStage.showAndWait();

        } catch (IOException ex) {
            ex.printStackTrace();
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle(bundle.getString("error"));
            error.setHeaderText(bundle.getString("ficheroNoCargado"));
            error.setContentText(bundle.getString("verificaRuta"));
            error.showAndWait();
        }
    }

    @FXML
    void actionGryffindor(ActionEvent e) { cargarAlumnosPorCasa(TipoBaseDatos.GRYFFINDOR); }
    @FXML
    void actionSlytherin(ActionEvent e) { cargarAlumnosPorCasa(TipoBaseDatos.SLYTHERIN); }
    @FXML
    void actionRavenclaw(ActionEvent e) { cargarAlumnosPorCasa(TipoBaseDatos.RAVENCLAW); }
    @FXML
    void actionHufflepuff(ActionEvent e) { cargarAlumnosPorCasa(TipoBaseDatos.HUFFLEPUFF); }
    @FXML
    void actionHogwarts(ActionEvent e) { cargarAlumnosPorCasa(TipoBaseDatos.MARIADB); }

    private void cargarAlumnosPorCasa(TipoBaseDatos tipoBase) {
        baseDatosActual = tipoBase;
        listaAlumnos.clear();

        // Mostrar indicador de progreso
        progressIndicator.setVisible(true);

        ServicioHogwarts.cargarAlumnosDesde(tipoBase)
                .thenAccept(alumnos -> Platform.runLater(() -> {
                    listaAlumnos.setAll(alumnos);
                    // Limpiar checkboxes seleccionados después de cargar
                    checkBoxMap.clear();
                    if (checkBox.getGraphic() instanceof CheckBox seleccionarTodosCheckBox) {
                        seleccionarTodosCheckBox.setSelected(false);
                    }
                    actualizarEstadoBotones();

                    // Ocultar indicador de progreso
                    progressIndicator.setVisible(false);
                }))
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    Platform.runLater(() -> {
                        // Ocultar indicador de progreso en caso de error
                        progressIndicator.setVisible(false);

                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error al cargar alumnos");
                        alert.setHeaderText("No se pudieron cargar los alumnos de " + tipoBase);
                        alert.setContentText(ex.getMessage());
                        alert.showAndWait();
                    });
                    return null;
                });
    }

    @FXML void clickElementoTabla(MouseEvent e) {}
    @FXML void actionBusqueda(ActionEvent e) {}
}
