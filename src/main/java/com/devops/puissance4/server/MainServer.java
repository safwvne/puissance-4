package com.devops.puissance4.server;
import java.io.IOException;

public class MainServer {
    public static void main(String[] args) {
        try {
            if (args.length != 1) {
<<<<<<< HEAD
                System.out.println("Usage: java com.devops.server.MainServer <port>"); // [cite: 270]
=======
                System.out.println("Usage: java com.devops.server.MainServer <port>");
>>>>>>> origin/gameplay
            } else {
                int port = Integer.parseInt(args[0]);
                new Server(port);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
}