package io.adrisdn.chessnsix.chess.database;

import java.sql.Date;
import java.util.ArrayList;

import io.adrisdn.chessnsix.chess.engine.League;
import io.adrisdn.chessnsix.chess.engine.FEN.FenUtilities;
import io.adrisdn.chessnsix.chess.engine.board.Board;
import io.adrisdn.chessnsix.chess.engine.board.MoveLog;
import io.adrisdn.chessnsix.chess.engine.player.Player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.async.*;
import com.google.common.collect.ImmutableList;
import com.badlogic.gdx.sql.*;

/**
 * Handles the connection with the database
 */
public final class ConnectionDatabase implements AutoCloseable {

	protected Database databaseHandler;

	private AsyncExecutor executor;

	/**
	 * Name of the database
	 */
	public static final String DATABASE_NAME = "chess960games";

	private static final String CREATE_GAMES_TABLE = "CREATE TABLE IF NOT EXISTS games ("
			+ "id INTEGER PRIMARY KEY,"
			+ "date TEXT,"
			+ "number_moves INTEGER,"
			+ "winner TEXT,"
			+ "final_position_fen TEXT"
			+ ");";

	private static final String QUERY_INSERT_GAME_GDX = "INSERT INTO GAMES (date, number_moves, winner, final_position_fen) VALUES ('%s', %d, '%s', '%s')";
	private static final String QUERY_SELECT_GAMES = "SELECT id, date, number_moves, winner, final_position_fen FROM games";

	private static final String CREATE_MOVES_TABLE = "CREATE TABLE IF NOT EXISTS moves ("
			+ "move TEXT,"
			+ "id_game INTEGER,"
			+ "number_move INTEGER,"
			+ "FOREIGN KEY (id_game) REFERENCES games(id),"
			+ "PRIMARY KEY (move, id_game, number_move)"
			+ ");";

	private static final String CREATE_DATABASE = CREATE_GAMES_TABLE + CREATE_MOVES_TABLE;

	private static final String QUERY_INSERT_MOVE_GDX = "INSERT INTO moves (move, id_game, number_move) VALUES ('%s', %d, %d)";
	private static final String QUERY_SELECT_MOVES_GAME_GDX = "SELECT move FROM moves WHERE id_game = %d";


	/**
	 * Creates the database if it doesn't exist and initializes the thread executor
	 */
	public ConnectionDatabase() {
		Gdx.app.log("Database", "Database opening");
		createDatabaseGdx();
		executor = new AsyncExecutor(1);

	}

	/**
	 * Creates the database if it doesn't exist
	 */
	public void createDatabaseGdx() {
		databaseHandler = DatabaseFactory.getNewDatabase(DATABASE_NAME, 1, CREATE_DATABASE, null);
		databaseHandler.setupDatabase();
		try {
			databaseHandler.openOrCreateDatabase();
			databaseHandler.execSQL(CREATE_DATABASE);
		} catch (SQLiteGdxException e) {
			e.printStackTrace();
		}
		Gdx.app.log("Database", "Created successfully");
	}

	/**
	 * Inserts a new played game into the database
	 * @param moveLog move history of the game
	 * @param currentPlayer player that had to move when the game ended
	 * @param board the board when the game ended
	 */
	protected void insertGameGdx(final MoveLog moveLog, final Player currentPlayer, final Board board) {
		String result = "";
		int idGame = 0;
		if (currentPlayer.isInCheckmate() || currentPlayer.isTimeOut()) {
			result = currentPlayer.getLeague() == League.WHITE ? "0 - 1" : "1 - 0";
		} else {
			result = "½ - ½";
		}

		try {
			databaseHandler.execSQL(
					String.format(QUERY_INSERT_GAME_GDX, new Date(System.currentTimeMillis()), moveLog.size(), result,
							FenUtilities.createFENFromGame(board)));
			DatabaseCursor cursor = databaseHandler.rawQuery("SELECT id FROM games ORDER BY id DESC LIMIT 1");
			if (cursor.next()) {
				idGame = cursor.getInt(0);
			}
			for (int i = 0; i < moveLog.size(); i++) {
				databaseHandler.execSQL(String.format(QUERY_INSERT_MOVE_GDX, moveLog.get(i), idGame, i + 1));
			}
		} catch (SQLiteGdxException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Inserts a new played game into the database in a new thread
	 * @param moveLog move history of the game
	 * @param currentPlayer player that had to move when the game ended
	 * @param board the board when the game ended
	 */
	public void insertGameAsync(final MoveLog moveLog, final Player currentPlayer, final Board board) {
		executor.submit(new AsyncTask<Void>() {
			@Override
			public Void call() throws Exception {
				insertGameGdx(moveLog, currentPlayer, board);
				return null;
			}
		});
	}

	/**
	 * Obtains all the games in the database
	 * @return the list of games in th database
	 */
	public ImmutableList<Game> getGames() {
		ArrayList<Game> games = new ArrayList<>();
		try {
			DatabaseCursor cursorGames = databaseHandler.rawQuery(QUERY_SELECT_GAMES);
			while (cursorGames.next()) {
				int id = cursorGames.getInt(0);
				String date = cursorGames.getString(1);
				int numberMoves = cursorGames.getInt(2);
				String winner = cursorGames.getString(3);
				String finalPositionFen = cursorGames.getString(4);
				games.add(new Game(id, date, numberMoves, winner, finalPositionFen, null));
			}
			ArrayList<String> moves = new ArrayList<>();
			for (Game game : games) {
				moves.clear();
				DatabaseCursor cursorMoves = databaseHandler.rawQuery(String.format(QUERY_SELECT_MOVES_GAME_GDX,game.getId()));
				while (cursorMoves.next()) {
					moves.add(cursorMoves.getString(0));
				}
				game.setMoves(ImmutableList.copyOf(moves));
			}
		} catch (SQLiteGdxException e) {
			e.printStackTrace();
			Gdx.app.log("Database", "Couldn't access game");
		}
		return ImmutableList.copyOf(games);
	}

	/**
	 * Obtains all the games in the database in a new thread
	 * @return the thread that will return the list of games in the database
	 */
	public AsyncResult<ImmutableList<Game>> getGamesAsync() {
		return executor.submit(new AsyncTask<ImmutableList<Game>>() {
			@Override
			public ImmutableList<Game> call() throws Exception {
				return getGames();
			}
		});
	}

	/**
	 * Closes the conection with the database and disposes the thread executor
	 */
	@Override
	public void close() throws SQLiteGdxException {
		if (databaseHandler != null) {
			this.databaseHandler.closeDatabase();
		}
		this.executor.dispose();
		Gdx.app.log("Database", "Closed succesfully");
	}

}
