package com.devops.puissance4.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class MainGui extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        System.out.println("Application démarrée");
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/devops/puissance4/login-view.fxml")
        );
        Parent root = loader.load();

        Scene scene = new Scene(root, 620, 800);
        stage.setTitle("Puissance 4 - Authentification");
        stage.setScene(scene);
        stage.show();
        System.out.println("Fenêtre affichée");
    }

    public static void main(String[] args) {
        launch(args);
    }
}