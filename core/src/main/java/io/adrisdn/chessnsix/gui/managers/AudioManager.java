package io.adrisdn.chessnsix.gui.managers;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class AudioManager {

	private static final Preferences PREFERENCES = Gdx.app.getPreferences("chess960prefs");

	private static AssetManager manager = null;
	private static HashMap<String, String> res = null;

	private static float musicVolume = 1;

	public static float getMusicVolume() {
		return musicVolume;
	}

	public static void setMusicVolume(float musicVolume) {
		if (musicVolume < 0 || musicVolume > 1) {
			throw new IllegalArgumentException("Volume must be be between 0 and 1");
		}
		PREFERENCES.putFloat("music_volume", musicVolume);
		PREFERENCES.flush();
		AudioManager.musicVolume = musicVolume;
	}

	public static void load() {
		if (manager != null && res != null) {
			throw new IllegalStateException("Assets already loaded");
		}
		manager = new AssetManager();
		res = new HashMap<>();
		res.put("game_music", "audio/music/game_music.wav");
		res.put("menu_music", "audio/music/menu_music.wav");

		musicVolume = PREFERENCES.getFloat("music_volume", 1.0f);

		manager.load(res.get("game_music"), Music.class);
		manager.load(res.get("menu_music"), Music.class);

		manager.finishLoading();
	}

	public static Sound getSound(String key) {
		if (manager == null || res == null) {
			throw new IllegalStateException("Assets not loaded, call load()");
		}
		if (manager.isLoaded(res.get(key))) {
			return manager.get(res.get(key), Sound.class);
		}
		throw new IllegalStateException("Asset not found");
	}

	public static Music getMusic(String key) {
		if (manager == null || res == null) {
			throw new IllegalStateException("Assets not loaded, call load()");
		}
		if (manager.isLoaded(res.get(key))) {
			return manager.get(res.get(key), Music.class);
		}
		throw new IllegalStateException("Asset not found");
	}

	public static void dispose() {
		manager.dispose();
		res = null;
		manager = null;
	}


}
