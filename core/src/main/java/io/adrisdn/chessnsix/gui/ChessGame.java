package io.adrisdn.chessnsix.gui;

import java.sql.SQLException;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.sql.SQLiteGdxException;

import io.adrisdn.chessnsix.chess.database.ConnectionDatabase;
import io.adrisdn.chessnsix.chess.engine.FEN.FenFisherRandom;
import io.adrisdn.chessnsix.gui.managers.LanguageManager;
import io.adrisdn.chessnsix.gui.screens.About;
import io.adrisdn.chessnsix.gui.screens.Credits;
import io.adrisdn.chessnsix.gui.screens.GameScreen;
import io.adrisdn.chessnsix.gui.screens.LoadingScreen;
import io.adrisdn.chessnsix.gui.screens.RecordsScreen;
import io.adrisdn.chessnsix.gui.screens.SetupGame;
import io.adrisdn.chessnsix.gui.screens.WelcomeScreen;

public final class ChessGame extends Game {

    private GameScreen gameScreen;
    private WelcomeScreen welcomeScreen;
    private About aboutScreen;
	private SetupGame setupGameScreen;
	private RecordsScreen recordsScreen;
	private LoadingScreen loadingScreen;
	private Credits creditsScreen;

	private ConnectionDatabase connectionDatabase;

	@Override
    public void create() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		FenFisherRandom.InitFisherRandomList();
		LanguageManager.loadLanguage();
		this.connectionDatabase = new ConnectionDatabase();
        this.gameScreen = new GameScreen(this);
        this.aboutScreen = new About(this);
		this.setupGameScreen = new SetupGame(this);
		this.loadingScreen = new LoadingScreen(this);
		this.recordsScreen = new RecordsScreen(this, null);
		this.creditsScreen = new Credits(this);
        this.welcomeScreen = new WelcomeScreen(this);
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


	public LoadingScreen getLoadingScreen() {
		return loadingScreen;
	}

	public Credits getCreditsScreen() {
		return creditsScreen;
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
