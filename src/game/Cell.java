package game;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import main.Main;
import main.Preferences;

public class Cell extends JButton {

    private Board board;
    private Cell cell;

    private int gridX, gridY;
    private boolean isBomb = false;
    private boolean isCovered = true;
    private boolean isIncorrectFlag = false;
    private boolean isFlagged = false;
    private int bombNeighbors = 0;

    public static int SPRITE_COVERED = 0;
    public static int SPRITE_FLAGGED = 1;
    public static int SPRITE_UNCOVERED_0 = 2;
    public static int SPRITE_UNCOVERED_1 = 3;
    public static int SPRITE_UNCOVERED_2 = 4;
    public static int SPRITE_UNCOVERED_3 = 5;
    public static int SPRITE_UNCOVERED_4 = 6;
    public static int SPRITE_UNCOVERED_5 = 7;
    public static int SPRITE_UNCOVERED_6 = 8;
    public static int SPRITE_UNCOVERED_7 = 9;
    public static int SPRITE_UNCOVERED_8 = 10;
    public static int SPRITE_BOMB = 11;
    public static int SPRITE_FLAGGED_INCORRECT = 12;

    public Cell(Board board, int gridX, int gridY, boolean isBomb) {
        this.board = board;
        this.gridX = gridX;
        this.gridY = gridY;
        this.isBomb = isBomb;
        this.isCovered = true;

        this.cell = this;

        // Setup the JButton.
        setFocusable(false);

        addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // Notify the board that this cell was pressed.
                board.notifyPressed(cell);

                // Ensure the cell is not already flagged.
                if(isFlagged) return;

                // Uncover this cell.
                uncover(board.getCells());

                // Check if every cell that is not a bomb has been uncovered.
                boolean cellCovered = false;

                for(int y = 0; y < board.getCells().length; y++) {
                    for(int x = 0; x < board.getCells()[y].length; x++) {
                        Cell currentCell = board.getCells()[y][x];

                        if(!currentCell.isBomb() && currentCell.isCovered()) cellCovered = true;
                    }
                }

                // If no non-bomb cells are still covered, notify listeners that the board has been cleared.
                if(!cellCovered) board.getGame().boardCleared();

                System.out.println("Every non-bomb cell is uncovered: " + !cellCovered);
            }
            
        });

        addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {}

            @Override
            public void mousePressed(MouseEvent e) {
                if(SwingUtilities.isRightMouseButton(e)) {
                    // Check if the cell is still covered.
                    if(isCovered) {
                        // Set the cell to be flagged if not, or unflagged if it is.
                        isFlagged = !isFlagged;

                        // Repaint the component to update the graphics faster.
                        repaint();
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
            
        });
    }

    @Override
    public void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;

        BufferedImage image = null;

        if(isIncorrectFlag) {
            image = Main.sprites[SPRITE_FLAGGED_INCORRECT];
        } else if(isFlagged && Preferences.shouldShowFlags()) {
            image = Main.sprites[SPRITE_FLAGGED];
        } else if(isCovered) {
            image = Main.sprites[SPRITE_COVERED];
        } else if(isBomb) {
            image = Main.sprites[SPRITE_BOMB];
        } else {
            switch(bombNeighbors) {
            case 0:
                image = Main.sprites[SPRITE_UNCOVERED_0];
                break;
            case 1:
                image = Main.sprites[SPRITE_UNCOVERED_1];
                break;
            case 2:
                image = Main.sprites[SPRITE_UNCOVERED_2];
                break;
            case 3:
                image = Main.sprites[SPRITE_UNCOVERED_3];
                break;
            case 4:
                image = Main.sprites[SPRITE_UNCOVERED_4];
                break;
            case 5:
                image = Main.sprites[SPRITE_UNCOVERED_5];
                break;
            case 6:
                image = Main.sprites[SPRITE_UNCOVERED_6];
                break;
            case 7:
                image = Main.sprites[SPRITE_UNCOVERED_7];
                break;
            case 8:
                image = Main.sprites[SPRITE_UNCOVERED_8];
                break;
            default:
                image = Main.sprites[SPRITE_UNCOVERED_0];
            }
        }

        g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
    }

    @Override
    public void paintBorder(Graphics graphics) {}

    public void calculateBombNeighbors(Cell[][] cells) {
        int bombCount = 0;

        // For each neighbor in a 3x3 array centered around this cell.
        for(int y = gridY - 1; y <= gridY + 1; y++) {
            for(int x = gridX - 1; x <= gridX + 1; x++) {
                // Ensure the cell is within the board.
                if(x < 0 || x >= board.getGridWidth() || y < 0 || y >= board.getGridHeight()) continue;

                // Ensure this is not the current cell.
                if(x == gridX && y == gridY) continue;

                // Add one to the bomb counter if this cell is a bomb.
                if(cells[y][x].isBomb()) bombCount++;
            }
        }

        bombNeighbors = bombCount;
    }

    public void uncover(Cell[][] cells) {
        isCovered = false;
        // Change the look of the bomb.
        System.out.printf("Cell [%d, %d] has %d neighbors.\n", gridX, gridY, bombNeighbors);

        if(isBomb) {
            // End game, blow up.
            System.out.println("Bomb, dead.");

            board.getGame().bombClicked(this);

            // TODO: Make this configurable as a difficulty setting?
            // Set the cell to be enabled again to allow the player to click the same bomb twice.
            setEnabled(true);

            return;
        }

        // Disable the cell so it cannot be clicked again.
        setEnabled(false);

        // Check if this cell has no bomb neighbors.
        if(bombNeighbors == 0) {
            // Uncover all neighbors.
            for(int y = gridY - 1; y <= gridY + 1; y++) {
                for(int x = gridX - 1; x <= gridX + 1; x++) {
                    // Ensure the cell is within the board.
                    if(x < 0 || x >= board.getGridWidth() || y < 0 || y >= board.getGridHeight()) continue;

                    // Ensure this is not the current cell.
                    if(x == gridX && y == gridY) continue;

                    // If the neighbor cell is already uncovered, continue.
                    if(!cells[y][x].isCovered()) continue;
    
                    // Uncover the neighbor cell.
                    cells[y][x].uncover(cells);
                }
            }
        }
    }

    public boolean isBomb() {
        return isBomb;
    }

    public void setBomb(boolean isBomb) {
        this.isBomb = isBomb;
    }

    public boolean isCovered() {
        return isCovered;
    }

    public void setCovered(boolean isCovered) {
        this.isCovered = isCovered;
    }

    public void setIncorrectFlag(boolean isIncorrectFlag) {
        this.isIncorrectFlag = isIncorrectFlag;
    }

    public boolean isFlagged() {
        return isFlagged;
    }

    public int getBombNeighbors() {
        return bombNeighbors;
    }

    public int getGridX() {
        return gridX;
    }

    public int getGridY() {
        return gridY;
    }

}
