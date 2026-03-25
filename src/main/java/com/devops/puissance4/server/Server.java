package com.devops.puissance4.server;
import com.devops.puissance4.common.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private int port;
    private List<ConnectedClient> clients;
    private int currentTurn = 1;

    public Server(int port) throws IOException {
        this.port = port;
        this.clients = new ArrayList<>();

        new Thread(new Connection(this)).start();
    }

    public int getPort() { return port; }
    public int getNumClients() { return clients.size(); }

    public synchronized void broadcastMessage(Message mess, int id) {
        for (ConnectedClient client : clients) {
            if (client.getId() != id) {
                client.sendMessage(mess);
            }
        }
    }

    public synchronized void addClient(ConnectedClient newClient) {
        clients.add(newClient);
        int playerNum = clients.size();
        newClient.setPlayerNumber(playerNum);

        if (clients.size() == 1) {
            newClient.sendMessage(new Message("Serveur", "STATE:WAITING"));
        } else {
            ConnectedClient p1 = clients.get(0);
            ConnectedClient p2 = clients.get(1);

            p1.sendMessage(new Message("Serveur", "STATE:READY:1"));
            p2.sendMessage(new Message("Serveur", "STATE:READY:2"));

            currentTurn = 1;
            broadcastMessage(new Message("Serveur", "TURN:1"), -1);
        }
    }

    public synchronized void handleMove(ConnectedClient sender, int col) {
        //TODO KT Verif tour du joueur
        if (sender.getPlayerNumber() != currentTurn) {
            sender.sendMessage(new Message("Serveur", "ERROR:NOT_YOUR_TURN"));
            return;
        }

        //TODO KT donne l'info
        broadcastMessage(new Message("Serveur", "MOVE:" + col), sender.getId());

        currentTurn = (currentTurn == 1) ? 2 : 1;
        broadcastMessage(new Message("Serveur", "TURN:" + currentTurn), -1);
    }

    public synchronized void disconnectedClient(ConnectedClient discClient) {
        discClient.closeClient();
        clients.remove(discClient);
        broadcastMessage(new Message("Serveur", "Le client " + discClient.getId() + " est parti."), -1);
    }
}