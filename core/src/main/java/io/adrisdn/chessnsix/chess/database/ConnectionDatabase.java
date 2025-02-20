package io.adrisdn.chessnsix.chess.database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import io.adrisdn.chessnsix.chess.engine.League;
import io.adrisdn.chessnsix.chess.engine.board.Move;
import io.adrisdn.chessnsix.chess.engine.board.MoveLog;
import io.adrisdn.chessnsix.chess.engine.player.Player;

public class ConnectionDatabase implements AutoCloseable {
	protected final Connection connection;

	public static final String DATABASE_PATH = "sqlite/database.db";

	private static final String CREATE_GAMES_TABLE = "CREATE TABLE IF NOT EXISTS games ("
		+ "id INTEGER PRIMARY KEY,"
		+ "date TEXT,"
		+ "number_moves INTEGER,"
		+ "winner TEXT"
		+ ");";

	private PreparedStatement insertGame = null;
	private final String QUERY_INSERT_GAME = "INSERT INTO GAMES (date, number_moves, winner) VALUES (?, ?, ?)";

	private static final String CREATE_MOVES_TABLE = "CREATE TABLE IF NOT EXISTS moves ("
		+ "move TEXT,"
		+ "id_game INTEGER,"
		+ "FOREIGN KEY (id_game) REFERENCES games(id),"
		+ "PRIMARY KEY (move, id_game)"
		+ ");";

	private PreparedStatement insertMoves = null;
	private static final String QUERY_INSERT_MOVE = "INSERT INTO moves (move, id_game) VALUES (?, ?)";

	public ConnectionDatabase(String databasePath, String databaseFileName) throws SQLException, ClassNotFoundException {
		Class.forName("org.sqlite.JDBC");
		connection = DriverManager.getConnection(String.format("jdbc:sqlite:%s/%s", databasePath, databaseFileName));
		createDatabase();
	}

	public ConnectionDatabase(String databaseFullFileName) throws SQLException, ClassNotFoundException {
		Class.forName("org.sqlite.JDBC");
		connection = DriverManager.getConnection(String.format("jdbc:sqlite:%s", databaseFullFileName));
		createDatabase();
	}

	public void createDatabase() throws SQLException {
		connection.createStatement().executeUpdate(CREATE_GAMES_TABLE);
		connection.createStatement().executeUpdate(CREATE_MOVES_TABLE);
	}

	public void insertGame(final MoveLog moveLog, Player whitePlayer) throws SQLException {
		if (whitePlayer.getLeague() != League.WHITE) {
			throw new IllegalArgumentException();
		}
		if (insertGame == null) {
			insertGame = connection.prepareStatement(QUERY_INSERT_GAME);
		}
		if (insertMoves == null) {
			insertMoves = connection.prepareStatement(QUERY_INSERT_MOVE);
		}

		insertGame.setDate(1, new Date(System.currentTimeMillis()));
		insertGame.setInt(2, moveLog.size());
		insertGame.setString(2, whitePlayer.isInStalemate() ? "½ - ½" : whitePlayer.isInCheckmate() || whitePlayer.isTimeOut() ? "0 - 1" : "1 - 0");

		insertGame.executeUpdate();

		int idGame = 0;
		ResultSet resultSet = connection.prepareStatement("SELECT id FROM games ORDER BY id DESC LIMIT 1").executeQuery();
		if (resultSet.next()) {
			idGame = resultSet.getInt(1);
		}

		for (int i = 0; i < moveLog.size(); i++) {
			insertMoves.setString(1, moveLog.get(i).toString());
			insertMoves.setInt(2, idGame);
			insertMoves.executeUpdate();
		}
	}

	@Override
	public void close() throws SQLException {
		this.connection.close();
	}
}
