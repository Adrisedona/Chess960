package io.adrisdn.chessnsix.chess.engine.player.artificialInteligence;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;

import io.adrisdn.chessnsix.chess.engine.board.Board;
import io.adrisdn.chessnsix.chess.engine.board.Move;
import io.adrisdn.chessnsix.chess.engine.pieces.Piece;
import io.adrisdn.chessnsix.chess.engine.pieces.PieceType;
import io.adrisdn.chessnsix.chess.engine.player.Player;

/**
 * Provides a comprehensive evaluation of a chess game board, considering
 * various aspects such as piece positions, mobility, attacks, castling status,
 * pawn structure, and check/checkmate conditions. This class is designed to be
 * used by chess engines to evaluate the strength of a given position for both
 * players (black and white) at a specific depth in the game.
 */
public final class StandardBoardEvaluation {

	/**
	 * The value awarded for the opponent's king being in check.
	 */
	private static final int CHECK_KING = 45;
	/**
	 * The value for checkmate.
	 */
	private static final int CHECK_MATE = 10000;
	/**
	 * Bonus multiplier for depth in search.
	 */
	private static final int DEPTH_BONUS = 100;
	/**
	 * Bonus for castling status.
	 */
	private static final int CASTLE_BONUS = 25;

	/**
	 * Multiplier for the mobility score.
	 */
	private static final int MOBILITY_MULTIPLIER = 5;
	/**
	 * Multiplier for the attack score.
	 */
	private static final int ATTACK_MULTIPLIER = 1;
	/**
	 * Bonus for having two bishops.
	 */
	private static final int TWO_BISHOPS_BONUS = 25;
	/**
	 * Object used to evaluate the player's pawn structure.
	 */
	private static final PawnStructureAnalyse pawnStructureScore = new PawnStructureAnalyse();

	/**
	 * Position-dependent evaluation for the King.
	 */
	private static final int[] kingEvaluation = {
			-30, -40, -40, -50, -50, -40, -40, -30,
			-30, -40, -40, -50, -50, -40, -40, -30,
			-30, -40, -40, -50, -50, -40, -40, -30,
			-30, -40, -40, -50, -50, -40, -40, -30,
			-20, -30, -30, -40, -40, -30, -30, -20,
			-10, -20, -20, -20, -20, -20, -20, -10,
			20, 20, 0, 0, 0, 0, 20, 20,
			20, 30, 10, 0, 0, 10, 30, 20
	};

	/**
	 * Position-dependent evaluation for the Queen.
	 */
	private static final int[] queenEvaluation = {
			-20, -10, -10, -5, -5, -10, -10, -20,
			-10, 0, 0, 0, 0, 0, 0, -10,
			-10, 0, 5, 5, 5, 5, 0, -10,
			-5, 0, 5, 5, 5, 5, 0, -5,
			0, 0, 5, 5, 5, 5, 0, -5,
			-10, 5, 5, 5, 5, 5, 0, -10,
			-10, 0, 5, 0, 0, 0, 0, -10,
			-20, -10, -10, -5, -5, -10, -10, -20
	};

	/**
	 * Position-dependent evaluation for the Rook.
	 */
	private static final int[] rookEvaluation = {
			0, 0, 0, 0, 0, 0, 0, 0,
			5, 20, 20, 20, 20, 20, 20, 5,
			-5, 0, 0, 0, 0, 0, 0, -5,
			-5, 0, 0, 0, 0, 0, 0, -5,
			-5, 0, 0, 0, 0, 0, 0, -5,
			-5, 0, 0, 0, 0, 0, 0, -5,
			-5, 0, 0, 0, 0, 0, 0, -5,
			0, 0, 0, 5, 5, 0, 0, 0
	};

	/**
	 * Position-dependent evaluation for the Bishop.
	 */
	private static final int[] bishopEvaluation = {
			-20, -10, -10, -10, -10, -10, -10, -20,
			-10, 0, 0, 0, 0, 0, 0, -10,
			-10, 0, 5, 10, 10, 5, 0, -10,
			-10, 5, 5, 10, 10, 5, 5, -10,
			-10, 0, 10, 10, 10, 10, 0, -10,
			-10, 10, 10, 10, 10, 10, 10, -10,
			-10, 5, 0, 0, 0, 0, 5, -10,
			-20, -10, -10, -10, -10, -10, -10, -20
	};

	/**
	 * Position-dependent evaluation for the Knight.
	 */
	private static final int[] knightEvaluation = {
			-50, -40, -30, -30, -30, -30, -40, -50,
			-40, -20, 0, 0, 0, 0, -20, -40,
			-30, 0, 10, 15, 15, 10, 0, -30,
			-30, 5, 15, 20, 20, 15, 5, -30,
			-30, 0, 15, 20, 20, 15, 0, -30,
			-30, 5, 10, 15, 15, 10, 5, -30,
			-40, -20, 0, 5, 5, 0, -20, -40,
			-50, -40, -30, -30, -30, -30, -40, -50
	};

	/**
	 * Position-dependent evaluation for the Pawn..
	 */
	private static final int[] pawnEvaluation = {
			0, 0, 0, 0, 0, 0, 0, 0,
			75, 75, 75, 75, 75, 75, 75, 75,
			25, 25, 29, 29, 29, 29, 25, 25,
			5, 5, 10, 55, 55, 10, 5, 5,
			0, 0, 0, 20, 20, 0, 0, 0,
			5, -5, -10, 0, 0, -10, -5, 5,
			5, 10, 10, -20, -20, 10, 10, 5,
			0, 0, 0, 0, 0, 0, 0, 0
	};

	/**
	 * Calculates the score for a given player by considering multiple factors
	 * including mobility, attack strength, castling status, piece evaluations, and
	 * pawn structure.
	 *
	 * @param player The player whose score is to be evaluated.
	 * @param depth  The search depth for evaluating checkmate and check situations.
	 * @return An integer score representing the player's position based on various
	 *         factors.
	 */
	private static int scorePlayer(final Player player, final int depth) {
		return mobility(player) +
				checkMate(player, depth) +
				attacks(player) +
				castled(player) +
				pieceEvaluations(player) +
				pawnStructure(player);
	}

	/**
	 * Evaluates the number of legal attacks made by the player and adds a score
	 * based on the value of the attacked pieces.
	 *
	 * @param player The player whose attack score is to be evaluated.
	 * @return An integer score representing the player's attack strength.
	 */
	private static int attacks(final Player player) {
		int attackScore = 0;
		for (final Move move : player.getLegalMoves()) {
			if (move.isAttack()) {
				final Piece movedPiece = move.getMovedPiece();
				final Piece attackedPiece = move.getAttackedPiece();
				if (movedPiece.getPieceValue() <= attackedPiece.getPieceValue()) {
					attackScore++;
				}
			}
		}
		return attackScore * ATTACK_MULTIPLIER;
	}

	/**
	 * Evaluates the value of the player's pieces, considering their type and
	 * position on the board.
	 *
	 * @param player The player whose piece evaluations are to be calculated.
	 * @return An integer representing the total evaluation of the player's pieces,
	 *         including a bonus if the player has two bishops.
	 */
	private static int pieceEvaluations(final Player player) {
		int pieceValuationScore = 0;
		int numBishops = 0;
		for (final Piece piece : player.getActivePieces()) {
			pieceValuationScore += piece.getPieceValue() + positionValue(piece).get(piece.getPiecePosition());
			if (piece.getPieceType() == PieceType.BISHOP) {
				numBishops++;
			}
		}
		return pieceValuationScore + (numBishops == 2 ? TWO_BISHOPS_BONUS : 0);
	}

	/**
	 * Calculates the player's mobility score based on the number of legal moves
	 * available.
	 *
	 * @param player The player whose mobility is to be evaluated.
	 * @return An integer score representing the player's mobility.
	 */
	private static int mobility(final Player player) {
		return MOBILITY_MULTIPLIER * mobilityRatio(player);
	}

	/**
	 * Computes the ratio of the player's legal moves to the opponent's legal moves,
	 * which is then multiplied by a mobility multiplier to get the final mobility
	 * score.
	 *
	 * @param player The player whose mobility ratio is to be calculated.
	 * @return An integer representing the ratio of legal moves between the player
	 *         and the opponent.
	 */
	private static int mobilityRatio(final Player player) {
		return (int) ((player.getLegalMoves().size() * 10.0f) / player.getOpponent().getLegalMoves().size());
	}

	/**
	 * Returns a bonus score if the player has castled.
	 *
	 * @param player
	 * @return
	 */
	private static int castled(final Player player) {
		return player.isCastled() ? CASTLE_BONUS : 0;
	}

	/**
	 * Checks if the opponent is in checkmate and assigns a corresponding score. The
	 * score is weighted by the depth of the search.
	 *
	 * @param player The player whose opponent's checkmate status is to be
	 *               evaluated.
	 * @param depth  The current depth in the search tree, which is used to weigh
	 *               the checkmate score.
	 * @return A score based on whether the opponent is in checkmate, with the score
	 *         being multiplied by the depth bonus.
	 */
	private static int checkMate(final Player player, final int depth) {
		return player.getOpponent().isInCheckmate() ? CHECK_MATE * depthBonus(depth) : check(player);
	}

	/**
	 * Returns a bonus score based on the current search depth.
	 *
	 * @param depth The current depth in the search tree.
	 * @return A bonus score for the depth, either 1 or a multiple of
	 *         {@link StandardBoardEvaluation#DEPTH_BONUS}.
	 */
	private static int depthBonus(final int depth) {
		return depth == 0 ? 1 : DEPTH_BONUS * depth;
	}

	/**
	 * Returns a score if the opponent's king is in check.
	 *
	 * @param player The player whose opponent's check status is to be evaluated.
	 * @return A score if the opponent is in check, otherwise 0.
	 */
	private static int check(final Player player) {
		return player.getOpponent().isInCheck() ? CHECK_KING : 0;
	}

	/**
	 * Calculates the player's pawn structure score.
	 *
	 * @param player The player whose pawn structure is to be evaluated.
	 * @return An integer representing the player's pawn structure score.
	 */
	private static int pawnStructure(final Player player) {
		return pawnStructureScore.pawnStructureScore(player);
	}

	/**
	 * Returns a list of position-dependent evaluation values for a given piece
	 * based on its type and position on the board.
	 *
	 * @param piece The piece whose position evaluation is to be retrieved.
	 * @return A list of integers representing the piece's position-dependent
	 *         evaluation values.
	 */
	private static ImmutableList<Integer> positionValue(final Piece piece) {
		final boolean isWhite = piece.getLeague().isWhite();

		if ("K".equals(piece.toString())) {
			return getPiecePositionValue(isWhite, kingEvaluation);
		} else if ("Q".equals(piece.toString())) {
			return getPiecePositionValue(isWhite, queenEvaluation);
		} else if ("R".equals(piece.toString())) {
			return getPiecePositionValue(isWhite, rookEvaluation);
		} else if ("B".equals(piece.toString())) {
			return getPiecePositionValue(isWhite, bishopEvaluation);
		} else if ("N".equals(piece.toString())) {
			return getPiecePositionValue(isWhite, knightEvaluation);
		} else {
			return getPiecePositionValue(isWhite, pawnEvaluation);
		}
	}

	/**
	 * Returns the position-dependent evaluation values for a piece, adjusting for
	 * whether the piece belongs to a white or black player.
	 *
	 * @param isWhite       A boolean indicating whether the piece belongs to the
	 *                      white player.
	 * @param positionValue An array of position-dependent evaluation values for the
	 *                      piece.
	 * @return A list of evaluation values for the piece, adjusted for its color.
	 */
	private static ImmutableList<Integer> getPiecePositionValue(final boolean isWhite, final int[] positionValue) {
		return isWhite ? ImmutableList.copyOf(Ints.asList(positionValue)) : reversePositionEvaluation(positionValue);
	}

	/**
	 * Reverses the position-dependent evaluation array to account for the player's
	 * color (i.e., for black pieces).
	 *
	 * @param positionValue An array of position-dependent evaluation values.
	 * @return A reversed list of position values.
	 */
	private static ImmutableList<Integer> reversePositionEvaluation(final int[] positionValue) {
		return ImmutableList.copyOf(Ints.asList(positionValue)).reverse();
	}

	/**
	 * Evaluates the board position from the perspective of both players (black and
	 * white) at a given search depth.
	 *
	 * @param board The current chess board to evaluate.
	 * @param depth The current depth in the search tree for the evaluation.
	 * @return An integer representing the evaluation of the board, with a positive
	 *         value favoring white and a negative value favoring black.
	 */
	public int evaluate(final Board board, final int depth) {
		return -scorePlayer(board.blackPlayer(), depth) + scorePlayer(board.whitePlayer(), depth);
	}
}
