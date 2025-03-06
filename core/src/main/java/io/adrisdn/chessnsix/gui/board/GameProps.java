package io.adrisdn.chessnsix.gui.board;

import io.adrisdn.chessnsix.chess.engine.board.Board;
import io.adrisdn.chessnsix.chess.engine.board.BoardUtils;
import io.adrisdn.chessnsix.gui.managers.GuiUtils;
import io.adrisdn.chessnsix.gui.screens.GameScreen;

/**
 * Provides enumerations and constants that define the various states and
 * properties of a chess game.
 */
public final class GameProps {

	private GameProps() {
		throw new IllegalStateException("Game Props should not be initialised!");
	}

	/**
	 * Defines the possible states of a game.
	 */
	public enum GameEnd {
		/**
		 * The game has finished
		 */
		ENDED {
			@Override
			public boolean isGameEnded() {
				return true;
			}
		},
		/**
		 * The game is happening
		 */
		ONGOING {
			@Override
			public boolean isGameEnded() {
				return false;
			}
		};

		/**
		 * Returns whether the game has ended or not.
		 *
		 * @return true if it has ended, false otherwise.
		 */
		public abstract boolean isGameEnded();
	}

	/**
	 * his enum defines the states of the Artificial Intelligence (AI) in the game.
	 */
	public enum ArtificialIntelligenceWorking {
		/**
		 * The AI is currently thinking and processing moves.
		 */
		WORKING {
			@Override
			public boolean isArtificialIntelligenceWorking() {
				return true;
			}
		},
		/**
		 * The AI is idle and not thinking.
		 */
		RESTING {
			@Override
			public boolean isArtificialIntelligenceWorking() {
				return false;
			}
		};

		/**
		 * Returns whether the AI is currently working.
		 *
		 * @return true if it's working, false otherwise.
		 */
		public abstract boolean isArtificialIntelligenceWorking();
	}

	/**
	 * Defines the possible highlight states for a move on the chessboard.
	 */
	public enum HighlightMove {
		/**
		 * Indicates that the move should be highlighted.
		 */
		HIGHLIGHT_MOVE {
			@Override
			public boolean isHighlightMove() {
				return true;
			}
		},
		/**
		 * Indicates that the move should not be highlighted.
		 */
		NO_HIGHLIGHT_MOVE {
			@Override
			public boolean isHighlightMove() {
				return false;
			}
		};

		/**
		 * Returns whether the move should be highlighted.
		 *
		 * @return true if it should be highlighted, false otherwise.
		 */
		public abstract boolean isHighlightMove();

		/**
		 * A utility method that returns the appropriate state based on a boolean value.
		 *
		 * @param checked the boolean value
		 * @return {@link HighlightMove#HIGHLIGHT_MOVE} if true, else
		 *         {@link HighlightMove#NO_HIGHLIGHT_MOVE}.
		 */
		public static HighlightMove getHighlightMoveState(final boolean checked) {
			return checked ? HIGHLIGHT_MOVE : NO_HIGHLIGHT_MOVE;
		}
	}

	/**
	 * Defines the possible highlight states for a previous move on the chessboard.
	 */
	public enum HighlightPreviousMove {
		/**
		 * Indicates that the previous move should be highlighted.
		 */
		HIGHLIGHT_PREVIOUS_MOVE {
			@Override
			public boolean isHighlightPreviousMove() {
				return true;
			}
		},
		/**
		 * Indicates that the previous move should not be highlighted.
		 */
		NO_HIGHLIGHT_PREVIOUS_MOVE {
			@Override
			public boolean isHighlightPreviousMove() {
				return false;
			}
		};

		/**
		 * Returns whether the previous move should be highlighted.
		 *
		 * @return true if it should be highlighted, false otherwise.
		 */
		public abstract boolean isHighlightPreviousMove();

		/**
		 * A utility method that returns the appropriate state based on a boolean value.
		 *
		 * @param checked the boolean value
		 * @return {@link HighlightPreviousMove#HIGHLIGHT_PREVIOUS_MOVE} if true, else
		 *         {@link HighlightPreviousMove#NO_HIGHLIGHT_PREVIOUS_MOVE}.
		 */
		public static HighlightPreviousMove getHighlightPreviousMoveState(final boolean checked) {
			return checked ? HIGHLIGHT_PREVIOUS_MOVE : NO_HIGHLIGHT_PREVIOUS_MOVE;
		}
	}

	/**
	 * Defines the possible player types.
	 */
	public enum PlayerType {
		/**
		 * Represents a human player
		 */
		HUMAN,
		/**
		 * Represents an AI-controlled player.
		 */
		COMPUTER;

		/**
		 * A utility method that returns the appropriate player type based on a boolean
		 * value.
		 *
		 * @param checked the boolean value
		 * @return @return {@link PlayerType#COMPUTER} if true, else
		 *         {@link PlayerType#HUMAN}.
		 */
		public static PlayerType getPlayerType(final boolean checked) {
			return checked ? COMPUTER : HUMAN;
		}
	}

	/**
	 * Defines the two possible directions for the chessboard (normal and flipped).
	 */
	protected enum BoardDirection {
		/**
		 * Represents the board as normally oriented (with white at the bottom).
		 */
		NORMAL_BOARD {
			@Override
			public void drawBoard(final GameScreen gameScreen, final io.adrisdn.chessnsix.gui.board.GameBoard gameBoard,
					final Board chessBoard,
					final io.adrisdn.chessnsix.gui.board.GameBoard.DisplayOnlyBoard displayOnlyBoard) {
				gameBoard.clearChildren();
				displayOnlyBoard.clearChildren();
				for (int i = 0; i < BoardUtils.NUM_TILES; i += 1) {
					if (i % 8 == 0) {
						gameBoard.row();
						displayOnlyBoard.row();
					}
					gameBoard.add(new io.adrisdn.chessnsix.gui.board.TileActor(gameScreen,
							gameBoard.textureRegion(chessBoard, i), i)).size(GuiUtils.TILE_SIZE);
					final io.adrisdn.chessnsix.gui.board.TileActor.DisplayOnlyTile tile = new io.adrisdn.chessnsix.gui.board.TileActor.DisplayOnlyTile(
							i);
					tile.repaint(gameBoard, chessBoard, gameScreen.getDisplayOnlyBoard());
					displayOnlyBoard.add(tile).size(GuiUtils.TILE_SIZE);
				}
				gameBoard.validate();
				displayOnlyBoard.validate();
			}

			@Override
			public BoardDirection opposite() {
				return FLIP_BOARD;
			}

			@Override
			public boolean flipped() {
				return false;
			}
		},
		/**
		 * Represents the board flipped (with black at the bottom).
		 */
		FLIP_BOARD {
			@Override
			public void drawBoard(final GameScreen gameScreen, final io.adrisdn.chessnsix.gui.board.GameBoard gameBoard,
					final Board chessBoard,
					final io.adrisdn.chessnsix.gui.board.GameBoard.DisplayOnlyBoard displayOnlyBoard) {
				gameBoard.clearChildren();
				displayOnlyBoard.clearChildren();
				for (int i = BoardUtils.NUM_TILES - 1; i >= 0; i -= 1) {
					gameBoard.add(new io.adrisdn.chessnsix.gui.board.TileActor(gameScreen,
							gameBoard.textureRegion(chessBoard, i), i)).size(GuiUtils.TILE_SIZE);
					final io.adrisdn.chessnsix.gui.board.TileActor.DisplayOnlyTile tile = new TileActor.DisplayOnlyTile(
							i);
					tile.repaint(gameBoard, chessBoard, gameScreen.getDisplayOnlyBoard());
					displayOnlyBoard.add(tile).size(GuiUtils.TILE_SIZE);
					if (i % 8 == 0) {
						gameBoard.row();
						displayOnlyBoard.row();
					}
				}
				gameBoard.validate();
				displayOnlyBoard.validate();
			}

			@Override
			public BoardDirection opposite() {
				return NORMAL_BOARD;
			}

			@Override
			public boolean flipped() {
				return true;
			}
		};

		/**
		 * Returns the opposite board direction
		 * @return the opposite board direction
		 */
		public abstract BoardDirection opposite();

		/**
		 * Returns a boolean indicating whether the board is flipped.
		 * @return true if flipped, false otherwise.
		 */
		public abstract boolean flipped();

		/**
		 * Draws the board according to the current direction.
		 * @param gameScreen screen to display the board
		 * @param gameBoard gameboard where the game is being played
		 * @param chessBoard internal board with all the game data and logic
		 * @param displayOnlyBoard board to paint
		 */
		public abstract void drawBoard(final GameScreen gameScreen,
				final io.adrisdn.chessnsix.gui.board.GameBoard gameBoard, final Board chessBoard,
				final GameBoard.DisplayOnlyBoard displayOnlyBoard);
	}
}
