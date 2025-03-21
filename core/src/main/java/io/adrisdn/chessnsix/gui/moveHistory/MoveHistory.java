package io.adrisdn.chessnsix.gui.moveHistory;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.google.common.collect.ImmutableList;

import io.adrisdn.chessnsix.chess.engine.League;
import io.adrisdn.chessnsix.chess.engine.board.Move;
import io.adrisdn.chessnsix.chess.engine.board.MoveLog;
import io.adrisdn.chessnsix.chess.engine.pieces.Piece;
import io.adrisdn.chessnsix.gui.managers.GuiUtils;

/**
 * Is responsible for maintaining and displaying the history of moves made
 * during a chess game. It includes a scrollable list of moves and sections for
 * captured pieces.
 */
public final class MoveHistory extends Table {

	private static final int SIZE = GuiUtils.GAME_BOARD_SR_SIZE / 2;

	private final Table table;
	private final TakenPiece whiteTakenPiece, blackTakenPiece;
	private final MoveLog moveLog;
	private TakenPieceDirection takenPieceDirection;

	/**
	 * Initializes the move history panel, creating sections for captured pieces and
	 * a scrollable table for the move list.
	 */
	public MoveHistory() {
		this.setVisible(true);

		this.takenPieceDirection = TakenPieceDirection.NORMAL;

		this.moveLog = new MoveLog();

		this.whiteTakenPiece = new MoveHistory.TakenPiece(League.WHITE, GuiUtils.WHITE_CAPTURED);
		this.blackTakenPiece = new MoveHistory.TakenPiece(League.BLACK, GuiUtils.BLACK_CAPTURED);

		this.table = new Table(GuiUtils.UI_SKIN);
		this.table.align(Align.topLeft);
		this.add(this.whiteTakenPiece).size(SIZE, 75).row();
		final ScrollPane scrollPane = new ScrollPane(this.table);
		scrollPane.setScrollbarsVisible(true);
		this.add(scrollPane).size(SIZE, 450).row();
		this.add(this.blackTakenPiece).size(SIZE, 75);
	}

	/**
	 * Is used to control the layout of the captured piece sections.
	 */
	private enum TakenPieceDirection {
		/**
		 * Displays captured white pieces at the top, move history in the middle, and
		 * captured black pieces at the bottom.
		 */
		NORMAL {
			@Override
			void redo(final MoveHistory moveHistory) {
				moveHistory.clearChildren();
				moveHistory.add(moveHistory.whiteTakenPiece).size(MoveHistory.SIZE, 75).row();
				moveHistory.add(new ScrollPane(moveHistory.table)).size(MoveHistory.SIZE, 450).row();
				moveHistory.add(moveHistory.blackTakenPiece).size(MoveHistory.SIZE, 75);
			}

			@Override
			TakenPieceDirection getOpposite() {
				return FLIPPED;
			}
		},
		/**
		 * Reverses the layout by displaying captured black pieces at the top and white
		 * pieces at the bottom.
		 */
		FLIPPED {
			@Override
			void redo(final MoveHistory moveHistory) {
				moveHistory.clearChildren();
				moveHistory.add(moveHistory.blackTakenPiece).size(MoveHistory.SIZE, 75).row();
				moveHistory.add(new ScrollPane(moveHistory.table)).size(MoveHistory.SIZE, 450).row();
				moveHistory.add(moveHistory.whiteTakenPiece).size(MoveHistory.SIZE, 75);
			}

			@Override
			TakenPieceDirection getOpposite() {
				return NORMAL;
			}
		};

		/**
		 * Updates the UI structure based on the selected layout.
		 *
		 * @param moveHistory the history of moves.
		 */
		abstract void redo(final MoveHistory moveHistory);

		/**
		 * Returns the opposite layout configuration.
		 *
		 * @return the opposite layout configuration.
		 */
		abstract TakenPieceDirection getOpposite();

		/**
		 * Clears and updates the move history list with move records and updates the
		 * captured piece sections.
		 *
		 * @param moveHistory the history of moves.
		 */
		private void updateMoveHistory(final MoveHistory moveHistory) {
			moveHistory.table.clearChildren();
			int i = 0, j = 1;
			for (final Move move : moveHistory.moveLog.getMoves()) {
				final Table table = new Table(GuiUtils.UI_SKIN);
				table.add(++i + ") " + move.toString());
				if ((j % 2 != 0 && i % 2 != 0) || (j % 2 == 0 && i % 2 == 0)) {
					table.setBackground(GuiUtils.MOVE_HISTORY_1);
				} else {
					table.setBackground(GuiUtils.MOVE_HISTORY_2);
				}
				table.align(Align.left);
				moveHistory.table.add(table).size(MoveHistory.SIZE / 2f, 50);
				if (i % 2 == 0) {
					moveHistory.table.row();
					j++;
				}
			}
			moveHistory.whiteTakenPiece.updateTakenPiece(moveHistory.moveLog);
			moveHistory.blackTakenPiece.updateTakenPiece(moveHistory.moveLog);
		}
	}

	/**
	 * Flips the order of the captured piece sections and the move history panel.
	 */
	public void changeMoveHistoryDirection() {
		this.takenPieceDirection = this.takenPieceDirection.getOpposite();
		this.takenPieceDirection.redo(this);
	}

	/**
	 * Refreshes the move history panel based on the current game state.
	 */
	public void updateMoveHistory() {
		this.takenPieceDirection.updateMoveHistory(this);
	}

	/**
	 * Returns the MoveLog object containing the recorded game moves.
	 *
	 * @return the MoveLog object containing the recorded game moves.
	 */
	public MoveLog getMoveLog() {
		return this.moveLog;
	}

	/**
	 * Represents a section of captured pieces for either white or black.
	 */
	private static final class TakenPiece extends Table {

		private final League league;

		/**
		 * Initializes a TakenPiece section with the given league and background.
		 * @param league The league (white or black) this section corresponds to.
		 * @param ninePatchDrawable the background of the component.
		 */
		private TakenPiece(final League league, final NinePatchDrawable ninePatchDrawable) {
			super(GuiUtils.UI_SKIN);
			this.league = league;
			this.setVisible(true);
			this.align(Align.bottomLeft);
			this.setBackground(ninePatchDrawable);
		}

		/**
		 * Updates the list of captured pieces based on the MoveLog.
		 * @param moveLog the history of moves.
		 */
		private void updateTakenPiece(final MoveLog moveLog) {

			final HashMap<Piece, Integer> takenPieces = new HashMap<>();

			for (final Move move : moveLog.getMoves()) {
				if (move.isAttack()) {
					final Piece takenPiece = move.getAttackedPiece();
					if (takenPiece.getLeague() == this.league) {
						final Piece piece = this.searchSamePiece(takenPieces, takenPiece);
						if (piece == null) {
							takenPieces.put(takenPiece, 1);
						} else {
							final int quantity = takenPieces.get(piece) + 1;
							takenPieces.remove(piece);
							takenPieces.put(takenPiece, quantity);
						}
					}
				}
			}

			this.addTakenPiece(takenPieces);

			this.validate();
		}

		/**
		 * Searches for a captured piece of the same type within the stored taken pieces.
		 * @param takenPieces pieces already taken.
		 * @param takenPiece the new taken piece.
		 * @return the piece that equals the parameter
		 */
		private Piece searchSamePiece(final HashMap<Piece, Integer> takenPieces, final Piece takenPiece) {
			return takenPieces.keySet().stream().filter(piece -> takenPiece.toString().equals(piece.toString()))
					.findFirst().orElse(null);
		}

		/**
		 * Sorts captured pieces in ascending order of piece value.
		 * @param takenPiecesMap the captured pieces
		 * @return a list with the captured piecees in order
		 */
		private List<Piece> sortedPieces(final HashMap<Piece, Integer> takenPiecesMap) {
			return ImmutableList.copyOf(takenPiecesMap.keySet().stream().sorted((piece1, piece2) -> {
				if (piece1.getPieceValue() > piece2.getPieceValue()) {
					return 1;
				} else if (piece1.getPieceValue() < piece2.getPieceValue()) {
					return -1;
				}
				return 0;
			}).collect(Collectors.toList()));
		}

		/**
		 * Adds the captured pieces to the UI along with their respective counts.
		 * @param takenPiecesMap the pieces already taken.
		 */
		private void addTakenPiece(final HashMap<Piece, Integer> takenPiecesMap) {
			final List<Piece> takenPieces = this.sortedPieces(takenPiecesMap);
			this.clearChildren();
			for (final Piece takenPiece : takenPieces) {
				this.add(new Image(GuiUtils.GET_PIECE_TEXTURE_REGION(takenPiece))).size(40, 40);
				final Label label = new Label(Integer.toString(takenPiecesMap.get(takenPiece)), GuiUtils.UI_SKIN);
				label.setSize(10, 10);
				this.add(label);
			}
		}
	}
}
