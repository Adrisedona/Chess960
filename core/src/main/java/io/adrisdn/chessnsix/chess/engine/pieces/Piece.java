package io.adrisdn.chessnsix.chess.engine.pieces;

import com.google.common.collect.ImmutableList;
import io.adrisdn.chessnsix.chess.engine.League;
import io.adrisdn.chessnsix.chess.engine.board.Board;
import io.adrisdn.chessnsix.chess.engine.board.Move;
import io.adrisdn.chessnsix.chess.engine.board.Move.MajorMove;
import io.adrisdn.chessnsix.chess.engine.board.MoveTransition;

/**
 * Represents a piece in a chess board
 */
public abstract class Piece {

	private final PieceType pieceType;
	private final int piecePosition;
	private final League league;
	private final int hashCode;
	private final boolean isFirstMove;

	/**
	 * Initializes a piece with type, position, color and if it has still to made
	 * its first move or not
	 *
	 * @param pieceType     Type of the piece.
	 * @param piecePosition Position on the board
	 * @param league        Color of the piece ({@link League#WHITE} or
	 *                      {@link League#BLACK}).
	 * @param isFirstMove   false if the piece has moved before, true if hasn't.
	 */
	public Piece(final PieceType pieceType, final int piecePosition, final League league, final boolean isFirstMove) {
		this.pieceType = pieceType;
		this.piecePosition = piecePosition;
		this.league = league;
		this.isFirstMove = isFirstMove;
		this.hashCode = this.generateHashCode();
	}

	/**
	 * Computes a unique hash code based on the piece's attributes.
	 *
	 * @return the hashcode of this piece
	 */
	private int generateHashCode() {
		int result = this.pieceType.hashCode();
		result = 31 * result + this.league.hashCode();
		result = 31 * result + this.piecePosition;
		result = 31 * result + (this.isFirstMove ? 1 : 0);
		return result;
	}

	// prior to JDK 7, a manual hashCode is needed
	@Override
	public int hashCode() {
		return this.hashCode;
	}

	@Override
	public boolean equals(final Object object) {

		if (this == object) {
			return true;
		}

		if (!(object instanceof Piece)) {
			return false;
		}

		final Piece otherPiece = (Piece) object;
		return this.piecePosition == otherPiece.getPiecePosition() && this.pieceType == otherPiece.getPieceType() &&
				this.league == otherPiece.getLeague() && this.isFirstMove == otherPiece.isFirstMove();
	}

	/**
	 * Returns true if the piece has never moved before.
	 *
	 * @return true if the piece has never moved before, false if it has.
	 */
	public final boolean isFirstMove() {
		return this.isFirstMove;
	}

	/**
	 * Determines valid moves for the piece based on the board state.
	 *
	 * @param board board where the piece is
	 * @return a list with the legal moves of the piece.
	 */
	public abstract ImmutableList<Move> calculateLegalMoves(final Board board);

	/**
	 * Simulates a potential move and checks if it is valid.
	 *
	 * @param board                          board where the move happens.
	 * @param candidateDestinationCoordinate coordiante where the piece tries to
	 *                                       move.
	 * @return true if the piece can move there, false if not.
	 */
	protected final boolean isLegalMove(final Board board, final int candidateDestinationCoordinate) {
		try {
			// make a move, if the move is safe, return true, else false
			final MoveTransition moveTransition = board.currentPlayer()
					.makeMove(new MajorMove(board, this, candidateDestinationCoordinate));
			return moveTransition.getMoveStatus().isDone();
		} catch (final RuntimeException e) {
			// for catching null board at the beginning of the game
			return true;
		}
	}

	/**
	 * Creates a new instance of the piece in the new position after a move.
	 *
	 * @param move move to make.
	 * @return piece after the move is made.
	 */
	public abstract Piece movedPiece(final Move move);

	/**
	 * Obtains the color of the piece.
	 *
	 * @return the color of the piece.
	 */
	public final League getLeague() {
		return this.league;
	}

	/**
	 * Returns the current position of the piece.
	 *
	 * @return the current position of the piece.
	 */
	public final int getPiecePosition() {
		return this.piecePosition;
	}

	/**
	 * Returns the type of the piece.
	 *
	 * @return the type of the piece.
	 */
	public final PieceType getPieceType() {
		return this.pieceType;
	}

	/**
	 * Returns the point value of the piece.
	 * @return the point value of the piece.
	 */
	public final int getPieceValue() {
		return this.pieceType.getPieceValue();
	}
}
