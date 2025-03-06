package io.adrisdn.chessnsix.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import io.adrisdn.chessnsix.gui.ChessGame;

/** Launches the Android application. */
public final class AndroidLauncher extends AndroidApplication {

	private final ChessGame chessGame;

	public AndroidLauncher() { this.chessGame = new ChessGame(); }

	@Override
	protected void onCreate (final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useAccelerometer = false;
		config.useCompass = false;
		this.initialize(this.chessGame, config);
	}




}
