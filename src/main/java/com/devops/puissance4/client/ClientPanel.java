package com.devops.puissance4.client;

import com.devops.puissance4.common.Message;
import com.devops.puissance4.model.Player;
import com.devops.puissance4.service.PlayerStats;
import com.devops.puissance4.service.PlayerStatsService;
import com.devops.puissance4.util.Session;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextFlow;

import java.util.Locale;

public class ClientPanel extends Parent {
    private static final int COLS = 7;
    private static final int ROWS = 6;
    private static final int CELL_SIZE = 70;

    private int playerNumber = 0;
    private int currentTurn = 0;
    private boolean gameStarted = false;
    private boolean isPvP = false;
    private boolean gameEnded = false;
    private boolean expectedDisconnect = false;

    private int[][] grid = new int[ROWS][COLS];
    private final Circle[][] circles = new Circle[ROWS][COLS];

    private Client client;

    private final StackPane root;
    private VBox startPane;
    private VBox lobbyPane;
    private VBox gamePane;
    private StackPane endOverlay;

    private Label startStatusLabel;
    private Label statusLabel;
    private Label turnLabel;
    private Label endTitleLabel;
    private Label endSubtitleLabel;

    private Label welcomeLabel;
    private Label totalGamesValueLabel;
    private Label winsValueLabel;
    private Label lossesValueLabel;
    private Label drawsValueLabel;
    private Label winRateValueLabel;

    private final PlayerStatsService playerStatsService = new PlayerStatsService();

    private TextArea textToSend;
    private ScrollPane scrollReceivedText;
    private TextFlow receivedText;

    private Button replayBtn;
    private Button menuBtn;

    private Circle myTokenLegend;
    private Circle opponentTokenLegend;
    private Label myTokenLegendLabel;
    private Label opponentTokenLegendLabel;

    public ClientPanel() {
        root = new StackPane();
        root.setPrefSize(620, 800);

        buildStartMenu();
        buildLobby();
        buildGame();
        buildEndOverlay();

        root.getChildren().addAll(gamePane, lobbyPane, startPane, endOverlay);
        refreshStats();
        showStartMenu("Choisissez un mode.");

        this.getChildren().add(root);
    }

    private void buildStartMenu() {
        startPane = new VBox(16);
        startPane.setAlignment(Pos.CENTER);
        startPane.setPrefSize(620, 800);
        startPane.setPadding(new Insets(20));

        Label title = new Label("PUISSANCE 4");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 42));

        welcomeLabel = new Label("Bienvenue");
        welcomeLabel.setTextFill(Color.web("#333333"));
        welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        VBox statsCard = new VBox(12);
        statsCard.setAlignment(Pos.CENTER);
        statsCard.setMaxWidth(420);
        statsCard.setStyle("-fx-background-color: white; -fx-padding: 18; -fx-background-radius: 16; -fx-border-radius: 16; -fx-border-color: #dcdce1;");

        Label statsTitle = new Label("Vos statistiques");
        statsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        statsTitle.setTextFill(Color.web("#1c1c1e"));

        HBox row1 = new HBox(12,
                createStatBox("Parties", totalGamesValueLabel = new Label("0")),
                createStatBox("Victoires", winsValueLabel = new Label("0"))
        );
        row1.setAlignment(Pos.CENTER);

        HBox row2 = new HBox(12,
                createStatBox("Défaites", lossesValueLabel = new Label("0")),
                createStatBox("Nuls", drawsValueLabel = new Label("0"))
        );
        row2.setAlignment(Pos.CENTER);

        HBox row3 = new HBox(
                createStatBox("Taux de victoire", winRateValueLabel = new Label("0%"))
        );
        row3.setAlignment(Pos.CENTER);

        statsCard.getChildren().addAll(statsTitle, row1, row2, row3);

        Button soloBtn = new Button("Jouer solo (IA)");
        soloBtn.setStyle("-fx-background-color: #3e8ef7; -fx-text-fill: white; -fx-font-size: 14; -fx-padding: 10 25; -fx-background-radius: 8;");
        soloBtn.setOnAction(e -> startPvE());

        Button pvpBtn = new Button("Rechercher un joueur (PvP)");
        pvpBtn.setStyle("-fx-background-color: #1a6fc4; -fx-text-fill: white; -fx-font-size: 14; -fx-padding: 10 25; -fx-background-radius: 8;");
        pvpBtn.setOnAction(e -> startPvPSearch());

        startStatusLabel = new Label("Choisissez un mode.");
        startStatusLabel.setTextFill(Color.DIMGRAY);
        startStatusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        startPane.getChildren().addAll(title, welcomeLabel, statsCard, soloBtn, pvpBtn, startStatusLabel);
    }

    private VBox createStatBox(String title, Label valueLabel) {
        VBox box = new VBox(6);
        box.setAlignment(Pos.CENTER);
        box.setPrefWidth(160);
        box.setPadding(new Insets(12));
        box.setStyle("-fx-background-color: #f5f6fa; -fx-background-radius: 12;");

        Label titleLabel = new Label(title);
        titleLabel.setTextFill(Color.web("#6e6e73"));
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        valueLabel.setTextFill(Color.web("#111827"));
        valueLabel.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 20));

        box.getChildren().addAll(titleLabel, valueLabel);
        return box;
    }

    private void refreshStats() {
        Player currentPlayer = Session.getCurrentPlayer();

        if (currentPlayer == null) {
            welcomeLabel.setText("Bienvenue");
            totalGamesValueLabel.setText("0");
            winsValueLabel.setText("0");
            lossesValueLabel.setText("0");
            drawsValueLabel.setText("0");
            winRateValueLabel.setText("0%");
            return;
        }

        String username = currentPlayer.getUsername() != null ? currentPlayer.getUsername() : "joueur";
        welcomeLabel.setText("Bienvenue " + username);

        PlayerStats stats = playerStatsService.getStatsForPlayer(currentPlayer.getId());

        totalGamesValueLabel.setText(String.valueOf(stats.getTotalGames()));
        winsValueLabel.setText(String.valueOf(stats.getWins()));
        lossesValueLabel.setText(String.valueOf(stats.getLosses()));
        drawsValueLabel.setText(String.valueOf(stats.getDraws()));
        winRateValueLabel.setText(String.format(Locale.US, "%.0f%%", stats.getWinRate()));
    }

    private void buildLobby() {
        lobbyPane = new VBox(20);
        lobbyPane.setAlignment(Pos.CENTER);
        lobbyPane.setPrefSize(620, 800);

        Label title = new Label("PUISSANCE 4");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 42));

        statusLabel = new Label("En attente d'un adversaire...");
        statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        HBox playersBox = new HBox(30);
        playersBox.setAlignment(Pos.CENTER);

        VBox p1Box = buildPlayerSlot("Joueur 1", Color.YELLOW);
        VBox p2Box = buildPlayerSlot("Joueur 2", Color.RED);
        playersBox.getChildren().addAll(p1Box, p2Box);

        Button cancelBtn = new Button("Retour au menu");
        cancelBtn.setStyle("-fx-background-color: #555; -fx-text-fill: white; -fx-font-size: 13; -fx-padding: 8 18; -fx-background-radius: 8;");
        cancelBtn.setOnAction(e -> returnToMenuFromLobby());

        lobbyPane.getChildren().addAll(title, statusLabel, playersBox, cancelBtn);
    }

    private VBox buildPlayerSlot(String name, Color color) {
        VBox box = new VBox(8);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: #d1d1db; -fx-padding: 15; -fx-background-radius: 10;");
        box.setPrefWidth(140);

        Circle token = new Circle(25, color);
        token.setStroke(Color.BLACK);
        token.setStrokeWidth(2);

        Label lbl = new Label(name);
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        box.getChildren().addAll(token, lbl);
        return box;
    }

    private void buildGame() {
        gamePane = new VBox(10);
        gamePane.setAlignment(Pos.TOP_CENTER);
        gamePane.setPrefSize(620, 800);

        turnLabel = new Label("En attente...");
        turnLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        HBox tokenLegend = new HBox(20);
        tokenLegend.setAlignment(Pos.CENTER);

        myTokenLegend = new Circle(10, Color.YELLOW);
        myTokenLegend.setStroke(Color.BLACK);
        myTokenLegendLabel = new Label("Vous (Jaune)");

        opponentTokenLegend = new Circle(10, Color.RED);
        opponentTokenLegend.setStroke(Color.BLACK);
        opponentTokenLegendLabel = new Label("Adversaire (Rouge)");

        HBox myLegendBox = new HBox(6, myTokenLegend, myTokenLegendLabel);
        myLegendBox.setAlignment(Pos.CENTER);

        HBox opponentLegendBox = new HBox(6, opponentTokenLegend, opponentTokenLegendLabel);
        opponentLegendBox.setAlignment(Pos.CENTER);

        tokenLegend.getChildren().addAll(myLegendBox, opponentLegendBox);

        GridPane boardGrid = new GridPane();
        boardGrid.setHgap(5);
        boardGrid.setVgap(5);
        boardGrid.setStyle("-fx-background-color: #1a6fc4; -fx-padding: 10; -fx-background-radius: 10;");

        for (int col = 0; col < COLS; col++) {
            final int c = col;

            Rectangle colHighlight = new Rectangle(CELL_SIZE, ROWS * (CELL_SIZE + 5) + 5);
            colHighlight.setFill(Color.TRANSPARENT);
            colHighlight.setArcWidth(10);
            colHighlight.setArcHeight(10);

            VBox columnCells = new VBox(5);
            columnCells.setAlignment(Pos.CENTER);

            for (int row = 0; row < ROWS; row++) {
                Circle circle = new Circle(CELL_SIZE / 2.0 - 5);
                circle.setFill(Color.WHITE);
                circle.setStroke(Color.DARKBLUE);
                circle.setStrokeWidth(1.5);
                circles[row][col] = circle;

                StackPane cell = new StackPane(circle);
                cell.setPrefSize(CELL_SIZE, CELL_SIZE);
                columnCells.getChildren().add(cell);
            }

            StackPane columnStack = new StackPane(columnCells, colHighlight);

            columnStack.setOnMouseEntered(e -> {
                if (isMyTurn()) {
                    colHighlight.setFill(Color.rgb(255, 255, 255, 0.15));
                } else {
                    colHighlight.setFill(Color.rgb(255, 0, 0, 0.08));
                }
            });
            columnStack.setOnMouseExited(e -> colHighlight.setFill(Color.TRANSPARENT));
            columnStack.setOnMouseClicked(e -> playInColumn(c));

            boardGrid.add(columnStack, col, 0);
        }

        HBox boardContainer = new HBox(boardGrid);
        boardContainer.setAlignment(Pos.CENTER);

        scrollReceivedText = new ScrollPane();
        scrollReceivedText.setPrefWidth(500);
        scrollReceivedText.setPrefHeight(120);
        receivedText = new TextFlow();
        receivedText.setPrefWidth(480);
        scrollReceivedText.setContent(receivedText);
        scrollReceivedText.vvalueProperty().bind(receivedText.heightProperty());

        HBox chatInput = new HBox(8);
        chatInput.setAlignment(Pos.CENTER);
        textToSend = new TextArea();
        textToSend.setPrefWidth(380);
        textToSend.setPrefHeight(50);
        textToSend.setWrapText(true);

        Button sendBtn = new Button("Envoyer");
        sendBtn.setStyle("-fx-background-color: #1a6fc4; -fx-text-fill: white; -fx-background-radius: 6;");
        Button clearBtn = new Button("Effacer");
        clearBtn.setStyle("-fx-background-color: #555; -fx-text-fill: white; -fx-background-radius: 6;");

        chatInput.getChildren().addAll(textToSend, sendBtn, clearBtn);

        gamePane.getChildren().addAll(turnLabel, tokenLegend, boardContainer, scrollReceivedText, chatInput);

        clearBtn.setOnAction(e -> textToSend.clear());
        sendBtn.setOnAction(e -> {
            String txt = textToSend.getText().trim();
            if (txt.isEmpty()) {
                return;
            }
            Message mess = new Message("Moi", txt);
            printChatMessage(mess);
            textToSend.clear();
            if (client != null) {
                client.sendMessage(mess);
            }
        });
    }

    private void buildEndOverlay() {
        endOverlay = new StackPane();
        endOverlay.setPrefSize(620, 800);
        endOverlay.setStyle("-fx-background-color: rgba(0,0,0,0.62);");
        endOverlay.setVisible(false);

        VBox modal = new VBox(14);
        modal.setAlignment(Pos.CENTER);
        modal.setPadding(new Insets(24));
        modal.setMaxWidth(420);
        modal.setStyle("-fx-background-color: #1f1f1f; -fx-background-radius: 14;");

        endTitleLabel = new Label("Victoire !");
        endTitleLabel.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 36));
        endTitleLabel.setTextFill(Color.WHITE);

        endSubtitleLabel = new Label("");
        endSubtitleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        endSubtitleLabel.setTextFill(Color.LIGHTGRAY);

        replayBtn = new Button("Rejouer");
        replayBtn.setStyle("-fx-background-color: #3e8ef7; -fx-text-fill: white; -fx-font-size: 14; -fx-padding: 10 25; -fx-background-radius: 8;");
        replayBtn.setOnAction(e -> onReplayClicked());

        menuBtn = new Button("Retour au menu");
        menuBtn.setStyle("-fx-background-color: #777; -fx-text-fill: white; -fx-font-size: 14; -fx-padding: 10 25; -fx-background-radius: 8;");
        menuBtn.setOnAction(e -> onMenuClicked());

        HBox actions = new HBox(10, replayBtn, menuBtn);
        actions.setAlignment(Pos.CENTER);

        modal.getChildren().addAll(endTitleLabel, endSubtitleLabel, actions);
        endOverlay.getChildren().add(modal);
    }

    private boolean isMyTurn() {
        if (!gameStarted || gameEnded) {
            return false;
        }
        if (!isPvP) {
            return true;
        }
        return currentTurn == playerNumber;
    }

    private void playInColumn(int col) {
        if (!isMyTurn()) {
            return;
        }

        for (int row = ROWS - 1; row >= 0; row--) {
            if (grid[row][col] == 0) {
                grid[row][col] = playerNumber;

                Color c = getColorForPlayer(playerNumber);
                circles[row][col].setFill(c);
                if (client != null) {
                    client.sendMessage(new Message("Moi", "MOVE:" + col));
                }

                if (hasConnectFour(row, col, playerNumber)) {
                    finishGame(playerNumber);
                } else if (isBoardFull()) {
                    finishGame(0);
                }

                if (!isPvP && !gameEnded) {
                    simulateAI();
                }
                return;
            }
        }
    }

    private void simulateAI() {
        Platform.runLater(() -> {
            for (int attempt = 0; attempt < 20; attempt++) {
                int col = (int) (Math.random() * COLS);
                for (int row = ROWS - 1; row >= 0; row--) {
                    if (grid[row][col] == 0) {
                        grid[row][col] = 2;
                        circles[row][col].setFill(getColorForPlayer(2));

                        if (hasConnectFour(row, col, 2)) {
                            finishGame(2);
                        } else if (isBoardFull()) {
                            finishGame(0);
                        }
                        return;
                    }
                }
            }
        });
    }

    public void applyOpponentMove(int col) {
        Platform.runLater(() -> {
            int opponentNum = (playerNumber == 1) ? 2 : 1;
            Color color = getColorForPlayer(opponentNum);
            for (int row = ROWS - 1; row >= 0; row--) {
                if (grid[row][col] == 0) {
                    grid[row][col] = opponentNum;
                    circles[row][col].setFill(color);

                    if (hasConnectFour(row, col, opponentNum)) {
                        finishGame(opponentNum);
                    } else if (isBoardFull()) {
                        finishGame(0);
                    }
                    return;
                }
            }
        });
    }

    private void startPvE() {
        isPvP = false;
        gameStarted = true;
        gameEnded = false;
        playerNumber = 1;
        currentTurn = 1;
        resetBoard();
        updateTokenLegend();

        startPane.setVisible(false);
        lobbyPane.setVisible(false);
        gamePane.setVisible(true);
        endOverlay.setVisible(false);

        turnLabel.setText("Mode Solo (PvE) - A vous de jouer !");
        turnLabel.setTextFill(Color.DEEPSKYBLUE);
    }

    private void startPvPSearch() {
        isPvP = true;
        gameStarted = false;
        gameEnded = false;
        playerNumber = 0;
        currentTurn = 0;
        resetBoard();

        startPane.setVisible(false);
        gamePane.setVisible(false);
        endOverlay.setVisible(false);
        lobbyPane.setVisible(true);
        statusLabel.setTextFill(Color.BLACK);
        statusLabel.setText("Recherche d'un adversaire...");

        if (client == null) {
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("Client reseau non configure.");
            return;
        }

        if (client.isConnected()) {
            statusLabel.setText("Connexion deja active. En attente...");
            return;
        }

        try {
            client.connect();
            statusLabel.setText("Connecte au serveur. En attente...");
        } catch (Exception e) {
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("Connexion impossible au serveur.");
        }
    }

    private void startPvP(int playerNum) {
        isPvP = true;
        gameStarted = true;
        gameEnded = false;
        playerNumber = playerNum;
        resetBoard();
        updateTokenLegend();

        startPane.setVisible(false);
        lobbyPane.setVisible(false);
        gamePane.setVisible(true);
        endOverlay.setVisible(false);

        updateTurnLabel();
    }

    private void restartPvERound() {
        gameEnded = false;
        gameStarted = true;
        currentTurn = 1;
        resetBoard();
        endOverlay.setVisible(false);
        turnLabel.setText("Mode Solo (PvE) - A vous de jouer !");
        turnLabel.setTextFill(Color.DEEPSKYBLUE);
    }

    private void restartPvPRound() {
        gameEnded = false;
        gameStarted = true;
        resetBoard();
        endOverlay.setVisible(false);
        replayBtn.setDisable(false);
        menuBtn.setDisable(false);
        endSubtitleLabel.setText("");
        updateTurnLabel();
    }

    private void onReplayClicked() {
        if (!gameEnded) {
            return;
        }

        if (!isPvP) {
            restartPvERound();
            return;
        }

        replayBtn.setDisable(true);
        endSubtitleLabel.setText("En attente de l'adversaire... (1/2)");

        if (client != null && client.isConnected()) {
            client.sendMessage(new Message("Moi", "REMATCH:READY"));
        } else {
            endSubtitleLabel.setText("Connexion perdue.");
            replayBtn.setDisable(false);
        }
    }

    private void onMenuClicked() {
        if (!isPvP) {
            refreshStats();
            showStartMenu("Choisissez un mode.");
            return;
        }

        if (client != null && client.isConnected()) {
            client.sendMessage(new Message("Moi", "REMATCH:MENU"));
        } else {
            refreshStats();
            showStartMenu("Connexion perdue.");
        }
    }

    private void returnToMenuFromLobby() {
        if (client != null && client.isConnected()) {
            expectedDisconnect = true;
            client.disconnect();
        }
        refreshStats();
        showStartMenu("Choisissez un mode.");
    }

    private void showStartMenu(String message) {
        gameStarted = false;
        gameEnded = false;
        isPvP = false;
        playerNumber = 0;
        currentTurn = 0;
        resetBoard();
        refreshStats();

        endOverlay.setVisible(false);
        gamePane.setVisible(false);
        lobbyPane.setVisible(false);
        startPane.setVisible(true);
        startStatusLabel.setText(message);
    }

    private void updateTurnLabel() {
        if (currentTurn == playerNumber) {
            turnLabel.setText("A vous de jouer !");
            turnLabel.setTextFill(Color.DEEPSKYBLUE);
        } else {
            turnLabel.setText("Tour de l'adversaire...");
            turnLabel.setTextFill(Color.LIGHTGRAY);
        }
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void printNewMessage(Message mess) {
        Platform.runLater(() -> {
            String content = mess.getContent() != null ? mess.getContent() : mess.toString();

            if (content.startsWith("STATE:WAITING")) {
                startPane.setVisible(false);
                gamePane.setVisible(false);
                endOverlay.setVisible(false);
                lobbyPane.setVisible(true);
                statusLabel.setText("En attente d'un adversaire...");
                statusLabel.setTextFill(Color.BLACK);

            } else if (content.startsWith("STATE:READY:")) {
                int num = Integer.parseInt(content.split(":")[2]);
                startPvP(num);

            } else if (content.startsWith("STATE:FULL")) {
                showStartMenu("Partie pleine. Reessayez plus tard.");

            } else if (content.startsWith("STATE:OPPONENT_LEFT")) {
                showStartMenu("L'adversaire a quitte la partie.");
                if (client != null && client.isConnected()) {
                    expectedDisconnect = true;
                    client.disconnect();
                }

            } else if (content.startsWith("STATE:SERVER_DISCONNECTED")) {
                if (expectedDisconnect) {
                    expectedDisconnect = false;
                    showStartMenu("Choisissez un mode.");
                } else {
                    showStartMenu("Connexion serveur fermee.");
                }

            } else if (content.startsWith("TURN:")) {
                currentTurn = Integer.parseInt(content.split(":")[1]);
                if (gameStarted && isPvP && !gameEnded) {
                    updateTurnLabel();
                }

            } else if (content.startsWith("MOVE:")) {
                int col = Integer.parseInt(content.split(":")[1].trim());
                applyOpponentMove(col);

            } else if (content.startsWith("GAME_OVER:WINNER:")) {
                int winner = Integer.parseInt(content.split(":")[2]);
                finishGame(winner);

            } else if (content.startsWith("GAME_OVER:DRAW")) {
                finishGame(0);

            } else if (content.startsWith("REMATCH:READY_COUNT:")) {
                int readyCount = Integer.parseInt(content.split(":")[2]);
                if (gameEnded && isPvP) {
                    endSubtitleLabel.setText("Joueurs prets: " + readyCount + "/2");
                }

            } else if (content.startsWith("REMATCH:START")) {
                if (isPvP) {
                    restartPvPRound();
                }

            } else if (content.startsWith("REMATCH:MENU")) {
                showStartMenu("Retour au menu.");
                if (client != null && client.isConnected()) {
                    expectedDisconnect = true;
                    client.disconnect();
                }

            } else if (!content.startsWith("ERROR:NOT_YOUR_TURN")) {
                printChatMessage(mess);
            }
        });
    }

    private void printChatMessage(Message mess) {
        Label text = new Label(mess.toString());
        text.setWrapText(true);
        text.setPrefWidth(scrollReceivedText.getPrefWidth() - 20);
        text.setAlignment(Pos.CENTER_LEFT);
        receivedText.getChildren().add(text);
    }

    private void resetBoard() {
        grid = new int[ROWS][COLS];
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                circles[r][c].setFill(Color.WHITE);
            }
        }
    }

    private Color getColorForPlayer(int player) {
        return (player == 1) ? Color.YELLOW : Color.RED;
    }

    private void updateTokenLegend() {
        Color myColor = getColorForPlayer(playerNumber == 0 ? 1 : playerNumber);
        int opponentNumber = (playerNumber == 1) ? 2 : 1;
        Color opponentColor = getColorForPlayer(opponentNumber);

        myTokenLegend.setFill(myColor);
        opponentTokenLegend.setFill(opponentColor);

        myTokenLegendLabel.setText("Vous (" + (myColor == Color.YELLOW ? "Jaune" : "Rouge") + ")");
        opponentTokenLegendLabel.setText("Adversaire (" + (opponentColor == Color.YELLOW ? "Jaune" : "Rouge") + ")");
    }

    private boolean hasConnectFour(int row, int col, int player) {
        return countDirection(row, col, 0, 1, player) + countDirection(row, col, 0, -1, player) - 1 >= 4
                || countDirection(row, col, 1, 0, player) + countDirection(row, col, -1, 0, player) - 1 >= 4
                || countDirection(row, col, 1, 1, player) + countDirection(row, col, -1, -1, player) - 1 >= 4
                || countDirection(row, col, 1, -1, player) + countDirection(row, col, -1, 1, player) - 1 >= 4;
    }

    private int countDirection(int row, int col, int dr, int dc, int player) {
        int count = 0;
        int r = row;
        int c = col;
        while (r >= 0 && r < ROWS && c >= 0 && c < COLS && grid[r][c] == player) {
            count++;
            r += dr;
            c += dc;
        }
        return count;
    }

    private boolean isBoardFull() {
        for (int c = 0; c < COLS; c++) {
            if (grid[0][c] == 0) {
                return false;
            }
        }
        return true;
    }

    private void finishGame(int winner) {
        gameEnded = true;

        if (winner == 0) {
            turnLabel.setText("Match nul !");
            turnLabel.setTextFill(Color.DEEPSKYBLUE);
            showEndOverlay("Match nul !", isPvP ? "Choisissez rejouer ou menu." : "Rejouer contre l'IA ?");
            return;
        }

        if (winner == playerNumber) {
            turnLabel.setText("Victoire !");
            turnLabel.setTextFill(Color.LIMEGREEN);
            showEndOverlay("Victoire !", isPvP ? "Bien joue." : "Vous avez gagne contre l'IA.");
        } else if (!isPvP && winner == 2) {
            turnLabel.setText("L'IA gagne !");
            turnLabel.setTextFill(Color.ORANGERED);
            showEndOverlay("Defaite !", "L'IA gagne cette manche.");
        } else {
            turnLabel.setText("Defaite !");
            turnLabel.setTextFill(Color.ORANGERED);
            showEndOverlay("Defaite !", "L'adversaire gagne cette manche.");
        }
    }

    private void showEndOverlay(String title, String subtitle) {
        endTitleLabel.setText(title);
        endSubtitleLabel.setText(subtitle);
        replayBtn.setDisable(false);
        menuBtn.setDisable(false);
        endOverlay.setVisible(true);
    }
}