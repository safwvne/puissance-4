package com.devops.puissance4.controller;

import com.devops.puissance4.client.Client;
import com.devops.puissance4.client.ClientPanel;
import com.devops.puissance4.util.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

public class GameViewController {

    @FXML
    private BorderPane rootPane;

    @FXML
    public void initialize() {
        try {
            ClientPanel clientPanel = new ClientPanel();
            rootPane.setCenter(clientPanel);

            String host = Session.getServerHost();
            int port = Session.getServerPort();

            Client client = new Client(host, port);
            clientPanel.setClient(client);
            client.setView(clientPanel);
        } catch (Exception e) {
            rootPane.setCenter(new Label("Erreur lors du chargement du jeu : " + e.getMessage()));
            e.printStackTrace();
        }
    }
}