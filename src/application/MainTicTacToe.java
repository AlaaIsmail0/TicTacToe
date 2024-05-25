package application;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MainTicTacToe extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        List<String> gameOptions = new ArrayList<>();
        gameOptions.add("Tic Tac Toe Two Players");
        gameOptions.add("Tic Tac Toe With AI");
        gameOptions.add("Tic Tac Toe With Random AI");

        ChoiceDialog<String> dialog = new ChoiceDialog<>(gameOptions.get(0), gameOptions);
        dialog.setTitle("Tic Tac Toe Game Selection");
        dialog.setHeaderText("Choose a Tic Tac Toe game to play:");
        dialog.setContentText("Select:");

        // Custom icon for the dialog
        Image icon = new Image(getClass().getResourceAsStream("/application/icon.png"));
        dialog.setGraphic(new ImageView(icon));

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(selectedGame -> {
            switch (selectedGame) {
                case "Tic Tac Toe Two Players":
                    launchTicTacToe2Players();
                    break;
                case "Tic Tac Toe With AI":
                    launchTicTacToeAI();
                    break;
                case "Tic Tac Toe With Random AI":
                    launchTicTacToeRandom();
                    break;
                default:
                    showAlert("Invalid game selection.");
            }
        });
    }

    private void launchTicTacToeAI() {
        TicTacToeAI ticTacToeAI = new TicTacToeAI();
        ticTacToeAI.start(new Stage());
    }

    private void launchTicTacToe2Players() {
        TicTacToe2Players ticTacToe2Players = new TicTacToe2Players();
        ticTacToe2Players.start(new Stage());
    }

    private void launchTicTacToeRandom() {
        TicTacToeRandom ticTacToeRandom = new TicTacToeRandom();
        ticTacToeRandom.start(new Stage());
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Custom icon for the alert
        Image icon = new Image(getClass().getResourceAsStream("/application/icon.png"));
        ImageView imageView = new ImageView(icon);
        imageView.setFitWidth(48);
        imageView.setFitHeight(48);
        alert.setGraphic(imageView);

        // Custom buttons for the alert
        ButtonType okayButton = new ButtonType("Okay");
        alert.getButtonTypes().setAll(okayButton);

        alert.showAndWait();
    }
}
