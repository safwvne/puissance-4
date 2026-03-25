package com.devops.puissance4.controller;

import com.devops.puissance4.model.Player;
import com.devops.puissance4.service.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    private final AuthService authService = new AuthService();

    @FXML
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
        }
    }

    @FXML
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
}