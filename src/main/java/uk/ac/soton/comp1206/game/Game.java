package uk.ac.soton.comp1206.game;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.Pair;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.event.Multimedia;
import uk.ac.soton.comp1206.event.NextPieceListener;
import uk.ac.soton.comp1206.event.clearBlocksListener;
import uk.ac.soton.comp1206.event.delayChangeListener;
import uk.ac.soton.comp1206.event.gameOverListener;
import uk.ac.soton.comp1206.event.highScoreListener;
import uk.ac.soton.comp1206.event.rotatePieceListener;

/**
 * The Game class handles the main logic, state and properties of the TetrECS
 * game. Methods to manipulate the game state
 * and to handle actions made by the player should take place inside this class.
 */
public class Game {

    private List<NextPieceListener> listenerNextPiece = new ArrayList<NextPieceListener>();
    private List<rotatePieceListener> listenerRotatePiece = new ArrayList<rotatePieceListener>();
    private List<gameOverListener> listenerGameover = new ArrayList<gameOverListener>();
    private List<highScoreListener> listenerHighScore = new ArrayList<highScoreListener>();
    private List<delayChangeListener> listenerDelayChange = new ArrayList<delayChangeListener>();
    private List<clearBlocksListener> listenerBlocksCleared = new ArrayList<clearBlocksListener>();

    private static final Logger logger = LogManager.getLogger(Game.class);

    Random rand = new Random();

    GamePiece currentPiece;
    GamePiece followingPiece = GamePiece.createPiece(5);

    public boolean temp = false;

    Multimedia sound = new Multimedia();

    private IntegerProperty score = new SimpleIntegerProperty(0);
    private SimpleDoubleProperty level = new SimpleDoubleProperty(0.0);
    private IntegerProperty lives = new SimpleIntegerProperty(3);
    private IntegerProperty multipliyer = new SimpleIntegerProperty(0);

    public IntegerProperty score() {
        return score;
    }

    public int getScore() {
        return score().get();
    }

    public void setScore(int value) {
        score.set(score.get() + value);
    }

    public SimpleDoubleProperty level() {
        return level;
    }

    public double getLevel() {
        return level().get();
    }

    public void setLevel(double value) {
        level.set(value);
    }

    public IntegerProperty lives() {
        return lives;
    }

    public int getLives() {
        return lives().get();
    }

    public void setLives(int value) {
        lives.set(lives.get() - value);
    }

    public IntegerProperty multipliyer() {
        return multipliyer;
    }

    public int getMultipliyer() {
        return multipliyer().get();
    }

    public void setMultipliyer(int value) {
        multipliyer.set(multipliyer.get() + value);
    }

    public void resetMultipliyer(int value) {
        multipliyer.set(value);
    }

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
     * Create a new game with the specified rows and columns. Creates a
     * corresponding grid model.
     * 
     * @param cols number of columns
     * @param rows number of rows
     */
    public Game(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        // Create a new grid model to represent the game state
        this.grid = new Grid(cols, rows);
    }

    public SimpleDoubleProperty getTimerDelay() {
        Double delayValue = Math.max(2500, 12000 - 500 * getLevel());
        SimpleDoubleProperty delay = new SimpleDoubleProperty(delayValue);
        receiveDelay(delay);

        return delay;
    }

    public Timer timer = new Timer();

    public int getHighScore() throws NumberFormatException, IOException {
        String getFolder = Multimedia.getScoreFolder();
        File newFile = new File((getFolder + "scores.txt").substring(5));

        ArrayList<Pair<String, Integer>> highScores = new ArrayList<>();

        FileReader fileReader = new FileReader(newFile);
        if (newFile.exists()) {
            try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    String nameScore[] = line.split(":");
                    String name = nameScore[0];
                    String score = nameScore[1];
                    Pair<String, Integer> scorePair = new Pair<>(name, Integer.parseInt(score));
                    highScores.add(scorePair);
                }
            }

            sortScores(highScores);

            return highScores.get(0).getValue();
        } else {
            // if file doesn exist, create one and write some default scores
            newFile.createNewFile();

            try {
                FileWriter fr = new FileWriter(newFile);
                fr.write("Jhon:30\nSarah:20\nTim:40"); // TODO: Test this, i dont think its creating it
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return 0;
    }

    public void sortScores(ArrayList<Pair<String, Integer>> scoreList) {
        scoreList.sort(new Comparator<Pair<String, Integer>>() {
            @Override
            public int compare(Pair<String, Integer> o1, Pair<String, Integer> o2) {
                if (o1.getValue() > o2.getValue()) {
                    return -1;
                } else if (o1.getValue().equals(o2.getValue())) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });
    }

    public void gameLoop() {

        if (getLives() == 0) {
            Multimedia.stopBackgroundMusic();
            sound.playSounds("explode.wav");
            timer.cancel();
            receiveGameover();
        }

        resetMultipliyer(1);
        sound.playSounds("lifelose.wav");
        setLives(1);
        nextPiece();
    }

    public void startTimer() {
        timer.cancel();
        timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                gameLoop();
            }
        };
        long delay = (long) getTimerDelay().get();
        timer.scheduleAtFixedRate(task, delay, delay);
    }

    public void rotateCurrentPiece() {
        logger.info("Piece: {} rotated.", currentPiece.getValue());

        sound.playSounds("rotate.wav");

        currentPiece.rotate();
        receive2(currentPiece); // feed listener generated new roatetd piece in 3x3 view
    }

    public void rotateCurrentPieceLeft() {
        logger.info("Piece: {} rotated left.", currentPiece.getValue());

        sound.playSounds("rotate.wav");

        currentPiece.rotateLeft();
        receive2(currentPiece);
    }

    public void setOnClear(clearBlocksListener listener) {
        this.listenerBlocksCleared.add(listener);
    }

    public void receiveClearedBlocks(Pair<Integer, Integer> cor) {
        for (clearBlocksListener listener : listenerBlocksCleared) {
            listener.blocksCleared(cor);
        }
    }

    // Listener for tracking timer delay change
    public void setOnDelayChange(delayChangeListener listener) {
        this.listenerDelayChange.add(listener);
    }

    public void receiveDelay(SimpleDoubleProperty delay) {
        for (delayChangeListener listener : listenerDelayChange) {
            listener.delayChange(delay);
        }
    }

    // Listener for updating current high score
    public void setOnHighScore(highScoreListener listener) {
        this.listenerHighScore.add(listener);
    }

    private void receiveHighScore() {
        for (highScoreListener listener : listenerHighScore) {
            listener.highScoreUpdate();
        }
    }

    // Listener for end of game
    public void setOnGameOver(gameOverListener listener) {
        this.listenerGameover.add(listener);
    }

    private void receiveGameover() {
        for (gameOverListener listener : listenerGameover) {
            listener.gameOver();
        }
    }

    // Listener code for updating 3x3 piece boards
    public void addListener(NextPieceListener listener) {
        this.listenerNextPiece.add(listener);
    }

    private void receive(GamePiece piece, GamePiece piece2) {
        // logger.info("Piece received: {}", piece.getValue());

        for (NextPieceListener listener : listenerNextPiece) {
            listener.nextPiece(piece, piece2);
        }
    }

    // Listener code for rotating piece
    public void addListener2(rotatePieceListener listener) {
        this.listenerRotatePiece.add(listener);
    }

    private void receive2(GamePiece pieceToRotate) {
        // logger.info("Piece received: {}", piece.getValue());

        for (rotatePieceListener listener : listenerRotatePiece) {
            listener.nextPiece(pieceToRotate);
        }
    }

    public void updateLevel() {
        double lvlRound = Math.floor(((getScore() / 1000) * 1000) / 1000);
        setLevel(lvlRound);
        sound.playSounds("level.wav");
    }

    public void score(int numOfBlocks, int numOfLines) {

        try {
            if (getScore() > getHighScore()) {
                receiveHighScore(); // inform listener
            }
        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
        }

        int scoreUpdate = numOfLines * numOfBlocks * 10 * getMultipliyer();

        if (scoreUpdate == 0) {
            scoreUpdate = 1;
        } else {
            setScore(scoreUpdate);
            updateLevel();
        }

    }

    public GamePiece spawnPiece() {
        int value = rand.nextInt(15);

        GamePiece newPiece = GamePiece.createPiece(value);

        // logger.info("Current piece: {}", newPiece.toString());

        receive(newPiece, followingPiece); // feed listener generated piece
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
        startTimer();

        currentPiece = spawnPiece();
        followingPiece = spawnPiece();
        receive2(currentPiece);

        logger.info("Initialising game");
    }

    /**
     * Handle what should happen when a particular block is clicked
     * 
     * @param gameBlock the block that was clicked
     */
    public void blockClicked(GameBlock gameBlock) {
        // Get the position of this block
        int x = gameBlock.getX();
        int y = gameBlock.getY();

        if (grid.canPlayPiece(currentPiece, x, y)) {
            temp = true;
            startTimer();

            sound.playSounds("place.wav");

            // Update piece on grid
            grid.playPiece(currentPiece, x, y);
            grid.afterPiece(this); // Check if lines need to be cleared
            nextPiece();
        } else {
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

    public void move(int dx, int dy) {
        int newX = currentX + dx;
        int newY = currentY + dy;

        if (newX >= 0 && newX < getCols() && newY >= 0 && newY < getRows()) {
            currentX = newX;
            currentY = newY;

            if (grid.get(currentX, currentY) <= 0) {
                grid.set(currentX, currentY, 16);
            }

            // Clear stale 16s in the movement direction
            if (dx != 0) {
                int start = (dx > 0) ? 0 : getCols() - 1;
                int end = currentX;
                int step = (dx > 0) ? 1 : -1;

                for (int i = start; i != end; i += step) {
                    if (grid.get(i, currentY) == 16) {
                        grid.set(i, currentY, 0);
                    }
                }
            } else if (dy != 0) {
                int start = (dy > 0) ? 0 : getRows() - 1;
                int end = currentY;
                int step = (dy > 0) ? 1 : -1;

                for (int i = start; i != end; i += step) {
                    if (grid.get(currentX, i) == 16) {
                        grid.set(currentX, i, 0);
                    }
                }
            }
        }
    }

    public void keyboardControlsW() {
        move(0, -1);
    }

    public void keyboardControlsA() {
        move(-1, 0);
    }

    public void keyboardControlsS() {
        move(0, 1);
    }

    public void keyboardControlsD() {
        move(1, 0);
    }

    public void keyboardControlsEnter() {
        if (grid.canPlayPiece(currentPiece, currentX, currentY)) {
            temp = true;
            startTimer();

            grid.playPiece(currentPiece, currentX, currentY);
            grid.afterPiece(this); // Check if lines need to be cleared
            nextPiece();
        } else {
            logger.info("BLOCK OCCUPIED! Pick an available slot.");
            sound.playSounds("fail.wav");
            temp = false;
        }
    }

    /**
     * Get the grid model inside this game representing the game state of the board
     * 
     * @return game grid model
     */
    public Grid getGrid() {
        return grid;
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
}