package io.adrisdn.chessnsix.gui;

import java.util.concurrent.Executors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;

import io.adrisdn.chessnsix.chess.engine.board.Move;
import io.adrisdn.chessnsix.chess.engine.player.artificialInteligence.MiniMax;
import io.adrisdn.chessnsix.gui.board.GameProps;
import io.adrisdn.chessnsix.gui.managers.GuiUtils;
import io.adrisdn.chessnsix.gui.screens.GameScreen;

public final class ArtificialIntelligence {

    private final SelectBox<Integer> level;
    private MiniMax miniMax;

    public ArtificialIntelligence() {
        this.level = new SelectBox<>(GuiUtils.UI_SKIN);
        this.level.setItems(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        this.miniMax = new MiniMax(0);
    }

    public void setStopAI(final boolean stopAI) {
        this.miniMax.setTerminateProcess(stopAI);
    }

    public SelectBox<Integer> getLevelSelector() {
        return this.level;
    }


    public int getMoveCount() {
        return this.miniMax.getMoveCount();
    }


    public void startAI(final GameScreen gameScreen) {
        if (this.level.getSelected() < 0 || this.level.getSelected() > 10) {
            throw new IllegalStateException("AI range from 1 to 10 ONLY");
        }
        Executors.newSingleThreadExecutor().execute(() -> {
            this.miniMax = new MiniMax(this.level.getSelected());
            final Move bestMove = miniMax.execute(gameScreen.getChessBoard());
            gameScreen.getGameBoard().updateAiMove(bestMove);
            gameScreen.getGameBoard().updateHumanMove(null);
            if (!bestMove.equals(Move.MoveFactory.getNullMove())) {
                gameScreen.updateChessBoard(gameScreen.getChessBoard().currentPlayer().makeMove(bestMove).getLatestBoard());
            }
            if (!this.miniMax.getTerminateProcess()) {
                Gdx.app.postRunnable(() -> {
                    gameScreen.getMoveHistory().getMoveLog().addMove(bestMove);
                    gameScreen.getMoveHistory().updateMoveHistory();
                    gameScreen.getGameBoard().drawBoard(gameScreen, gameScreen.getChessBoard(), gameScreen.getDisplayOnlyBoard());
                    gameScreen.getGameBoard().fireGameSetupPropertyChangeSupport();
                });
            }
            this.setStopAI(false);
            gameScreen.getGameBoard().updateArtificialIntelligenceWorking(GameProps.ArtificialIntelligenceWorking.RESTING);
        });
    }
}
