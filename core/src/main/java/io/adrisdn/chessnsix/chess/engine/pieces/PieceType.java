package io.adrisdn.chessnsix.chess.engine.pieces;

/**
 * Represents different chess piece types, each with a symbol, a point value,
 * and methods to determine if the piece is a king or a rook.
 */
public enum PieceType {
	PAWN("P", 100) {
		@Override
		public boolean isKing() {
			return false;
		}

		@Override
		public boolean isRook() {
			return false;
		}
	},

	KNIGHT("N", 300) {
		@Override
		public boolean isKing() {
			return false;
		}

		@Override
		public boolean isRook() {
			return false;
		}
	},
	BISHOP("B", 300) {
		@Override
		public boolean isKing() {
			return false;
		}

		@Override
		public boolean isRook() {
			return false;
		}
	},
	ROOK("R", 500) {
		@Override
		public boolean isKing() {
			return false;
		}

		@Override
		public boolean isRook() {
			return true;
		}
	},
	QUEEN("Q", 900) {
		@Override
		public boolean isKing() {
			return false;
		}

		@Override
		public boolean isRook() {
			return false;
		}
	},
	KING("K", 10000) {
		@Override
		public boolean isKing() {
			return true;
		}

		@Override
		public boolean isRook() {
			return false;
		}
	};

	private final String pieceName;
	private final int pieceValue;

	/**
	 * Initializes a piece type, with its name and value.
	 *
	 * @param pieceName  name of the piece, for FEN and chess notation
	 *                   representation.
	 * @param pieceValue value of the piece, for AI evaluation.
	 */
	PieceType(final String pieceName, final int pieceValue) {

		this.pieceName = pieceName;
		this.pieceValue = pieceValue;
	}

	@Override
	public String toString() {
		return this.pieceName;
	}

	/**
	 * Returns the value of the piece type.
	 * @return the value of the piece type.
	 */
	public int getPieceValue() {
		return this.pieceValue;
	}

	/**
	 * Determines if the piece is a king (true for {@link PieceType#KING}, false for others).
	 *
	 * @return true for {@link PieceType#KING}, false for others.
	 */
	public abstract boolean isKing();

	/**
	 * Determines if the piece is a rook (true for {@link PieceType#ROOK}, false for others).
	 *
	 * @return true for {@link PieceType#ROOK}, false for others.
	 */
	public abstract boolean isRook();
}
