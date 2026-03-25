package com.devops.puissance4.model;

import jakarta.persistence.*;

@Entity
@Table(
        name = "game_participation",
        uniqueConstraints = @UniqueConstraint(columnNames = {"player_id", "game_id"})
)
public class GameParticipation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private ParticipationRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "result", nullable = false, length = 30)
    private ParticipationResult result;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    public GameParticipation() {
    }

    public GameParticipation(Player player, ParticipationRole role, ParticipationResult result) {
        this.player = player;
        this.role = role;
        this.result = result;
    }

    public Long getId() { return id; }
    public ParticipationRole getRole() { return role; }
    public void setRole(ParticipationRole role) { this.role = role; }
    public ParticipationResult getResult() { return result; }
    public void setResult(ParticipationResult result) { this.result = result; }
    public Player getPlayer() { return player; }
    public void setPlayer(Player player) { this.player = player; }
    public Game getGame() { return game; }
    public void setGame(Game game) { this.game = game; }
}