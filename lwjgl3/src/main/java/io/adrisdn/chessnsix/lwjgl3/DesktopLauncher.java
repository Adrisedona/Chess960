package io.adrisdn.chessnsix.lwjgl3;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import io.adrisdn.chessnsix.gui.ChessGame;
import io.adrisdn.chessnsix.gui.managers.GuiUtils;
import io.adrisdn.chessnsix.gui.managers.LanguageManager;

/** Launches the desktop (LWJGL3) application. */
public final class DesktopLauncher {

	private final ChessGame chessGame;

	private DesktopLauncher() { this.chessGame = new ChessGame(); }

	public static void main (final String[] arg) { new DesktopLauncher().createGame(); }

	private void createGame() { new Lwjgl3Application(this.chessGame, this.generateConfiguration()); }

	private Lwjgl3ApplicationConfiguration generateConfiguration() {
		final Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
		configuration.setWindowedMode(1200, 640);
		configuration.setWindowIcon(Files.FileType.Internal, "chess_logo.png");
		//avoid overheat issues just in case
		configuration.setIdleFPS(15);
		configuration.setForegroundFPS(60);

		configuration.setWindowListener(new Lwjgl3WindowAdapter() {
			@Override
			public boolean closeRequested() {
				DesktopLauncher.this.showExitDialog();
				return false;
			}
		});

		return configuration;
	}

	private void showExitDialog() {
		final Label label;
		final String dialogTitle = LanguageManager.get("exit_game_confirmation_title");
		if (this.chessGame.getScreen().equals(this.chessGame.getGameScreen())) {
			DesktopLauncher.this.chessGame.getGameScreen().getGameTimerPanel().continueTimer(false);
			label = new Label(LanguageManager.get("exit_game_confirmation_text"), GuiUtils.UI_SKIN);
			label.setColor(Color.BLACK);
			new Dialog(dialogTitle, GuiUtils.UI_SKIN) {
				@Override
				protected void result(final Object object) {
					this.remove();
					if (object == null) {
						DesktopLauncher.this.chessGame.getGameScreen().getGameTimerPanel().continueTimer(true);
						return;
					}
					Gdx.app.exit();
				}
			}.button(LanguageManager.get("ok"), true)
					.button(LanguageManager.get("cancel"))
					.text(label)
					.show(this.chessGame.getGameScreen().getStage());
			return;

		} else if (this.chessGame.getScreen().equals(this.chessGame.getAboutScreen()) || this.chessGame.getScreen().equals(this.chessGame.getWelcomeScreen()) || this.chessGame.getScreen().equals(this.chessGame.getRecordsScreen()) || this.chessGame.getScreen().equals(this.chessGame.getSetupGameScreen()) || this.chessGame.getScreen().equals(chessGame.getCreditsScreen())) {
			label = new Label(LanguageManager.get("exit_game_confirmation_text"), GuiUtils.UI_SKIN);
			label.setColor(Color.BLACK);
			new Dialog(dialogTitle, GuiUtils.UI_SKIN) {
				@Override
				protected void result(final Object object) {
					this.remove();
					if ((Boolean) object) { Gdx.app.exit(); }
				}
			}.button(LanguageManager.get("ok"), true)
					.button(LanguageManager.get("cancel"), false)
					.text(label)
					.show((this.chessGame.getScreen().equals(this.chessGame.getAboutScreen()) ? this.chessGame.getAboutScreen().getStage() : this.chessGame.getScreen().equals(this.chessGame.getRecordsScreen()) ? this.chessGame.getRecordsScreen().getStage() : this.chessGame.getScreen().equals(this.chessGame.getSetupGameScreen()) ? this.chessGame.getSetupGameScreen().getStage() : this.chessGame.getScreen().equals(chessGame.getCreditsScreen()) ? chessGame.getCreditsScreen().getStage() : this.chessGame.getWelcomeScreen().getStage()));
			return;
		}
		throw new IllegalStateException("Should not reach here, the new screen needs to be implemented here");
	}
}
