package io.adrisdn.chessnsix.gui.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.google.common.collect.ImmutableList;

import io.adrisdn.chessnsix.gui.ChessGame;
import io.adrisdn.chessnsix.gui.managers.AudioManager;
import io.adrisdn.chessnsix.gui.managers.GuiUtils;
import io.adrisdn.chessnsix.gui.managers.LanguageManager;

/**
 * Screen to handle the music and sound volume, the enabling and disabling of vibration, and the language.
 */
public class Settings extends AbstractScreen {


	/**
	 * Initializes the screen with the values loaded in the managers
	 * @param chessGame the game this screen belongs to.
	 */
	public Settings(final ChessGame chessGame) {
		this.stage = new Stage(new FitViewport(GuiUtils.WORLD_WIDTH, GuiUtils.WORLD_HEIGHT), new SpriteBatch());

		final Table table = new Table(GuiUtils.UI_SKIN);

		table.add(LanguageManager.get("music")).width(GuiUtils.WIDTH).pad(GuiUtils.PAD).align(Align.left).row();
		final Slider sliderMusic = new Slider(0, 1, 0.1f, false, GuiUtils.UI_SKIN);
		sliderMusic.setValue(AudioManager.getMusicVolume());
		sliderMusic.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				AudioManager.setMusicVolume(sliderMusic.getValue(), chessGame);
			}

		});
		table.add(sliderMusic).width(GuiUtils.WIDTH).pad(GuiUtils.PAD).align(Align.left).row();

		table.add(LanguageManager.get("sound")).width(GuiUtils.WIDTH).pad(GuiUtils.PAD).align(Align.left).row();
		final Slider sliderSound = new Slider(0, 1, 0.1f, false, GuiUtils.UI_SKIN);
		sliderSound.setValue(AudioManager.getSoundVolume());
		sliderSound.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				AudioManager.setSoundVolume(sliderSound.getValue());
			}

		});
		table.add(sliderSound).width(GuiUtils.WIDTH).pad(GuiUtils.PAD).align(Align.left).row();

		if (GuiUtils.IS_SMARTPHONE) {
			final CheckBox checkBoxVibration = new CheckBox(LanguageManager.get("vibration"), GuiUtils.UI_SKIN);
			checkBoxVibration.setChecked(AudioManager.isVibration());
			checkBoxVibration.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					AudioManager.setVibration(checkBoxVibration.isChecked());
				}
			});
			table.add(checkBoxVibration).width(GuiUtils.WIDTH).pad(GuiUtils.PAD).align(Align.left).row();
		}

		final SelectBox<LangLocale> lang = new  SelectBox<>(GuiUtils.UI_SKIN);
		lang.setItems(LangLocale.getLocales().toArray(new LangLocale[] {}));
		lang.setSelected(new LangLocale(LanguageManager.get(LanguageManager.getCurrentLang().equals("en") ? "english" : "spanish"), LanguageManager.getCurrentLang()));
		lang.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				LanguageManager.setLanguage(lang.getSelected().getCode());
				chessGame.reload();
			}
		});
		table.add(lang).width(GuiUtils.WIDTH).pad(GuiUtils.PAD).align(Align.left).row();

		table.add(this.backButton(chessGame)).width(300).pad(GuiUtils.PAD).row();

		table.setFillParent(true);

		this.stage.addActor(table);
	}

	/**
	 * Button to go back to the {@link WelcomeScreen}
	 *
	 * @param chessGame game that handles the screen change
	 * @return
	 */
	private TextButton backButton(final ChessGame chessGame) {
		final TextButton textButton = new TextButton(LanguageManager.get("back_menu"), GuiUtils.UI_SKIN);
		textButton.addListener(new ClickListener() {
			@Override
			public void clicked(final InputEvent event, final float x, final float y) {
				chessGame.setScreen(chessGame.getWelcomeScreen());
			}
		});
		return textButton;
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		this.stage.act(delta);
		this.stage.getBatch().begin();
		this.stage.getBatch().draw(GuiUtils.BACKGROUND, 0, 0);

		this.stage.getBatch().end();
		this.stage.draw();
	}

	/**
	 * Handles the diferentes languages
	 */
	private static class LangLocale {
		private final String name;
		private final String code;

		/**
		 * Initializes a new locale based on it's name and code.
		 * @param name name of the language.
		 * @param code code o the language.
		 */
		public LangLocale(String name, String code) {
			this.name = name;
			this.code = code;
		}

		/**
		 * Returns the code of this language.
		 * @return the code of this language.
		 */
		public String getCode() {
			return code;
		}

		@Override
		public String toString() {
			return name;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof LangLocale) {
				return ((LangLocale)obj).code.equals(this.code);
			}
			return false;
		}

		/**
		 * Obtains the differents languages in the game.
		 * @return a list with all the languages avaliables
		 */
		public static ImmutableList<LangLocale> getLocales() {
			ArrayList<LangLocale> langLocales = new ArrayList<>();
			langLocales.add(new LangLocale(LanguageManager.get("english"), "en"));
			langLocales.add(new LangLocale(LanguageManager.get("spanish"), "es"));
			return ImmutableList.copyOf(langLocales);
		}

	}

}
