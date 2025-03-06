package io.adrisdn.chessnsix.gui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

import io.adrisdn.chessnsix.gui.ChessGame;
import io.adrisdn.chessnsix.gui.managers.GuiUtils;
import io.adrisdn.chessnsix.gui.managers.LanguageManager;

/**
 * Main screen of the game
 */
public final class WelcomeScreen extends AbstractScreen {

	/**
	 * Initializes the screen, with all the button to go to different screens.
	 * @param chessGame game this screen belongs to.
	 */
	public WelcomeScreen(final ChessGame chessGame) {
		this.stage = new Stage(new FitViewport(GuiUtils.WORLD_WIDTH, GuiUtils.WORLD_HEIGHT), new SpriteBatch());

		Gdx.graphics.setTitle(LanguageManager.get("app_name"));

		final Table table = new Table(GuiUtils.UI_SKIN);

		final int WIDTH = 300;

		table.add(LanguageManager.get("app_name")).padBottom(GuiUtils.PAD).row();
		table.add(this.startGameButton(chessGame)).width(WIDTH).padBottom(GuiUtils.PAD).row();
		table.add(this.recordsButton(chessGame)).width(WIDTH).padBottom(GuiUtils.PAD).row();
		table.add(this.settingsButton(chessGame)).width(WIDTH).padBottom(GuiUtils.PAD).row();
		table.add(this.aboutButton(chessGame)).width(WIDTH).padBottom(GuiUtils.PAD).row();
		table.add(this.creditsButton(chessGame)).width(WIDTH).padBottom(GuiUtils.PAD).row();
		table.add(this.exitGameButton()).width(WIDTH).padBottom(GuiUtils.PAD);

		table.setFillParent(true);

		this.stage.addActor(table);
	}

	/**
	 * Creates the button to go to the {@link SetupGame} screen.
	 * @param chessGame game that handles the change of screen
	 * @return the created button
	 */
	private TextButton startGameButton(final ChessGame chessGame) {
		final TextButton textButton = new TextButton(LanguageManager.get("start_game"), GuiUtils.UI_SKIN);
		textButton.addListener(new ClickListener() {
			@Override
			public void clicked(final InputEvent event, final float x, final float y) {
				chessGame.setScreen(chessGame.getSetupGameScreen());
			}
		});
		return textButton;
	}

	/**
	 * Creates the button to exit the application
	 * @return the created button
	 */
	private TextButton exitGameButton() {
		final TextButton textButton = new TextButton(LanguageManager.get("exit_game"), GuiUtils.UI_SKIN);
		textButton.addListener(new ClickListener() {
			@Override
			public void clicked(final InputEvent event, final float x, final float y) {
				Gdx.app.exit();
			}
		});
		return textButton;
	}

	/**
	 * Creates the button to go to the {@link About} screen.
	 * @param chessGame game that handles the change of screen
	 * @return the created button
	 */
	private TextButton aboutButton(final ChessGame chessGame) {
		final TextButton textButton = new TextButton(LanguageManager.get("tutorial_title"), GuiUtils.UI_SKIN);
		textButton.addListener(new ClickListener() {
			@Override
			public void clicked(final InputEvent event, final float x, final float y) {
				chessGame.setScreen(chessGame.getAboutScreen());
			}
		});
		return textButton;
	}

	/**
	 * Creates the button to go to the {@link Credits} screen.
	 * @param chessGame game that handles the change of screen
	 * @return the created button
	 */
	private TextButton creditsButton(final ChessGame chessGame) {
		final TextButton textButton = new TextButton(LanguageManager.get("credits_title"), GuiUtils.UI_SKIN);
		textButton.addListener(new ClickListener() {
			@Override
			public void clicked(final InputEvent event, final float x, final float y) {
				chessGame.setScreen(chessGame.getCreditsScreen());
			}
		});
		return textButton;
	}

	/**
	 * Creates the button to go to the {@link RecordsScreen}.
	 * @param chessGame game that handles the change of screen
	 * @return the created button
	 */
	private TextButton recordsButton(final ChessGame chessGame) {
		final TextButton textButton = new TextButton(LanguageManager.get("records"), GuiUtils.UI_SKIN);
		textButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				chessGame.getLoadingScreen().setGames();
				chessGame.setScreen(chessGame.getLoadingScreen());
			}
		});
		return textButton;
	}

	/**
	 * Creates the button to go to the {@link Settings} screen.
	 * @param chessGame game that handles the change of screen
	 * @return the created button
	 */
	private TextButton settingsButton(final ChessGame chessGame) {
		final TextButton textButton = new TextButton(LanguageManager.get("settings"), GuiUtils.UI_SKIN);
		textButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				chessGame.setScreen(chessGame.getSettingsScreen());
			}
		});
		return textButton;
	}

	@Override
	public void render(final float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		this.stage.act(delta);
		this.stage.getBatch().begin();
		this.stage.getBatch().draw(GuiUtils.BACKGROUND, 0, 0);

		this.stage.getBatch().end();
		this.stage.draw();
	}
}
