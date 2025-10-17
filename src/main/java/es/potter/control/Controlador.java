package es.potter.control;

import es.potter.model.Alumno;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Controlador {

    // Mapa para almacenar el estado de los checkboxes de cada alumno
    private Map<Alumno, CheckBox> checkBoxMap = new HashMap<>();

    @FXML
    private Button btnArchivo;

    @FXML
    private Button btnAyuda;

    @FXML
    private Button btnCerrar;

    @FXML
    private Button btnEditar;

    @FXML
    private Button btnEliminar;

    @FXML
    private Button btnGryffindor;

    @FXML
    private Button btnHogwarts;

    @FXML
    private Button btnHufflepuff;

    @FXML
    private Button btnNuevo;

    @FXML
    private Button btnRavenclaw;

    @FXML
    private Button btnRecargar;

    @FXML
    private Button btnSlytherin;

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
    public void initialize() {
        // Configurar las columnas de datos con PropertyValueFactory
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        colCurso.setCellValueFactory(new PropertyValueFactory<>("curso"));
        colCasa.setCellValueFactory(new PropertyValueFactory<>("casa"));
        colPatronus.setCellValueFactory(new PropertyValueFactory<>("patronus"));

        // Crear el checkbox "Seleccionar todos" en el header
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
                            // Guardar referencia del checkbox para este alumno
                            checkBoxMap.put(alumno, checkBox);
                            setGraphic(checkBox);
                        }
                    }
                };
            }
        });

        // Configurar el evento del checkbox "Seleccionar todos"
        seleccionarTodosCheckBox.setOnAction(event -> {
            boolean seleccionado = seleccionarTodosCheckBox.isSelected();
            // Marcar o desmarcar todos los checkboxes
            for (CheckBox cb : checkBoxMap.values()) {
                cb.setSelected(seleccionado);
            }
        });

        // Datos de prueba (puedes eliminar esto después)
        Alumno alumno1 = new Alumno("Harry", "Potter", 5, "Gryffindor", "Ciervo");
        alumno1.setId("GRY00001");

        Alumno alumno2 = new Alumno("Draco", "Malfoy", 5, "Slytherin", "Ninguno");
        alumno2.setId("SLY00001");

        Alumno alumno3 = new Alumno("Luna", "Lovegood", 4, "Ravenclaw", "Liebre");
        alumno3.setId("RAV00001");

        tablaAlumnos.getItems().addAll(alumno1, alumno2, alumno3);
    }

    @FXML
    void actionArchivo(ActionEvent event) {

    }

    @FXML
    void actionAyuda(ActionEvent event) {

    }

    @FXML
    void actionCerrar(ActionEvent event) {

    }

    @FXML
    void actionEditar(ActionEvent event) {

    }

    @FXML
    void actionEliminar(ActionEvent event) {
        // Obtener los alumnos seleccionados
        List<Alumno> alumnosSeleccionados = obtenerAlumnosSeleccionados();

        if (alumnosSeleccionados.isEmpty()) {
            System.out.println("No hay alumnos seleccionados");
            return;
        }

        // Aquí puedes agregar un diálogo de confirmación si lo deseas
        System.out.println("Eliminando " + alumnosSeleccionados.size() + " alumno(s)");

        // Eliminar los alumnos seleccionados de la tabla
        tablaAlumnos.getItems().removeAll(alumnosSeleccionados);

        // Limpiar el mapa de checkboxes para los alumnos eliminados
        for (Alumno alumno : alumnosSeleccionados) {
            checkBoxMap.remove(alumno);
        }
    }

    @FXML
    void actionGryffindor(ActionEvent event) {

    }

    @FXML
    void actionHogwarts(ActionEvent event) {

    }

    @FXML
    void actionHufflepuff(ActionEvent event) {

    }

    @FXML
    void actionNuevo(ActionEvent event) {

    }

    @FXML
    void actionRavenclaw(ActionEvent event) {

    }

    @FXML
    void actionRecargar(ActionEvent event) {

    }

    @FXML
    void actionSlytherin(ActionEvent event) {

    }

    @FXML
    void clickElementoTabla(MouseEvent event) {

    }

    /**
     * Método helper para obtener la lista de alumnos que tienen su checkbox marcado
     * @return Lista de alumnos seleccionados
     */
    private List<Alumno> obtenerAlumnosSeleccionados() {
        List<Alumno> seleccionados = new ArrayList<>();

        // Recorrer el mapa de checkboxes
        for (Map.Entry<Alumno, CheckBox> entry : checkBoxMap.entrySet()) {
            if (entry.getValue().isSelected()) {
                seleccionados.add(entry.getKey());
            }
        }

        return seleccionados;
    }

}
