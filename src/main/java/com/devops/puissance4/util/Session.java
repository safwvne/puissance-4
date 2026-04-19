package com.devops.puissance4.util;

import com.devops.puissance4.model.Player;

public final class Session {
    private static Player currentPlayer;

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
}