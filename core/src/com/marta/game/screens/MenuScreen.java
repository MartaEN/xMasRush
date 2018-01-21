package com.marta.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class MenuScreen implements Screen {

    private SpriteBatch batch;
    private TextureRegion background;
    private Stage stage;

    MenuScreen (SpriteBatch batch) { this.batch = batch; }

    @Override
    public void show() {
        this.background = Assets.getInstance().getAtlas().findRegion("background");
        createGUI();
    }

    private void createGUI () {

        this.stage = new Stage(ScreenManager.getInstance().getViewport(), batch);
        Gdx.input.setInputProcessor(stage);

        Button btnNewGame = new TextButton ("New Game", Assets.getInstance().getSkin());
        Button btnBonusShop = new TextButton ("Stats and Upgrades", Assets.getInstance().getSkin());
        Button btnExitGame = new TextButton("Exit", Assets.getInstance().getSkin());

        btnNewGame.setPosition(400, 400);
        btnBonusShop.setPosition(400, 280);
        btnExitGame.setPosition(400, 160);
        btnNewGame.setSize(400, 100);
        btnBonusShop.setSize(400, 100);
        btnExitGame.setSize(400,100);

        stage.addActor(btnNewGame);
        stage.addActor(btnBonusShop);
        stage.addActor(btnExitGame);

        btnNewGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ScreenManager.getInstance().switchScreenTo(ScreenManager.ScreenType.GAME);
            }
        });

        btnBonusShop.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ScreenManager.getInstance().switchScreenTo(ScreenManager.ScreenType.SHOP);
            }
        });

        btnExitGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
    }

    private void update (float dt) {
        stage.act(dt);
    }

    @Override
    public void render(float delta) {
        update(delta);
        batch.begin();
        batch.draw(background, 0,0);
        batch.end();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        ScreenManager.getInstance().onResize(width,height);

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
        Gdx.input.setInputProcessor(null);
    }
}
