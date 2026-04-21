package com.devops.puissance4.service;

import com.devops.puissance4.model.ParticipationResult;
import com.devops.puissance4.util.JpaUtil;
import jakarta.persistence.EntityManager;

public class PlayerStatsService {

    public PlayerStats getStatsForPlayer(Long playerId) {
        if (playerId == null) {
            return new PlayerStats(0, 0, 0, 0);
        }

        EntityManager em = JpaUtil.getEntityManager();
        try {
            long totalGames = countByPlayer(em, playerId, null);
            long wins = countByPlayer(em, playerId, ParticipationResult.WIN);
            long losses = countByPlayer(em, playerId, ParticipationResult.LOSS);
            long draws = countByPlayer(em, playerId, ParticipationResult.DRAW);

            return new PlayerStats(totalGames, wins, losses, draws);
        } finally {
            em.close();
        }
    }

    private long countByPlayer(EntityManager em, Long playerId, ParticipationResult result) {
        String jpql = "SELECT COUNT(gp) FROM GameParticipation gp " +
                "WHERE gp.player.id = :playerId AND gp.game.status = com.devops.puissance4.model.GameStatus.FINISHED";

        if (result != null) {
            jpql += " AND gp.result = :result";
        }

        var query = em.createQuery(jpql, Long.class)
                .setParameter("playerId", playerId);

        if (result != null) {
            query.setParameter("result", result);
        }

        return query.getSingleResult();
    }
}