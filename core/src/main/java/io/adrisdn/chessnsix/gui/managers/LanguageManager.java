package io.adrisdn.chessnsix.gui.managers;

import java.util.Locale;
import java.util.MissingResourceException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.I18NBundle;

public class LanguageManager {

	private static final Preferences PREFERENCES = Gdx.app.getPreferences("chess960prefs");

	private static I18NBundle bundle;

	private static String currentLang = "es";

	public static String getCurrentLang() {
		return currentLang;
	}


	private LanguageManager() {
		throw new IllegalStateException("Cannot instantiate LanguageManager");
	}


	public static void loadLanguage() {
		String langCode = PREFERENCES.getString("lang", "es");
		Locale locale = Locale.forLanguageTag(langCode);

		try {
			bundle = I18NBundle.createBundle(Gdx.files.internal(String.format("lang/%s" , langCode)), locale);
			currentLang = langCode;
		} catch (MissingResourceException e) {
			e.printStackTrace();
		}
	}

	public static String get(String key) {
		if (bundle == null) {
			throw new IllegalStateException("Language not loaded");
		}
		return bundle.get(key).replace("\\s", " ");

	}

	public static void setLanguage(String langCode) {
		if (!currentLang.equals(langCode)) {
			PREFERENCES.putString("lang", langCode);
			PREFERENCES.flush();
			loadLanguage();
		}
	}

}
