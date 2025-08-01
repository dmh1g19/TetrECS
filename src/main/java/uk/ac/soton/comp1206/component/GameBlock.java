package uk.ac.soton.comp1206.component;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.*;

/**
 * The Visual User Interface component representing a single block in the grid.
 *
 * Extends Canvas and is responsible for drawing itself.
 *
 * Displays an empty square (when the value is 0) or a coloured square depending on value.
 *
 * The GameBlock value should be bound to a corresponding block in the Grid model.
 */
public class GameBlock extends Canvas {

    GraphicsContext gc = getGraphicsContext2D();

    /**
     * The set of colours for different pieces
     */
    public static final Color[] COLOURS = {
            Color.TRANSPARENT,
            Color.DEEPPINK,
            Color.RED,
            Color.ORANGE,
            Color.YELLOW,
            Color.YELLOWGREEN,
            Color.LIME,
            Color.GREEN,
            Color.DARKGREEN,
            Color.DARKTURQUOISE,
            Color.DEEPSKYBLUE,
            Color.AQUA,
            Color.AQUAMARINE,
            Color.BLUE,
            Color.MEDIUMPURPLE,
            Color.PURPLE,
            Color.rgb(0, 0, 0, 0.5)
    };

    private final GameBoard gameBoard;

    private final double width;
    private final double height;

    /**
     * The column this block exists as in the grid
     */
    private final int x;

    /**
     * The row this block exists as in the grid
     */
    private final int y;

    /**
     * The value of this block (0 = empty, otherwise specifies the colour to render as)
     */
    private final IntegerProperty value = new SimpleIntegerProperty(0);

    /**
     * Create a new single Game Block
     * @param gameBoard the board this block belongs to
     * @param x the column the block exists in
     * @param y the row the block exists in
     * @param width the width of the canvas to render
     * @param height the height of the canvas to render
     */
    public GameBlock(GameBoard gameBoard, int x, int y, double width, double height) {
        this.gameBoard = gameBoard;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;

        //A canvas needs a fixed width and height
        setWidth(width);
        setHeight(height);

        //Do an initial paint
        paint();

        getOnHover();

        //When the value property is updated, call the internal updateValue method
        value.addListener(this::updateValue);
    }

    /**
     * When the value of this block is updated,
     * @param observable what was updated
     * @param oldValue the old value
     * @param newValue the new value
     */
    private void updateValue(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        paint();
    }

    /**
     * Handle painting of the block canvas
     */
    public void paint() {
        //If the block is empty, paint as empty
        if(value.get() == 0) {
            paintEmpty();
        }
        else if(getX() == 1 && getY() == 1 && gameBoard.getBlocks().length == 3) {
            paintCenterPiece(COLOURS[value.get()]);
        }
        else {
            //If the block is not empty, paint with the colour represented by the value
            paintColor(COLOURS[value.get()]);
        }
    }

    public void getOnHover() {
        this.setOnMouseEntered(e -> {
            //logger.info("HOVER X: {}, Y: {}", getX(), getY());

            if(this.getValue() < 1) {
                //Clear
                gc.clearRect(0,1,width,height);

                //Fill - default semi-transparent grid
                gc.setFill(Color.rgb(0, 0, 0, 0.4));
                gc.fillRect(0,1, width, height);

                //Border
                gc.setStroke(Color.BLACK);
                gc.strokeRect(0,1,width,height);

            }
        });

        this.setOnMouseExited(e -> {
            if(this.getValue() < 1) {
                paintEmpty();
            }
        });
    }

    /**
     * Paint this canvas empty
     */
    private void paintEmpty() {
        //var gc = getGraphicsContext2D();

        //Clear
        gc.clearRect(0,0,width,height);

        //Fill - default semi-transparent grid
        gc.setFill(Color.rgb(0, 0, 0, 0.2));
        gc.fillRect(0,0, width, height);

        //Border
        gc.setStroke(Color.BLACK);
        gc.strokeRect(0,0,width,height);
    }

    /**
     * Paint this canvas with the given colour
     * @param colour the colour to paint
     */
    private void paintColor(Paint colour) {
        var gc = getGraphicsContext2D();

        //Clear
        gc.clearRect(0,0,width,height);

        //Colour fill
        Stop[] stops = new Stop[] { new Stop(0,((Color) colour)), new Stop(1, Color.WHITE)};
        LinearGradient lg = new LinearGradient(1, 1, 2, 0, true, CycleMethod.REFLECT, stops);

        gc.setFill(lg);
        gc.fillRect(0,0, width, height);

        //Border
        gc.setStroke(Color.BLACK);
        gc.strokeRect(0,0,width,height);

        int triangleCor = 100;
        gc.setFill(Color.rgb(0, 0, 0, 0.2));
        gc.fillPolygon(new double[]{0, triangleCor, triangleCor}, new double[]{0, triangleCor, 0}, 3);

        gc.setFill(Color.rgb(0, 0, 0, 0.4));
        gc.fillRect(1, 0, width-2, height-2);
    }

    private void paintCenterPiece(Paint colour) {
        var gc = getGraphicsContext2D();

        //Clear
        gc.clearRect(0,0,width,height);

        //Colour fill
        Stop[] stops = new Stop[] { new Stop(0,((Color) colour)), new Stop(1, Color.WHITE)};
        LinearGradient lg = new LinearGradient(1, 1, 2, 0, true, CycleMethod.REFLECT, stops);

        gc.setFill(lg);
        gc.fillRect(0,0, width, height);

        gc.setFill(Color.rgb(0, 0, 0, 0.5));
        gc.fillOval(10, 10, width-20, height-20);

        //Border
        gc.setStroke(Color.BLACK);
        gc.strokeRect(0,0,width,height);

        int triangleCor = 100;
        gc.setFill(Color.rgb(0, 0, 0, 0.2));
        gc.fillPolygon(new double[]{0, triangleCor, triangleCor}, new double[]{0, triangleCor, 0}, 3);

        gc.setFill(Color.rgb(0, 0, 0, 0.4));
        gc.fillRect(1, 0, width-2, height-2);

    }

//    public void flashGreen() {
//        AnimationTimer timer = new AnimationTimer(){
//            @Override
//            public void handle(long now) {
//                gc.setFill(Color.GREEN);
//            }
//        };
//
//        timer.start();
//    }

    /**
     * Get the column of this block
     * @return column number
     */
    public int getX() {
        return x;
    }

    /**
     * Get the row of this block
     * @return row number
     */
    public int getY() {
        return y;
    }

    /**
     * Get the current value held by this block, representing it's colour
     * @return value
     */
    public int getValue() {
        return this.value.get();
    }

    /**
     * Bind the value of this block to another property. Used to link the visual block to a corresponding block in the Grid.
     * @param input property to bind the value to
     */
    public void bind(ObservableValue<? extends Number> input) {
        value.bind(input);
    }

}
