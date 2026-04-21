package com.devops.puissance4.service;

public class PlayerStats {
    private final long totalGames;
    private final long wins;
    private final long losses;
    private final long draws;

    public PlayerStats(long totalGames, long wins, long losses, long draws) {
        this.totalGames = totalGames;
        this.wins = wins;
        this.losses = losses;
        this.draws = draws;
    }

    public long getTotalGames() {
        return totalGames;
    }

    public long getWins() {
        return wins;
    }

    public long getLosses() {
        return losses;
    }

    public long getDraws() {
        return draws;
    }

    public double getWinRate() {
        if (totalGames == 0) {
            return 0.0;
        }
        return (wins * 100.0) / totalGames;
    }
}