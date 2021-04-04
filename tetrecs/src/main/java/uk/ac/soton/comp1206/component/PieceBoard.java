package uk.ac.soton.comp1206.component;

import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;

public class PieceBoard extends GameBoard {

    int cols;
    int rows;
    double width;
    double height;

    public PieceBoard(int cols, int rows, double width, double height) {
        super(cols, rows, width, height);
        cols = this.cols;
        rows = this.rows;
        width = this.width;
        height = this.height;

        build();
    }

    //Display the piece onto the 3x3 grid
    public void addPieceToGrid(GamePiece piece) {
        clearGrid();
        for (int i = 0; i<blocks.length; i++){
            for (int j = 0; j<blocks[i].length; j++){
                //grid.set(j, i, 14);
                if(piece.getBlocks()[j][i] >= 1) {
                    grid.set(j, i, piece.getValue());
                }
            } 
        }
        grid.set(1,1,1);
    }

    public void clearGrid() {
        for (int i = 0; i<blocks.length; i++){
            for (int j = 0; j<blocks[i].length; j++){
                grid.set(j,i,0);
            }
        }
    }
}
