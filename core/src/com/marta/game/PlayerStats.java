package com.marta.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.marta.game.screens.ShopScreen;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

public class PlayerStats {

    public static final int costOf1stUpgrade = 100;
    public static final int costOf2ndUpgrade = 500;
    public static final int costOf3rdUpgrade = 1200;

    private String name;
    private int levelLuck;
    private int levelNoSnow;
    private int levelHeal;
    private int levelSuperJump;
    private int levelSuperBullets;
    private int levelInvisibility;
    private int totalKillCount;
    private int totalGiftCount;
    private int highScore;
    private int credits;
    private static Sound soundSuccess;
    private static Sound soundReject;

    static {
        soundSuccess = Gdx.audio.newSound(Gdx.files.internal("cashRegister.wav"));
        soundReject = Gdx.audio.newSound(Gdx.files.internal("click.wav"));
    }

    private PlayerStats(String name) {
        this.name = name;
        this.levelLuck = 0;
        this.levelNoSnow = 1;
        this.levelHeal = 0;
        this.levelSuperJump = 0;
        this.levelSuperBullets = 0;
        this.levelInvisibility = 0;
        this.totalKillCount = 0;
        this.totalGiftCount = 0;
        this.highScore = 0;
        this.credits = 0;

    }

    // здесь заглушка - реализовано сохранение статистики единственного игрока; в дальнейшем возможно ведение списка игроков
    private final static PlayerStats thisPlayer = new PlayerStats("-");
    public static PlayerStats getCurrentPlayer () { return  thisPlayer; }

    public String getName() { return name; }
    public int getHighScore () { return highScore; }
    public int getTotalKillCount() { return totalKillCount; }
    public int getTotalGiftCount() { return totalGiftCount; }

    public int getLevel(ShopScreen.PowerUpType bonus) {
        switch (bonus) {
            case SUN:       return levelNoSnow;
            case MEDKIT:    return levelHeal;
            case CAKE:      return levelSuperBullets;
            case DEER:      return levelSuperJump;
            case HAT:       return levelInvisibility;
            case CLOVER:    return levelLuck;
            default:        return 0;
        }
    }

    public void discardLuck() { this.levelLuck = 0; }

    public void levelup (ShopScreen.PowerUpType type) {
        switch (type) {
            case SUN:
                levelNoSnow = levelup(levelNoSnow);
                break;
            case MEDKIT:
                levelHeal = levelup(levelHeal);
                break;
            case DEER:
                levelSuperJump = levelup(levelSuperJump);
                break;
            case CAKE:
                levelSuperBullets = levelup(levelSuperBullets);
                break;
            case HAT:
                levelInvisibility = levelup(levelInvisibility);
                break;
            case CLOVER:
                levelLuck = levelup(levelLuck);
        }
        updatePlayerStatsFile(getCurrentPlayer());
    }

    private int levelup (int currentLevel) {
        switch (currentLevel) {
            case 0:
                if (credits >= costOf1stUpgrade) {
                    credits -= costOf1stUpgrade;
                    soundSuccess.play(0.5f);
                    return ++currentLevel;
                }
                break;
            case 1:
                if (credits >= costOf2ndUpgrade) {
                    credits -= costOf2ndUpgrade;
                    soundSuccess.play(0.5f);
                    return ++currentLevel;
                }
                break;
            case 2:
                if (credits >= costOf3rdUpgrade) {
                    credits -= costOf3rdUpgrade;
                    soundSuccess.play(0.5f);
                    return ++currentLevel;
                }
                break;
        }
        soundReject.play(0.3f);
        return currentLevel;
    }

    public void checkIfHighScore(int thisGameScore) { this.highScore = Math.max(this.highScore, thisGameScore); }

    public int getCredits() { return credits; }
    public void addCredits(int score) { credits += score; }
    public void addKillCount (int killCount ) {totalKillCount += killCount;}
    public void addGiftCount (int giftCount ) {totalGiftCount += giftCount;}

    static void createPlayerStatsFileIfNone() {
        if (Gdx.files.local("stats.txt").exists()) return;
        Writer writer = null;
        try {
            writer = Gdx.files.local("stats.txt").writer(false);
            writer.write("Unknown 0 1 0 0 0 0 0 0 0 0");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static void loadPlayerStatsFile() {
        BufferedReader br = null;
        try {
            br = Gdx.files.local("stats.txt").reader(8192);
            String [] data = br.readLine().split(" ");
            thisPlayer.name = data[0];
            thisPlayer.levelLuck = Integer.parseInt(data[1]);
            thisPlayer.levelNoSnow = Integer.parseInt(data[2]);
            thisPlayer.levelHeal = Integer.parseInt(data[3]);
            thisPlayer.levelSuperJump = Integer.parseInt(data[4]);
            thisPlayer.levelSuperBullets = Integer.parseInt(data[5]);
            thisPlayer.levelInvisibility = Integer.parseInt(data[6]);
            thisPlayer.totalKillCount = Integer.parseInt(data[7]);
            thisPlayer.totalGiftCount = Integer.parseInt(data[8]);
            thisPlayer.highScore = Integer.parseInt(data[9]);
            thisPlayer.credits = Integer.parseInt(data[10]);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void updatePlayerStatsFile(PlayerStats player) {
        Writer writer = null;
        try {
            writer = Gdx.files.local("stats.txt").writer(false);
            writer.write(player.name
                    + " " + player.levelLuck
                    + " " + player.levelNoSnow
                    + " " + player.levelHeal
                    + " " + player.levelSuperJump
                    + " " + player.levelSuperBullets
                    + " " + player.levelInvisibility
                    + " " + player.totalKillCount
                    + " " + player.totalGiftCount
                    + " " + player.highScore
                    + " " + player.credits);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
