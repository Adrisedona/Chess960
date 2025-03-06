package io.adrisdn.chessnsix.gui.managers;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import io.adrisdn.chessnsix.gui.ChessGame;

/**
 * Handles the audio files in the game, and if vibration is enabled or not.
 */
public class AudioManager {

	/**
	 * Preferences file to save all the settings.
	 */
	private static final Preferences PREFERENCES = Gdx.app.getPreferences("chess960prefs");

	private static final String MUSIC_KEY = "music_volume";
	private static final String SOUND_KEY = "sound_volume";
	private static final String VIBRATION_KEY = "vibration";

	public static final String MOVE_SOUND = "move";
	public static final String PROMOTION_SOUND = "promotion";
	public static final String CAPTURE_SOUND = "capture";
	public static final String CASTLE_SOUND = "castle";
	public static final String CHECK_SOUND = "check";
	public static final String CHECKMATE_SOUND = "checkmate";

	public static final String GAME_MUSIC_KEY = "game_music";
	public static final String MENU_MUSIC_KEY = "menu_music";

	private static AssetManager manager = null;
	private static HashMap<String, String> res = null;

	private static float musicVolume = PREFERENCES.getFloat(MUSIC_KEY, 1);
	private static float soundVolume = PREFERENCES.getFloat(SOUND_KEY, 1);
	private static boolean vibration = PREFERENCES.getBoolean(VIBRATION_KEY, true);

	/**
	 * Obtains whether vibration is enabled or not.
	 *
	 * @return true if it's enabled, false otherwise.
	 */
	public static boolean isVibration() {
		return vibration;
	}

	/**
	 * Obtains the volume for sounds.
	 *
	 * @return the volume for sounds.
	 */
	public static float getSoundVolume() {
		return soundVolume;
	}

	/**
	 * Obtains the volume for music.
	 *
	 * @return the volume for music.
	 */
	public static float getMusicVolume() {
		return musicVolume;
	}

	/**
	 * Sets the volume for the music
	 *
	 * @param musicVolume volume to set
	 * @param chessGame   game to set the volume for
	 */
	public static void setMusicVolume(final float musicVolume, final ChessGame chessGame) {
		if (musicVolume < 0 || musicVolume > 1) {
			throw new IllegalArgumentException("Volume must be be between 0 and 1");
		}
		PREFERENCES.putFloat(MUSIC_KEY, musicVolume);
		PREFERENCES.flush();
		AudioManager.musicVolume = musicVolume;
		changeVolume(chessGame);
	}

	/**
	 * Changes the volume of the music files.
	 *
	 * @param chessGame game containing the music files.
	 */
	private static void changeVolume(final ChessGame chessGame) {
		chessGame.getMenuMusic().setVolume(musicVolume);
		chessGame.getGameMusic().setVolume(musicVolume);
	}

	/**
	 * Loads all the sounds and music of the game.
	 */
	public static void load() {
		if (manager != null && res != null) {
			throw new IllegalStateException("Assets already loaded");
		}
		manager = new AssetManager();
		res = new HashMap<>();
		res.put(GAME_MUSIC_KEY, "audio/music/game_music.wav");
		res.put(MENU_MUSIC_KEY, "audio/music/menu_music.wav");

		res.put(MOVE_SOUND, "audio/sounds/move.wav");
		res.put(PROMOTION_SOUND, "audio/sounds/promotion.wav");
		res.put(CAPTURE_SOUND, "audio/sounds/capture.wav");
		res.put(CASTLE_SOUND, "audio/sounds/castle.wav");
		res.put(CHECK_SOUND, "audio/sounds/check.wav");
		res.put(CHECKMATE_SOUND, "audio/sounds/checkmate.wav");

		musicVolume = PREFERENCES.getFloat("music_volume", 1.0f);

		manager.load(res.get(GAME_MUSIC_KEY), Music.class);
		manager.load(res.get(MENU_MUSIC_KEY), Music.class);

		manager.load(res.get(MOVE_SOUND), Sound.class);
		manager.load(res.get(PROMOTION_SOUND), Sound.class);
		manager.load(res.get(CAPTURE_SOUND), Sound.class);
		manager.load(res.get(CASTLE_SOUND), Sound.class);
		manager.load(res.get(CHECK_SOUND), Sound.class);
		manager.load(res.get(CHECKMATE_SOUND), Sound.class);

		manager.finishLoading();
	}

	/**
	 * Obtains a specific sound
	 * @param key key of the sound to get
	 * @return the correspondig sound.
	 */
	public static Sound getSound(final String key) {
		if (manager == null || res == null) {
			throw new IllegalStateException("Assets not loaded, call load()");
		}
		if (manager.isLoaded(res.get(key))) {
			return manager.get(res.get(key), Sound.class);
		}
		throw new IllegalStateException("Asset not found");
	}

	/**
	 * Obtains a specific music
	 * @param key key of the music to get
	 * @return the correspondig music.
	 */
	public static Music getMusic(final String key) {
		if (manager == null || res == null) {
			throw new IllegalStateException("Assets not loaded, call load()");
		}
		if (manager.isLoaded(res.get(key))) {
			return manager.get(res.get(key), Music.class);
		}
		throw new IllegalStateException("Asset not found");
	}

	/**
	 * Frees all the auido files form memory
	 */
	public static void dispose() {
		manager.dispose();
		res = null;
		manager = null;
	}

	/**
	 * Sets the volume for the ssounds
	 *
	 * @param soundcVolume volume to set
	 */
	public static void setSoundVolume(final float soundVolume) {
		if (soundVolume < 0 || soundVolume > 1) {
			throw new IllegalArgumentException("Volume must be be between 0 and 1");
		}
		PREFERENCES.putFloat(SOUND_KEY, soundVolume);
		PREFERENCES.flush();
		AudioManager.soundVolume = soundVolume;
	}

	/**
	 * Enables or disables the vibration
	 * @param vibration true to enable false to disable.
	 */
	public static void setVibration(final boolean vibration) {
		PREFERENCES.putFloat(VIBRATION_KEY, musicVolume);
		PREFERENCES.flush();
		AudioManager.vibration = vibration;
	}

}
