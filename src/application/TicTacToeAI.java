package application;

import java.util.Optional;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class TicTacToeAI extends Application {

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
	private Label aiEvaluationLabel = new Label("AI's Move Evaluations:\n");

	@Override
	public void start(Stage primaryStage) {
		GridPane gridPane = createGridPane();
		Scene scene = new Scene(gridPane, 480, 600);
		primaryStage.setTitle("Tic Tac Toe Player VS AI");
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
				button.setDisable(true); // Initially disable the buttons
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
			playerScoreLabel.setText(playerName + " Score: "); // Update player name in the label

			try {
				totalRounds = Integer.parseInt(roundsField.getText());

				// Update round label before showing the alert
				roundLabel.setText("Round: 1 / " + totalRounds);

				showAlert("Game started!\nPlayer: " + playerName + "\nRounds: " + totalRounds);

				// Determine the starting player for the current round
				playerTurn = (turnToggleGroup.getSelectedToggle() == playerRadioButton);
				gameStarted = true; // Set gameStarted to true

				// Enable buttons for the player's turn
				enableAllButtons();

				if (!playerTurn) {
					makeAIMove();
				}
			} catch (NumberFormatException ex) {
				showAlert("Please enter a valid number for rounds!");
				return;
			}
		});

		// Set the text fields to span multiple columns
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
		gridPane.add(aiEvaluationLabel, 3, 4, 1, 2);

		return gridPane;
	}

	private void makeAIMove() {
		int[] bestMove = makeBestMove();
		buttons[bestMove[0]][bestMove[1]].setText("O");
		displayAIEvaluation();

		if (checkWin("O")) {
			announceWinner("AI");
		} else if (isBoardFull()) {
			announceWinner("Draw");
		} else {
			playerTurn = true;
		}
	}

	private void displayAIEvaluation() {
		StringBuilder evaluationText = new StringBuilder("AI's Moves :\n");

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (buttons[i][j].getText().isEmpty()) {
					buttons[i][j].setText("O");

					int evaluation = minimax(false);

					buttons[i][j].setText("");

					String evaluationSymbol = getEvaluationSymbol(evaluation);
					evaluationText.append(evaluationSymbol).append(" ");

					// Print a newline after the second column
					if (j == 1) {
						evaluationText.append("| ");
					}
				}
			}

			evaluationText.append("\n");
			// Print a separator line after the first row
			if (i == 0 || i == 1) {
				evaluationText.append("------------------\n");
			}
		}

		aiEvaluationLabel.setText(evaluationText.toString());
	}

	private String getEvaluationSymbol(int evaluation) {
		if (evaluation == 1) {
			return "Win";
		} else if (evaluation == 0) {
			return "Draw";
		} else {
			return "Lose";
		}
	}

	private int[] makeBestMove() {
		int[] bestMove = new int[] { -1, -1 };
		int bestScore = Integer.MIN_VALUE;

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (buttons[i][j].getText().isEmpty()) {
					buttons[i][j].setText("O");
					int score = minimax(false);
					buttons[i][j].setText("");

					if (score > bestScore) {
						bestScore = score;
						bestMove[0] = i;
						bestMove[1] = j;
					}
				}
			}
		}
		return bestMove;
	}

	private int minimax(boolean maximizingPlayer) {
		if (checkWin("X")) {
			return -1;
		} else if (checkWin("O")) {
			return 1;
		} else if (isBoardFull()) {
			return 0;
		}

		if (maximizingPlayer) {
			int bestScore = Integer.MIN_VALUE;
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					if (buttons[i][j].getText().isEmpty()) {
						buttons[i][j].setText("O");
						int score = minimax(false);
						buttons[i][j].setText("");
						bestScore = Math.max(score, bestScore);
					}
				}
			}
			return bestScore;
		} else {
			int bestScore = Integer.MAX_VALUE;
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					if (buttons[i][j].getText().isEmpty()) {
						buttons[i][j].setText("X");
						int score = minimax(true);
						buttons[i][j].setText("");
						bestScore = Math.min(score, bestScore);
					}
				}
			}
			return bestScore;
		}
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

		// Update player and AI score labels
		playerScoreLabel.setText("Player Score: " + playerWins);
		aiScoreLabel.setText("AI Score: " + aiWins);

		// Update round label
		roundLabel.setText("Round: " + roundsPlayed + " / " + totalRounds);

		// Set the event handler for when the alert is hidden
		alert.setOnHidden(event -> {
			if (roundsPlayed < totalRounds) {
				clearBoard();
				enableAllButtons(); // Enable buttons for the next round

				// Update round label for the next round
				roundLabel.setText("Round: " + (roundsPlayed + 1) + " / " + totalRounds);
			} else {
				displayFinalResults();
				resetGame(); // Reset the game state after displaying final results
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
				button.setText(""); // Clear button text
			}
		}
		playerTurn = true; // Reset to player's turn

		// Enable buttons for the next round
		enableAllButtons();
	}

	private void resetGame() {
		roundsPlayed = 0;
		playerWins = 0;
		aiWins = 0;
		playerTurn = true;
		playerName = "Player";
		totalRounds = 1;
		gameStarted = false; // Reset gameStarted to false
		disableAllButtons(); // Disable buttons when the game is reset
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
					return false; // If any cell is empty, the board is not full
				}
			}
		}
		return true; // All cells are filled, board is full
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
