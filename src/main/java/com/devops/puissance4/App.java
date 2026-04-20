package com.devops.puissance4;

import com.devops.puissance4.model.Game;
import com.devops.puissance4.model.Player;
import com.devops.puissance4.service.AuthService;
import com.devops.puissance4.service.GameService;
import com.devops.puissance4.util.JpaUtil;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        try {
            AuthService authService = new AuthService();
            GameService gameService = new GameService();

            Player p1;
            Player p2;

            try {
                p1 = authService.register("alice", "password123");
            } catch (Exception e) {
                p1 = authService.login("alice", "password123");
            }

            try {
                p2 = authService.register("bob", "password123");
            } catch (Exception e) {
                p2 = authService.login("bob", "password123");
            }

            Game game = gameService.saveFinishedGame(p1, p2, p1);
            System.out.println("Game saved with id: " + game.getId());

        } catch (Exception e) {
            e.printStackTrace();
        }

        stage.setTitle("Connect4");
        stage.show();
    }

    @Override
    public void stop() {
        JpaUtil.shutdown();
    }

    public static void main(String[] args) {
        launch();
    }
}