package io.adrisdn.chessnsix.gui.board;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import com.google.common.collect.ImmutableList;

import io.adrisdn.chessnsix.chess.engine.board.Move;

import io.adrisdn.chessnsix.gui.GuiUtils;
import io.adrisdn.chessnsix.gui.screens.GameScreen;


public class MoveDisambiguationInterface {
	
	private final GameScreen gameScreen;
	private final ImmutableList<Move> possibleMoves;
	private final Dialog possibleMovesDialog;
	private Move chosenMove;
	private DialogResultListener listener;

	public void setListener(DialogResultListener listener) {
		this.listener = listener;
	}

	public MoveDisambiguationInterface(final ImmutableList<Move> possibleMoves, final GameScreen gameScreen) {
		if (possibleMoves.size() < 2) {
			throw new IllegalArgumentException("You shouldn't use this for just one possible move");
		}
		this.possibleMoves = possibleMoves;
		this.gameScreen = gameScreen;
		// this.executor = new AsyncExecutor(1);
		possibleMovesDialog = new Dialog("title", GuiUtils.UI_SKIN) {//TODO: fix string
			@Override
			protected void result(Object object) {
				chosenMove = (Move)object;
				if (listener != null) {
					listener.onDialogResult(chosenMove);
				}
			}
		};
		for (Move move : this.possibleMoves) {
			possibleMovesDialog.button(move.toString(), move);
		}
		possibleMovesDialog.text(new Label("text for picking move", GuiUtils.UI_SKIN));//TODO: fix string
	}

	public void showDisambiguateMoveDialog() {
		possibleMovesDialog.show(this.gameScreen.getStage());
	}

	public interface DialogResultListener {
        void onDialogResult(Move result);
    }

	// private MoveTextButton[] moveDisambiguationButtons() {
	// 	MoveTextButton[] buttons = new MoveTextButton[possibleMoves.size()];
	// 	for (int i = 0; i < buttons.length; i++) {
	// 		buttons[i] = new MoveTextButton(possibleMoves.get(i).toString(), GuiUtils.UI_SKIN, this, possibleMovesDialog,
	// 				possibleMoves.get(i));
	// 	}
	// 	return buttons;
	// }

	// private class MoveTextButton extends TextButton {

	// 	public MoveTextButton(String text, Skin skin, final MoveDisambiguationInterface moveInterface,
	// 			final Dialog dialog, final Move move) {
	// 		super(text, skin);
	// 		this.addListener(new ClickListener() {
	// 			@Override
	// 			public void clicked(InputEvent event, float x, float y) {
	// 				super.clicked(event, x, y);
	// 				chosenMove = move;
	// 				dialog.hide();
	// 			}
	// 		});
	// 	}

	// }
}
