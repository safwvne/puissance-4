package com.devops.puissance4.service;

import com.devops.puissance4.dao.GameRepository;
import com.devops.puissance4.model.*;

import java.time.LocalDateTime;

public class GameService {

    private final GameRepository gameRepository = new GameRepository();

    public Game saveFinishedGame(Player player1, Player player2, Player winner) {
        if (player1 == null || player2 == null) {
            throw new IllegalArgumentException("Two players are required.");
        }

        if (player1.getId().equals(player2.getId())) {
            throw new IllegalArgumentException("A player cannot play against themselves.");
        }

        Game game = new Game(GameStatus.FINISHED);
        game.setStartTime(LocalDateTime.now().minusMinutes(5));
        game.setEndTime(LocalDateTime.now());

        ParticipationResult result1;
        ParticipationResult result2;

        if (winner == null) {
            result1 = ParticipationResult.DRAW;
            result2 = ParticipationResult.DRAW;
        } else if (winner.getId().equals(player1.getId())) {
            result1 = ParticipationResult.WIN;
            result2 = ParticipationResult.LOSS;
        } else if (winner.getId().equals(player2.getId())) {
            result1 = ParticipationResult.LOSS;
            result2 = ParticipationResult.WIN;
        } else {
            throw new IllegalArgumentException("Winner must be one of the players.");
        }

        GameParticipation gp1 = new GameParticipation(player1, ParticipationRole.PLAYER1, result1);
        GameParticipation gp2 = new GameParticipation(player2, ParticipationRole.PLAYER2, result2);

        game.addParticipation(gp1);
        game.addParticipation(gp2);

        return gameRepository.save(game);
    }
}