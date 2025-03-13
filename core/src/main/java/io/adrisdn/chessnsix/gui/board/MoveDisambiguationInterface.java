package io.adrisdn.chessnsix.gui.board;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import com.google.common.collect.ImmutableList;

import io.adrisdn.chessnsix.chess.engine.board.Move;
import io.adrisdn.chessnsix.gui.managers.GuiUtils;
import io.adrisdn.chessnsix.gui.managers.LanguageManager;
import io.adrisdn.chessnsix.gui.screens.GameScreen;

/**
 * Is used to display a dialog for the player to choose between multiple
 * possible moves in a chess game.
 */
public final class MoveDisambiguationInterface {

	private final GameScreen gameScreen;
	private final ImmutableList<Move> possibleMoves;
	private final Dialog possibleMovesDialog;
	private Move chosenMove;
	private DialogResultListener listener;

	/**
	 * Sets the listener that will be notified when a move is selected from the
	 * dialog.
	 *
	 * @param listener The listener that will handle the result when the dialog is
	 *                 closed.
	 */
	public void setListener(final DialogResultListener listener) {
		this.listener = listener;
	}

	/**
	 * Initializes the interface with a list of possible moves and a reference to
	 * the GameScreen.
	 *
	 * @param possibleMoves A list of possible moves that the player can choose
	 *                      from. It must contain at least two moves.
	 * @param gameScreen    The GameScreen instance that represents the screen where
	 *                      the dialog will be displayed.
	 */
	public MoveDisambiguationInterface(final ImmutableList<Move> possibleMoves, final GameScreen gameScreen) {
		if (possibleMoves.size() < 2) {
			throw new IllegalArgumentException("You shouldn't use this for just one possible move");
		}
		final Label label = new Label(LanguageManager.get("choose_move_text"), GuiUtils.UI_SKIN);
		label.setColor(Color.BLACK);
		this.possibleMoves = possibleMoves;
		this.gameScreen = gameScreen;
		// this.executor = new AsyncExecutor(1);
		possibleMovesDialog = new Dialog(LanguageManager.get("choose_move_title"), GuiUtils.UI_SKIN) {
			@Override
			protected void result(Object object) {
				chosenMove = (Move) object;
				if (listener != null) {
					listener.onDialogResult(chosenMove);
				}
			}
		};
		for (Move move : this.possibleMoves) {
			possibleMovesDialog.button(move.toString(), move);
		}
		possibleMovesDialog.text(label);
	}

	/**
	 * Displays the dialog that allows the user to choose between the available
	 * moves.
	 */
	public void showDisambiguateMoveDialog() {
		possibleMovesDialog.show(this.gameScreen.getStage());
	}

	/**
	 * Must be implemented by any class that wishes to receive the result from the
	 * move disambiguation dialog.
	 */
	public interface DialogResultListener {
		/**
		 * Is invoked when the user selects a move from the dialog. The chosen move is
		 * passed as the result.
		 *
		 * @param result move selected by the player
		 */
		void onDialogResult(Move result);
	}

}
