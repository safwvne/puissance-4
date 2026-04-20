package com.devops.puissance4.util;

import com.devops.puissance4.model.Player;

public final class Session {
    private static Player currentPlayer;
    private static String serverHost = "127.0.0.1";
    private static int serverPort = 6000;

    private Session() {
    }

    public static Player getCurrentPlayer() {
        return currentPlayer;
    }

    public static void setCurrentPlayer(Player player) {
        currentPlayer = player;
    }

    public static boolean isLoggedIn() {
        return currentPlayer != null;
    }

    public static void clear() {
        currentPlayer = null;
    }

    public static String getServerHost() {
        return serverHost;
    }

    public static int getServerPort() {
        return serverPort;
    }

    public static void setServerEndpoint(String host, int port) {
        if (host != null && !host.isBlank()) {
            serverHost = host.trim();
        }
        if (port > 0 && port <= 65535) {
            serverPort = port;
        }
    }
}