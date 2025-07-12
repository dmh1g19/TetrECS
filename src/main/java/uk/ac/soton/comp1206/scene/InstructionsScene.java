package uk.ac.soton.comp1206.scene;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.event.Multimedia;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

public class InstructionsScene extends BaseScene {
    
    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    public InstructionsScene(GameWindow gameWindow) {
        super(gameWindow);
    }

    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var menuPane = new StackPane();
        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());
        menuPane.getStyleClass().add("menu-background");

        Image titleImg = new Image(Multimedia.getImage("Instructions.png"));
        ImageView mv = new ImageView(titleImg);
        mv.setPreserveRatio(true);
        mv.setFitHeight(420);
        mv.setFitWidth(800);

        Text gridLabel = new Text("Pieces");
        gridLabel.getStyleClass().add("heading");

        int width = 50;
        int height = 50;
        int cols = 3;
        int rows = 3;
        PieceBoard pb1 = new PieceBoard(cols, rows, width, height);
        pb1.addPieceToGrid(GamePiece.createPiece(0));
        PieceBoard pb2 = new PieceBoard(cols, rows, width, height);
        pb2.addPieceToGrid(GamePiece.createPiece(1));
        PieceBoard pb3 = new PieceBoard(cols, rows, width, height);
        pb3.addPieceToGrid(GamePiece.createPiece(2));
        PieceBoard pb4 = new PieceBoard(cols, rows, width, height);
        pb4.addPieceToGrid(GamePiece.createPiece(3));
        PieceBoard pb5 = new PieceBoard(cols, rows, width, height);
        pb5.addPieceToGrid(GamePiece.createPiece(4));
        PieceBoard pb6 = new PieceBoard(cols, rows, width, height);
        pb6.addPieceToGrid(GamePiece.createPiece(5));
        PieceBoard pb7 = new PieceBoard(cols, rows, width, height);
        pb7.addPieceToGrid(GamePiece.createPiece(6));
        PieceBoard pb8 = new PieceBoard(cols, rows, width, height);
        pb8.addPieceToGrid(GamePiece.createPiece(7));
        PieceBoard pb9 = new PieceBoard(cols, rows, width, height);
        pb9.addPieceToGrid(GamePiece.createPiece(8));
        PieceBoard pb10 = new PieceBoard(cols, rows, width, height);
        pb10.addPieceToGrid(GamePiece.createPiece(9));
        PieceBoard pb11 = new PieceBoard(cols, rows, width, height);
        pb11.addPieceToGrid(GamePiece.createPiece(10));
        PieceBoard pb12 = new PieceBoard(cols, rows, width, height);
        pb12.addPieceToGrid(GamePiece.createPiece(11));
        PieceBoard pb13 = new PieceBoard(cols, rows, width, height);
        pb13.addPieceToGrid(GamePiece.createPiece(12));
        PieceBoard pb14 = new PieceBoard(cols, rows, width, height);
        pb14.addPieceToGrid(GamePiece.createPiece(13));
        PieceBoard pb15 = new PieceBoard(cols, rows, width, height);
        pb15.addPieceToGrid(GamePiece.createPiece(14));

        int spacing = 5;

        HBox hbox = new HBox();
        hbox.getChildren().addAll(pb1, pb2, pb3, pb4, pb5);
        hbox.setSpacing(spacing);
        hbox.setAlignment(Pos.CENTER);
        HBox hbox2 = new HBox();
        hbox2.getChildren().addAll(pb6, pb7, pb8, pb9, pb10);
        hbox2.setSpacing(spacing);
        hbox2.setAlignment(Pos.CENTER);
        HBox hbox3 = new HBox();
        hbox3.getChildren().addAll(pb11, pb12, pb13, pb14, pb15);
        hbox3.setSpacing(spacing);
        hbox3.setAlignment(Pos.CENTER);

        VBox vb = new VBox();
        vb.setSpacing(spacing);
        vb.setAlignment(Pos.BOTTOM_CENTER);
        vb.getChildren().addAll(gridLabel, hbox, hbox2, hbox3);

        menuPane.getChildren().addAll(mv, vb);
        StackPane.setAlignment(mv, Pos.TOP_CENTER);
        StackPane.setAlignment(vb, Pos.BOTTOM_CENTER);

        root.getChildren().add(menuPane);
    }

    @Override
    public void initialise() {
        getScene().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                gameWindow.startMenu();
            }
        });
    }

}
