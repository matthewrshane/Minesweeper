package game;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Random;

import javax.swing.JPanel;

public class Board extends JPanel {

    private final int cellWidth = 40;
    private final int cellHeight = 40;
    
    private final Game game;
    private final int gridWidth, gridHeight;
    private final int numberOfBombs;

    private Cell[][] cells;
    private boolean gameStarted = false;

    public Board(Game game, BoardType boardType) {
        this.game = game;

        switch(boardType) {
        default:
        case BEGINNER:
            gridWidth = 8;
            gridHeight = 8;
            numberOfBombs = 10;
            break;
        case INTERMEDIATE:
            gridWidth = 16;
            gridHeight = 16;
            numberOfBombs = 40;
            break;
        case EXPERT:
            gridWidth = 30;
            gridHeight = 16;
            numberOfBombs = 99;
            break;
        }

        GridLayout layout = new GridLayout(gridHeight, gridWidth, 0, 0);
        setLayout(layout);

        cells = new Cell[gridHeight][gridWidth];

        for(int y = 0; y < gridHeight; y++) {
            for(int x = 0; x < gridWidth; x++) {
                Cell cell = new Cell(this, x, y, false);

                // Set up cell as JButton.
                cell.setSize(new Dimension(cellWidth, cellHeight));
                cell.setMinimumSize(new Dimension(cellWidth, cellHeight));
                cell.setMaximumSize(new Dimension(cellWidth, cellHeight));
                cell.setPreferredSize(new Dimension(cellWidth, cellHeight));

                // Add the cell to the JPanel.
                add(cell);

                // Add the cell to the cells array.
                cells[y][x] = cell;
            }
        }
    }

    private int[] shuffle(int[] input) {
        // Store the length of the array.
        int n = input.length;

        // Hard copy the input array.
        int[] array = new int[n];
        for(int i = 0; i < input.length; i++) {
            array[i] = input[i];
        }

        // Define a temp value for swapping values in the array.
        int temp = 0;

        // Define a Random object.
        Random random = new Random();

        // Shuffle using the Fisher-Yates method.
        for(int i = 0; i < n - 2; i++) {
            // Get a random int in the range [i, n).
            int j = random.nextInt(i, n);

            // Swap array[i] and array[j].
            temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }

        // Return the shuffled array.
        return array;
    }

    public void notifyPressed(Cell cell) {
        int gridX = cell.getGridX();
        int gridY = cell.getGridY();

        if(gameStarted == false) {
            gameStarted = true;
            
            // Shuffle array and set bombs.
            int[] indices = new int[gridWidth * gridHeight - 1];

            int cellIndex = (gridY * gridWidth) + gridX;

            for(int i = 0; i < indices.length; i++) {
                if(i < cellIndex) indices[i] = i;
                else indices[i] = i + 1;
            }

            int[] bombs = shuffle(indices);

            for(int i = 0; i < numberOfBombs; i++) {
                int cellX = bombs[i] % gridWidth;
                int cellY = (bombs[i] - cellX) / gridWidth;

                cells[cellY][cellX].setBomb(true);
            }

            // Populate cells with their neighbor values.
            for(int y = 0; y < gridHeight; y++) {
                for(int x = 0; x < gridWidth; x++) {
                    cells[y][x].calculateBombNeighbors(cells);
                }
            }

            // Start timer, etc.
        } else {
            // Game has already started.
            if(cell.isBomb()) {
                // Game has ended, show all bomb locations.
                showBombLocations();
            }
        }
    }

    public void showBombLocations() {
        for(int y = 0; y < cells.length; y++) {
            for(int x = 0; x < cells[y].length; x++) {
                Cell currentCell = cells[y][x];

                if(currentCell.isBomb() && !currentCell.isFlagged()) {
                    currentCell.setCovered(false);
                } else if(!currentCell.isBomb() && currentCell.isFlagged()) {
                    currentCell.setIncorrectFlag(true);
                }
            }
        }
        repaint();
    }

    public void hideBombLocations() {
        for(int y = 0; y < cells.length; y++) {
            for(int x = 0; x < cells[y].length; x++) {
                Cell currentCell = cells[y][x];

                if(currentCell.isBomb() && !currentCell.isFlagged()) {
                    currentCell.setCovered(true);
                } else if(!currentCell.isBomb() && currentCell.isFlagged()) {
                    currentCell.setIncorrectFlag(false);
                }
            }
        }
        repaint();
    }

    public Game getGame() {
        return game;
    }

    public Cell[][] getCells() {
        return cells;
    }

    public int getGridWidth() {
        return gridWidth;
    }

    public int getGridHeight() {
        return gridHeight;
    }

}
