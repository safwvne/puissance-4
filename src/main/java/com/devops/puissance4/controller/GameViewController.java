package com.devops.puissance4.controller;

import com.devops.puissance4.client.Client;
import com.devops.puissance4.client.ClientPanel;
import com.devops.puissance4.model.Player;
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

            String host = "127.0.0.1";
            int port = 5000;

            Client client = new Client(host, port);
            clientPanel.setClient(client);
            client.setView(clientPanel);

            Player currentPlayer = Session.getCurrentPlayer();
            if (currentPlayer != null) {
                client.authenticate(currentPlayer.getUsername());
            }

        } catch (Exception e) {
            rootPane.setCenter(new Label("Erreur lors du chargement du jeu : " + e.getMessage()));
            e.printStackTrace();
        }
    }
}