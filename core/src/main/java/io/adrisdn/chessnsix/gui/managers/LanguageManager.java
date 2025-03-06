package io.adrisdn.chessnsix.gui.managers;

import java.util.Locale;
import java.util.MissingResourceException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.I18NBundle;

/**
 * Handles all the localizable strings
 */
public class LanguageManager {

	/**
	 * Preferences file to save all the settings.
	 */
	private static final Preferences PREFERENCES = Gdx.app.getPreferences("chess960prefs");

	private static I18NBundle bundle;

	private static String currentLang = "es";

	/**
	 * Obtains the code of the current language.
	 * @return the code of the current language.
	 */
	public static String getCurrentLang() {
		return currentLang;
	}


	private LanguageManager() {
		throw new IllegalStateException("Cannot instantiate LanguageManager");
	}


	/**
	 * Loads the selected language
	 */
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

	/**
	 * Obtains a specific string by its key
	 * @param key key of the string to obtain
	 * @return the corresponding string
	 */
	public static String get(String key) {
		if (bundle == null) {
			throw new IllegalStateException("Language not loaded");
		}
		return bundle.get(key).replace("\\s", " ");

	}

	/**
	 * Sets the current language.
	 * @param langCode language to set.
	 */
	public static void setLanguage(String langCode) {
		if (!currentLang.equals(langCode)) {
			PREFERENCES.putString("lang", langCode);
			PREFERENCES.flush();
			loadLanguage();
		}
	}

}
