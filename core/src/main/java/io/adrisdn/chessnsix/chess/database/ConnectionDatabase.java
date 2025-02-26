package io.adrisdn.chessnsix.chess.database;

// import android.database.sqlite.*;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import io.adrisdn.chessnsix.chess.engine.League;
import io.adrisdn.chessnsix.chess.engine.FEN.FenUtilities;
import io.adrisdn.chessnsix.chess.engine.board.Board;
// import io.adrisdn.chessnsix.chess.engine.board.Move;
import io.adrisdn.chessnsix.chess.engine.board.MoveLog;
import io.adrisdn.chessnsix.chess.engine.player.Player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.async.*;
import com.google.common.collect.ImmutableList;
import com.badlogic.gdx.sql.*;

public class ConnectionDatabase implements AutoCloseable {
	protected Connection connection;
	protected Database databaseHandler;

	private AsyncExecutor executor;

	public static final String DATABASE_PATH = "sqlite/database.db";
	public static final String DATABASE_NAME = "chess960games";

	private static final String CREATE_GAMES_TABLE = "CREATE TABLE IF NOT EXISTS games ("
			+ "id INTEGER PRIMARY KEY,"
			+ "date TEXT,"
			+ "number_moves INTEGER,"
			+ "winner TEXT,"
			+ "final_position_fen TEXT"
			+ ");";

	private PreparedStatement insertGame = null;
	private static final String QUERY_INSERT_GAME = "INSERT INTO GAMES (date, number_moves, winner, final_position_fen) VALUES (?, ?, ?, ?)";
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

	private PreparedStatement insertMoves = null;
	private static final String QUERY_INSERT_MOVE = "INSERT INTO moves (move, id_game, number_move) VALUES (?, ?, ?)";
	private static final String QUERY_INSERT_MOVE_GDX = "INSERT INTO moves (move, id_game, number_move) VALUES ('%s', %d, %d)";
	private static final String QUERY_SELECT_MOVES_GAME_GDX = "SELECT move FROM moves WHERE id_game = %d";

	public ConnectionDatabase(String databasePath, String databaseFileName) {
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager
					.getConnection(String.format("jdbc:sqlite:%s/%s", databasePath, databaseFileName));
			createDatabase();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public ConnectionDatabase(String databaseFullFileName) {
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection(String.format("jdbc:sqlite:%s", databaseFullFileName));
			createDatabase();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public ConnectionDatabase() {
		// this(DATABASE_PATH);
		Gdx.app.log("Database", "Database opening");
		createDatabaseGdx();
		executor = new AsyncExecutor(1);

	}

	public void createDatabase() throws SQLException {
		connection.createStatement().executeUpdate(CREATE_GAMES_TABLE);
		connection.createStatement().executeUpdate(CREATE_MOVES_TABLE);
	}

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

	public void insertGame(final MoveLog moveLog, final Player currentPlayer) throws SQLException {
		String result = "";
		if (currentPlayer.isInCheckmate() || currentPlayer.isTimeOut()) {
			result = currentPlayer.getLeague() == League.WHITE ? "0 - 1" : "1 - 0";
		} else {
			result = "½ - ½";
		}

		if (insertGame == null) {
			insertGame = connection.prepareStatement(QUERY_INSERT_GAME);
		}
		if (insertMoves == null) {
			insertMoves = connection.prepareStatement(QUERY_INSERT_MOVE);
		}

		insertGame.setDate(1, new Date(System.currentTimeMillis()));
		insertGame.setInt(2, moveLog.size());
		insertGame.setString(2, result);

		insertGame.executeUpdate();

		int idGame = 0;
		ResultSet resultSet = connection.prepareStatement("SELECT id FROM games ORDER BY id DESC LIMIT 1")
				.executeQuery();
		if (resultSet.next()) {
			idGame = resultSet.getInt(1);
		}

		for (int i = 0; i < moveLog.size(); i++) {
			insertMoves.setString(1, moveLog.get(i).toString());
			insertMoves.setInt(2, idGame);
			insertMoves.executeUpdate();
		}
	}

	public void clearDatabase() throws SQLException {
		connection.createStatement().executeQuery("DELETE FROM moves");
		connection.createStatement().executeQuery("DELETE FROM games");
	}

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

	public void insertGameAsync(final MoveLog moveLog, final Player currentPlayer, final Board board) {
		executor.submit(new AsyncTask<Void>() {
			@Override
			public Void call() throws Exception {
				insertGameGdx(moveLog, currentPlayer, board);
				return null;
			}
		});
	}

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

	public AsyncResult<ImmutableList<Game>> getGamesAsync() {
		return executor.submit(new AsyncTask<ImmutableList<Game>>() {
			@Override
			public ImmutableList<Game> call() throws Exception {
				return getGames();
			}
		});
	}

	@Override
	public void close() throws SQLException, SQLiteGdxException {
		if (databaseHandler != null) {
			this.databaseHandler.closeDatabase();
		}
		this.executor.dispose();
		Gdx.app.log("Database", "Closed succesfully");
		if (connection != null) {
			this.connection.close();
		}
	}

}
