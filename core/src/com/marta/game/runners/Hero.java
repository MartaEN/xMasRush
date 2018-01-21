package com.marta.game.runners;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.marta.game.PlayerStats;
import com.marta.game.screens.Assets;
import com.marta.game.screens.ScreenManager;
import com.marta.game.stuff.bullets.Bullet;
import com.marta.game.screens.GameScreen;

public class Hero extends Dude {

    private boolean scoreProcessed;
    private int killCount;
    private int giftCount;
    private StringBuilder labelStats;
    private Button btnLeft;
    private Button btnRight;
    private Button btnJump;
    private Button btnFire;

    public Hero(GameScreen gameScreen) {
        super(true, gameScreen,
                "snowmanWalking", 64,
                "snowmanIdle", 64,
                "snowmanDead", 117,
                100,
                240.0f, 400.0f,100,
                0.2f, Bullet.BulletType.CARROT, Bullet.BulletType.CAKE
                ,"ding.wav","fade.wav", "woosh2.wav"
        );
        this.labelStats = new StringBuilder();
        restart();
    }

    @Override
    public void restart() {
        stdRestart();
        this.position = new Vector2(220 - 32, screen.getMap().nearestFloor(220,300));
        this.velocity = new Vector2(0,0);
        killCount = 0;
        giftCount = 0;
        scoreProcessed = false;
    }

    @Override
    void act(float dt) {

        // эти кнопки оставлены всегда активными для удобства тестирования андроида на десктопе
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) moveRight();
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) moveLeft();

        if(ScreenManager.getInstance().isDesktop()) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) jump();
            if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) fire();
            if (!Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) discardFireInterval();
        }

        else {
            if (btnLeft.isPressed()) moveLeft();
            if (btnRight.isPressed()) moveRight();
            if (btnJump.isPressed()) jump();
            if (btnFire.isPressed()) fire();
            if (!btnFire.isPressed()) discardFireInterval();
        }
    }

    @Override
    void exit(float dt) {
        if(!scoreProcessed) {
            becomeVisible();
            PlayerStats.getCurrentPlayer().checkIfHighScore(score);
            PlayerStats.getCurrentPlayer().addCredits(score);
            PlayerStats.getCurrentPlayer().addKillCount(killCount);
            PlayerStats.getCurrentPlayer().addGiftCount(giftCount);
            PlayerStats.getCurrentPlayer().discardLuck();
            scoreProcessed = true;
        }
    }

    public void showStats(SpriteBatch batch, BitmapFont font) {
        labelStats.setLength(0);
        labelStats.append("HP: ").append(hp).append(" / ").append(maxHp).append("\nSCORE: ").append(score);
        font.draw(batch, labelStats, 20, 700);
    }

    public void killCount () { ++killCount; }

    public void giftCount () { ++giftCount; }

    public void createPlayersGUI(Stage stage) {

        btnLeft = new Button(Assets.getInstance().getSkin().getDrawable("dirLeftWithBG"));
        btnRight = new Button(Assets.getInstance().getSkin().getDrawable("dirRightWithBG"));
        btnJump = new Button(Assets.getInstance().getSkin().getDrawable("dirUpWithBG"));
        btnFire = new Button(Assets.getInstance().getSkin().getDrawable("carrotWithBG"));

        int btnSize = 120;
        int sideMargin = 20;
        btnLeft.setPosition(sideMargin, 250);
        btnRight.setPosition(sideMargin, 90);
        btnJump.setPosition(ScreenManager.SCREEN_WIDTH - btnSize - sideMargin, 250);
        btnFire.setPosition(ScreenManager.SCREEN_WIDTH - btnSize - sideMargin, 90);

        Button[] actionButtons = {btnLeft, btnRight, btnJump, btnFire};
        for (int i = 0; i < actionButtons.length; i++) {
            actionButtons[i].setSize(btnSize, btnSize);
            actionButtons[i].setRound(true);
            stage.addActor(actionButtons[i]);
        }
    }
}
