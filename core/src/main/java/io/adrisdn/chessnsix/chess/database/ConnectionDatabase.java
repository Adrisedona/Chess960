package io.adrisdn.chessnsix.chess.database;

// import android.database.sqlite.*;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import io.adrisdn.chessnsix.chess.engine.League;
// import io.adrisdn.chessnsix.chess.engine.board.Move;
import io.adrisdn.chessnsix.chess.engine.board.MoveLog;
import io.adrisdn.chessnsix.chess.engine.player.Player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.async.*;
import com.badlogic.gdx.sql.*;

public class ConnectionDatabase implements AutoCloseable {
	protected final Connection connection;
	protected Database databaseHandler;

	public static final String DATABASE_PATH = "sqlite/database.db";
	public static final String DATABASE_NAME = "chess960games";

	private static final String CREATE_GAMES_TABLE = "CREATE TABLE IF NOT EXISTS games ("
			+ "id INTEGER PRIMARY KEY,"
			+ "date TEXT,"
			+ "number_moves INTEGER,"
			+ "winner TEXT"
			+ ");";

	private PreparedStatement insertGame = null;
	private static final String QUERY_INSERT_GAME = "INSERT INTO GAMES (date, number_moves, winner) VALUES (?, ?, ?)";
	private static final String QUERY_INSERT_GAME_GDX = "INSERT INTO GAMES (date, number_moves, winner) VALUES ('%s', %d, '%s')";

	private static final String CREATE_MOVES_TABLE = "CREATE TABLE IF NOT EXISTS moves ("
			+ "move TEXT,"
			+ "id_game INTEGER,"
			+ "FOREIGN KEY (id_game) REFERENCES games(id),"
			+ "PRIMARY KEY (move, id_game)"
			+ ");";

	private static final String CREATE_DATABASE = CREATE_GAMES_TABLE + CREATE_MOVES_TABLE;

	private PreparedStatement insertMoves = null;
	private static final String QUERY_INSERT_MOVE = "INSERT INTO moves (move, id_game) VALUES (?, ?)";
	private static final String QUERY_INSERT_MOVE_GDX = "INSERT INTO moves (move, id_game) VALUES ('%s', %d)";

	public ConnectionDatabase(String databasePath, String databaseFileName)
			throws SQLException, ClassNotFoundException {
		Class.forName("org.sqlite.JDBC");
		connection = DriverManager.getConnection(String.format("jdbc:sqlite:%s/%s", databasePath, databaseFileName));
		createDatabase();
	}

	public ConnectionDatabase(String databaseFullFileName) throws SQLException, ClassNotFoundException {
		Class.forName("org.sqlite.JDBC");
		connection = DriverManager.getConnection(String.format("jdbc:sqlite:%s", databaseFullFileName));
		createDatabase();
	}

	public ConnectionDatabase() throws ClassNotFoundException, SQLException {
		this(DATABASE_PATH);
		Gdx.app.log("Database", "Database opening");
		createDatabaseGdx();
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

	protected void insertGameGdx(final MoveLog moveLog, final Player currentPlayer) {
		String result = "";
		if (currentPlayer.isInCheckmate() || currentPlayer.isTimeOut()) {
			result = currentPlayer.getLeague() == League.WHITE ? "0 - 1" : "1 - 0";
		} else {
			result = "½ - ½";
		}

		try {
			databaseHandler.execSQL(String.format(QUERY_INSERT_GAME, new Date(System.currentTimeMillis()), moveLog.size(), result));

		} catch (SQLiteGdxException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() throws SQLException, SQLiteGdxException {
		this.connection.close();
		if (databaseHandler != null) {
			this.databaseHandler.closeDatabase();
		}
	}

	private class ConnectionDatabaseAsync implements AsyncTask<Pair> {


	}
}
