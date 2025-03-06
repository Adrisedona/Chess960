package io.adrisdn.chessnsix.chess.engine.player;

import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import io.adrisdn.chessnsix.chess.engine.League;
import io.adrisdn.chessnsix.chess.engine.board.Board;
import io.adrisdn.chessnsix.chess.engine.board.Move;
import io.adrisdn.chessnsix.chess.engine.board.MoveStatus;
import io.adrisdn.chessnsix.chess.engine.board.MoveTransition;
import io.adrisdn.chessnsix.chess.engine.board.Tile;
import io.adrisdn.chessnsix.chess.engine.pieces.King;
import io.adrisdn.chessnsix.chess.engine.pieces.Piece;

/**
 * Represents a player in a chess game.
 */
public abstract class Player {

	private final Board board;
	private final King playerKing;
	private final ImmutableList<Move> legalMoves;
	private final boolean isInCheck;
	private int minute, second, millisecond;

	/**
	 * Initializes a player.
	 *
	 * @param board              The current board instance.
	 * @param legalMoves         The list of legal moves for the player.
	 * @param opponentLegalMoves The list of legal moves for the opponent
	 * @param minute             The number of minutes remaining on the player's
	 *                           timer.
	 * @param second             The number of seconds remaining on the player's
	 *                           timer.
	 * @param millisecondThe     The number of milliseconds remaining on the
	 *                           player's
	 *                           timer.
	 */
	public Player(final Board board, final ImmutableList<Move> legalMoves, final ImmutableList<Move> opponentLegalMoves,
			final int minute, final int second, final int millisecond) {
		this.board = board;
		this.playerKing = this.establishKing();
		this.isInCheck = !Player.calculateAttacksOnTile(this.playerKing.getPiecePosition(), opponentLegalMoves)
				.isEmpty();
		this.legalMoves = ImmutableList.<Move>builder().addAll(legalMoves)
				.addAll(calculateKingCastles(opponentLegalMoves)).build();
		this.minute = minute;
		this.second = second;
		this.millisecond = millisecond;
	}

	/**
	 * Returns true if the player has no timer
	 *
	 * @return true if the player has no timer, false otherwise.
	 */
	public final boolean isNoTimer() {
		return this.minute == -1;
	}

	/**
	 * Decreases the player's timer by one millisecond. If the milliseconds reach 0,
	 * it decreases the seconds, and if seconds reach 0, it decreases the minutes.
	 */
	public final void countDown() {
		if (this.millisecond == 0) {
			if (this.second == 0) {
				if (this.minute == 0) {
					return;
				}
				this.second = 60;
				this.minute -= 1;
			}
			this.millisecond = 99;
			this.second -= 1;
		}
		this.millisecond -= 1;
	}

	/**
	 * Returns the current board instance.
	 *
	 * @return the current board instance.
	 */
	public final Board getBoard() {
		return this.board;
	}

	/**
	 * Returns the remaining time in minutes.
	 *
	 * @return the remaining time in minutes.
	 */
	public final int getMinute() {
		return this.minute;
	}

	/**
	 * Returns the remaining time in seconds.
	 *
	 * @return the remaining time in seconds.
	 */
	public final int getSecond() {
		return this.second;
	}

	/**
	 * Returns the remaining time in miliseconds.
	 *
	 * @return the remaining time in miliseconds.
	 */
	public final int getMillisecond() {
		return this.millisecond;
	}

	/**
	 * Returns true if the player’s time has run out
	 *
	 * @return true if the player’s time has run out, false otherwise.
	 */
	public final boolean isTimeOut() {
		return this.minute == 0 && this.second == 0 && this.millisecond == 0;
	}

	/**
	 * Returns the player's King piece.
	 *
	 * @return the player's King piece.
	 */
	public final King getPlayerKing() {
		return this.playerKing;
	}

	/**
	 * Returns an immutable list of legal moves for the player.
	 *
	 * @return an immutable list of legal moves for the player.
	 */
	public final ImmutableList<Move> getLegalMoves() {
		return this.legalMoves;
	}

	/**
	 * Calculates all moves that attack a particular tile.
	 *
	 * @param piecePosition tile to check its attacks.
	 * @param moves         list of moves
	 * @return a list of moves that attack the specified tile.
	 */
	public static ImmutableList<Move> calculateAttacksOnTile(final int piecePosition, final ImmutableList<Move> moves) {
		return ImmutableList.copyOf(moves.stream().filter(move -> piecePosition == move.getDestinationCoordinate())
				.collect(Collectors.toList()));
	}

	/**
	 * Initializes and returns the player's King piece from the active pieces.
	 *
	 * @return the player's King piece.
	 */
	private King establishKing() {
		return ((King) this.getActivePieces().stream().filter(piece -> piece.getPieceType().isKing()).findFirst()
				.orElseThrow(() -> new IllegalStateException("Invalid board")));
	}

	/**
	 * Returns the player's active pieces.
	 *
	 * @return the player's active pieces.
	 */
	public abstract ImmutableList<Piece> getActivePieces();

	/**
	 * Returns the player's color.
	 *
	 * @return the player's color.
	 */
	public abstract League getLeague();

	/**
	 * Returns this player's opponent.
	 *
	 * @return this player's opponent.
	 */
	public abstract Player getOpponent();

	/**
	 * Returns the code of the player to save it in a FEN string.
	 *
	 * @return the code of the player.
	 */
	public abstract String getCode();

	/**
	 * Returns true if the player is in check.
	 *
	 * @return true if the player is in check, false otherwise.
	 */
	public final boolean isInCheck() {
		return this.isInCheck;
	}

	/**
	 * Returns true if the player is in checkmate.
	 *
	 * @return true if the player is in checkmate, false otherwise.
	 */
	public final boolean isInCheckmate() {
		return this.isInCheck && this.noEscapeMoves();
	}

	/**
	 * Returns true if the player is in stalemate.
	 *
	 * @return true if the player is in stalemate, false otherwise.
	 */
	public final boolean isInStalemate() {
		final ImmutableList<Piece> activePieces = this.getActivePieces();
		final ImmutableList<Piece> opponentActivePieces = this.getOpponent().getActivePieces();
		if (activePieces.size() == 1 && opponentActivePieces.size() == 1) {
			if (activePieces.get(0) instanceof King && opponentActivePieces.get(0) instanceof King) {
				return true;
			}
			throw new IllegalStateException("If there is only 1 active piece left, it must be king, however it is "
					+ activePieces + " and " + opponentActivePieces);
		}
		this.getOpponent().getActivePieces();
		return !this.isInCheck && this.noEscapeMoves();
	}

	/**
	 * Returns the king-side castling move.
	 *
	 * @param opponentLegals the legal moves of the opponent.
	 * @return the king-side castling move.
	 */
	protected abstract Move.KingSideCastleMove getKingSideCastleMove(final ImmutableList<Move> opponentLegals);

	/**
	 * Returns the queen-side castling move.
	 *
	 * @param opponentLegals the legal moves of the opponent.
	 * @return the queen-side castling move.
	 */
	protected abstract Move.QueenSideCastleMove getQueenSideCastleMove(final ImmutableList<Move> opponentLegals);

	/**
	 * Returns the castling moves.
	 *
	 * @param opponentLegals the legal moves of the opponent.
	 * @return the castling moves.
	 */
	public abstract ImmutableList<Move> calculateKingCastles(final ImmutableList<Move> opponentLegals);

	/**
	 * Returns true if the player’s king has castled.
	 *
	 * @return true if the player’s king has castled, false otherwise.
	 */
	public final boolean isCastled() {
		return this.playerKing.isCastled();
	}

	/**
	 * Returns true if the player is capable of castling on the king-side.
	 *
	 * @return true if the player is capable of castling on the king-side, false
	 *         otherwise.
	 */
	public final boolean isKingSideCastleCapable() {
		final Tile rookTile = this.board.getTile(this.getLeague().isWhite() ? 63 : 7);
		return !(!rookTile.isTileOccupied() || this.playerKing.isCastled()) && rookTile.getPiece().isFirstMove();
	}

	/**
	 * Returns true if the player is capable of castling on the queen-side.
	 *
	 * @return true if the player is capable of castling on the queen-side, false
	 *         otherwise.
	 */
	public final boolean isQueenSideCastleCapable() {
		final Tile rookTile = this.board.getTile(this.getLeague().isWhite() ? 56 : 0);
		return !(!rookTile.isTileOccupied() || this.playerKing.isCastled()) && rookTile.getPiece().isFirstMove();
	}

	/**
	 * Returns true if the player has no legal moves that escape from the current
	 * position.
	 *
	 * @return true if the player has no legal moves that escape from the current
	 *         position, false otherwise.
	 */
	protected final boolean noEscapeMoves() {
		return this.legalMoves.stream().noneMatch(move -> this.makeMove(move).getMoveStatus().isDone());
	}

	/**
	 * Attempts to execute the given move and returns a MoveTransition object
	 * representing the result of the move.
	 *
	 * @param move the move to attempt to execute.
	 * @return a MoveTransition object representing the result of the move.
	 */
	public final MoveTransition makeMove(final Move move) {

		final Board transitionBoard = move.execute();
		if (transitionBoard != null) {
			final ImmutableList<Move> currentPlayerLegals = transitionBoard.currentPlayer().getLegalMoves();
			final ImmutableList<Move> kingAttacks = Player.calculateAttacksOnTile(
					transitionBoard.currentPlayer().getOpponent().getPlayerKing().getPiecePosition(),
					currentPlayerLegals);

			if (!kingAttacks.isEmpty()) {
				return new MoveTransition(board, board, MoveStatus.LEAVES_PLAYER_IN_CHECK);
			}

			return new MoveTransition(transitionBoard, board, MoveStatus.DONE);
		}
		return new MoveTransition(null, null, MoveStatus.ILLEGAL_MOVE);
	}
}
