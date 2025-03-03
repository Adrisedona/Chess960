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


public final class GameOption extends TextButton {

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

    private static final class GameOptionDialog extends Dialog {

        private GameOptionDialog(final GameScreen gameScreen) {
            super(LanguageManager.get("game_option_title"), GuiUtils.UI_SKIN);
            this.getContentTable().padTop(10);
            final ImmutableList<GameOptionCheckBox> gameOptionCheckBoxList = ImmutableList.of(new HighlightLegalMove(gameScreen), new ShowPreviousMove(gameScreen), new PauseTimer(gameScreen));
            gameOptionCheckBoxList.forEach(gameOptionCheckBox -> this.getContentTable().add(gameOptionCheckBox).align(Align.left).padBottom(20).row());
            this.getContentTable().add(new OKButton(gameScreen, this, gameOptionCheckBoxList)).align(Align.left);
            this.getContentTable().add(new CancelButton(gameScreen, this)).align(Align.right);
        }
    }

    private static final class OKButton extends TextButton {

        protected OKButton(final GameScreen gameScreen, final Dialog dialog, final List<GameOptionCheckBox> gameOptionCheckBoxList) {
            super(LanguageManager.get("ok"), GuiUtils.UI_SKIN);
            this.addListener(new ClickListener() {
                @Override
                public void clicked(final InputEvent event, final float x, final float y) {
                    gameScreen.getGameBoard().drawBoard(gameScreen, gameScreen.getChessBoard(), gameScreen.getDisplayOnlyBoard());
                    dialog.remove();
                    gameScreen.getGameTimerPanel().continueTimer(true);
                    for (final GameOptionCheckBox gameOptionCheckBox : gameOptionCheckBoxList) {
                        gameOptionCheckBox.update();
                    }
                }
            });
        }
    }

    private static abstract class GameOptionCheckBox extends CheckBox {

        private final GameScreen gameScreen;

        protected GameOptionCheckBox(final GameScreen gameScreen, final String text, final boolean commonState) {
            super(text, GuiUtils.UI_SKIN);
            this.gameScreen = gameScreen;
            this.setChecked(commonState);
        }

        protected abstract void update();
    }

    private static final class PauseTimer extends GameOptionCheckBox {

        protected PauseTimer(final GameScreen gameScreen) {
            super(gameScreen, LanguageManager.get("pause_timer"), false);
        }

        @Override
        protected void update() {
            super.gameScreen.getGameTimerPanel().setPauseTimerOption(isChecked());
        }
    }

    private static final class HighlightLegalMove extends GameOptionCheckBox {

        protected HighlightLegalMove(final GameScreen gameScreen) {
            super(gameScreen, LanguageManager.get("highlight_legals"), true);
        }

        @Override
        protected void update() {
            super.gameScreen.getGameBoard().updateHighlightMove(GameProps.HighlightMove.getHighlightMoveState(isChecked()));
        }
    }

    private static final class ShowPreviousMove extends GameOptionCheckBox {

        protected ShowPreviousMove(final GameScreen gameScreen) {
            super(gameScreen, LanguageManager.get("highlight_previous"), true);
        }

        @Override
        protected void update() {
            super.gameScreen.getGameBoard().updateHighlightPreviousMove(GameProps.HighlightPreviousMove.getHighlightPreviousMoveState(isChecked()));
        }
    }
}
