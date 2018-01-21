package com.marta.game.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.marta.game.MerryChristmas;

public class ScreenManager {

    public static final int SCREEN_WIDTH = 1280;
    public static final int SCREEN_HEIGHT = 720;
    public static final int NO_OF_SCREENS_IN_MAP = 2;
    public static final int MAP_WIDTH = NO_OF_SCREENS_IN_MAP * SCREEN_WIDTH;
    public static boolean desktop;

    public enum ScreenType {
        MENU, GAME, SHOP
    }
    private MerryChristmas game;
    private SpriteBatch batch;
    private Viewport viewport;
    private LoadingScreen loadingScreen;
    private GameScreen gameScreen;
    private MenuScreen menuScreen;
    private ShopScreen shopScreen;
    private Screen targetScreen;
    private Music music;

    private ScreenManager() {}
    private static final ScreenManager ourInstance = new ScreenManager();
    public static ScreenManager getInstance () {return ourInstance;}
    public SpriteBatch getBatch () { return this.batch; }
    public Viewport getViewport () { return this.viewport;}
    public boolean isDesktop () {return  desktop;}

    public void init (MerryChristmas game) {
        this.game = game;
        this.desktop = Gdx.app.getType()== Application.ApplicationType.Desktop;
//        this.desktop = false; // uncomment this for ANDROID TEST MODE
        this.batch = new SpriteBatch();
        this.viewport = new FitViewport(ScreenManager.SCREEN_WIDTH, ScreenManager.SCREEN_HEIGHT);
        this.viewport.apply();
        this.loadingScreen = new LoadingScreen(batch);
        this.gameScreen = new GameScreen(batch);
        this.menuScreen = new MenuScreen(batch);
        this.shopScreen = new ShopScreen(batch);

        Assets.getInstance().loadAssets();

        music = Gdx.audio.newMusic(Gdx.files.internal("menu.mp3"));
        music.setLooping(true);
        music.setVolume(0.1f);
        music.play();
    }

    // закомментированные строки - для варианта, когда разные экраны пользуются разными ресурсами
    public void switchScreenTo (ScreenType type) {

        Screen currentScreen = game.getScreen();
        if (currentScreen != null ) currentScreen.dispose();

        switch (type) {
            case MENU:
                targetScreen = menuScreen;
//                Assets.getInstance().loadAssets(ScreenType.MENU);
                game.setScreen(loadingScreen);
                music.play();
                break;
            case GAME:
                targetScreen = gameScreen;
//                Assets.getInstance().loadAssets(ScreenType.GAME);
                game.setScreen(loadingScreen);
                music.stop();
                break;
            case SHOP:
                targetScreen = shopScreen;
//                Assets.getInstance().loadAssets(ScreenType.SHOP);
                game.setScreen(loadingScreen);
                music.play();
                break;
        }
    }

    void goToTargetScreen () {
        game.setScreen(targetScreen);
        targetScreen = null;
    }

    public void onResize (int width, int height) {
        viewport.update(width,height,true);
        viewport.apply();
    }

    public void dispose() {
        Assets.getInstance().dispose();
        music.dispose();
        game.getScreen().dispose();
        batch.dispose();
    }
}
