package com.marta.game.runners;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.marta.game.screens.Assets;
import com.marta.game.stuff.bullets.Bullet;
import com.marta.game.screens.GameScreen;

public abstract class Dude {

    GameScreen screen;
    private TextureRegion[] regionsIdle;
    private TextureRegion [] regionsWalking;
    private TextureRegion [] regionsDead;
    Vector2 position;
    Vector2 velocity;
    int maxHp;
    int hp;
    private float superJumpTime;
    private float timeToVisible;
    private float hurtAndBluish;
    private int superBullets;
    private Bullet.BulletType normalBullet;
    private Bullet.BulletType superBullet;
    int score;
    private final int HEIGHT;
    private final int WIDTH;
    final float STD_VEL_X;
    final float STD_VEL_Y;
    float rotation;
    private final int TILE_SIZE;
    private final int SIDE_MARGIN;
    private final int TOP_MARGIN;
    private Rectangle hitArea;
    private float time;
    private int frameDead;
    boolean dirRight;
    private final float FIRE_INTERVAL; // частота выстрелов (для оружия дальнего боя)
    private float timeToNextFire;
    private boolean friend;
    private boolean exitProcessed;
    private Sound entranceSound;
    private Sound exitSound;
//    private Sound jumpSound;
    private Sound fireSound;

    public float getCenterX () {return position.x; }
    public Rectangle getHitArea() { return hitArea; }
    public Vector2 getBottomCenterPoint () {return new Vector2(position.x, position.y + HEIGHT / 8);}
    public int getMaxHp() {return maxHp;}

    Dude(boolean friend, GameScreen screen,
         String pictureWalking, int widthWalking,
         String pictureIdle, int widthIdle,
         String pictureDead, int widthDead,
         int height,
         float velX, float velY,
         int maxHP, float fireInterval,
         Bullet.BulletType normalBullet, Bullet.BulletType superBullet
         , String entranceSound
         , String exitSound
//         , String jumpSound,
         , String fireSound
        ){
        this.friend = friend;
        this.screen = screen;
        this.WIDTH = widthWalking;
        this.HEIGHT = height;
        this.STD_VEL_X = velX;
        this.STD_VEL_Y = velY;
        this.TILE_SIZE = screen.getMap().getTILE_SIZE();
        this.SIDE_MARGIN = WIDTH > TILE_SIZE ? (WIDTH - TILE_SIZE) / 2 : 0;
        this.TOP_MARGIN = HEIGHT / 4;
        this.position = new Vector2(0,0);
        this.velocity = new Vector2(0,0);
        this.hitArea = new Rectangle(0,0,0,0);
        setHitArea();
        this.regionsWalking = new TextureRegion(Assets.getInstance().getAtlas().findRegion(pictureWalking)).split(widthWalking,HEIGHT)[0];
        this.regionsIdle = new TextureRegion(Assets.getInstance().getAtlas().findRegion(pictureIdle)).split(widthIdle,HEIGHT)[0];
        this.regionsDead = new TextureRegion(Assets.getInstance().getAtlas().findRegion(pictureDead)).split(widthDead,HEIGHT)[0];
        this.maxHp = maxHP;
        this.FIRE_INTERVAL = fireInterval;
        this.normalBullet = normalBullet;
        this.superBullet = superBullet;
        this.entranceSound = Gdx.audio.newSound(Gdx.files.internal(entranceSound));
        this.exitSound = Gdx.audio.newSound(Gdx.files.internal(exitSound));
//        this.jumpSound = Gdx.audio.newSound(Gdx.files.internal(jumpSound));
        this.fireSound = Gdx.audio.newSound(Gdx.files.internal(fireSound));

        stdRestart();
    }

    void stdRestart () {
        this.hp = maxHp;
        this.score = 0;
        this.superBullets = 0;
        this.superJumpTime = 0.0f;
        this.timeToVisible = 0.0f;
        this.hurtAndBluish = 0.0f;
        this.frameDead = 0;
        this.dirRight = true;
        this.rotation = 0.0f;
        this.exitProcessed = false;
        entranceSound.play(0.4f);
    }

    public abstract void restart();

    private void setHitArea () {
        hitArea.set(position.x - WIDTH/6, position.y, WIDTH/3, HEIGHT * 2 / 3);
    }

    public void update (float dt) {

        if(isAlive()) act(dt);
        else if(!exitProcessed) prepareExit();
        else exit(dt);

        screen.getMap().unblockPlace(position);

        position.mulAdd(velocity, dt);

        checkWallCollisions();

        screen.getMap().blockPlace(position);

        setHitArea();

        velocity.y -= 500.0f * dt;
        velocity.x *= 0.6f;

        hurtAndBluish -= dt;
        hurtAndBluish = hurtAndBluish < 0 ? 0 : hurtAndBluish;

        time += dt;
        timeToNextFire -= dt;
        superJumpTime -= dt;
        timeToVisible -= dt;
    }

    private void checkWallCollisions() {

        // проверка пола внизу
        if(screen.getMap().tileNotPassable(position.x - (WIDTH / 2 - SIDE_MARGIN - 3f), position.y ) ||
                screen.getMap().tileNotPassable(position.x + (WIDTH / 2 - SIDE_MARGIN - 3f), position.y ) ) {
            position.y = (position.y + TILE_SIZE) - (position.y + TILE_SIZE) % TILE_SIZE;
            velocity.y = 0f;
        }

        //проверка стены слева
        if(anyVerticalWall(position.x - WIDTH / 2 + SIDE_MARGIN, position.y) ) {
            float xLeftWall = (position.x - WIDTH / 2 + SIDE_MARGIN + TILE_SIZE) - (position.x - WIDTH / 2 + SIDE_MARGIN + TILE_SIZE) % TILE_SIZE;
            if(xLeftWall > position.x - ( WIDTH / 2 - SIDE_MARGIN ) ) {
                position.x = xLeftWall + ( WIDTH / 2 - SIDE_MARGIN + 1f );
                velocity.x = 0f;
            }
        }
        // проверка стены справа
        if(anyVerticalWall(position.x + WIDTH / 2 - SIDE_MARGIN, position.y)) {
            float xRightWall = (position.x + WIDTH / 2 - SIDE_MARGIN) - (position.x + WIDTH / 2 - SIDE_MARGIN) % TILE_SIZE;
            if(position.x + ( WIDTH / 2 - SIDE_MARGIN ) > xRightWall) {
                position.x = xRightWall - ( WIDTH / 2 - SIDE_MARGIN +  1f );
                velocity.x = 0f;
            }
        }

        // проверка потолка вверху
        if(isAlive() && ( screen.getMap().tileNotPassable(position.x - WIDTH / 2 + SIDE_MARGIN, position.y + HEIGHT - TOP_MARGIN) ||
                             screen.getMap().tileNotPassable(position.x + WIDTH / 2 - SIDE_MARGIN, position.y + HEIGHT - TOP_MARGIN)     )) {
            float yCeiling = (position.y + HEIGHT - TOP_MARGIN) - (position.y + HEIGHT - TOP_MARGIN) % TILE_SIZE;
            if(position.y + HEIGHT - TOP_MARGIN > yCeiling) {
                position.y = yCeiling - (HEIGHT - TOP_MARGIN);
                velocity.y = 0f;
            }
        }
    }

    private boolean anyVerticalWall (float x, float y) {
        for (int i = 1; i <= HEIGHT - TOP_MARGIN; i += 5) {
            if(screen.getMap().tileNotPassable(x,y + i)) return true;
        }
        return false;
    }

    abstract void act(float dt);

    abstract void exit (float dt);

    void prepareExit () {
        exitSound.play(0.5f);
        exitProcessed = true;
    }

    void moveRight() {
        velocity.x = STD_VEL_X;
        dirRight = true;
    }

    void moveLeft() {
        velocity.x = -STD_VEL_X;
        dirRight = false;
    }

    void jump () {
        if(position.y == screen.getMap().nearestFloor(position.x - (WIDTH / 2 - SIDE_MARGIN) , position.y)
                || position.y == screen.getMap().nearestFloor(position.x + (WIDTH / 2 - SIDE_MARGIN), position.y)) {
            velocity.y = STD_VEL_Y;
            if(canSuperJump()) velocity.y *=1.3f;
        }
    }

    void fire () {
        if (timeToNextFire <= 0) {
            screen.getBulletEmitter().setup(chooseBullet(), friend, position.x, position.y + HEIGHT / 2, dirRight);
            fireSound.play(0.3f);
            timeToNextFire = FIRE_INTERVAL;
        }
    }

    private Bullet.BulletType chooseBullet () {
        if (superBullets > 0 ) {
            superBullets--;
            return superBullet;
        }
        return normalBullet;
    }

    void discardFireInterval () {
        timeToNextFire = 0;
    }

    public void gainScore (int score) { this.score += score; }
    public void takeDamage (int damage) {
        hurtAndBluish += 0.3f;
        hp = hp > damage ? hp - damage: 0; }
    public void becomeInvisible(float time) { timeToVisible = time;}
    void becomeVisible() { timeToVisible = 0;}
    public void heal(int hp) {this.hp = Math.min (this.hp + hp, maxHp);}
    public void gainSuperBullets (int bullets) {this.superBullets = bullets; }
    public void gainSuperJump(float time) {this.superJumpTime = time; }
    public void trampolineJump () {
        velocity.y = STD_VEL_Y;
        if(canSuperJump()) velocity.y *= 1.3f;
    }


    public boolean isAlive() { return hp > 0; }
    public boolean isVisible () {return timeToVisible <= 0; }
    public boolean canSuperJump () { return  superJumpTime > 0; }
    public boolean hasSuperBullets() {return superBullets > 0;}

    public void render (SpriteBatch batch) {

        TextureRegion currentView;

        if (isAlive()) {
            int frame = (int) (time * 20.0f);
            if (Math.abs(velocity.x) < 0.01f) {
                currentView = regionsIdle[frame % regionsIdle.length];
            } else currentView = regionsWalking[frame % regionsWalking.length];
        } else {
            currentView = regionsDead[frameDead];
            if (frameDead < regionsDead.length - 1) frameDead++;
        }

        if (!dirRight && !currentView.isFlipX()) currentView.flip(true, false);
        if (dirRight && currentView.isFlipX()) currentView.flip(true, false);

        float transparency = timeToVisible <= 0 ? 0.0f :
                timeToVisible < 1.0f ? 0.7f * timeToVisible : 0.7f;

        batch.setColor(1- hurtAndBluish,1- hurtAndBluish,1 ,1 - transparency);
        batch.draw(currentView,
                position.x - currentView.getRegionWidth() / 2, position.y,
                currentView.getRegionWidth() / 2, currentView.getRegionHeight() / 2,
                currentView.getRegionWidth(), currentView.getRegionHeight(),
                1.0f, 1.0f,
                rotation);

        batch.setColor(1,1,1,1);
    }
}