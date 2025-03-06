package io.adrisdn.chessnsix.chess.engine.board;

/**
 * Indicates the state of a move
 */
public enum MoveStatus {
	/**
	 * Move completely legal
	 */
	DONE {
        @Override
        public boolean isDone() {
            return true;
        }
    },
	/**
	 * Move illegal, cannot be done
	 */
    ILLEGAL_MOVE {
        @Override
        public boolean isDone() {
            return false;
        }
    },
	/**
	 * Move leaves the player in chet, so it cannot be done
	 */
    LEAVES_PLAYER_IN_CHECK {
        @Override
        public boolean isDone() {
            return false;
        }
    };

	/**
	 * Obtains if a move is done or not
	 * @return true if it's done, false if not
	 */
    public abstract boolean isDone();
}
