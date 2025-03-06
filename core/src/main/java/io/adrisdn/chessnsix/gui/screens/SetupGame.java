package io.adrisdn.chessnsix.gui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;

import io.adrisdn.chessnsix.chess.engine.board.BoardUtils;
import io.adrisdn.chessnsix.gui.ChessGame;
import io.adrisdn.chessnsix.gui.board.GameProps;
import io.adrisdn.chessnsix.gui.gameMenu.AIButton;
import io.adrisdn.chessnsix.gui.managers.GuiUtils;
import io.adrisdn.chessnsix.gui.managers.LanguageManager;

/**
 * Screen to handle the game configuration, like time, AI or color scheme.
 */
public class SetupGame extends AbstractScreen {

	private Table table;

	private int minutes;

	private final ChessGame chessGame;

	private final AIButton aiButton;
	private final BoardColorButton boardColorButton;

	/**
	 * Initializes this screen and its components
	 *
	 * @param chessGame the game this screen belongs to.
	 */
	public SetupGame(final ChessGame chessGame) {
		this.chessGame = chessGame;
		this.aiButton = new AIButton(chessGame);
		this.boardColorButton = new BoardColorButton();
		this.stage = new Stage(new FitViewport(GuiUtils.WORLD_WIDTH, GuiUtils.WORLD_HEIGHT), new SpriteBatch());
		this.table = new Table(GuiUtils.UI_SKIN);
		this.minutes = BoardUtils.DEFAULT_TIMER_MINUTE;

		this.table.add(this.timerButton()).pad(GuiUtils.PAD).width(GuiUtils.WIDTH + 50);
		this.table.add(this.aiButton).pad(GuiUtils.PAD).width(GuiUtils.WIDTH + 50).row();
		this.table.add(this.boardColorButton).pad(GuiUtils.PAD).width(GuiUtils.WIDTH + 50);
		this.table.add(this.startGameButton()).pad(GuiUtils.PAD).width(GuiUtils.WIDTH + 50).row();
		this.table.add(this.backButton()).width(GuiUtils.WIDTH * 2 + GuiUtils.PAD * 2 + 100).pad(GuiUtils.PAD)
				.colspan(2);

		this.table.setWidth(GuiUtils.WORLD_WIDTH);
		this.table.setPosition(0, GuiUtils.WORLD_HEIGHT / 2);

		this.stage.addActor(table);
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

	/**
	 * Creates the button that displays the dialog for configuration of the timer.
	 *
	 * @return the button that displays the dialog for configuration of the timer.
	 */
	private TextButton timerButton() {
		TextButton button = new TextButton(LanguageManager.get("setup_timer"), GuiUtils.UI_SKIN);
		button.addListener(new ClickListener() {
			SetupTimer setupTimer = new SetupTimer();

			@Override
			public void clicked(InputEvent event, float x, float y) {
				setupTimer.show(stage);
			}
		});
		return button;
	}

	/**
	 * Creates the button that starts the game when pressed.
	 *
	 * @return the button that starts the game when pressed.
	 */
	private TextButton startGameButton() {
		TextButton button = new TextButton(LanguageManager.get("start_game"), GuiUtils.UI_SKIN);
		button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				startGame();
			}
		});
		return button;
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
	 * Dialog to configure the timer
	 */
	private final class SetupTimer extends Dialog {

		private final SelectBox<TimerMinute> timer;
		private int minute;

		/**
		 * Enum with the different possible timer values.
		 */
		private enum TimerMinute {
			FIVE {
				@Override
				int getMinute() {
					return 5;
				}

				@Override
				public String toString() {
					return LanguageManager.get("five_minutes");
				}
			},
			TEN {
				@Override
				int getMinute() {
					return 10;
				}

				@Override
				public String toString() {
					return LanguageManager.get("ten_minutes");
				}
			},
			FIFTEEN {
				@Override
				int getMinute() {
					return 15;
				}

				@Override
				public String toString() {
					return LanguageManager.get("fifteen_minutes");
				}
			},
			THIRTY {
				@Override
				int getMinute() {
					return 30;
				}

				@Override
				public String toString() {
					return LanguageManager.get("thirty_minutes");
				}
			},
			FORTY_FIVE {
				@Override
				int getMinute() {
					return 45;
				}

				@Override
				public String toString() {
					return LanguageManager.get("forty_five_minutes");
				}
			},
			SIXTY {
				@Override
				int getMinute() {
					return 60;
				}

				@Override
				public String toString() {
					return LanguageManager.get("one_hour");
				}
			},
			NO_TIMER {
				@Override
				int getMinute() {
					return -1;
				}

				@Override
				public String toString() {
					return LanguageManager.get("no_timer");
				}
			};

			/**
			 * Returns the amount of minutes for the timer.
			 *
			 * @return the amount of minutes for the timer.
			 */
			abstract int getMinute();
		}

		/**
		 * Initializes the configuration dialog.
		 */
		private SetupTimer() {
			super(LanguageManager.get("setup_timer"), GuiUtils.UI_SKIN);
			this.timer = new SelectBox<>(GuiUtils.UI_SKIN);
			this.timer.setItems(TimerMinute.FIVE, TimerMinute.TEN, TimerMinute.FIFTEEN, TimerMinute.THIRTY,
					TimerMinute.FORTY_FIVE, TimerMinute.SIXTY, TimerMinute.NO_TIMER);
			this.getContentTable().padTop(10);
			this.getContentTable().add(this.timer).padBottom(20).row();
			this.getContentTable().add(new SetupButton(this, LanguageManager.get("ok")));
			this.minute = BoardUtils.DEFAULT_TIMER_MINUTE;
			this.addListener(new ChangeListener() {
				@Override
				public void changed(final ChangeEvent event, final Actor actor) {
					minute = timer.getSelected().getMinute();
				}
			});
		}

		/**
		 * Button that confirms the time selected in the dialog
		 */
		private final class SetupButton extends TextButton {

			/**
			 * Initializes the buttom
			 *
			 * @param setupTimer timer where the button belongs
			 * @param text       text of the button
			 */
			private SetupButton(final SetupTimer setupTimer,
					final String text) {
				super(text, GuiUtils.UI_SKIN);
				this.addListener(new ClickListener() {
					@Override
					public void clicked(final InputEvent event, final float x, final float y) {
						SetupGame.this.minutes = setupTimer.minute;
						setupTimer.remove();
					}
				});
			}
		}

	}

	/**
	 * Button that shows the dialog to choose the color palette.
	 */
	private final class BoardColorButton extends TextButton {

		/**
		 * Initializes the button
		 */
		private BoardColorButton() {
			super(LanguageManager.get("board_color"), GuiUtils.UI_SKIN);
			final Label label = new Label(LanguageManager.get("board_color_text"), GuiUtils.UI_SKIN);
			label.setColor(Color.BLACK);
			final Dialog dialog = new Dialog(LanguageManager.get("board_color"), GuiUtils.UI_SKIN).text(label);
			dialog.getButtonTable().add(boardStyle(dialog));
			dialog.getContentTable().row();

			this.addListener(new ClickListener() {
				@Override
				public void clicked(final InputEvent event, final float x, final float y) {
					dialog.show(SetupGame.this.stage);
				}
			});
		}

		/**
		 * Generates the button for choosing a color palete.
		 * @param dialog dialog where the buttons belong.
		 * @return an array with all the buttons.
		 */
		private Button[] boardStyle(final Dialog dialog) {
			final Button[] buttons = new Button[6];
			for (int i = 0; i < 6; i++) {
				buttons[i] = new Button(new TextureRegionDrawable(
						GuiUtils.GET_TILE_TEXTURE_REGION(GuiUtils.BOARD_COLORS.get(i).toString())));
				final int finalI = i;
				buttons[i].addListener(new ClickListener() {
					@Override
					public void clicked(final InputEvent event, final float x, final float y) {
						chessGame.getGameScreen().getDisplayOnlyBoard().setTileColor(GuiUtils.BOARD_COLORS.get(finalI));
						dialog.remove();
					}
				});
			}
			return buttons;
		}
	}

	/**
	 * Starts a new game with the configured values.
	 */
	private void startGame() {
		GameScreen gameScreen = chessGame.getGameScreen();
		gameScreen.updateChessBoard(GameScreen.BOARD_STATE.NEW_CHESS960_GAME.getBoard(gameScreen, minutes,
				BoardUtils.DEFAULT_TIMER_SECOND, BoardUtils.DEFAULT_TIMER_MILLISECOND));
		gameScreen.getMoveHistory().getMoveLog().clear();
		gameScreen.getGameBoard().updateHumanPiece(null);
		gameScreen.getGameBoard().updateAiMove(null);
		gameScreen.getGameBoard().updateHumanMove(null);
		gameScreen.getGameBoard().drawBoard(gameScreen, gameScreen.getChessBoard(), gameScreen.getDisplayOnlyBoard());
		gameScreen.getGameBoard().updateGameEnd(GameProps.GameEnd.ONGOING);
		gameScreen.getMoveHistory().updateMoveHistory();
		gameScreen.getGameTimerPanel().resetTimer(gameScreen.getChessBoard().whitePlayer(),
				gameScreen.getChessBoard().blackPlayer());
		gameScreen.getGameTimerPanel().continueTimer(true);
		gameScreen.getGameBoard().fireGameSetupPropertyChangeSupport();
		chessGame.getMenuMusic().stop();
		chessGame.getGameMusic().play();
		chessGame.setScreen(gameScreen);
	}
}
