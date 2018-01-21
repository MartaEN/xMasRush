package com.marta.game.stuff.bullets;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class BulletEmitter extends ObjectPool<Bullet> {

    @Override
    protected Bullet newObject() { return new Bullet(); }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < activeList.size(); i++) {
            batch.draw(activeList.get(i).getType().getTexture(),
                    activeList.get(i).getPosition().x - 24,
                    activeList.get(i).getPosition().y - 24,
                    24, 24,
                    48, 48,
                    0.7f, 0.7f,
                    activeList.get(i).getRotation());
        }
    }

    public void update(float dt) {
        for (int i = 0; i < activeList.size(); i++) activeList.get(i).update(dt);
    }

    public void setup(Bullet.BulletType type, boolean isEnemyBullet, float x, float y, boolean dirRight) {
        Bullet b = getActiveElement();
        b.activate(type, isEnemyBullet, x, y, dirRight);
    }
}
