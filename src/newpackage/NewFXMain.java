package newpackage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class NewFXMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Ajusta la ruta a tu archivo FXML de login según la estructura de tu proyecto
        Parent root = FXMLLoader.load(getClass().getResource("/Vista/Login.fxml")); // Asegúrate de que la ruta sea correcta

        Scene scene = new Scene(root);
        
        primaryStage.setTitle("Login"); // Cambia el título según sea necesario
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
