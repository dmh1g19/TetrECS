package uk.ac.soton.comp1206.game;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.Pair;
import uk.ac.soton.comp1206.event.Multimedia;

/**
 * The Grid is a model which holds the state of a game board. It is made up of a
 * set of Integer values arranged in a 2D
 * arrow, with rows and columns.
 *
 * Each value inside the Grid is an IntegerProperty can be bound to enable
 * modification and display of the contents of
 * the grid.
 *
 * The Grid contains functions related to modifying the model, for example,
 * placing a piece inside the grid.
 *
 * The Grid should be linked to a GameBoard for it's display.
 */
public class Grid {

    private static final Logger logger = LogManager.getLogger(Grid.class);

    Multimedia sound = new Multimedia();

    /**
     * The number of columns in this grid
     */
    private final int cols;

    /**
     * The number of rows in this grid
     */
    private final int rows;

    /**
     * The grid is a 2D arrow with rows and columns of SimpleIntegerProperties.
     */
    private final SimpleIntegerProperty[][] grid;

    /**
     * Create a new Grid with the specified number of columns and rows and
     * initialise them
     * 
     * @param cols number of columns
     * @param rows number of rows
     */
    public Grid(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        // Create the grid itself
        grid = new SimpleIntegerProperty[cols][rows];

        // Add a SimpleIntegerProperty to every block in the grid
        for (var y = 0; y < rows; y++) {
            for (var x = 0; x < cols; x++) {
                grid[x][y] = new SimpleIntegerProperty(0);
            }
        }
    }

    /**
     * Get the Integer property contained inside the grid at a given row and column
     * index. Can be used for binding.
     * 
     * @param x column
     * @param y row
     * @return the IntegerProperty at the given x and y in this grid
     */
    public IntegerProperty getGridProperty(int x, int y) {
        return grid[x][y];
    }

    /**
     * Update the value at the given x and y index within the grid
     * 
     * @param x     column
     * @param y     row
     * @param value the new value
     */
    public void set(int x, int y, int value) {
        grid[x][y].set(value);
    }

    /**
     * Get the value represented at the given x and y index within the grid
     * 
     * @param x column
     * @param y row
     * @return the value
     */
    public int get(int x, int y) {
        try {
            // Get the value held in the property at the x and y index provided
            return grid[x][y].get();
        } catch (ArrayIndexOutOfBoundsException e) {
            // No such index
            return -1;
        }
    }

    /**
     * Get the number of columns in this game
     * 
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Get the number of rows in this game
     * 
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }

    // Check if location is valid for placement
    // TODO: doesnt wuite work fully
    public boolean canPlayPiece(GamePiece piece, int x, int y) {

        for (int i = 0; i < piece.getBlocks().length; i++) {
            for (int j = 0; j < piece.getBlocks()[i].length; j++) {

                if ((piece.getBlocks()[j][i] > 0 && get((x + j) - 1, (y + i) - 1) > 0)
                        && get((x + j) - 1, (y + i) - 1) != 16) {
                    logger.info("BLOCK OCCUPIED! Pick an available slot.");
                    sound.playSounds("fail.wav");

                    return false;
                }
            }
        }

        return true;
    }

    // Place piece onto the board
    public void playPiece(GamePiece piece, int x, int y) {

        logger.info("Clicked x:{}, y:{}", x, y);

        for (int i = 0; i < piece.getBlocks().length; i++) {
            for (int j = 0; j < piece.getBlocks()[i].length; j++) {

                if (piece.getBlocks()[j][i] >= 1) {
                    set((x + j) - 1, (y + i) - 1, piece.getValue());
                }
            }
        }
        sound.playSounds("place.wav");
    }

    public void afterPiece(Game gm) {
        Set<Integer> fullRows = new HashSet<>();
        Set<Integer> fullCols = new HashSet<>();

        // Find full rows (horizontal lines)
        for (int row = 0; row < getRows(); row++) {
            boolean isFull = true;
            for (int col = 0; col < getCols(); col++) {
                if (get(row, col) <= 0) {
                    isFull = false;
                    break;
                }
            }
            if (isFull) {
                fullRows.add(row);
                sound.playSounds("clear.wav");
            }
        }

        // Find full columns (vertical lines)
        for (int col = 0; col < getCols(); col++) {
            boolean isFull = true;
            for (int row = 0; row < getRows(); row++) {
                if (get(row, col) <= 0) {
                    isFull = false;
                    break;
                }
            }
            if (isFull) {
                fullCols.add(col);
                sound.playSounds("clear.wav");
            }
        }

        // Collect unique cells to clear (from full rows or columns)
        Set<Pair<Integer, Integer>> clearedCells = new HashSet<>();
        for (int row : fullRows) {
            for (int col = 0; col < getCols(); col++) {
                clearedCells.add(new Pair<>(row, col));
            }
        }
        for (int col : fullCols) {
            for (int row = 0; row < getRows(); row++) {
                clearedCells.add(new Pair<>(row, col));
            }
        }

        int numOfLines = fullRows.size() + fullCols.size();
        int numOfBlocks = clearedCells.size();

        // Update multiplier
        if (numOfLines > 0) {
            gm.setMultipliyer(1); // Could be enhanced to use numOfLines
        } else {
            gm.resetMultipliyer(0);
        }

        // Update score
        gm.score(numOfBlocks, numOfLines);

        logger.info("Current level:{}", gm.getLevel());
        logger.info("Num of lines cleared:{}", numOfLines);
        logger.info("Total blocks cleared:{}", numOfBlocks);
        logger.info("Current multipliyer:{}", gm.getMultipliyer());
        logger.info("Current score:{}", gm.getScore());

        // Clear the blocks
        for (Pair<Integer, Integer> cor : clearedCells) {
            gm.receiveClearedBlocks(cor);
        }
    }
}