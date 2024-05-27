package com.example;

public class Puzzle {
    int[][] numbers = new int[SudokuConstants.GRID_SIZE][SudokuConstants.GRID_SIZE];
    boolean[][] isGiven = new boolean[SudokuConstants.GRID_SIZE][SudokuConstants.GRID_SIZE];

    // Generate a new puzzle using the Generator class
    public void newPuzzle(int cellsToGuess) {
        Generator generator = new Generator(SudokuConstants.GRID_SIZE);
        generator.fillValues();
        
        // Retrieve generated puzzle and update Puzzle instance
        this.numbers = generator.mat;
        for (int i = 0; i < SudokuConstants.GRID_SIZE; i++) {
            for (int j = 0; j < SudokuConstants.GRID_SIZE; j++) {
                if (this.numbers[i][j] != 0) {
                    this.isGiven[i][j] = true;
                }
            }
        }

        removeKDigits(cellsToGuess, this.numbers);
    }
 
    // Random generator
    int randomGenerator(int num)
    {
        return (int) Math.floor((Math.random()*num+1));
    }

    // Remove the K no. of digits to
    // complete game
    public void removeKDigits(int K, int[][] mat)
    {
        int count = K;
        while (count != 0)
        {
            int N = SudokuConstants.GRID_SIZE;
            int cellId = randomGenerator(N*N)-1;
 
            // System.out.println(cellId);
            // extract coordinates i and j
            int i = (cellId/N);
            int j = cellId%N;
            if (j != 0)
                j = j - 1;
 
            // System.out.println(i+" "+j);
            if (mat[i][j] != 0)
            {
                count--;
                this.isGiven[i][j] = false;
            }
        }
    }
}