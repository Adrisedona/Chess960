package io.adrisdn.chessnsix.gui.screens;

import com.badlogic.gdx.Gdx;
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
import io.adrisdn.chessnsix.gui.managers.LanguageManager;

/**
 * Screen that shows all the games played.
 */
public class RecordsScreen extends AbstractScreen {

	private final ChessGame chessGame;
	private ImmutableList<Game> games;

	private Table table;
	private Table tableGames;

	/**
	 * Initializes the screen without games
	 *
	 * @param chessGame the game this screen belongs to.
	 */
	public RecordsScreen(final ChessGame chessGame) {
		this.chessGame = chessGame;
		this.games = null;
		this.stage = new Stage(new FitViewport(GuiUtils.WORLD_WIDTH, GuiUtils.WORLD_HEIGHT), new SpriteBatch());
	}

	/**
	 * Initializes the screens with all the games it has
	 */
	public void initScreen() {
		stage.clear();
		table = new Table(GuiUtils.UI_SKIN);
		table.top().left();
		table.setFillParent(true);
		tableGames = new Table(GuiUtils.UI_SKIN);
		tableGames.setFillParent(true);
		tableGames.top().left();
		tableGames.add(LanguageManager.get("date")).pad(GuiUtils.PAD);
		tableGames.add(LanguageManager.get("n_moves")).pad(GuiUtils.PAD);
		tableGames.add(LanguageManager.get("result")).pad(GuiUtils.PAD);
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
		table.add(scrollPane).pad(GuiUtils.PAD).width(GuiUtils.WORLD_WIDTH - 200).height(GuiUtils.WORLD_HEIGHT)
				.center();
		table.add(backButton()).width(300).padLeft(-250).padTop(GuiUtils.PAD).top().row();
		this.stage.addActor(table);
	}

	/**
	 * Button to go back to the {@link WelcomeScreen}
	 *
	 * @param chessGame game that handles the screen change
	 * @return
	 */
	private TextButton backButton() {
		TextButton button = new TextButton(LanguageManager.get("back_menu"), GuiUtils.UI_SKIN);
		button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				chessGame.setScreen(chessGame.getWelcomeScreen());
			}
		});
		return button;
	}

	/**
	 * Sets the list of games.
	 *
	 * @param games the list of games to set.
	 */
	public void setGames(ImmutableList<Game> games) {
		this.games = games;
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

}
