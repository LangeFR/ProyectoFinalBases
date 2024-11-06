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

public class Visual_TablasController implements Initializable {

    @FXML
    private Button btn_Regresar;
    @FXML
    private TableView<ObservableList<String>> tabla1_view; 
    @FXML
    private TableView<ObservableList<String>> tabla2_view; 

    private Connection conn;

    public void initialize(URL url, ResourceBundle rb) {
        // Las configuraciones de las tablas se realizan dinámicamente en loadTableData
    }

    public void initializeWithTables(String dbName, String tableName1, String tableName2) {
        connectDatabase(dbName);
        if (tableName1 != null) {
            loadTableData(tableName1, tabla1_view);
        }
        if (tableName2 != null) {
            loadTableData(tableName2, tabla2_view);
        }
    }

    private void connectDatabase(String dbName) {
        try {
            String jdbcUrl = "jdbc:mysql://localhost:3306/" + dbName + "?useSSL=false";
            String username = "root";
            String password = "12345";
            conn = DriverManager.getConnection(jdbcUrl, username, password);
        } catch (SQLException ex) {
            System.out.println("Error connecting to database: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void loadTableData(String tableName, TableView<ObservableList<String>> tableView) {
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Configuración dinámica de las columnas en base a los metadatos
            tableView.getColumns().clear();
            for (int i = 1; i <= columnCount; i++) {
                final int j = i;
                TableColumn<ObservableList<String>, String> column = new TableColumn<>(metaData.getColumnName(i));
                column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(j - 1)));
                tableView.getColumns().add(column);
            }

            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(rs.getString(i));
                }
                data.add(row);
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
}
