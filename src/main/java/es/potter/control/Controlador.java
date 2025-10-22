package es.potter.control;

import es.potter.dao.DaoAlumno;
import es.potter.database.TipoBaseDatos;
import es.potter.model.Alumno;
import es.potter.servicio.ServicioHogwarts;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class Controlador {

    private ObservableList<Alumno> listaAlumnos = FXCollections.observableArrayList();
    private Map<Alumno, CheckBox> checkBoxMap = new HashMap<>();
    private Map<String, String> alumnosEliminados = new HashMap<>();

    /** Tipo de base de datos actual */
    private TipoBaseDatos baseDatosActual = TipoBaseDatos.MARIADB;

    @FXML
    private Button btnArchivo, btnAyuda, btnCerrar, btnEditar, btnEliminar, btnGryffindor,
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
    private ContextMenu menuArchivo;
    private ContextMenu menuAyuda;

    @FXML
    public void initialize() {
        bundle = ResourceBundle.getBundle("es.potter.mensajes", Locale.getDefault());

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        colCurso.setCellValueFactory(new PropertyValueFactory<>("curso"));
        colCasa.setCellValueFactory(new PropertyValueFactory<>("casa"));
        colPatronus.setCellValueFactory(new PropertyValueFactory<>("patronus"));

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

        // Configurar filtrado de tabla
        filteredList = new FilteredList<>(listaAlumnos, p -> true);
        tablaAlumnos.setItems(filteredList);
        txtBusqueda.textProperty().addListener((obs, oldValue, newValue) -> filtrarTabla(newValue));

        // Quitar mensaje "Tabla sin contenido"
        tablaAlumnos.setPlaceholder(new javafx.scene.control.Label(""));

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

        // Guardar los eliminados en el Map temporal
        for (Alumno alumno : alumnosSeleccionados) {
            alumnosEliminados.put(alumno.getId(), alumno.getCasa());
        }

        // Eliminar de la lista y del map de checkboxes
        listaAlumnos.removeAll(alumnosSeleccionados);
        for (Alumno alumno : alumnosSeleccionados) checkBoxMap.remove(alumno);

        // Deseleccionar todos los checkboxes y refrescar la tabla
        checkBoxMap.values().forEach(cb -> cb.setSelected(false));
        tablaAlumnos.refresh(); // <--- forzar refresco visual
        actualizarEstadoBotones();
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
        if (listaAlumnos.isEmpty() && alumnosEliminados.isEmpty()) return;

        List<CompletableFuture<Boolean>> operaciones = new ArrayList<>();

        // Procesar eliminados
        for (Map.Entry<String, String> entry : alumnosEliminados.entrySet()) {
            String id = entry.getKey();
            String casaAlumno = entry.getValue();
            Alumno dummy = new Alumno();
            dummy.setId(id);

            CompletableFuture<Boolean> op = DaoAlumno.eliminarAlumno(dummy, TipoBaseDatos.MARIADB)
                    .thenCompose(ok -> DaoAlumno.eliminarAlumno(dummy, TipoBaseDatos.SQLITE))
                    .thenCompose(ok -> {
                        try {
                            TipoBaseDatos casa = TipoBaseDatos.obtenerTipoBaseDatosPorCasa(casaAlumno);
                            return DaoAlumno.eliminarAlumno(dummy, casa);
                        } catch (IllegalArgumentException e) {
                            return CompletableFuture.completedFuture(true);
                        }
                    });

            operaciones.add(op);
        }
        alumnosEliminados.clear();

        // Procesar alumnos existentes/nuevos
        for (Alumno alumno : listaAlumnos) {
            TipoBaseDatos casa = TipoBaseDatos.obtenerTipoBaseDatosPorCasa(alumno.getCasa());

            CompletableFuture<Boolean> futuraOp = DaoAlumno.modificarAlumno(alumno.getId(), alumno, TipoBaseDatos.MARIADB)
                    .exceptionally(ex -> false)
                    .thenCompose(exito -> {
                        if (!exito) return DaoAlumno.nuevoAlumno(alumno, TipoBaseDatos.MARIADB);
                        return CompletableFuture.completedFuture(true);
                    })
                    .thenCompose(ok -> DaoAlumno.modificarAlumno(alumno.getId(), alumno, casa)
                            .exceptionally(ex -> false)
                            .thenCompose(exitoCasa -> {
                                if (!exitoCasa) return DaoAlumno.nuevoAlumno(alumno, casa);
                                return CompletableFuture.completedFuture(true);
                            }))
                    .thenCompose(ok -> DaoAlumno.modificarAlumno(alumno.getId(), alumno, TipoBaseDatos.SQLITE)
                            .exceptionally(ex -> false)
                            .thenCompose(exitoSqlite -> {
                                if (!exitoSqlite) return DaoAlumno.nuevoAlumno(alumno, TipoBaseDatos.SQLITE);
                                return CompletableFuture.completedFuture(true);
                            }));

            operaciones.add(futuraOp);
        }

        CompletableFuture.allOf(operaciones.toArray(new CompletableFuture[0]))
                .thenRun(() -> Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Recargar");
                    alert.setHeaderText("Sincronización completada");
                    alert.setContentText("Todos los cambios se han guardado en MariaDB, SQLite y las bases de las casas correspondientes.");
                    alert.showAndWait();
                }));
    }

    @FXML
    void actionArchivo(ActionEvent e) {
        if (menuArchivo == null) {
            menuArchivo = new ContextMenu();
            MenuItem cerrarItem = new MenuItem("Cerrar pestaña");
            cerrarItem.setOnAction(event -> actionCerrar(event));
            menuArchivo.getItems().add(cerrarItem);
        }

        if (menuArchivo.isShowing()) menuArchivo.hide();
        else menuArchivo.show(btnArchivo,
                btnArchivo.localToScreen(0, btnArchivo.getHeight()).getX(),
                btnArchivo.localToScreen(0, btnArchivo.getHeight()).getY());
    }

    @FXML
    void actionAyuda(ActionEvent e) {
        if (menuAyuda == null) {
            menuAyuda = new ContextMenu();
            MenuItem sobreMiItem = new MenuItem("Sobre mí");
            sobreMiItem.setOnAction(event -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Sobre mí");
                alert.setHeaderText("Equipo Potter");
                alert.setContentText("""
                        Aplicación desarrollada por el Equipo Potter.
                        
                        • Curso: DM2
                        • Año: 2025
                        """);
                alert.showAndWait();
            });
            menuAyuda.getItems().add(sobreMiItem);
        }

        if (menuAyuda.isShowing()) menuAyuda.hide();
        else menuAyuda.show(btnAyuda,
                btnAyuda.localToScreen(0, btnAyuda.getHeight()).getX(),
                btnAyuda.localToScreen(0, btnAyuda.getHeight()).getY());
    }

    @FXML
    void actionEditar(ActionEvent e) {
        List<Alumno> seleccionados = obtenerAlumnosSeleccionados();
        if (seleccionados.size() != 1) return;

        Alumno alumnoSeleccionado = seleccionados.get(0);

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/es/potter/fxml/modalEditar.fxml"),
                    bundle
            );
            Parent root = loader.load();

            ControladorEditar controladorEditar = loader.getController();
            controladorEditar.setAlumno(alumnoSeleccionado);

            Stage modalStage = new Stage();
            modalStage.setTitle(bundle.getString("editarAlumno"));
            modalStage.setScene(new Scene(root));
            modalStage.initModality(Modality.APPLICATION_MODAL);
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
                    getClass().getResource("/es/potter/fxml/modalAniadir.fxml"),
                    bundle
            );
            Parent root = loader.load();

            ControladorAniadir controladorAniadir = loader.getController();
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
