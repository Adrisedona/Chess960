package io.adrisdn.chessnsix.gui;

import java.sql.SQLException;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.sql.SQLiteGdxException;

import io.adrisdn.chessnsix.chess.database.ConnectionDatabase;
import io.adrisdn.chessnsix.chess.engine.FEN.FenFisherRandom;
import io.adrisdn.chessnsix.chess.engine.board.Board;
import io.adrisdn.chessnsix.gui.board.GameProps.GameEnd;
import io.adrisdn.chessnsix.gui.screens.About;
import io.adrisdn.chessnsix.gui.screens.GameScreen;
import io.adrisdn.chessnsix.gui.screens.RecordsScreen;
import io.adrisdn.chessnsix.gui.screens.SetupGame;
import io.adrisdn.chessnsix.gui.screens.WelcomeScreen;

public final class ChessGame extends Game {

    private GameScreen gameScreen;
    private WelcomeScreen welcomeScreen;
    private About aboutScreen;
	private SetupGame setupGameScreen;
	private RecordsScreen recordsScreen;

	private ConnectionDatabase connectionDatabase;

	@Override
    public void create() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		FenFisherRandom.InitFisherRandomList();
		this.connectionDatabase = new ConnectionDatabase();
        this.gameScreen = new GameScreen(this);
        this.aboutScreen = new About(this);
        this.welcomeScreen = new WelcomeScreen(this);
		this.setupGameScreen = new SetupGame(this);
		this.recordsScreen = new RecordsScreen(this);
        this.setScreen(this.welcomeScreen);
    }

    public GameScreen getGameScreen() {
        return this.gameScreen;
    }

    public WelcomeScreen getWelcomeScreen() {
        return this.welcomeScreen;
    }

    public About getAboutScreen() {
        return this.aboutScreen;
    }

	public SetupGame getSetupGameScreen() {
		return setupGameScreen;
	}

	public RecordsScreen getRecordsScreen() {
		return recordsScreen;
	}

    public void gotoGameScreen(final GameScreen.BOARD_STATE board_state, final Board board) {
        this.gameScreen.updateChessBoard(board);
        if (board_state == GameScreen.BOARD_STATE.NEW_GAME || board_state == GameScreen.BOARD_STATE.NEW_CHESS960_GAME) {
            this.gameScreen.getMoveHistory().getMoveLog().clear();
        }
        this.gameScreen.getGameBoard().updateAiMove(null);
        this.gameScreen.getGameBoard().updateHumanMove(null);
        this.gameScreen.getMoveHistory().updateMoveHistory();
        this.gameScreen.getGameBoard().drawBoard(this.gameScreen, gameScreen.getChessBoard(), this.gameScreen.getDisplayOnlyBoard());
        this.gameScreen.getGameBoard().updateGameEnd(GameEnd.ONGOING);
        this.gameScreen.getGameTimerPanel().resetTimer(this.gameScreen.getChessBoard().whitePlayer(), this.gameScreen.getChessBoard().blackPlayer());
        this.gameScreen.getGameTimerPanel().continueTimer(true);
        Gdx.input.setInputProcessor(this.gameScreen.getStage());
        this.setScreen(this.gameScreen);
    }

	public ConnectionDatabase getConnectionDatabase() {
		return connectionDatabase;
	}

    @Override
    public void dispose() {
        this.gameScreen.dispose();
        this.welcomeScreen.dispose();
        this.aboutScreen.dispose();
		this.recordsScreen.dispose();
		this.setupGameScreen.dispose();
		try {
			this.connectionDatabase.close();
		} catch (SQLException | SQLiteGdxException e) {
			e.printStackTrace();
		}
    }
}
