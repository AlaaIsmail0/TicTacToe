package application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class TicTacToeRandom extends Application {

	private Button[][] buttons = new Button[3][3];
	private boolean playerTurn = true;
	private String playerName = "Player";
	private int totalRounds = 1;
	private int roundsPlayed = 0;
	private int playerWins = 0;
	private int aiWins = 0;
	private boolean gameStarted = false;
	private Label roundLabel = new Label("Round: 0 / 0");
	private ToggleGroup turnToggleGroup = new ToggleGroup();
	private Label playerScoreLabel = new Label("Player Score: ");
	private Label aiScoreLabel = new Label("AI Score: 0");

	@Override
	public void start(Stage primaryStage) {
		GridPane gridPane = createGridPane();
		Scene scene = new Scene(gridPane, 340, 530);
		primaryStage.setTitle("Tic Tac Toe Player VS Random AI");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private GridPane createGridPane() {
		GridPane gridPane = new GridPane();
		gridPane.setHgap(10);
		gridPane.setVgap(10);
		gridPane.setPadding(new Insets(10));

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				Button button = new Button();
				button.setMinSize(100, 100);
				button.setDisable(true);
				button.setOnAction(e -> {
					if (!gameStarted || !((Button) e.getSource()).getText().isEmpty() || !playerTurn) {
						return;
					}
					((Button) e.getSource()).setText("X");
					if (checkWin("X")) {
						announceWinner("Player");
					} else if (isBoardFull()) {
						announceWinner("Draw");
					} else {
						playerTurn = false;
						makeAIMove();
					}
				});
				buttons[i][j] = button;
				gridPane.add(button, j, i);
			}
		}

		TextField nameField = new TextField();
		nameField.setPromptText("Enter Your Name");

		TextField roundsField = new TextField();
		roundsField.setPromptText("Enter Number of Rounds");

		ChoiceBox<String> startChoiceBox = new ChoiceBox<>();
		startChoiceBox.getItems().addAll("Player", "AI");
		startChoiceBox.setValue("Player");

		Button startGameButton = new Button("Start Game");
		RadioButton playerRadioButton = new RadioButton("Player");
		RadioButton aiRadioButton = new RadioButton("AI");
		playerRadioButton.setToggleGroup(turnToggleGroup);
		aiRadioButton.setToggleGroup(turnToggleGroup);
		playerRadioButton.setSelected(true);

		startGameButton.setOnAction(e -> {
			playerName = nameField.getText().isEmpty() ? "Player" : nameField.getText();
			playerScoreLabel.setText(playerName + " Score: 0");

			try {
				totalRounds = Integer.parseInt(roundsField.getText());
				roundLabel.setText("Round: 1 / " + totalRounds);
				showAlert("Game started!\nPlayer: " + playerName + "\nRounds: " + totalRounds);
				playerTurn = (turnToggleGroup.getSelectedToggle() == playerRadioButton);
				gameStarted = true;
				enableAllButtons();

				if (!playerTurn) {
					makeAIMove();
				}
			} catch (NumberFormatException ex) {
				showAlert("Please enter a valid number for rounds!");
				return;
			}
		});

		GridPane.setColumnSpan(nameField, 2);
		GridPane.setColumnSpan(roundsField, 2);
		GridPane.setColumnSpan(startChoiceBox, 2);

		gridPane.add(nameField, 0, 3);
		gridPane.add(roundsField, 0, 4);
		gridPane.add(playerRadioButton, 0, 5);
		gridPane.add(aiRadioButton, 1, 5);
		gridPane.add(startGameButton, 0, 6);
		gridPane.add(roundLabel, 0, 7);
		gridPane.add(playerScoreLabel, 0, 8);
		gridPane.add(aiScoreLabel, 1, 8);

		return gridPane;
	}

	private void makeAIMove() {
		int[] randomMove = randomOption(boardToStringArray(), "O");
		if (randomMove != null) {
			buttons[randomMove[0]][randomMove[1]].setText("O");
			if (checkWin("O")) {
				announceWinner("AI");
			} else if (isBoardFull()) {
				announceWinner("Draw");
			} else {
				playerTurn = true;
			}
		}
	}

	private int[] randomOption(String[][] board, String currentPlayer) {
		Random random = new Random();
		List<int[]> legalMoves = new ArrayList<>(); // list to store coordinates of legal moves

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (board[i][j].isEmpty()) {
					legalMoves.add(new int[] { i, j });
				}
			}
		}
		return legalMoves.isEmpty() ? null : legalMoves.get(random.nextInt(legalMoves.size()));
	}

	private String[][] boardToStringArray() {
		String[][] boardArray = new String[3][3];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				boardArray[i][j] = buttons[i][j].getText();
			}
		}
		return boardArray;
	}

	private void announceWinner(String winner) {
		roundsPlayed++;

		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Round Over");

		if (winner.equals("Player")) {
			alert.setHeaderText("Congratulations, " + playerName + "! You win!");
			playerWins++;
		} else if (winner.equals("AI")) {
			alert.setHeaderText("AI wins! Better luck next time, " + playerName + " -_-");
			aiWins++;
		} else {
			alert.setHeaderText("It's a draw! No one wins.");
		}

		playerScoreLabel.setText("Player Score: " + playerWins);
		aiScoreLabel.setText("AI Score: " + aiWins);

		// Update round label before showing the alert
		roundLabel.setText("Round: " + roundsPlayed + " / " + totalRounds);

		alert.setOnHidden(event -> {
			if (roundsPlayed < totalRounds) {
				clearBoard();
				enableAllButtons(); // Enable buttons for the next round

				// Update round label for the next round
				roundLabel.setText("Round: " + (roundsPlayed + 1) + " / " + totalRounds);
			} else {
				displayFinalResults();
				resetGame();
			}
		});

		alert.showAndWait();
	}

	private void displayFinalResults() {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Game Over");

		String resultMessage = "Final Results:\n";
		resultMessage += playerName + " Wins: " + playerWins + "\n";
		resultMessage += "AI Wins: " + aiWins + "\n";
		resultMessage += "Draws: " + (totalRounds - playerWins - aiWins) + "\n";

		if (playerWins > aiWins) {
			resultMessage += playerName + " wins the series!";
		} else if (aiWins > playerWins) {
			resultMessage += "AI wins the series!";
		} else {
			resultMessage += "It's a draw!";
		}

		alert.setHeaderText(resultMessage);

		ButtonType newGameButton = new ButtonType("New Game", ButtonBar.ButtonData.OK_DONE);
		ButtonType exitButton = new ButtonType("Exit", ButtonBar.ButtonData.CANCEL_CLOSE);
		alert.getButtonTypes().setAll(newGameButton, exitButton);

		// Show confirmation dialog
		Optional<ButtonType> result = alert.showAndWait();

		// Handle the result
		if (result.isPresent() && result.get() == newGameButton) {
			clearBoard();
			enableAllButtons();
			resetGame();
		} else {
			// Exit the application or perform any other desired action
			Platform.exit();
		}
	}

	private void clearBoard() {
		for (Button[] row : buttons) {
			for (Button button : row) {
				button.setText("");
			}
		}
		playerTurn = true; 
		enableAllButtons();
	}

	private void resetGame() {
		roundsPlayed = 0;
		playerWins = 0;
		aiWins = 0;
		playerTurn = true;
		playerName = "Player";
		totalRounds = 1;
		gameStarted = false;
		disableAllButtons(); 
	}

	private void disableAllButtons() {
		for (Button[] row : buttons) {
			for (Button button : row) {
				button.setDisable(true);
			}
		}
	}

	private void enableAllButtons() {
		for (Button[] row : buttons) {
			for (Button button : row) {
				button.setDisable(false);
			}
		}
	}

	private boolean isBoardFull() {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (buttons[i][j].getText().isEmpty()) {
					return false; // If any cell is empty -> the board is not full
				}
			}
		}
		return true; // All cells are filled -> board is full
	}

	private boolean checkWin(String symbol) {
		// Check rows, columns, and diagonals for a win for the given symbol
		for (int i = 0; i < 3; i++) {
			// Check rows
			if (buttons[i][0].getText().equals(symbol) && buttons[i][1].getText().equals(symbol)
					&& buttons[i][2].getText().equals(symbol)) {
				return true;
			}
			// Check columns
			if (buttons[0][i].getText().equals(symbol) && buttons[1][i].getText().equals(symbol)
					&& buttons[2][i].getText().equals(symbol)) {
				return true;
			}
		}
		// Check diagonals
		if (buttons[0][0].getText().equals(symbol) && buttons[1][1].getText().equals(symbol)
				&& buttons[2][2].getText().equals(symbol)) {
			return true;
		}
		if (buttons[0][2].getText().equals(symbol) && buttons[1][1].getText().equals(symbol)
				&& buttons[2][0].getText().equals(symbol)) {
			return true;
		}
		return false; // No win found
	}

	private void showAlert(String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Attention");
		alert.setHeaderText(message);
		alert.showAndWait();
	}
}
