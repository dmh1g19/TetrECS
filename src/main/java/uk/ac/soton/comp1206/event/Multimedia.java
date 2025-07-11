package uk.ac.soton.comp1206.event;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class Multimedia {
    // Playing media (Method structure)
    // -> 1) Create a media objecta (passing in the string file name)
    // -> 2) Create a MediaPlayer object (pass in the media object)
    // -> 3) Play the media (call .play() on it)

    private static final Logger logger = LogManager.getLogger(Multimedia.class);

    static MediaPlayer audioPlayer;
    static MediaPlayer musicPlayer;
    static String musicFolder = "music/";
    static String soundFolder = "sounds/";
    static String imageFolder = "images/";
    public static String scoresFolder = "scores/";
    static boolean audioEnabled = true;

    public static String getImage(String file) {
        String imageName = Multimedia.class.getResource("/" + imageFolder + file).toExternalForm();

        return imageName;
    }

    public static String getScore(String file) {
        String scoreFile = Multimedia.class.getResource("/" + scoresFolder + file).toExternalForm();

        return scoreFile;
    }

    public static String getScoreFolder() {
        String scoreFolder = Multimedia.class.getResource("/" + scoresFolder).toExternalForm();

        return scoreFolder;
    }

    public static void playSounds(String file) {
        String toPlay = Multimedia.class.getResource("/" + soundFolder + file).toExternalForm();
        if (toPlay == null) {
            logger.error("Audio resource not found: /" + musicFolder + file);
            return;
        }

        logger.info("Attempting to play background music: " + toPlay);

        try {
            Media play = new Media(toPlay);
            audioPlayer = new MediaPlayer(play);
            audioPlayer.play();
        } catch (Exception e) {
            audioEnabled = false;
            e.printStackTrace();
            logger.error("Enable to play audio, it has been disabled.");
        }

    }

    public static void playBackgroundMusic(String file) {
        // For looping audio
        String toPlay = Multimedia.class.getResource("/" + musicFolder + file).toExternalForm();

        if (toPlay == null) {
            logger.error("Audio resource not found: /" + musicFolder + file);
            return;
        }
        logger.info("Attempting to play background music: " + toPlay);

        try {
            Media play = new Media(toPlay);
            musicPlayer = new MediaPlayer(play);

            // Loop code
            musicPlayer.setOnEndOfMedia(new Runnable() {
                public void run() {
                    musicPlayer.seek(Duration.ZERO);
                }
            });

            musicPlayer.play();
            logger.info("Playing backgroudn audio: " + toPlay);
        } catch (Exception e) {
            audioEnabled = false;
            e.printStackTrace();
            logger.error("Enable to play audio, it has been disabled.");
        }
    }

    public static void stopBackgroundMusic() {
        musicPlayer.stop();
    }
}
