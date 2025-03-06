package io.adrisdn.chessnsix.gui;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.sql.SQLiteGdxException;

import io.adrisdn.chessnsix.chess.database.ConnectionDatabase;
import io.adrisdn.chessnsix.chess.engine.FEN.FenFischerRandom;
import io.adrisdn.chessnsix.gui.managers.AudioManager;
import io.adrisdn.chessnsix.gui.managers.GuiUtils;
import io.adrisdn.chessnsix.gui.managers.LanguageManager;
import io.adrisdn.chessnsix.gui.screens.About;
import io.adrisdn.chessnsix.gui.screens.AbstractScreen;
import io.adrisdn.chessnsix.gui.screens.Credits;
import io.adrisdn.chessnsix.gui.screens.GameScreen;
import io.adrisdn.chessnsix.gui.screens.LoadingScreen;
import io.adrisdn.chessnsix.gui.screens.RecordsScreen;
import io.adrisdn.chessnsix.gui.screens.Settings;
import io.adrisdn.chessnsix.gui.screens.SetupGame;
import io.adrisdn.chessnsix.gui.screens.WelcomeScreen;

/**
 * Main class of the game, controls all the screens and audio
 */
public final class ChessGame extends Game {

	private GameScreen gameScreen;
	private WelcomeScreen welcomeScreen;
	private About aboutScreen;
	private SetupGame setupGameScreen;
	private RecordsScreen recordsScreen;
	private LoadingScreen loadingScreen;
	private Credits creditsScreen;
	private Settings settingsScreen;

	private ConnectionDatabase connectionDatabase;

	private Music menuMusic;
	private Music gameMusic;

	private Sound moveSound;
	private Sound captureSound;
	private Sound castleSound;
	private Sound promotionSound;
	private Sound checkSound;
	private Sound checkMateSound;

	/**
	 * Creates the game, initializing all the screens, the database, the preferred languaje and all the audio assets
	 */
	@Override
	public void create() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		FenFischerRandom.InitFischerRandomList();
		LanguageManager.loadLanguage();
		AudioManager.load();
		this.connectionDatabase = new ConnectionDatabase();
		this.gameScreen = new GameScreen(this);
		this.aboutScreen = new About(this);
		this.setupGameScreen = new SetupGame(this);
		this.loadingScreen = new LoadingScreen(this);
		this.recordsScreen = new RecordsScreen(this, null);
		this.creditsScreen = new Credits(this);
		this.welcomeScreen = new WelcomeScreen(this);
		this.settingsScreen = new Settings(this);
		this.captureSound = AudioManager.getSound(AudioManager.CAPTURE_SOUND);
		this.castleSound = AudioManager.getSound(AudioManager.CASTLE_SOUND);
		this.moveSound = AudioManager.getSound(AudioManager.MOVE_SOUND);
		this.promotionSound = AudioManager.getSound(AudioManager.PROMOTION_SOUND);
		this.checkSound = AudioManager.getSound(AudioManager.CHECK_SOUND);
		this.checkMateSound = AudioManager.getSound(AudioManager.CHECKMATE_SOUND);
		this.gameMusic = AudioManager.getMusic(AudioManager.GAME_MUSIC_KEY);
		this.gameMusic.setLooping(true);
		this.gameMusic.setVolume(AudioManager.getMusicVolume());
		this.menuMusic = AudioManager.getMusic(AudioManager.MENU_MUSIC_KEY);
		this.menuMusic.setLooping(true);
		this.menuMusic.setVolume(AudioManager.getMusicVolume());
		this.menuMusic.play();
		Gdx.graphics.setTitle(LanguageManager.get("app_name"));
		this.setScreen(this.welcomeScreen);
	}

	/**
	 * Obtains the playing screen
	 * @return the playing screen
	 */
	public GameScreen getGameScreen() {
		return this.gameScreen;
	}

	/**
	 * Obtains the main screen
	 * @return the main screen
	 */
	public WelcomeScreen getWelcomeScreen() {
		return this.welcomeScreen;
	}

	/**
	 * Obtains the tutorial screen
	 * @return the tutorial screen
	 */
	public About getAboutScreen() {
		return this.aboutScreen;
	}

	/**
	 * Obtains the game settings screen
	 * @return the game settings screen
	 */
	public SetupGame getSetupGameScreen() {
		return setupGameScreen;
	}

	/**
	 * Obtains the game history screen
	 * @return the game history screen
	 */
	public RecordsScreen getRecordsScreen() {
		return recordsScreen;
	}

	/**
	 * Obtains the loading screen
	 * @return the loading screen
	 */
	public LoadingScreen getLoadingScreen() {
		return loadingScreen;
	}

	/**
	 * Obtains the credits screen
	 * @return the credits screen
	 */
	public Credits getCreditsScreen() {
		return creditsScreen;
	}

	/**
	 * Obtains the global settings screen
	 * @return the global settings screen
	 */
	public Settings getSettingsScreen() {
		return settingsScreen;
	}

	/**
	 * Obtains the connection with the database
	 * @return the connection with the database
	 */
	public ConnectionDatabase getConnectionDatabase() {
		return connectionDatabase;
	}

	/**
	 * Obtains the music for the menu
	 * @return the music for the menu
	 */
	public Music getMenuMusic() {
		return menuMusic;
	}

	/**
	 * Obtains the music for the game
	 * @return the music for the game
	 */
	public Music getGameMusic() {
		return gameMusic;
	}

	/**
	 * Obtains the sound that plays when a move is made
	 * @return the sound that plays when a move is made
	 */
	public Sound getMoveSound() {
		return moveSound;
	}

	/**
	 * Obtains the sound that plays when a piece is captured
	 * @return the sound that plays when a piece is captured
	 */
	public Sound getCaptureSound() {
		return captureSound;
	}

	/**
	 * Obtains the sound that plays when a player castles
	 * @return the sound that plays when a player castles
	 */
	public Sound getCastleSound() {
		return castleSound;
	}

	/**
	 * Obtains the sound that plays when a player promotes a piece
	 * @return the sound that plays when a player promotes a piece
	 */
	public Sound getPromotionSound() {
		return promotionSound;
	}

	/**
	 * Obtains the sound that plays when a player is in check
	 * @return the sound that plays when a player is in check
	 */
	public Sound getCheckSound() {
		return checkSound;
	}

	/**
	 * Obtains the sound that plays when a player is checkmated
	 * @return the sound that plays when a player is checkmated
	 */
	public Sound getCheckMateSound() {
		return checkMateSound;
	}

	/**
	 * Disposes all the screens, the assets and closes the connection with the database
	 */
	@Override
	public void dispose() {
		this.gameScreen.dispose();
		this.welcomeScreen.dispose();
		this.aboutScreen.dispose();
		this.recordsScreen.dispose();
		this.loadingScreen.dispose();
		this.setupGameScreen.dispose();
		this.creditsScreen.dispose();
		try {
			this.connectionDatabase.close();
		} catch (SQLiteGdxException e) {
			e.printStackTrace();
		}
		AudioManager.dispose();
		GuiUtils.dispose();
	}

	/**
	 * Reloads all the screens of the game, preserving the game at its current screen
	 */
	public void reload() {
		final Screen currentScreen = this.getScreen();
		this.gameScreen = new GameScreen(this);
		this.aboutScreen = new About(this);
		this.setupGameScreen = new SetupGame(this);
		this.loadingScreen = new LoadingScreen(this);
		this.recordsScreen = new RecordsScreen(this, null);
		this.creditsScreen = new Credits(this);
		this.settingsScreen = new Settings(this);
		this.welcomeScreen = new WelcomeScreen(this);
		this.setScreen(getReloadedCurrentScreen(currentScreen));
	}

	/**
	 * Establishes the current screen of the game
	 */
	@Override
	public void setScreen(final Screen screen) {
		Gdx.input.setInputProcessor(((AbstractScreen) screen).getStage());
		super.setScreen(screen);
	}

	/**
	 * Determines the current screen after a reload
	 * @param currentScreen the screen that was active before the reload
	 * @return the reloaded screen that corresponds with the active screen
	 */
	private Screen getReloadedCurrentScreen(final Screen currentScreen) {
		if (currentScreen.getClass() == GameScreen.class) {
			return this.gameScreen;
		}
		if (currentScreen.getClass() == Settings.class) {
			return this.settingsScreen;
		}
		if (currentScreen.getClass() == About.class) {
			return this.aboutScreen;
		}
		throw new IllegalArgumentException("Parameter screen not implemented in the method");
	}
}
