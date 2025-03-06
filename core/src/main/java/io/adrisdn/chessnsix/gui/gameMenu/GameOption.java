package io.adrisdn.chessnsix.gui.gameMenu;

import java.util.List;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.google.common.collect.ImmutableList;

import io.adrisdn.chessnsix.gui.board.GameProps;
import io.adrisdn.chessnsix.gui.managers.GuiUtils;
import io.adrisdn.chessnsix.gui.managers.LanguageManager;
import io.adrisdn.chessnsix.gui.screens.GameScreen;

/**
 * Represents a button that opens a dialog for configuring various game options,
 * such as highlighting legal moves, showing previous moves, and pausing the
 * game timer. It allows players to customize the game's behavior and appearance
 * during gameplay.
 */
public final class GameOption extends TextButton {

	/**
	 * Initializes the GameOption button. When clicked, it opens the
	 * {@link GameOptionDialog} that allows the user to adjust the game settings.
	 *
	 * @param gameScreen The GameScreen instance
	 */
	public GameOption(final GameScreen gameScreen) {
		super(LanguageManager.get("game_option_title"), GuiUtils.UI_SKIN);
		final GameOptionDialog gameMenuDialog = new GameOptionDialog(gameScreen);
		this.addListener(new ClickListener() {
			@Override
			public void clicked(final InputEvent event, final float x, final float y) {
				gameScreen.getGameTimerPanel().continueTimer(false);
				gameMenuDialog.show(gameScreen.getStage());
			}
		});
	}

	/**
	 * Represents the dialog that appears when the {@link GameOption} button is
	 * clicked.
	 */
	private static final class GameOptionDialog extends Dialog {

		/**
		 * creates the dialog and populates it with three checkboxes
		 * ({@link HighlightLegalMove}, {@link ShowPreviousMove}, {@link PauseTimer}),
		 * each representing a different game option.
		 *
		 * @param gameScreen The GameScreen instance
		 */
		private GameOptionDialog(final GameScreen gameScreen) {
			super(LanguageManager.get("game_option_title"), GuiUtils.UI_SKIN);
			this.getContentTable().padTop(10);
			final ImmutableList<GameOptionCheckBox> gameOptionCheckBoxList = ImmutableList.of(
					new HighlightLegalMove(gameScreen), new ShowPreviousMove(gameScreen), new PauseTimer(gameScreen));
			gameOptionCheckBoxList.forEach(gameOptionCheckBox -> this.getContentTable().add(gameOptionCheckBox)
					.align(Align.left).padBottom(20).row());
			this.getContentTable().add(new OKButton(gameScreen, this, gameOptionCheckBoxList)).align(Align.left);
			this.getContentTable().add(new CancelButton(gameScreen, this)).align(Align.right);
		}
	}

	/**
	 * Represents the "OK" button in the GameOptionDialog. When clicked, it applies
	 * the selected options and closes the dialog, resuming the game timer.
	 */
	private static final class OKButton extends TextButton {

		/**
		 * Creates the "OK" button, which applies the settings selected in the
		 * checkboxes when clicked.
		 *
		 * @param gameScreen             The GameScreen instance.
		 * @param dialog                 The GameOptionDialog instance.
		 * @param gameOptionCheckBoxList list of GameOptionCheckBox instances
		 *                               representing the options to be applied.
		 */
		protected OKButton(final GameScreen gameScreen, final Dialog dialog,
				final List<GameOptionCheckBox> gameOptionCheckBoxList) {
			super(LanguageManager.get("ok"), GuiUtils.UI_SKIN);
			this.addListener(new ClickListener() {
				@Override
				public void clicked(final InputEvent event, final float x, final float y) {
					gameScreen.getGameBoard().drawBoard(gameScreen, gameScreen.getChessBoard(),
							gameScreen.getDisplayOnlyBoard());
					dialog.remove();
					gameScreen.getGameTimerPanel().continueTimer(true);
					for (final GameOptionCheckBox gameOptionCheckBox : gameOptionCheckBoxList) {
						gameOptionCheckBox.update();
					}
				}
			});
		}
	}

	/**
	 * Represents a checkbox in the options dialog.
	 */
	private static abstract class GameOptionCheckBox extends CheckBox {

		private final GameScreen gameScreen;

		/**
		 * Initializes the checkbox with the given text and state.
		 *
		 * @param gameScreen  The GameScreen instance.
		 * @param text        The text label for the checkbox.
		 * @param commonState The initial state (checked or unchecked) of the checkbox.
		 */
		protected GameOptionCheckBox(final GameScreen gameScreen, final String text, final boolean commonState) {
			super(text, GuiUtils.UI_SKIN);
			this.gameScreen = gameScreen;
			this.setChecked(commonState);
		}

		/**
		 * ach subclass implements this method to apply the option's setting when the
		 * "OK" button is clicked.
		 */
		protected abstract void update();
	}

	/**
	 * Represents the option to pause the game timer.
	 */
	private static final class PauseTimer extends GameOptionCheckBox {

		/**
		 * Creates a checkbox for pausing the game timer, with the initial state set to false (unchecked).
		 * @param gameScreen The GameScreen instance.
		 */
		protected PauseTimer(final GameScreen gameScreen) {
			super(gameScreen, LanguageManager.get("pause_timer"), false);
		}

		@Override
		protected void update() {
			super.gameScreen.getGameTimerPanel().setPauseTimerOption(isChecked());
		}
	}

	/**
	 * Represents the option to highlight legal moves for the current player.
	 */
	private static final class HighlightLegalMove extends GameOptionCheckBox {

		/**
		 * Creates a checkbox for highlighting legal moves, with the initial state set to true (checked).
		 * @param gameScreen The GameScreen instance.
		 */
		protected HighlightLegalMove(final GameScreen gameScreen) {
			super(gameScreen, LanguageManager.get("highlight_legals"), true);
		}

		@Override
		protected void update() {
			super.gameScreen.getGameBoard()
					.updateHighlightMove(GameProps.HighlightMove.getHighlightMoveState(isChecked()));
		}
	}

	/**
	 * Represents the option to highlight the previous move made by either player.
	 */
	private static final class ShowPreviousMove extends GameOptionCheckBox {

		/**
		 * Creates a checkbox for showing previous moves, with the initial state set to true (checked).
		 * @param gameScreen The GameScreen instance.
		 */
		protected ShowPreviousMove(final GameScreen gameScreen) {
			super(gameScreen, LanguageManager.get("highlight_previous"), true);
		}

		@Override
		protected void update() {
			super.gameScreen.getGameBoard().updateHighlightPreviousMove(
					GameProps.HighlightPreviousMove.getHighlightPreviousMoveState(isChecked()));
		}
	}
}
