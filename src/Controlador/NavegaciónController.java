package Controlador;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;


public class NavegaciónController implements Initializable {

    @FXML 
    private ListView<String> list_tablas;
    @FXML 
    private ListView<String> list_tablas2;
    @FXML 
    private ListView<String> list_base;
    @FXML 
    private Button btn_Estructuras;
    @FXML 
    private RadioButton btnTabla1;
    @FXML 
    private RadioButton btnTabla2;
    @FXML 
    private Button btn_Consultar;
    private ToggleGroup tableSelectionGroup; // Grupo de toggles para los radio buttons
    private Connection conn = null; // Conexión a la base de datos
    @FXML 
    private Button btn_verTablas;
    
    private String contrasena;
    private String username;
    private String jdbcUrl;
    
    public void login(String usuario, String contrasena, String url) {
        //Recibe los datos para conectar correctamente con mysql
        this.contrasena = contrasena;
        this.username = usuario;
        this.jdbcUrl = url;
         connectDatabase();
    }

    // Método que se llama automáticamente al cargar el controlador
    public void initialize(URL url, ResourceBundle rb) {
         // Establece la conexión con la base de datos
         // Carga las bases de datos disponibles y las muestra
        setupRadioButtons(); // Configura los botones de radio
        setupDatabaseSelectionListener(); // Establece los listeners para la selección de base de datos
        initializeListViews(); // Inicializa las vistas de listas
    }

    // Establece la conexión con la base de datos
    private void connectDatabase() {    
        try {
            this.conn = DriverManager.getConnection(jdbcUrl, username, contrasena);
            System.out.println("Conexión establecida");
            loadDatabases();
        } catch (SQLException ex) {
            System.out.println("Error connecting to database: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // Carga las bases de datos disponibles y las muestra en la lista
    private void loadDatabases() {
    // Verifica si existe una conexión activa a la base de datos.
    if (conn != null) {
        // Inicia un bloque try para manejar posibles excepciones SQL.
        try {
            // Crea una lista observable que se actualizará automáticamente en la interfaz de usuario.
            ObservableList<String> databases = FXCollections.observableArrayList();
            // Ejecuta una consulta SQL que pide al servidor de bases de datos listar todas las bases de datos disponibles.
            ResultSet rs = conn.createStatement().executeQuery("SHOW DATABASES");
            // Itera sobre cada fila del resultado de la consulta.
            while (rs.next()) {
                // Añade el nombre de cada base de datos (ubicado en la primera columna del resultado) a la lista observable.
                databases.add(rs.getString(1));
            }
            // Establece los elementos de la lista observable como los elementos que mostrará list_base en la interfaz gráfica.
            list_base.setItems(databases);
            // Imprime un mensaje en la consola indicando que las bases de datos se cargaron correctamente.
            System.out.println("Bases cargadas exitosamente");
        // Bloque catch que captura y maneja posibles excepciones de SQL.
        } catch (SQLException ex) {
            // Imprime un mensaje de error en la consola si ocurre una excepción.
            System.out.println("Error al cargar las bases");
            // Imprime el rastreo de pila para ayudar en la depuración del error.
            ex.printStackTrace();
        }
    }
    }

    // Configura los botones de radio y sus comportamientos
    private void setupRadioButtons() {
    // Crea un nuevo grupo de botones de opción (ToggleGroup) para que sólo un botón de radio pueda ser seleccionado a la vez.
    tableSelectionGroup = new ToggleGroup();
    
    // Asigna los botones al tableselectiongroup
    btnTabla1.setToggleGroup(tableSelectionGroup);
    btnTabla2.setToggleGroup(tableSelectionGroup);
    
    // Añade listener de cambio de propiedad a 'selectedToggleProperty' del grupo de botones de opción.
    tableSelectionGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
        // Verifica si hay un nuevo botón de radio seleccionado.
        if (newToggle != null) {
            // Si hay un botón de radio seleccionado, ejecuta la función 'doTabla' para manejar la lógica de selección de tablas.
            doTabla();
        } else {
            // Si no hay un botón de radio seleccionado, limpia las listas de tablas y las desactiva.
            list_tablas.setItems(FXCollections.observableArrayList());
            list_tablas2.setItems(FXCollections.observableArrayList());
            list_tablas.setDisable(true); // Deshabilita la vista de lista 
            list_tablas2.setDisable(true); 
        }
    });
}


    // Gestiona la carga de tablas basada en la selección del modo de tabla
    private void doTabla() {
    // Obtiene el botón de radio actualmente seleccionado del grupo de botones.
    RadioButton selected = (RadioButton) tableSelectionGroup.getSelectedToggle();
    
    // Obtiene la base de datos seleccionada en la lista de bases de datos.
    String selectedDatabase = list_base.getSelectionModel().getSelectedItem();
    
    // Verifica si se ha seleccionado tanto una base de datos como un modo de tabla; si no, muestra un mensaje y sale del método.
    if (selectedDatabase == null || selected == null) {
        System.out.println("Seleccione una base de datos y un modo de tabla primero.");
        return;
    }
    
    // Comprueba si el botón seleccionado es 'btnTabla1' (modo de selección de una sola tabla).
    if (selected == btnTabla1) {
        // Establece el modo de selección de la lista 'list_tablas' para permitir solo un elemento seleccionado a la vez.
        list_tablas.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        
        // Habilita 'list_tablas' para que se pueda interactuar con ella.
        list_tablas.setDisable(false);
        
        // Deshabilita 'list_tablas2' y limpia cualquier selección que pueda haber.
        list_tablas2.setDisable(true);
        list_tablas2.getSelectionModel().clearSelection();
        
        // Carga las tablas de la base de datos seleccionada en 'list_tablas'.
        loadTables(selectedDatabase, list_tablas);
    } 
    // Comprueba si el botón seleccionado es 'btnTabla2' (modo de selección de dos tablas).
    else if (selected == btnTabla2) {
        // Establece el modo de selección de ambas listas de tablas para permitir solo una selección en cada una.
        list_tablas.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        list_tablas2.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        
        // Habilita ambas listas de tablas para que se pueda interactuar con ellas.
        list_tablas.setDisable(false);
        list_tablas2.setDisable(false);
        
        // Carga las tablas de la base de datos seleccionada en ambas listas de tablas.
        loadTables(selectedDatabase, list_tablas, list_tablas2);
        }
    }


    // Carga las tablas de la base de datos seleccionada en las vistas de lista
private void loadTables(String database, ListView<String>... lists) {
    // Verifica si existe una conexión de base de datos activa antes de intentar cargar las tablas.
    if (conn != null) {
        // Bloque try-catch para manejar posibles errores SQL.
        try {
            // Crea una lista observable para almacenar los nombres de las tablas.
            ObservableList<String> tables = FXCollections.observableArrayList();
            // Crea un objeto Statement para enviar comandos SQL a la base de datos.
            Statement stmt = conn.createStatement();
            // Ejecuta el comando SQL para cambiar la base de datos activa a la seleccionada.
            stmt.execute("USE " + database);
            // Ejecuta el comando SQL para obtener todas las tablas de la base de datos activa y almacena el resultado en un ResultSet.
            ResultSet rs = stmt.executeQuery("SHOW TABLES");
            // Itera sobre cada fila del ResultSet para obtener los nombres de las tablas.
            while (rs.next()) {
                // Añade el nombre de la tabla (en la primera columna del resultado) a la lista observable.
                tables.add(rs.getString(1));
            }
            // Itera sobre cada ListView proporcionada en el método.
            for (ListView<String> list : lists) {
                // Asigna la lista observable de nombres de tablas al ListView para mostrarlos en la interfaz.
                list.setItems(tables);
                // Habilita el ListView para que sea interactivo y pueda ser utilizado por el usuario.
                list.setDisable(false);
            }
            // Imprime un mensaje en la consola indicando que las tablas han sido cargadas correctamente.
            System.out.println("Tablas cargadas para la base de datos: " + database);
            
        // Bloque catch para manejar excepciones SQL.
        } catch (SQLException ex) {
            // Imprime un mensaje de error en la consola si ocurre un error al cargar las tablas.
            System.out.println("Error al cargar tablas para la base de datos: " + ex.getMessage());
            // Imprime el rastreo de la pila de errores para ayudar en la depuración.
            ex.printStackTrace();
        }
    }
}


    // Inicializa las listas de vistas deshabilitándolas inicialmente
    private void initializeListViews() {
        list_tablas.setDisable(true);
        list_tablas2.setDisable(true);
    }

    // Establece un listener para la selección de la base de datos
    private void setupDatabaseSelectionListener() {
        list_base.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                System.out.println("Base de datos seleccionada: " + newSelection);
                doTabla();
            }
        });
    }

    // Maneja el evento de clic en el botón Consultar, cargando la vista correspondiente
    @FXML
    private void doConsultar(ActionEvent event) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Vista/Consultas.fxml"));
        Parent root = loader.load();
        ConsultasController consultasController = loader.getController();

        String selectedDatabase = list_base.getSelectionModel().getSelectedItem();
        String selectedTable1 = list_tablas.getSelectionModel().getSelectedItem();
        String selectedTable2 = list_tablas2.isDisabled() ? null : list_tablas2.getSelectionModel().getSelectedItem();

        // Verificar si solo hay una tabla seleccionada
        if (selectedTable2 == null) {
            // Llamar a initializeData para una sola tabla
            consultasController.initializeData(conn, selectedDatabase, selectedTable1);
        } else {
            // Llamar a initializeData para dos tablas
            consultasController.initializeData(conn, selectedDatabase, selectedTable1, selectedTable2);
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    } catch (IOException e) {
        System.out.println("Error al cargar la vista de Consultas: " + e.getMessage());
        e.printStackTrace();
    }
}


    // Maneja el evento de clic en el botón Estructuras, cargando la vista de estructuras
    @FXML
    private void doEstructuras(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Vista/Visual_Estructuras.fxml"));
            Parent root = loader.load();

            // Obtener el controlador de la pantalla de visualización de estructuras
            Visual_EstructurasController estructurasController = loader.getController();

            // Pasar la base de datos y las tablas seleccionadas al controlador
            estructurasController.initializeWithTables(
                list_base.getSelectionModel().getSelectedItem(),
                list_tablas.getSelectionModel().getSelectedItem(),
                list_tablas2.isDisabled() ? null : list_tablas2.getSelectionModel().getSelectedItem()
            );

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println("Error al cargar la vista de Visual Estructuras: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Maneja el evento de clic en el botón Ver Tablas, cargando la vista de tablas
    @FXML
    private void doVerTablas(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Vista/Visual_Tablas.fxml"));
            Parent root = loader.load();
            Visual_TablasController tablasController = loader.getController();

            // Pasar la base de datos y las tablas seleccionadas al controlador de tablas
            String database = list_base.getSelectionModel().getSelectedItem();
            String table1 = list_tablas.getSelectionModel().getSelectedItem();
            String table2 = list_tablas2.getSelectionModel().getSelectedItem();
            tablasController.initializeWithTables(database, table1, table2);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println("Error al cargar la vista de Visual Tablas: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
