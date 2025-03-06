package io.adrisdn.chessnsix.gui.timer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

import io.adrisdn.chessnsix.chess.engine.board.BoardUtils;
import io.adrisdn.chessnsix.chess.engine.player.Player;
import io.adrisdn.chessnsix.gui.managers.GuiUtils;
import io.adrisdn.chessnsix.gui.managers.LanguageManager;
import io.adrisdn.chessnsix.gui.screens.GameScreen;

/**
 * Handles the timers of both players
 */
public final class TimerPanel extends Table {

	/**
	 * Size of the TimerPanel
	 */
    public static final int SIZE = GuiUtils.GAME_BOARD_SR_SIZE / 2 - 15;

    private final PlayerTimerTable whitePlayerTimerTable, blackPlayerTimerTable;
    private boolean continueTimer, pauseTimerOption, noTimer;
    private TIMER_PANEL_DIRECTION timer_panel_direction;

	/**
	 * The possible values for the orientation of the TimerPanel
	 */
    private enum TIMER_PANEL_DIRECTION {
        FLIPPED {
            @Override
            void flip(final TimerPanel timerPanel) {
                timerPanel.clearChildren();
                timerPanel.add(timerPanel.whitePlayerTimerTable).size(TimerPanel.SIZE).row();
                timerPanel.add(timerPanel.blackPlayerTimerTable).size(TimerPanel.SIZE);
            }

            @Override
            TIMER_PANEL_DIRECTION getOpposite() {
                return NORMAL;
            }
        },
        NORMAL {
            @Override
            void flip(final TimerPanel timerPanel) {
                timerPanel.clearChildren();
                timerPanel.add(timerPanel.blackPlayerTimerTable).size(TimerPanel.SIZE).row();
                timerPanel.add(timerPanel.whitePlayerTimerTable).size(TimerPanel.SIZE);
            }

            @Override
            TIMER_PANEL_DIRECTION getOpposite() {
                return FLIPPED;
            }
        };

		/**
		 * Flips the timerPanel vertically
		 * @param timerPanel component to flip
		 */
        abstract void flip(final TimerPanel timerPanel);

		/**
		 * Obtains the opposite value of the current orientation
		 * @return the opposite value of the current orientation
		 */
        abstract TIMER_PANEL_DIRECTION getOpposite();

		/**
		 * Updates the TimerPanel in a GameScreen
		 * @param timerPanel the component to update
		 * @param gameScreen the screen where the timerPanel belongs
		 */
        private void update(final TimerPanel timerPanel, final GameScreen gameScreen) {
            if (gameScreen.getGameBoard().isGameEnd()) {
                return;
            }
            gameScreen.getGameBoard().displayTimeOutMessage(gameScreen.getChessBoard(), gameScreen.getStage());
            if (gameScreen.getChessBoard().currentPlayer().getLeague().isWhite()) {
                timerPanel.whitePlayerTimerTable.updateTimer(gameScreen.getChessBoard().currentPlayer());
            } else {
                timerPanel.blackPlayerTimerTable.updateTimer(gameScreen.getChessBoard().currentPlayer());
            }
        }
    }

	/**
	 * Initializes the properties of the TimerPanel
	 */
    public TimerPanel() {
        this.setVisible(true);
        this.whitePlayerTimerTable = new PlayerTimerTable(Color.WHITE, Color.BLACK, LanguageManager.get("white_player"));
        this.continueTimer = true;
        this.pauseTimerOption = false;
        this.noTimer = false;
        this.blackPlayerTimerTable = new PlayerTimerTable(Color.BLACK, Color.WHITE, LanguageManager.get("black_player"));
        this.timer_panel_direction = TIMER_PANEL_DIRECTION.NORMAL;
        this.add(this.blackPlayerTimerTable).size(SIZE).row();
        this.add(this.whitePlayerTimerTable).size(SIZE);
    }

    /**
	 * Stablishes if the timer is paused or not
	 * @param pauseTimerOption if the timer is paused or not
	 */
    public void setPauseTimerOption(final boolean pauseTimerOption) {
        this.pauseTimerOption = pauseTimerOption;
    }

	/**
	 * Stablishes if the timer continues or not
	 * @param continueTimer true to continue, false to pause.
	 */
    public void continueTimer(final boolean continueTimer) {
        this.continueTimer = continueTimer;
    }

    /**
	 * Obtains if the timer is paused or not
	 * @param pauseTimerOption true if the timer is paused, false if not
	 */
    public boolean isPauseTimerOption() {
        return this.pauseTimerOption;
    }

	/**
	 * Obtains whether the timer continues or not
	 * @return true means continue, false means pause.
	 */
    public boolean isTimerContinue() {
        return this.continueTimer;
    }

	/**
	 * Obtains if this game has a timer or not
	 * @return true if it doesn't have, false otherwise.
	 */
    public boolean isNoTimer() {
        return this.noTimer;
    }

	/**
	 * Changes the direction of the timerPanel.
	 */
    public void changeTimerPanelDirection() {
        this.timer_panel_direction = this.timer_panel_direction.getOpposite();
        this.continueTimer = true;
        this.timer_panel_direction.flip(this);
    }

	/**
	 * Resets the timerPanel
	 * @param whitePlayer white player
	 * @param blackPlayer black player
	 */
    public void resetTimer(final Player whitePlayer, final Player blackPlayer) {
        this.noTimer = whitePlayer.isNoTimer() && blackPlayer.isNoTimer();
        this.whitePlayerTimerTable.resetTimer(whitePlayer);
        this.blackPlayerTimerTable.resetTimer(blackPlayer);
    }

	/**
	 * Updates the timerPanel.
	 * @param gameScreen screen where this panel belongs.
	 */
    public void update(final GameScreen gameScreen) {
        this.timer_panel_direction.update(this, gameScreen);
    }

	/**
	 * Represents the timer of a player.
	 */
    private static final class PlayerTimerTable extends Table {

        private final Label timerLabel;

		/**
		 * Initializes the timer of a player
		 * @param panelColor color of the timer
		 * @param labelColor color of the text
		 * @param text text of the timer
		 */
        private PlayerTimerTable(final Color panelColor, final Color labelColor, final CharSequence text) {
            super(GuiUtils.UI_SKIN);
            final Label label = new Label(text, GuiUtils.UI_SKIN);
            label.setColor(labelColor);
            this.add(label).row();
            this.timerLabel = new Label(this.getTimeFormat(BoardUtils.DEFAULT_TIMER_MINUTE, BoardUtils.DEFAULT_TIMER_SECOND, BoardUtils.DEFAULT_TIMER_MILLISECOND), GuiUtils.UI_SKIN);
            this.timerLabel.setColor(labelColor);
            this.add(timerLabel);
            this.setVisible(true);
            this.setBackground(new NinePatchDrawable(new NinePatch(GuiUtils.GET_TILE_TEXTURE_REGION("white"), panelColor)));
        }

		/**
		 * Formats the time of the player in a string
		 * @param minute minutes of the player
		 * @param second seconds of the player
		 * @param millisecond milliseconds of the player.
		 * @return The formatted string
		 */
        private String getTimeFormat(final int minute, final int second, final int millisecond) {
			return String.format("%02d : %02d : %02d", minute, second, millisecond);
        }

		/**
		 * Upadtes a player's timer.
		 * @param player player whose timer has to be updated.
		 */
        private void updateTimer(final Player player) {
			if (player.isNoTimer()) {
				return;
			}
            player.countDown();
            this.timerLabel.setText(this.getTimeFormat(player.getMinute(), player.getSecond(), player.getMillisecond()));
        }

		/**
		 * Reset a player's timer.
		 * @param player player whose timer has to be resetted.
		 */
        private void resetTimer(final Player player) {
            if (!player.isNoTimer()) {
                this.timerLabel.setText(this.getTimeFormat(player.getMinute(), player.getSecond(), player.getMillisecond()));
            } else {
                this.timerLabel.setText(LanguageManager.get("no_timer"));
            }
        }
    }
}
