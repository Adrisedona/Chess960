package io.adrisdn.chessnsix.chess.database;

import com.google.common.collect.ImmutableList;

public class Game {
	private final int id;
	private final String date;
	private final int numberMoves;
	private final String result;
	private final ImmutableList<String> moves;

	public Game(final int id, final String date, final int numberMoves, final String result, final ImmutableList<String> moves) {
		this.id = id;
		this.date = date;
		this.numberMoves = numberMoves;
		this.result = result;
		this.moves = moves;
	}

	public int getId() {
		return id;
	}

	public String getDate() {
		return date;
	}

	public int getNumberMoves() {
		return numberMoves;
	}

	public String getResult() {
		return result;
	}

	public ImmutableList<String> getMoves() {
		return moves;
	}


}
