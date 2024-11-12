/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Controlador;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import Controlador.Consulta;

import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.scene.text.Text;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;


public class ConsultasController implements Initializable {

    private Connection conn;
    private String selectedDatabase;
    private String selectedTable1;
    private String selectedTable2;
    private Consulta consulta;
    private boolean cond1 = false;
    private boolean cond2 = false;

    //2 tablas
    @FXML
    private Text txtRelacionesTablas; // Texto para "Relaciones de Tablas"
    @FXML
    private ComboBox<String> comboRelacionesTabla1; // ComboBox para la primera tabla en relaciones
    @FXML
    private ComboBox<String> comboRelacionesTabla2; // ComboBox para la segunda tabla en relaciones
    @FXML
    private Text txtIgualRelaciones; // Texto para el símbolo de igualdad o relación entre tablas
    @FXML
    private Text txtCondicion1; // Texto para la primera condición
    @FXML
    private Text txtCondicion2; // Texto para la segunda condición
    @FXML
    private RadioButton btnTabla1Condicion1; // RadioButton para seleccionar Tabla 1 en la Condición 1
    @FXML
    private RadioButton btnTabla1Condicion2; // RadioButton para seleccionar Tabla 1 en la Condición 2
    @FXML
    private RadioButton btnTabla2Condicion1; // RadioButton para seleccionar Tabla 2 en la Condición 1
    @FXML
    private RadioButton btnTabla2Condicion2; // RadioButton para seleccionar Tabla 2 en la Condición 2

    
    @FXML
    private ComboBox<String> comboCampo1;
    @FXML
    private ComboBox<String> comboCampo2;
    @FXML
    private ComboBox<String> comboOperador1;
    @FXML
    private ComboBox<String> comboOperador2;
    @FXML
    private ComboBox<String> comboOperador3;

    @FXML
    private TextField textInput1;
    @FXML
    private TextField textInput2;

    


    
    @FXML
    private TableView<ObservableList<String>> tablaResultados;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeOperatorCombos();
        initializeLogicalOperatorCombo();
        initializeTextInputs();
        initializeListeners();

        // Establecer valores predeterminados para los radio buttons
        btnTabla1Condicion1.setSelected(true);
        btnTabla1Condicion2.setSelected(true);

        // Agregar listeners para los radio buttons para asegurar la actualización
        ToggleGroup groupCondicion1 = new ToggleGroup();
        btnTabla1Condicion1.setToggleGroup(groupCondicion1);
        btnTabla2Condicion1.setToggleGroup(groupCondicion1);

        ToggleGroup groupCondicion2 = new ToggleGroup();
        btnTabla1Condicion2.setToggleGroup(groupCondicion2);
        btnTabla2Condicion2.setToggleGroup(groupCondicion2);

        // Listener para Condición 1
        groupCondicion1.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                System.out.println("Condición 1 seleccionada: " + ((RadioButton) newValue).getText());
            }
        });

        // Listener para Condición 2
        groupCondicion2.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                System.out.println("Condición 2 seleccionada: " + ((RadioButton) newValue).getText());
            }
        });
    }
    

    // Si solo se selecciona 1 tabla
    public void initializeData(Connection conn, String database, String table) {
        this.conn = conn;
        this.selectedDatabase = database;
        this.selectedTable1 = table;
        this.selectedTable2 = null; // No hay segunda tabla

        setDatabase();

        // Cargar los campos de la única tabla seleccionada en los ComboBox de Condiciones
        loadTableColumns(selectedTable1, comboCampo1);
        loadTableColumns(selectedTable1, comboCampo2);

        // Deshabilitar y ocultar componentes relacionados con la segunda tabla
        txtRelacionesTablas.setDisable(true);
        txtRelacionesTablas.setVisible(false);

        comboRelacionesTabla1.setDisable(true);
        comboRelacionesTabla1.setVisible(false);

        comboRelacionesTabla2.setDisable(true);
        comboRelacionesTabla2.setVisible(false);

        txtIgualRelaciones.setDisable(true);
        txtIgualRelaciones.setVisible(false);

        btnTabla2Condicion1.setDisable(true);
        btnTabla2Condicion1.setVisible(false);

        btnTabla2Condicion2.setDisable(true);
        btnTabla2Condicion2.setVisible(false);

        // Asegurar que las condiciones apunten a la primera tabla y estén habilitadas
        btnTabla1Condicion1.setSelected(true);
        btnTabla1Condicion1.setDisable(false);
        btnTabla1Condicion1.setVisible(false);

        btnTabla1Condicion2.setSelected(true);
        btnTabla1Condicion2.setDisable(false);
        btnTabla1Condicion2.setVisible(false);

        // Si tienes campos de texto específicos para condiciones, asegúrate de que estén habilitados
        txtCondicion1.setDisable(false);
        txtCondicion1.setVisible(false);

        txtCondicion2.setDisable(false);
        txtCondicion2.setVisible(false);

        // Inicializar la instancia de Consulta para una sola tabla
        this.consulta = new Consulta(conn, selectedTable1);

        // Inicializar listeners después de configurar los datos
        initializeListeners();
    }



    public void initializeData(Connection conn, String database, String table1, String table2) {
        this.conn = conn;
        this.selectedDatabase = database;
        this.selectedTable1 = table1;
        this.selectedTable2 = table2;

        setDatabase();
        loadTableColumns(table1, comboRelacionesTabla1);
        if (table2 != null) {
            loadTableColumns(table2, comboRelacionesTabla2);
        }

        loadTableColumns(table1, comboCampo1);
        if (table2 != null) {
            loadTableColumns(table2, comboCampo2);
        }

        consulta = new Consulta(conn, selectedTable1);

        // Asegurarse de que los componentes de relaciones estén habilitados y visibles
        txtRelacionesTablas.setDisable(false);
        txtRelacionesTablas.setVisible(true);

        comboRelacionesTabla1.setDisable(false);
        comboRelacionesTabla1.setVisible(true);

        comboRelacionesTabla2.setDisable(false);
        comboRelacionesTabla2.setVisible(true);

        txtIgualRelaciones.setDisable(false);
        txtIgualRelaciones.setVisible(true);

        btnTabla2Condicion1.setDisable(false);
        btnTabla2Condicion1.setVisible(true);

        btnTabla2Condicion2.setDisable(false);
        btnTabla2Condicion2.setVisible(true);

        // Inicializar listeners después de configurar los datos
        initializeListeners();
    }


    private void setDatabase() {
        try {
            conn.createStatement().execute("USE " + selectedDatabase);
        } catch (SQLException ex) {
            System.out.println("Error al cambiar la base de datos: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void initializeListeners() {
        // Listeners para los operadores que son independientes de la segunda tabla
        comboOperador1.valueProperty().addListener((observable, oldValue, newValue) -> {
            handleOperatorChange(newValue, textInput1);
            ejecutarConsulta();
        });

        comboOperador2.valueProperty().addListener((observable, oldValue, newValue) -> {
            handleOperatorChange(newValue, textInput2);
            ejecutarConsulta();
        });

        // Inicializar listeners para relaciones solo si hay dos tablas seleccionadas
        if (selectedTable2 != null) {
            initializeListenersRelaciones();
        }

        // Listeners para los RadioButtons de selección de tabla para Condición 1
        ChangeListener<Boolean> tablaCondicion1Listener = (observable, oldValue, newValue) -> {
            if (newValue) { // Solo reaccionar cuando el RadioButton está seleccionado
                if (btnTabla1Condicion1.isSelected()) {
                    loadTableColumns(selectedTable1, comboCampo1);
                } else if (btnTabla2Condicion1.isSelected()) {
                    loadTableColumns(selectedTable2, comboCampo1);
                }
                ejecutarConsulta();
            }
        };

        // Listeners para los RadioButtons de selección de tabla para Condición 2
        ChangeListener<Boolean> tablaCondicion2Listener = (observable, oldValue, newValue) -> {
            if (newValue) {
                if (btnTabla1Condicion2.isSelected()) {
                    loadTableColumns(selectedTable1, comboCampo2);
                } else if (btnTabla2Condicion2.isSelected()) {
                    loadTableColumns(selectedTable2, comboCampo2);
                }
                ejecutarConsulta();
            }
        };

        // Asignar los listeners a los RadioButtons correspondientes
        btnTabla1Condicion1.selectedProperty().addListener(tablaCondicion1Listener);
        btnTabla2Condicion1.selectedProperty().addListener(tablaCondicion1Listener);
        btnTabla1Condicion2.selectedProperty().addListener(tablaCondicion2Listener);
        btnTabla2Condicion2.selectedProperty().addListener(tablaCondicion2Listener);

        // Otros listeners existentes (ejecutar consulta en vivo, etc.)
        setupLiveUpdateListeners();
    }

    private void handleOperatorChange(String newValue, TextField inputField) {
        // Lógica especial para operadores "Esta vacio" y "No esta vacio"
        if (newValue.equals("Esta vacio") || newValue.equals("No esta vacio")) {
            inputField.clear();
            inputField.setDisable(true);
        } else {
            inputField.setDisable(false);
        }
    }

    private void setupLiveUpdateListeners() {
        ChangeListener<Object> liveUpdateListener = (observable, oldValue, newValue) -> ejecutarConsulta();
        comboCampo1.valueProperty().addListener(liveUpdateListener);
        comboCampo2.valueProperty().addListener(liveUpdateListener);
        comboOperador1.valueProperty().addListener(liveUpdateListener);
        comboOperador2.valueProperty().addListener(liveUpdateListener);
        comboOperador3.valueProperty().addListener(liveUpdateListener);

        textInput1.textProperty().addListener(liveUpdateListener);
        textInput2.textProperty().addListener(liveUpdateListener);
    }


    private void initializeListenersRelaciones(){
        // Listener para comboRelacionesTabla1
        comboRelacionesTabla1.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals("Campo")) {
                // Actualizar comboCampo1 según la tabla seleccionada en los RadioButtons
                if (btnTabla1Condicion1.isSelected()) {
                    loadTableColumns(selectedTable1, comboCampo1);
                } else if (btnTabla2Condicion1.isSelected()) {
                    loadTableColumns(selectedTable2, comboCampo1);
                }
                ejecutarConsulta();
            }
        });

        // Listener para comboRelacionesTabla2
        comboRelacionesTabla2.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals("Campo")) {
                // Actualizar comboCampo2 según la tabla seleccionada en los RadioButtons
                if (btnTabla1Condicion2.isSelected()) {
                    loadTableColumns(selectedTable1, comboCampo2);
                } else if (btnTabla2Condicion2.isSelected()) {
                    loadTableColumns(selectedTable2, comboCampo2);
                }
                ejecutarConsulta();
            }
        });
    }

    @FXML
// Variable para controlar la alerta
private boolean alertShown = false;

private void ejecutarConsulta() {
    if (consulta == null) {
        System.err.println("La instancia de 'consulta' no está inicializada.");
        return;
    }

    if (selectedTable2 != null && !selectedTable2.isEmpty()) {
        String relacion1 = comboRelacionesTabla1.getValue();
        String relacion2 = comboRelacionesTabla2.getValue();

        boolean relacionesDefinidas = relacion1 != null && !relacion1.equals("Campo")
                                      && relacion2 != null && !relacion2.equals("Campo");

        if (!relacionesDefinidas) {
            if (!alertShown) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error de Relaciones");
                alert.setHeaderText(null);
                alert.setContentText("Debe escoger una relación entre las tablas antes de ejecutar la consulta.");
                alert.showAndWait();
                alertShown = true; // Marcar que la alerta se ha mostrado
            }
            return;
        } else {
            alertShown = false; // Resetear el indicador si las relaciones están definidas
        }
        alertShown = false;
        consulta.clear();
        consulta.addJoin("JOIN " + selectedTable2 + " ON " + selectedTable1 + "." + relacion1 + " = " + selectedTable2 + "." + relacion2);
    } else {
        consulta.clear();
    }

    // Agregar condiciones
    if (verifyConditions()) {
        agregarCondicionesYConsultar();
    }
}

    private boolean verifyConditions() {
        cond1 = (comboCampo1.getValue() != null && !comboCampo1.getValue().equals("Campo"))
                && (comboOperador1.getValue() != null && !comboOperador1.getValue().equals("Operador"))
                && (comboOperador1.getValue().equals("Esta vacio") || comboOperador1.getValue().equals("No esta vacio")
                    || (textInput1.getText() != null && !textInput1.getText().isEmpty()));

        cond2 = (comboCampo2.getValue() != null && !comboCampo2.getValue().equals("Campo"))
                && (comboOperador2.getValue() != null && !comboOperador2.getValue().equals("Operador"))
                && (comboOperador2.getValue().equals("Esta vacio") || comboOperador2.getValue().equals("No esta vacio")
                    || (textInput2.getText() != null && !textInput2.getText().isEmpty()));

        return cond1 || cond2;
    }

    private void agregarCondicionesYConsultar() {
        if (cond1) {
            agregarFiltro(comboCampo1.getValue(), comboOperador1.getValue(), textInput1.getText());
        }
        if (cond2) {
            agregarFiltro(comboCampo2.getValue(), comboOperador2.getValue(), textInput2.getText(), comboOperador3.getValue());
        }

        ResultSet rs = consulta.execute();
        mostrarResultados(rs);
    }




    private void agregarFiltro(String campo, String operador, String valor) {
        agregarFiltro(campo, operador, valor, null);
    }
    
    
    private void agregarFiltro(String campo, String operador, String valor, String operadorLogico) {
    System.out.println("=== Inicio de agregarFiltro ===");
    System.out.println("Campo recibido: " + campo);
    System.out.println("Operador recibido: " + operador);
    System.out.println("Valor recibido: " + valor);
    System.out.println("Operador lógico recibido: " + operadorLogico);

    if (operador.equals("Esta vacio") || operador.equals("No esta vacio")) {
        valor = ""; // No se utiliza un valor en estos operadores
        if (campo.equals(comboCampo1.getValue())) {
            textInput1.clear();
            textInput1.setDisable(true);
        } else if (campo.equals(comboCampo2.getValue())) {
            textInput2.clear();
            textInput2.setDisable(true);
        }
    } else {
        // Reactivar el campo de texto si el operador cambia a otro valor
        if (campo.equals(comboCampo1.getValue())) {
            textInput1.setDisable(false);
        } else if (campo.equals(comboCampo2.getValue())) {
            textInput2.setDisable(false);
        }
    }

    if (campo != null && !campo.isEmpty() && operador != null && !operador.isEmpty()) {
        boolean isAmbiguous = checkAmbiguity(campo);
        if (isAmbiguous) {
            if (campo.equals(comboCampo1.getValue())) {
                if (btnTabla1Condicion1.isSelected()) {
                    campo = selectedTable1 + "." + campo;
                } else if (btnTabla2Condicion1.isSelected()) {
                    campo = selectedTable2 + "." + campo;
                }
            } else if (campo.equals(comboCampo2.getValue())) {
                if (btnTabla1Condicion2.isSelected()) {
                    campo = selectedTable1 + "." + campo;
                } else if (btnTabla2Condicion2.isSelected()) {
                    campo = selectedTable2 + "." + campo;
                }
            }
        }

        // Construcción del filtro
        String filtro = "";
        if (operador.equals("Esta vacio") || operador.equals("No esta vacio")) {
            filtro = campo + " " + getSQLOperator(operador);
        } else if (operador.equals("Empieza por") || operador.equals("Termina por") || operador.equals("Contiene")) {
            valor = switch (operador) {
                case "Empieza por" -> valor + "%";
                case "Termina por" -> "%" + valor;
                case "Contiene" -> "%" + valor + "%";
                default -> valor;
            };
            filtro = campo + " " + getSQLOperator(operador) + " '" + valor + "'";
        } else {
            filtro = campo + " " + getSQLOperator(operador) + " '" + valor + "'";
        }

        System.out.println("Filtro final: " + filtro);

        if (!consulta.getFilters().isEmpty() && operadorLogico != null && !operadorLogico.isEmpty()) {
            filtro = operadorLogico + " " + filtro;
        }

        consulta.addFilter(filtro);
        System.out.println("Consulta en construcción: " + consulta.buildQuery());
    }
    System.out.println("=== Fin de agregarFiltro ===");
}




    // Método auxiliar para verificar si un campo es ambiguo
    private boolean checkAmbiguity(String campo) {
    // Verificar si el campo existe en ambas tablas (usar nombres de columnas)
    return (comboCampo1.getItems().contains(campo) && comboCampo2.getItems().contains(campo));
}



    private void mostrarResultados(ResultSet rs) {
        tablaResultados.getColumns().clear();
        tablaResultados.getItems().clear();

        if (rs == null) {
            System.out.println(consulta.getError());
            return;
        }

        try {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                final int colIndex = i - 1;
                TableColumn<ObservableList<String>, String> column = new TableColumn<>(metaData.getColumnName(i));
                column.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().get(colIndex)));
                tablaResultados.getColumns().add(column);
            }

            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(rs.getString(i));
                }
                tablaResultados.getItems().add(row);
            }

        } catch (SQLException ex) {
            System.out.println("Error al mostrar los resultados: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private String getSQLOperator(String operador) {
        return switch (operador) {
            case "Menor que" -> "<";
            case "Mayor que" -> ">";
            case "Menor o igual a" -> "<=";
            case "Mayor o igual a" -> ">=";
            case "Empieza por", "Contiene", "Termina por" -> "LIKE";
            case "Esta vacio" -> "IS NULL";
            case "No esta vacio" -> "IS NOT NULL";
            default -> "=";
        };
    }

    private void initializeOperatorCombos() {
        ObservableList<String> operadores = FXCollections.observableArrayList(
            "Operador", "Menor que", "Mayor que", "Menor o igual a", "Mayor o igual a",
            "Empieza por", "Contiene", "Termina por", "Esta vacio", "No esta vacio"
        );
        comboOperador1.setItems(operadores);
        comboOperador2.setItems(operadores);
        comboOperador1.getSelectionModel().selectFirst();
        comboOperador2.getSelectionModel().selectFirst();
    }

    private void initializeLogicalOperatorCombo() {
        ObservableList<String> logicalOperators = FXCollections.observableArrayList("AND", "OR");
        comboOperador3.setItems(logicalOperators);
        comboOperador3.getSelectionModel().selectFirst();
    }

    private void initializeTextInputs() {
        textInput1.setText("");
        textInput2.setText("");
    }

    private void loadTableColumns(String tableName, ComboBox<String> comboBox) {
        ObservableList<String> columns = FXCollections.observableArrayList("Campo");
        try {
            ResultSet rs = conn.createStatement().executeQuery("SHOW COLUMNS FROM " + tableName);
            while (rs.next()) {
                columns.add(rs.getString(1));
            }
            comboBox.setItems(columns);
        } catch (SQLException ex) {
            System.out.println("Error al cargar columnas de la tabla " + tableName + ": " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
