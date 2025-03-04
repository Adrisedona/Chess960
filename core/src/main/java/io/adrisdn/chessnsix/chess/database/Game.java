package io.adrisdn.chessnsix.chess.database;

import com.google.common.collect.ImmutableList;

/**
 * Represents a chess game with metadata including ID, date, number of moves,
 * result, and final position in FEN format.
 */
public class Game {
	private final int id;
	private final String date;
	private final int numberMoves;
	private final String result;
	private final String finalPositionFen;

	private ImmutableList<String> moves;

	/**
	 * Constructs a Game instance with the given parameters.
	 *
	 * @param id               The unique identifier of the game.
	 * @param date             The date when the game was played.
	 * @param numberMoves      The total number of moves played in the game.
	 * @param result           The result of the game (e.g., "1-0", "0-1",
	 *                         "1/2-1/2").
	 * @param finalPositionFen The final position of the game in FEN notation.
	 * @param moves            An immutable list of moves played in the game.
	 */
	public Game(final int id, final String date, final int numberMoves, final String result,
			final String finalPositionFen, final ImmutableList<String> moves) {
		this.id = id;
		this.date = date;
		this.numberMoves = numberMoves;
		this.result = result;
		this.finalPositionFen = finalPositionFen;
		this.moves = moves;
	}

	/**
	 * Gets the game ID.
	 *
	 * @return The unique identifier of the game.
	 */
	public int getId() {
		return id;
	}

	 /**
     * Gets the date of the game.
     *
     * @return The date when the game was played.
     */
	public String getDate() {
		return date;
	}

	 /**
     * Gets the number of moves in the game.
     *
     * @return The total number of moves played in the game.
     */
	public int getNumberMoves() {
		return numberMoves;
	}

	/**
     * Gets the result of the game.
     *
     * @return The result of the game (e.g., "1-0", "0-1", "1/2-1/2").
     */
	public String getResult() {
		return result;
	}

	/**
     * Gets the final position of the game in FEN notation.
     *
     * @return The final position in FEN format.
     */
	public String getFinalPositionFen() {
		return finalPositionFen;
	}

	 /**
     * Gets the list of moves played in the game.
     *
     * @return An immutable list of moves.
     */
	public ImmutableList<String> getMoves() {
		return moves;
	}

	/**
     * Sets the list of moves played in the game.
     *
     * @param moves An immutable list of moves.
     */
	public void setMoves(ImmutableList<String> moves) {
		this.moves = moves;
	}

}
