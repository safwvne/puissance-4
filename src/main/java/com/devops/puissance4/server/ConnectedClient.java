package com.devops.puissance4.server;

import com.devops.puissance4.common.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ConnectedClient implements Runnable {
    private static int idCounter = 0;
<<<<<<< HEAD

    private int id;
    private int playerNumber;
    private String username;
=======
    private int id;
    private int playerNumber;
>>>>>>> origin/gameplay
    private Server server;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public ConnectedClient(Server server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        this.id = idCounter++;
        this.out = new ObjectOutputStream(socket.getOutputStream());
    }

    public int getId() { return id; }
    public int getPlayerNumber() { return playerNumber; }
    public void setPlayerNumber(int n) { this.playerNumber = n; }

<<<<<<< HEAD
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

=======
>>>>>>> origin/gameplay
    public void sendMessage(Message mess) {
        try {
            out.writeObject(mess);
            out.flush();
<<<<<<< HEAD
        } catch (IOException e) {
            e.printStackTrace();
        }
=======
        } catch (IOException e) { e.printStackTrace(); }
>>>>>>> origin/gameplay
    }

    public void run() {
        try {
            in = new ObjectInputStream(socket.getInputStream());
            while (true) {
                Message mess = (Message) in.readObject();
                if (mess != null) {
                    String content = mess.getContent();
<<<<<<< HEAD

                    if (content != null && content.startsWith("AUTH:")) {
                        String receivedUsername = content.substring("AUTH:".length()).trim();
                        if (!receivedUsername.isBlank()) {
                            this.username = receivedUsername;
                            System.out.println("Client " + id + " authentifié en tant que " + username);
                        }

                    } else if (content != null && content.startsWith("MOVE:")) {
                        try {
                            int col = Integer.parseInt(content.split(":")[1].trim());
                            server.handleMove(this, col);
                        } catch (NumberFormatException ignored) {
                        }

                    } else {
                        String displayName = (username != null && !username.isBlank())
                                ? username
                                : "Joueur " + playerNumber;

                        mess.setSender(displayName);
=======
                    if (content != null && content.startsWith("MOVE:")) {
                        try {
                            int col = Integer.parseInt(content.split(":")[1].trim());
                            server.handleMove(this, col);
                        } catch (NumberFormatException ignored) {}
                    } else if ("REMATCH:READY".equals(content)) {
                        server.handleRematchReady(this);
                    } else if ("REMATCH:MENU".equals(content)) {
                        server.handleRematchMenu();
                    } else {
                        mess.setSender("Joueur " + playerNumber);
>>>>>>> origin/gameplay
                        server.broadcastMessage(mess, id);
                    }
                }
            }
        } catch (Exception e) {
            server.disconnectedClient(this);
        }
    }

    public void closeClient() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            socket.close();
<<<<<<< HEAD
        } catch (IOException e) {
            e.printStackTrace();
        }
=======
        } catch (IOException e) { e.printStackTrace(); }
>>>>>>> origin/gameplay
    }
}