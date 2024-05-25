package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TicTacToe2Players extends Application {

	private String player1Name = "";
	private String player2Name = "";
	private Button[][] buttons = new Button[3][3];
	private boolean player1Turn = true;
	private int totalRounds = 0;
	private int roundsPlayed = 0;
	private int player1Wins = 0;
	private int player2Wins = 0;
	private Label roundCounterLabel;
	private Label player1ScoreLabel;
	private Label player2ScoreLabel;
	private Stage primaryStage;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		initializeGame();

		Scene initialScene = new Scene(createInitialLayout(), 700, 500);
		primaryStage.setTitle("Tic Tac Player VS Player");
		primaryStage.setScene(initialScene);
		primaryStage.show();
	}

	private VBox createInitialLayout() {
		VBox root = new VBox();
		root.setPadding(new Insets(10));
		root.setSpacing(10);

		GridPane gameGrid = createGameGrid();
		VBox player1Box = createPlayerBox("Player 1 (X):", player1Name);
		VBox player2Box = createPlayerBox("Player 2 (O):", player2Name);

		Label roundsLabel = new Label("Number of Rounds:");
		TextField roundsField = new TextField();

		Button startButton = new Button("Start Game");

		HBox inputBox = new HBox(10);
		inputBox.getChildren().addAll(player1Box, player2Box, roundsLabel, roundsField, startButton);

		roundCounterLabel = new Label();
		player1ScoreLabel = new Label("Player 1 Score: 0");
		player2ScoreLabel = new Label("Player 2 Score: 0");

		updateRoundCounter();

		startButton.setOnAction(e -> startGame(player1Box, player2Box, roundsField, gameGrid, root));

		root.getChildren().addAll(gameGrid, inputBox, roundCounterLabel, player1ScoreLabel, player2ScoreLabel);
		return root;
	}

	private void initializeGame() {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				buttons[i][j] = new Button();
				buttons[i][j].setMinSize(100, 100);
			}
		}
	}

	private GridPane createGameGrid() {
		GridPane gameGrid = new GridPane();
		gameGrid.setPadding(new Insets(10));
		gameGrid.setHgap(5);
		gameGrid.setVgap(5);

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				Button button = buttons[i][j];
				button.setOnAction(e -> handleButtonClick(button));
				gameGrid.add(button, j, i);
			}
		}

		return gameGrid;
	}

	private VBox createPlayerBox(String label, String playerName) {
		VBox playerBox = new VBox(5);
		Label nameLabel = new Label(label);
		TextField nameField = new TextField(playerName);
		playerBox.getChildren().addAll(nameLabel, nameField);
		return playerBox;
	}

	private void startGame(VBox player1Box, VBox player2Box, TextField roundsField, GridPane gameGrid, VBox root) {
		player1Name = ((TextField) player1Box.getChildren().get(1)).getText();
		player2Name = ((TextField) player2Box.getChildren().get(1)).getText();

		try {
			totalRounds = Integer.parseInt(roundsField.getText());
		} catch (NumberFormatException ex) {
			showAlert("Please enter a valid number for rounds!");
			return;
		}

		if (!player1Name.isEmpty() && !player2Name.isEmpty() && totalRounds > 0) {
			((TextField) player1Box.getChildren().get(1)).setEditable(false);
			((TextField) player2Box.getChildren().get(1)).setEditable(false);

			updateRoundCounter();
			player1ScoreLabel.setText(player1Name + " Score: 0");
			player2ScoreLabel.setText(player2Name + " Score: 0");

			root.getChildren().removeAll(gameGrid, player1Box, player2Box);
			HBox hbox = new HBox(50, player1Box, gameGrid, player2Box);
			root.getChildren().add(hbox);
			clearGameBoard(); // Added to clear the game board at the start of each round
		} else {
			showAlert("Please enter names for both players and specify the number of rounds!");
		}
	}

	private void handleButtonClick(Button button) {
		if (button.getText().isEmpty() && totalRounds > 0) {
			if (player1Turn) {
				button.setText("X");
			} else {
				button.setText("O");
			}
			player1Turn = !player1Turn;
			checkGameStatus();
		}
	}

	private void updateRoundCounter() {
		roundCounterLabel.setText("Played Rounds: " + (roundsPlayed + 1 + "/" + totalRounds));
	}

	private void checkGameStatus() {
		String winner = "";

		for (int i = 0; i < 3; i++) {
			if (!buttons[i][0].getText().isEmpty() && buttons[i][0].getText().equals(buttons[i][1].getText())
					&& buttons[i][0].getText().equals(buttons[i][2].getText())) {
				winner = buttons[i][0].getText();
				break;
			}
			if (!buttons[0][i].getText().isEmpty() && buttons[0][i].getText().equals(buttons[1][i].getText())
					&& buttons[0][i].getText().equals(buttons[2][i].getText())) {
				winner = buttons[0][i].getText();
				break;
			}
		}

		if (!buttons[0][0].getText().isEmpty() && buttons[0][0].getText().equals(buttons[1][1].getText())
				&& buttons[0][0].getText().equals(buttons[2][2].getText())) {
			winner = buttons[0][0].getText();
		}

		if (!buttons[0][2].getText().isEmpty() && buttons[0][2].getText().equals(buttons[1][1].getText())
				&& buttons[0][2].getText().equals(buttons[2][0].getText())) {
			winner = buttons[0][2].getText();
		}

		if (!winner.isEmpty()) {
			if (winner.equals("X")) {
				winner = player1Name;
				showAlert(winner + " wins!   " + player2Name + " Starts Now");
			} else if (winner.equals("O")) {
				winner = player2Name;
				showAlert(winner + " wins!   " + player1Name + " Starts Now");
			}
			updateScores(winner);

			roundsPlayed++;
			if (roundsPlayed < totalRounds) {
				clearGameBoard();
				updateRoundCounter();
			} else {
				displayFinalResults();
			}
			return;
		}

		boolean draw = true;
		for (Button[] row : buttons) {
			for (Button button : row) {
				if (button.getText().isEmpty()) {
					draw = false;
					break;
				}
			}
		}

		if (draw) {
			showAlert("It's a draw!");
			roundsPlayed++;
			if (roundsPlayed == totalRounds) {
				displayFinalResults();
			} else {
				clearGameBoard();
				updateRoundCounter();
			}
			return;
		}
	}

	private void updateScores(String winner) {
		if (winner.equals(player1Name)) {
			player1Wins++;
		} else if (winner.equals(player2Name)) {
			player2Wins++;
		}
		player1ScoreLabel.setText(player1Name + " Score: " + player1Wins);
		player2ScoreLabel.setText(player2Name + " Score: " + player2Wins);
	}

	private void displayFinalResults() {
		String finalResult = "Final Results:\n";
		finalResult += player1Name + ": " + player1Wins + " wins\n";
		finalResult += player2Name + ": " + player2Wins + " wins\n";
		finalResult += "Draws: " + (totalRounds - player1Wins - player2Wins) + "\n";

		if (player1Wins > player2Wins) {
			finalResult += player1Name + " wins the series!";
		} else if (player2Wins > player1Wins) {
			finalResult += player2Name + " wins the series!";
		} else {
			finalResult += "It's a draw!";
		}

		showAlert(finalResult);
		resetGame();

		VBox root = new VBox();
		root.setPadding(new Insets(10));
		root.setSpacing(10);

		GridPane gameGrid = new GridPane();
		gameGrid.setPadding(new Insets(10));
		gameGrid.setHgap(5);
		gameGrid.setVgap(5);

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				Button button = new Button();
				button.setMinSize(100, 100);
				button.setOnAction(e -> {
					if (((Button) e.getSource()).getText().isEmpty() && totalRounds > 0) {
						if (player1Turn) {
							((Button) e.getSource()).setText("X");
						} else {
							((Button) e.getSource()).setText("O");
						}
						player1Turn = !player1Turn;
						checkGameStatus();
					}
				});
				buttons[i][j] = button;
				gameGrid.add(button, j, i);
			}
		}

		VBox player1Box = createPlayerBox("Player 1 (X):", player1Name);
		VBox player2Box = createPlayerBox("Player 2 (O):", player2Name);

		Label roundsLabel = new Label("Number of Rounds:");
		TextField roundsField = new TextField();

		Button startButton = new Button("Start Game");

		HBox inputBox = new HBox(10);
		inputBox.getChildren().addAll(player1Box, player2Box, roundsLabel, roundsField, startButton);

		roundCounterLabel = new Label();
		player1ScoreLabel = new Label("Player 1 Score: 0");
		player2ScoreLabel = new Label("Player 2 Score: 0");

		updateRoundCounter();

		root.getChildren().addAll(gameGrid, inputBox, roundCounterLabel, player1ScoreLabel, player2ScoreLabel);

		startButton.setOnAction(e -> {
			player1Name = ((TextField) ((VBox) inputBox.getChildren().get(0)).getChildren().get(1)).getText();
			player2Name = ((TextField) ((VBox) inputBox.getChildren().get(1)).getChildren().get(1)).getText();

			try {
				totalRounds = Integer.parseInt(roundsField.getText());
			} catch (NumberFormatException ex) {
				showAlert("Please enter a valid number for rounds!");
				return;
			}

			if (!player1Name.isEmpty() && !player2Name.isEmpty() && totalRounds > 0) {
				// Disable player name text fields after starting the game
				((TextField) ((VBox) inputBox.getChildren().get(0)).getChildren().get(1)).setEditable(false);
				((TextField) ((VBox) inputBox.getChildren().get(1)).getChildren().get(1)).setEditable(false);

				// Update round counter and player names in the score labels
				updateRoundCounter();
				player1ScoreLabel.setText(player1Name + " Score: 0");
				player2ScoreLabel.setText(player2Name + " Score: 0");

				root.getChildren().removeAll(gameGrid, inputBox);
				HBox hbox = new HBox(50, player1Box, gameGrid, player2Box);
				root.getChildren().add(hbox);
			} else {
				showAlert("Please enter names for both players and specify the number of rounds!");
			}
		});

		Scene initialScene = new Scene(root, 700, 500);
		primaryStage.setScene(initialScene);
		primaryStage.show();
	}

	private void clearGameBoard() {
		for (Button[] row : buttons) {
			for (Button button : row) {
				button.setText("");
			}
		}
	}

	private void resetGame() {
		roundsPlayed = 0;
		player1Wins = 0;
		player2Wins = 0;
		player1Turn = true;
		player1Name = "";
		player2Name = "";
		totalRounds = 0;
		roundCounterLabel.setText("");
		player1ScoreLabel.setText("Player 1 Score: 0");
		player2ScoreLabel.setText("Player 2 Score: 0");
	}

	private void showAlert(String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Game Over");
		alert.setHeaderText(message);
		alert.showAndWait();
	}
}
