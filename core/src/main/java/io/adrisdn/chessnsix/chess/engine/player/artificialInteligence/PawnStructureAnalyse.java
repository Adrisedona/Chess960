package io.adrisdn.chessnsix.chess.engine.player.artificialInteligence;

import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import io.adrisdn.chessnsix.chess.engine.pieces.Piece;
import io.adrisdn.chessnsix.chess.engine.pieces.PieceType;
import io.adrisdn.chessnsix.chess.engine.player.Player;

public final class PawnStructureAnalyse {
    public static final int ISOLATED_PAWN_PENALTY = -10;
    public static final int DOUBLED_PAWN_PENALTY = -10;

    private static ImmutableList<Piece> calculatePlayerPawns(final Player player) {
        return ImmutableList.copyOf(player.getActivePieces().stream().filter(piece -> piece.getPieceType() == PieceType.PAWN).collect(Collectors.toList()));
    }

    private static int calculatePawnColumnStack(final int[] pawnsOnColumnTable) {
        int pawnStackPenalty = 0;
        for (final int pawnStack : pawnsOnColumnTable) {
            if (pawnStack > 1) {
                pawnStackPenalty += pawnStack;
            }
        }
        return pawnStackPenalty * DOUBLED_PAWN_PENALTY;
    }

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

    private static int[] createPawnColumnTable(final ImmutableList<Piece> playerPawns) {
        final int[] table = new int[8];
        for (final Piece playerPawn : playerPawns) {
            table[playerPawn.getPiecePosition() % 8]++;
        }
        return table;
    }

    public int pawnStructureScore(final Player player) {
        final int[] pawnsOnColumnTable = createPawnColumnTable(calculatePlayerPawns(player));
        return calculatePawnColumnStack(pawnsOnColumnTable) + calculateIsolatedPawnPenalty(pawnsOnColumnTable);
    }

    public int isolatedPawnPenalty(final Player player) {
        return calculateIsolatedPawnPenalty(createPawnColumnTable(calculatePlayerPawns(player)));
    }

    public int doubledPawnPenalty(final Player player) {
        return calculatePawnColumnStack(createPawnColumnTable(calculatePlayerPawns(player)));
    }
}
