package io.adrisdn.chessnsix.gui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.async.AsyncResult;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.google.common.collect.ImmutableList;

import io.adrisdn.chessnsix.chess.database.Game;
import io.adrisdn.chessnsix.gui.ChessGame;
import io.adrisdn.chessnsix.gui.managers.GuiUtils;
import io.adrisdn.chessnsix.gui.managers.LanguageManager;;

/**
 * Intermediate screen between {@link WelcomeScreen} and {@link RecordsScreen},
 * that gets all the games played from the database before going to this last
 * screen.
 */
public class LoadingScreen extends AbstractScreen {

	private final ChessGame chessGame;
	private AsyncResult<ImmutableList<Game>> games;

	/**
	 * Initializes the LoadingScreen
	 * @param chessGame the game this screen belongs to.
	 */
	public LoadingScreen(final ChessGame chessGame) {
		this.chessGame = chessGame;
		this.stage = new Stage(new FitViewport(GuiUtils.WORLD_WIDTH, GuiUtils.WORLD_HEIGHT), new SpriteBatch());

		final Table table = new Table(GuiUtils.UI_SKIN);

		table.add(LanguageManager.get("loading")).row();

		table.setFillParent(true);

		this.stage.addActor(table);
	}

	/**
	 * Starts the process of getting the games from the database-
	 */
	public void setGames() {
		this.games = this.chessGame.getConnectionDatabase().getGamesAsync();
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		this.stage.act(delta);
		this.stage.getBatch().begin();
		this.stage.getBatch().draw(GuiUtils.BACKGROUND, 0, 0);

		//Every frame check if it has to go to the next screen
		if (games.isDone()) {
			chessGame.getRecordsScreen().setGames(games.get());
			chessGame.getRecordsScreen().initScreen();
			chessGame.setScreen(chessGame.getRecordsScreen());
		}

		this.stage.getBatch().end();
		this.stage.draw();
	}

}
