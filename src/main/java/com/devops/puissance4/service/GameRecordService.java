package com.devops.puissance4.service;

import com.devops.puissance4.model.Game;
import com.devops.puissance4.model.GameParticipation;
import com.devops.puissance4.model.GameStatus;
import com.devops.puissance4.model.ParticipationResult;
import com.devops.puissance4.model.ParticipationRole;
import com.devops.puissance4.model.Player;
import com.devops.puissance4.util.JpaUtil;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;

public class GameRecordService {

    public void recordFinishedGame(
            Long player1Id,
            Long player2Id,
            int winnerPlayerNumber,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        if (player1Id == null || player2Id == null) {
            return;
        }

        EntityManager em = JpaUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            Player player1 = em.find(Player.class, player1Id);
            Player player2 = em.find(Player.class, player2Id);

            if (player1 == null || player2 == null) {
                em.getTransaction().rollback();
                return;
            }

            Game game = new Game();
            game.setStartTime(startTime != null ? startTime : LocalDateTime.now());
            game.setEndTime(endTime != null ? endTime : LocalDateTime.now());
            game.setStatus(GameStatus.FINISHED);

            ParticipationResult p1Result;
            ParticipationResult p2Result;

            if (winnerPlayerNumber == 1) {
                p1Result = ParticipationResult.WIN;
                p2Result = ParticipationResult.LOSS;
            } else if (winnerPlayerNumber == 2) {
                p1Result = ParticipationResult.LOSS;
                p2Result = ParticipationResult.WIN;
            } else {
                p1Result = ParticipationResult.DRAW;
                p2Result = ParticipationResult.DRAW;
            }

            GameParticipation gp1 = new GameParticipation(
                    player1,
                    ParticipationRole.PLAYER1,
                    p1Result
            );

            GameParticipation gp2 = new GameParticipation(
                    player2,
                    ParticipationRole.PLAYER2,
                    p2Result
            );

            game.addParticipation(gp1);
            game.addParticipation(gp2);

            em.persist(game);

            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}