package com.marta.game.stuff.bullets;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.marta.game.screens.Assets;
import com.marta.game.screens.ScreenManager;

public class Bullet implements Poolable {

    private BulletType type;
    private boolean isFriendsWeapon;
    private Vector2 position;
    private Vector2 velocity;
    private float rotation;
    private boolean active;

    public static enum BulletType {
        SNOW ("snow", 5), CARROT("carrot", 20), CAKE("cakepiece", 50);

        private TextureRegion texture;
        private int damage;

        public TextureRegion getTexture() { return texture; }
        public int getDamage() { return damage; }

        BulletType (String textureName, int damage) {
            this.texture = Assets.getInstance().getAtlas().findRegion(textureName);
            this.damage = damage;
        }

    }

    public BulletType getType() { return type;}
    public boolean isFriendsWeapon() { return isFriendsWeapon; }
    public Vector2 getPosition() { return position; }
    public float getRotation() { return rotation; }
    public boolean isActive() { return active; }

    public Bullet() {
        this.position = new Vector2(0.0f, 0.0f);
        this.velocity = new Vector2(0.0f, 0.0f);
        this.active = false;
    }

    public void deactivate() { this.active = false; }

    public void activate(BulletType type, boolean isFriendsWeapon, float x, float y, boolean dirRight) {
        this.type = type;
        this.isFriendsWeapon = isFriendsWeapon;
        position.set(x, y);
        if(dirRight) velocity.set(600.0f, 100.0f);
        else velocity.set(-600.0f, 100.0f);
        rotation = 0.0f;
        active = true;
    }

    public void update(float dt) {
        position.mulAdd(velocity, dt);
        if (position.x > ScreenManager.MAP_WIDTH || position.x < 0 || position.y < 0 || position.y > ScreenManager.SCREEN_HEIGHT) {
            deactivate();
        }
        rotation += 500.0f * dt;
        velocity.y -= 500.0f * dt;
    }
}
