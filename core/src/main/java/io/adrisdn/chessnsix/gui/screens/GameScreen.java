package io.adrisdn.chessnsix.gui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

import io.adrisdn.chessnsix.chess.engine.FEN.FenFisherRandom;
import io.adrisdn.chessnsix.chess.engine.FEN.FenUtilities;
import io.adrisdn.chessnsix.chess.engine.board.Board;
import io.adrisdn.chessnsix.chess.engine.board.BoardUtils;
import io.adrisdn.chessnsix.gui.ChessGame;
import io.adrisdn.chessnsix.gui.board.GameBoard;
// import io.adrisdn.chessnsix.gui.gameMenu.AIButton;
import io.adrisdn.chessnsix.gui.gameMenu.GameOption;
import io.adrisdn.chessnsix.gui.managers.GuiUtils;
import io.adrisdn.chessnsix.gui.managers.LanguageManager;
import io.adrisdn.chessnsix.gui.moveHistory.MoveHistory;
import io.adrisdn.chessnsix.gui.timer.TimerPanel;

public final class GameScreen implements Screen {

	private final Stage stage;
	private Board chessBoard;

	private final io.adrisdn.chessnsix.gui.board.GameBoard gameBoard;
	private final io.adrisdn.chessnsix.gui.board.GameBoard.DisplayOnlyBoard displayOnlyBoard;
	private final io.adrisdn.chessnsix.gui.moveHistory.MoveHistory moveHistory;
	private final io.adrisdn.chessnsix.gui.timer.TimerPanel gameTimerPanel;

	private final ChessGame chessGame;


	public enum BOARD_STATE {
		NEW_GAME {
			@Override
			public Board getBoard(final GameScreen gameScreen, int minutes, int seconds, int milliseconds) {
				return Board.createStandardBoard(minutes, seconds, milliseconds);
			}
		},
		LOAD_GAME {
			@Override
			public Board getBoard(final GameScreen gameScreen, int minutes, int seconds, int milliseconds) {
				return FenUtilities.createGameFromSavedData(GuiUtils.MOVE_LOG_PREF.getString(GuiUtils.MOVE_LOG_STATE),
						gameScreen.getMoveHistory().getMoveLog());
			}
		},
		NEW_CHESS960_GAME {
			@Override
			public Board getBoard(final GameScreen gameScreen, int minutes, int seconds, int milliseconds) {
				return FenUtilities.createGameFromFEN(FenFisherRandom.getRandomFen(), minutes, seconds, milliseconds);
			}
		};

		public abstract Board getBoard(final GameScreen gameScreen, int minutes, int seconds, int milliseconds);
	}

	// setter
	public void updateChessBoard(final Board board) {
		this.chessBoard = board;
	}

	// getter
	public Board getChessBoard() {
		return this.chessBoard;
	}

	public ChessGame getChessGame() {
		return chessGame;
	}

	public io.adrisdn.chessnsix.gui.board.GameBoard getGameBoard() {
		return this.gameBoard;
	}

	public io.adrisdn.chessnsix.gui.board.GameBoard.DisplayOnlyBoard getDisplayOnlyBoard() {
		return this.displayOnlyBoard;
	}

	public io.adrisdn.chessnsix.gui.moveHistory.MoveHistory getMoveHistory() {
		return this.moveHistory;
	}

	public io.adrisdn.chessnsix.gui.timer.TimerPanel getGameTimerPanel() {
		return this.gameTimerPanel;
	}

	public Stage getStage() {
		return this.stage;
	}


	public GameScreen(final ChessGame chessGame) {
		// init
		this.chessGame = chessGame;
		this.stage = new Stage(new FitViewport(GuiUtils.WORLD_WIDTH, GuiUtils.WORLD_HEIGHT), new SpriteBatch());
		this.chessBoard = Board.createStandardBoard(BoardUtils.DEFAULT_TIMER_MINUTE, BoardUtils.DEFAULT_TIMER_SECOND,
				BoardUtils.DEFAULT_TIMER_MILLISECOND);
		this.moveHistory = new MoveHistory();
		this.gameBoard = new io.adrisdn.chessnsix.gui.board.GameBoard(this);
		this.displayOnlyBoard = new GameBoard.DisplayOnlyBoard();
		this.gameTimerPanel = new TimerPanel();

		Gdx.graphics.setTitle(LanguageManager.get("app_name"));

		final VerticalGroup verticalGroup = new VerticalGroup();

		final HorizontalGroup horizontalGroup = new HorizontalGroup();

		horizontalGroup.addActor(this.moveHistory);
		horizontalGroup.addActor(this.initGameBoard());
		horizontalGroup.addActor(this.gameTimerPanel);

		verticalGroup.setFillParent(true);
		verticalGroup.addActor(this.initGameMenu());
		verticalGroup.addActor(horizontalGroup);

		this.stage.addActor(verticalGroup);
	}

	private Stack initGameBoard() {
		final Stack stack = new Stack();
		stack.add(this.displayOnlyBoard);
		stack.add(this.gameBoard);
		return stack;
	}

	private Table initGameMenu() {
		final Table table = new Table();
		final int BUTTON_WIDTH = 250;
		table.add(this.newGameButton()).width(BUTTON_WIDTH);
		table.add(new FlipBoardButton(this)).width(BUTTON_WIDTH);
		table.add(new GameOption(this)).width(BUTTON_WIDTH);
		table.add(this.exitGameButton()).width(BUTTON_WIDTH);
		return table;
	}

	private TextButton newGameButton() {
		TextButton button = new TextButton(LanguageManager.get("new_game_title"), GuiUtils.UI_SKIN);
		button.addListener(new ClickListener() {
			Dialog dialog = newGameDialog();
			@Override
			public void clicked(InputEvent event, float x, float y) {
				GameScreen.this.getGameTimerPanel().continueTimer(false);
				dialog.show(GameScreen.this.stage);
			}
		});
		return button;
	}

	private Dialog newGameDialog() {
		Label label = new Label(LanguageManager.get("new_game_text"), GuiUtils.UI_SKIN);
		label.setColor(Color.BLACK);
		Dialog dialog = new Dialog(LanguageManager.get("new_game_title"), GuiUtils.UI_SKIN) {
			@Override
			protected void result(Object object) {
				this.remove();
				if ((boolean)object) {
					chessGame.getGameMusic().stop();
					chessGame.getMenuMusic().play();
					Gdx.input.setInputProcessor(chessGame.getSetupGameScreen().getStage());
					chessGame.setScreen(chessGame.getSetupGameScreen());
				} else {
					GameScreen.this.getGameTimerPanel().continueTimer(true);
				}
			}
		}.text(label)
			.button(LanguageManager.get("ok"), true)
			.button(LanguageManager.get("cancel"), false);
		return dialog;
	}

	private TextButton exitGameButton() {
		TextButton button = new TextButton(LanguageManager.get("exit_game_title"), GuiUtils.UI_SKIN);
		button.addListener(new ClickListener() {
			Dialog dialog = exitGameDialog();
			@Override
			public void clicked(InputEvent event, float x, float y) {
				GameScreen.this.getGameTimerPanel().continueTimer(false);
				dialog.show(GameScreen.this.stage);
			}
		});
		return button;
	}

	private Dialog exitGameDialog() {
		Label label = new Label(LanguageManager.get("exit_game_text"), GuiUtils.UI_SKIN);
		label.setColor(Color.BLACK);
		Dialog dialog = new Dialog(LanguageManager.get("exit_game_title"), GuiUtils.UI_SKIN) {
			@Override
			protected void result(Object object) {
				this.remove();
				if ((boolean)object) {
					chessGame.getGameMusic().stop();
					chessGame.getMenuMusic().play();
					Gdx.input.setInputProcessor(chessGame.getWelcomeScreen().getStage());
					chessGame.setScreen(chessGame.getWelcomeScreen());
				} else {
					GameScreen.this.getGameTimerPanel().continueTimer(true);
				}
			}
		}.text(label)
			.button(LanguageManager.get("ok"), true)
			.button(LanguageManager.get("cancel"), false);
		return dialog;
	}

	private static final class FlipBoardButton extends TextButton {
        private FlipBoardButton(final GameScreen gameScreen) {
            super(LanguageManager.get("flip_board"), GuiUtils.UI_SKIN);
            this.addListener(new ClickListener() {
                @Override
                public void clicked(final InputEvent event, final float x, final float y) {
                    gameScreen.getGameTimerPanel().continueTimer(false);

                    gameScreen.getGameBoard().updateBoardDirection();
                    gameScreen.getGameBoard().drawBoard(gameScreen, gameScreen.getChessBoard(), gameScreen.getDisplayOnlyBoard());

                    gameScreen.getGameTimerPanel().changeTimerPanelDirection();
                    gameScreen.getGameTimerPanel().update(gameScreen);

                    gameScreen.getMoveHistory().changeMoveHistoryDirection();
                    gameScreen.getMoveHistory().updateMoveHistory();

                    gameScreen.getGameTimerPanel().continueTimer(true);
                }
            });
        }
    }

	@Override
	public void resize(final int width, final int height) {
		this.stage.getViewport().update(width, height, true);
	}

	@Override
	public void render(final float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		this.stage.act(delta);
		if (!this.gameTimerPanel.isNoTimer() && this.gameTimerPanel.isTimerContinue()
				&& !this.gameTimerPanel.isPauseTimerOption()) {
			this.gameTimerPanel.update(this);
			if (this.gameBoard.isAIPlayer(this.chessBoard.currentPlayer())
					&& this.chessBoard.currentPlayer().isTimeOut()) {
				this.gameBoard.getArtificialIntelligence().setStopAI(true);
			}
		}
		this.stage.getBatch().begin();
		this.stage.getBatch().draw(GuiUtils.BACKGROUND, 0, 0);
		this.stage.getBatch().end();
		this.stage.draw();
	}

	@Override
	public void dispose() {
		this.stage.dispose();
		this.stage.getBatch().dispose();
		GuiUtils.dispose();
	}

	@Deprecated
	public void show() {
	}

	@Deprecated
	public void pause() {
	}

	@Deprecated
	public void resume() {
	}

	@Deprecated
	public void hide() {
	}
}
