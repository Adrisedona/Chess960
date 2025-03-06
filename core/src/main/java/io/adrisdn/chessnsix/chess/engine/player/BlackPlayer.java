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
import io.adrisdn.chessnsix.chess.engine.pieces.King;
import io.adrisdn.chessnsix.chess.engine.pieces.Piece;
import io.adrisdn.chessnsix.chess.engine.pieces.Rook;
import io.adrisdn.chessnsix.gui.managers.LanguageManager;

public final class BlackPlayer extends Player {
	public BlackPlayer(final Board board, final ImmutableList<Move> whiteStandardLegalMoves,
			final ImmutableList<Move> blackStandardLegalMoves, final int minute, final int second,
			final int millisecond) {
		super(board, blackStandardLegalMoves, whiteStandardLegalMoves, minute, second, millisecond);
	}

	@Override
	public ImmutableList<Piece> getActivePieces() {
		return super.getBoard().getBlackPieces();
	}

	@Override
	public League getLeague() {
		return League.BLACK;
	}

	@Override
	public Player getOpponent() {
		return super.getBoard().whitePlayer();
	}

	@Override
	protected KingSideCastleMove getKingSideCastleMove(final ImmutableList<Move> opponentLegals) {
		Rook rook = null;
		King king = super.getPlayerKing();
		for (int i = king.getPiecePosition() + 1; i < 8; i++) {
			if (super.getBoard().getTile(i).isTileOccupied()) {
				if (super.getBoard().getTile(i).getPiece().getClass() == Rook.class) {
					if (rook == null) {
						rook = (Rook) super.getBoard().getTile(i).getPiece();
					} else {
						return null;
					}
					if (!rook.isFirstMove()) {
						return null;
					}
				} else {
					if (rook != null && i > 6) {
						continue;
					}
					return null;
				}
			}
		}
		if (rook == null) {
			return null;
		}
		for (int i = king.getPiecePosition() + 1; i < 7; i++) {
			if (!calculateAttacksOnTile(i, opponentLegals).isEmpty()) {
				return null;
			}
		}
		for (int i = 5; i < 7; i++) {
			if (super.getBoard().getTile(i).isTileOccupied()) {
				Piece piece = super.getBoard().getTile(i).getPiece();
				if (piece == king || piece == rook) {
					continue;
				} else {
					return null;
				}
			}
		}

		return new KingSideCastleMove(super.getBoard(), king, 6,
				rook, rook.getPiecePosition(), 5);

		// if (!super.getBoard().getTile(5).isTileOccupied() &&
		// !super.getBoard().getTile(6).isTileOccupied()) {
		// final Tile rookTile = super.getBoard().getTile(7);
		// if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()) {
		// if (calculateAttacksOnTile(5, opponentLegals).isEmpty() &&
		// calculateAttacksOnTile(6, opponentLegals).isEmpty() &&
		// rookTile.getPiece() instanceof Rook) {
		// return new KingSideCastleMove(super.getBoard(), super.getPlayerKing(), 6,
		// (Rook) rookTile.getPiece(), rookTile.getTileCoordinate(), 5);
		// }
		// }
		// }
		// return null;
	}

	@Override
	protected QueenSideCastleMove getQueenSideCastleMove(ImmutableList<Move> opponentLegals) {
		Rook rook = null;
		King king = super.getPlayerKing();
		for (int i = king.getPiecePosition() - 1; i >= 0; i--) {
			if (super.getBoard().getTile(i).isTileOccupied()) {
				if (super.getBoard().getTile(i).getPiece().getClass() == Rook.class) {
					if (rook == null) {
						rook = (Rook) super.getBoard().getTile(i).getPiece();
					} else {
						return null;
					}
					if (!rook.isFirstMove()) {
						return null;
					}
				} else {
					if (rook != null && i < 2) {
						continue;
					}
					return null;
				}
			}
		}
		if (rook == null) {
			return null;
		}
		for (int i = king.getPiecePosition() - 1; i > 1; i--) {
			if (!calculateAttacksOnTile(i, opponentLegals).isEmpty()) {
				return null;
			}
		}
		for (int i = 3; i >= 2; i--) {
			if (super.getBoard().getTile(i).isTileOccupied()) {
				Piece piece = super.getBoard().getTile(i).getPiece();
				if (piece == king || piece == rook) {
					continue;
				} else {
					return null;
				}
			}
		}

		return new QueenSideCastleMove(super.getBoard(), king, 2, rook,
				rook.getPiecePosition(), 3);

		// if (!super.getBoard().getTile(1).isTileOccupied() &&
		// !super.getBoard().getTile(2).isTileOccupied() &&
		// !super.getBoard().getTile(3).isTileOccupied()) {
		// final Tile rookTile = super.getBoard().getTile(0);
		// if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove() &&
		// calculateAttacksOnTile(2, opponentLegals).isEmpty() &&
		// calculateAttacksOnTile(3, opponentLegals).isEmpty() &&
		// rookTile.getPiece() instanceof Rook) {
		// return new QueenSideCastleMove(super.getBoard(), super.getPlayerKing(), 2,
		// (Rook) rookTile.getPiece(),
		// rookTile.getTileCoordinate(), 3);
		// }
		// }
		// return null;
	}

	@Override
	public String toString() {
		return LanguageManager.get("black_player");
	}

	@Override
	public ImmutableList<Move> calculateKingCastles(final ImmutableList<Move> opponentLegals) {
		return !this.isCastled() && super.getPlayerKing().isFirstMove() && !this.isInCheck()
				? ImmutableList.copyOf(Arrays.asList(new Move[] {
						this.getKingSideCastleMove(opponentLegals), this.getQueenSideCastleMove(opponentLegals)
				}).stream().filter(Objects::nonNull).collect(Collectors.toList()))
				: ImmutableList.of();
	}

	@Override
	public String getCode() {
		return "b";
	}
}
