package com.marta.game.runners;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.marta.game.screens.ScreenManager;
import com.marta.game.stuff.bullets.Bullet;
import com.marta.game.screens.GameScreen;

public class Monster extends Dude {

    private Hero target; // за кем гоняется монстр
    private float meleeDistance; // расстояние до героя, при котором монстр переходит с оружия дальнего боя на оружие ближнего боя
    private int meleeDamage; // урон, наносимый монстром в ближнем бою
    private float wanderingTime; // вспомогательный счетчик времени
    private final float MONSTER_INTERVAL; // плановое время до появления нового монстра после победы над предыдущим
    private float timeToNextMonster; // счетчик времени до появления нового монстра

    public int getDamage () {return meleeDamage;}

    public Monster (GameScreen gameScreen, Hero target) {
        super(false, gameScreen,
                "monsterWalking", 70,
                "monsterWalking", 70,
                "monsterWalking", 70,
                100,
                80.0f, 350.0f,100,
                2.0f, Bullet.BulletType.SNOW, Bullet.BulletType.SNOW
                , "laughter.wav","blip.wav", "woosh1.wav"
        );
        this.target = target;
        this.meleeDistance = 200.0f;
        this.meleeDamage = target.getMaxHp();
        this.MONSTER_INTERVAL = 10.0f;
        restart();
    }

    @Override
    public void restart() {
        if (target.isAlive()) {
            stdRestart();
            this.wanderingTime = 0f;
            this.timeToNextMonster = MONSTER_INTERVAL;
            this.position = new Vector2(MathUtils.random( screen.getMovableCameraX() - ScreenManager.SCREEN_WIDTH / 2 + 50,
                    screen.getMovableCameraX() + ScreenManager.SCREEN_WIDTH / 2 - 50), ScreenManager.SCREEN_HEIGHT + 50);
            this.velocity = new Vector2(0.0f, 0.0f);
        }
    }

    @Override
    void act(float dt) {

        if(!target.isAlive()) { // если враг погиб - уходи
            exit(dt);
            return;
        }

        if (velocity.x == 0.0f) { // если застрял - в любом случае для начала подпрыгни
            jump();
        }

        if(target.isVisible()) { // если видишь врага:
            if (target.getCenterX() > position.x + 2f) { // если цель справа - беги направо
                moveRight();
            } else if (target.getCenterX() < position.x - 2f) { // если цель слева - беги налево
                moveLeft();
            } else {  // если цель прямо под или над тобой - беги на месте (минимальная скорость, чтобы не прыгал где не надо)
                velocity.x = 1.0f;
                dirRight = true;
            }
            // и стреляй, если ты еще не приблизился ко врагу на дистанцию для ближнего боя
            if(Math.abs(target.getCenterX() - position.x) > meleeDistance) fire();

        } else { // если враг в режиме невидимки - ходи в растерянности вправо - влево
            velocity.x = STD_VEL_X / 2;
            if (wanderingTime <= 0) {
                dirRight = !dirRight;
                wanderingTime = 2.0f;
            }
            if(!dirRight) velocity.x *= -1;
        }

        wanderingTime -= dt;
    }

    @Override
    void exit(float dt) {

        if(timeToNextMonster <= 0 ) {
            restart();
            return;
        }
        if(timeToNextMonster == MONSTER_INTERVAL) { // данный блок срабатывает однократно, при уничтожении монстра
            dirRight = !dirRight; // монстр разворачивается
            if(target.isAlive()) {
                target.killCount();
                target.gainScore(100); // герою, если он сам не погиб в этой схватке, начисляются очки
            }
        }
        velocity.x = 2 * STD_VEL_X; // дальше монстр улетает, вращаясь, вверх и немного вбок
        if (!dirRight) velocity.x *= -1;
        if(velocity.y < 10.0f) velocity.y = 3 * STD_VEL_Y;
        rotation += 500.0f * dt;

        timeToNextMonster -= dt;
    }
}