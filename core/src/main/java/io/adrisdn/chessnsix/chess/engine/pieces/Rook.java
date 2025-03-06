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
 * Represents a rook in a chess board
 */
public final class Rook extends Piece {

	private static final int[] MOVE_VECTOR_COORDINATE = { -8, -1, 1, 8 };

	/**
	 * Initializes a rook piece with first move set to true.
	 *
	 * @param league        color of the piece.
	 * @param piecePosition position of the piece
	 */
	public Rook(final League league, final int piecePosition) {
		super(PieceType.ROOK, piecePosition, league, true);
	}

	/**
	 * Initializes a rook piece with specified first move status.
	 *
	 * @param league        color of the piece.
	 * @param piecePosition position of the piece
	 * @param isFirstMove   true if it hasn't moved, false if it has.
	 */
	public Rook(final League league, final int piecePosition, final boolean isFirstMove) {
		super(PieceType.ROOK, piecePosition, league, isFirstMove);
	}

	@Override
	public ImmutableList<Move> calculateLegalMoves(final Board board) {
		final List<Move> legalMoves = new ArrayList<>();

		for (final int CoordinateOFFSET : MOVE_VECTOR_COORDINATE) {

			int candidateDestinationCoordinate = super.getPiecePosition();

			while (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {

				if (isEighthColumnExclusion(candidateDestinationCoordinate, CoordinateOFFSET) ||
						isFirstColumnExclusion(candidateDestinationCoordinate, CoordinateOFFSET)) {
					break;
				}

				candidateDestinationCoordinate += CoordinateOFFSET;

				if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
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
						break;
					}
				}
			}
		}

		return ImmutableList.copyOf(legalMoves);
	}

	@Override
	public Rook movedPiece(Move move) {
		return new Rook(move.getMovedPiece().getLeague(), move.getDestinationCoordinate(), false);
	}

	@Override
	public String toString() {
		return PieceType.ROOK.toString();
	}

	/**
	 * Prevents illegal moves that would go out of bounds from the first column.
	 *
	 * @param currentPosition current position of the piece.
	 * @param candidateOFFSET candidate destination for the piece.
	 * @return true to exclude the column, false if otherwise.
	 */
	private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOFFSET) {
		return BoardUtils.FIRST_COLUMN.get(currentPosition) && (candidateOFFSET == -1);
	}

	/**
	 * Prevents illegal moves from the eighth column.
	 *
	 * @param currentPosition current position of the piece.
	 * @param candidateOFFSET candidate destination for the piece.
	 * @return true to exclude the column, false if otherwise.
	 */
	private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOFFSET) {
		return BoardUtils.EIGHTH_COLUMN.get(currentPosition) && (candidateOFFSET == 1);
	}
}
