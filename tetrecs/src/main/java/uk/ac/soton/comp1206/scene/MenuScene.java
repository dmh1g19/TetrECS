package uk.ac.soton.comp1206.scene;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uk.ac.soton.comp1206.App;
import uk.ac.soton.comp1206.event.Multimedia;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The main menu of the game. Provides a gateway to the rest of the game.
 */
public class MenuScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    /**
     * Create a new menu scene
     * @param gameWindow the Game Window this will be displayed in
     */
    public MenuScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Menu Scene");
    }

    /**
     * Build the menu layout
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var menuPane = new StackPane();
        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());
        menuPane.getStyleClass().add("menu-background");
        root.getChildren().add(menuPane);

        var mainPane = new BorderPane();

        /// Title image ///
        Image titleImg = new Image(Multimedia.getImage("TetrECS.png"));
        var title = new HBox();
        ImageView mv = new ImageView(titleImg);
        mv.setFitHeight(150);
        mv.setPreserveRatio(true);
        title.getChildren().add(mv);
        title.setPrefHeight(100);
        title.setPrefWidth(100);
        title.setPadding(new Insets(50, 5, 5, 5));
        title.setAlignment(Pos.TOP_CENTER);
        mainPane.setTop(title);

        menuPane.getChildren().addAll(mainPane);

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(8), title);
        fadeOut.setFromValue(0.0);
        fadeOut.setToValue(1.0);
        fadeOut.play();

        TranslateTransition transition = new TranslateTransition();
        transition.setDuration(Duration.seconds(2));
        transition.setNode(title);
        transition.setToY(-6);
        transition.setToY(12);
        transition.setAutoReverse(true);
        transition.setCycleCount(TranslateTransition.INDEFINITE);
        transition.play();

        //Buttons container
        var buttons = new VBox();
        buttons.setPadding(new Insets(200, 0, 0, 0));
        buttons.setAlignment(Pos.TOP_CENTER);

        //Start game button
        var startButton = new Text("Play");
        startButton.getStyleClass().add("menuItem");

        //Scores button
        var scoreButton = new Text("Scores");
        scoreButton.getStyleClass().add("menuItem");

        //Instructions button
        var instructionsButton = new Text("Instructions");
        instructionsButton.getStyleClass().add("menuItem");
        mainPane.setBottom(instructionsButton);

        //Exit button
        var exitButton = new Text("Exit");
        exitButton.getStyleClass().add("menuItem");
        mainPane.setBottom(exitButton);

        buttons.getChildren().addAll(startButton, instructionsButton, scoreButton, exitButton);
        mainPane.setCenter(buttons);

        startButton.setOnMouseClicked(e -> {
            Multimedia.stopBackgroundMusic(); //Stop the menu specific background music
            gameWindow.startChallenge();
        });

        instructionsButton.setOnMouseClicked(e -> {
            gameWindow.startInstructions();
        });

        scoreButton.setOnMouseClicked(e -> {
            gameWindow.startScores();
        });

        exitButton.setOnMouseClicked(e -> {
            System.exit(0);
        });
    }

    /**
     * Initialise the menu
     */
    @Override
    public void initialise() {
        Multimedia.playBackgroundMusic("menu.mp3");

        getScene().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                System.exit(0);
            }
        });
    }

    /**
     * Handle when the Start Game button is pressed
     * @param event event
     */
    private void startGame(ActionEvent event) {
        //Not used
    }

}
