package com.devops.puissance4.dao;

import com.devops.puissance4.model.Player;
import com.devops.puissance4.util.JpaUtil;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class PlayerRepository {

    public Player save(Player player) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            if (player.getId() == null) {
                em.persist(player);
            } else {
                player = em.merge(player);
            }

            em.getTransaction().commit();
            return player;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public Optional<Player> findByUsername(String username) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            List<Player> results = em.createQuery(
                            "SELECT p FROM Player p WHERE p.username = :username", Player.class)
                    .setParameter("username", username)
                    .getResultList();

            return results.stream().findFirst();
        } finally {
            em.close();
        }
    }

    public Optional<Player> findById(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return Optional.ofNullable(em.find(Player.class, id));
        } finally {
            em.close();
        }
    }

    public List<Player> findAll() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery("SELECT p FROM Player p ORDER BY p.username", Player.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}