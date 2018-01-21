package com.marta.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class LoadingScreen implements Screen {
    private SpriteBatch batch;
    private Texture texture;

    public LoadingScreen(SpriteBatch batch) { this.batch = batch; }

    @Override
    public void show() {
        Pixmap pixmap = new Pixmap(580, 30, Pixmap.Format.RGB888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        texture = new Texture(pixmap);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(39f/255f, 70f/255f, 135f/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if(Assets.getInstance().getAssetManager().update()) {
            Assets.getInstance().makeLinks();
            ScreenManager.getInstance().goToTargetScreen();
        }
        batch.begin();
        batch.setColor(56f/255f,88f/255f,158f/255f,1);
        batch.draw(texture, 200, 200, 580 , 30);
        batch.setColor(1,1,1,1);
        batch.draw(texture, 200, 200, 580 * Assets.getInstance().getAssetManager().getProgress(), 30);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        ScreenManager.getInstance().onResize(width, height);
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
        texture.dispose();
    }
}
