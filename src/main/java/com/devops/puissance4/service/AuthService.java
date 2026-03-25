package com.devops.puissance4.service;

import com.devops.puissance4.dao.PlayerRepository;
import com.devops.puissance4.model.Player;
import com.devops.puissance4.util.PasswordUtil;

import java.time.LocalDateTime;
import java.util.Optional;

public class AuthService {

    private final PlayerRepository playerRepository = new PlayerRepository();

    public Player register(String username, String rawPassword) {
        validateCredentials(username, rawPassword);

        Optional<Player> existing = playerRepository.findByUsername(username);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Username already exists.");
        }

        String hash = PasswordUtil.hashPassword(rawPassword);
        Player player = new Player(username, hash);
        return playerRepository.save(player);
    }

    public Player login(String username, String rawPassword) {
        Player player = playerRepository.findByUsername(username)
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