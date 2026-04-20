package com.devops.puissance4.server;

import com.devops.puissance4.common.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Server {
    private static final int ROWS = 6;
    private static final int COLS = 7;

    private final int port;
    private final List<ConnectedClient> clients;
    private final Set<Integer> rematchReadyPlayers = new HashSet<>();

    private int currentTurn = 1;
    private final int[][] grid = new int[ROWS][COLS];
    private boolean gameStarted = false;

    public Server(int port) throws IOException {
        this.port = port;
        this.clients = new ArrayList<>();
        new Thread(new Connection(this)).start();
    }

    public int getPort() {
        return port;
    }

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

        resetBoard();
        gameStarted = true;
        currentTurn = 1;
        rematchReadyPlayers.clear();

        p1.sendMessage(new Message("Serveur", "STATE:READY:1"));
        p2.sendMessage(new Message("Serveur", "STATE:READY:2"));
        broadcastMessage(new Message("Serveur", "TURN:1"), -1);
    }

    public synchronized void handleMove(ConnectedClient sender, int col) {
        if (!gameStarted || clients.size() < 2) {
            return;
        }

        if (sender.getPlayerNumber() != currentTurn) {
            sender.sendMessage(new Message("Serveur", "ERROR:NOT_YOUR_TURN"));
            return;
        }

        if (col < 0 || col >= COLS) {
            sender.sendMessage(new Message("Serveur", "ERROR:INVALID_MOVE"));
            return;
        }

        int row = findAvailableRow(col);
        if (row == -1) {
            sender.sendMessage(new Message("Serveur", "ERROR:COLUMN_FULL"));
            return;
        }

        int player = sender.getPlayerNumber();
        grid[row][col] = player;

        broadcastMessage(new Message("Serveur", "MOVE:" + col), sender.getId());

        if (isWinningMove(row, col, player)) {
            gameStarted = false;
            broadcastMessage(new Message("Serveur", "GAME_OVER:WINNER:" + player), -1);
            return;
        }

        if (isBoardFull()) {
            gameStarted = false;
            broadcastMessage(new Message("Serveur", "GAME_OVER:DRAW"), -1);
            return;
        }

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
            sendReadyState();
            broadcastMessage(new Message("Serveur", "REMATCH:START"), -1);
        }
    }

    public synchronized void handleRematchMenu() {
        rematchReadyPlayers.clear();
        gameStarted = false;
        resetBoard();
        broadcastMessage(new Message("Serveur", "REMATCH:MENU"), -1);
    }

    public synchronized void disconnectedClient(ConnectedClient discClient) {
        discClient.closeClient();
        clients.remove(discClient);
        rematchReadyPlayers.clear();
        gameStarted = false;
        resetBoard();
        currentTurn = 1;

        if (clients.size() == 1) {
            ConnectedClient remaining = clients.get(0);
            remaining.setPlayerNumber(1);
            remaining.sendMessage(new Message("Serveur", "STATE:OPPONENT_LEFT"));
            remaining.sendMessage(new Message("Serveur", "STATE:WAITING"));
        }
    }

    private void resetBoard() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                grid[r][c] = 0;
            }
        }
    }

    private int findAvailableRow(int col) {
        for (int row = ROWS - 1; row >= 0; row--) {
            if (grid[row][col] == 0) {
                return row;
            }
        }
        return -1;
    }

    private boolean isBoardFull() {
        for (int col = 0; col < COLS; col++) {
            if (grid[0][col] == 0) {
                return false;
            }
        }
        return true;
    }

    private boolean isWinningMove(int row, int col, int player) {
        return countAligned(row, col, 0, 1, player) >= 4
                || countAligned(row, col, 1, 0, player) >= 4
                || countAligned(row, col, 1, 1, player) >= 4
                || countAligned(row, col, 1, -1, player) >= 4;
    }

    private int countAligned(int row, int col, int dRow, int dCol, int player) {
        int count = 1;
        count += countDirection(row, col, dRow, dCol, player);
        count += countDirection(row, col, -dRow, -dCol, player);
        return count;
    }

    private int countDirection(int row, int col, int dRow, int dCol, int player) {
        int count = 0;
        int r = row + dRow;
        int c = col + dCol;

        while (r >= 0 && r < ROWS && c >= 0 && c < COLS && grid[r][c] == player) {
            count++;
            r += dRow;
            c += dCol;
        }

        return count;
    }
}