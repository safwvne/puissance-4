package com.devops.puissance4.server;
import java.io.IOException;

public class MainServer {
    public static void main(String[] args) {
        try {
            int port = 6000;
            if (args.length >= 1) {
                try {
                    port = Integer.parseInt(args[0]);
                } catch (NumberFormatException ignored) {
                    // garde le port par defaut
                }
            }
            new Server(port);
        } catch (IOException e) {
            System.err.println("Impossible de demarrer le serveur: " + e.getMessage());
        }
    }
}