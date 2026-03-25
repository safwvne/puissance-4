package com.devops.puissance4.dao;

import com.devops.puissance4.model.Game;
import com.devops.puissance4.util.JpaUtil;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class GameRepository {

    public Game save(Game game) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            if (game.getId() == null) {
                em.persist(game);
            } else {
                game = em.merge(game);
            }

            em.getTransaction().commit();
            return game;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public Optional<Game> findById(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return Optional.ofNullable(em.find(Game.class, id));
        } finally {
            em.close();
        }
    }

    public List<Game> findAll() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery("SELECT g FROM Game g ORDER BY g.startTime DESC", Game.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}