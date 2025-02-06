package io.adrisdn.chessnsix.chess.engine.player;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import io.adrisdn.chessnsix.chess.engine.League;
import io.adrisdn.chessnsix.chess.engine.board.Board;
import io.adrisdn.chessnsix.chess.engine.board.Move;
import io.adrisdn.chessnsix.chess.engine.board.Move.KingSideCastleMove;
import io.adrisdn.chessnsix.chess.engine.board.Move.QueenSideCastleMove;
import io.adrisdn.chessnsix.chess.engine.board.Tile;
import io.adrisdn.chessnsix.chess.engine.pieces.Piece;
import io.adrisdn.chessnsix.chess.engine.pieces.Rook;

public final class WhitePlayer extends Player {
    public WhitePlayer(final Board board, final ImmutableList<Move> whiteStandardLegalMoves, final ImmutableList<Move> blackStandardLegalMoves, final int minute, final int second, final int millisecond) {
        super(board, whiteStandardLegalMoves, blackStandardLegalMoves, minute, second, millisecond);
    }

    @Override
    public ImmutableList<Piece> getActivePieces() {
        return super.getBoard().getWhitePieces();
    }

    @Override
    public League getLeague() {
        return League.WHITE;
    }

    @Override
    public Player getOpponent() {
        return super.getBoard().blackPlayer();
    }

    @Override
    protected KingSideCastleMove getKingSideCastleMove(final ImmutableList<Move> opponentLegals) {//TODO: change for chess960 castling
        if (!super.getBoard().getTile(61).isTileOccupied() && !super.getBoard().getTile(62).isTileOccupied()) {
            final Tile rookTile = super.getBoard().getTile(63);
            if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()) {
                if (calculateAttacksOnTile(61, opponentLegals).isEmpty() &&
                        calculateAttacksOnTile(62, opponentLegals).isEmpty() &&
                        rookTile.getPiece() instanceof Rook) {
                    return new KingSideCastleMove(super.getBoard(), super.getPlayerKing(), 62, (Rook) rookTile.getPiece(), rookTile.getTileCoordinate(), 61);
                }

            }
        }
        return null;
    }

    @Override
    protected QueenSideCastleMove getQueenSideCastleMove(ImmutableList<Move> opponentLegals) {//TODO: change for chess960 castling
        if (!super.getBoard().getTile(59).isTileOccupied() &&
                !super.getBoard().getTile(58).isTileOccupied() &&
                !super.getBoard().getTile(57).isTileOccupied()) {
            final Tile rookTile = super.getBoard().getTile(56);
            if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove() &&
                    calculateAttacksOnTile(58, opponentLegals).isEmpty() &&
                    calculateAttacksOnTile(59, opponentLegals).isEmpty() &&
                    rookTile.getPiece() instanceof Rook) {
                return new QueenSideCastleMove(super.getBoard(), super.getPlayerKing(), 58, (Rook) rookTile.getPiece(), rookTile.getTileCoordinate(), 59);
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "White";
    }

    @Override
    public ImmutableList<Move> calculateKingCastles(final ImmutableList<Move> opponentLegals) {
        return !this.isCastled() && super.getPlayerKing().isFirstMove() && !this.isInCheck() ? ImmutableList.copyOf(Arrays.asList(new Move[]{
                this.getKingSideCastleMove(opponentLegals), this.getQueenSideCastleMove(opponentLegals)
        }).parallelStream().filter(Objects::nonNull).collect(Collectors.toList())) : ImmutableList.of();
    }
}
