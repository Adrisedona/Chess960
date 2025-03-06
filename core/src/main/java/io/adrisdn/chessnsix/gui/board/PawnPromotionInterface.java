package io.adrisdn.chessnsix.gui.board;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import io.adrisdn.chessnsix.chess.engine.board.Board;
import io.adrisdn.chessnsix.chess.engine.board.Move;
import io.adrisdn.chessnsix.chess.engine.pieces.Piece;
import io.adrisdn.chessnsix.gui.managers.AudioManager;
import io.adrisdn.chessnsix.gui.managers.GuiUtils;
import io.adrisdn.chessnsix.gui.managers.LanguageManager;
import io.adrisdn.chessnsix.gui.screens.GameScreen;

import java.util.List;

/**
 * Handles the logic and user interface for promoting a pawn in a chess game.
 */
public final class PawnPromotionInterface {

	/**
	 * This method initializes and shows the pawn promotion dialog in the GameScreen
	 * when a pawn promotion occurs.
	 *
	 * @param gameScreen    The current GameScreen instance.
	 * @param pawnPromotion The Move.PawnPromotion object representing the promotion
	 *                      move, which contains the promoted pawn and the
	 *                      destination coordinate.
	 */
	public void startLibGDXPromotion(final GameScreen gameScreen, final Move.PawnPromotion pawnPromotion) {
		gameScreen.updateChessBoard(this.promoteLibGDXPawn(gameScreen.getChessBoard(), pawnPromotion));
		final Dialog promoteDialog = new Dialog(LanguageManager.get("pawn_promotion_title"), GuiUtils.UI_SKIN);
		final Label text = new Label(LanguageManager.get("pawn_promotion_text"), GuiUtils.UI_SKIN);
		text.setColor(Color.BLACK);
		promoteDialog.text(text);
		promoteDialog.getContentTable().row();
		promoteDialog.getButtonTable()
				.add(this.pawnPromotionButton(gameScreen,
						pawnPromotion.getPromotedPawn().getPromotionPieces(pawnPromotion.getDestinationCoordinate()),
						promoteDialog, pawnPromotion));
		promoteDialog.show(gameScreen.getStage());
	}

	/**
	 * Performs the pawn promotion on the given board, updates the game state, and
	 * returns the modified board.
	 *
	 * @param board         The current state of the chessboard.
	 * @param pawnPromotion The Move.PawnPromotion object containing the details of
	 *                      the promotion.
	 * @return The updated Board after the promotion.
	 */
	private Board promoteLibGDXPawn(final Board board, final Move.PawnPromotion pawnPromotion) {
		// promotion take a move, which the move flips player turn after executed, so
		// this should not flip again
		final Board.Builder builder = new Board.Builder(pawnPromotion.getBoard().getMoveCount() + 1,
				board.currentPlayer().getOpponent().getLeague(), null)
				.updateWhiteTimer(pawnPromotion.getBoard().whitePlayer().getMinute(),
						pawnPromotion.getBoard().whitePlayer().getSecond(),
						pawnPromotion.getBoard().whitePlayer().getMillisecond())
				.updateBlackTimer(pawnPromotion.getBoard().blackPlayer().getMinute(),
						pawnPromotion.getBoard().blackPlayer().getSecond(),
						pawnPromotion.getBoard().blackPlayer().getMillisecond());

		pawnPromotion.getBoard().currentPlayer().getActivePieces().forEach(piece -> {
			if (!pawnPromotion.getPromotedPawn().equals(piece)) {
				builder.setPiece(piece);
			}
		});

		pawnPromotion.getBoard().currentPlayer().getOpponent().getActivePieces().forEach(builder::setPiece);
		builder.setPiece(pawnPromotion.getPromotedPiece().movedPiece(pawnPromotion));
		builder.setTransitionMove(pawnPromotion);
		return builder.build();
	}

	/**
	 * Creates and returns buttons for each possible piece that the pawn can be
	 * promoted to.
	 *
	 * @param gameScreen         The current GameScreen instance.
	 * @param getPromotionPieces A list of the possible promotion pieces.
	 * @param promoteDialog      The dialog that displays the promotion options.
	 * @param pawnPromotion      The Move.PawnPromotion object, used to apply the
	 *                           promotion once a button is clicked.
	 * @return An array of Button objects corresponding to each of the promotion
	 *         options.
	 */
	private Button[] pawnPromotionButton(final GameScreen gameScreen, final List<Piece> getPromotionPieces,
			final Dialog promoteDialog, final Move.PawnPromotion pawnPromotion) {
		final Button[] buttons = new Button[4];
		for (int i = 0; i < 4; i++) {
			buttons[i] = new Button(
					new TextureRegionDrawable(GuiUtils.GET_PIECE_TEXTURE_REGION(getPromotionPieces.get(i))));
			final int finalI = i;
			buttons[i].addListener(new ClickListener() {
				@Override
				public void clicked(final InputEvent event, final float x, final float y) {
					pawnPromotion.setPromotedPiece(getPromotionPieces.get(finalI));
					promoteDialog.remove();
					gameScreen.getChessGame().getPromotionSound().play(AudioManager.getSoundVolume());
					gameScreen.updateChessBoard(promoteLibGDXPawn(gameScreen.getChessBoard(), pawnPromotion));
					gameScreen.getGameBoard().drawBoard(gameScreen, gameScreen.getChessBoard(),
							gameScreen.getDisplayOnlyBoard());
					gameScreen.getMoveHistory().getMoveLog().addMove(pawnPromotion);
					gameScreen.getMoveHistory().updateMoveHistory();
					if (gameScreen.getGameBoard().isAIPlayer(gameScreen.getChessBoard().currentPlayer())) {
						gameScreen.getGameBoard().fireGameSetupPropertyChangeSupport();
					} else {
						gameScreen.getGameBoard().displayEndGameMessage(gameScreen.getChessBoard(),
								gameScreen.getStage());
					}
				}
			});
		}
		return buttons;
	}
}
