package io.adrisdn.chessnsix.gui.managers;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.google.common.collect.ImmutableList;

import io.adrisdn.chessnsix.chess.engine.pieces.Piece;

/**
 * Provides utility methods and constants for managing and configuring the
 * graphical user interface (GUI) of the chess game.
 */
public final class GuiUtils {

	// dialog
	public static final boolean IS_SMARTPHONE = Gdx.app.getType() == Application.ApplicationType.Android
			|| Gdx.app.getType() == Application.ApplicationType.iOS;
	public static final int PAD = 20;
	public static final int WIDTH = IS_SMARTPHONE ? 200 : 300;
	// private
	private static final TextureAtlas GAME_TEXTURE_ATLAS = new TextureAtlas(
			Gdx.files.internal("gameTextureAtlas.atlas"));
	public static final Texture BACKGROUND = new Texture(Gdx.files.internal("wood_light_brown_shades_hd_wooden.jpg"));
	public static final Texture LOGO = new Texture("chess_logo.png");
	public static final TextureRegion WHITE_TEXTURE_REGION = GAME_TEXTURE_ATLAS.findRegion("white");
	public static final TextureRegion TRANSPARENT_TEXTURE_REGION = GAME_TEXTURE_ATLAS.findRegion("transparent");
	// public
	public static final int WORLD_WIDTH = 1250, WORLD_HEIGHT = 675;
	public static final int GAME_BOARD_SR_SIZE = 630, TILE_SIZE = 75;
	public static final String MOVE_LOG_STATE = "MOVE_LOG_STATE";
	public static final Skin UI_SKIN = new Skin(Gdx.files.internal("UISKIN2/uiskin2.json"));
	public static final NinePatchDrawable MOVE_HISTORY_1 = new NinePatchDrawable(
			new NinePatch(WHITE_TEXTURE_REGION, Color.valueOf("#2A2B2D")));
	public static final NinePatchDrawable MOVE_HISTORY_2 = new NinePatchDrawable(
			new NinePatch(WHITE_TEXTURE_REGION, Color.valueOf("#1C1C1B")));
	public static final NinePatchDrawable WHITE_CAPTURED = new NinePatchDrawable(
			new NinePatch(WHITE_TEXTURE_REGION, Color.valueOf("#171515")));
	public static final NinePatchDrawable BLACK_CAPTURED = new NinePatchDrawable(
			new NinePatch(WHITE_TEXTURE_REGION, Color.valueOf("#2D2926")));
	// Previous and current tile
	public static final Color HUMAN_PREVIOUS_TILE = new Color(102 / 255f, 255 / 255f, 102 / 255f, 1),
			HUMAN_CURRENT_TILE = new Color(50 / 255f, 205 / 255f, 50 / 255f, 1);
	public static final Color AI_PREVIOUS_TILE = Color.PINK, AI_CURRENT_TILE = new Color(1, 51 / 255f, 51 / 255f, 1);
	public static final ImmutableList<TILE_COLOR> BOARD_COLORS = ImmutableList.of(TILE_COLOR.CLASSIC,
			TILE_COLOR.DARK_BLUE, TILE_COLOR.DARK_GRAY, TILE_COLOR.LIGHT_BLUE, TILE_COLOR.LIGHT_GRAY,
			TILE_COLOR.BUMBLEBEE);

	private GuiUtils() {
		throw new IllegalStateException("Cannot instantiate GuiUtils");
	}

	/**
	 * Generates a string identifier for a piece based on its league and type (e.g.,
	 * "wK" for a white king). This identifier is used to find the appropriate
	 * texture region for the piece.
	 *
	 * @param piece The Piece object whose texture region is being requested.
	 * @return the identifier.
	 */
	private static String GET_PIECE_REGION(final Piece piece) {
		return piece.getLeague().toString().charAt(0) + piece.toString();
	}

	/**
	 * Retrieves the texture region for a specific piece from the texture atlas
	 * based on the generated piece region identifier.
	 *
	 * @param piece The Piece object whose texture region is being requested.
	 * @return the texture of the piece.
	 */
	public static TextureRegion GET_PIECE_TEXTURE_REGION(final Piece piece) {
		return GAME_TEXTURE_ATLAS.findRegion(GET_PIECE_REGION(piece));
	}

	/**
	 * Retrieves a texture region for a specific tile from the texture atlas.
	 *
	 * @param region name of the region
	 * @return the corresponding texture.
	 */
	public static TextureRegion GET_TILE_TEXTURE_REGION(final String region) {
		return GAME_TEXTURE_ATLAS.findRegion(region);
	}

	/**
	 * Disposes of the resources used by the class, including textures and the
	 * texture atlas.
	 */
	public static void dispose() {
		BACKGROUND.dispose();
		LOGO.dispose();
		GAME_TEXTURE_ATLAS.dispose();
		UI_SKIN.dispose();
	}

	/**
	 * defines different color schemes for the chess board tiles.
	 */
	public enum TILE_COLOR {
		CLASSIC {
			@Override
			public Color DARK_TILE() {
				return new Color(181 / 255f, 136 / 255f, 99 / 255f, 1);
			}

			@Override
			public Color LIGHT_TILE() {
				return new Color(240 / 255f, 217 / 255f, 181 / 255f, 1);
			}

			@Override
			public Color HIGHLIGHT_LEGAL_MOVE_DARK_TILE() {
				return new Color(105 / 255f, 105 / 255f, 105 / 255f, 1);
			}

			@Override
			public Color HIGHLIGHT_LEGAL_MOVE_LIGHT_TILE() {
				return new Color(169 / 255f, 169 / 255f, 169 / 255f, 1);
			}
		},

		DARK_BLUE {
			@Override
			public Color DARK_TILE() {
				return new Color(29 / 255f, 61 / 255f, 99 / 255f, 1);
			}

			@Override
			public Color LIGHT_TILE() {
				return Color.WHITE;
			}

			@Override
			public Color HIGHLIGHT_LEGAL_MOVE_DARK_TILE() {
				return new Color(105 / 255f, 105 / 255f, 105 / 255f, 1);
			}

			@Override
			public Color HIGHLIGHT_LEGAL_MOVE_LIGHT_TILE() {
				return new Color(169 / 255f, 169 / 255f, 169 / 255f, 1);
			}
		},

		LIGHT_BLUE {
			@Override
			public Color DARK_TILE() {
				return new Color(137 / 255f, 171 / 255f, 227 / 255f, 1);
			}

			@Override
			public Color LIGHT_TILE() {
				return Color.WHITE;
			}

			@Override
			public Color HIGHLIGHT_LEGAL_MOVE_DARK_TILE() {
				return new Color(105 / 255f, 105 / 255f, 105 / 255f, 1);
			}

			@Override
			public Color HIGHLIGHT_LEGAL_MOVE_LIGHT_TILE() {
				return new Color(169 / 255f, 169 / 255f, 169 / 255f, 1);
			}
		},

		BUMBLEBEE {
			@Override
			public Color DARK_TILE() {
				return new Color(64 / 255f, 64 / 255f, 64 / 255f, 1);
			}

			@Override
			public Color LIGHT_TILE() {
				return new Color(254 / 255f, 231 / 255f, 21 / 255f, 1);
			}

			@Override
			public Color HIGHLIGHT_LEGAL_MOVE_DARK_TILE() {
				return new Color(105 / 255f, 105 / 255f, 105 / 255f, 1);
			}

			@Override
			public Color HIGHLIGHT_LEGAL_MOVE_LIGHT_TILE() {
				return new Color(169 / 255f, 169 / 255f, 169 / 255f, 1);
			}
		},

		DARK_GRAY {
			@Override
			public Color DARK_TILE() {
				return new Color(105 / 255f, 105 / 255f, 105 / 255f, 1);
			}

			@Override
			public Color LIGHT_TILE() {
				return Color.WHITE;
			}

			@Override
			public Color HIGHLIGHT_LEGAL_MOVE_DARK_TILE() {
				return new Color(1, 252 / 255f, 84 / 255f, 1);
			}

			@Override
			public Color HIGHLIGHT_LEGAL_MOVE_LIGHT_TILE() {
				return new Color(1, 252 / 255f, 84 / 255f, 0.95f);
			}
		},

		LIGHT_GRAY {
			@Override
			public Color DARK_TILE() {
				return new Color(177 / 255f, 179 / 255f, 179 / 255f, 1);
			}

			@Override
			public Color LIGHT_TILE() {
				return Color.WHITE;
			}

			@Override
			public Color HIGHLIGHT_LEGAL_MOVE_DARK_TILE() {
				return new Color(1, 252 / 255f, 84 / 255f, 1);
			}

			@Override
			public Color HIGHLIGHT_LEGAL_MOVE_LIGHT_TILE() {
				return new Color(1, 252 / 255f, 84 / 255f, 0.95f);
			}
		};

		/**
		 * Returns the color for dark tiles in the board.
		 *
		 * @return the color for dark tiles in the board.
		 */
		public abstract Color DARK_TILE();

		/**
		 * Returns the color for light tiles in the board.
		 *
		 * @return the color for light tiles in the board.
		 */
		public abstract Color LIGHT_TILE();

		/**
		 * Returns the highlight color for dark tiles when a legal move is highlighted.
		 *
		 * @return the highlight color for dark tiles when a legal move is highlighted.
		 */
		public abstract Color HIGHLIGHT_LEGAL_MOVE_DARK_TILE();

		/**
		 * Returns the highlight color for light tiles when a legal move is highlighted.
		 *
		 * @return the highlight color for light tiles when a legal move is highlighted.
		 */
		public abstract Color HIGHLIGHT_LEGAL_MOVE_LIGHT_TILE();
	}
}
