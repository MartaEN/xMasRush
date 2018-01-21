package com.marta.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.marta.game.PlayerStats;

import static com.marta.game.PlayerStats.getCurrentPlayer;

public class ShopScreen implements Screen {

    private SpriteBatch batch;
    private TextureRegion background;
    private PowerUp[] powerups;
    private BitmapFont bigFont;
    private BitmapFont mediumFont;
    private BitmapFont smallFont;
    private Stage stage;
    private Skin skin;
    private final String labelToMenu = "back\nto\nmenu";
    private final String labelToGame = "new\ngame";
    private final String labelCost = "Upgrade for "+PlayerStats.costOf1stUpgrade+
            " / "+PlayerStats.costOf2ndUpgrade+
            " / "+PlayerStats.costOf3rdUpgrade+" credits";
    private StringBuilder labelBuilder;
    private String [] tipOfTheDay;
    private int tipNo;


    public enum PowerUpType {

        SUN("sunWithBG"),
        MEDKIT("medkitWithBG"),
        CAKE("cakeWithBG"),
        DEER ("deerWithBG"),
        HAT ("hatWithBG"),
        CLOVER ("cloverWithBG");

        private String drawable;

        PowerUpType(String drawable) { this.drawable = drawable; }
    }

    private class PowerUp {

        private PowerUpType powerUpType;
        private Group group;
        private Button button;
        private Image star1;
        private Image star2;
        private Image star3;

        Group getGroup () { return group; }

        PowerUp(PowerUpType powerUpType, float x, float y) {

            this.powerUpType = powerUpType;

            this.group = new Group();

            this.button = new Button (skin.getDrawable(powerUpType.drawable));
            button.setSize(120, 120);
            button.setRound(true);

            star1 = new Image(skin.getDrawable("star"));
            star2 = new Image(skin.getDrawable("star"));
            star3 = new Image(skin.getDrawable("star"));

            button.setPosition(25, 38);
            star1.setPosition(0,48);
            star2.setPosition(25, 18);
            star3.setPosition(62, 0);

            star1.setRotation(-20);
            star3.setRotation(25);

            group.addActor(button);
            group.addActor(star1);
            group.addActor(star2);
            group.addActor(star3);

            group.setPosition(x,y);

            final PowerUpType type = powerUpType;

            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    PlayerStats.getCurrentPlayer().levelup(type);
                }
            });
        }

        void refreshStars () {
            star1.setColor(1, 1, 1, 0.2f);
            star2.setColor(1, 1, 1, 0.2f);
            star3.setColor(1, 1, 1, 0.2f);
            switch (getCurrentPlayer().getLevel(this.powerUpType)) {
                case 3: star3.setColor(1, 1, 1, 1);
                case 2: star2.setColor(1, 1, 1, 1);
                case 1: star1.setColor(1, 1, 1, 1);
            }
        }
    }

    ShopScreen(SpriteBatch batch) {
        this.batch = ScreenManager.getInstance().getBatch();
        this.tipOfTheDay = new String[15];
        tipOfTheDay [0] = "never ever hug with yetis";
        tipOfTheDay [1] = "yetis just want some food";
        tipOfTheDay [2] = "snow hurts you, try to avoid it";
        tipOfTheDay [3] = "you can become invisible with the hat bonus";
        tipOfTheDay [4] = "lucky clover bonus is only valid for one game";
        tipOfTheDay [5] = "clover bonus makes you lucky";
        tipOfTheDay [6] = "yetis love carrots and carrot cakes";
        tipOfTheDay [7] = "you can stop snow with the sun bonus";
        tipOfTheDay [8] = "jump on a yeti's head? at your own risk...";
        tipOfTheDay [9] = "what is luck? more bonuses on the screen!!!";
        tipOfTheDay [10] = "upgrade your medkit to get more heal points";
        tipOfTheDay [11] = "let yeti run on a cake bonus...";
        tipOfTheDay [12] = "you get score points for yetis you feed";
        tipOfTheDay [13] = "snow hurts you even when you are invisible";
        tipOfTheDay [14] = "yetis come to us from the skies";
    }

    @Override
    public void show() {

        this.background = Assets.getInstance().getAtlas().findRegion("background");

        this.bigFont = Assets.getInstance().getAssetManager().get("maxWhite.ttf", BitmapFont.class);
        this.mediumFont = Assets.getInstance().getAssetManager().get("medWhite.ttf", BitmapFont.class);
        this.smallFont = Assets.getInstance().getAssetManager().get("minWhite.ttf", BitmapFont.class);

        this.skin = new Skin();
        skin.addRegions(Assets.getInstance().getAtlas());
        ImageButton.ImageButtonStyle buttonStyle = new ImageButton.ImageButtonStyle();
        skin.add("default", buttonStyle);

        tipNo = (int) ( Math.random() * tipOfTheDay.length ) ;

        createGUI();
    }

    private void createGUI () {

        this.stage = new Stage(ScreenManager.getInstance().getViewport(), batch);
        Gdx.input.setInputProcessor(stage);

        this.powerups = new PowerUp[6];
        powerups[0] = new PowerUp(PowerUpType.SUN, 155, 40);
        powerups[1] = new PowerUp(PowerUpType.MEDKIT , 315, 70);
        powerups[2] = new PowerUp(PowerUpType.CAKE, 495, 65);
        powerups[3] = new PowerUp(PowerUpType.DEER, 665, 35);
        powerups[4] = new PowerUp(PowerUpType.HAT, 835, 10);
        powerups[5] = new PowerUp(PowerUpType.CLOVER, 1005, 5);

        for (int i = 0; i < powerups.length; i++) {
            stage.addActor(powerups[i].getGroup());
        }

        Button btnMenu = new Button(skin.getDrawable("signToTheLeft"));
        Button btnGame = new Button(skin.getDrawable("signToTheRight"));
        btnMenu.setPosition(20, 210);
        btnGame.setPosition(1180, 175);
        btnMenu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ScreenManager.getInstance().switchScreenTo(ScreenManager.ScreenType.MENU); }
        });
        btnGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ScreenManager.getInstance().switchScreenTo(ScreenManager.ScreenType.GAME); }
        });
        stage.addActor(btnMenu);
        stage.addActor(btnGame);

        labelBuilder = new StringBuilder();
    }

    private void update (float dt) {
        for (int i = 0; i < powerups.length; i++) { powerups[i].refreshStars(); }
        stage.act(dt);
    }

    @Override
    public void render(float delta) {

        update(delta);

        batch.begin();
        batch.draw(background, 0,0);

        smallFont.draw(batch, labelCost, 270, 50);
        smallFont.draw(batch, labelToMenu, 30, 200);
        smallFont.draw(batch, labelToGame, 1190,165);

        labelBuilder.setLength(0);
//        labelBuilder.append("Hello, ").append(PlayerStats.getCurrentPlayer().getName()).append("!");
        labelBuilder.append("Take a break...");
        bigFont.draw(batch, labelBuilder, 180, 650);

        smallFont.draw(batch, "tip of the day:", 180,500);
        mediumFont.draw(batch, tipOfTheDay[tipNo], 180,450);

        smallFont.draw(batch, "more BONUSES for your score points here ! ", 180, 320);
        smallFont.draw(batch, "your credits: ", 680, 300);
        mediumFont.draw(batch, (""+PlayerStats.getCurrentPlayer().getCredits()), 850, 297);

        labelBuilder.setLength(0);
        labelBuilder.append("SOME STATS: \nyour high score : ")
                .append(PlayerStats.getCurrentPlayer().getHighScore())
                .append("\ntotal xMas presents \ncollected: ")
                .append(PlayerStats.getCurrentPlayer().getTotalGiftCount())
                .append("\ntotal hungry \nYetis fed: "+PlayerStats.getCurrentPlayer().getTotalKillCount());
        smallFont.draw(batch, labelBuilder,950, 630);

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
        stage.dispose();
        skin.dispose();
        Gdx.input.setInputProcessor(null);
    }
}
