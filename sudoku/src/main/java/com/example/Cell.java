

package com.example;
import javafx.scene.control.TextField;

public class Cell extends TextField {
    private int number;
    private CellStatus status;
    private int row, col;

    public Cell() {
        super();
        getStyleClass().add("text-field-style");

        textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d") && !newValue.isEmpty()) {
                setText(oldValue);
            }
        });
    }

    public void setNumberAndStatus(int number, boolean isGiven) {
        this.number = number;
        this.status = isGiven ? CellStatus.GIVEN : CellStatus.TO_GUESS;
        setText(isGiven ? String.valueOf(number) : "");
        setEditable(!isGiven);
    }

    public int getNumber() {
        return number;
    }

    public CellStatus getStatus() {
        return status;
    }

    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public void setStatus(CellStatus status) {
        this.status = status;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}

