package io.adrisdn.chessnsix.gui.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * Schema to create screens, so the actual screens only need to implement the methods they actually need.
 */
public abstract class AbstractScreen implements Screen {

	protected Stage stage;

	/**
	 * Obtains this screen's stage.
	 * @return this screen's stage.
	 */
	public Stage getStage() {
		return stage;
	}

	@Override
	public void show() {

	}

	@Override
	public void render(float delta) {

	}

	@Override
	public void resize(int width, int height) {
		this.stage.getViewport().update(width, height, true);
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {
		this.stage.dispose();
		this.stage.getBatch().dispose();
	}

}
