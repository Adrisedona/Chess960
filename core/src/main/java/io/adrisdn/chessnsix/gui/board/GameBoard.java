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

	/**
	 * Initializes the GameBoard with the given GameScreen.
	 *
	 * @param gameScreen the screen where this gameboard belongs
	 */
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
				if (!isArtificialIntelligenceWorking()) {
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

	/**
	 * Updates the humanPiece field with the piece controlled by the human player.
	 *
	 * @param humanPiece piece to update to.
	 */
	public void updateHumanPiece(final Piece humanPiece) {
		this.humanPiece = humanPiece;
	}

	/**
	 * Updates the humanMove field with the last move made by the human player.
	 *
	 * @param humanMove move to update to.
	 */
	public void updateHumanMove(final Move humanMove) {
		this.humanMove = humanMove;
	}

	/**
	 * Updates the aiMove field with the last move made by the AI.
	 *
	 * @param aiMove move to update to.
	 */
	public void updateAiMove(final Move aiMove) {
		this.aiMove = aiMove;
	}

	/**
	 * Updates the state of the AI
	 *
	 * @param AIThinking state to update to.
	 */
	public void updateArtificialIntelligenceWorking(final GameProps.ArtificialIntelligenceWorking AIThinking) {
		this.artificialIntelligenceWorking = AIThinking;
	}

	/**
	 * Updates the game end state.
	 *
	 * @param gameEnd state to update to.
	 */
	public void updateGameEnd(final GameProps.GameEnd gameEnd) {
		if (gameEnd == GameProps.GameEnd.ENDED) {
			this.gameScreen.getChessGame().getConnectionDatabase().insertGameAsync(
					gameScreen.getMoveHistory().getMoveLog(), gameScreen.getChessBoard().currentPlayer(),
					gameScreen.getChessBoard());

		}
		this.gameEnd = gameEnd;
	}

	/**
	 * Updates whether moves should be highlighted.
	 *
	 * @param highlightMove state to update to.
	 */
	public void updateHighlightMove(final GameProps.HighlightMove highlightMove) {
		this.highlightMove = highlightMove;
	}

	/**
	 * Updates whether previous moves should be highlighted.
	 *
	 * @param highlightPreviousMove state to update to.
	 */
	public void updateHighlightPreviousMove(final GameProps.HighlightPreviousMove highlightPreviousMove) {
		this.highlightPreviousMove = highlightPreviousMove;
	}

	/**
	 * Updates the board direction.
	 */
	public void updateBoardDirection() {
		this.boardDirection = this.boardDirection.opposite();
	}

	/**
	 * Updates the player type for the white player.
	 *
	 * @param playerType type to update to.
	 */
	public void updateWhitePlayerType(final GameProps.PlayerType playerType) {
		this.whitePlayerType = playerType;
	}

	/**
	 * Updates the player type for the black player.
	 *
	 * @param playerType type to update to.
	 */
	public void updateBlackPlayerType(final GameProps.PlayerType playerType) {
		this.blackPlayerType = playerType;
	}

	/**
	 * Returns the piece currently controlled by the human player.
	 *
	 * @return the piece currently controlled by the human player.
	 */
	public Piece getHumanPiece() {
		return this.humanPiece;
	}

	/**
	 * Returns the last move made by the human player.
	 *
	 * @return the last move made by the human player.
	 */
	public Move getHumanMove() {
		return this.humanMove;
	}

	/**
	 * Returns the last move made by the AI player.
	 *
	 * @return the last move made by the AI player.
	 */
	public Move getAiMove() {
		return this.aiMove;
	}

	/**
	 * Triggers the gameSetupPropertyChangeSupport to notify listeners of any
	 * changes in the game setup.
	 */
	public void fireGameSetupPropertyChangeSupport() {
		this.gameSetupPropertyChangeSupport.firePropertyChange(null, null, null);
	}

	/**
	 * Returns whether the AI is currently working.
	 *
	 * @return true if it's working, false otherwise.
	 */
	public boolean isArtificialIntelligenceWorking() {
		return this.artificialIntelligenceWorking.isArtificialIntelligenceWorking();
	}

	/**
	 * Returns whether the game has ended
	 *
	 * @return true if it has ended, false otherwise.
	 */
	public boolean isGameEnd() {
		return this.gameEnd.isGameEnded();
	}

	/**
	 * Returns whether the moves should be highlighted.
	 *
	 * @return true if moves should be highlighted, false otheriwse.
	 */
	public boolean isHighlightMove() {
		return this.highlightMove.isHighlightMove();
	}

	/**
	 * Returns whether the previous moves should be highlighted.
	 *
	 * @return true if previous moves should be highlighted, false otheriwse.
	 */
	public boolean isHighlightPreviousMove() {
		return this.highlightPreviousMove.isHighlightPreviousMove();
	}

	/**
	 * Returns the ArtificialIntelligence object that handles AI logic.
	 *
	 * @return the ArtificialIntelligence object that handles AI logic.
	 */
	public ArtificialIntelligence getArtificialIntelligence() {
		return this.artificialIntelligence;
	}

	/**
	 * Determines whether a given player is controlled by AI based on the player's
	 * league and type (white or black).
	 *
	 * @param player playet to determine if is human or AI.
	 * @return true if it's an AI, false if it's a player.
	 */
	public boolean isAIPlayer(final Player player) {
		return player.getLeague() == League.WHITE ? this.whitePlayerType == GameProps.PlayerType.COMPUTER
				: this.blackPlayerType == GameProps.PlayerType.COMPUTER;
	}

	/**
	 * Draws the game board according to the current game state and direction.
	 *
	 * @param gameScreen       game where to paint the board
	 * @param chessBoard       board with all the information about the game
	 * @param displayOnlyBoard board to paint
	 */
	public void drawBoard(final GameScreen gameScreen, final Board chessBoard,
			final GameBoard.DisplayOnlyBoard displayOnlyBoard) {
		this.boardDirection.drawBoard(gameScreen, this, chessBoard, displayOnlyBoard);
	}

	/**
	 * Displays a message if the current player has run out of time.
	 *
	 * @param chessBoard board with all the information about the game
	 * @param stage      active stage to put the dialog in.
	 */
	public void displayTimeOutMessage(final Board chessBoard, final Stage stage) {
		if (chessBoard.currentPlayer().isTimeOut()) {
			final Label label = new Label(chessBoard.currentPlayer() + LanguageManager.get("timed_out_msg"),
					GuiUtils.UI_SKIN);
			label.setColor(Color.BLACK);
			new Dialog(LanguageManager.get("timed_out_title"), GuiUtils.UI_SKIN).text(label)
					.button(LanguageManager.get("ok")).show(stage);
			this.updateGameEnd(GameProps.GameEnd.ENDED);
		}
	}

	/**
	 * Displays a message when the game ends, indicating whether it was a checkmate
	 * or stalemate.
	 *
	 * @param chessBoard board with all the information about the game
	 * @param stage      active stage to put the dialog in.
	 */
	public void displayEndGameMessage(final Board chessBoard, final Stage stage) {
		final String state = chessBoard.currentPlayer().isInCheckmate() ? LanguageManager.get("checkmate")
				: chessBoard.currentPlayer().isInStalemate() ? LanguageManager.get("stalemate") : null;
		if (state == null) {
			return;
		}
		final Label label = new Label(
				chessBoard.currentPlayer() + LanguageManager.get("player_in") + state.toLowerCase() + "!",
				GuiUtils.UI_SKIN);
		label.setColor(Color.BLACK);
		new Dialog(state, GuiUtils.UI_SKIN).text(label).button(LanguageManager.get("ok")).show(stage);
		this.updateGameEnd(GameProps.GameEnd.ENDED);
	}

	/**
	 * Returns the texture region for a tile based on whether it is occupied by a
	 * piece or not.
	 *
	 * @param board  board with the game information.
	 * @param tileID tile to paint.
	 * @return texture to paint.
	 */
	protected TextureRegion textureRegion(final Board board, final int tileID) {
		return board.getTile(tileID).isTileOccupied()
				? GuiUtils.GET_PIECE_TEXTURE_REGION(board.getTile(tileID).getPiece())
				: GuiUtils.TRANSPARENT_TEXTURE_REGION;
	}

	/**
	 * Displais the chessboard in a read-only state, without game logic.
	 */
	public static final class DisplayOnlyBoard extends Table {

		private io.adrisdn.chessnsix.gui.managers.GuiUtils.TILE_COLOR tileColor;

		/**
		 * Initializes a display only board.
		 */
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

		/**
		 * Returns the current tile color scheme (light or dark).
		 *
		 * @param TILE_COLOR color palette.
		 * @param i          index of the tile.
		 * @return color of the tile.
		 */
		private static Color getTileColor(final GuiUtils.TILE_COLOR TILE_COLOR, final int i) {
			if (BoardUtils.FIRST_ROW.get(i) || BoardUtils.THIRD_ROW.get(i) || BoardUtils.FIFTH_ROW.get(i)
					|| BoardUtils.SEVENTH_ROW.get(i)) {
				return i % 2 == 0 ? TILE_COLOR.LIGHT_TILE() : TILE_COLOR.DARK_TILE();
			}
			return i % 2 != 0 ? TILE_COLOR.LIGHT_TILE() : TILE_COLOR.DARK_TILE();
		}

		/**
		 * : Determines the color of a tile based on its row and position.
		 *
		 * @param TILE_COLOR color palette.
		 * @param i          index of the tile.
		 * @return color of the tile.
		 */
		private static Color getHighlightTileColor(final GuiUtils.TILE_COLOR TILE_COLOR, final int i) {
			if (BoardUtils.FIRST_ROW.get(i) || BoardUtils.THIRD_ROW.get(i) || BoardUtils.FIFTH_ROW.get(i)
					|| BoardUtils.SEVENTH_ROW.get(i)) {
				return i % 2 == 0 ? TILE_COLOR.HIGHLIGHT_LEGAL_MOVE_LIGHT_TILE()
						: TILE_COLOR.HIGHLIGHT_LEGAL_MOVE_DARK_TILE();
			}
			return i % 2 != 0 ? TILE_COLOR.HIGHLIGHT_LEGAL_MOVE_LIGHT_TILE()
					: TILE_COLOR.HIGHLIGHT_LEGAL_MOVE_DARK_TILE();
		}

		/**
		 * Returns the current tile color scheme.
		 *
		 * @return the current color palette.
		 */
		public io.adrisdn.chessnsix.gui.managers.GuiUtils.TILE_COLOR getTileColor() {
			return this.tileColor;
		}

		/**
		 * Sets the tile color scheme for the board.
		 *
		 * @param tile_color the tile color scheme for the board.
		 */
		public void setTileColor(final GuiUtils.TILE_COLOR tile_color) {
			this.tileColor = tile_color;
		}

		/**
		 * Highlights the legal moves available for the human player's piece.
		 *
		 * @param gameBoard  gameboard where the game is being played.
		 * @param chessBoard internal board for handling logic.
		 */
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
