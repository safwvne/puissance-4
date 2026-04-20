package com.devops.puissance4.client;

import java.io.IOException;

public class MainClient {
    public static void main(String[] args) {
        try {
            String host = "127.0.0.1";
            int port = 6000;

            if (args.length >= 1 && !args[0].isBlank()) {
                host = args[0];
            }
            if (args.length >= 2) {
                try {
                    port = Integer.parseInt(args[1]);
                } catch (NumberFormatException ignored) {
                    // garde le port par defaut
                }
            }

            Client client = new Client(host, port);
            client.connect();
        } catch (IOException e) {
            System.out.println("Erreur : Impossible de se connecter au serveur.");
        }
    }
}