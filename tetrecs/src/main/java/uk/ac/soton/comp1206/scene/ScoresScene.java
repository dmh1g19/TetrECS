package uk.ac.soton.comp1206.scene;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Observable;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import uk.ac.soton.comp1206.event.Multimedia;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.ui.ScoresList;

public class ScoresScene extends BaseScene {
    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    protected Game game;

    ScoresList scoreBox = new ScoresList();

    private final ObservableList<Pair<String,Integer>> scoreList = FXCollections.observableArrayList();
    private final ListProperty<Pair<String,Integer>> scoreProperty = new SimpleListProperty<>(scoreList);

    public ScoresScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating scores Scene");

    }

    public void loadScores() throws IOException {
        String fileName = Multimedia.getScore("scores.txt");
        FileReader fileReader = new FileReader(fileName.substring(5));

        scoreProperty.clear();

        try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            String line;
            while((line=bufferedReader.readLine()) != null) {
                String nameScore[] = line.split(":"); 
                String name = nameScore[0]; 
                String score = nameScore[1];
                Pair<String, Integer> testPair = new Pair<>(name, Integer.parseInt(score));
                scoreProperty.add(testPair);
            }
        }

        sortScores(scoreProperty);
    }

    public String enterName() {
        TextInputDialog textInput = new TextInputDialog();
        textInput.setTitle("Congratulations!");
        textInput.getDialogPane().setContentText("Input your name");
        Optional<String> result = textInput.showAndWait();
        TextField input = textInput.getEditor();

        return input.getText();
    }

    public void writeScores() throws IOException {
        String getFolder = Multimedia.getScoreFolder();

        File newFile = new File((getFolder+"scores.txt").substring(5));

        if(!newFile.exists()) {
            //if file doesn exist, create one and write some default scores
            newFile.createNewFile();

            try {
                FileWriter fr = new FileWriter(newFile);
                fr.write("Jhon:30\nSarah:20\nTim:40");
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            logger.info("Score file already exists.");

            //go through list, if high score beats any of the top scores, write score to file
            game = new Game(5, 5);
            loadScores();
            for(Pair<String, Integer> currentEntry : scoreProperty) {
                if(game.getScore() > currentEntry.getValue()) {
                    FileWriter fr = new FileWriter(newFile, true);
                    fr.write("\n"+enterName()+":"+game.getScore());
                    fr.close();
                    break;
                }
            }
        }
    }

    public void sortScores(ListProperty<Pair<String,Integer>> scoreList) {
        scoreList.sort(new Comparator<Pair<String, Integer>>() {
            @Override
            public int compare(Pair<String, Integer> o1, Pair<String, Integer> o2) {
                if (o1.getValue() > o2.getValue()) {
                    return -1;
                }
                else if (o1.getValue().equals(o2.getValue())) {
                    return 0; 
                }
                else {
                    return 1;
                }
            }
        });
    }

    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        try {
            writeScores();
            loadScores();
        } catch (IOException e) {
            e.printStackTrace();
        }

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var menuPane = new StackPane();
        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());
        menuPane.getStyleClass().add("menu-background");
        root.getChildren().add(menuPane);

        var mainPane = new BorderPane();

        Image titleImg = new Image(Multimedia.getImage("TetrECS.png"));
        var title = new HBox();
        ImageView mv = new ImageView(titleImg);
        mv.setFitHeight(80);
        mv.setPreserveRatio(true);
        title.getChildren().add(mv);
        title.setPrefHeight(70);
        title.setPrefWidth(70);
        title.setPadding(new Insets(50, 5, 5, 5));
        title.setAlignment(Pos.TOP_CENTER);
        mainPane.setTop(title);

        //main scores container
        var scores = new VBox();
        scores.setPadding(new Insets(4, 4, 4, 4));
        scores.setAlignment(Pos.BOTTOM_CENTER);
        scores.getStyleClass().add("scores");

        var scoreLabel = new Label("High Scores");
        scoreLabel.getStyleClass().add("heading");
        scores.getChildren().add(scoreLabel);
        scores.getChildren().add(scoreBox);

        scoreBox.scoreProperty().bind(scoreProperty);
        mainPane.setCenter(scoreBox);

        menuPane.getChildren().addAll(mainPane);
    }

    @Override
    public void initialise() {
        Multimedia.playBackgroundMusic("menu.mp3");

        getScene().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                Multimedia.stopBackgroundMusic();
                gameWindow.startMenu();
            }
        });
    }
}