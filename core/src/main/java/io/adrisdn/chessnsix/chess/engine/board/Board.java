package io.adrisdn.chessnsix.chess.engine.board;

import static io.adrisdn.chessnsix.chess.engine.board.BoardUtils.getBoardNumStream;

import com.google.common.collect.ImmutableList;
import io.adrisdn.chessnsix.chess.engine.League;
import io.adrisdn.chessnsix.chess.engine.board.Move.MoveFactory;
import io.adrisdn.chessnsix.chess.engine.pieces.Bishop;
import io.adrisdn.chessnsix.chess.engine.pieces.King;
import io.adrisdn.chessnsix.chess.engine.pieces.Knight;
import io.adrisdn.chessnsix.chess.engine.pieces.Pawn;
import io.adrisdn.chessnsix.chess.engine.pieces.Piece;
import io.adrisdn.chessnsix.chess.engine.pieces.Queen;
import io.adrisdn.chessnsix.chess.engine.pieces.Rook;
import io.adrisdn.chessnsix.chess.engine.player.BlackPlayer;
import io.adrisdn.chessnsix.chess.engine.player.Player;
import io.adrisdn.chessnsix.chess.engine.player.WhitePlayer;

import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Represents a chess board with players, pieces, and game state.
 */
public final class Board {

	private final ImmutableList<Tile> gameBoard;
	private final ImmutableList<Piece> whitePieces, blackPieces;

	private final WhitePlayer whitePlayer;
	private final BlackPlayer blackPlayer;
	private final Player currentPlayer;

	private final Pawn enPassantPawn;
	private final int moveCount;

	private final Move transitionMove;

	/**
	 * Constructs a Board instance using the given builder.
	 *
	 * @param builder The Builder instance containing board configuration.
	 */
	private Board(final Builder builder) {
		this.gameBoard = createGameBoard(builder);
		this.whitePieces = calculateActivePieces(builder, League.WHITE);
		this.blackPieces = calculateActivePieces(builder, League.BLACK);

		this.enPassantPawn = builder.enPassantPawn;
		final ImmutableList<Move> whiteStandardLegalMoves = this.calculateLegalMoves(this.whitePieces);
		final ImmutableList<Move> blackStandardLegalMoves = this.calculateLegalMoves(this.blackPieces);

		this.whitePlayer = new WhitePlayer(this, whiteStandardLegalMoves, blackStandardLegalMoves, builder.whiteMinute,
				builder.whiteSecond, builder.whiteMillisecond);
		this.blackPlayer = new BlackPlayer(this, whiteStandardLegalMoves, blackStandardLegalMoves, builder.blackMinute,
				builder.blackSecond, builder.blackMillisecond);

		this.currentPlayer = builder.nextMoveMaker.choosePlayer(this.whitePlayer, this.blackPlayer);

		this.moveCount = builder.moveCount();
		this.transitionMove = builder.transitionMove != null ? builder.transitionMove : MoveFactory.getNullMove();
	}

	/**
	 * Calculates active pieces for the given league (color).
	 *
	 * @param builder The board builder containing piece configurations.
	 * @param league  The league (WHITE or BLACK) to filter pieces.
	 * @return An immutable list of active pieces.
	 */
	private static ImmutableList<Piece> calculateActivePieces(final Builder builder, final League league) {
		return ImmutableList.copyOf(builder.boardConfig.values().stream().filter(piece -> piece.getLeague() == league)
				.collect(Collectors.toList()));
	}

	/**
	 * Creates the game board based on the builder's configuration.
	 *
	 * @param builder The board builder containing piece placements.
	 * @return An immutable list representing the board tiles.
	 */
	public static ImmutableList<Tile> createGameBoard(final Builder builder) {
		return ImmutableList.copyOf(getBoardNumStream().map(i -> Tile.createTile(i, builder.boardConfig.get(i)))
				.collect(Collectors.toList()));
	}

	/**
	 * Creates a standard board from a move history
	 *
	 * @param whiteTimer string representing the value of the white player timer
	 * @param blackTimer string representing the value of the black player timer
	 * @return standard chess board with the pieces places matching the move history
	 */
	public static Board createStandardBoardForMoveHistory(final String[] whiteTimer, final String[] blackTimer) {
		// white to move
		final Builder builder = new Builder(0, League.WHITE, null)
				.updateWhiteTimer(Integer.parseInt(whiteTimer[0]), Integer.parseInt(whiteTimer[1]),
						Integer.parseInt(whiteTimer[2]))
				.updateBlackTimer(Integer.parseInt(blackTimer[0]), Integer.parseInt(blackTimer[1]),
						Integer.parseInt(blackTimer[2]));
		// Black Layout
		builder.setPiece(new Rook(League.BLACK, 0))
				.setPiece(new Knight(League.BLACK, 1))
				.setPiece(new Bishop(League.BLACK, 2))
				.setPiece(new Queen(League.BLACK, 3))
				.setPiece(new King(League.BLACK, 4, true, true))
				.setPiece(new Bishop(League.BLACK, 5))
				.setPiece(new Knight(League.BLACK, 6))
				.setPiece(new Rook(League.BLACK, 7));
		for (int i = 8; i < 16; i++) {
			builder.setPiece(new Pawn(League.BLACK, i));
		}
		// White Layout
		for (int i = 48; i < 56; i++) {
			builder.setPiece(new Pawn(League.WHITE, i));
		}
		builder.setPiece(new Rook(League.WHITE, 56))
				.setPiece(new Knight(League.WHITE, 57))
				.setPiece(new Bishop(League.WHITE, 58))
				.setPiece(new Queen(League.WHITE, 59))
				.setPiece(new King(League.WHITE, 60, true, true))
				.setPiece(new Bishop(League.WHITE, 61))
				.setPiece(new Knight(League.WHITE, 62))
				.setPiece(new Rook(League.WHITE, 63));
		// build the board
		return builder.build();
	}

	/**
	 * Creates a standard board with the default timer
	 *
	 * @return A standard chess board with default piece placements.
	 */
	public static Board createStandardBoardWithDefaultTimer() {
		return createStandardBoard(BoardUtils.DEFAULT_TIMER_MINUTE, BoardUtils.DEFAULT_TIMER_SECOND,
				BoardUtils.DEFAULT_TIMER_MILLISECOND);
	}

	/**
	 * Creates a standard board with a specified timer.
	 *
	 * @param minute      The number of minutes per player.
	 * @param second      The number of seconds per player.
	 * @param millisecond The number of milliseconds per player.
	 * @return A standard chess board with default piece placements.
	 */
	public static Board createStandardBoard(final int minute, final int second, final int millisecond) {
		// white to move
		final Builder builder = new Builder(0, League.WHITE, null)
				.updateWhiteTimer(minute, second, millisecond)
				.updateBlackTimer(minute, second, millisecond);
		// Black Layout
		builder.setPiece(new Rook(League.BLACK, 0))
				.setPiece(new Knight(League.BLACK, 1))
				.setPiece(new Bishop(League.BLACK, 2))
				.setPiece(new Queen(League.BLACK, 3))
				.setPiece(new King(League.BLACK, 4, true, true))
				.setPiece(new Bishop(League.BLACK, 5))
				.setPiece(new Knight(League.BLACK, 6))
				.setPiece(new Rook(League.BLACK, 7));
		for (int i = 8; i < 16; i++) {
			builder.setPiece(new Pawn(League.BLACK, i));
		}
		// White Layout
		for (int i = 48; i < 56; i++) {
			builder.setPiece(new Pawn(League.WHITE, i));
		}
		builder.setPiece(new Rook(League.WHITE, 56))
				.setPiece(new Knight(League.WHITE, 57))
				.setPiece(new Bishop(League.WHITE, 58))
				.setPiece(new Queen(League.WHITE, 59))
				.setPiece(new King(League.WHITE, 60, true, true))
				.setPiece(new Bishop(League.WHITE, 61))
				.setPiece(new Knight(League.WHITE, 62))
				.setPiece(new Rook(League.WHITE, 63));
		// build the board
		return builder.build();
	}

	/**
	 * Gets the current move count.
	 *
	 * @return The number of moves played.
	 */
	public int getMoveCount() {
		return this.moveCount;
	}

	/**
	 * Gets the current player.
	 *
	 * @return The player whose turn it is.
	 */
	public Player currentPlayer() {
		return this.currentPlayer;
	}

	/**
	 * Gets the white player.
	 *
	 * @return The white player instance.
	 */
	public Player whitePlayer() {
		return this.whitePlayer;
	}

	/**
	 * Gets the black player.
	 *
	 * @return The black player instance.
	 */
	public Player blackPlayer() {
		return this.blackPlayer;
	}

	/**
	 * Gets the white pieces
	 *
	 * @return The list of the white pieces
	 */
	public ImmutableList<Piece> getWhitePieces() {
		return this.whitePieces;
	}

	/**
	 * Gets the black pieces
	 *
	 * @return The list of the black pieces
	 */
	public ImmutableList<Piece> getBlackPieces() {
		return this.blackPieces;
	}

	/**
	 * Gets the en passant pawn if applicable.
	 *
	 * @return The en passant pawn or null if none.
	 */
	public Pawn getEnPassantPawn() {
		return this.enPassantPawn;
	}

	/**
	 * Gets the tile at a specific coordinate.
	 *
	 * @param tileCoordinate The coordinate of the tile.
	 * @return The tile at the given coordinate.
	 */
	public Tile getTile(final int tileCoordinate) {
		return this.gameBoard.get(tileCoordinate);
	}

	/**
	 * Gets the last move transition.
	 *
	 * @return The last move played.
	 */
	public Move getTransitionMove() {
		return this.transitionMove;
	}

	/**
	 * Gets all pieces on the board.
	 *
	 * @return An immutable list of all pieces.
	 */
	public ImmutableList<Piece> getAllPieces() {
		return new ImmutableList.Builder<Piece>().addAll(this.whitePieces).addAll(this.blackPieces).build();
	}

	/**
	 * Calculates all the legal moves of the pieces of the parameter list
	 *
	 * @param pieces list of pieces to calculate their legal moves
	 * @return The list of all the legal moves
	 */
	private ImmutableList<Move> calculateLegalMoves(final ImmutableList<Piece> pieces) {
		return ImmutableList.copyOf(pieces.stream().flatMap(piece -> piece.calculateLegalMoves(this).stream())
				.collect(Collectors.toList()));
	}

	/**
	 * Builder class for constructing Board instances.
	 */
	public static final class Builder {

		private final HashMap<Integer, Piece> boardConfig;
		private final League nextMoveMaker;
		private final Pawn enPassantPawn;
		private final int moveCount;
		private int whiteMinute, whiteSecond, whiteMillisecond;
		private int blackMinute, blackSecond, blackMillisecond;
		private Move transitionMove;

		/**
         * Constructs a Builder instance with initial values.
         *
         * @param moveCount     The number of moves played so far.
         * @param nextMoveMaker The league (WHITE or BLACK) that makes the next move.
         * @param enPassantPawn The en passant pawn, if any.
         */
		public Builder(final int moveCount, final League nextMoveMaker, final Pawn enPassantPawn) {
			// set initialCapacity to 32 and loadFactor to 1 to reduce chance of hash
			// collision
			this.boardConfig = new HashMap<>(32, 1);
			this.nextMoveMaker = nextMoveMaker;
			this.moveCount = moveCount;
			this.enPassantPawn = enPassantPawn;
			this.whiteMillisecond = BoardUtils.DEFAULT_TIMER_MILLISECOND;
			this.whiteSecond = BoardUtils.DEFAULT_TIMER_SECOND;
			this.whiteMinute = BoardUtils.DEFAULT_TIMER_MINUTE;
			this.blackMillisecond = BoardUtils.DEFAULT_TIMER_MILLISECOND;
			this.blackSecond = BoardUtils.DEFAULT_TIMER_SECOND;
			this.blackMinute = BoardUtils.DEFAULT_TIMER_MINUTE;
		}

		/**
         * Places a piece on the board.
         *
         * @param piece The piece to be placed.
         * @return This builder instance.
         */
		public Builder setPiece(final Piece piece) {
			this.boardConfig.put(piece.getPiecePosition(), piece);
			return this;
		}

		/**
         * Builds a Board instance.
         *
         * @return The constructed Board.
         */
		public Board build() {
			return new Board(this);
		}

		/**
         * Sets the transition move for the board.
         *
         * @param transitionMove The move transitioning to this board state.
         */
		public void setTransitionMove(final Move transitionMove) {
			this.transitionMove = transitionMove;
		}

		/**
         * Gets the number of moves played.
         *
         * @return The move count.
         */
		public int moveCount() {
			return this.moveCount;
		}

		/**
         * Updates the timer for the white player.
         *
         * @param whiteMinute      Minutes remaining.
         * @param whiteSecond      Seconds remaining.
         * @param whiteMillisecond Milliseconds remaining.
         * @return This builder instance.
         */
		public Builder updateWhiteTimer(final int whiteMinute, final int whiteSecond, final int whiteMillisecond) {
			this.whiteMinute = whiteMinute;
			this.whiteSecond = whiteSecond;
			this.whiteMillisecond = whiteMillisecond;
			return this;
		}

		/**
		 * Updates the timer for the black player.
		 *
		 * @param blackMinute      Minutes remaining.
		 * @param blackSecond      Seconds remaining.
		 * @param blackMillisecond Milliseconds remaining.
		 * @return This builder instance.
		 */
		public Builder updateBlackTimer(final int blackMinute, final int blackSecond, final int blackMillisecond) {
			this.blackMinute = blackMinute;
			this.blackSecond = blackSecond;
			this.blackMillisecond = blackMillisecond;
			return this;
		}
	}
}
