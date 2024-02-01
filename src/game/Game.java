package game;
import java.util.ArrayList;
import java.util.List;

public class Game {

    private Board board;
    private List<GameListener> listeners;
    private long startTime;

    public Game(BoardType boardType) {
        this.board = new Board(this, boardType);
        this.listeners = new ArrayList<GameListener>();
        this.startTime = System.currentTimeMillis();
    }

    public void addGameListener(GameListener listener) {
        listeners.add(listener);
    }

    public Board getBoard() {
        return board;
    }

    public long getStartTime() {
        return startTime;
    }

    public void bombClicked(Cell cell) {
        for(GameListener listener : listeners) {
            listener.bombClicked(cell);
        }
    }

    public void boardCleared() {
        for(GameListener listener : listeners) {
            listener.boardCleared();
        }
    }

}
