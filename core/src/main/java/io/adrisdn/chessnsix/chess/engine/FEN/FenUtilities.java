package io.adrisdn.chessnsix.chess.engine.FEN;

import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import io.adrisdn.chessnsix.chess.engine.League;
import io.adrisdn.chessnsix.chess.engine.board.Board;
import io.adrisdn.chessnsix.chess.engine.board.Board.Builder;
import io.adrisdn.chessnsix.chess.engine.board.BoardUtils;
import io.adrisdn.chessnsix.chess.engine.board.MoveLog;
import io.adrisdn.chessnsix.chess.engine.pieces.Bishop;
import io.adrisdn.chessnsix.chess.engine.pieces.King;
import io.adrisdn.chessnsix.chess.engine.pieces.Knight;
import io.adrisdn.chessnsix.chess.engine.pieces.Pawn;
import io.adrisdn.chessnsix.chess.engine.pieces.Queen;
import io.adrisdn.chessnsix.chess.engine.pieces.Rook;

/**
 * Has methods to create games from FEN strings, and convert games to FEN
 */
public final class FenUtilities {

	private FenUtilities() {
		throw new RuntimeException("Non instantiable");
	}

	/**
	 * Represents a move coordinate pair (currentCoordinate →
	 * destinationCoordinate).
	 */
	private static final class Coordinate {
		private final int currentCoordinate, destinationCoordinate;

		/**
		 * Initializes a coordinate
		 *
		 * @param currentCoordinate     the original coordinate of the piece
		 * @param destinationCoordinate the target coordinate of the piece
		 */
		public Coordinate(final int currentCoordinate, final int destinationCoordinate) {
			this.currentCoordinate = currentCoordinate;
			this.destinationCoordinate = destinationCoordinate;
		}
	}

	/**
	 * Recursively builds the string containig the game data.
	 *
	 * @param coordinateList the list of moves.
	 * @param gameData       the current iteration of the generated string.
	 * @param index          the index of the move being processed in an iteration.
	 * @return the string containing the game data
	 */
	private static String formGameData(final ImmutableList<Coordinate> coordinateList, final String gameData,
			final int index) {
		if (index == coordinateList.size()) {
			return gameData;
		}
		final Coordinate coordinate = coordinateList.get(index);
		return formGameData(coordinateList,
				gameData + " " + coordinate.currentCoordinate + " " + coordinate.destinationCoordinate, index + 1);
	}

	/**
	 * Generates a string representation of game moves and player timers.
	 *
	 * @param moveLog moves made in the game
	 * @param board   end board of the game
	 * @return a string representing all the moves in the game and the time
	 *         remaining of each player
	 */
	public static String getGameData(final MoveLog moveLog, final Board board) {
		final ImmutableList<Coordinate> coordinateList = ImmutableList.copyOf(moveLog.getMoves().stream()
				.map(move -> new Coordinate(move.getCurrentCoordinate(), move.getDestinationCoordinate()))
				.collect(Collectors.toList()));
		return formGameData(coordinateList, "", 0).trim() + getPlayerTimer(board);
	}

	/**
	 * Obtains a string representing the remaining time of each player.
	 *
	 * @param board the board where the game is being played
	 * @return a string representing the remaining time of each player.
	 */
	private static String getPlayerTimer(final Board board) {
		final String whitePlayerTimer = board.whitePlayer().getMinute() + ":" + board.whitePlayer().getSecond() + ":"
				+ board.whitePlayer().getMillisecond();
		final String blackPlayerTimer = board.blackPlayer().getMinute() + ":" + board.blackPlayer().getSecond() + ":"
				+ board.blackPlayer().getMillisecond();
		return "\n" + whitePlayerTimer + "\n" + blackPlayerTimer;
	}

	/**
	 * Generates a FEN string based on the current board position.
	 *
	 * @param board the board to calculate the string from.
	 * @return the generated FEN string.
	 */
	public static String createFENFromGame(final Board board) {
		return calculateBoardText(board) + " " +
				calculateCurrentPlayerText(board) + " " +
				calculateCastleText(board) + " " +
				calculateEnPassantSquare(board) + " " +
				"0 " + board.getMoveCount();
	}

	/**
	 * Replaces all the dashes in a string with the amount of consecutives dashes
	 * that were in the string (max 8).
	 *
	 * @param string the original string
	 * @return the modified string
	 */
	private static String replaceDashWithNum(final String string) {
		return string.replaceAll("--------", "8")
				.replaceAll("-------", "7")
				.replaceAll("------", "6")
				.replaceAll("-----", "5")
				.replaceAll("----", "4")
				.replaceAll("---", "3")
				.replaceAll("--", "2")
				.replaceAll("-", "1");
	}

	/**
	 * Replaces the numbers in a string with a number of dashes equal to the
	 * replaced number (max 8).
	 *
	 * @param string the original string
	 * @return the modified string
	 */
	private static String replaceNumWithDash(final String string) {
		return string.replaceAll("/", "")
				.replaceAll("8", "--------")
				.replaceAll("7", "-------")
				.replaceAll("6", "------")
				.replaceAll("5", "-----")
				.replaceAll("4", "----")
				.replaceAll("3", "---")
				.replaceAll("2", "--")
				.replaceAll("1", "-");
	}

	/**
	 * Converts board state into FEN notation.
	 *
	 * @param board board to be converted.
	 * @return the corresponding FEN string.
	 */
	private static String calculateBoardText(final Board board) {
		final StringBuilder builder = new StringBuilder();
		builder.append(
				BoardUtils.getBoardNumStream().map(i -> board.getTile(i).toString()).collect(Collectors.joining()));
		builder.insert(8, "/");
		builder.insert(17, "/");
		builder.insert(26, "/");
		builder.insert(35, "/");
		builder.insert(44, "/");
		builder.insert(53, "/");
		builder.insert(62, "/");
		return replaceDashWithNum(builder.toString());
	}

	/**
	 * Checks if a king-side castle is allowed ("K" for White, "k" for Black).
	 *
	 * @param fenCastleString string containing all the castling rights.
	 * @param isWhite         flag. true for white, false for black.
	 * @return true if a king-side castle is allowed, false if not.
	 */
	private static boolean kingSideCastle(final String fenCastleString, final boolean isWhite) {
		return isWhite ? fenCastleString.contains("K") : fenCastleString.contains("k");
	}

	/**
	 * Checks if a queen-side castle is allowed ("Q" for White, "q" for Black).
	 *
	 * @param fenCastleString string containing all the castling rights.
	 * @param isWhite         flag. true for white, false for black.
	 * @return true if a king-side castle is allowed, false if not.
	 */
	private static boolean queenSideCastle(final String fenCastleString, final boolean isWhite) {
		return isWhite ? fenCastleString.contains("Q") : fenCastleString.contains("q");
	}

	/**
	 * Identifies the pawn involved in en passant (if applicable).
	 *
	 * @param league                 color of the piece
	 * @param fenEnPassantCoordinate coordinte where the en passant can happen.
	 * @return the pawn that can make an en passant, null if there isn't any.
	 */
	private static Pawn getEnPassantPawn(final League league, final String fenEnPassantCoordinate) {
		if (!"-".equals(fenEnPassantCoordinate)) {
			final int enPassantPawnPosition = BoardUtils.getCoordinateAtPosition(fenEnPassantCoordinate)
					- (8) * league.getDirection();
			return new Pawn(league.isBlack() ? League.WHITE : League.BLACK, enPassantPawnPosition);
		}
		return null;
	}

	/**
	 * Parses a FEN string and reconstructs the board.
	 *
	 * @param fenString   string with the board's information
	 * @param minutes     minutes for both players
	 * @param seconds     seconds for both players
	 * @param millisecond milliseconds for both players
	 * @return the corresponding board
	 */
	public static Board createGameFromFEN(final String fenString, int minutes, int seconds, int millisecond) {
		final String[] fenPartitions = fenString.trim().split(" ");

		final League playerLeague = getLeague(fenPartitions[1]);

		final Builder builder = new Builder(Integer.parseInt(fenPartitions[fenPartitions.length - 1]), playerLeague,
				getEnPassantPawn(playerLeague, fenPartitions[3]))
				.updateBlackTimer(minutes, seconds, millisecond)
				.updateWhiteTimer(minutes, seconds, millisecond);

		final boolean whiteKingSideCastle = kingSideCastle(fenPartitions[2], true);
		final boolean whiteQueenSideCastle = queenSideCastle(fenPartitions[2], true);
		final boolean blackKingSideCastle = kingSideCastle(fenPartitions[2], false);
		final boolean blackQueenSideCastle = queenSideCastle(fenPartitions[2], false);

		final String gameConfiguration = fenPartitions[0];
		final char[] boardTiles = replaceNumWithDash(gameConfiguration.replaceAll("/", "")).toCharArray();
		int i = 0;
		while (i < boardTiles.length) {
			switch (boardTiles[i]) {
				case 'r':
					builder.setPiece(new Rook(League.BLACK, i));
					i++;
					break;
				case 'n':
					builder.setPiece(new Knight(League.BLACK, i));
					i++;
					break;
				case 'b':
					builder.setPiece(new Bishop(League.BLACK, i));
					i++;
					break;
				case 'q':
					builder.setPiece(new Queen(League.BLACK, i));
					i++;
					break;
				case 'k':
					builder.setPiece(new King(League.BLACK, i, blackKingSideCastle, blackQueenSideCastle));
					i++;
					break;
				case 'p':
					builder.setPiece(new Pawn(League.BLACK, i));
					i++;
					break;
				case 'R':
					builder.setPiece(new Rook(League.WHITE, i));
					i++;
					break;
				case 'N':
					builder.setPiece(new Knight(League.WHITE, i));
					i++;
					break;
				case 'B':
					builder.setPiece(new Bishop(League.WHITE, i));
					i++;
					break;
				case 'Q':
					builder.setPiece(new Queen(League.WHITE, i));
					i++;
					break;
				case 'K':
					builder.setPiece(new King(League.WHITE, i, whiteKingSideCastle, whiteQueenSideCastle));
					i++;
					break;
				case 'P':
					builder.setPiece(new Pawn(League.WHITE, i));
					i++;
					break;
				case '-':
					i++;
					break;
				default:
					throw new RuntimeException("Invalid FEN String " + gameConfiguration);
			}
		}
		return builder.build();
	}

	/**
	 * Determines the player's turn from the FEN string ("w" → White, "b" → Black).
	 *
	 * @param moveMakerString the string representing the current player
	 * @return the color of the player that has the turn currently.
	 */
	private static League getLeague(final String moveMakerString) {
		if ("w".equals(moveMakerString)) {
			return League.WHITE;
		} else if ("b".equals(moveMakerString)) {
			return League.BLACK;
		}
		throw new RuntimeException("Invalid FEN String " + moveMakerString);
	}

	/**
	 * Identifies en passant target squares based on pawn moves.
	 *
	 * @param board game from where calculate the possible en passant.
	 * @return "-" if no en passant move is available, else the coordinate where the
	 *         en passant is possible.
	 */
	private static String calculateEnPassantSquare(final Board board) {
		final Pawn enPassantPawn = board.getEnPassantPawn();
		if (enPassantPawn != null) {
			return BoardUtils.getPositionAtCoordinate(
					enPassantPawn.getPiecePosition() - (8) * enPassantPawn.getLeague().getDirection());
		}
		return "-";
	}

	/**
	 * Returns "w" for White's turn and "b" for Black's turn.
	 *
	 * @param board game to get its current player.
	 * @return "w" for White's turn and "b" for Black's turn.
	 */
	private static String calculateCurrentPlayerText(final Board board) {
		return board.currentPlayer().getCode();
	}

	/**
	 * Determines castling rights from the board state.
	 *
	 * @param board board to calculate castling rights text
	 * @return a string with "KQkq" (King/Queen-side castling) or "-" if no castling
	 *         is possible.
	 */
	private static String calculateCastleText(final Board board) {
		final StringBuilder builder = new StringBuilder();

		if (board.whitePlayer().isKingSideCastleCapable()) {
			builder.append("K");
		}
		if (board.whitePlayer().isQueenSideCastleCapable()) {
			builder.append("Q");
		}

		if (board.blackPlayer().isKingSideCastleCapable()) {
			builder.append("k");
		}
		if (board.blackPlayer().isQueenSideCastleCapable()) {
			builder.append("q");
		}

		final String result = builder.toString();

		return result.isEmpty() ? "-" : result;
	}
}
