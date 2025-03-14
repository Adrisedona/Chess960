package io.adrisdn.chessnsix.gui.gameMenu;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import io.adrisdn.chessnsix.gui.managers.GuiUtils;
import io.adrisdn.chessnsix.gui.managers.LanguageManager;
import io.adrisdn.chessnsix.gui.screens.GameScreen;

/**
 * Button to cancel dialogs
 */
public final class CancelButton extends TextButton {

	/**
	 * Creates a button that cancels dialogs
	 * @param gameScreen The GameScreen instance
	 * @param dialog The dialog this button belongs to.
	 */
    protected CancelButton(final GameScreen gameScreen, final Dialog dialog) {
        super(LanguageManager.get("cancel"), GuiUtils.UI_SKIN);
        this.addListener(new ClickListener() {
            @Override
            public void clicked(final InputEvent event, final float x, final float y) {
                dialog.remove();
                gameScreen.getGameTimerPanel().continueTimer(true);
            }
        });
    }
}
