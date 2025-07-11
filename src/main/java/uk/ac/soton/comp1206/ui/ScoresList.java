package uk.ac.soton.comp1206.ui;

import java.util.ArrayList;

import javafx.animation.FadeTransition;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import javafx.util.Pair;

public class ScoresList extends VBox {

    public final SimpleListProperty<Pair<String, Integer>> scores = new SimpleListProperty<>();


    public ScoresList() {
        //Set Style
        getStyleClass().add("scorelist");
        setAlignment(Pos.CENTER);
        setSpacing(2);

        scores.addListener((ListChangeListener<? super Pair<String, Integer>>) (c) -> updateList());
    }

    public void updateList() {
        getChildren().clear();

        int counter = 0;

        for(Pair<String, Integer> score : scores) {
            counter++;
            if(counter > 5) {
                break;
            }

            HBox scoreBox = new HBox();
            scoreBox.getStyleClass().add("scoreitem");
            scoreBox.setAlignment(Pos.CENTER);
            scoreBox.setSpacing(10);

            var name = new Text(score.getKey());
            name.getStyleClass().add("scorer");
            name.setTextAlignment(TextAlignment.CENTER);
            HBox.setHgrow(name, Priority.ALWAYS);

            var points = new Text(score.getValue().toString());
            points.getStyleClass().add("points");
            points.setTextAlignment(TextAlignment.CENTER);
            HBox.setHgrow(points, Priority.ALWAYS);

            scoreBox.getChildren().addAll(name, points);

            getChildren().add(scoreBox);

            reveal();
        }
    }

    //Animate score reveal
    public void reveal() {
            FadeTransition fade = new FadeTransition(Duration.millis(3000), this);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.setCycleCount(1);
            fade.play();
    }

    public ListProperty<Pair<String, Integer>> scoreProperty() {
        return scores;
    }

}
