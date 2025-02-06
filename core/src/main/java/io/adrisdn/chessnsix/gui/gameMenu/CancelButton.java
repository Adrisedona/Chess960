package io.adrisdn.chessnsix.gui.gameMenu;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import io.adrisdn.chessnsix.gui.GuiUtils;
import io.adrisdn.chessnsix.gui.gameScreen.GameScreen;


public final class CancelButton extends TextButton {

    protected CancelButton(final GameScreen gameScreen, final Dialog dialog) {
        super("Cancel", GuiUtils.UI_SKIN);
        this.addListener(new ClickListener() {
            @Override
            public void clicked(final InputEvent event, final float x, final float y) {
                dialog.remove();
                gameScreen.getGameTimerPanel().continueTimer(true);
            }
        });
    }
}
