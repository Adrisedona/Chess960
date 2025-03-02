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

// import io.adrisdn.chessnsix.chess.engine.board.BoardUtils;
import io.adrisdn.chessnsix.gui.ChessGame;
import io.adrisdn.chessnsix.gui.managers.GuiUtils;
import io.adrisdn.chessnsix.gui.managers.LanguageManager;

public final class WelcomeScreen extends AbstractScreen {

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

	public Stage getStage() {
		return this.stage;
	}

	private TextButton startGameButton(final ChessGame chessGame) {
		final TextButton textButton = new TextButton(LanguageManager.get("start_game"), GuiUtils.UI_SKIN);
		textButton.addListener(new ClickListener() {
			@Override
			public void clicked(final InputEvent event, final float x, final float y) {
				// chessGame.gotoGameScreen(GameScreen.BOARD_STATE.NEW_GAME,
				// 		GameScreen.BOARD_STATE.NEW_GAME.getBoard(chessGame.getGameScreen(),
				// 				BoardUtils.DEFAULT_TIMER_MINUTE, BoardUtils.DEFAULT_TIMER_SECOND,
				// 				BoardUtils.DEFAULT_TIMER_MILLISECOND));
				chessGame.setScreen(chessGame.getSetupGameScreen());
			}
		});
		return textButton;
	}

	private TextButton exitGameButton() {
		final TextButton textButton = new TextButton(LanguageManager.get("exit_game"), GuiUtils.UI_SKIN);
		textButton.addListener(new ClickListener() {
			@Override
			public void clicked(final InputEvent event, final float x, final float y) {
				Gdx.app.exit();
				// System.exit(0);
			}
		});
		return textButton;
	}

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

	// private TextButton loadGameButton(final ChessGame chessGame) {
	// 	final TextButton textButton = new TextButton("Load Game", GuiUtils.UI_SKIN);
	// 	textButton.addListener(new ClickListener() {
	// 		@Override
	// 		public void clicked(final InputEvent event, final float x, final float y) {
	// 			try {
	// 				chessGame.gotoGameScreen(GameScreen.BOARD_STATE.LOAD_GAME,
	// 						GameScreen.BOARD_STATE.LOAD_GAME.getBoard(chessGame.getGameScreen(),
	// 								BoardUtils.DEFAULT_TIMER_MINUTE, BoardUtils.DEFAULT_TIMER_SECOND,
	// 								BoardUtils.DEFAULT_TIMER_MILLISECOND));
	// 			} catch (final RuntimeException e) {
	// 				final Label label = new Label("No game to load", GuiUtils.UI_SKIN);
	// 				label.setColor(Color.BLACK);
	// 				new Dialog("Load Game", GuiUtils.UI_SKIN).text(label).button("Ok").show(stage);
	// 			}
	// 		}
	// 	});
	// 	return textButton;
	// }

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

	@Override
	public void dispose() {
		this.stage.dispose();
		this.stage.getBatch().dispose();
	}

}
