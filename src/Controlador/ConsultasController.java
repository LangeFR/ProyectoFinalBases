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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class ConsultasController implements Initializable {

    // Variables de instancia para almacenar los datos inicializados
    private Connection conn;          // Conexión a la base de datos
    private String selectedDatabase;  // Base de datos seleccionada
    private String selectedTable1;    // Primera tabla seleccionada
    private String selectedTable2;    // Segunda tabla seleccionada (opcional)

    // Referencias a los ComboBox de la interfaz
    @FXML
    private ComboBox<String> comboRelacionesTabla1; // Campos de la primera tabla
    @FXML
    private ComboBox<String> comboRelacionesTabla2; // Campos de la segunda tabla
    @FXML
    private ComboBox<String> comboCampo1;
    @FXML
    private ComboBox<String> comboCampo2;
    @FXML
    private ComboBox<String> comboOperador1;
    @FXML
    private ComboBox<String> comboOperador2;
    @FXML
    private ComboBox<String> comboOperador3; // AND / OR para combinar condiciones

    // Referencias a los campos de texto para ingresar valores de consulta
    @FXML
    private TextField textInput1;
    @FXML
    private TextField textInput2;

    // Referencia a la tabla de resultados
    @FXML
    private TableView<ObservableList<String>> tablaResultados;

    // Método de inicialización
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeOperatorCombos();
        initializeLogicalOperatorCombo();
        initializeTextInputs();
        initializeListeners(); // Añadir listeners para la actualización en vivo
    }

    // Método personalizado para inicializar los datos (que llamaremos desde NavegacionController)
    public void initializeData(Connection conn, String database, String table1, String table2) {
        this.conn = conn;
        this.selectedDatabase = database;
        this.selectedTable1 = table1;
        this.selectedTable2 = table2;

        // Cambiar la base de datos activa
        setDatabase();

        // Cargar los campos de cada tabla en los desplegables de relaciones
        loadTableColumns(table1, comboRelacionesTabla1); // Campos para la relación de la primera tabla
        if (table2 != null) {
            loadTableColumns(table2, comboRelacionesTabla2); // Campos para la relación de la segunda tabla
        }

        // Cargar los campos de cada tabla en los desplegables de consultas
        loadTableColumns(table1, comboCampo1);
        if (table2 != null) {
            loadTableColumns(table2, comboCampo2);
        }
    }

    // Cambia la base de datos activa
    private void setDatabase() {
        try {
            conn.createStatement().execute("USE " + selectedDatabase);
        } catch (SQLException ex) {
            System.out.println("Error al cambiar la base de datos: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // Inicializa listeners para los ComboBox y TextField para actualizar en vivo
    private void initializeListeners() {
        ChangeListener<Object> liveUpdateListener = (observable, oldValue, newValue) -> {
            ejecutarConsulta();
        };

        //comboRelacionesTabla1.valueProperty().addListener(liveUpdateListener);
        //comboRelacionesTabla2.valueProperty().addListener(liveUpdateListener);
        comboCampo1.valueProperty().addListener(liveUpdateListener);
        comboCampo2.valueProperty().addListener(liveUpdateListener);
        comboOperador1.valueProperty().addListener(liveUpdateListener);
        comboOperador2.valueProperty().addListener(liveUpdateListener);
        comboOperador3.valueProperty().addListener(liveUpdateListener);

        textInput1.textProperty().addListener(liveUpdateListener);
        textInput2.textProperty().addListener(liveUpdateListener);
    }

    // Ejecuta la consulta y muestra los resultados en la tabla
    @FXML
    private void ejecutarConsulta() {
        // Construir la consulta SQL basada en la entrada del usuario
        String sql = construirConsultaSQL();
        System.out.println("SQL Generado: " + sql); // Imprimir la consulta en la consola

        // Limpiar la tabla de resultados
        tablaResultados.getColumns().clear();
        tablaResultados.getItems().clear();

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            // Obtener metadatos de la consulta para las columnas
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Crear las columnas dinámicamente en la tabla
            for (int i = 1; i <= columnCount; i++) {
                final int colIndex = i - 1;
                TableColumn<ObservableList<String>, String> column = new TableColumn<>(metaData.getColumnName(i));
                column.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().get(colIndex)));
                tablaResultados.getColumns().add(column);
            }

            // Cargar los datos en la tabla
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(rs.getString(i));
                }
                tablaResultados.getItems().add(row);
            }

        } catch (SQLException ex) {
            System.out.println("Error al ejecutar la consulta: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

// Construye la consulta SQL basada en los valores de la interfaz
private String construirConsultaSQL() {
    // Asegurarse de que al menos una tabla esté seleccionada
    if (selectedTable1 == null || selectedTable1.isEmpty()) {
        System.out.println("Error: No se ha seleccionado la tabla principal.");
        return ""; // Retornar una cadena vacía para evitar ejecutar una consulta incompleta
    }

    StringBuilder sql = new StringBuilder("SELECT * FROM " + selectedTable1);

    // Validar y construir JOIN si hay una segunda tabla seleccionada
    if (selectedTable2 != null && !selectedTable2.isEmpty()) {
        String relacion1 = comboRelacionesTabla1.getValue();
        String relacion2 = comboRelacionesTabla2.getValue();

        if (relacion1 != null && !relacion1.isEmpty() && relacion2 != null && !relacion2.isEmpty()) {
            sql.append(" JOIN ").append(selectedTable2)
               .append(" ON ").append(selectedTable1).append(".").append(relacion1)
               .append(" = ").append(selectedTable2).append(".").append(relacion2);
        } else {
            System.out.println("Error: Las relaciones entre tablas no están completas.");
            return ""; // Retornar una cadena vacía para evitar ejecutar una consulta incompleta
        }
    }

    // Validar los campos de consulta
    String campo1 = comboCampo1.getValue();
    String operador1 = getSQLOperator(comboOperador1.getValue());
    String valor1 = textInput1.getText();

    String campo2 = comboCampo2.getValue();
    String operador2 = getSQLOperator(comboOperador2.getValue());
    String valor2 = textInput2.getText();

    String operadorLogico = comboOperador3.getValue();

    StringBuilder whereClause = new StringBuilder();

    // Construir la primera condición si está completa
    if (campo1 != null && !campo1.isEmpty() && operador1 != null && !operador1.isEmpty() && valor1 != null && !valor1.isEmpty()) {
        // Prefijar la columna con el nombre de la tabla si hay un JOIN
        if (selectedTable2 != null && !selectedTable2.isEmpty()) {
            campo1 = selectedTable1 + "." + campo1;
        }

        if (operador1.equals("LIKE")) {
            valor1 = (comboOperador1.getValue().equals("Empieza por") ? valor1 + "%" :
                     comboOperador1.getValue().equals("Termina por") ? "%" + valor1 :
                     "%" + valor1 + "%");
        }
        whereClause.append(campo1).append(" ").append(operador1).append(" '").append(valor1).append("'");
    }

    // Construir la segunda condición si está completa
    if (campo2 != null && !campo2.isEmpty() && operador2 != null && !operador2.isEmpty() && valor2 != null && !valor2.isEmpty()) {
        // Prefijar la columna con el nombre de la tabla si hay un JOIN
        if (selectedTable2 != null && !selectedTable2.isEmpty()) {
            campo2 = selectedTable2 + "." + campo2;
        }

        if (whereClause.length() > 0 && operadorLogico != null && !operadorLogico.isEmpty()) {
            whereClause.append(" ").append(operadorLogico).append(" ");
        }

        if (operador2.equals("LIKE")) {
            valor2 = (comboOperador2.getValue().equals("Empieza por") ? valor2 + "%" :
                     comboOperador2.getValue().equals("Termina por") ? "%" + valor2 :
                     "%" + valor2 + "%");
        }
        whereClause.append(campo2).append(" ").append(operador2).append(" '").append(valor2).append("'");
    }

    // Agregar la cláusula WHERE a la consulta si hay condiciones
    if (whereClause.length() > 0) {
        sql.append(" WHERE ").append(whereClause);
    }

    // Validación final para verificar si se construyó una consulta válida
    String finalSQL = sql.toString();
    if (finalSQL.trim().isEmpty()) {
        System.out.println("Error: No hay condiciones de consulta válidas.");
        return ""; // Retornar cadena vacía si no hay condiciones válidas
    }

    System.out.println("SQL Generado: " + finalSQL);
    return finalSQL;
}




    // Convierte el operador seleccionado en su equivalente SQL
    private String getSQLOperator(String operador) {
        switch (operador) {
            case "Menor que":
                return "<";
            case "Mayor que":
                return ">";
            case "Menor o igual a":
                return "<=";
            case "Mayor o igual a":
                return ">=";
            case "Empieza por":
                return "LIKE"; // Se usa "LIKE" con valor + "%"
            case "Contiene":
                return "LIKE"; // Se usa "%" + valor + "%"
            case "Termina por":
                return "LIKE"; // Se usa "%" + valor
            case "Está vacío":
                return "IS NULL";
            case "No está vacío":
                return "IS NOT NULL";
            default:
                return "="; // Igualdad por defecto
        }
    }

    // Inicializa los ComboBox de operadores con las opciones solicitadas
    private void initializeOperatorCombos() {
        ObservableList<String> operadores = FXCollections.observableArrayList(
            "Operador",
            "Menor que",
            "Mayor que",
            "Menor o igual a",
            "Mayor o igual a",
            "Empieza por",
            "Contiene",
            "Termina por",
            "Está vacío",
            "No está vacío"
        );

        // Establece los operadores en ambos ComboBox de operadores
        comboOperador1.setItems(operadores);
        comboOperador2.setItems(operadores);

        // Selecciona por defecto el primer elemento ("Operador")
        comboOperador1.getSelectionModel().selectFirst();
        comboOperador2.getSelectionModel().selectFirst();
    }

    // Inicializa el ComboBox de operador lógico (AND/OR)
    private void initializeLogicalOperatorCombo() {
        ObservableList<String> logicalOperators = FXCollections.observableArrayList("AND", "OR");
        comboOperador3.setItems(logicalOperators);
        // Selecciona por defecto "AND"
        comboOperador3.getSelectionModel().selectFirst();
    }

    // Inicializa los campos de texto para los valores de entrada
    private void initializeTextInputs() {
        // Configura los campos de texto en blanco por defecto
        textInput1.setText("");
        textInput2.setText("");
    }
    
    // Carga los nombres de columnas de una tabla en un ComboBox específico
    private void loadTableColumns(String tableName, ComboBox<String> comboBox) {
        ObservableList<String> columns = FXCollections.observableArrayList();
        try {
            // Ejecuta la consulta para obtener los nombres de las columnas de la tabla
            ResultSet rs = conn.createStatement().executeQuery("SHOW COLUMNS FROM " + tableName);
            while (rs.next()) {
                columns.add(rs.getString(1)); // Agrega el nombre de la columna
            }
            comboBox.setItems(columns); // Establece las columnas en el ComboBox
        } catch (SQLException ex) {
            System.out.println("Error al cargar columnas de la tabla " + tableName + ": " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}
