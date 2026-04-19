package com.devops.puissance4.service;

import com.devops.puissance4.dao.PlayerRepository;
import com.devops.puissance4.model.Player;
import com.devops.puissance4.util.PasswordUtil;

import java.time.LocalDateTime;

public class AuthService {

    private final PlayerRepository playerRepository = new PlayerRepository();

    public Player register(String username, String rawPassword) {
        validateCredentials(username, rawPassword);

        username = username.trim();

        if (playerRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists.");
        }

        String passwordHash = PasswordUtil.hashPassword(rawPassword);

        Player player = new Player();
        player.setUsername(username);
        player.setPasswordHash(passwordHash);
        player.setCreatedAt(LocalDateTime.now());
        player.setLastLogin(null);

        return playerRepository.save(player);
    }

    public Player login(String username, String rawPassword) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username is required.");
        }

        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Password is required.");
        }

        Player player = playerRepository.findByUsername(username.trim())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password."));

        if (!PasswordUtil.verifyPassword(rawPassword, player.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid username or password.");
        }

        player.setLastLogin(LocalDateTime.now());
        return playerRepository.save(player);
    }

    private void validateCredentials(String username, String rawPassword) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username is required.");
        }

        if (rawPassword == null || rawPassword.length() < 6) {
            throw new IllegalArgumentException("Password must contain at least 6 characters.");
        }
    }
}