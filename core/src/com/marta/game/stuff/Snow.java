package com.marta.game.stuff;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.marta.game.screens.Assets;
import com.marta.game.screens.ScreenManager;

public class Snow {
    private Vector2 position;
    private Vector2 velocity;
    private TextureRegion texture;
    private static final int RADIUS = 32;
    private Rectangle hitArea;
    private float scale;
    private float angle;
    private static boolean snowing;

    public Rectangle getHitArea() { return hitArea; }
    public static void setSnowing (boolean snowing) {Snow.snowing = snowing;}

    public Snow() {
        this.texture = Assets.getInstance().getAtlas().findRegion("snow");
        this.position = new Vector2(0, 0);
        this.velocity = new Vector2(0, 0);
        this.hitArea = new Rectangle(0,0,0,0);
        this.scale = 1.0f;
        setHitArea();
    }

    private void setHitArea () {
        hitArea.set(position.x - 0.8f * RADIUS * scale, position.y - 0.8f * RADIUS * scale,
                1.6f * RADIUS * scale, 1.6f * RADIUS * scale);
    }

    public void respawn() {
        if(snowing) position.x = MathUtils.random(0, ScreenManager.MAP_WIDTH);
        else position.x = -100;
        position.y = MathUtils.random(1500, 5000);
        velocity.set(0, -500.0f);
        scale = MathUtils.random(0.6f, 1.5f);
        setHitArea();
        angle = MathUtils.random(0, 360);
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x - RADIUS, position.y - RADIUS, RADIUS, RADIUS,
                2 * RADIUS, 2 * RADIUS, scale, scale, angle);
    }

    public void update(float dt) {
        position.mulAdd(velocity, dt);
        if (position.y < -100) respawn();
        if(!snowing && position.y > 720) respawn();
        setHitArea();
    }
}
