package io.adrisdn.chessnsix.chess.engine.board;

/**
 * Used to control the board after and before a move is made
 */
public final class MoveTransition {

	private final Board latestBoard, previousBoard;
    private final MoveStatus moveStatus;

	/**
	 * Initializes a move transition by storing the latest and previous board states along with the move status.
	 *
	 * @param latestBoard The new board state after executing the move.
	 * @param previousBoard The board state before the move was made.
	 * @param moveStatus The result of the move (e.g., {@link MoveStatus#DONE}, {@link MoveStatus#ILLEGAL_MOVE}).
	 */
    public MoveTransition(final Board latestBoard, final Board previousBoard, final MoveStatus moveStatus) {
        this.latestBoard = latestBoard;
        this.previousBoard = previousBoard;
        this.moveStatus = moveStatus;
    }

	/**
	 * Returns the status of the move.
	 *
	 * @return the status of the move.
	 */
    public MoveStatus getMoveStatus() {
        return this.moveStatus;
    }

	/**
	 * Returns the board state before the move.
	 *
	 * @return the board state before the move.
	 */
    public Board getPreviousBoard() {
        return this.previousBoard;
    }

	/**
	 * Returns the board state after the move.
	 *
	 * @return the board state after the move.
	 */
    public Board getLatestBoard() {
        return this.latestBoard;
    }
}
