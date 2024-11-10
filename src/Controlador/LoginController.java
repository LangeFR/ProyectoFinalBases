/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */

package Controlador;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Carlitos
 */
public class LoginController implements Initializable {

    @FXML
    private Button btn_Ingresar;
    @FXML
    private ImageView img_Logo;
    @FXML
    private ImageView img_Divisor;
    @FXML
    private TextField text_Servidor;
    @FXML
    private TextField text_Puerto;
    @FXML
    private TextField text_Usuario;
    @FXML
    private TextField text_Contrasena;
    @FXML
    private String contrasena;

    /**
     * Método inicializador que se ejecuta tras cargar el FXML pero antes de que la ventana sea visible.
     * Aquí se configuran las imágenes de los componentes de la UI.
     */
  
    public void initialize(URL url, ResourceBundle rb) {
        // Establece las imágenes en los ImageView desde los recursos del proyecto.
        img_Logo.setImage(new Image("/vista/logo.png"));
        img_Divisor.setImage(new Image("/vista/divisor.png"));
    }    
    
    /**
     * Método invocado al hacer clic en el botón "Ingresar".
     * conexión con la base de datos usando los datos proporcionados en la interfaz.
     * 
     */
    @FXML
    public String getContrasena(){
        return contrasena;
    }
    
    
    @FXML
    private void DoIngresar(ActionEvent event) {
        // Recoge los valores ingresados por el usuario en los campos de texto.
        String servidor = text_Servidor.getText().trim();
        String puerto = text_Puerto.getText().trim();
        String usuario = text_Usuario.getText().trim();
        contrasena = text_Contrasena.getText().trim();

        // Verifica que todos los campos estén llenos.
        if (servidor.isEmpty() || puerto.isEmpty() || usuario.isEmpty() || contrasena.isEmpty()) {
            // Muestra una alerta si algún campo está vacío.
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Datos incompletos");
            alert.setHeaderText("Todos los campos deben estar llenos");
            alert.setContentText("Por favor, ingresa todos los datos requeridos.");
            alert.showAndWait();
            return;
        }

        // Construye la URL de conexión con el formato adecuado.
        String url = "jdbc:mysql://" + servidor + ":" + puerto + "?useSSL=false";

        try {
            // Intenta establecer la conexión con la base de datos.
            Connection conn = DriverManager.getConnection(url, usuario, contrasena);
            System.out.println("Conexión establecida");
            // Si la conexión es exitosa, cambia a la vista de navegación principal.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Vista/Navegacion.fxml"));
            Parent root = loader.load();
            
            NavegaciónController navegacionController = loader.getController();
            navegacionController.login(usuario, contrasena, url);
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (SQLException e) {
            // Captura errores de conexión y muestra una alerta.
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error de conexión");
            alert.setHeaderText("No se pudo conectar a la base de datos");
            alert.setContentText("Verifica los datos de conexión e inténtalo de nuevo.");
            alert.showAndWait();
            System.out.println("Error al conectar con la base de datos: " + e.getMessage());
        } catch (IOException e) {
            // Captura errores al cargar la nueva vista y muestra una alerta.
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error de interfaz");
            alert.setHeaderText("No se pudo cargar la vista");
            alert.setContentText("Hubo un problema al cargar la vista siguiente.");
            alert.showAndWait();
            System.out.println("Error al cargar la vista: " + e.getMessage());
        }
    }
}
