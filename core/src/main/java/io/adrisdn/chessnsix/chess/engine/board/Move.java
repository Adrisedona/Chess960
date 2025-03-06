package io.adrisdn.chessnsix.chess.engine.board;

import java.util.ArrayList;

import com.google.common.collect.ImmutableList;

import io.adrisdn.chessnsix.chess.engine.pieces.Pawn;
import io.adrisdn.chessnsix.chess.engine.pieces.Piece;
import io.adrisdn.chessnsix.chess.engine.pieces.Rook;

/**
 * Represents a single move made on a chessboard
 */
public abstract class Move {

	private final Board board;
	private final Piece movePiece;
	private final int destinationCoordinate;
	private final boolean isFirstMove;

	/**
	 * Initializes a move with a given board, piece, and destination coordinate. It
	 * also determines if the move is the piece's first move.
	 *
	 * @param board                 The current state of the chessboard on which the
	 *                              move is being made.
	 * @param movePiece             The piece being moved in this move
	 * @param destinationCoordinate The destination coordinate (in the form of an
	 *                              index) where the piece is moving to.
	 */
	private Move(final Board board, final Piece movePiece, final int destinationCoordinate) {
		this.board = board;
		this.movePiece = movePiece;
		this.destinationCoordinate = destinationCoordinate;
		this.isFirstMove = movePiece != null && movePiece.isFirstMove();
	}

	/**
	 * Initializes a move with a given board and destination coordinate.
	 *
	 * @param board                 The current state of the chessboard on which the
	 *                              move is being made.
	 * @param destinationCoordinate The destination coordinate (in the form of an
	 *                              index) where the piece is moving to.
	 */
	private Move(final Board board, final int destinationCoordinate) {
		this(board, null, destinationCoordinate);
	}

	/**
	 * Calculates and returns a hash code for the Move object, which is based on the
	 * destination coordinate, the moved piece, the piece's position, and whether it
	 * is the first move for that piece.
	 */
	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + this.destinationCoordinate;
		result = 31 * result + this.movePiece.hashCode();
		result = 31 * result + this.movePiece.getPiecePosition();
		result = result + (isFirstMove ? 1 : 0);
		return result;
	}

	@Override
	public boolean equals(final Object object) {

		if (this == object) {
			return true;
		}

		if (!(object instanceof Move)) {
			return false;
		}

		final Move otherMove = (Move) object;
		return this.getCurrentCoordinate() == otherMove.getCurrentCoordinate() &&
				this.getDestinationCoordinate() == otherMove.getDestinationCoordinate() &&
				this.getMovedPiece().equals(otherMove.getMovedPiece());
	}

	/**
	 * Returns the current Board object where the move is happening
	 *
	 * @return the board where the move is happening
	 */
	public final Board getBoard() {
		return this.board;
	}

	/**
	 * Returns the destination coordinate of the move.
	 *
	 * @return the destination coordinate of the move.
	 */
	public final int getDestinationCoordinate() {
		return this.destinationCoordinate;
	}

	/**
	 * Returns the piece being moved.
	 *
	 * @return the piece being moved.
	 */
	public final Piece getMovedPiece() {
		return this.movePiece;
	}

	/**
	 * Returns whether the move is the first move for the piece.
	 *
	 * @return true if it's its first move, false if not
	 */
	public final boolean isFirstMove() {
		return this.isFirstMove;
	}

	/**
	 * Returns the current position of the piece being moved on the board.
	 *
	 * @return the current position of the piece being moved on the board.
	 */
	public int getCurrentCoordinate() {
		return this.getMovedPiece().getPiecePosition();
	}

	/**
	 * Obtains if the move is a capture or not.
	 *
	 * @return true if it's a capture, false if not.
	 */
	public boolean isAttack() {
		return false;
	}

	/**
	 * Obtains if the move is a promotion or not.
	 *
	 * @return true if it's a promotion, false if not.
	 */
	public boolean isPromotionMove() {
		return false;
	}

	/**
	 * Obtains if the move is a castling move or not.
	 *
	 * @return true if it's a castling move, false if not.
	 */
	public boolean isCastlingMove() {
		return false;
	}

	/**
	 * Obtains the captured piece by this move
	 *
	 * @return the captured piece by this move
	 */
	public Piece getAttackedPiece() {
		return null;
	}

	/**
	 * Executes the move by creating a new Board object that reflects the new state
	 * after the move.
	 *
	 * @return the board after the move
	 */
	public Board execute() {

		final Board.Builder builder = new Board.Builder(this.board.getMoveCount() + 1,
				this.board.currentPlayer().getOpponent().getLeague(), null)
				.updateWhiteTimer(this.board.whitePlayer().getMinute(), this.board.whitePlayer().getSecond(),
						this.board.whitePlayer().getMillisecond())
				.updateBlackTimer(this.board.blackPlayer().getMinute(), this.board.blackPlayer().getSecond(),
						this.board.blackPlayer().getMillisecond());

		this.board.currentPlayer().getActivePieces().forEach(piece -> {
			if (!this.movePiece.equals(piece)) {
				builder.setPiece(piece);
			}
		});
		this.board.currentPlayer().getOpponent().getActivePieces().forEach(builder::setPiece);

		builder.setPiece(this.movePiece.movedPiece(this));
		builder.setTransitionMove(this);

		return builder.build();
	}

	/**
	 * Represents legal moves.
	 */
	public static final class MajorMove extends Move {

		/**
		 * Initializes a legal move with the given board, the piece being moved, and the
		 * destination coordinate.
		 *
		 * @param board                 The current state of the chessboard on which the
		 *                              move is being made.
		 * @param movePiece             The piece being moved in this move
		 * @param destinationCoordinate The destination coordinate (in the form of an
		 *                              index) where the piece is moving to.
		 */
		public MajorMove(final Board board, final Piece movePiece, final int destinationCoordinate) {
			super(board, movePiece, destinationCoordinate);
		}

		@Override
		public boolean equals(final Object object) {
			return this == object || object instanceof MajorMove && super.equals(object);
		}

		@Override
		public String toString() {
			return getMovedPiece().getPieceType().toString()
					+ BoardUtils.getPositionAtCoordinate(super.getDestinationCoordinate());
		}
	}

	/**
	 * Represents captures
	 */
	public static class AttackMove extends Move {

		private final Piece attackedPiece;

		/**
		 * Initializes an attack move with the given board, the piece being moved, the
		 * destination coordinate, and the attacked piece.
		 *
		 * @param board                 The current state of the chessboard on which the
		 *                              move is being made.
		 * @param movePiece             The piece being moved in this move
		 * @param destinationCoordinate The destination coordinate (in the form of an
		 *                              index) where the piece is moving to.
		 * @param attackedPiece         piece to be taken in the capture.
		 */
		public AttackMove(final Board board, final Piece movePiece, final int destinationCoordinate,
				final Piece attackedPiece) {
			super(board, movePiece, destinationCoordinate);
			this.attackedPiece = attackedPiece;
		}

		@Override
		public int hashCode() {
			return this.attackedPiece.hashCode() + super.hashCode();
		}

		@Override
		public boolean equals(final Object object) {
			if (this == object) {
				return true;
			}

			if (!(object instanceof AttackMove)) {
				return false;
			}

			final AttackMove otherAttackMove = (AttackMove) object;
			return super.equals(otherAttackMove) && getAttackedPiece().equals(otherAttackMove.getAttackedPiece());
		}

		@Override
		public boolean isAttack() {
			return true;
		}

		@Override
		public Piece getAttackedPiece() {
			return this.attackedPiece;
		}
	}

	/**
	 * Represents legal captures
	 */
	public static final class MajorAttackMove extends AttackMove {

		/**
		 * Initializes an attack move with the given board, the piece being moved, the
		 * destination coordinate, and the attacked piece.
		 *
		 * @param board                 The current state of the chessboard on which the
		 *                              move is being made.
		 * @param movePiece             The piece being moved in this move
		 * @param destinationCoordinate The destination coordinate (in the form of an
		 *                              index) where the piece is moving to.
		 * @param attackedPiece         piece to be taken in the capture.
		 */
		public MajorAttackMove(final Board board, final Piece piece, final int destinationCoordinate,
				final Piece pieceAttacked) {
			super(board, piece, destinationCoordinate, pieceAttacked);
		}

		@Override
		public boolean equals(final Object object) {
			return this == object || object instanceof MajorAttackMove && super.equals(object);
		}

		@Override
		public String toString() {
			return getMovedPiece().getPieceType() + BoardUtils.getPositionAtCoordinate(super.getDestinationCoordinate())
					+ "x" + this.getAttackedPiece();
		}
	}

	/**
	 * Represents pawn moves
	 */
	public static final class PawnMove extends Move {

		/**
		 * Initializes a pawn move with the given board, the piece being moved and the
		 * destination coordinate
		 *
		 * @param board                 The current state of the chessboard on which the
		 *                              move is being made.
		 * @param movePiece             The piece being moved in this move
		 * @param destinationCoordinate The destination coordinate (in the form of an
		 *                              index) where the piece is moving to.
		 */
		public PawnMove(final Board board, final Piece movePiece, final int destinationCoordinate) {
			super(board, movePiece, destinationCoordinate);
		}

		@Override
		public boolean equals(final Object object) {
			return this == object || object instanceof PawnMove && super.equals(object);
		}

		@Override
		public String toString() {
			return BoardUtils.getPositionAtCoordinate(super.getDestinationCoordinate());
		}
	}

	/**
	 * Initializes a pawn attack move with the given board, the piece being moved,
	 * the
	 * destination coordinate, and the attacked piece.
	 *
	 * @param board                 The current state of the chessboard on which the
	 *                              move is being made.
	 * @param movePiece             The piece being moved in this move
	 * @param destinationCoordinate The destination coordinate (in the form of an
	 *                              index) where the piece is moving to.
	 * @param attackedPiece         piece to be taken in the capture.
	 */
	public static class PawnAttackMove extends AttackMove {

		public PawnAttackMove(final Board board, final Piece movePiece, final int destinationCoordinate,
				final Piece attackedPiece) {
			super(board, movePiece, destinationCoordinate, attackedPiece);
		}

		@Override
		public boolean equals(final Object object) {
			return this == object || object instanceof PawnAttackMove && super.equals(object);
		}

		@Override
		public String toString() {
			return BoardUtils.getPositionAtCoordinate(super.getMovedPiece().getPiecePosition()).charAt(0) + "x"
					+ BoardUtils.getPositionAtCoordinate(super.getDestinationCoordinate());
		}
	}

	/**
	 * Represents an en passant capture
	 */
	public static final class PawnEnPassantAttackMove extends PawnAttackMove {

		/**
		 * Initializes an en passant capture move with the given board, the piece being
		 * moved, the destination coordinate, and the attacked piece.
		 *
		 * @param board                 The current state of the chessboard on which the
		 *                              move is being made.
		 * @param movePiece             The piece being moved in this move
		 * @param destinationCoordinate The destination coordinate (in the form of an
		 *                              index) where the piece is moving to.
		 * @param attackedPiece         piece to be taken in the capture.
		 */
		public PawnEnPassantAttackMove(final Board board, final Piece movePiece, final int destinationCoordinate,
				final Piece attackedPiece) {
			super(board, movePiece, destinationCoordinate, attackedPiece);
		}

		@Override
		public boolean equals(final Object object) {
			return this == object || object instanceof PawnEnPassantAttackMove && super.equals(object);
		}

		@Override
		public Board execute() {
			final Board.Builder builder = new Board.Builder(super.getBoard().getMoveCount() + 1,
					super.getBoard().currentPlayer().getOpponent().getLeague(), null)
					.updateWhiteTimer(super.getBoard().whitePlayer().getMinute(),
							super.getBoard().whitePlayer().getSecond(), super.getBoard().whitePlayer().getMillisecond())
					.updateBlackTimer(super.getBoard().blackPlayer().getMinute(),
							super.getBoard().blackPlayer().getSecond(),
							super.getBoard().blackPlayer().getMillisecond());

			super.getBoard().currentPlayer().getActivePieces().forEach(piece -> {
				if (!super.getMovedPiece().equals(piece)) {
					builder.setPiece(piece);
				}
			});
			super.getBoard().currentPlayer().getOpponent().getActivePieces().forEach(piece -> {
				if (!piece.equals(this.getAttackedPiece())) {
					builder.setPiece(piece);
				}
			});

			builder.setPiece(super.getMovedPiece().movedPiece(this));
			builder.setTransitionMove(this);

			return builder.build();
		}

		@Override
		public String toString() {
			return super.toString();
		}
	}

	/**
	 * Represents a promotion of a pawn
	 */
	public static final class PawnPromotion extends Move {

		private final Move decoratedMove;
		private final Pawn promotedPawn;
		private Piece promotedPiece;
		private final Piece minimaxPromotionPiece;

		/**
		 * Initializes a pawn promotion move
		 *
		 * @param decoratedMove         the original move
		 * @param minimaxPromotionPiece the piece that can be promoted to, for AI
		 *                              evaluation
		 */
		public PawnPromotion(final Move decoratedMove, final Piece minimaxPromotionPiece) {
			super(decoratedMove.getBoard(), decoratedMove.getMovedPiece(), decoratedMove.getDestinationCoordinate());
			this.decoratedMove = decoratedMove;
			this.promotedPawn = (Pawn) decoratedMove.getMovedPiece();
			this.minimaxPromotionPiece = minimaxPromotionPiece;
		}

		/**
		 * Sets the piece that the pawn is promoted to (e.g., Queen, Rook, Bishop, or
		 * Knight).
		 *
		 * @param piece the piece that the pawn is promoted to.
		 */
		public void setPromotedPiece(final Piece piece) {
			this.promotedPiece = piece;
		}

		/**
		 * Returns the original move before the promotion was applied.
		 *
		 * @return the original move before the promotion was applied.
		 */
		public Move getDecoratedMove() {
			return this.decoratedMove;
		}

		/**
		 * Returns the piece that replaced the promoted pawn.
		 *
		 * @return the piece that replaced the promoted pawn.
		 */
		public Piece getPromotedPiece() {
			return this.promotedPiece;
		}

		/**
		 * Returns the original pawn that was promoted.
		 *
		 * @return the original pawn that was promoted.
		 */
		public Pawn getPromotedPawn() {
			return this.promotedPawn;
		}

		@Override
		public boolean isPromotionMove() {
			return true;
		}

		@Override
		public Board execute() {

			final Board pawnMoveBoard = this.decoratedMove.execute();
			final Board.Builder builder = new Board.Builder(super.getBoard().getMoveCount() + 1,
					pawnMoveBoard.currentPlayer().getLeague(), null)
					.updateWhiteTimer(super.getBoard().whitePlayer().getMinute(),
							super.getBoard().whitePlayer().getSecond(), super.getBoard().whitePlayer().getMillisecond())
					.updateBlackTimer(super.getBoard().blackPlayer().getMinute(),
							super.getBoard().blackPlayer().getSecond(),
							super.getBoard().blackPlayer().getMillisecond());

			super.getBoard().currentPlayer().getActivePieces().forEach(piece -> {
				if (!this.promotedPawn.equals(piece)) {
					builder.setPiece(piece);
				}
			});
			super.getBoard().currentPlayer().getOpponent().getActivePieces().forEach(builder::setPiece);

			this.setPromotedPiece(this.minimaxPromotionPiece);
			builder.setPiece(this.minimaxPromotionPiece.movedPiece(this));
			return builder.build();
		}

		@Override
		public boolean isAttack() {
			return this.decoratedMove.isAttack();
		}

		@Override
		public Piece getAttackedPiece() {
			return this.decoratedMove.getAttackedPiece();
		}

		@Override
		public String toString() {
			return BoardUtils.getPositionAtCoordinate(super.getDestinationCoordinate()) + "="
					+ this.promotedPiece.toString().charAt(0);
		}

		@Override
		public int hashCode() {
			return this.decoratedMove.hashCode() + (31 * this.promotedPawn.hashCode());
		}

		@Override
		public boolean equals(final Object object) {
			return this == object || object instanceof PawnPromotion && (super.equals(object));
		}
	}

	/**
	 * Represents a pawn move that moves two tiles instead of one
	 */
	public static final class PawnJump extends Move {

		/**
		 * Initializes a pawn jump move
		 *
		 * @param board                 The current state of the chessboard on which the
		 *                              move is being made.
		 * @param movePiece             The piece being moved in this move
		 * @param destinationCoordinate The destination coordinate (in the form of an
		 *                              index) where the piece is moving to.
		 */
		public PawnJump(final Board board, final Piece movePiece, final int destinationCoordinate) {
			super(board, movePiece, destinationCoordinate);
		}

		@Override
		public Board execute() {
			final Pawn movedPawn = (Pawn) super.getMovedPiece().movedPiece(this);

			final Board.Builder builder = new Board.Builder(super.getBoard().getMoveCount() + 1,
					super.getBoard().currentPlayer().getOpponent().getLeague(), movedPawn)
					.updateWhiteTimer(super.getBoard().whitePlayer().getMinute(),
							super.getBoard().whitePlayer().getSecond(), super.getBoard().whitePlayer().getMillisecond())
					.updateBlackTimer(super.getBoard().blackPlayer().getMinute(),
							super.getBoard().blackPlayer().getSecond(),
							super.getBoard().blackPlayer().getMillisecond());

			super.getBoard().currentPlayer().getActivePieces().forEach(piece -> {
				if (!super.getMovedPiece().equals(piece)) {
					builder.setPiece(piece);
				}
			});
			super.getBoard().currentPlayer().getOpponent().getActivePieces().forEach(builder::setPiece);

			builder.setPiece(movedPawn);
			builder.setTransitionMove(this);
			return builder.build();
		}

		@Override
		public String toString() {
			return BoardUtils.getPositionAtCoordinate(super.getDestinationCoordinate());
		}
	}

	/**
	 * Represents a castling move
	 */
	private static abstract class CastleMove extends Move {

		protected final Rook castleRook;

		protected final int castleRookStart, castleRookDestination;

		/**
		 * Initializes a castling move
		 *
		 * @param board                 the board where the move happened
		 * @param movePiece             the king moved in this move
		 * @param destinationCoordinate the tile where the king goes
		 * @param castleRook            the rook involved int this castling move
		 * @param castleRookStart       the initial tile where the rook was
		 * @param castleRookDestination the tile where the rook goes
		 */
		public CastleMove(final Board board, final Piece movePiece, final int destinationCoordinate,
				final Rook castleRook, final int castleRookStart, final int castleRookDestination) {
			super(board, movePiece, destinationCoordinate);
			this.castleRook = castleRook;
			this.castleRookStart = castleRookStart;
			this.castleRookDestination = castleRookDestination;
		}

		/**
		 * Returns the rook involved in the castling move.
		 *
		 * @return the rook involved in the castling move.
		 */
		public Rook getCastleRook() {
			return this.castleRook;
		}

		@Override
		public boolean isCastlingMove() {
			return true;
		}

		@Override
		public Board execute() {

			final Board.Builder builder = new Board.Builder(super.getBoard().getMoveCount() + 1,
					super.getBoard().currentPlayer().getOpponent().getLeague(), null)
					.updateWhiteTimer(super.getBoard().whitePlayer().getMinute(),
							super.getBoard().whitePlayer().getSecond(), super.getBoard().whitePlayer().getMillisecond())
					.updateBlackTimer(super.getBoard().blackPlayer().getMinute(),
							super.getBoard().blackPlayer().getSecond(),
							super.getBoard().blackPlayer().getMillisecond());

			super.getBoard().getAllPieces().forEach(piece -> {
				if (!super.getMovedPiece().equals(piece) && !this.castleRook.equals(piece)) {
					builder.setPiece(piece);
				}
			});

			builder.setPiece(super.getMovedPiece().movedPiece(this));
			builder.setPiece(new Rook(this.castleRook.getLeague(), this.castleRookDestination, false));
			builder.setTransitionMove(this);
			return builder.build();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + this.castleRook.hashCode();
			result = prime * result + this.castleRookDestination;
			return result;
		}

		@Override
		public boolean equals(final Object object) {
			if (this == object) {
				return true;
			}
			if (!(object instanceof CastleMove)) {
				return false;
			}
			final CastleMove castleMove = (CastleMove) object;

			return super.equals(castleMove) && this.castleRook.equals(castleMove.getCastleRook());
		}
	}

	/**
	 * Represents a king side castling move
	 */
	public static final class KingSideCastleMove extends CastleMove {

		/**
		 * Initializes a king side castling move
		 *
		 * @param board                 the board where the move happened
		 * @param movePiece             the king moved in this move
		 * @param destinationCoordinate the tile where the king goes
		 * @param castleRook            the rook involved int this castling move
		 * @param castleRookStart       the initial tile where the rook was
		 * @param castleRookDestination the tile where the rook goes
		 */
		public KingSideCastleMove(final Board board, final Piece movePiece, final int destinationCoordinate,
				final Rook castleRook, final int castleRookStart, final int castleRookDestination) {
			super(board, movePiece, destinationCoordinate, castleRook, castleRookStart, castleRookDestination);
		}

		@Override
		public boolean equals(final Object object) {
			return this == object || object instanceof KingSideCastleMove && super.equals(object);
		}

		@Override
		public String toString() {
			return "O-O";
		}
	}

	/**
	 * Represents a queen side caslting move
	 */
	public static final class QueenSideCastleMove extends CastleMove {

		/**
		 * Initializes a queen side castling move
		 *
		 * @param board                 the board where the move happened
		 * @param movePiece             the king moved in this move
		 * @param destinationCoordinate the tile where the king goes
		 * @param castleRook            the rook involved int this castling move
		 * @param castleRookStart       the initial tile where the rook was
		 * @param castleRookDestination the tile where the rook goes
		 */
		public QueenSideCastleMove(final Board board, final Piece movePiece, final int destinationCoordinate,
				final Rook castleRook, final int castleRookStart, final int castleRookDestination) {
			super(board, movePiece, destinationCoordinate, castleRook, castleRookStart, castleRookDestination);
		}

		@Override
		public boolean equals(final Object object) {
			return this == object || object instanceof QueenSideCastleMove && super.equals(object);
		}

		@Override
		public String toString() {
			return "O-O-O";
		}
	}

	/**
	 * Represents a null move, used when a piece doesn't have any legal moves
	 */
	private static final class NullMove extends Move {

		/**
		 * Initializes a null move
		 */
		private NullMove() {
			super(null, 65);
		}

		@Override
		public Board execute() {
			return null;
		}

		@Override
		public int getCurrentCoordinate() {
			return -1;
		}
	}

	/**
	 * Builds moves for boards
	 */
	public static final class MoveFactory {

		private static final Move NULL_MOVE = new NullMove();

		private MoveFactory() {
			throw new RuntimeException("Not instantiatable");
		}

		/**
		 * Obtains a null move.
		 *
		 * @return a null move.
		 */
		public static Move getNullMove() {
			return NULL_MOVE;
		}

		/**
		 * Obtains all the legal moves for a piece that goes to a certain coordinate.
		 *
		 * @param board                 the board where the move is happening
		 * @param piece                 the piece to calculate its legal moves
		 * @param destinationCoordinate the coordinate in the board where the piece
		 *                              wants to move
		 * @return a list with the legal moves (usually will contain only one move,
		 *         except when castling in certain starting positions)
		 */
		public static ImmutableList<Move> createMove(final Board board, final Piece piece,
				final int destinationCoordinate) {
			ArrayList<Move> possibleMoves = new ArrayList<>();
			ImmutableList<Move> legalMoves = piece.calculateLegalMoves(board);
			for (Move move : legalMoves) {
				if (move.getCurrentCoordinate() == piece.getPiecePosition()
						&& move.getDestinationCoordinate() == destinationCoordinate) {
					if (move.getClass() == Move.PawnPromotion.class) {
						possibleMoves.add(move);
						break;
					}
					possibleMoves.add(move);
				}
			}
			if (possibleMoves.isEmpty()) {
				possibleMoves.add(NULL_MOVE);
			}
			return ImmutableList.copyOf(possibleMoves);
		}

	}
}
