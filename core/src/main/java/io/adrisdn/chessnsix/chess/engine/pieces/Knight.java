package io.adrisdn.chessnsix.chess.engine.pieces;

import com.google.common.collect.ImmutableList;
import io.adrisdn.chessnsix.chess.engine.League;
import io.adrisdn.chessnsix.chess.engine.board.Board;
import io.adrisdn.chessnsix.chess.engine.board.BoardUtils;
import io.adrisdn.chessnsix.chess.engine.board.Move;
import io.adrisdn.chessnsix.chess.engine.board.Move.MajorAttackMove;
import io.adrisdn.chessnsix.chess.engine.board.Move.MajorMove;
import io.adrisdn.chessnsix.chess.engine.board.Tile;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a knight in a chess game.
 */
public final class Knight extends Piece {

	private static final int[] MOVE_VECTOR_COORDINATE = { -17, -15, -10, -6, 6, 10, 15, 17 };

	/**
	 * Initializes a knight piece with first move set to true.
	 *
	 * @param league        color of the piece.
	 * @param piecePosition position of the piece
	 */
	public Knight(final League league, final int piecePosition) {
		super(PieceType.KNIGHT, piecePosition, league, true);
	}

	/**
	 * Initializes a knight piece with specified first move status.
	 *
	 * @param league        color of the piece.
	 * @param piecePosition position of the piece
	 * @param isFirstMove   true if it hasn't moved, false if it has.
	 */
	public Knight(final League league, final int piecePosition, final boolean isFirstMove) {
		super(PieceType.KNIGHT, piecePosition, league, isFirstMove);
	}

	@Override
	public ImmutableList<Move> calculateLegalMoves(Board board) {
		final List<Move> legalMoves = new ArrayList<>();
		for (final int currentCandidateOFFSET : MOVE_VECTOR_COORDINATE) {

			final int candidateDestinationCoordinate = super.getPiecePosition() + currentCandidateOFFSET;

			// not out of bound
			if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {

				if (isFirstColumnExclusion(super.getPiecePosition(), currentCandidateOFFSET) ||
						isSecondColumnExclusion(super.getPiecePosition(), currentCandidateOFFSET) ||
						isSeventhColumnExclusion(super.getPiecePosition(), currentCandidateOFFSET) ||
						isEighthColumnExclusion(super.getPiecePosition(), currentCandidateOFFSET)) {
					continue;
				}

				final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
				if (!candidateDestinationTile.isTileOccupied()
						&& this.isLegalMove(board, candidateDestinationCoordinate)) {
					legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));

				} else if (candidateDestinationTile.isTileOccupied()) {
					final Piece pieceDestination = candidateDestinationTile.getPiece();
					final League league = pieceDestination.getLeague();

					if (this.getLeague() != league && this.isLegalMove(board, candidateDestinationCoordinate)) {
						legalMoves.add(
								new MajorAttackMove(board, this, candidateDestinationCoordinate, pieceDestination));
					}
				}
			}
		}

		return ImmutableList.copyOf(legalMoves);
	}

	@Override
	public Knight movedPiece(Move move) {
		return new Knight(move.getMovedPiece().getLeague(), move.getDestinationCoordinate(), false);
	}

	@Override
	public String toString() {
		return PieceType.KNIGHT.toString();
	}

	/**
	 * Prevents illegal moves that would go out of bounds from the first column.
	 *
	 * @param currentPosition current position of the piece.
	 * @param candidateOFFSET candidate destination for the piece.
	 * @return true to exclude the column, false if otherwise.
	 */
	private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOFFSET) {
		return BoardUtils.FIRST_COLUMN.get(currentPosition)
				&& (candidateOFFSET == -17 || candidateOFFSET == -10 || candidateOFFSET == 6 || candidateOFFSET == 15);
	}

	/**
	 * Prevents illegal moves from the second column.
	 *
	 * @param currentPosition current position of the piece.
	 * @param candidateOFFSET candidate destination for the piece.
	 * @return true to exclude the column, false if otherwise.
	 */
	private static boolean isSecondColumnExclusion(final int currentPosition, final int candidateOFFSET) {
		return BoardUtils.SECOND_COLUMN.get(currentPosition) && (candidateOFFSET == -10 || candidateOFFSET == 6);
	}

	/**
	 * Prevents illegal moves from the seventh column.
	 *
	 * @param currentPosition current position of the piece.
	 * @param candidateOFFSET candidate destination for the piece.
	 * @return true to exclude the column, false if otherwise.
	 */
	private static boolean isSeventhColumnExclusion(final int currentPosition, final int candidateOFFSET) {
		return BoardUtils.SEVENTH_COLUMN.get(currentPosition) && (candidateOFFSET == -6 || candidateOFFSET == 10);
	}

	/**
	 * Prevents illegal moves from the eighth column.
	 *
	 * @param currentPosition current position of the piece.
	 * @param candidateOFFSET candidate destination for the piece.
	 * @return true to exclude the column, false if otherwise.
	 */
	private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOFFSET) {
		return BoardUtils.EIGHTH_COLUMN.get(currentPosition)
				&& (candidateOFFSET == -15 || candidateOFFSET == -6 || candidateOFFSET == 10 || candidateOFFSET == 17);
	}
}
