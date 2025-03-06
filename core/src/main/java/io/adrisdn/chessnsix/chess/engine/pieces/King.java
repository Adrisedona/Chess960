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
 * Represents a king in a chess game
 */
public final class King extends Piece {

	private static final int[] MOVE_VECTOR_COORDINATE = { -9, -8, -7, -1, 1, 7, 8, 9 };
	private final boolean isCastled;
	private final boolean kingSideCastleCapable;
	private final boolean queenSideCastleCapable;

	/**
	 * Initializes the king, with its color, position, if it has moved or not, if
	 * it's castled or not and if int can castle king side, queen side or none.
	 *
	 * @param league                 color of the piece
	 * @param piecePosition          position of the piece
	 * @param isFirstMove            true if it hasn't moved, false if it has
	 * @param isCastled              true if it has castled, false if not
	 * @param kingSideCastleCapable  true if it can castle king side, false if not
	 * @param queenSideCastleCapable true if it can castle queen side, false if not
	 */
	public King(final League league, final int piecePosition, final boolean isFirstMove,
			final boolean isCastled, final boolean kingSideCastleCapable, final boolean queenSideCastleCapable) {
		super(PieceType.KING, piecePosition, league, isFirstMove);
		this.isCastled = isCastled;
		this.kingSideCastleCapable = kingSideCastleCapable;
		this.queenSideCastleCapable = queenSideCastleCapable;
	}

	/**
	 * Initializes the king, with its color, position and the normal properties of a
	 * king in a starting position.
	 *
	 * @param league                 color of the piece
	 * @param piecePosition          position of the piece
	 * @param kingSideCastleCapable  true if it can castle king side, false if not
	 * @param queenSideCastleCapable true if it can castle queen side, false if not
	 */
	public King(final League league, final int piecePosition, final boolean kingSideCastleCapable,
			final boolean queenSideCastleCapable) {
		super(PieceType.KING, piecePosition, league, true);
		this.kingSideCastleCapable = kingSideCastleCapable;
		this.queenSideCastleCapable = queenSideCastleCapable;
		this.isCastled = false;
	}

	/**
	 * Prevents illegal left-side moves at column 1.
	 *
	 * @param currentPosition current position of the piece.
	 * @param candidateOFFSET candidate destination for the piece.
	 * @return true to exclude the column, false if otherwise.
	 */
	private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOFFSET) {
		return BoardUtils.FIRST_COLUMN.get(currentPosition)
				&& (candidateOFFSET == -9 || candidateOFFSET == 7 || candidateOFFSET == -1);
	}

	/**
	 * Prevents illegal right-side moves at column 8.
	 *
	 * @param currentPosition current position of the piece.
	 * @param candidateOFFSET candidate destination for the piece.
	 * @return true to exclude the column, false if otherwise.
	 */
	private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOFFSET) {
		return BoardUtils.EIGHTH_COLUMN.get(currentPosition)
				&& (candidateOFFSET == 9 || candidateOFFSET == -7 || candidateOFFSET == 1);
	}

	/**
	 * Returns true if the king has castled.
	 *
	 * @return true if the king has castled, false if not.
	 */
	public boolean isCastled() {
		return this.isCastled;
	}

	@Override
	public ImmutableList<Move> calculateLegalMoves(final Board board) {
		final List<Move> legalMoves = new ArrayList<>();
		for (final int currentCandidateOFFSET : MOVE_VECTOR_COORDINATE) {

			final int candidateDestinationCoordinate = super.getPiecePosition() + currentCandidateOFFSET;

			if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {

				final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);

				if (isFirstColumnExclusion(super.getPiecePosition(), currentCandidateOFFSET) ||
						isEighthColumnExclusion(super.getPiecePosition(), currentCandidateOFFSET)) {
					continue;
				}

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
		if (!this.isCastled && board.currentPlayer() != null) {
			legalMoves.addAll(
					board.currentPlayer().calculateKingCastles(board.currentPlayer().getOpponent().getLegalMoves()));
		}
		return ImmutableList.copyOf(legalMoves);
	}

	@Override
	public King movedPiece(final Move move) {
		return new King(move.getMovedPiece().getLeague(), move.getDestinationCoordinate(), false, move.isCastlingMove(),
				false, false);
	}

	@Override
	public String toString() {
		return PieceType.KING.toString();
	}
}
