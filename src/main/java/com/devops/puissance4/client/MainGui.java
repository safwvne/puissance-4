package com.devops.puissance4.client;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainGui extends Application {

    @Override
    public void start(Stage stage) {
        Group root = new Group();

        ClientPanel clientPanel = new ClientPanel();
        root.getChildren().add(clientPanel);

        //Setup btn click

        Scene scene = new Scene(root, 620, 800);
        stage.setTitle("Puissance 4");
        stage.setScene(scene);
        stage.show();

        // Arguments: host port
        String host = "127.0.0.1";
        int port = 5000;

        if (getParameters().getRaw().size() >= 2) {
            host = getParameters().getRaw().get(0);
            port = Integer.parseInt(getParameters().getRaw().get(1));
        }

        Client client = new Client(host, port);

        clientPanel.setClient(client);
        client.setView(clientPanel);
    }

    public static void main(String[] args) {
        launch(args);
    }
}