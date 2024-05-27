package com.example;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SudokuGameController {
    private int easy = 20;
    private int medium = 30;
    private int hard = 40;
    private final IntegerProperty currentDifficulty = new SimpleIntegerProperty(easy);
    private Timeline timeline;
    private int seconds;
    private Cell selectedCell;
    private Cell[][] cells = new Cell[SudokuConstants.GRID_SIZE][SudokuConstants.GRID_SIZE];
    private Puzzle puzzle = new Puzzle();
    
    @FXML private Label timeLabel;
    @FXML private ImageView top;
    @FXML private GridPane gridPane;
    @FXML private AnchorPane anchorPane;

    // Number buttons
    @FXML private Button button_one;
    @FXML private Button button_two;
    @FXML private Button button_three;
    @FXML private Button button_four;
    @FXML private Button button_five;
    @FXML private Button button_six;
    @FXML private Button button_seven;
    @FXML private Button button_eight;
    @FXML private Button button_nine;

    // Update button
    @FXML private Button update;
    // Hint button
    @FXML private Button hint;
    // Note button
    @FXML private Button note;


    public void initialize() {
        // Setting up number button handlers
        button_one.setOnAction(event -> handleNumberButtonClick(1));
        button_two.setOnAction(event -> handleNumberButtonClick(2));
        button_three.setOnAction(event -> handleNumberButtonClick(3));
        button_four.setOnAction(event -> handleNumberButtonClick(4));
        button_five.setOnAction(event -> handleNumberButtonClick(5));
        button_six.setOnAction(event -> handleNumberButtonClick(6));
        button_seven.setOnAction(event -> handleNumberButtonClick(7));
        button_eight.setOnAction(event -> handleNumberButtonClick(8));
        button_nine.setOnAction(event -> handleNumberButtonClick(9));

        gridPane.setAlignment(Pos.CENTER);

        for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
                cells[row][col] = new Cell();
                cells[row][col].setPosition(row, col);
                cells[row][col].setEditable(true);
                gridPane.add(cells[row][col], col, row);

                // Create final copies of row and col
                final int finalRow = row;
                final int finalCol = col;

                // Set focus listener to update selectedCell
                cells[finalRow][finalCol].focusedProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        selectedCell = cells[finalRow][finalCol];
                    }
                });
            }
        }

        gridPane.addEventFilter(KeyEvent.KEY_PRESSED, this::handleArrowNavigation);

        CellInputListener listener = new CellInputListener();

        for (int row = 0; row < cells.length; row++) {
            for (int col = 0; col < cells[row].length; col++) {
                if (cells[row][col].isEditable()) {
                    cells[row][col].setOnAction(listener);
                }
            }
        }

        newGame(currentDifficulty.get());
        update.setOnAction(event -> newGame(currentDifficulty.get()));
        hint.setOnAction(event -> handleHintButtonClick()); // Add hint button handler
        startTimer();
    }

    public void newGame(int difficulty) {
        puzzle.newPuzzle(difficulty);
        
        for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
                cells[row][col].setNumberAndStatus(0, false);
                cells[row][col].setNumberAndStatus(puzzle.numbers[row][col], puzzle.isGiven[row][col]);
                cells[row][col].setStyle("-fx-text-fill: black;");
            }
        }

        // Reset the timer
        seconds = 0;
        timeLabel.setText("00:00:00");
        if (timeline != null) {
            timeline.stop();
        }
        startTimer();

        System.out.println("A new game has been created");
    }

    public boolean isSolved() {
        for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
                if (cells[row][col].getStatus() == CellStatus.TO_GUESS || cells[row][col].getStatus() == CellStatus.WRONG_GUESS) {
                    return false;
                }
            }
        }
        return true;
    }

    private void handleArrowNavigation(KeyEvent event) {
        Node source = (Node) event.getSource();
        Node focused = source.getScene().getFocusOwner();
        if (event.getCode().isArrowKey() && focused.getParent() == source) {
            int row = GridPane.getRowIndex(focused);
            int col = GridPane.getColumnIndex(focused);
            switch (event.getCode()) {
                case LEFT:
                    cells[row][Math.max(0, col - 1)].requestFocus();
                    break;
                case RIGHT:
                    cells[row][Math.min(SudokuConstants.GRID_SIZE - 1, col + 1)].requestFocus();
                    break;
                case UP:
                    cells[Math.max(0, row - 1)][col].requestFocus();
                    break;
                case DOWN:
                    cells[Math.min(SudokuConstants.GRID_SIZE - 1, row + 1)][col].requestFocus();
                    break;
                default:
                    break;
            }
            event.consume();
        }
    }


    private class CellInputListener implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            Cell sourceCell = (Cell) event.getSource();
            selectedCell = sourceCell;
            String text = sourceCell.getText();

            try {
                int numberIn = Integer.parseInt(text);
                
                if (numberIn == sourceCell.getNumber()) {
                    sourceCell.setStatus(CellStatus.CORRECT_GUESS);
                    sourceCell.setStyle("-fx-text-fill: blue;");
                    sourceCell.setEditable(false);
                    System.out.println("Correct guess");
                } else {
                    sourceCell.setStatus(CellStatus.WRONG_GUESS);
                    sourceCell.setStyle("-fx-text-fill: red;");
                    System.out.println("Wrong guess");
                }

                System.out.println("You entered " + numberIn);

                if (isSolved()) {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Congratulations!");
                    alert.setHeaderText(null);
                    alert.setContentText("Puzzle solved!");
                    alert.showAndWait();
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid input: " + text);
            }
        }
    }

    private void handleNumberButtonClick(int number) {
        if (selectedCell != null && selectedCell.isEditable()) {
            selectedCell.setText(String.valueOf(number));
            selectedCell.fireEvent(new ActionEvent());
        }
    }

    private void handleHintButtonClick() {
        List<Cell> emptyCells = new ArrayList<>();
        for (int row = 0; row < SudokuConstants.GRID_SIZE; row++) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; col++) {
                if (cells[row][col].isEditable() && cells[row][col].getText().isEmpty()) {
                    emptyCells.add(cells[row][col]);
                }
            }
        }

        if (!emptyCells.isEmpty()) {
            Random random = new Random();
            Cell hintCell = emptyCells.get(random.nextInt(emptyCells.size()));
            int row = hintCell.getRow();
            int col = hintCell.getCol();
            hintCell.setText(String.valueOf(puzzle.numbers[row][col]));
            hintCell.setStatus(CellStatus.CORRECT_GUESS);
            System.out.println("hint has been activated");
            hintCell.setStyle("-fx-text-fill: green;");
            hintCell.setEditable(false);
        }
    }


    private void startTimer() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            seconds++;
            int hours = seconds / 3600;
            int minutes = (seconds % 3600) / 60;
            int secs = seconds % 60;
            timeLabel.setText(String.format("%02d:%02d:%02d", hours, minutes, secs));
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
}

