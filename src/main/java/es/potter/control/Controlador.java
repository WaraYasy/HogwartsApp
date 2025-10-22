package es.potter.control;

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

public class Controlador {

    private ObservableList<Alumno> listaAlumnos = FXCollections.observableArrayList();
    private Map<Alumno, CheckBox> checkBoxMap = new HashMap<>();

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

    private FilteredList<Alumno> filteredList;
    private ContextMenu menuArchivo;
    private ContextMenu menuAyuda;

    @FXML
    public void initialize() {
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

        // Datos de ejemplo
        Alumno alumno1 = new Alumno("Harry", "Potter", 5, "Gryffindor", "Ciervo");
        alumno1.setId("GRY00001");
        Alumno alumno2 = new Alumno("Draco", "Malfoy", 5, "Slytherin", "Ninguno");
        alumno2.setId("SLY00001");
        Alumno alumno3 = new Alumno("Luna", "Lovegood", 4, "Ravenclaw", "Liebre");
        alumno3.setId("RAV00001");

        listaAlumnos.addAll(alumno1, alumno2, alumno3);

        filteredList = new FilteredList<>(listaAlumnos, p -> true);
        tablaAlumnos.setItems(filteredList);

        txtBusqueda.textProperty().addListener((obs, oldValue, newValue) -> filtrarTabla(newValue));
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

    @FXML
    void actionEliminar(ActionEvent event) {
        List<Alumno> alumnosSeleccionados = obtenerAlumnosSeleccionados();

        if (alumnosSeleccionados.isEmpty()) {
            System.out.println("No hay alumnos seleccionados");
            return;
        }

        listaAlumnos.removeAll(alumnosSeleccionados);
        for (Alumno alumno : alumnosSeleccionados) checkBoxMap.remove(alumno);

        for (CheckBox cb : checkBoxMap.values()) cb.setSelected(false);
        if (checkBox.getGraphic() instanceof CheckBox seleccionarTodosCheckBox)
            seleccionarTodosCheckBox.setSelected(false);
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

    @FXML void actionRecargar(ActionEvent e) {}

    @FXML
    void actionArchivo(ActionEvent e) {
        if (menuArchivo == null) {
            menuArchivo = new ContextMenu();
            MenuItem cerrarItem = new MenuItem("Cerrar pestaña");
            cerrarItem.setOnAction(event -> actionCerrar(event));
            menuArchivo.getItems().add(cerrarItem);
        }

        if (menuArchivo.isShowing()) {
            menuArchivo.hide();
        } else {
            menuArchivo.show(btnArchivo,
                    btnArchivo.localToScreen(0, btnArchivo.getHeight()).getX(),
                    btnArchivo.localToScreen(0, btnArchivo.getHeight()).getY());
        }
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
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(
                    "es.potter.resourcebundle.mensajes", Locale.getDefault()
            );

            Alumno alumnoSeleccionado = tablaAlumnos.getSelectionModel().getSelectedItem();
            if (alumnoSeleccionado == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle(bundle.getString("sinSeleccion"));
                alert.setHeaderText(bundle.getString("mensajeSeleccion"));
                alert.setContentText(bundle.getString("seleccionaAlumno"));
                alert.showAndWait();
                return;
            }

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

        } catch (IOException ex) {
            ex.printStackTrace();
            ResourceBundle bundle = ResourceBundle.getBundle(
                    "es.potter.resourcebundle.mensajes", Locale.getDefault()
            );
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
            ResourceBundle bundle = ResourceBundle.getBundle(
                    "es.potter.resourcebundle.mensajes", Locale.getDefault()
            );

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/es/potter/fxml/modalAniadir.fxml"),
                    bundle
            );
            Parent root = loader.load();

            Stage modalStage = new Stage();
            modalStage.setTitle(bundle.getString("nuevoAlumno"));
            modalStage.setScene(new Scene(root));
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setResizable(false);
            modalStage.showAndWait();

        } catch (IOException ex) {
            ex.printStackTrace();
            ResourceBundle bundle = ResourceBundle.getBundle(
                    "es.potter.resourcebundle.mensajes", Locale.getDefault()
            );
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
        listaAlumnos.clear();
        ServicioHogwarts.cargarAlumnosDesde(tipoBase)
                .thenAccept(alumnos -> Platform.runLater(() -> listaAlumnos.setAll(alumnos)))
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    Platform.runLater(() -> {
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
