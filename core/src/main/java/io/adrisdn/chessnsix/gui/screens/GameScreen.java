package io.adrisdn.chessnsix.gui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.viewport.FitViewport;

import io.adrisdn.chessnsix.chess.engine.FEN.FenFisherRandom;
import io.adrisdn.chessnsix.chess.engine.FEN.FenUtilities;
import io.adrisdn.chessnsix.chess.engine.board.Board;
import io.adrisdn.chessnsix.chess.engine.board.BoardUtils;
import io.adrisdn.chessnsix.gui.ChessGame;
import io.adrisdn.chessnsix.gui.GuiUtils;
import io.adrisdn.chessnsix.gui.board.GameBoard;
import io.adrisdn.chessnsix.gui.gameMenu.AIButton;
import io.adrisdn.chessnsix.gui.gameMenu.GameMenu;
import io.adrisdn.chessnsix.gui.gameMenu.GameOption;
import io.adrisdn.chessnsix.gui.gameMenu.GamePreference;
import io.adrisdn.chessnsix.gui.moveHistory.MoveHistory;
import io.adrisdn.chessnsix.gui.timer.TimerPanel;

public final class GameScreen implements Screen {

    private final Stage stage;
    private Board chessBoard;

    private final io.adrisdn.chessnsix.gui.board.GameBoard gameBoard;
    private final io.adrisdn.chessnsix.gui.board.GameBoard.DisplayOnlyBoard displayOnlyBoard;
    private final io.adrisdn.chessnsix.gui.moveHistory.MoveHistory moveHistory;
    private final io.adrisdn.chessnsix.gui.timer.TimerPanel gameTimerPanel;

    private final io.adrisdn.chessnsix.gui.gameMenu.GameMenu gameMenu;
    private final io.adrisdn.chessnsix.gui.gameMenu.GamePreference gamePreference;

    public enum BOARD_STATE {
        NEW_GAME {
            @Override
            public Board getBoard(final GameScreen gameScreen, int minutes, int seconds, int milliseconds) {
                return Board.createStandardBoard(minutes, seconds, milliseconds);
            }
        }, LOAD_GAME {
            @Override
            public Board getBoard(final GameScreen gameScreen, int minutes, int seconds, int milliseconds) {
                return FenUtilities.createGameFromSavedData(GuiUtils.MOVE_LOG_PREF.getString(GuiUtils.MOVE_LOG_STATE), gameScreen.getMoveHistory().getMoveLog());
            }
        }, NEW_CHESS960_GAME {
			@Override
            public Board getBoard(final GameScreen gameScreen, int minutes, int seconds, int milliseconds) {
                return FenUtilities.createGameFromFEN(FenFisherRandom.getRandomFen(), minutes, seconds, milliseconds);
            }
		};

        public abstract Board getBoard(final GameScreen gameScreen, int minutes, int seconds, int milliseconds);
    }

    //setter
    public void updateChessBoard(final Board board) {
        this.chessBoard = board;
    }

    //getter
    public Board getChessBoard() {
        return this.chessBoard;
    }

    public io.adrisdn.chessnsix.gui.board.GameBoard getGameBoard() {
        return this.gameBoard;
    }

    public io.adrisdn.chessnsix.gui.board.GameBoard.DisplayOnlyBoard getDisplayOnlyBoard() {
        return this.displayOnlyBoard;
    }

    public io.adrisdn.chessnsix.gui.moveHistory.MoveHistory getMoveHistory() {
        return this.moveHistory;
    }

    public io.adrisdn.chessnsix.gui.timer.TimerPanel getGameTimerPanel() {
        return this.gameTimerPanel;
    }

    public Stage getStage() {
        return this.stage;
    }

    public GameScreen(final ChessGame chessGame) {
        //init
        this.stage = new Stage(new FitViewport(GuiUtils.WORLD_WIDTH, GuiUtils.WORLD_HEIGHT), new SpriteBatch());
        this.chessBoard = Board.createStandardBoard(BoardUtils.DEFAULT_TIMER_MINUTE, BoardUtils.DEFAULT_TIMER_SECOND, BoardUtils.DEFAULT_TIMER_MILLISECOND);
        this.moveHistory = new MoveHistory();
        this.gameBoard = new io.adrisdn.chessnsix.gui.board.GameBoard(this);
        this.displayOnlyBoard = new GameBoard.DisplayOnlyBoard();
        this.gameTimerPanel = new TimerPanel();

        this.gameMenu = new GameMenu(chessGame, this);
        this.gamePreference = new GamePreference(this);

        Gdx.graphics.setTitle("LibGDX Simple Parallel Chess 2.0");//TODO: cambiar titulo a recurso

        final VerticalGroup verticalGroup = new VerticalGroup();

        final HorizontalGroup horizontalGroup = new HorizontalGroup();

        horizontalGroup.addActor(this.moveHistory);
        horizontalGroup.addActor(this.initGameBoard());
        horizontalGroup.addActor(this.gameTimerPanel);

        verticalGroup.setFillParent(true);
        verticalGroup.addActor(this.initGameMenu());
        verticalGroup.addActor(horizontalGroup);

        this.stage.addActor(verticalGroup);
    }

    private Stack initGameBoard() {
        final Stack stack = new Stack();
        stack.add(this.displayOnlyBoard);
        stack.add(this.gameBoard);
        return stack;
    }

    private Table initGameMenu() {
        final Table table = new Table();
        final int BUTTON_WIDTH = 250;
        table.add(this.gameMenu).width(BUTTON_WIDTH);
        table.add(this.gamePreference).width(BUTTON_WIDTH);
        table.add(new GameOption(this)).width(BUTTON_WIDTH);
        table.add(new AIButton(this)).width(BUTTON_WIDTH);
        return table;
    }

    @Override
    public void resize(final int width, final int height) {
        this.stage.getViewport().update(width, height, true);
    }

    @Override
    public void render(final float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        this.stage.act(delta);
        this.gameMenu.detectKeyPressed(this);
        this.gamePreference.detectUndoMoveKeyPressed(this);
        if (this.getGameBoard().getArtificialIntelligenceWorking()) {
            this.getGameBoard().getArtificialIntelligence().getProgressBar().setValue(this.getGameBoard().getArtificialIntelligence().getMoveCount());
        }
        if (!this.gameTimerPanel.isNoTimer() && this.gameTimerPanel.isTimerContinue() && !this.gameTimerPanel.isPauseTimerOption()) {
            this.gameTimerPanel.update(this);
            if (this.gameBoard.isAIPlayer(this.chessBoard.currentPlayer()) && this.chessBoard.currentPlayer().isTimeOut()) {
                this.gameBoard.getArtificialIntelligence().setStopAI(true);
            }
        }
        this.stage.getBatch().begin();
        this.stage.getBatch().draw(GuiUtils.BACKGROUND, 0, 0);
        this.stage.getBatch().end();
        this.stage.draw();
    }

    @Override
    public void dispose() {
        this.stage.dispose();
        this.stage.getBatch().dispose();
        GuiUtils.dispose();
    }

    @Deprecated
    public void show() {
    }

    @Deprecated
    public void pause() {
    }

    @Deprecated
    public void resume() {
    }

    @Deprecated
    public void hide() {
    }
}
