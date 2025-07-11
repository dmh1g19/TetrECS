package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.game.GamePiece;

// This listener handles the next piece shape to be played by the player, it passes the shape

public interface NextPieceListener {
    public void nextPiece(GamePiece piece, GamePiece piece2);
}
