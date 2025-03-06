package io.adrisdn.chessnsix.chess.engine.board;

import java.util.ArrayList;
import java.util.List;

/**
 * Saves all the moves that happened in a chess game.
 */
public final class MoveLog {

    private final List<Move> moves;

	/**
	 * Initializes an empty list to store moves.
	 */
    public MoveLog() {
        this.moves = new ArrayList<>();
    }

	/**
	 * Returns the list of all moves.
	 *
	 * @return the list of all moves.
	 */
    public List<Move> getMoves() {
        return this.moves;
    }

	/**
	 * Adds a move to the log.
	 *
	 * @param move the move to add.
	 */
    public void addMove(final Move move) {
        this.moves.add(move);
    }

	/**
	 * Returns the move at index i in the list.
	 *
	 * @param i index of the move
	 * @return the move at index i in the list.
	 */
    public Move get(final int i) {
        return this.moves.get(i);
    }

	/**
	 * Returns the number of moves stored in the log.
	 *
	 * @return the number of moves stored in the log.
	 */
    public int size() {
        return this.moves.size();
    }

	/**
	 * Clears all stored moves from the log.
	 */
    public void clear() {
        this.moves.clear();
    }

    @Override
    public String toString() {
        return this.moves.toString();
    }
}
