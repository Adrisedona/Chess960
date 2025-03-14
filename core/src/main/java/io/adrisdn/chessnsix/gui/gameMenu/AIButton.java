package io.adrisdn.chessnsix.gui.gameMenu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import io.adrisdn.chessnsix.gui.board.GameProps;
import io.adrisdn.chessnsix.gui.managers.GuiUtils;
import io.adrisdn.chessnsix.gui.managers.LanguageManager;
import io.adrisdn.chessnsix.gui.screens.GameScreen;

/**
 * Represents a button in the chess game that opens a dialog to configure AI
 * player settings.
 */
public final class AIButton extends TextButton {

	/**
	 * Initializes the AIButton as a button. When clicked, it opens an AI
	 * configuration dialog ({@link AIDialog}).
	 *
	 * @param chessGame The ChessGame instance
	 */
	public AIButton(final io.adrisdn.chessnsix.gui.ChessGame chessGame) {
		super(LanguageManager.get("setup_ai"), GuiUtils.UI_SKIN);
		final AIDialog aiDialog = new AIDialog(chessGame.getGameScreen());
		this.addListener(new ClickListener() {
			@Override
			public void clicked(final InputEvent event, final float x, final float y) {
				aiDialog.show(chessGame.getSetupGameScreen().getStage());
			}
		});
	}

	/**
	 * dialog box that appears when the user clicks on the {@link AIButton}.
	 */
	private static final class AIDialog extends Dialog {

		private final CheckBox whitePlayerCheckBox, blackPlayerCheckBox;

		/**
		 * Creates a dialog box for configuring AI settings. It
		 * includes checkboxes for selecting whether the white or black player will be
		 * AI-controlled and a level selector to choose the AI's difficulty level. Also
		 * includes "OK" and "Cancel" buttons for submitting or canceling the
		 * configuration.
		 *
		 * @param gameScreen The GameScreen instance
		 */
		private AIDialog(final io.adrisdn.chessnsix.gui.screens.GameScreen gameScreen) {
			super(LanguageManager.get("setup_ai"), GuiUtils.UI_SKIN);

			this.whitePlayerCheckBox = new CheckBox(LanguageManager.get("white_ai"), GuiUtils.UI_SKIN);
			this.blackPlayerCheckBox = new CheckBox(LanguageManager.get("black_ai"), GuiUtils.UI_SKIN);

			this.getContentTable().padTop(10);

			this.getContentTable().add(this.whitePlayerCheckBox).align(Align.left).row();
			this.getContentTable().add(this.blackPlayerCheckBox).align(Align.left).row();

			final Label label = new Label(LanguageManager.get("select_level"), GuiUtils.UI_SKIN);
			label.setColor(Color.BLACK);
			this.getContentTable().add(label);
			this.getContentTable().add(gameScreen.getGameBoard().getArtificialIntelligence().getLevelSelector()).row();

			this.getContentTable().add(new OKButton(gameScreen, this)).align(Align.left);
			this.getContentTable().add(new CancelButton(gameScreen, this)).align(Align.right);
		}

		/**
		 *epresents the "OK" button within the {@link AIDialog}. When clicked, it applies the selected AI settings and closes the dialog.
		 */
		private static final class OKButton extends TextButton {

			/**
			 * Creates an "OK" button that, when clicked, applies the AI settings from the dialog to the game.
			 * @param gameScreen The GameScreen instance
			 * @param aiDialog The instance of AIDialog that the "OK" button is part of.
			 */
			public OKButton(final GameScreen gameScreen, final AIDialog aiDialog) {
				super(LanguageManager.get("ok"), GuiUtils.UI_SKIN);
				this.addListener(new ClickListener() {
					@Override
					public void clicked(final InputEvent event, final float x, final float y) {
						aiDialog.remove();
						gameScreen.getGameBoard().updateWhitePlayerType(
								GameProps.PlayerType.getPlayerType(aiDialog.whitePlayerCheckBox.isChecked()));
						gameScreen.getGameBoard().updateBlackPlayerType(
								GameProps.PlayerType.getPlayerType(aiDialog.blackPlayerCheckBox.isChecked()));
						if (!gameScreen.getGameBoard().isAIPlayer(gameScreen.getChessBoard().currentPlayer())) {
							gameScreen.getGameBoard().getArtificialIntelligence().setStopAI(true);
						}
					}
				});
			}
		}
	}
}
