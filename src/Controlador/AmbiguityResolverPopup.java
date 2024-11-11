/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AmbiguityResolverPopup {

    private String selectedTable;

    public String display(String columnName, String table1, String table2) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Seleccione la Tabla para " + columnName);

        Label label = new Label("¿A cuál tabla pertenece el campo \"" + columnName + "\"?");

        ToggleGroup group = new ToggleGroup();

        RadioButton option1 = new RadioButton(table1);
        option1.setToggleGroup(group);
        option1.setOnAction(e -> selectedTable = table1);

        RadioButton option2 = new RadioButton(table2);
        option2.setToggleGroup(group);
        option2.setOnAction(e -> selectedTable = table2);

        Button confirmButton = new Button("Confirmar");
        confirmButton.setOnAction(e -> window.close());

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, option1, option2, confirmButton);

        Scene scene = new Scene(layout, 300, 200);
        window.setScene(scene);
        window.showAndWait();

        return selectedTable;
    }
}
