package game;
public interface GameListener {
    
    /**
     * Called when a bomb cell is clicked on.
     * @param cell the bomb cell that was clicked on
     */
    public void bombClicked(Cell cell);

    /**
     * Called when every cell that is not a bomb has been uncovered.
     */
    public void boardCleared();

}
