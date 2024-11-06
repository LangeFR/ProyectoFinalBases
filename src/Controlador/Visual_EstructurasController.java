package Controlador;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class Visual_EstructurasController implements Initializable {

    @FXML
    private TableView<ColumnData> tableview_tabla1;
    @FXML
    private TableView<ColumnData> tableview_tabla2;

    private Connection conn;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        connectDatabase();
    }

    private void connectDatabase() {
        try {
            String jdbcUrl = "jdbc:mysql://localhost:3306/?useSSL=false";
            String username = "root";
            String password = "12345";
            conn = DriverManager.getConnection(jdbcUrl, username, password);
        } catch (SQLException ex) {
            System.out.println("Error al conectar a la base");
            ex.printStackTrace();
        }
    }

    public void initializeWithTables(String dbName, String tableName1, String tableName2) {
    if (dbName != null) {
        selectDatabase(dbName); // Se asegura de seleccionar la base de datos correcta
    }
    if (tableName1 != null) {
        setupTableStructure(tableview_tabla1);
        loadTableStructure(tableName1, tableview_tabla1);
    }
    if (tableName2 != null) {
        setupTableStructure(tableview_tabla2);
        loadTableStructure(tableName2, tableview_tabla2);
    }
}

    private void selectDatabase(String dbName) {
        try {
            Statement stmt = conn.createStatement();
            stmt.execute("USE " + dbName); // Cambia la base de datos activa
        } catch (SQLException ex) {
            System.out.println("Error");
            ex.printStackTrace();
        }
    }

    private void setupTableStructure(TableView<ColumnData> tableView) {
        tableView.getColumns().clear();

        TableColumn<ColumnData, String> fieldName = new TableColumn<>("Field");
        fieldName.setCellValueFactory(new PropertyValueFactory<>("fieldName"));

        TableColumn<ColumnData, String> dataType = new TableColumn<>("Type");
        dataType.setCellValueFactory(new PropertyValueFactory<>("dataType"));

        TableColumn<ColumnData, String> nullable = new TableColumn<>("Null");
        nullable.setCellValueFactory(new PropertyValueFactory<>("nullable"));

        TableColumn<ColumnData, String> key = new TableColumn<>("Key");
        key.setCellValueFactory(new PropertyValueFactory<>("key"));

        TableColumn<ColumnData, String> defaultValue = new TableColumn<>("Default");
        defaultValue.setCellValueFactory(new PropertyValueFactory<>("defaultValue"));

        TableColumn<ColumnData, String> extra = new TableColumn<>("Extra");
        extra.setCellValueFactory(new PropertyValueFactory<>("extra"));

        tableView.getColumns().addAll(fieldName, dataType, nullable, key, defaultValue, extra);
    }

    private void loadTableStructure(String tableName, TableView<ColumnData> tableView) {
        ObservableList<ColumnData> data = FXCollections.observableArrayList();
        try {
            String query = "DESCRIBE " + tableName;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                data.add(new ColumnData(
                    rs.getString("Field"),
                    rs.getString("Type"),
                    rs.getString("Null"),
                    rs.getString("Key"),
                    rs.getString("Default"),
                    rs.getString("Extra")
                ));
            }
            tableView.setItems(data);
        } catch (SQLException ex) {
            System.out.println("Error");
            ex.printStackTrace();
        }
    }

    @FXML
    private void doRegresar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Vista/Navegacion.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class ColumnData {
        private final SimpleStringProperty fieldName;
        private final SimpleStringProperty dataType;
        private final SimpleStringProperty nullable;
        private final SimpleStringProperty key;
        private final SimpleStringProperty defaultValue;
        private final SimpleStringProperty extra;

        public ColumnData(String fieldName, String dataType, String nullable, String key, String defaultValue, String extra) {
            this.fieldName = new SimpleStringProperty(fieldName);
            this.dataType = new SimpleStringProperty(dataType);
            this.nullable = new SimpleStringProperty(nullable);
            this.key = new SimpleStringProperty(key);
            this.defaultValue = new SimpleStringProperty(defaultValue);
            this.extra = new SimpleStringProperty(extra);
        }

        public String getFieldName() { return fieldName.get(); }
        public String getDataType() { return dataType.get(); }
        public String getNullable() { return nullable.get(); }
        public String getKey() { return key.get(); }
        public String getDefaultValue() { return defaultValue.get(); }
        public String getExtra() { return extra.get(); }
    }
}
