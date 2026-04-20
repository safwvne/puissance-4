package com.devops.puissance4.controller;

import com.devops.puissance4.model.Player;
import com.devops.puissance4.service.AuthService;
<<<<<<< HEAD
import com.devops.puissance4.util.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
=======
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
>>>>>>> origin/gameplay

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    private final AuthService authService = new AuthService();

    @FXML
<<<<<<< HEAD
    private void handleLogin() {
        try {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();

            Player player = authService.login(username, password);
            Session.setCurrentPlayer(player);

            openGameView();
        } catch (IllegalArgumentException e) {
            showMessage(e.getMessage(), false);
        } catch (Exception e) {
            showMessage("Une erreur est survenue pendant la connexion.", false);
=======
    public void onLoginClicked() {
        try {
            Player player = authService.login(
                    usernameField.getText(),
                    passwordField.getText()
            );

            messageLabel.setText("Welcome " + player.getUsername());
            // ici tu changes de scène
        } catch (Exception e) {
            messageLabel.setText(e.getMessage());
>>>>>>> origin/gameplay
        }
    }

    @FXML
<<<<<<< HEAD
    private void handleRegister() {
        try {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();

            Player player = authService.register(username, password);
            Session.setCurrentPlayer(player);

            openGameView();
        } catch (IllegalArgumentException e) {
            showMessage(e.getMessage(), false);
        } catch (Exception e) {
            showMessage("Une erreur est survenue pendant la création du compte.", false);
        }
    }

    private void openGameView() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/devops/puissance4/game-view.fxml")
        );
        Parent root = loader.load();

        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setTitle("Puissance 4");
        stage.setScene(new Scene(root, 620, 800));
        stage.show();
    }

    private void showMessage(String message, boolean success) {
        messageLabel.setText(message);
        messageLabel.setStyle(success
                ? "-fx-text-fill: #1f7a1f;"
                : "-fx-text-fill: #c62828;");
    }
=======
    public void onRegisterClicked() {
        try {
            Player player = authService.register(
                    usernameField.getText(),
                    passwordField.getText()
            );

            messageLabel.setText("Account created for " + player.getUsername());
        } catch (Exception e) {
            messageLabel.setText(e.getMessage());
        }
    }
>>>>>>> origin/gameplay
}