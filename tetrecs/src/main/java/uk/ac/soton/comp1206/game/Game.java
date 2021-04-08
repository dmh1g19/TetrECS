package uk.ac.soton.comp1206.game;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.event.Multimedia;
import uk.ac.soton.comp1206.event.NextPieceListener;
import uk.ac.soton.comp1206.event.gameOverListener;
import uk.ac.soton.comp1206.event.rotatePieceListener;
import uk.ac.soton.comp1206.scene.ChallengeScene;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javafx.beans.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * The Game class handles the main logic, state and properties of the TetrECS game. Methods to manipulate the game state
 * and to handle actions made by the player should take place inside this class.
 */
public class Game {
  
    private List<NextPieceListener> listeners = new ArrayList<NextPieceListener>();
    private List<rotatePieceListener> listeners2 = new ArrayList<rotatePieceListener>();
    private List<gameOverListener> listenerGameover = new ArrayList<gameOverListener>();

    private static final Logger logger = LogManager.getLogger(Game.class);

    Random rand = new Random();

    GamePiece currentPiece;
    GamePiece followingPiece = GamePiece.createPiece(5);

    public boolean temp = false;

    Multimedia sound = new Multimedia();

    private static IntegerProperty score = new SimpleIntegerProperty(0);
    private static IntegerProperty level = new SimpleIntegerProperty(0);
    private static IntegerProperty lives = new SimpleIntegerProperty(3);
    private static IntegerProperty multipliyer = new SimpleIntegerProperty(1);

    public IntegerProperty score(){return score;}
    public int getScore(){return score().get();}
    public void setScore(int value){score.set(score.get()+value);}

    public IntegerProperty level(){return level;}
    public int getLevel(){return level().get();}
    public void setLevel(int value){level.set(value);}

    public IntegerProperty lives(){return lives;}
    public int getLives(){return lives().get();}
    public void setLives(int value){lives.set(lives.get()-value);}

    public IntegerProperty multipliyer(){return multipliyer;}
    public int getMultipliyer(){return multipliyer().get();}
    public void setMultipliyer(int value){multipliyer.set(multipliyer.get()+value);}
    public void resetMultipliyer(int value){multipliyer.set(value);}

    /**
     * Number of rows
     */
    protected final int rows;

    /**
     * Number of columns
     */
    protected final int cols;

    /**
     * The grid model linked to the game
     */
    protected final Grid grid;

    /**
     * Create a new game with the specified rows and columns. Creates a corresponding grid model.
     * @param cols number of columns
     * @param rows number of rows
     */
    public Game(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        //Create a new grid model to represent the game state
        this.grid = new Grid(cols,rows);
    }

    public int delay;
    public int getTimerDelay() {
        delay = Math.max(2500, 12000-500*getLevel());

        return delay;
    }

    Timer timer = new Timer();

    public void gameLoop() {

        if(getLives() == 3) {
            Multimedia.stopBackgroundMusic();
            Multimedia.playSounds("explode.wav");
            timer.cancel();
            receiveGameover();
        }

        resetMultipliyer(1);
        sound.playSounds("lifelose.wav");
        setLives(1);
        nextPiece();
    }

    TimerTask task = new TimerTask(){
        @Override
        public void run() {
            gameLoop();
        }
    };

    public void rotateCurrentPiece() {
        logger.info("Piece: {} rotated.", currentPiece.getValue());

        sound.playSounds("rotate.wav");

        currentPiece.rotate();
        receive2(currentPiece); //feed listener generated new roatetd piece in 3x3 view
    }

    public void rotateCurrentPieceLeft() {
        logger.info("Piece: {} rotated left.", currentPiece.getValue());

        sound.playSounds("rotate.wav");

        currentPiece.rotateLeft();
        receive2(currentPiece); 
    }

    //Listener for end of game
    public void setOnGameOver(gameOverListener listener) {
        this.listenerGameover.add(listener);
    }

    private void receiveGameover() {
        for(gameOverListener listener : listenerGameover) {
            listener.gameOver();
        }
    }

    // Listener code for updating 3x3 piece boards
    public void addListener(NextPieceListener listener) {
        this.listeners.add(listener);
    }

    private void receive(GamePiece piece, GamePiece piece2) {
        //logger.info("Piece received: {}", piece.getValue());

        for(NextPieceListener listener : listeners) {
            listener.nextPiece(piece, piece2);
        }
    }

    // Listener code for rotating piece
    public void addListener2(rotatePieceListener listener) {
        this.listeners2.add(listener);
    }

    private void receive2(GamePiece pieceToRotate) {
        //logger.info("Piece received: {}", piece.getValue());

        for(rotatePieceListener listener : listeners2) {
            listener.nextPiece(pieceToRotate);
        }
    }

    public void score(int numOfBlocks, int numOfLines) {
        //number of lines * number of grid blocks cleared * 10 * the current multiplier

        int scoreUpdate = numOfLines*numOfBlocks*10*getMultipliyer();

        if(scoreUpdate == 0) {
            scoreUpdate = 1;
        }
        else {
            setScore(scoreUpdate);
            int lvlRound = (int) Math.floor((((double)getScore()/1000)*1000)/1000);
            sound.playSounds("level.wav");
            setLevel(lvlRound);
        }

    }

    public GamePiece spawnPiece() {
        int value = rand.nextInt(15);

        GamePiece newPiece = GamePiece.createPiece(value);
        
        //logger.info("Current piece: {}", newPiece.toString());

        receive(newPiece, followingPiece); //feed listener generated piece
        return newPiece;
    }

    public void nextPiece() {
        logger.info("Current piece: {}", currentPiece);
        logger.info("Following piece: {}", followingPiece);

        currentPiece = followingPiece;
        followingPiece = spawnPiece();
    }

    public void swapCurrentPiece() {
        logger.info("Piece board clicked: swapping pieces");
        
        sound.playSounds("rotate.wav");

        GamePiece temp = followingPiece;
        followingPiece = currentPiece;
        currentPiece = temp;
        receive(followingPiece, currentPiece);
    }

    /**
     * Start the game
     */
    public void start() {
        logger.info("Starting game");

        initialiseGame();
    }

    /**
     * Initialise a new game and set up anything that needs to be done at the start
     */
    public void initialiseGame() {
        //Start the timer
        timer = new Timer();
        timer.scheduleAtFixedRate(task, getTimerDelay(), getTimerDelay());

        currentPiece = spawnPiece();
        followingPiece = spawnPiece();
        receive2(currentPiece);
        
        logger.info("Initialising game");
    }

    /**
     * Handle what should happen when a particular block is clicked
     * @param gameBlock the block that was clicked
     */
    public void blockClicked(GameBlock gameBlock) {
        sound.playSounds("place.wav");

        //Get the position of this block
        int x = gameBlock.getX();
        int y = gameBlock.getY();

        if(grid.canPlayPiece(currentPiece, x, y) == true) {
            temp = true;
            //Reset timer
            logger.info("Timer reset!");
            timer.cancel();
            timer = new Timer();
            TimerTask task = new TimerTask(){
                public void run() {
                    gameLoop();
                }
            };
            timer.scheduleAtFixedRate(task, getTimerDelay(), getTimerDelay());
            System.out.println("Game: "+getTimerDelay());

            //Update piece on grid
            grid.playPiece(currentPiece, x, y);
            grid.afterPiece(this); //Check if lines need to be cleared
            nextPiece();
        }
        else {
            temp = false;
        }
    }

    int currentX = 0;
    int currentY = 0;

    public int getCurrentX() {
        return currentX;
    }

    public int getCurrentY() {
        return currentY;
    }

    public void keyboardControlsD() {
        if(currentX <= getCols()-2) {
            currentX++;
            if(grid.get(currentX, currentY) <= 0) {
                grid.set(currentX, currentY, 16);
            }
        }
        for(int i=0;i<currentX;i++) {
            if(grid.get(i, currentY) == 16) {
                grid.set(i, currentY, 0);
            }
        }
    }

    public void keyboardControlsS() {
        if(currentY <= getRows()-2) {
            currentY++;
            if(grid.get(currentX, currentY) <= 0) {
                grid.set(currentX, currentY, 16);
            }
        }
        for(int i=0;i<currentY;i++) {
            if(grid.get(currentX, i) == 16) {
                grid.set(currentX, i, 0);
            }
        }
    }
    public void keyboardControlsA() {
        if(currentX > 0) {
            currentX--;
            if(grid.get(currentX, currentY) <= 0) {
                grid.set(currentX, currentY, 16);
            }
        }
        for(int i=getCols()-1;i>currentX;i--) {
            if(grid.get(i, currentY) == 16) {
                grid.set(i, currentY, 0);
            }
        }
    }
    public void keyboardControlsW() {
        if(currentY > 0) {
            currentY--;
            if(grid.get(currentX, currentY) <= 0) {
                grid.set(currentX, currentY, 16);
            }
        }
        for(int i=getRows()-1;i>currentY;i--) {
            if(grid.get(currentX, i) == 16) { 
                grid.set(currentX, i, 0);
            }
        }
    }
    public void keyboardControlsEnter() {
        //Place block when usser presses enter at currentX, currentY location
        if(grid.canPlayPiece(currentPiece, currentX, currentY) == true) {
            temp = true;
            //Reset timer
            logger.info("Timer reset!");
            timer.cancel();
            timer = new Timer();
            TimerTask task = new TimerTask(){
                public void run() {
                    gameLoop();
                }
            };
            timer.scheduleAtFixedRate(task, getTimerDelay(), getTimerDelay());

            grid.playPiece(currentPiece, currentX, currentY);
            grid.afterPiece(this); //Check if lines need to be cleared
            nextPiece();
        }
    }

    /**
     * Get the grid model inside this game representing the game state of the board
     * @return game grid model
     */
    public Grid getGrid() {
        return grid;
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
}