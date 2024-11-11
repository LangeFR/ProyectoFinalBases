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

public class ConsultasController implements Initializable {

    private Connection conn;
    private String selectedDatabase;
    private String selectedTable1;
    private String selectedTable2;
    private Consulta consulta;
    private boolean cond1 = false;
    private boolean cond2 = false;

    @FXML
    private ComboBox<String> comboRelacionesTabla1;
    @FXML
    private ComboBox<String> comboRelacionesTabla2;
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
    private RadioButton btnTabla1Condicion1;
    @FXML
    private RadioButton btnTabla2Condicion1;
    @FXML
    private RadioButton btnTabla1Condicion2;
    @FXML
    private RadioButton btnTabla2Condicion2;

    
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
    // Listener para los RadioButtons de selección de tabla para Condición 1
    ChangeListener<Boolean> tablaCondicion1Listener = (observable, oldValue, newValue) -> {
        if (newValue) { // Solo reaccionar cuando el RadioButton está seleccionado
            if (btnTabla1Condicion1.isSelected()) {
                loadTableColumns(selectedTable1, comboCampo1);
            } else if (btnTabla2Condicion1.isSelected()) {
                loadTableColumns(selectedTable2, comboCampo1);
            }
        }
    };

    // Listener para los RadioButtons de selección de tabla para Condición 2
    ChangeListener<Boolean> tablaCondicion2Listener = (observable, oldValue, newValue) -> {
        if (newValue) {
            if (btnTabla1Condicion2.isSelected()) {
                loadTableColumns(selectedTable1, comboCampo2);
            } else if (btnTabla2Condicion2.isSelected()) {
                loadTableColumns(selectedTable2, comboCampo2);
            }
        }
    };

    // Asignar los listeners a los RadioButtons correspondientes
    btnTabla1Condicion1.selectedProperty().addListener(tablaCondicion1Listener);
    btnTabla2Condicion1.selectedProperty().addListener(tablaCondicion1Listener);
    btnTabla1Condicion2.selectedProperty().addListener(tablaCondicion2Listener);
    btnTabla2Condicion2.selectedProperty().addListener(tablaCondicion2Listener);

    // Otros listeners existentes (ejecutar consulta en vivo, etc.)
    ChangeListener<Object> liveUpdateListener = (observable, oldValue, newValue) -> ejecutarConsulta();
    comboCampo1.valueProperty().addListener(liveUpdateListener);
    comboCampo2.valueProperty().addListener(liveUpdateListener);
    comboOperador1.valueProperty().addListener(liveUpdateListener);
    comboOperador2.valueProperty().addListener(liveUpdateListener);
    comboOperador3.valueProperty().addListener(liveUpdateListener);

    textInput1.textProperty().addListener(liveUpdateListener);
    textInput2.textProperty().addListener(liveUpdateListener);
}

    @FXML
    private void ejecutarConsulta() {
        // Limpiar consulta anterior
        consulta.clear();

        // Configurar JOIN si hay una segunda tabla seleccionada y ambas relaciones tienen un valor
        String relacion1 = comboRelacionesTabla1.getValue();
        String relacion2 = comboRelacionesTabla2.getValue();
        if (selectedTable2 != null && !selectedTable2.isEmpty() 
            && relacion1 != null && !relacion1.isEmpty() 
            && relacion2 != null && !relacion2.isEmpty()) {
            consulta.addJoin("JOIN " + selectedTable2 + " ON " + selectedTable1 + "." + relacion1 + " = " + selectedTable2 + "." + relacion2);
        }

        // Verificar si se debe agregar la primera condición
        cond1 = (comboCampo1.getValue() != null && !comboCampo1.getValue().equals("Campo"))
                && (comboOperador1.getValue() != null && !comboOperador1.getValue().equals("Operador"))
                && (textInput1.getText() != null && !textInput1.getText().isEmpty());

        // Verificar si se debe agregar la segunda condición
        cond2 = (comboCampo2.getValue() != null && !comboCampo2.getValue().equals("Campo"))
                && (comboOperador2.getValue() != null && !comboOperador2.getValue().equals("Operador"))
                && (textInput2.getText() != null && !textInput2.getText().isEmpty());

        // Agregar condiciones si las banderas están activadas
        if (cond1) {
            agregarFiltro(comboCampo1.getValue(), comboOperador1.getValue(), textInput1.getText());
        }
        if (cond2) {
            //agregarFiltro(comboCampo2.getValue(), comboOperador2.getValue(), textInput2.getText(), comboOperador3.getValue());
            agregarFiltro(comboCampo2.getValue(), comboOperador2.getValue(), textInput2.getText());
        }

        // Ejecutar consulta y cargar los resultados
        ResultSet rs = consulta.execute();
        mostrarResultados(rs);
    }


    private void agregarFiltro(String campo, String operador, String valor) {
        agregarFiltro(campo, operador, valor, null);
    }
    
    
    private void agregarFiltro(String campo, String operador, String valor, String operadorLogico) {
    System.out.println("Campo recibido: " + campo);
    System.out.println("Valor de comboCampo1: " + comboCampo1.getValue());
    System.out.println("Valor de comboCampo2: " + comboCampo2.getValue());

    if (campo != null && !campo.isEmpty() && operador != null && !operador.isEmpty()) {
        // Verificar si el campo es ambiguo
        boolean isAmbiguous = checkAmbiguity(campo);
        if (isAmbiguous) {
            System.out.println("Campo ambiguo detectado: " + campo);

            // Determinar la condición (Condición 1 o Condición 2)
            if (campo.equals(comboCampo1.getValue())) { // Condición 1
                System.out.println("Campo corresponde a Condición 1");
                System.out.println("Estado btnTabla1Condicion1 seleccionado: " + btnTabla1Condicion1.isSelected());
                System.out.println("Estado btnTabla2Condicion1 seleccionado: " + btnTabla2Condicion1.isSelected());
                
                if (btnTabla1Condicion1.isSelected()) {
                    campo = selectedTable1 + "." + campo;
                } else if (btnTabla2Condicion1.isSelected()) {
                    campo = selectedTable2 + "." + campo;
                }
            } else if (campo.equals(comboCampo2.getValue())) { // Condición 2
                System.out.println("Campo corresponde a Condición 2");
                System.out.println("Estado btnTabla1Condicion2 seleccionado: " + btnTabla1Condicion2.isSelected());
                System.out.println("Estado btnTabla2Condicion2 seleccionado: " + btnTabla2Condicion2.isSelected());
                
                if (btnTabla1Condicion2.isSelected()) {
                    campo = selectedTable1 + "." + campo;
                } else if (btnTabla2Condicion2.isSelected()) {
                    campo = selectedTable2 + "." + campo;
                }
            }
            
            System.out.println("Campo después de prefijar: " + campo);
        }

        // Procesar valor para el operador LIKE usando switch
        if (operador.equals("Empieza por") || operador.equals("Termina por") || operador.equals("Contiene")) {
            valor = switch (operador) {
                case "Empieza por" -> valor + "%";
                case "Termina por" -> "%" + valor;
                case "Contiene" -> "%" + valor + "%";
                default -> valor;
            };
        }

        String filtro = campo + " " + getSQLOperator(operador) + " '" + valor + "'";

        // Añadir operador lógico solo si ya hay una condición en la consulta
        if (!consulta.getFilters().isEmpty() && operadorLogico != null && !operadorLogico.isEmpty()) {
            filtro = operadorLogico + " " + filtro;
        }

        consulta.addFilter(filtro);

        // Imprimir el estado actual de la consulta después de agregar el filtro
        System.out.println("Consulta en construcción: " + consulta.buildQuery());
    }
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
            case "Está vacío" -> "IS NULL";
            case "No está vacío" -> "IS NOT NULL";
            default -> "=";
        };
    }

    private void initializeOperatorCombos() {
        ObservableList<String> operadores = FXCollections.observableArrayList(
            "Operador", "Menor que", "Mayor que", "Menor o igual a", "Mayor o igual a",
            "Empieza por", "Contiene", "Termina por", "Está vacío", "No está vacío"
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
