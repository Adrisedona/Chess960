package io.adrisdn.chessnsix.chess.engine.board;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import io.adrisdn.chessnsix.chess.engine.pieces.Piece;
import io.adrisdn.chessnsix.chess.engine.pieces.PieceType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Utility class for various board-related calculations and constants.
 */
public final class BoardUtils {

	public static final ImmutableList<String> ALGEBRAIC_NOTATION = initializeAlgebraicNotation();
	public static final int NUM_TILES = 64, NUM_TILES_PER_ROW = 8;
	public static final ImmutableList<Boolean> FIRST_COLUMN = initColumn(0);
	public static final ImmutableList<Boolean> SECOND_COLUMN = initColumn(1);
	public static final ImmutableList<Boolean> SEVENTH_COLUMN = initColumn(6);
	public static final ImmutableList<Boolean> EIGHTH_COLUMN = initColumn(7);
	public static final ImmutableList<Boolean> FIRST_ROW = initRow(0);
	public static final ImmutableList<Boolean> SECOND_ROW = initRow(8);
	public static final ImmutableList<Boolean> THIRD_ROW = initRow(16);
	public static final ImmutableList<Boolean> FIFTH_ROW = initRow(32);
	public static final ImmutableList<Boolean> SEVENTH_ROW = initRow(48);
	public static final ImmutableList<Boolean> EIGHTH_ROW = initRow(56);
	public static final ImmutableMap<String, Integer> POSITION_TO_COORDINATE = initializePositionToCoordinateMap();
	public static final int DEFAULT_TIMER_MINUTE = 5, DEFAULT_TIMER_SECOND = 0, DEFAULT_TIMER_MILLISECOND = 0;

	/**
	 * Private constructor to prevent instantiation.
	 */
	private BoardUtils() {
		throw new RuntimeException("Cannot instantiate BoardUtils");
	}

	/**
	 * Returns a stream of board tile numbers (0 to 63).
	 *
	 * @return Stream of integers representing tile coordinates.
	 */
	public static Stream<Integer> getBoardNumStream() {
		return IntStream.range(0, NUM_TILES).boxed();
	}

	/**
	 * Checks if a given tile coordinate is valid on the board.
	 *
	 * @param coordinate The tile coordinate.
	 * @return True if the coordinate is valid, false otherwise.
	 */
	public static boolean isValidTileCoordinate(final int coordinate) {
		return coordinate >= 0 && coordinate < NUM_TILES;
	}

	/**
	 * Gets the algebraic notation of a tile given its coordinate.
	 *
	 * @param destinationCoordinate The tile coordinate.
	 * @return The algebraic notation of the tile.
	 */
	public static String getPositionAtCoordinate(final int destinationCoordinate) {
		return ALGEBRAIC_NOTATION.get(destinationCoordinate);
	}

	/**
	 * Gets the coordinate of a tile given its algebraic notation.
	 *
	 * @param destinationPosition The algebraic notation of the tile.
	 * @return The integer coordinate of the tile.
	 */
	public static int getCoordinateAtPosition(final String destinationPosition) {
		return POSITION_TO_COORDINATE.get(destinationPosition);
	}

	/**
	 * Determines if a move results in a king being under threat.
	 *
	 * @param move The move to check.
	 * @return True if the move puts the king in check, false otherwise.
	 */
	public static boolean kingThreat(final Move move) {
		return move.getBoard().currentPlayer().makeMove(move).getLatestBoard().currentPlayer().isInCheck();
	}

	/**
	 * Checks if the board state is an endgame scenario (checkmate or stalemate).
	 *
	 * @param board The board to check.
	 * @return True if it is an endgame scenario, false otherwise.
	 */
	public static boolean isEndGameScenario(final Board board) {
		return board.currentPlayer().isInCheckmate() || board.currentPlayer().isInStalemate();
	}

	/**
	 * Initializes a list of chessboard positions in algebraic notation, starting
	 * from the top-left corner (a8) to the bottom-right corner (h1). The notation
	 * represents each square of a standard 8x8 chessboard, where columns are
	 * labeled a to h and rows are labeled 1 to 8.
	 *
	 * @return An immutable list of strings representing the chessboard positions
	 *         from a8 to h1 in algebraic notation.
	 */
	private static ImmutableList<String> initializeAlgebraicNotation() {
		return ImmutableList.copyOf(Arrays.asList(
				"a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8",
				"a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
				"a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
				"a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5",
				"a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
				"a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
				"a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
				"a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1"));
	}

	/**
	 * Generates a list of integers representing the column positions on a
	 * chessboard.
	 *
	 * @param columnList The current list of column positions being built.
	 * @param column     The starting column index
	 * @return An immutable list of integers representing the column positions on
	 *         the chessboard.
	 */
	private static ImmutableList<Integer> generateColumnList(final ImmutableList<Integer> columnList,
			final int column) {
		return column < NUM_TILES
				? generateColumnList(ImmutableList.<Integer>builder().addAll(columnList).add(column).build(),
						column + NUM_TILES_PER_ROW)
				: columnList;
	}

	/**
	 * Generates a list of boolean values for a specified column. Each boolean value
	 * indicates whether the current index on the board belongs to the given column.
	 *
	 * @param column The column index for which the initialization is performed.
	 * @return An immutable list of booleans where each element indicates whether
	 *         the corresponding index on the board belongs to the specified column.
	 */
	private static ImmutableList<Boolean> initColumn(final int column) {
		final ImmutableList<Integer> columnList = generateColumnList(ImmutableList.of(), column);
		return ImmutableList.copyOf(getBoardNumStream().map(columnList::contains).collect(Collectors.toList()));
	}

	/**
	 * generates a list of integers representing the row positions on the
	 * chessboard. It starts from a given row and continues to add subsequent row
	 * indices until the method has processed all the rows, checking for the first
	 * column based on the boolean firstColumn.
	 *
	 * @param rowList     The current list of row positions being built
	 * @param row         The starting row index.
	 * @param firstColumn A boolean flag used to determine if the first column is
	 *                    being processed.
	 * @return An immutable list of integers representing the row positions on the
	 *         chessboard.
	 */
	private static ImmutableList<Integer> generateRowList(final ImmutableList<Integer> rowList, final int row,
			final boolean firstColumn) {
		return firstColumn || row % NUM_TILES_PER_ROW != 0
				? generateRowList(ImmutableList.<Integer>builder().addAll(rowList).add(row).build(), row + 1, false)
				: rowList;
	}

	/**
	 * Generates a list of boolean values for a specified row. Each boolean value
	 * indicates whether the current index on the board belongs to the given row.
	 *
	 * @param row The row index for which the initialization is performed.
	 * @return An immutable list of booleans where each element indicates whether
	 *         the corresponding index on the board belongs to the specified row.
	 */
	private static ImmutableList<Boolean> initRow(int row) {
		final ImmutableList<Integer> rowList = generateRowList(ImmutableList.of(), row, true);
		return ImmutableList.copyOf(getBoardNumStream().map(rowList::contains).collect(Collectors.toList()));
	}

	/**
	 * Gets the most valuable victim (MVV) and least valuable aggressor (LVA) score
	 * for a move.
	 *
	 * @param move The move to evaluate.
	 * @return An integer score representing the MVV-LVA value.
	 */
	public static int mostValuableVictimLeastValuableAggressor(final Move move) {
		final int movingPieceValue = move.getMovedPiece().getPieceValue();
		return move.isAttack()
				? (move.getAttackedPiece().getPieceValue() - movingPieceValue + PieceType.KING.getPieceValue()) * 100
				: PieceType.KING.getPieceValue() - movingPieceValue;
	}

	/**
	 * Retrieves the last N moves played on the board.
	 *
	 * @param board The board to analyze.
	 * @param N     The number of moves to retrieve.
	 * @return An immutable list of the last N moves.
	 */
	public static ImmutableList<Move> lastNMoves(final Board board, final int N) {
		final List<Move> moveHistory = new ArrayList<>();
		Move currentMove = board.getTransitionMove();
		int i = 0;
		while (currentMove != Move.MoveFactory.getNullMove() && i < N) {
			moveHistory.add(currentMove);
			currentMove = currentMove.getBoard().getTransitionMove();
			i++;
		}
		return ImmutableList.copyOf(moveHistory);
	}

	private static ImmutableMap<String, Integer> initializePositionToCoordinateMap() {
		return Maps.uniqueIndex(getBoardNumStream().collect(Collectors.toList()), ALGEBRAIC_NOTATION::get);
	}

	/**
	 * Retrieves a piece at a given board position.
	 *
	 * @param board    The board to check.
	 * @param position The algebraic notation of the position.
	 * @return The piece at the given position.
	 */
	public static Piece getPieceAtPosition(final Board board, final String position) {
		return board.getAllPieces().stream()
				.filter(piece -> piece.getPiecePosition() == BoardUtils.getCoordinateAtPosition(position)).findFirst()
				.orElseThrow(() -> new IllegalStateException("Invalid Piece"));
	}
}
