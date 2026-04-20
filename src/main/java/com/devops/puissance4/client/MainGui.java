package com.devops.puissance4.client;

import javafx.application.Application;
<<<<<<< HEAD
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
=======
import javafx.scene.Group;
import javafx.scene.Scene;
>>>>>>> origin/gameplay
import javafx.stage.Stage;

public class MainGui extends Application {

    @Override
<<<<<<< HEAD
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
=======
    public void start(Stage stage) {
        Group root = new Group();

        ClientPanel clientPanel = new ClientPanel();
        root.getChildren().add(clientPanel);

        Scene scene = new Scene(root, 620, 800);
        stage.setTitle("Puissance 4");
        stage.setScene(scene);
        stage.show();

        String host = "127.0.0.1";
        int port = 5000;

        if (getParameters().getRaw().size() >= 2) {
            host = getParameters().getRaw().get(0);
            port = Integer.parseInt(getParameters().getRaw().get(1));
        }

        Client client = new Client(host, port);

        clientPanel.setClient(client);
        client.setView(clientPanel);
>>>>>>> origin/gameplay
    }

    public static void main(String[] args) {
        launch(args);
    }
}