package com.devops.puissance4;

import com.devops.puissance4.model.Player;
import com.devops.puissance4.util.JpaUtil;
import jakarta.persistence.EntityManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) {
        System.out.println("=== TEST JPA START ===");

        try {
            EntityManager em = JpaUtil.getEntityManager();

            em.getTransaction().begin();

            Player player = new Player("saf", "hash_test");
            em.persist(player);

            em.getTransaction().commit();

            System.out.println("=== PLAYER SAVED WITH ID: " + player.getId() + " ===");

            em.close();
        } catch (Exception e) {
            System.out.println("=== JPA ERROR ===");
            e.printStackTrace();
        }

        stage.setTitle("Puissance 4");
        stage.show();
    }

    @Override
    public void stop() {
        try {
            JpaUtil.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}