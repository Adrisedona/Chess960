package io.adrisdn.chessnsix.gui.gameMenu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import io.adrisdn.chessnsix.gui.GuiUtils;
import io.adrisdn.chessnsix.gui.board.GameProps;
import io.adrisdn.chessnsix.gui.screens.GameScreen;

public final class AIButton extends TextButton {

    public AIButton(final io.adrisdn.chessnsix.gui.screens.GameScreen gameScreen) {
        super("Setup AI", GuiUtils.UI_SKIN);//TODO: fix string
        final AIDialog aiDialog = new AIDialog(gameScreen);
        this.addListener(new ClickListener() {
            @Override
            public void clicked(final InputEvent event, final float x, final float y) {
                gameScreen.getGameTimerPanel().continueTimer(false);
                aiDialog.show(gameScreen.getStage());
            }
        });
    }

    private static final class AIDialog extends Dialog {

        private final CheckBox whitePlayerCheckBox, blackPlayerCheckBox;

        private AIDialog(final io.adrisdn.chessnsix.gui.screens.GameScreen gameScreen) {
            super("Setup AI", GuiUtils.UI_SKIN);//TODO: fix string

            this.whitePlayerCheckBox = new CheckBox("White as AI", GuiUtils.UI_SKIN);//TODO: fix string
            this.blackPlayerCheckBox = new CheckBox("Black as AI", GuiUtils.UI_SKIN);//TODO: fix string

            this.getContentTable().padTop(10);

            this.getContentTable().add(this.whitePlayerCheckBox).align(Align.left).row();
            this.getContentTable().add(this.blackPlayerCheckBox).align(Align.left).row();

            final Label label = new Label("Select Level", GuiUtils.UI_SKIN);//TODO: fix string
            label.setColor(Color.BLACK);
            this.getContentTable().add(label);
            this.getContentTable().add(gameScreen.getGameBoard().getArtificialIntelligence().getLevelSelector()).row();

            this.getContentTable().add(new OKButton(gameScreen, this)).align(Align.left);
            this.getContentTable().add(new CancelButton(gameScreen, this)).align(Align.right);
        }

        private static final class OKButton extends TextButton {

            public OKButton(final GameScreen gameScreen, final AIDialog aiDialog) {
                super("Ok", GuiUtils.UI_SKIN);//TODO: fix string
                this.addListener(new ClickListener() {
                    @Override
                    public void clicked(final InputEvent event, final float x, final float y) {
                        aiDialog.remove();
                        gameScreen.getGameBoard().updateWhitePlayerType(GameProps.PlayerType.getPlayerType(aiDialog.whitePlayerCheckBox.isChecked()));
                        gameScreen.getGameBoard().updateBlackPlayerType(GameProps.PlayerType.getPlayerType(aiDialog.blackPlayerCheckBox.isChecked()));
                        if (!gameScreen.getGameBoard().isAIPlayer(gameScreen.getChessBoard().currentPlayer())) {
                            gameScreen.getGameBoard().getArtificialIntelligence().setStopAI(true);
                        }
                        gameScreen.getGameBoard().fireGameSetupPropertyChangeSupport();
                        gameScreen.getGameTimerPanel().continueTimer(true);
                    }
                });
            }
        }
    }
}
