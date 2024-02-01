package main;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import fileIO.SpriteLoader;
import game.BoardType;
import game.Cell;
import game.Game;
import game.GameListener;

public class Main implements GameListener {

    public static BufferedImage[] sprites;

    private JFrame frame;
    private Game game;

    private BoardType currentBoardType = BoardType.BEGINNER;

    public static void main(String[] args) throws Exception {
        new Main();
    }

    public Main() {
        frame = new JFrame("Minesweeper");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Setup the menu bar.
        JMenuBar menuBar = new JMenuBar();

        // Setup the action listener for every menu button.
        ActionListener actionListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JMenuItem menuItem = (JMenuItem) e.getSource();
                switch(menuItem.getName()) {
                case "menuGameItemNewGame":
                    newGame(currentBoardType);
                    break;
                case "menuGameItemBeginner":
                    Component[] components = menuItem.getParent().getComponents();
                    for(Component component : components) {
                        if(component.getName() != null && (component.getName().equals("menuGameItemIntermediate") || component.getName().equals("menuGameItemExpert"))) {
                            JCheckBoxMenuItem item = (JCheckBoxMenuItem) component;
                            item.setSelected(false);
                        }
                    }
                    menuItem.setSelected(true);
                    if(currentBoardType != BoardType.BEGINNER) {
                        currentBoardType = BoardType.BEGINNER;
                        newGame(currentBoardType);
                        frame.setLocationRelativeTo(null);
                    }
                    break;
                case "menuGameItemIntermediate":
                    components = menuItem.getParent().getComponents();
                    for(Component component : components) {
                        if(component.getName() != null && (component.getName().equals("menuGameItemBeginner") || component.getName().equals("menuGameItemExpert"))) {
                            JCheckBoxMenuItem item = (JCheckBoxMenuItem) component;
                            item.setSelected(false);
                        }
                    }
                    menuItem.setSelected(true);
                    if(currentBoardType != BoardType.INTERMEDIATE) {
                        currentBoardType = BoardType.INTERMEDIATE;
                        newGame(currentBoardType);
                        frame.setLocationRelativeTo(null);
                    }
                    break;
                case "menuGameItemExpert":
                    components = menuItem.getParent().getComponents();
                    for(Component component : components) {
                        if(component.getName() != null && (component.getName().equals("menuGameItemBeginner") || component.getName().equals("menuGameItemIntermediate"))) {
                            JCheckBoxMenuItem item = (JCheckBoxMenuItem) component;
                            item.setSelected(false);
                        }
                    }
                    menuItem.setSelected(true);
                    if(currentBoardType != BoardType.EXPERT) {
                        currentBoardType = BoardType.EXPERT;
                        newGame(currentBoardType);
                        frame.setLocationRelativeTo(null);
                    }
                    break;
                case "menuGameItemShowFlags":
                    Preferences.setShowFlags(menuItem.isSelected());
                    game.getBoard().repaint();
                    break;
                case "menuGameItemSafeMode":
                    Preferences.setSafeMode(menuItem.isSelected());
                    break;
                case "menuGameItemBestTimes":
                    // TODO: Show best times leaderboard.
                    break;
                case "menuGameItemExit":
                    System.exit(0);
                    break;
                }
            }
            
        };

        JMenu menuGame = new JMenu("Game");

        JMenuItem menuGameItemNewGame = new JMenuItem("New Game");
        menuGameItemNewGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        menuGameItemNewGame.setName("menuGameItemNewGame");
        menuGameItemNewGame.addActionListener(actionListener);
        menuGame.add(menuGameItemNewGame);

        menuGame.addSeparator();

        JCheckBoxMenuItem menuGameItemBeginner = new JCheckBoxMenuItem("Beginner");
        menuGameItemBeginner.setName("menuGameItemBeginner");
        menuGameItemBeginner.setSelected(true);
        menuGameItemBeginner.addActionListener(actionListener);
        menuGame.add(menuGameItemBeginner);

        JCheckBoxMenuItem menuGameItemIntermediate = new JCheckBoxMenuItem("Intermediate");
        menuGameItemIntermediate.setName("menuGameItemIntermediate");
        menuGameItemIntermediate.addActionListener(actionListener);
        menuGame.add(menuGameItemIntermediate);

        JCheckBoxMenuItem menuGameItemExpert = new JCheckBoxMenuItem("Expert");
        menuGameItemExpert.setName("menuGameItemExpert");
        menuGameItemExpert.addActionListener(actionListener);
        menuGame.add(menuGameItemExpert);

        menuGame.addSeparator();

        JCheckBoxMenuItem menuGameItemShowFlags = new JCheckBoxMenuItem("Show Flags");
        menuGameItemShowFlags.setName("menuGameItemShowFlags");
        menuGameItemShowFlags.setSelected(true);
        menuGameItemShowFlags.addActionListener(actionListener);
        menuGame.add(menuGameItemShowFlags);

        JCheckBoxMenuItem menuGameItemSafeMode = new JCheckBoxMenuItem("Safe Mode");
        menuGameItemSafeMode.setName("menuGameItemSafeMode");
        menuGameItemSafeMode.addActionListener(actionListener);
        menuGame.add(menuGameItemSafeMode);

        menuGame.addSeparator();

        JMenuItem menuGameItemBestTimes = new JMenuItem("Best Times");
        menuGameItemBestTimes.setName("menuGameItemBestTimes");
        menuGameItemBestTimes.addActionListener(actionListener);
        menuGame.add(menuGameItemBestTimes);

        menuGame.addSeparator();

        JMenuItem menuGameItemExit = new JMenuItem("Exit");
        menuGameItemExit.setName("menuGameItemExit");
        menuGameItemExit.addActionListener(actionListener);
        menuGame.add(menuGameItemExit);

        menuBar.add(menuGame);

        JMenu menuHelp = new JMenu("Help");
        menuBar.add(menuHelp);
        frame.setJMenuBar(menuBar);

        // Load the sprites and save them to the Icon[].
        sprites = SpriteLoader.loadSpritesheet("spritesheet.png");

        newGame(currentBoardType);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void newGame(BoardType boardType) {
        // Start a new game.
        if(game != null) frame.remove(game.getBoard());

        game = new Game(boardType);
        game.addGameListener(this);
    
        frame.add(game.getBoard());
        frame.pack();
    }

    @Override
    public void bombClicked(Cell cell) {
        System.out.printf("Bomb was clicked at (%d, %d)\n", cell.getGridX(), cell.getGridY());

        int choice = JOptionPane.showConfirmDialog(frame, "You've hit a bomb! Would you like to start a new game?", "You hit a bomb!", JOptionPane.YES_NO_OPTION);
        if(choice == JOptionPane.YES_OPTION) {
            newGame(currentBoardType);
        } else {
            // Continue playing the same game and undo the last click.
            game.getBoard().hideBombLocations();
        }
    }

    @Override
    public void boardCleared() {
        // Calculate the time the game took
        long gameTimeMillis = System.currentTimeMillis() - game.getStartTime();
        long seconds = gameTimeMillis / 1000;
        long minutes = seconds / 60;
        gameTimeMillis -= seconds * 1000;
        seconds -= minutes * 60;

        int choice = JOptionPane.showConfirmDialog(frame, String.format("You cleared the board in %02d:%02d.%03d! Would you like to start a new game?", minutes, seconds, gameTimeMillis),
                                                 "You win!", JOptionPane.YES_NO_OPTION);
        if(choice == JOptionPane.YES_OPTION) {
            newGame(currentBoardType);
        }
    }
}
