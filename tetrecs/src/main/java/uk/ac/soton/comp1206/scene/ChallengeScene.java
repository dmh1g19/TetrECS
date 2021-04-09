package uk.ac.soton.comp1206.scene;

import javafx.animation.FillTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.event.Multimedia;
import uk.ac.soton.comp1206.event.NextPieceListener;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class ChallengeScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    protected Game game;

    Timeline timeline;
    Rectangle bar;
    KeyFrame frame;
    KeyValue widthValue;
    FillTransition ft;

    /**
     * Create a new Single Player challenge scene
     * @param gameWindow the Game Window
     */
    public ChallengeScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Challenge Scene");
        //Multimedia.playBackgroundMusic("game.wav");
    }

    /**
     * Build the Challenge window
     */
    @Override
    public void build() {

        logger.info("Building " + this.getClass().getName());

        setupGame();

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("menu-background");
        root.getChildren().add(challengePane);

        /// Score display information ///
        var info = new VBox();
        info.setPadding(new Insets(10, 50, 10, 10));
        info.setAlignment(Pos.TOP_CENTER);

        var scoreLabel = new Text("Score");
        scoreLabel.getStyleClass().add("heading");
        var scoreText = new Text();
        scoreText.setTextAlignment(TextAlignment.CENTER);
        scoreText.getStyleClass().add("score");
        scoreText.textProperty().bind(game.score().asString());

        var levelLabel = new Text("Level");
        levelLabel.getStyleClass().add("heading");
        var levelText = new Text();
        levelText.getStyleClass().add("level");
        levelText.setTextAlignment(TextAlignment.CENTER);
        levelText.textProperty().bind(game.level().asString());

        var livesLabel = new Text("Lives");
        livesLabel.getStyleClass().add("heading");
        var livesText = new Text();
        livesText.getStyleClass().add("lives");
        livesText.setTextAlignment(TextAlignment.CENTER);
        livesText.textProperty().bind(game.lives().asString());

        var multipliyerLabel = new Text("Multiplier");
        multipliyerLabel.getStyleClass().add("heading");
        var multipliyerText = new Text();
        multipliyerText.getStyleClass().add("lives");
        multipliyerText.setTextAlignment(TextAlignment.CENTER);
        multipliyerText.textProperty().bind(game.multipliyer().asString());

        var highScoreLabel = new Text("Current High Score");
        highScoreLabel.getStyleClass().add("heading");
        var highScoreText = new Text();
        highScoreText.getStyleClass().add("lives");
        highScoreText.setTextAlignment(TextAlignment.CENTER);
        try {
            highScoreText.setText(String.valueOf(game.getHighScore()));
        } catch (NumberFormatException | IOException e1) {
            e1.printStackTrace();
        }

        info.getChildren().addAll(multipliyerLabel, multipliyerText, scoreLabel, scoreText, livesLabel, livesText, levelLabel, levelText, highScoreLabel, highScoreText);

        /// Mini game piece view grid ///
        var pieceGridLabel = new Text("Incoming");
        pieceGridLabel.getStyleClass().add("heading");

        var pieceView = new VBox();
        pieceView.setPadding(new Insets(5, 5, 5, 15));
        pieceView.setAlignment(Pos.TOP_CENTER);
       
        PieceBoard pb = new PieceBoard(3, 3, 100, 100);
        PieceBoard pbSmall = new PieceBoard(3, 3, 150, 150);
        pieceView.setSpacing(5);
        pieceView.getChildren().addAll(pieceGridLabel, pbSmall, pb);

        var titleContainer = new HBox();
        titleContainer.setAlignment(Pos.TOP_CENTER);
        titleContainer.setPadding(new Insets(25, 5, 5, 70));
        var title = new Text("Challenger");
        title.getStyleClass().add("title");
        titleContainer.getChildren().add(title);

        var timerBarSection = new HBox();
        timerBarSection.setAlignment(Pos.BOTTOM_LEFT);
        timerBarSection.setPadding(new Insets(5, 5, 5, 5));
        bar = new Rectangle(0, 0, gameWindow.getWidth(), 20);
        bar.setFill(Color.RED);
        timerBarSection.getChildren().add(bar);

        var mainPane = new BorderPane();
        challengePane.getChildren().add(mainPane);
        mainPane.setRight(info); //add info section
        mainPane.setLeft(pieceView); //add current piece view grid
        mainPane.setTop(titleContainer);
        mainPane.setBottom(timerBarSection);

        var board = new GameBoard(game.getGrid(),gameWindow.getWidth()/2,gameWindow.getWidth()/2);
        mainPane.setCenter(board);

        //Handle block on gameboard grid being clicked
        board.setOnBlockClick(this::blockClicked);

        //Listener for updating delay on progress bar
        game.setOnDelayChange((delay) -> {
            Platform.runLater(() -> {
                widthValue = new KeyValue(bar.widthProperty(), 0); //TODO: might need global scope
                logger.info("Current delay: {}", delay);
                frame = new KeyFrame(Duration.millis(delay), widthValue);
                timeline = new Timeline(frame);
                timeline.setCycleCount(Timeline.INDEFINITE);
                timeline.play();

                ft = new FillTransition(Duration.millis(delay), bar, Color.GREEN, Color.RED);
                ft.setCycleCount(Timeline.INDEFINITE);
                ft.play();
            });
        });

        //Listener for updating highscore during game if player beats it
        game.setOnHighScore(() -> {
            Platform.runLater(() -> {
                highScoreText.textProperty().bind(game.score().asString());
            });
        });

        //Listener so scene knows when game has ended
        game.setOnGameOver(() -> {
            Platform.runLater(() -> {
                timeline.stop();
                gameWindow.startScores();
            });
        });

        //Listener to rotate
        game.addListener2((pieceToRotate) -> {
            Platform.runLater(() -> {
                pbSmall.addPieceToGrid(pieceToRotate);
            });
        });

        //Listener to add game piece to 3x3 grids
        game.addListener((piece, piece2) -> {
            Platform.runLater(() -> {
                pb.addPieceToGrid(piece);
                pbSmall.addPieceToGrid(piece2);
            });
        });
        board.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY)
            {
                game.rotateCurrentPiece();
            }
            else if (e.getButton() == MouseButton.PRIMARY)
            {
                if(game.temp) {
                    timeline.stop();
                    bar.setWidth(gameWindow.getWidth());
                    timeline.play();
                }
            }
        });

        pb.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY)
            {
                game.swapCurrentPiece();
            }
        });

        pbSmall.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY)
            {
                game.rotateCurrentPiece();
            }
        });
    }

    public void startScoresScene() {
        gameWindow.startScores();
    }

    /**
     * Handle when a block is clicked
     * @param gameBlock the Game Block that was clocked
     */
    private void blockClicked(GameBlock gameBlock) {
        game.blockClicked(gameBlock);
    }

    /**
     * Setup the game object and model
     */
    public void setupGame() {
        logger.info("Starting a new challenge");

        //Start new game
        game = new Game(5, 5);
    }

    /**
     * Initialise the scene and start the game
     */
    @Override
    public void initialise() {
        logger.info("Initialising Challenge");
        game.start();

        getScene().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.D
                || e.getCode() == KeyCode.RIGHT) {
                game.keyboardControlsD();
            }
            if (e.getCode() == KeyCode.S
                || e.getCode() == KeyCode.DOWN) {
                game.keyboardControlsS();
            }
            if (e.getCode() == KeyCode.A
                || e.getCode() == KeyCode.LEFT) {
                game.keyboardControlsA();
            }
            if (e.getCode() == KeyCode.W
                || e.getCode() == KeyCode.UP) {
                game.keyboardControlsW();
            }
            if (e.getCode() == KeyCode.ENTER
                || e.getCode() == KeyCode.X) {

                game.keyboardControlsEnter();

                if(game.temp) {
                    timeline.stop();
                    bar.setWidth(gameWindow.getWidth());
                    timeline.play();
                }
            }
            if (e.getCode() == KeyCode.ESCAPE) {
                Multimedia.stopBackgroundMusic();
                gameWindow.startMenu();
            }
            if (e.getCode() == KeyCode.SPACE
                || e.getCode() == KeyCode.R) {
                game.swapCurrentPiece();
            }
            if (e.getCode() == KeyCode.E
                || e.getCode() == KeyCode.C
                || e.getCode() == KeyCode.CLOSE_BRACKET) {
                game.rotateCurrentPiece();
            }
            if (e.getCode() == KeyCode.Q
                || e.getCode() == KeyCode.Z
                || e.getCode() == KeyCode.OPEN_BRACKET) {
                game.rotateCurrentPieceLeft();
            }
        });

    }

}
