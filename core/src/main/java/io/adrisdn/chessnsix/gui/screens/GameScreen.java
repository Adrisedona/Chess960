package io.adrisdn.chessnsix.gui.screens;

import com.badlogic.gdx.Gdx;
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

import io.adrisdn.chessnsix.chess.engine.FEN.FenFischerRandom;
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

/**
 * GameScreen represents the main screen where the chess game is played.
 * It manages the game board, move history, timers, and user interactions.
 */
public final class GameScreen extends AbstractScreen {

	private Board chessBoard;

	private final io.adrisdn.chessnsix.gui.board.GameBoard gameBoard;
	private final io.adrisdn.chessnsix.gui.board.GameBoard.DisplayOnlyBoard displayOnlyBoard;
	private final io.adrisdn.chessnsix.gui.moveHistory.MoveHistory moveHistory;
	private final io.adrisdn.chessnsix.gui.timer.TimerPanel gameTimerPanel;

	private final ChessGame chessGame;

	/**
	 * Enum representing the possible board states for new games.
	 */
	public enum BOARD_STATE {
		NEW_GAME {
			@Override
			public Board getBoard(final GameScreen gameScreen, int minutes, int seconds, int milliseconds) {
				return Board.createStandardBoard(minutes, seconds, milliseconds);
			}
		},
		NEW_CHESS960_GAME {
			@Override
			public Board getBoard(final GameScreen gameScreen, int minutes, int seconds, int milliseconds) {
				return FenUtilities.createGameFromFEN(FenFischerRandom.getRandomFen(), minutes, seconds, milliseconds);
			}
		};

		/**
		 * Obtains a game board to start a game
		 *
		 * @param gameScreen   screen to start the game in
		 * @param minutes      Minutes for both players
		 * @param seconds      Seconds for both players
		 * @param milliseconds Milliseconds for both players
		 * @return The corresponding starting board
		 */
		public abstract Board getBoard(final GameScreen gameScreen, int minutes, int seconds, int milliseconds);
	}

	/**
	 * Updates the chess board.
	 *
	 * @param board the new board.
	 */
	public void updateChessBoard(final Board board) {
		this.chessBoard = board;
	}

	/**
	 * Obtains the current board.
	 *
	 * @return the current board.
	 */
	public Board getChessBoard() {
		return this.chessBoard;
	}

	/**
	 * Returns the game this screen belongs to.
	 *
	 * @return the game this screen belongs to.
	 */
	public ChessGame getChessGame() {
		return chessGame;
	}

	/**
	 * Obtains the game board.
	 *
	 * @return the game board.
	 */
	public io.adrisdn.chessnsix.gui.board.GameBoard getGameBoard() {
		return this.gameBoard;
	}

	/**
	 * Obtains the display only board.
	 *
	 * @return the display only board.
	 */
	public io.adrisdn.chessnsix.gui.board.GameBoard.DisplayOnlyBoard getDisplayOnlyBoard() {
		return this.displayOnlyBoard;
	}

	/**
	 * Obtains the move history.
	 *
	 * @return the move history.
	 */
	public io.adrisdn.chessnsix.gui.moveHistory.MoveHistory getMoveHistory() {
		return this.moveHistory;
	}

	/**
	 * Obtains the timer panel.
	 *
	 * @return the timer panel.
	 */
	public io.adrisdn.chessnsix.gui.timer.TimerPanel getGameTimerPanel() {
		return this.gameTimerPanel;
	}

	/**
	 * Constructs a new GameScreen.
	 *
	 * @param chessGame The ChessGame instance managing the game.
	 */
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

	/**
	 * Initializes the game board stack.
	 *
	 * @return A Stack containing the game board elements.
	 */
	private Stack initGameBoard() {
		final Stack stack = new Stack();
		stack.add(this.displayOnlyBoard);
		stack.add(this.gameBoard);
		return stack;
	}

	/**
	 * Initializes the game menu.
	 *
	 * @return A Table containing the game menu buttons.
	 */
	private Table initGameMenu() {
		final Table table = new Table();
		final int BUTTON_WIDTH = 250;
		table.add(this.newGameButton()).width(BUTTON_WIDTH + 10);
		table.add(new FlipBoardButton(this)).width(BUTTON_WIDTH + 10);
		table.add(new GameOption(this)).width(BUTTON_WIDTH + 10);
		table.add(this.exitGameButton()).width(BUTTON_WIDTH + 10);
		return table;
	}

	/**
	 * Creates the buttin to go to the {@link SetupGame} screen
	 *
	 * @return the button
	 */
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

	/**
	 * Creates the dialog of confirmation to craete new game.
	 *
	 * @return
	 */
	private Dialog newGameDialog() {
		Label label = new Label(LanguageManager.get("new_game_text"), GuiUtils.UI_SKIN);
		label.setColor(Color.BLACK);
		Dialog dialog = new Dialog(LanguageManager.get("new_game_title"), GuiUtils.UI_SKIN) {
			@Override
			protected void result(Object object) {
				if ((boolean) object) {
					chessGame.getGameMusic().stop();
					chessGame.getMenuMusic().play();
					this.remove();
					if (gameBoard.isArtificialIntelligenceWorking()) {
						gameBoard.getArtificialIntelligence().setStopAI(true);
					}
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

	/**
	 * Creates the exit game button.
	 *
	 * @return the exit game button.
	 */
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

	/**
	 * Creates the dialog for confirmation to exit game.
	 *
	 * @return
	 */
	private Dialog exitGameDialog() {
		Label label = new Label(LanguageManager.get("exit_game_text"), GuiUtils.UI_SKIN);
		label.setColor(Color.BLACK);
		Dialog dialog = new Dialog(LanguageManager.get("exit_game_title"), GuiUtils.UI_SKIN) {
			@Override
			protected void result(Object object) {
				this.remove();
				if ((boolean) object) {
					chessGame.getGameMusic().stop();
					chessGame.getMenuMusic().play();
					this.remove();
					if (gameBoard.isArtificialIntelligenceWorking()) {
						gameBoard.getArtificialIntelligence().setStopAI(true);
					}
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

	/**
	 * Button to flip the board
	 */
	private static final class FlipBoardButton extends TextButton {

		/**
		 * Initializes the button to flip the board
		 * @param gameScreen screen where this button belongs to.
		 */
		private FlipBoardButton(final GameScreen gameScreen) {
			super(LanguageManager.get("flip_board"), GuiUtils.UI_SKIN);
			this.addListener(new ClickListener() {
				@Override
				public void clicked(final InputEvent event, final float x, final float y) {
					gameScreen.getGameTimerPanel().continueTimer(false);

					gameScreen.getGameBoard().updateBoardDirection();
					gameScreen.getGameBoard().drawBoard(gameScreen, gameScreen.getChessBoard(),
							gameScreen.getDisplayOnlyBoard());

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

}
