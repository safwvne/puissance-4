package com.devops.puissance4.server;

import com.devops.puissance4.common.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ConnectedClient implements Runnable {
    private static int idCounter = 0;

    private int id;
    private int playerNumber;
    private Long playerId;
    private String username;
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

    public int getId() {
        return id;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public void setPlayerNumber(int n) {
        this.playerNumber = n;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public String getUsername() {
        return username;
    }

    public void setPlayerIdentity(Long playerId, String username) {
        this.playerId = playerId;
        this.username = username;
    }

    public boolean hasLinkedPlayer() {
        return playerId != null;
    }

    public void sendMessage(Message mess) {
        try {
            out.writeObject(mess);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            in = new ObjectInputStream(socket.getInputStream());
            while (true) {
                Message mess = (Message) in.readObject();
                if (mess != null) {
                    String content = mess.getContent();

                    if (content != null && content.startsWith("JOIN_PLAYER:")) {
                        handleJoinPlayer(content);

                    } else if (content != null && content.startsWith("MOVE:")) {
                        try {
                            int col = Integer.parseInt(content.split(":")[1].trim());
                            server.handleMove(this, col);
                        } catch (NumberFormatException ignored) {
                        }

                    } else if ("REMATCH:READY".equals(content)) {
                        server.handleRematchReady(this);

                    } else if ("REMATCH:MENU".equals(content)) {
                        server.handleRematchMenu();

                    } else {
                        mess.setSender("Joueur " + playerNumber);
                        server.broadcastMessage(mess, id);
                    }
                }
            }
        } catch (Exception e) {
            server.disconnectedClient(this);
        }
    }

    private void handleJoinPlayer(String content) {
        try {
            String[] parts = content.split(":", 3);
            if (parts.length < 3) {
                return;
            }

            Long parsedPlayerId = Long.parseLong(parts[1].trim());
            String parsedUsername = parts[2].trim();

            setPlayerIdentity(parsedPlayerId, parsedUsername);
        } catch (Exception ignored) {
        }
    }

    public void closeClient() {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}