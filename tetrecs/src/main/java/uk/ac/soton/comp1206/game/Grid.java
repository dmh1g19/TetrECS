package uk.ac.soton.comp1206.game;

import javafx.animation.FadeTransition;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.Duration;
import javafx.util.Pair;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.event.Multimedia;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Grid is a model which holds the state of a game board. It is made up of a set of Integer values arranged in a 2D
 * arrow, with rows and columns.
 *
 * Each value inside the Grid is an IntegerProperty can be bound to enable modification and display of the contents of
 * the grid.
 *
 * The Grid contains functions related to modifying the model, for example, placing a piece inside the grid.
 *
 * The Grid should be linked to a GameBoard for it's display.
 */
public class Grid {

    private static final Logger logger = LogManager.getLogger(Grid.class);

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
     * Create a new Grid with the specified number of columns and rows and initialise them
     * @param cols number of columns
     * @param rows number of rows
     */
    public Grid(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        //Create the grid itself
        grid = new SimpleIntegerProperty[cols][rows];

        //Add a SimpleIntegerProperty to every block in the grid
        for(var y = 0; y < rows; y++) {
            for(var x = 0; x < cols; x++) {
                grid[x][y] = new SimpleIntegerProperty(0);
            }
        }
    }

    /**
     * Get the Integer property contained inside the grid at a given row and column index. Can be used for binding.
     * @param x column
     * @param y row
     * @return the IntegerProperty at the given x and y in this grid
     */
    public IntegerProperty getGridProperty(int x, int y) {
        return grid[x][y];
    }

    /**
     * Update the value at the given x and y index within the grid
     * @param x column
     * @param y row
     * @param value the new value
     */
    public void set(int x, int y, int value) {
        grid[x][y].set(value);
    }

    /**
     * Get the value represented at the given x and y index within the grid
     * @param x column
     * @param y row
     * @return the value
     */
    public int get(int x, int y) {
        try {
            //Get the value held in the property at the x and y index provided
            return grid[x][y].get();
        } catch (ArrayIndexOutOfBoundsException e) {
            //No such index
            return -1;
        }
    }

    /**
     * Get the number of columns in this game
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Get the number of rows in this game
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }

    //Check if location is valid for placement
    public boolean canPlayPiece(GamePiece piece, int x, int y) {

        for (int i = 0; i<piece.getBlocks().length; i++){
            for (int j = 0; j<piece.getBlocks()[i].length; j++){

                if((piece.getBlocks()[j][i] > 0 && get((x+j)-1, (y+i)-1) > 0) && get((x+j)-1, (y+i)-1) != 16) {
                    logger.info("BLOCK OCCUPIED! Pick an available slot.");
                    Multimedia.playSounds("fail.wav");

                    return false;
                }
            } 
        }

        return true;
    }

    //Place piece onto the board
    public void playPiece(GamePiece piece, int x, int y) {

        logger.info("Clicked x:{}, y:{}", x, y);

        for (int i = 0; i<piece.getBlocks().length; i++){
            for (int j = 0; j<piece.getBlocks()[i].length; j++){

                if(piece.getBlocks()[j][i] >= 1) {
                    set((x+j)-1, (y+i)-1, piece.getValue());
                }
            } 
        }
        Multimedia.playSounds("place.wav");
    }

    public void afterPiece(Game gm) {

        ArrayList<Integer> x = new ArrayList<Integer>();
        ArrayList<Integer> y = new ArrayList<Integer>();

        //Find horizontal lines
        for(int columns=0;columns<getCols();columns++) {
            int blockOccupied = 0;
            for(int rows=0;rows<getRows();rows++) {
                if(get(rows, columns) > 0) {
                    blockOccupied++;
                    if(blockOccupied == getCols()) {
                        Multimedia.playSounds("clear.wav");
                        //Add to array for clearing
                        for(int i=0;i<getCols();i++) {
                            //set(i,columns,0);
                            x.add(i);
                            y.add(columns);
                        }
                        break;
                    }
                }
            }
        }
        //Find vertical lines
        for(int columns=0;columns<getCols();columns++) {
            int blockOccupied = 0;
            for(int rows=0;rows<getRows();rows++) {
                if(get(columns, rows) > 0) {
                    blockOccupied++;
                    if(blockOccupied == getCols()) {
                        Multimedia.playSounds("clear.wav");
                        //Add to array for clearing
                        for(int i=0;i<getCols();i++) {                               
                            x.add(columns);
                            y.add(i);
                        }
                        break;
                    }
                }
            }
        }

        if(!(x.isEmpty())) {
            gm.setMultipliyer(1);
        }
        else if(x.isEmpty()) {
            gm.resetMultipliyer(0);
        }

        int numOfLines = x.size()/5;
        int numOfBlocks = x.size();
       
        gm.score(numOfBlocks, numOfLines);
        //gm.setMultipliyer(numOfLines);

        logger.info("Current level:{}", gm.getLevel());
        logger.info("Num of lines cleared:{}", numOfLines);
        logger.info("Total blocks cleared:{}", numOfBlocks);
        logger.info("Current multipliyer:{}", gm.getMultipliyer());
        logger.info("Current score:{}", gm.getScore());

        for(int i=0;i<x.size();i++) {
            Pair<Integer, Integer> cor = new Pair<>(x.get(i), y.get(i));
            gm.receiveClearedBlocks(cor);
        }

        x.removeAll(x);
        y.removeAll(y);
    }
}