package com.devops.puissance4.server;
import com.devops.puissance4.common.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Server {
    private int port;
    private List<ConnectedClient> clients;
    private int currentTurn = 1;
    private final Set<Integer> rematchReadyPlayers = new HashSet<>();

    public Server(int port) throws IOException {
        this.port = port;
        this.clients = new ArrayList<>();

        new Thread(new Connection(this)).start();
    }

    public int getPort() { return port; }

    public synchronized void broadcastMessage(Message mess, int id) {
        for (ConnectedClient client : clients) {
            if (client.getId() != id) {
                client.sendMessage(mess);
            }
        }
    }

    public synchronized boolean addClient(ConnectedClient newClient) {
        if (clients.size() >= 2) {
            newClient.sendMessage(new Message("Serveur", "STATE:FULL"));
            newClient.closeClient();
            return false;
        }

        clients.add(newClient);
        int playerNum = clients.size();
        newClient.setPlayerNumber(playerNum);

        if (clients.size() == 1) {
            newClient.sendMessage(new Message("Serveur", "STATE:WAITING"));
        } else {
            sendReadyState();
        }

        return true;
    }

    private void sendReadyState() {
        if (clients.size() < 2) {
            return;
        }

        ConnectedClient p1 = clients.get(0);
        ConnectedClient p2 = clients.get(1);
        p1.setPlayerNumber(1);
        p2.setPlayerNumber(2);

        p1.sendMessage(new Message("Serveur", "STATE:READY:1"));
        p2.sendMessage(new Message("Serveur", "STATE:READY:2"));

        currentTurn = 1;
        rematchReadyPlayers.clear();
        broadcastMessage(new Message("Serveur", "TURN:1"), -1);
    }

    public synchronized void handleMove(ConnectedClient sender, int col) {
        if (clients.size() < 2) {
            return;
        }

        if (sender.getPlayerNumber() != currentTurn) {
            sender.sendMessage(new Message("Serveur", "ERROR:NOT_YOUR_TURN"));
            return;
        }

        broadcastMessage(new Message("Serveur", "MOVE:" + col), sender.getId());

        currentTurn = (currentTurn == 1) ? 2 : 1;
        broadcastMessage(new Message("Serveur", "TURN:" + currentTurn), -1);
    }

    public synchronized void handleRematchReady(ConnectedClient sender) {
        if (clients.size() < 2) {
            return;
        }

        rematchReadyPlayers.add(sender.getPlayerNumber());
        broadcastMessage(new Message("Serveur", "REMATCH:READY_COUNT:" + rematchReadyPlayers.size()), -1);

        if (rematchReadyPlayers.size() == 2) {
            rematchReadyPlayers.clear();
            currentTurn = 1;
            broadcastMessage(new Message("Serveur", "REMATCH:START"), -1);
            broadcastMessage(new Message("Serveur", "TURN:1"), -1);
        }
    }

    public synchronized void handleRematchMenu() {
        rematchReadyPlayers.clear();
        broadcastMessage(new Message("Serveur", "REMATCH:MENU"), -1);
    }

    public synchronized void disconnectedClient(ConnectedClient discClient) {
        discClient.closeClient();
        clients.remove(discClient);
        rematchReadyPlayers.clear();

        if (clients.size() == 1) {
            ConnectedClient remaining = clients.get(0);
            remaining.setPlayerNumber(1);
            remaining.sendMessage(new Message("Serveur", "STATE:OPPONENT_LEFT"));
            remaining.sendMessage(new Message("Serveur", "STATE:WAITING"));
        }
    }
}