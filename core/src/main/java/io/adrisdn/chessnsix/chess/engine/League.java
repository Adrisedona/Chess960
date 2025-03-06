package io.adrisdn.chessnsix.chess.engine;

import io.adrisdn.chessnsix.chess.engine.board.BoardUtils;
import io.adrisdn.chessnsix.chess.engine.player.BlackPlayer;
import io.adrisdn.chessnsix.chess.engine.player.Player;
import io.adrisdn.chessnsix.chess.engine.player.WhitePlayer;

/**
 * Represents the two primary factions in a chess game: White and Black.
 */
public enum League {
	/**
	 * Represents the White player
	 */
	WHITE {
		@Override
		public int getDirection() {
			return -1;
		}

		@Override
		public boolean isWhite() {
			return true;
		}

		@Override
		public boolean isBlack() {
			return false;
		}

		@Override
		public int getOppositeDirection() {
			return 1;
		}

		@Override
		public Player choosePlayer(final WhitePlayer whitePlayer, final BlackPlayer blackPlayer) {
			return whitePlayer;
		}

		@Override
		public boolean isPawnPromotionSquare(final int position) {
			return BoardUtils.FIRST_ROW.get(position);
		}
	},
	/**
	 * Represents the Black player
	 */
	BLACK {
		@Override
		public int getDirection() {
			return 1;
		}

		@Override
		public boolean isWhite() {
			return false;
		}

		@Override
		public boolean isBlack() {
			return true;
		}

		@Override
		public int getOppositeDirection() {
			return -1;
		}

		@Override
		public Player choosePlayer(final WhitePlayer whitePlayer, final BlackPlayer blackPlayer) {
			return blackPlayer;
		}

		@Override
		public boolean isPawnPromotionSquare(final int position) {
			return BoardUtils.EIGHTH_ROW.get(position);
		}
	};

	/**
	 * Returns the direction that the pieces of the league move.
	 *
	 * @return the direction that the pieces of the league move.
	 */
	public abstract int getDirection();

	/**
	 * Returns the opposite direction that the pieces of the league move.
	 *
	 * @return the opposite direction that the pieces of the league move.
	 */
	public abstract int getOppositeDirection();

	/**
	 * Returns true if the league is {@link League#WHITE} and false if the league is
	 * {@link League#BLACK}.
	 *
	 * @return true if the league is {@link League#WHITE} and false if the league is
	 *         {@link League#BLACK}.
	 */
	public abstract boolean isWhite();

	/**
	 * Returns true if the league is {@link League#BLACK} and false if the league is
	 * {@link League#WHITE}.
	 *
	 * @return true if the league is {@link League#BLACK} and false if the league is
	 *         {@link League#WHITE}.
	 */
	public abstract boolean isBlack();

	/**
	 * Determines if a given position on the board is a pawn promotion square for the league.
	 * @param position position to evaluate
	 * @return true if its a promotion square, false otherwise.
	 */
	public abstract boolean isPawnPromotionSquare(final int position);

	/**
	 *Returns the player associated with the league.
	 * @param whitePlayer the white player
	 * @param blackPlayer the black player
	 * @return the player correspondig with this league.
	 */
	public abstract Player choosePlayer(final WhitePlayer whitePlayer, final BlackPlayer blackPlayer);
}
