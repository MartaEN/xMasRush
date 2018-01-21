package com.marta.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.marta.game.PlayerStats;
import com.marta.game.runners.*;
import com.marta.game.stuff.*;
import com.marta.game.stuff.bullets.*;

public class GameScreen implements Screen {

    private SpriteBatch batch;
    private Camera fixedCamera;
    private Camera movableCamera;
    private boolean paused;
    private TextureRegion background;
    private BitmapFont font;

    private Music music;
//    private Sound hitSound;

    private Map map;
    private Hero hero;
    private Monster monster;
    private Snow[] snowballs;
    private Bonus[] bonuses;
    private BulletEmitter bulletEmitter;
    private float noSnowInterval;
    private float infoTime;
    private Stage stage;
    private Button btnGameOver;

    public BulletEmitter getBulletEmitter() { return bulletEmitter; }
    public Map getMap() { return map; }

    GameScreen(SpriteBatch batch) {
        this.batch = batch;
    }

    private void restart() {
        map.restart();
        hero.restart();
        monster.restart();
        for (int i = 0; i < snowballs.length; i++) snowballs[i].respawn();
        Bonus.BonusType.refresh();
        for (int i = 0; i < bonuses.length; i++) bonuses[i].respawn();
        paused = false;
    }

    @Override
    public void show() {

        fixedCamera = new OrthographicCamera(ScreenManager.SCREEN_WIDTH, ScreenManager.SCREEN_HEIGHT);
        fixedCamera.position.set(ScreenManager.SCREEN_WIDTH / 2, ScreenManager.SCREEN_HEIGHT / 2, 0);
        fixedCamera.update();

        movableCamera = new OrthographicCamera(ScreenManager.SCREEN_WIDTH, ScreenManager.SCREEN_HEIGHT);
        movableCamera.position.set(ScreenManager.SCREEN_WIDTH / 2, 360, 0);

        this.font = Assets.getInstance().getAssetManager().get("medWhite.ttf", BitmapFont.class);
        this.background = Assets.getInstance().getAtlas().findRegion("background");

        map = new Map();

        hero = new Hero(this);
        monster = new Monster(this, hero);

        turnSnowOff(10.0f);
        snowballs = new Snow[30 * ScreenManager.NO_OF_SCREENS_IN_MAP];
        for (int i = 0; i < snowballs.length; i++) {
            snowballs[i] = new Snow();
            snowballs[i].respawn();
        }

        Bonus.BonusType.refresh();
        bonuses = new Bonus[4 * ScreenManager.NO_OF_SCREENS_IN_MAP];
        for (int i = 0; i < bonuses.length; i++) {
            bonuses[i] = new Bonus(this);
            bonuses[i].respawn();
        }

        this.bulletEmitter = new BulletEmitter();

        if(!ScreenManager.getInstance().isDesktop()) { createGUI(); }

        music = Gdx.audio.newMusic(Gdx.files.internal("xmas.wav"));
        music.setLooping(true);
        music.setVolume(0.05f);
        music.play();

//        this.hitSound = Gdx.audio.newSound(Gdx.files.internal("snowballHurts.wav"));

        this.infoTime = 5.0f;
        paused = false;
    }

    private void update(float dt) {

        hero.update(dt);
        monster.update(dt);

        // check collisions monster vs hero
        if (monster.isAlive()) {
            if (monster.getHitArea().contains(hero.getBottomCenterPoint())) hero.trampolineJump();
            else if (hero.isVisible() && hero.getHitArea().overlaps(monster.getHitArea())) {
                hero.takeDamage(monster.getDamage());
                monster.takeDamage(monster.getMaxHp());
            }
        }

        // bonuses - update and check collisions
        for (int i = 0; i < bonuses.length; i++) {
            bonuses[i].update(dt);
            if (hero.getHitArea().overlaps(bonuses[i].getHitArea())) bonuses[i].getUsedBy(hero);
            if (monster.getHitArea().overlaps(bonuses[i].getHitArea()))
                bonuses[i].getUsedBy(monster);
        }

        // snow - update and check collisions
        if (noSnowInterval < 0) Snow.setSnowing(true);
        noSnowInterval -= dt;
        for (int i = 0; i < snowballs.length; i++) {
            snowballs[i].update(dt);
            if (hero.isAlive() && hero.getHitArea().overlaps(snowballs[i].getHitArea())) {
                hero.takeDamage(Bullet.BulletType.SNOW.getDamage());
                snowballs[i].respawn();
            }
        }

        // bullets - update and check collisions
        bulletEmitter.update(dt);
        for (int i = 0; i < bulletEmitter.getActiveList().size(); i++) {
            Bullet b = bulletEmitter.getActiveList().get(i);
            if (map.tileNotPassable(b.getPosition().x, b.getPosition().y)) {
                b.deactivate();
            } else if (!b.isFriendsWeapon() && hero.getHitArea().contains(b.getPosition())) {
                hero.takeDamage(b.getType().getDamage());
                b.deactivate();
//                hitSound.play();
            } else if (b.isFriendsWeapon() && monster.getHitArea().contains(b.getPosition())) {
                monster.takeDamage(b.getType().getDamage());
                b.deactivate();
            }
        }
        bulletEmitter.checkPool();

        // gameplay update finished

    }

    public void turnSnowOff (float time) {
        noSnowInterval = time;
        Snow.setSnowing(false);
    }


    @Override
    public void render(float dt) {

        updateScreenGUI(dt);  // update screen gui always
        if(!paused) update(dt); // update all the rest only in case game is not paused

        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        TextureAtlas atlas = Assets.getInstance().getAtlas();

        batch.begin();

        // отображаем все элементы игровой карты на подвижной камере
        movableCamera.position.set(
                Math.min(Math.max(hero.getCenterX(), ScreenManager.SCREEN_WIDTH / 2),
                        ScreenManager.MAP_WIDTH - ScreenManager.SCREEN_WIDTH / 2), 360, 0);
        movableCamera.update();
        batch.setProjectionMatrix(movableCamera.combined);

        for (int i = 0; i < ScreenManager.MAP_WIDTH / ScreenManager.SCREEN_WIDTH; i++) {
            batch.draw(background,ScreenManager.SCREEN_WIDTH * i ,0);
        }

        map.render(batch);
        hero.render(batch);
        monster.render(batch);
        for (int i = 0; i < bonuses.length; i++) bonuses[i].render(batch);
        for (int i = 0; i < snowballs.length; i++) snowballs[i].render(batch);
        bulletEmitter.render(batch);

        // отображаем все элементы GUI на неподвижной камере
        batch.setProjectionMatrix(fixedCamera.combined);

        hero.showStats(batch, font);

        if ( noSnowInterval > 0 ) batch.setColor(1,1,1,1);
        else batch.setColor(0.5f,0.5f,0.5f,0.2f);
        batch.draw(atlas.findRegion("sunWithBG"), 400, 630, 80, 80);

        if (  hero.hasSuperBullets() ) batch.setColor(1,1,1,1);
        else batch.setColor(0.5f,0.5f,0.5f,0.2f);
        batch.draw(atlas.findRegion("cakeWithBG"), 500, 630, 80, 80);

        if (  hero.canSuperJump() ) batch.setColor(1,1,1,1);
        else batch.setColor(0.5f,0.5f,0.5f,0.2f);
        batch.draw(atlas.findRegion("deerWithBG"), 600, 630, 80, 80);

        if (  !hero.isVisible() ) batch.setColor(1,1,1,1);
        else batch.setColor(0.5f,0.5f,0.5f,0.2f);
        batch.draw(atlas.findRegion("hatWithBG"), 700, 630, 80, 80);

        if (  PlayerStats.getCurrentPlayer().getLevel(ShopScreen.PowerUpType.CLOVER) > 0 ) batch.setColor(1,1,1,1);
        else batch.setColor(0.5f,0.5f,0.5f,0.2f);
        batch.draw(atlas.findRegion("cloverWithBG"), 800, 630, 80, 80);

        batch.setColor(1,1,1,1);

        if ( ScreenManager.getInstance().isDesktop() ) {
            if ( infoTime > 0) {
                font.draw(batch, "arrows to move\nspace to jump\nalt to shoot\np to pause\nesc to escape", 1000, 620);
                infoTime -= dt;
            }
            if(!hero.isAlive()) {
                font.draw(batch, "Game over\nPress ENTER", 1100, 700);
            }
        }

        batch.end();

        // если игра запущена на Андроиде - отображаем экранные элементы управления
        if(!ScreenManager.getInstance().isDesktop()) {
            stage.draw();
        }
    }

    public float getMovableCameraX () {
        return movableCamera.position.x;
    }

    private void createGUI () {

        this.stage = new Stage(ScreenManager.getInstance().getViewport(), batch);
        Gdx.input.setInputProcessor(stage);

        createScreenGUI();
        hero.createPlayersGUI(stage);

    }

     private void createScreenGUI () {

         final Button btnPause = new Button(Assets.getInstance().getSkin().getDrawable("pause"));
         final Button btnPlay = new Button (Assets.getInstance().getSkin().getDrawable("play"));

         Button [] btnsPausePlay = {btnPause, btnPlay};
         for (int i = 0; i < btnsPausePlay.length; i++) {
             btnsPausePlay[i].setSize(80,80);
             btnsPausePlay[i].setPosition(1180, 630);
             btnsPausePlay[i].setRound(true);
             stage.addActor(btnsPausePlay[i]);
         }

         btnPlay.setVisible(false);

         btnPause.addListener(new ChangeListener() {
             @Override
             public void changed(ChangeEvent event, Actor actor) {
                 paused = true;
                 music.pause();
                 btnPause.setVisible(false);
                 btnPlay.setVisible(true);
             }
         });

         btnPlay.addListener(new ChangeListener() {
             @Override
             public void changed(ChangeEvent event, Actor actor) {
                 paused = false;
                 music.play();
                 btnPause.setVisible(true);
                 btnPlay.setVisible(false);
             }
         });

         btnGameOver = new TextButton ("Game Over", Assets.getInstance().getSkin());
         btnGameOver.setVisible(false);
         btnGameOver.setSize(360, 100);
         btnGameOver.setPosition(460,470);
         stage.addActor(btnGameOver);

         btnGameOver.addListener(new ChangeListener() {
             @Override
             public void changed(ChangeEvent event, Actor actor) {
                 ScreenManager.getInstance().switchScreenTo(ScreenManager.ScreenType.SHOP);
             }
         });

    }


    private void updateScreenGUI(float dt) {  // check user actions beyond gameplay

        if(ScreenManager.getInstance().isDesktop()) {

            if (Gdx.input.isKeyJustPressed(Input.Keys.P)) paused = !paused;
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
                ScreenManager.getInstance().switchScreenTo(ScreenManager.ScreenType.MENU);
            if (!hero.isAlive() && Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
                PlayerStats.updatePlayerStatsFile(PlayerStats.getCurrentPlayer());
                ScreenManager.getInstance().switchScreenTo(ScreenManager.ScreenType.SHOP);
            }
        }

        else {
            if(!hero.isAlive()) {
                btnGameOver.setVisible(true);
            }
            stage.act();
        }
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
        music.dispose();
        Gdx.input.setInputProcessor(null);
    }
}