package io.adrisdn.chessnsix.chess.engine.player.artificialInteligence;

import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import io.adrisdn.chessnsix.chess.engine.pieces.Piece;
import io.adrisdn.chessnsix.chess.engine.pieces.PieceType;
import io.adrisdn.chessnsix.chess.engine.player.Player;

/**
 * Helper of pawn structure analysis.
 */
public final class PawnStructureAnalyse {
	public static final int ISOLATED_PAWN_PENALTY = -10;
	public static final int DOUBLED_PAWN_PENALTY = -10;

	/**
	 * Collects all pawns of a given player.
	 *
	 * @param player The player whose pawns are to be evaluated.
	 * @return An immutable list of Piece objects representing the player's pawns.
	 */
	private static ImmutableList<Piece> calculatePlayerPawns(final Player player) {
		return ImmutableList.copyOf(player.getActivePieces().stream()
				.filter(piece -> piece.getPieceType() == PieceType.PAWN).collect(Collectors.toList()));
	}

	/**
	 * Calculates the penalty for doubled pawns based on the number of pawns in each
	 * column.
	 *
	 * @param pawnsOnColumnTable An array where each index represents a column, and
	 *                           the value at each index indicates how many pawns
	 *                           are in that column.
	 * @return An integer representing the penalty for doubled pawns, calculated by
	 *         multiplying the number of pawn stacks greater than 1 by the
	 *         {@link PawnStructureAnalyse#DOUBLED_PAWN_PENALTY}.
	 */
	private static int calculatePawnColumnStack(final int[] pawnsOnColumnTable) {
		int pawnStackPenalty = 0;
		for (final int pawnStack : pawnsOnColumnTable) {
			if (pawnStack > 1) {
				pawnStackPenalty += pawnStack;
			}
		}
		return pawnStackPenalty * DOUBLED_PAWN_PENALTY;
	}

	/**
	 * Calculates the penalty for isolated pawns, i.e., pawns that have no other
	 * pawns in adjacent columns.
	 *
	 * @param pawnsOnColumnTable An array where each index represents a column, and
	 *                           the value at each index indicates how many pawns
	 *                           are in that column.
	 * @return An integer representing the penalty for isolated pawns, calculated by
	 *         multiplying the number of isolated pawns by the
	 *         {@link PawnStructureAnalyse#ISOLATED_PAWN_PENALTY}.
	 */
	private static int calculateIsolatedPawnPenalty(final int[] pawnsOnColumnTable) {
		int numIsolatedPawns = 0;
		if (pawnsOnColumnTable[0] > 0 && pawnsOnColumnTable[1] == 0) {
			numIsolatedPawns += pawnsOnColumnTable[0];
		}
		if (pawnsOnColumnTable[7] > 0 && pawnsOnColumnTable[6] == 0) {
			numIsolatedPawns += pawnsOnColumnTable[7];
		}
		for (int i = 1; i < pawnsOnColumnTable.length - 1; i++) {
			if ((pawnsOnColumnTable[i - 1] == 0 && pawnsOnColumnTable[i + 1] == 0)) {
				numIsolatedPawns += pawnsOnColumnTable[i];
			}
		}
		return numIsolatedPawns * ISOLATED_PAWN_PENALTY;
	}

	/**
	 * Creates a table that tracks how many pawns are in each of the 8 columns (A to
	 * H).
	 *
	 * @param playerPawns The list of the player's pawns.
	 * @return An integer array of size 8, where each index corresponds to a column,
	 *         and the value at each index represents the number of pawns in that
	 *         column.
	 */
	private static int[] createPawnColumnTable(final ImmutableList<Piece> playerPawns) {
		final int[] table = new int[8];
		for (final Piece playerPawn : playerPawns) {
			table[playerPawn.getPiecePosition() % 8]++;
		}
		return table;
	}

	/**
	 * Calculates the total pawn structure score for a player. This score is the sum
	 * of the penalties for doubled pawns and isolated pawns.
	 *
	 * @param player The player whose pawn structure is to be evaluated.
	 * @return An integer representing the total pawn structure score. This score is
	 *         the sum of penalties for doubled pawns and isolated pawns.
	 */
	public int pawnStructureScore(final Player player) {
		final int[] pawnsOnColumnTable = createPawnColumnTable(calculatePlayerPawns(player));
		return calculatePawnColumnStack(pawnsOnColumnTable) + calculateIsolatedPawnPenalty(pawnsOnColumnTable);
	}

	/**
	 * Calculates and returns the penalty score for isolated pawns in the player's
	 * pawn structur
	 *
	 * @param player The player whose isolated pawn penalty is to be evaluated.
	 * @return An integer representing the penalty score for isolated pawns.
	 */
	public int isolatedPawnPenalty(final Player player) {
		return calculateIsolatedPawnPenalty(createPawnColumnTable(calculatePlayerPawns(player)));
	}

	/**
	 * Calculates and returns the penalty score for doubled pawns in the player's
	 * pawn structure.
	 *
	 * @param player The player whose doubled pawn penalty is to be evaluated.
	 * @return An integer representing the penalty score for doubled pawns.
	 */
	public int doubledPawnPenalty(final Player player) {
		return calculatePawnColumnStack(createPawnColumnTable(calculatePlayerPawns(player)));
	}
}
