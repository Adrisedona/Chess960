package io.adrisdn.chessnsix.chess.engine.FEN;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.badlogic.gdx.Gdx;

import com.google.common.collect.ImmutableList;

/**
 * Holds the FEN string for the possible starting positions in FischerRandom
 */
public class FenFischerRandom {

	private static ImmutableList<String> FISHER_RANDOM_FEN;
	private static boolean initiable = true;

	private FenFischerRandom() {
		throw new RuntimeException("Non instantiable");
	}

	/**
	 * Initializes the list of possible position from the file.
	 */
	public static void InitFischerRandomList() {
		if (!initiable) {
			throw new IllegalArgumentException("List already inicialiced");
		}
		List<String> temp = new ArrayList<>();
		try (Scanner sc = new Scanner(Gdx.files.internal("fen/fenfisherrandom.fen").read())) {
			while (sc.hasNextLine()) {
				temp.add(sc.nextLine());
			}
		}
		FISHER_RANDOM_FEN = ImmutableList.copyOf(temp);
		initiable = false;
	}

	/**
	 * Obtains a random starting position from the list
	 * @return the FEN string representing the starting position
	 */
	public static String getRandomFen() {
		if (initiable) {
			throw new IllegalStateException("The list hasn't been inicialized");
		}
		return FISHER_RANDOM_FEN.get((int)(Math.random() * FISHER_RANDOM_FEN.size()));
	}
}
