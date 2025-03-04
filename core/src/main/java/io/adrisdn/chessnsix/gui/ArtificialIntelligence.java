package io.adrisdn.chessnsix.gui;

import java.util.concurrent.Executors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;

import io.adrisdn.chessnsix.chess.engine.board.Move;
import io.adrisdn.chessnsix.chess.engine.player.artificialInteligence.MiniMax;
import io.adrisdn.chessnsix.gui.board.GameProps;
import io.adrisdn.chessnsix.gui.managers.GuiUtils;
import io.adrisdn.chessnsix.gui.screens.GameScreen;

/**
 * Handles the AI and its level
 */
public final class ArtificialIntelligence {

    private final SelectBox<Integer> level;
    private MiniMax miniMax;

	//Initializes the level selector and the AI
    public ArtificialIntelligence() {
        this.level = new SelectBox<>(GuiUtils.UI_SKIN);
        this.level.setItems(1, 2, 3, 4, 5);
        this.miniMax = new MiniMax(0);
    }

	/**
	 * Starts or stops the AI
	 * @param stopAI true to stop, false to start
	 */
    public void setStopAI(final boolean stopAI) {
        this.miniMax.setTerminateProcess(stopAI);
    }

	/**
	 * Obtains the AI level selector
	 * @return the AI level selector
	 */
    public SelectBox<Integer> getLevelSelector() {
        return this.level;
    }


	/**
	 * Starts the AI to play in the game screen
	 * @param gameScreen game screen where the game is played
	 */
    public void startAI(final GameScreen gameScreen) {
        if (this.level.getSelected() < 0 || this.level.getSelected() > 5) {
            throw new IllegalStateException("AI range from 1 to 5 ONLY");
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
