package com.devops.puissance4.client;

import com.devops.puissance4.common.Message;
import javafx.application.Platform;
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

public class ClientPanel extends Parent {
    private static final int COLS = 7;
    private static final int ROWS = 6;
    private static final int CELL_SIZE = 70;

    private int playerNumber = 0;
    private int currentTurn = 0;
    private boolean gameStarted = false;
    private boolean isPvP = false;

    // Grille logique : 0 = vide, 1 = joueur1, 2 = joueur2
    private int[][] grid = new int[ROWS][COLS];
    private Circle[][] circles = new Circle[ROWS][COLS];

    private Client client;

    private StackPane root;
    private VBox lobbyPane;
    private VBox gamePane;
    private Label statusLabel;
    private Label turnLabel;
    private TextArea textToSend;
    private ScrollPane scrollReceivedText;
    private TextFlow receivedText;
    private Button sendBtn;
    private Button clearBtn;

    // Main
    public ClientPanel() {
        root = new StackPane();
        root.setPrefSize(620, 800);

        buildLobby();
        buildGame();

        root.getChildren().addAll(gamePane, lobbyPane);
        lobbyPane.setVisible(true);
        gamePane.setVisible(false);

        this.getChildren().add(root);
    }

    // Attente de joueur
    private void buildLobby() {
        lobbyPane = new VBox(20);
        lobbyPane.setAlignment(Pos.CENTER);
        lobbyPane.setPrefSize(620, 800);

        Label title = new Label("PUISSANCE 4");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 42));

        statusLabel = new Label("En attente d'un adversaire...");
        statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        // Indicateur de joueurs
        HBox playersBox = new HBox(30);
        playersBox.setAlignment(Pos.CENTER);

        VBox p1Box = buildPlayerSlot("Joueur 1", Color.YELLOW);
        VBox p2Box = buildPlayerSlot("Joueur 2", Color.RED);
        playersBox.getChildren().addAll(p1Box, p2Box);

        Button pveBtn = new Button("Jouer seul (PvE)");
        pveBtn.setStyle("-fx-background-color: #d1d1db; -fx-text-fill: BLACK; -fx-font-size: 14; -fx-padding: 10 25; -fx-background-radius: 8;");
        pveBtn.setOnAction(e -> startPvE());

        lobbyPane.getChildren().addAll(title, statusLabel, playersBox, pveBtn);
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

    //Jeu
    private void buildGame() {
        gamePane = new VBox(10);
        gamePane.setAlignment(Pos.TOP_CENTER);
        gamePane.setPrefSize(620, 800);

        // Bandeau statut
        turnLabel = new Label("En attente...");
        turnLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        // Plateau
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
                if (isMyTurn()) colHighlight.setFill(Color.rgb(255, 255, 255, 0.15));
                else colHighlight.setFill(Color.rgb(255, 0, 0, 0.08));
            });
            columnStack.setOnMouseExited(e -> colHighlight.setFill(Color.TRANSPARENT));
            columnStack.setOnMouseClicked(e -> playInColumn(c));

            boardGrid.add(columnStack, col, 0);
        }

        HBox boardContainer = new HBox(boardGrid);
        boardContainer.setAlignment(Pos.CENTER);

        // Chat
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

        sendBtn = new Button("Envoyer");
        sendBtn.setStyle("-fx-background-color: #1a6fc4; -fx-text-fill: white; -fx-background-radius: 6;");
        clearBtn = new Button("Effacer");
        clearBtn.setStyle("-fx-background-color: #555; -fx-text-fill: white; -fx-background-radius: 6;");

        chatInput.getChildren().addAll(textToSend, sendBtn, clearBtn);

        gamePane.getChildren().addAll(turnLabel, boardContainer, scrollReceivedText, chatInput);

        clearBtn.setOnAction(e -> textToSend.clear());
        sendBtn.setOnAction(e -> {
            String txt = textToSend.getText().trim();
            if (txt.isEmpty()) return;
            Message mess = new Message("Moi", txt);
            printChatMessage(mess);
            textToSend.clear();
            if (client != null) client.sendMessage(mess);
        });
    }

    //logique
    private boolean isMyTurn() {
        if (!gameStarted) return false;
        if (!isPvP) return true;
        return currentTurn == playerNumber;
    }

    private void playInColumn(int col) {
        if (!isMyTurn()) return;

        for (int row = ROWS - 1; row >= 0; row--) {
            if (grid[row][col] == 0) {
                grid[row][col] = 1;
                circles[row][col].setFill(Color.YELLOW);
                if (client != null) {
                    client.sendMessage(new Message("Moi", "MOVE:" + col));
                }

                if (!isPvP) {
                    simulateAI();
                }
                return;
            }
        }
    }

    private void simulateAI() {
        Platform.runLater(() -> {
            for (int attempt = 0; attempt < 20; attempt++) {
                int col = (int)(Math.random() * COLS);
                for (int row = ROWS - 1; row >= 0; row--) {
                    if (grid[row][col] == 0) {
                        grid[row][col] = 2;
                        circles[row][col].setFill(Color.RED);
                        return;
                    }
                }
            }
        });
    }

    public void applyOpponentMove(int col) {
        Platform.runLater(() -> {
            int opponentNum = (playerNumber == 1) ? 2 : 1;
            Color color = (opponentNum == 1) ? Color.YELLOW : Color.RED;
            for (int row = ROWS - 1; row >= 0; row--) {
                if (grid[row][col] == 0) {
                    grid[row][col] = opponentNum;
                    circles[row][col].setFill(color);
                    return;
                }
            }
        });
    }

    private void startPvE() {
        isPvP = false;
        gameStarted = true;
        playerNumber = 1;
            turnLabel.setTextFill(Color.YELLOW);
        currentTurn = 1;
        Platform.runLater(() -> {
            lobbyPane.setVisible(false);
            gamePane.setVisible(true);
            turnLabel.setText("Mode Solo (PvE)");
        });
    }

    private void startPvP(int playerNum) {
        isPvP = true;
        gameStarted = true;
        playerNumber = playerNum;
        Platform.runLater(() -> {
            lobbyPane.setVisible(false);
            gamePane.setVisible(true);
            updateTurnLabel();
        });
    }

    private void updateTurnLabel() {
        if (currentTurn == playerNumber) {
            turnLabel.setText("À vous de jouer !");
            turnLabel.setTextFill(Color.YELLOW);
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
                statusLabel.setText("En attente d'un adversaire...");

            } else if (content.startsWith("STATE:READY:")) {
                int num = Integer.parseInt(content.split(":")[2]);
                startPvP(num);

            } else if (content.startsWith("STATE:FULL")) {
                statusLabel.setText("Partie pleine ! Réessayez plus tard.");
                statusLabel.setTextFill(Color.RED);

            } else if (content.startsWith("STATE:OPPONENT_LEFT")) {
                gameStarted = false;
                gamePane.setVisible(false);
                lobbyPane.setVisible(true);
                statusLabel.setText("L'adversaire a quitté. En attente...");
                statusLabel.setTextFill(Color.ORANGE);
                resetBoard();

            } else if (content.startsWith("TURN:")) {
                currentTurn = Integer.parseInt(content.split(":")[1]);
                if (gameStarted && isPvP) updateTurnLabel();

            } else if (content.startsWith("MOVE:")) {
                int col = Integer.parseInt(content.split(":")[1].trim());
                applyOpponentMove(col);

            } else if (content.startsWith("ERROR:NOT_YOUR_TURN")) {
                // Ignorer, l'UI bloque déjà

            } else {
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
        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c < COLS; c++)
                circles[r][c].setFill(Color.WHITE);
    }
}