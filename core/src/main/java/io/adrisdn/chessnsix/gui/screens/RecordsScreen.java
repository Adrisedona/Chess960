package io.adrisdn.chessnsix.gui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.google.common.collect.ImmutableList;

import io.adrisdn.chessnsix.chess.database.Game;
import io.adrisdn.chessnsix.gui.ChessGame;
import io.adrisdn.chessnsix.gui.managers.GuiUtils;

public class RecordsScreen implements Screen {

	private final ChessGame chessGame;
	private ImmutableList<Game> games;

	private final Stage stage;
	private Table table;
	private Table tableGames;

	public RecordsScreen(final ChessGame chessGame, final ImmutableList<Game> games) {
		this.chessGame = chessGame;
		this.games = games;

		this.stage = new Stage(new FitViewport(GuiUtils.WORLD_WIDTH, GuiUtils.WORLD_HEIGHT), new SpriteBatch());
	}



	public void initScreen() {//TODO: centrar la tabla
		stage.clear();
		table = new Table(GuiUtils.UI_SKIN);
		table.top().left();
		table.setFillParent(true);
		tableGames = new Table(GuiUtils.UI_SKIN);
		tableGames.setFillParent(true);
		tableGames.top().left();
		tableGames.add("Date").pad(GuiUtils.PAD);//TODO: fix string
		tableGames.add("Number of moves").pad(GuiUtils.PAD);//TODO: fix string
		tableGames.add("Result").pad(GuiUtils.PAD);//TODO: fix string
		tableGames.row();
		for (Game game : games) {
			tableGames.add(game.getDate()).pad(GuiUtils.PAD);
			tableGames.add(game.getNumberMoves() + "").pad(GuiUtils.PAD);
			tableGames.add(game.getResult()).pad(GuiUtils.PAD);
			tableGames.row();
		}
		final ScrollPane scrollPane = new ScrollPane(tableGames);
		scrollPane.setScrollingDisabled(true, false);
		scrollPane.setFillParent(true);
		table.add(scrollPane).pad(GuiUtils.PAD).width(GuiUtils.WORLD_WIDTH - 200).height(GuiUtils.WORLD_HEIGHT).center();
		table.add(backButton()).padLeft(-70).padTop(GuiUtils.PAD).top().row();
		this.stage.addActor(table);
	}

	private TextButton backButton() {
		TextButton button = new TextButton("Back to menu", GuiUtils.UI_SKIN);//TODO: fix string
		button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.input.setInputProcessor(chessGame.getWelcomeScreen().getStage());
				chessGame.setScreen(chessGame.getWelcomeScreen());
			}
		});
		return button;
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

	@Override
	public void show() {

	}



	public ChessGame getChessGame() {
		return chessGame;
	}



	public ImmutableList<Game> getGames() {
		return games;
	}



	public void setGames(ImmutableList<Game> games) {
		this.games = games;
	}


	public Stage getStage() {
		return stage;
	}


}
