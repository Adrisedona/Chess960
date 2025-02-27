package io.adrisdn.chessnsix.gui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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
import io.adrisdn.chessnsix.gui.GuiUtils;
import io.adrisdn.chessnsix.gui.board.GameProps;
import io.adrisdn.chessnsix.gui.gameMenu.AIButton;

public class SetupGame implements Screen {

	private final Stage stage;

	private Table table;

	private int minutes;

	private final ChessGame chessGame;
	private final GameScreen gameScreen;

	private final AIButton aiButton;
	private final BoardColorButton boardColorButton;

	public SetupGame(final ChessGame chessGame) {
		this.chessGame = chessGame;
		this.gameScreen = this.chessGame.getGameScreen();
		this.aiButton = new AIButton(chessGame);
		this.boardColorButton = new BoardColorButton();
		this.stage = new Stage(new FitViewport(GuiUtils.WORLD_WIDTH, GuiUtils.WORLD_HEIGHT), new SpriteBatch());
		this.table = new Table(GuiUtils.UI_SKIN);

		this.table.add(this.timerButton()).pad(GuiUtils.PAD).width(GuiUtils.WIDTH);
		this.table.add(this.aiButton).pad(GuiUtils.PAD).width(GuiUtils.WIDTH).row();
		this.table.add(this.boardColorButton).pad(GuiUtils.PAD).width(GuiUtils.WIDTH);
		this.table.add(this.startGameButton()).pad(GuiUtils.PAD).width(GuiUtils.WIDTH).row();
		this.table.add(this.backButton()).width(GuiUtils.WIDTH * 2 + GuiUtils.PAD * 2).pad(GuiUtils.PAD).colspan(2);

		this.table.setWidth(GuiUtils.WORLD_WIDTH);
		this.table.setPosition(0, GuiUtils.WORLD_HEIGHT / 2);

		this.stage.addActor(table);
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

	public Stage getStage() {
		return stage;
	}

	private TextButton timerButton() {
		TextButton button = new TextButton("Setup Timer", GuiUtils.UI_SKIN);// TODO: fix string
		button.addListener(new ClickListener() {
			SetupTimer setupTimer = new SetupTimer(chessGame.getGameScreen());
			@Override
			public void clicked(InputEvent event, float x, float y) {
				setupTimer.show(stage);
			}
		});
		return button;
	}


	private TextButton startGameButton() {
		TextButton button = new TextButton("Start Game", GuiUtils.UI_SKIN);// TODO: fix string
		button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				startGame();
			}
		});
		return button;
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

	private final class SetupTimer extends Dialog {

		private final SelectBox<TimerMinute> timer;
		private int minute;

		private enum TimerMinute {
			FIVE {
				@Override
				int getMinute() {
					return 5;
				}

				@Override
				public String toString() {
					return "5 minutes";// TODO: fix string
				}
			},
			TEN {
				@Override
				int getMinute() {
					return 10;
				}

				@Override
				public String toString() {
					return "10 minutes";// TODO: fix string
				}
			},
			FIFTEEN {
				@Override
				int getMinute() {
					return 15;
				}

				@Override
				public String toString() {
					return "15 minutes";// TODO: fix string
				}
			},
			THIRTY {
				@Override
				int getMinute() {
					return 30;
				}

				@Override
				public String toString() {
					return "30 minutes";// TODO: fix string
				}
			},
			FORTY_FIVE {
				@Override
				int getMinute() {
					return 45;
				}

				@Override
				public String toString() {
					return "45 minutes";// TODO: fix string
				}
			},
			SIXTY {
				@Override
				int getMinute() {
					return 60;
				}

				@Override
				public String toString() {
					return "60 minutes";// TODO: fix string
				}
			},
			NO_TIMER {
				@Override
				int getMinute() {
					return -1;
				}

				@Override
				public String toString() {
					return "No Timer";// TODO: fix string
				}
			};

			abstract int getMinute();
		}

		private SetupTimer(final GameScreen gameScreen) {
			super("Setup Timer", GuiUtils.UI_SKIN);// TODO: fix string
			this.timer = new SelectBox<>(GuiUtils.UI_SKIN);
			this.timer.setItems(TimerMinute.FIVE, TimerMinute.TEN, TimerMinute.FIFTEEN, TimerMinute.THIRTY,
					TimerMinute.FORTY_FIVE, TimerMinute.SIXTY, TimerMinute.NO_TIMER);
			this.getContentTable().padTop(10);
			this.getContentTable().add(this.timer).padBottom(20).row();
			this.getContentTable().add(new SetupButton(gameScreen, this, "OK"));//TODO: fix string
			this.minute = BoardUtils.DEFAULT_TIMER_MINUTE;
			this.addListener(new ChangeListener() {
				@Override
				public void changed(final ChangeEvent event, final Actor actor) {
					minute = timer.getSelected().getMinute();
				}
			});
		}

		private final class SetupButton extends TextButton {

			private SetupButton(final GameScreen gameScreen, final SetupTimer setupTimer,
					final String text) {
				super(text, GuiUtils.UI_SKIN);
				this.addListener(new ClickListener() {
					@Override
					public void clicked(final InputEvent event, final float x, final float y) {
						SetupGame.this.minutes = setupTimer.minute;
						setupTimer.remove();
						// gameScreen.getGameBoard().getArtificialIntelligence().setStopAI(true);
						// setupTimer.restartGame(gameScreen, minutes);
					}
				});
			}
		}

	}

	private final class BoardColorButton extends TextButton {
        private BoardColorButton() {
            super("Board Color", GuiUtils.UI_SKIN);
            final Label label = new Label("Choose a Board Color", GuiUtils.UI_SKIN);
            label.setColor(Color.BLACK);
            final Dialog dialog = new Dialog("Board Color", GuiUtils.UI_SKIN).text(label);
            dialog.getButtonTable().add(boardStyle(dialog));
            dialog.getContentTable().row();

            this.addListener(new ClickListener() {
                @Override
                public void clicked(final InputEvent event, final float x, final float y) {
                    dialog.show(SetupGame.this.stage);
                }
            });
        }

        private Button[] boardStyle(final Dialog promoteDialog) {
            final Button[] buttons = new Button[6];
            for (int i = 0; i < 6; i++) {
                buttons[i] = new Button(new TextureRegionDrawable(GuiUtils.GET_TILE_TEXTURE_REGION(GuiUtils.BOARD_COLORS.get(i).toString())));
                final int finalI = i;
                buttons[i].addListener(new ClickListener() {
                    @Override
                    public void clicked(final InputEvent event, final float x, final float y) {
                        gameScreen.getDisplayOnlyBoard().setTileColor(GuiUtils.BOARD_COLORS.get(finalI));
                        // gameScreen.getGameBoard().drawBoard(gameScreen, gameScreen.getChessBoard(), gameScreen.getDisplayOnlyBoard());
                        promoteDialog.remove();
                        // gameScreen.getGameTimerPanel().continueTimer(true);
                    }
                });
            }
            return buttons;
        }
    }

	private void startGame() {
		// gameScreen.updateChessBoard(Board.createStandardBoard(minute, BoardUtils.DEFAULT_TIMER_SECOND,
		// 		BoardUtils.DEFAULT_TIMER_MILLISECOND));// TODO: nuevo juego con minutos va aqui
		gameScreen.updateChessBoard(GameScreen.BOARD_STATE.NEW_CHESS960_GAME.getBoard(gameScreen, minutes, BoardUtils.DEFAULT_TIMER_SECOND, BoardUtils.DEFAULT_TIMER_MILLISECOND));
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
		Gdx.input.setInputProcessor(gameScreen.getStage());
		chessGame.setScreen(gameScreen);
	}
}
