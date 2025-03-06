package io.adrisdn.chessnsix.chess.engine.board;

import io.adrisdn.chessnsix.chess.engine.pieces.Piece;

/**
 * Represents a square in a chess board.
 */
public abstract class Tile {

	protected final int tileCoordinate;

	/**
	 * Returns an OccupiedTile if a piece is present; otherwise, returns an
	 * EmptyTile.
	 *
	 * @param tileCoordinate coordinate of the tile to create
	 * @param piece          piece that might be in that tile
	 * @return the created tile
	 */
	public static Tile createTile(final int tileCoordinate, final Piece piece) {
		return piece != null ? new OccupiedTile(tileCoordinate, piece) : new EmptyTile(tileCoordinate);
	}

	/**
	 * Initializes a tile with a specific board coordinate.
	 *
	 * @param tileCoordinate the coordinate of the tile
	 */
	private Tile(final int tileCoordinate) {
		this.tileCoordinate = tileCoordinate;
	}

	/**
	 * Returns true if a piece occupies the tile, false otherwise.
	 *
	 * @return true if a piece occupies the tile, false otherwise.
	 */
	public abstract boolean isTileOccupied();

	/**
	 * Returns the piece on the tile if occupied; otherwise, returns null.
	 *
	 * @return the piece on the tile if occupied; otherwise, null.
	 */
	public abstract Piece getPiece();

	/**
	 * Returns the coordinate of the tile.
	 *
	 * @return the coordinate of the tile.
	 */
	public final int getTileCoordinate() {
		return this.tileCoordinate;
	}

	/**
	 * Represents a tile without a piece in it.
	 */
	public static final class EmptyTile extends Tile {

		/**
		 * Initializes a tile without a piece
		 *
		 * @param coordinate the coordinate of the tile in the board
		 */
		private EmptyTile(final int coordinate) {
			super(coordinate);
		}

		@Override
		public String toString() {
			return "-";
		}

		@Override
		public boolean isTileOccupied() {
			return false;
		}

		@Override
		public Piece getPiece() {
			return null;
		}
	}

	public static final class OccupiedTile extends Tile {
		private final Piece pieceOnTile;

		/**
		 * Initializes a tile with a piece in it.
		 *
		 * @param coordinate the coordinate of the tile in the board
		 * @param the        piece in this tile.
		 */
		private OccupiedTile(final int tileCoordinate, final Piece pieceOnTile) {
			super(tileCoordinate);
			this.pieceOnTile = pieceOnTile;
		}

		@Override
		public String toString() {
			return getPiece().getLeague().isBlack() ? getPiece().toString().toLowerCase() : getPiece().toString();
		}

		@Override
		public boolean isTileOccupied() {
			return true;
		}

		@Override
		public Piece getPiece() {
			return this.pieceOnTile;
		}
	}
}
