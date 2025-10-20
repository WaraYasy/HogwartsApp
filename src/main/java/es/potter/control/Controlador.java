package es.potter.control;

import es.potter.model.Alumno;
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

    // Lista observable para la tabla
    private ObservableList<Alumno> listaAlumnos = FXCollections.observableArrayList();

    // Mapa para almacenar el estado de los checkboxes de cada alumno
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

    // 👇 NUEVA COLUMNA
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

    // Filtro para la búsqueda
    private FilteredList<Alumno> filteredList;

    @FXML
    public void initialize() {
        // Configurar columnas
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos")); // 👈 NUEVA LÍNEA
        colCurso.setCellValueFactory(new PropertyValueFactory<>("curso"));
        colCasa.setCellValueFactory(new PropertyValueFactory<>("casa"));
        colPatronus.setCellValueFactory(new PropertyValueFactory<>("patronus"));

        // Crear checkbox de encabezado
        CheckBox seleccionarTodosCheckBox = new CheckBox();
        checkBox.setGraphic(seleccionarTodosCheckBox);

        // Configurar la columna de checkboxes
        checkBox.setCellFactory(new Callback<TableColumn<Alumno, Void>, TableCell<Alumno, Void>>() {
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

        // Evento para "Seleccionar todos"
        seleccionarTodosCheckBox.setOnAction(event -> {
            boolean seleccionado = seleccionarTodosCheckBox.isSelected();
            for (CheckBox cb : checkBoxMap.values()) {
                cb.setSelected(seleccionado);
            }
        });

        // Datos de prueba
        Alumno alumno1 = new Alumno("Harry", "Potter", 5, "Gryffindor", "Ciervo");
        alumno1.setId("GRY00001");
        Alumno alumno2 = new Alumno("Draco", "Malfoy", 5, "Slytherin", "Ninguno");
        alumno2.setId("SLY00001");
        Alumno alumno3 = new Alumno("Luna", "Lovegood", 4, "Ravenclaw", "Liebre");
        alumno3.setId("RAV00001");

        listaAlumnos.addAll(alumno1, alumno2, alumno3);

        // Configurar lista filtrada
        filteredList = new FilteredList<>(listaAlumnos, p -> true);
        tablaAlumnos.setItems(filteredList);

        // Escuchar cambios en el TextField para búsqueda en tiempo real
        txtBusqueda.textProperty().addListener((obs, oldValue, newValue) -> filtrarTabla(newValue));
    }

    /** Filtro de búsqueda */
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

        System.out.println("Eliminando " + alumnosSeleccionados.size() + " alumno(s)");

        listaAlumnos.removeAll(alumnosSeleccionados);

        for (Alumno alumno : alumnosSeleccionados) {
            checkBoxMap.remove(alumno);
        }

        for (CheckBox cb : checkBoxMap.values()) {
            cb.setSelected(false);
        }

        if (checkBox.getGraphic() instanceof CheckBox seleccionarTodosCheckBox) {
            seleccionarTodosCheckBox.setSelected(false);
        }
    }

    /** Obtener alumnos seleccionados */
    private List<Alumno> obtenerAlumnosSeleccionados() {
        List<Alumno> seleccionados = new ArrayList<>();
        for (Map.Entry<Alumno, CheckBox> entry : checkBoxMap.entrySet()) {
            if (entry.getValue().isSelected()) {
                seleccionados.add(entry.getKey());
            }
        }
        return seleccionados;
    }

    //CONFIRMACIÓN AL CERRAR
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

    @FXML void actionArchivo(ActionEvent e) {}
    @FXML void actionAyuda(ActionEvent e) {}
    @FXML void actionEditar(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/es/potter/fxml/modalEditar.fxml"));
            Parent root = loader.load();

            Stage modalStage = new Stage();
            modalStage.setTitle("Editar alumno");
            modalStage.setScene(new Scene(root));
            modalStage.initModality(Modality.APPLICATION_MODAL); // Bloquea la ventana principal
            modalStage.setResizable(false);

            modalStage.showAndWait();

        } catch (IOException ex) {
            ex.printStackTrace();
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Error al abrir ventana");
            error.setHeaderText("No se pudo cargar 'modalEditar.fxml'");
            error.setContentText("Verifica que el archivo está en 'resources/es/potter/fxml/'");
            error.showAndWait();
        }
    }
    @FXML void actionGryffindor(ActionEvent e) {}
    @FXML void actionHogwarts(ActionEvent e) {}
    @FXML void actionHufflepuff(ActionEvent e) {}
    @FXML
    void actionNuevo(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/es/potter/fxml/modalAniadir.fxml"));
            Parent root = loader.load();

            Stage modalStage = new Stage();
            modalStage.setTitle("Añadir nuevo alumno");
            modalStage.setScene(new Scene(root));
            modalStage.initModality(Modality.APPLICATION_MODAL); // Bloquea la ventana principal
            modalStage.setResizable(false);

            modalStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Error al abrir ventana");
            error.setHeaderText("No se pudo cargar 'modalAniadir.fxml'");
            error.setContentText("Verifica que el archivo está en 'resources/es/potter/fxml/'");
            error.showAndWait();
        }
    }


    @FXML void actionRavenclaw(ActionEvent e) {}
    @FXML void actionRecargar(ActionEvent e) {}
    @FXML void actionSlytherin(ActionEvent e) {}
    @FXML void clickElementoTabla(MouseEvent e) {}
    @FXML void actionBusqueda(ActionEvent e) {}
}
