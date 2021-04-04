package uk.ac.soton.comp1206.game;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import uk.ac.soton.comp1206.event.Multimedia;

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

    int numOfLines = 0;
    int numOfBlocks = 0;

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

    public void afterPiece() {
        //This is very bad im sorry! :<
        //TODO: IMPLEMENT LINE CLEARING AND SCORES CORRECTLY

        Game gm = new Game(0, 0);

        //Remove horizontal lines
        for(int t=0;t<getCols();t++) {
            int counter = 0;
            for (int i=0;i<getCols();i++) {
                if(get(i, t) > 0) {
                    counter++;
                    if(counter == getCols()) {
                        logger.info("Line found!");

                        numOfLines++; //increase lines cleared count
                        for(int p=0;p<getCols();p++) {
                            set(p, t, 0);
                            numOfBlocks++; //count blocks cleared
                        }
                        Multimedia.playSounds("clear.wav");
                        break;
                    }
                }
            }

            gm.score(numOfBlocks, numOfLines);
            //gm.setMultipliyer(numOfLines);
        }

        //Remove vertical lines
        for(int t=0;t<getRows();t++) {
            int counter = 0;
            for (int i=0;i<getRows();i++) {
                if(get(t, i) > 0) {
                    counter++;
                    if(counter == getRows()) {
                        logger.info("Line found!");
                        for(int p=0;p<getRows();p++) {
                            set(t, p, 0);
                        }
                        Multimedia.playSounds("clear.wav");
                        break;
                    }
                }
            }
        }

        //logger.info("Current multipliyer:{}", gm.getMultipliyer());
        //logger.info("Num of lines cleared:{}", numOfLines);
        //logger.info("Total blocks cleared:{}", numOfBlocks);
    }

}
