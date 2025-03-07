package io.adrisdn.chessnsix.gui.board;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.google.common.collect.ImmutableList;

import io.adrisdn.chessnsix.chess.engine.board.Board;
import io.adrisdn.chessnsix.chess.engine.board.BoardUtils;
import io.adrisdn.chessnsix.chess.engine.board.Move;
import io.adrisdn.chessnsix.chess.engine.board.MoveTransition;
import io.adrisdn.chessnsix.chess.engine.pieces.Piece;
import io.adrisdn.chessnsix.chess.engine.player.Player;
import io.adrisdn.chessnsix.gui.board.MoveDisambiguationInterface.DialogResultListener;
import io.adrisdn.chessnsix.gui.managers.AudioManager;
import io.adrisdn.chessnsix.gui.managers.GuiUtils;
import io.adrisdn.chessnsix.gui.screens.GameScreen;

/**
 * Represents a clickable tile on the chessboard
 */
public final class TileActor extends Image {

	/**
	 * Initializes the TileActor as a clickable tile. It listens for user clicks and
	 * processes tile interactions based on the state of the game.
	 *
	 * @param gameScreen The current GameScreen instance used to interact with the
	 *                   chess game.
	 * @param region     The texture region for the tile (e.g., visual
	 *                   representation of a chessboard square).
	 * @param tileID     The unique identifier for the tile on the chessboard.
	 */
	protected TileActor(final GameScreen gameScreen, final TextureRegion region, final int tileID) {
		super(region);
		this.setVisible(true);
		this.addListener(new ClickListener() {
			@Override
			public void clicked(final InputEvent event, final float x, final float y) {
				try {
					super.clicked(event, x, y);
					if (gameScreen.getGameBoard().isGameEnd()
							|| gameScreen.getGameBoard().isArtificialIntelligenceWorking()) {
						return;
					}

					if (gameScreen.getGameBoard().getHumanPiece() == null) {
						gameScreen.getGameBoard().drawBoard(gameScreen, gameScreen.getChessBoard(),
								gameScreen.getDisplayOnlyBoard());
						if (gameScreen.getChessBoard().getTile(tileID).getPiece().getLeague() == gameScreen
								.getChessBoard().currentPlayer().getLeague()) {
							gameScreen.getGameBoard()
									.updateHumanPiece(gameScreen.getChessBoard().getTile(tileID).getPiece());
							if (gameScreen.getGameBoard().isHighlightMove()) {
								gameScreen.getDisplayOnlyBoard().highlightLegalMove(gameScreen.getGameBoard(),
										gameScreen.getChessBoard());
							}
						}

					} else {
						if (gameScreen.getGameBoard().getHumanPiece().getLeague() == gameScreen.getChessBoard()
								.currentPlayer().getLeague()) {
							final Move move;
							final ImmutableList<Move> moves = Move.MoveFactory.createMove(gameScreen.getChessBoard(),
									gameScreen.getGameBoard().getHumanPiece(), tileID);
							if (moves.size() == 1) {
								move = moves.get(0);
								processMove(gameScreen, tileID, move);
							} else {
								MoveDisambiguationInterface disambiguationInterface = new MoveDisambiguationInterface(
										moves, gameScreen);
								disambiguationInterface.setListener(new DialogResultListener() {

									@Override
									public void onDialogResult(Move result) {
										processMove(gameScreen, tileID, result);
									}

								});
								disambiguationInterface.showDisambiguateMoveDialog();
							}

						} else {
							gameScreen.getGameBoard().drawBoard(gameScreen, gameScreen.getChessBoard(),
									gameScreen.getDisplayOnlyBoard());
							gameScreen.getGameBoard().updateHumanPiece(null);
						}
					}
				} catch (final NullPointerException ignored) {
				}
			}

			/**
			 * processes the move made by the player, updates the game state, and handles
			 * any special moves (like castling or promotion).
			 *
			 * @param gameScreen The current GameScreen instance, used to update the board
			 *                   and game state.
			 * @param tileID     The tile that the player clicked.
			 * @param move       The move that the player made.
			 */
			private void processMove(final GameScreen gameScreen, final int tileID, final Move move) {
				Player opponent = gameScreen.getChessBoard().currentPlayer().getOpponent();
				final MoveTransition transition = gameScreen.getChessBoard().currentPlayer().makeMove(move);
				if (transition.getMoveStatus().isDone()) {
					gameScreen.getGameBoard().updateHumanPiece(null);
					gameScreen.updateChessBoard(transition.getLatestBoard());
					gameScreen.getGameBoard().updateAiMove(null);
					gameScreen.getGameBoard().updateHumanMove(move);
					if (move.isPromotionMove()) {
						// display pawn promotion interface
						new PawnPromotionInterface().startLibGDXPromotion(gameScreen, (Move.PawnPromotion) move);
					} else {
						gameScreen.getGameBoard().drawBoard(gameScreen, gameScreen.getChessBoard(),
								gameScreen.getDisplayOnlyBoard());
						gameScreen.getMoveHistory().getMoveLog().addMove(move);
						gameScreen.getMoveHistory().updateMoveHistory();
						if (gameScreen.getGameBoard().isAIPlayer(gameScreen.getChessBoard().currentPlayer())) {
							gameScreen.getGameBoard().fireGameSetupPropertyChangeSupport();
						} else {
							gameScreen.getGameBoard().displayEndGameMessage(gameScreen.getChessBoard(),
									gameScreen.getStage());
						}
						if (move.isCastlingMove()) {
							gameScreen.getChessGame().getCastleSound().play(AudioManager.getSoundVolume());
						} else if (move.isAttack()) {
							gameScreen.getChessGame().getCaptureSound().play(AudioManager.getSoundVolume());
						} else {
							gameScreen.getChessGame().getMoveSound().play(AudioManager.getSoundVolume());
						}
					}
					if (opponent.isInCheckmate()) {
						gameScreen.getChessGame().getCheckMateSound().play(AudioManager.getSoundVolume());
					} else if (opponent.isInCheck()) {
						gameScreen.getChessGame().getCheckSound().play(AudioManager.getSoundVolume());
					}

				} else {
					gameScreen.getGameBoard().updateHumanPiece(
							getPiece(gameScreen.getChessBoard(), gameScreen.getGameBoard().getHumanPiece(), tileID));
					gameScreen.getGameBoard().drawBoard(gameScreen, gameScreen.getChessBoard(),
							gameScreen.getDisplayOnlyBoard());
					if (GuiUtils.IS_SMARTPHONE && AudioManager.isVibration()) {
						Gdx.input.vibrate(500);
					}
					if (getPiece(gameScreen.getChessBoard(), gameScreen.getGameBoard().getHumanPiece(), tileID) != null
							&& gameScreen.getGameBoard().isHighlightMove()) {
						gameScreen.getDisplayOnlyBoard().highlightLegalMove(gameScreen.getGameBoard(),
								gameScreen.getChessBoard());
					}
				}
			}
		});
	}

	/**
	 * Checks whether a piece at a specified tile belongs to the current player and
	 * matches the selected piece.
	 *
	 * @param chessBoard The current Board state.
	 * @param humanPiece The piece selected by the player.
	 * @param tileID     The tile ID to check.
	 * @return The Piece on the specified tile if it belongs to the current player
	 *         and matches the selected piece. Returns null otherwise.
	 */
	private Piece getPiece(final Board chessBoard, final Piece humanPiece, final int tileID) {
		final Piece piece = chessBoard.getTile(tileID).getPiece();
		if (piece == null) {
			return null;
		}
		if (piece.getPiecePosition() == tileID && humanPiece.getLeague() == piece.getLeague()) {
			return piece;
		}
		return null;
	}

	/**
	 * Represents a tile on the board that is only used for displaying purposes,
	 * without interaction.
	 */
	protected static final class DisplayOnlyTile extends Image {

		private final int tileID;

		/**
		 * This constructor initializes a tile for display purposes with a specific
		 * texture and visibility.
		 *
		 * @param tileID The unique identifier for the tile.
		 */
		protected DisplayOnlyTile(final int tileID) {
			super(GuiUtils.GET_TILE_TEXTURE_REGION("white"));
			this.tileID = tileID;
			this.setVisible(true);
		}

		/**
		 * Determines the color of the tile based on its position on the board.
		 *
		 * @param TILE_COLOR the color palette.
		 * @return The Color object representing the tile's color.
		 */
		private Color getTileColor(final GuiUtils.TILE_COLOR TILE_COLOR) {
			if (BoardUtils.FIRST_ROW.get(this.tileID) || BoardUtils.THIRD_ROW.get(this.tileID)
					|| BoardUtils.FIFTH_ROW.get(this.tileID) || BoardUtils.SEVENTH_ROW.get(this.tileID)) {
				return this.tileID % 2 == 0 ? TILE_COLOR.LIGHT_TILE() : TILE_COLOR.DARK_TILE();
			}
			return this.tileID % 2 != 0 ? TILE_COLOR.LIGHT_TILE() : TILE_COLOR.DARK_TILE();
		}

		/**
		 * This method determines the tile color when highlighting the human player's
		 * move.
		 *
		 * @param gameBoard        The GameBoard instance.
		 * @param displayOnlyBoard he DisplayOnlyBoard instance.
		 * @return The Color representing the highlight color for the human player's
		 *         move.
		 */
		private Color getHumanMoveColor(final GameBoard gameBoard, final GameBoard.DisplayOnlyBoard displayOnlyBoard) {
			if (this.tileID == gameBoard.getHumanMove().getCurrentCoordinate()) {
				return GuiUtils.HUMAN_PREVIOUS_TILE;
			} else if (this.tileID == gameBoard.getHumanMove().getDestinationCoordinate()) {
				return GuiUtils.HUMAN_CURRENT_TILE;
			}
			return this.getTileColor(displayOnlyBoard.getTileColor());
		}

		/**
		 * Determines the tile color when highlighting the AI player's move.
		 *
		 * @param gameBoard        The GameBoard instance.
		 * @param displayOnlyBoard he DisplayOnlyBoard instance.
		 * @returnThe Color representing the highlight color for the AI player's move.
		 */
		private Color getAIMoveColor(final GameBoard gameBoard, final GameBoard.DisplayOnlyBoard displayOnlyBoard) {
			if (this.tileID == gameBoard.getAiMove().getCurrentCoordinate()) {
				return GuiUtils.AI_PREVIOUS_TILE;
			} else if (this.tileID == gameBoard.getAiMove().getDestinationCoordinate()) {
				return GuiUtils.AI_CURRENT_TILE;
			}
			return this.getTileColor(displayOnlyBoard.getTileColor());
		}

		/**
		 * Updates the visual appearance of the tile based on the current game state
		 *
		 * @param gameBoard        The GameBoard instance.
		 * @param chessBoard       The Board instance representing the current state of
		 *                         the chess game.
		 * @param displayOnlyBoard The DisplayOnlyBoard instance used for rendering
		 *                         purposes.
		 */
		public void repaint(final GameBoard gameBoard, final Board chessBoard,
				final GameBoard.DisplayOnlyBoard displayOnlyBoard) {
			if (chessBoard.currentPlayer().isInCheck()
					&& chessBoard.currentPlayer().getPlayerKing().getPiecePosition() == this.tileID) {
				this.setColor(Color.RED);
			} else if (gameBoard.getHumanMove() != null && gameBoard.isHighlightPreviousMove()) {
				this.setColor(this.getHumanMoveColor(gameBoard, displayOnlyBoard));
			} else if (gameBoard.getAiMove() != null && gameBoard.isHighlightPreviousMove()) {
				this.setColor(this.getAIMoveColor(gameBoard, displayOnlyBoard));
			} else {
				this.setColor(this.getTileColor(displayOnlyBoard.getTileColor()));
			}
		}
	}
}
