package io.adrisdn.chessnsix.chess.engine.player.artificialInteligence;

import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;

import io.adrisdn.chessnsix.chess.engine.board.Board;
import io.adrisdn.chessnsix.chess.engine.board.BoardUtils;
import io.adrisdn.chessnsix.chess.engine.board.Move;
import io.adrisdn.chessnsix.chess.engine.board.MoveTransition;
import io.adrisdn.chessnsix.chess.engine.player.Player;

/**
 * Implements the MiniMax algorithm with Alpha-Beta pruning for optimizing
 * decision-making in a chess AI engine. The algorithm evaluates potential moves
 * by recursively exploring the game tree to a specified depth and selecting the
 * best possible move for the current player. It supports multi-threading for
 * parallel evaluation of different moves.
 */
public final class MiniMax {

	private final StandardBoardEvaluation evaluator;
	private final int searchDepth, nThreads;
	private int quiescenceCount;
	private static final int MAX_QUIESCENCE = 5000 * 5;
	private final AtomicBoolean terminateProcess;
	private final AtomicInteger moveCount;

	/**
	 * Defines different strategies for sorting moves before evaluating them.
	 */
	private enum MoveSorter {

		/**
		 * Sorts moves based on castling priority and the value of the aggressor and
		 * victim.
		 */
		STANDARD_SORT {
			@Override
			ImmutableList<Move> sort(final ImmutableList<Move> moves) {
				return Ordering.from((Comparator<Move>) (move1, move2) -> ComparisonChain.start()
						.compareTrueFirst(move1.isCastlingMove(), move2.isCastlingMove())
						.compare(BoardUtils.mostValuableVictimLeastValuableAggressor(move2),
								BoardUtils.mostValuableVictimLeastValuableAggressor(move1))
						.result()).immutableSortedCopy(moves);
			}
		},

		/**
		 * Sorts moves based on king safety (threats) in addition to castling and
		 * aggressor/victim values.
		 */
		EXPENSIVE_SORT {
			@Override
			ImmutableList<Move> sort(final ImmutableList<Move> moves) {
				return Ordering.from((Comparator<Move>) (move1, move2) -> ComparisonChain.start()
						.compareTrueFirst(BoardUtils.kingThreat(move1), BoardUtils.kingThreat(move2))
						.compareTrueFirst(move1.isCastlingMove(), move2.isCastlingMove())
						.compare(BoardUtils.mostValuableVictimLeastValuableAggressor(move2),
								BoardUtils.mostValuableVictimLeastValuableAggressor(move1))
						.result()).immutableSortedCopy(moves);
			}
		};

		/**
		 * Sorts the possible moves depending on their evaluation
		 *
		 * @param moves list of moves to evaluate
		 * @return ordered list
		 */
		abstract ImmutableList<Move> sort(final ImmutableList<Move> moves);
	}

	/**
	 * Initializes the MiniMax algorithm, setting up the evaluator, search depth,
	 * and number of threads based on the system's available processors.
	 *
	 * @param searchDepth The depth of the search (how many moves ahead to
	 *                    evaluate).
	 */
	public MiniMax(final int searchDepth) {
		this.evaluator = new StandardBoardEvaluation();
		this.nThreads = Runtime.getRuntime().availableProcessors();
		this.searchDepth = this.nThreads > 4 ? searchDepth + 1 : searchDepth;
		this.quiescenceCount = 0;
		this.moveCount = new AtomicInteger(0);
		this.terminateProcess = new AtomicBoolean(false);
	}

	/**
	 * executes the MiniMax algorithm by evaluating all possible moves for the
	 * current player up to a specified depth.
	 *
	 * @param board board to evaluate
	 * @return best move calculated by the engine.
	 */
	public Move execute(final Board board) {
		final Player currentPlayer = board.currentPlayer();
		final AtomicReference<Move> bestMove = new AtomicReference<>(Move.MoveFactory.getNullMove());
		if (currentPlayer.isTimeOut()) {
			this.setTerminateProcess(true);
			return bestMove.get();
		}
		final AtomicInteger highestSeenValue = new AtomicInteger(Integer.MIN_VALUE);
		final AtomicInteger lowestSeenValue = new AtomicInteger(Integer.MAX_VALUE);
		final AtomicInteger currentValue = new AtomicInteger(0);

		final ExecutorService executorService = Executors.newFixedThreadPool(this.nThreads);

		for (final Move move : MoveSorter.EXPENSIVE_SORT.sort((board.currentPlayer().getLegalMoves()))) {
			final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
			this.quiescenceCount = 0;

			if (moveTransition.getMoveStatus().isDone()) {
				if (moveTransition.getLatestBoard().currentPlayer().isInCheckmate()) {
					return move;
				}
				executorService.execute(() -> {
					final int currentVal = currentPlayer.getLeague().isWhite()
							? min(moveTransition.getLatestBoard(), MiniMax.this.searchDepth - 1, highestSeenValue.get(),
									lowestSeenValue.get())
							: max(moveTransition.getLatestBoard(), MiniMax.this.searchDepth - 1, highestSeenValue.get(),
									lowestSeenValue.get());

					currentValue.set(currentVal);
					if (terminateProcess.get()) {
						// immediately set move to null after time out for AI
						bestMove.set(Move.MoveFactory.getNullMove());
					} else {
						if (currentPlayer.getLeague().isWhite() && currentValue.get() > highestSeenValue.get()) {
							highestSeenValue.set(currentValue.get());
							bestMove.set(move);
						} else if (currentPlayer.getLeague().isBlack() && currentValue.get() < lowestSeenValue.get()) {
							lowestSeenValue.set(currentValue.get());
							bestMove.set(move);
						}
						moveCount.set(moveCount.get() + 1);
					}
				});
			}

		}

		executorService.shutdown();
		try {
			executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
		return bestMove.get();
	}

	/**
	 * Sets the flag that can be used to stop the search prematurely
	 *
	 * @param terminateProcess A boolean value to terminate the search process
	 */
	public void setTerminateProcess(final boolean terminateProcess) {
		this.terminateProcess.set(terminateProcess);
	}

	/**
	 * Returns whether the search process has been flagged for termination.
	 *
	 * @return true ifthe search process has been flagged for termination, false if
	 *         not.
	 */
	public boolean getTerminateProcess() {
		return this.terminateProcess.get();
	}

	/**
	 * Returns the total number of moves evaluated during the search process.
	 *
	 * @return the total number of moves evaluated during the search process.
	 */
	public int getMoveCount() {
		return this.moveCount.get();
	}

	/**
	 * Maximizing function in the Minimax algorithm, evaluates possible moves for
	 * the current player and returns the highest evaluation score.
	 *
	 * @param board   board to evaluate
	 * @param depth   number of moves ahead to look
	 * @param highest best evaluation found
	 * @param lowest  worst evaluation found
	 * @return the best evaluation found
	 */
	private int max(final Board board, final int depth, final int highest, final int lowest) {
		if (this.terminateProcess.get()) {
			return highest;
		}
		if (depth == 0 || BoardUtils.isEndGameScenario(board)) {
			return this.evaluator.evaluate(board, depth);
		}
		int currentHighest = highest;
		for (final Move move : MoveSorter.STANDARD_SORT.sort((board.currentPlayer().getLegalMoves()))) {
			final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
			if (moveTransition.getMoveStatus().isDone()) {
				final Board toBoard = moveTransition.getLatestBoard();
				currentHighest = Math.max(currentHighest, min(toBoard,
						calculateQuiescenceDepth(toBoard, depth), currentHighest, lowest));
				if (currentHighest >= lowest) {
					return lowest;
				}
			}
		}
		return currentHighest;
	}

	/**
	 * Minimizing function in the Minimax algorithm, evaluates possible moves for
	 * the opponent player and returns the lowest evaluation score.
	 *
	 * @param board   board to evaluate
	 * @param depth   number of moves ahead to look
	 * @param highest best evaluation found
	 * @param lowest  worst evaluation found
	 * @return the worst evaluation found
	 */
	private int min(final Board board, final int depth, final int highest, final int lowest) {
		if (this.terminateProcess.get()) {
			return lowest;
		}
		if (depth == 0 || BoardUtils.isEndGameScenario(board)) {
			return this.evaluator.evaluate(board, depth);
		}
		int currentLowest = lowest;
		for (final Move move : MoveSorter.STANDARD_SORT.sort((board.currentPlayer().getLegalMoves()))) {
			final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
			if (moveTransition.getMoveStatus().isDone()) {
				final Board toBoard = moveTransition.getLatestBoard();
				currentLowest = Math.min(currentLowest, max(toBoard,
						calculateQuiescenceDepth(toBoard, depth), highest, currentLowest));
				if (currentLowest <= highest) {
					return highest;
				}
			}
		}
		return currentLowest;
	}

	/**
	 * Prevents the "horizon effect" by continuing the evaluation of moves in
	 * positions with high activity (e.g., attacks or checks). If the board has
	 * significant action (e.g., the king is in check), the depth of search is
	 * increased temporarily.
	 *
	 * @param toBoard board to valuate
	 * @param depth   number of moves to look ahead
	 * @return actual depth of the search
	 */
	private int calculateQuiescenceDepth(final Board toBoard, final int depth) {
		if (depth == 1 && this.quiescenceCount < MAX_QUIESCENCE) {
			int activityMeasure = 0;
			if (toBoard.currentPlayer().isInCheck()) {
				activityMeasure += 1;
			}
			for (final Move move : BoardUtils.lastNMoves(toBoard, 2)) {
				if (move.isAttack()) {
					activityMeasure += 1;
				}
			}
			if (activityMeasure >= 2) {
				this.quiescenceCount += 1;
				return 2;
			}
		}
		return depth - 1;
	}
}
