package io.adrisdn.chessnsix.gui;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

import io.adrisdn.chessnsix.chess.engine.FEN.FenFisherRandom;
import io.adrisdn.chessnsix.chess.engine.board.Board;
import io.adrisdn.chessnsix.gui.board.GameProps.GameEnd;
import io.adrisdn.chessnsix.gui.screens.About;
import io.adrisdn.chessnsix.gui.screens.GameScreen;
import io.adrisdn.chessnsix.gui.screens.WelcomeScreen;

public final class ChessGame extends Game {

    private GameScreen gameScreen;
    private WelcomeScreen welcomeScreen;
    private About aboutScreen;

    @Override
    public void create() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		FenFisherRandom.InitFisherRandomList();
        this.gameScreen = new GameScreen(this);
        this.aboutScreen = new About(this);
        this.welcomeScreen = new WelcomeScreen(this);
        this.setScreen(this.welcomeScreen);
    }

    public GameScreen getGameScreen() {
        return this.gameScreen;
    }

    public WelcomeScreen getWelcomeScreen() {
        return this.welcomeScreen;
    }

    public About getAboutScreen() {
        return this.aboutScreen;
    }

    public void gotoGameScreen(final GameScreen.BOARD_STATE board_state, final Board board) {
        this.gameScreen.updateChessBoard(board);
        if (board_state == GameScreen.BOARD_STATE.NEW_GAME) {
            this.gameScreen.getMoveHistory().getMoveLog().clear();
        }
        this.gameScreen.getGameBoard().updateAiMove(null);
        this.gameScreen.getGameBoard().updateHumanMove(null);
        this.gameScreen.getMoveHistory().updateMoveHistory();
        this.gameScreen.getGameBoard().drawBoard(this.gameScreen, gameScreen.getChessBoard(), this.gameScreen.getDisplayOnlyBoard());
        this.gameScreen.getGameBoard().updateGameEnd(GameEnd.ONGOING);
        this.gameScreen.getGameTimerPanel().resetTimer(this.gameScreen.getChessBoard().whitePlayer(), this.gameScreen.getChessBoard().blackPlayer());
        this.gameScreen.getGameTimerPanel().continueTimer(true);
        Gdx.input.setInputProcessor(this.gameScreen.getStage());
        this.setScreen(this.gameScreen);
    }

    @Override
    public void dispose() {
        this.gameScreen.dispose();
        this.welcomeScreen.dispose();
        this.aboutScreen.dispose();
    }
}
