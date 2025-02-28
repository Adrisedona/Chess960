package io.adrisdn.chessnsix.gui.board;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.google.common.collect.ImmutableList;

import io.adrisdn.chessnsix.chess.engine.League;
import io.adrisdn.chessnsix.chess.engine.board.Board;
import io.adrisdn.chessnsix.chess.engine.board.BoardUtils;
import io.adrisdn.chessnsix.chess.engine.board.Move;
import io.adrisdn.chessnsix.chess.engine.pieces.Piece;
import io.adrisdn.chessnsix.chess.engine.player.Player;
import io.adrisdn.chessnsix.gui.ArtificialIntelligence;
import io.adrisdn.chessnsix.gui.managers.GuiUtils;
import io.adrisdn.chessnsix.gui.managers.LanguageManager;
import io.adrisdn.chessnsix.gui.screens.GameScreen;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public final class GameBoard extends Table {

	private final PropertyChangeSupport gameSetupPropertyChangeSupport;
	private final ArtificialIntelligence artificialIntelligence;
	// object
	private Piece humanPiece;
	private Move humanMove, aiMove;
	// enum
	private GameProps.GameEnd gameEnd;
	private GameProps.HighlightPreviousMove highlightPreviousMove;
	private GameProps.HighlightMove highlightMove;
	private GameProps.ArtificialIntelligenceWorking artificialIntelligenceWorking;
	private GameProps.PlayerType whitePlayerType, blackPlayerType;
	private GameProps.BoardDirection boardDirection;
	private GameScreen gameScreen;

	public GameBoard(final GameScreen gameScreen) {
		// mutable
		this.humanPiece = null;
		this.humanMove = null;

		this.gameEnd = GameProps.GameEnd.ONGOING;
		this.highlightMove = GameProps.HighlightMove.HIGHLIGHT_MOVE;
		this.highlightPreviousMove = GameProps.HighlightPreviousMove.HIGHLIGHT_PREVIOUS_MOVE;
		this.artificialIntelligenceWorking = GameProps.ArtificialIntelligenceWorking.RESTING;

		this.whitePlayerType = GameProps.PlayerType.HUMAN;
		this.blackPlayerType = GameProps.PlayerType.HUMAN;

		this.gameScreen = gameScreen;

		// immutable
		this.artificialIntelligence = new ArtificialIntelligence();
		final PropertyChangeListener gameSetupPropertyChangeListener = propertyChangeEvent -> {
			if (isAIPlayer(gameScreen.getChessBoard().currentPlayer())
					&& !gameScreen.getChessBoard().currentPlayer().isInCheckmate()
					&& !gameScreen.getChessBoard().currentPlayer().isInStalemate()) {
				if (!getArtificialIntelligenceWorking()) {
					updateArtificialIntelligenceWorking(GameProps.ArtificialIntelligenceWorking.WORKING);
					this.artificialIntelligence.startAI(gameScreen);
				}
			}
			displayEndGameMessage(gameScreen.getChessBoard(), gameScreen.getStage());
		};
		this.gameSetupPropertyChangeSupport = new PropertyChangeSupport(gameSetupPropertyChangeListener);
		this.gameSetupPropertyChangeSupport.addPropertyChangeListener(gameSetupPropertyChangeListener);

		this.boardDirection = GameProps.BoardDirection.NORMAL_BOARD;
		this.setFillParent(true);
		for (int i = 0; i < BoardUtils.NUM_TILES; i += 1) {
			if (i % 8 == 0) {
				this.row();
			}
			this.add(new io.adrisdn.chessnsix.gui.board.TileActor(gameScreen,
					this.textureRegion(gameScreen.getChessBoard(), i), i)).size(GuiUtils.TILE_SIZE);
		}
		this.validate();
	}

	// object updater
	public void updateHumanPiece(final Piece humanPiece) {
		this.humanPiece = humanPiece;
	}

	public void updateHumanMove(final Move humanMove) {
		this.humanMove = humanMove;
	}

	public void updateAiMove(final Move aiMove) {
		this.aiMove = aiMove;
	}

	// enum updater
	public void updateArtificialIntelligenceWorking(final GameProps.ArtificialIntelligenceWorking AIThinking) {
		this.artificialIntelligenceWorking = AIThinking;
	}

	public void updateGameEnd(final GameProps.GameEnd gameEnd) {
		if (gameEnd == GameProps.GameEnd.ENDED) {
			this.gameScreen.getChessGame().getConnectionDatabase().insertGameAsync(
					gameScreen.getMoveHistory().getMoveLog(), gameScreen.getChessBoard().currentPlayer(), gameScreen.getChessBoard());

		}
		this.gameEnd = gameEnd;
	}

	public void updateHighlightMove(final GameProps.HighlightMove highlightMove) {
		this.highlightMove = highlightMove;
	}

	public void updateHighlightPreviousMove(final GameProps.HighlightPreviousMove highlightPreviousMove) {
		this.highlightPreviousMove = highlightPreviousMove;
	}

	public void updateBoardDirection() {
		this.boardDirection = this.boardDirection.opposite();
	}

	public void updateWhitePlayerType(final GameProps.PlayerType playerType) {
		this.whitePlayerType = playerType;
	}

	public void updateBlackPlayerType(final GameProps.PlayerType playerType) {
		this.blackPlayerType = playerType;
	}

	// getter
	public Piece getHumanPiece() {
		return this.humanPiece;
	}

	public Move getHumanMove() {
		return this.humanMove;
	}

	public Move getAiMove() {
		return this.aiMove;
	}

	public void fireGameSetupPropertyChangeSupport() {
		this.gameSetupPropertyChangeSupport.firePropertyChange(null, null, null);
	}

	public boolean getArtificialIntelligenceWorking() {
		return this.artificialIntelligenceWorking.isArtificialIntelligenceWorking();
	}

	public boolean isGameEnd() {
		return this.gameEnd.isGameEnded();
	}

	public boolean isHighlightMove() {
		return this.highlightMove.isHighlightMove();
	}

	public boolean isHighlightPreviousMove() {
		return this.highlightPreviousMove.isHighlightPreviousMove();
	}

	public ArtificialIntelligence getArtificialIntelligence() {
		return this.artificialIntelligence;
	}

	public boolean isAIPlayer(final Player player) {
		return player.getLeague() == League.WHITE ? this.whitePlayerType == GameProps.PlayerType.COMPUTER
				: this.blackPlayerType == GameProps.PlayerType.COMPUTER;
	}

	public void drawBoard(final GameScreen gameScreen, final Board chessBoard,
			final GameBoard.DisplayOnlyBoard displayOnlyBoard) {
		this.boardDirection.drawBoard(gameScreen, this, chessBoard, displayOnlyBoard);
	}

	public void displayTimeOutMessage(final Board chessBoard, final Stage stage) {
		if (chessBoard.currentPlayer().isTimeOut()) {
			final Label label = new Label(chessBoard.currentPlayer() + LanguageManager.get("timed_out_msg"), GuiUtils.UI_SKIN);
			label.setColor(Color.BLACK);
			new Dialog(LanguageManager.get("timed_out_title"), GuiUtils.UI_SKIN).text(label).button(LanguageManager.get("ok")).show(stage);
			this.updateGameEnd(GameProps.GameEnd.ENDED);
		}
	}

	public void displayEndGameMessage(final Board chessBoard, final Stage stage) {
		final String state = chessBoard.currentPlayer().isInCheckmate() ? LanguageManager.get("checkmate")
				: chessBoard.currentPlayer().isInStalemate() ? LanguageManager.get("stalemate") : null;
		if (state == null) {
			return;
		}
		final Label label = new Label(chessBoard.currentPlayer() + LanguageManager.get("player_in") + state.toLowerCase() + "!",
				GuiUtils.UI_SKIN);
		label.setColor(Color.BLACK);
		new Dialog(state, GuiUtils.UI_SKIN).text(label).button(LanguageManager.get("ok")).show(stage);
		this.updateGameEnd(GameProps.GameEnd.ENDED);
	}

	protected TextureRegion textureRegion(final Board board, final int tileID) {
		return board.getTile(tileID).isTileOccupied()
				? GuiUtils.GET_PIECE_TEXTURE_REGION(board.getTile(tileID).getPiece())
				: GuiUtils.TRANSPARENT_TEXTURE_REGION;
	}

	public static final class DisplayOnlyBoard extends Table {

		private io.adrisdn.chessnsix.gui.managers.GuiUtils.TILE_COLOR tileColor;

		public DisplayOnlyBoard() {
			this.setFillParent(true);
			this.tileColor = GuiUtils.TILE_COLOR.CLASSIC;
			for (int i = 0; i < BoardUtils.NUM_TILES; i += 1) {
				if (i % 8 == 0) {
					this.row();
				}
				final io.adrisdn.chessnsix.gui.board.TileActor.DisplayOnlyTile displayOnlyTile = new TileActor.DisplayOnlyTile(
						i);
				displayOnlyTile.setColor(getTileColor(this.tileColor, i));
				this.add(displayOnlyTile).size(GuiUtils.TILE_SIZE);
			}
			this.validate();
		}

		private static Color getTileColor(final GuiUtils.TILE_COLOR TILE_COLOR, final int i) {
			if (BoardUtils.FIRST_ROW.get(i) || BoardUtils.THIRD_ROW.get(i) || BoardUtils.FIFTH_ROW.get(i)
					|| BoardUtils.SEVENTH_ROW.get(i)) {
				return i % 2 == 0 ? TILE_COLOR.LIGHT_TILE() : TILE_COLOR.DARK_TILE();
			}
			return i % 2 != 0 ? TILE_COLOR.LIGHT_TILE() : TILE_COLOR.DARK_TILE();
		}

		private static Color getHighlightTileColor(final GuiUtils.TILE_COLOR TILE_COLOR, final int i) {
			if (BoardUtils.FIRST_ROW.get(i) || BoardUtils.THIRD_ROW.get(i) || BoardUtils.FIFTH_ROW.get(i)
					|| BoardUtils.SEVENTH_ROW.get(i)) {
				return i % 2 == 0 ? TILE_COLOR.HIGHLIGHT_LEGAL_MOVE_LIGHT_TILE()
						: TILE_COLOR.HIGHLIGHT_LEGAL_MOVE_DARK_TILE();
			}
			return i % 2 != 0 ? TILE_COLOR.HIGHLIGHT_LEGAL_MOVE_LIGHT_TILE()
					: TILE_COLOR.HIGHLIGHT_LEGAL_MOVE_DARK_TILE();
		}

		public io.adrisdn.chessnsix.gui.managers.GuiUtils.TILE_COLOR getTileColor() {
			return this.tileColor;
		}

		public void setTileColor(final GuiUtils.TILE_COLOR tile_color) {
			this.tileColor = tile_color;
		}

		public void highlightLegalMove(final GameBoard gameBoard, final Board chessBoard) {
			final Piece piece = gameBoard.getHumanPiece();
			final ImmutableList<Move> moveList = piece != null
					&& piece.getLeague() == chessBoard.currentPlayer().getLeague()
							? ImmutableList.copyOf(piece.calculateLegalMoves(chessBoard))
							: ImmutableList.of();
			for (final Move move : moveList) {
				final int tileID = gameBoard.boardDirection.flipped() ? 63 - move.getDestinationCoordinate()
						: move.getDestinationCoordinate();
				if (move.isAttack()
						|| move.isPromotionMove() && ((Move.PawnPromotion) move).getDecoratedMove().isAttack()) {
					this.getChildren().get(tileID).setColor(new Color(204 / 255f, 0 / 255f, 0 / 255f, 1));
				} else {
					this.getChildren().get(tileID).setColor(getHighlightTileColor(getTileColor(), tileID));
				}
			}
		}
	}
}
