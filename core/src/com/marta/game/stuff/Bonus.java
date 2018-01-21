package com.marta.game.stuff;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.marta.game.runners.Monster;
import com.marta.game.screens.Assets;
import com.marta.game.screens.GameScreen;
import com.marta.game.PlayerStats;
import com.marta.game.runners.Hero;
import com.marta.game.screens.ShopScreen;
import com.marta.game.stuff.bullets.Bullet;

public class Bonus {

    private Vector2 position;
    private final int SIZE;
    private float initY;
    private float time;
    private Rectangle hitArea;
    private BonusType type;
    private GameScreen screen;
    private static Sound bonusPickup;

    static {bonusPickup = Gdx.audio.newSound(Gdx.files.internal("ding.wav"));}

    public enum BonusType {

        SCORE_GOLD (30.0f, "gift1", 15, 1),
        SCORE_SILVER (30.0f, "gift2", 10, 1),
        SCORE_BRONZE (30.0f, "gift3", 5, 1),
        NO_SNOW(10.0f, "sun", 10, 1),
        HEAL(10.0f, "medkit", 30, 0),
        SUPER_BULLETS(10.0f, "cake", 2, 0),
        SUPER_JUMP (10.0f, "deer", 10, 0),
        INVISIBILITY (10.0f, "hat", 10, 0);


        private TextureRegion texture;
        private final int VALUE;
        private float LIFETIME;
        private int level;

        BonusType(float lifetime, String pngName, int value,  int level) {
            this.texture = Assets.getInstance().getAtlas().findRegion(pngName);
            this.VALUE = value;
            this.LIFETIME = lifetime;
            this.level = level;
        }

        public static void refresh () {
            try {
                NO_SNOW.level = PlayerStats.getCurrentPlayer().getLevel(ShopScreen.PowerUpType.SUN);
                HEAL.level = PlayerStats.getCurrentPlayer().getLevel(ShopScreen.PowerUpType.MEDKIT);
                SUPER_BULLETS.level = PlayerStats.getCurrentPlayer().getLevel(ShopScreen.PowerUpType.CAKE);
                SUPER_JUMP.level = PlayerStats.getCurrentPlayer().getLevel(ShopScreen.PowerUpType.DEER);
                INVISIBILITY.level = PlayerStats.getCurrentPlayer().getLevel(ShopScreen.PowerUpType.HAT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Rectangle getHitArea() { return hitArea; }

    public Bonus(GameScreen screen) {
        this.screen = screen;
        this.SIZE = 60;
        this.type = BonusType.SCORE_BRONZE;
        this.position = new Vector2(-100, -100);
        this.hitArea = new Rectangle(0,0,0,0 );
        setHitArea();
    }

    public void respawn () {

        int chance = MathUtils.random(1, 15 + 3 * PlayerStats.getCurrentPlayer().getLevel(ShopScreen.PowerUpType.CLOVER));

        if (chance <= 8) type = BonusType.SCORE_BRONZE;
        else if (chance <= 12) type = BonusType.SCORE_SILVER;
        else if(chance <=14) type = BonusType.SCORE_GOLD;
        else {
            chance = MathUtils.random(1,5);
            switch (chance) {
                case 1: type = BonusType.INVISIBILITY;
                        if(type.level > 0) break;
                case 2: type = BonusType.SUPER_JUMP;
                        if(type.level > 0) break;
                case 3: type = BonusType.SUPER_BULLETS;
                        if(type.level > 0) break;
                case 4: type = BonusType.HEAL;
                        if(type.level > 0) break;
                case 5: type = BonusType.NO_SNOW;
            }
        }

        position = screen.getMap().getRandomEmptyFloorTile();
        screen.getMap().blockPlace(position);
        initY = position.y;
        time = type.LIFETIME;
        setHitArea();

    }

    private void setHitArea () {
        hitArea.set(position.x  - 0.4f * SIZE, position.y, 0.8f * SIZE, 0.8f * SIZE);
    }

    public void getUsedBy (Hero hero) {
        switch (type) {
            case SCORE_GOLD:
            case SCORE_SILVER:
            case SCORE_BRONZE:
                hero.gainScore(type.VALUE);
                hero.giftCount();
                break;
            case NO_SNOW:
                screen.turnSnowOff((float)BonusType.NO_SNOW.VALUE * BonusType.NO_SNOW.level);
                break;
            case HEAL:
                hero.heal( BonusType.HEAL.VALUE * BonusType.HEAL.level);
                break;
            case SUPER_BULLETS:
                hero.gainSuperBullets(BonusType.SUPER_BULLETS.VALUE * BonusType.SUPER_BULLETS.level);
                break;
            case SUPER_JUMP:
                hero.gainSuperJump((float)BonusType.SUPER_JUMP.VALUE * BonusType.SUPER_JUMP.level);
                break;
            case INVISIBILITY:
                hero.becomeInvisible((float)BonusType.INVISIBILITY.VALUE * BonusType.INVISIBILITY.level);
        }
        bonusPickup.play(0.5f);
        screen.getMap().unblockPlace(position.x, initY);
        respawn();
    }

    public void getUsedBy (Monster monster) {
        switch (type) {
            case SUPER_BULLETS:
                monster.takeDamage(BonusType.SUPER_BULLETS.VALUE * Bullet.BulletType.CAKE.getDamage());
                break;
        }
        screen.getMap().unblockPlace(position.x, initY);
        respawn();
    }

    public void update (float dt) {
        time -= dt;
        position.y = initY + 10 * (float)Math.sin(time * 3.0f);
        if (time < 0) respawn();
    }

    public void render (SpriteBatch batch) {
        if( time < 1.0f ) batch.setColor( time, time, time, time);
        batch.draw(type.texture, position.x - SIZE / 2, position.y);
        batch.setColor(1,1,1,1);
    }
}
