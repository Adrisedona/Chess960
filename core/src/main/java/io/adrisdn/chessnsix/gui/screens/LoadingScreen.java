package io.adrisdn.chessnsix.gui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.async.AsyncResult;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.google.common.collect.ImmutableList;

import io.adrisdn.chessnsix.chess.database.Game;
import io.adrisdn.chessnsix.gui.ChessGame;
import io.adrisdn.chessnsix.gui.GuiUtils;

public class LoadingScreen implements Screen {

	private final Stage stage;
	private final ChessGame chessGame;
	private final AsyncResult<ImmutableList<Game>> games;

	public LoadingScreen(final ChessGame chessGame) {
		this.chessGame = chessGame;
		this.stage = new Stage(new FitViewport(GuiUtils.WORLD_WIDTH, GuiUtils.WORLD_HEIGHT), new SpriteBatch());
		this.games = this.chessGame.getConnectionDatabase().getGamesAsync();

		Gdx.input.setInputProcessor(this.stage);

		final Table table = new Table(GuiUtils.UI_SKIN);

		table.add("Loading").row();//TODO: fix string

		table.setFillParent(true);

		this.stage.addActor(table);
		this.stage.addActor(new Actor() {
			@Override
			public void act(float delta) {
				if (games.isDone()) {
					chessGame.setScreen(chessGame.getRecordsScreen());
				}
			}
		});
	}

	@Override
	public void show() {

	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		this.stage.act(delta);
		this.stage.getBatch().begin();
		this.stage.getBatch().draw(GuiUtils.BACKGROUND, 0, 0);

		this.stage.getBatch().end();
		this.stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		this.stage.getViewport().update(width, height, true);
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {
		this.stage.dispose();
		this.stage.getBatch().dispose();
	}

}
