package com.marta.game.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Map {

    private Tile [][] map;
    private final int TILE_SIZE = 40;
    private final int MAP_WIDTH_IN_TILES = (int)Math.ceil((double)ScreenManager.MAP_WIDTH /TILE_SIZE);
    private final int MAP_HEIGHT_IN_TILES = (int)Math.ceil((double)ScreenManager.SCREEN_HEIGHT/TILE_SIZE);

    private enum Tile {
        SURFACE ("tile2"), DEEP ("tile5"), EMPTY ("tile0"), USED ("tile0");

        private TextureRegion texture;

        Tile (String pngName) { this.texture = Assets.getInstance().getAtlas().findRegion(pngName); }
    }

    public int getTILE_SIZE() { return TILE_SIZE; }

    Map() {
        map = new Tile [MAP_WIDTH_IN_TILES][MAP_HEIGHT_IN_TILES];
        restart();
    }

    void restart() {

        // clear map
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                map[i][j]= Tile.EMPTY;
            }
        }

        // create base ground
        int bar = 0;
        int height = 4;
        while (bar < map.length) {
            int length = MathUtils.random(3, 5);
            height += MathUtils.random(-3, 3);
            if (height < 1) height = 1;
            if (height > 6) height = 6;
            for (; length > 0; length--, bar++) fillBar(bar, height);
        }
        if(!ScreenManager.getInstance().isDesktop()) {
            for (int i = 0; i < 4; i++) fillBar(i,9);
            for (int i = MAP_WIDTH_IN_TILES-1; i > MAP_WIDTH_IN_TILES-5; i--)fillBar(i,9);
        }

        // create floating islands
        int islandCount = 15;
        while (islandCount > 0) {
            int x = MathUtils.random(1,MAP_WIDTH_IN_TILES - 5);
            int y = MathUtils.random(5, 14);
            int width = MathUtils.random(2,4);
            if(areaGoodForIsland(x,y,width)) {
                for (int i = 0; i < width; i++) {
                    map[x+i][y]=Tile.SURFACE;
                }
                islandCount--;
            }
        }
    }

    private void fillBar (int x, int y) {
        if(x < map.length) {
            map[x][y] = Tile.SURFACE;
            for (int i = y - 1; i >= 0; i--) {
                map[x][i] = Tile.DEEP;
            }
        }
    }

    private boolean areaGoodForIsland (int x, int y, int width) {
        for (int i = -1; i < width + 1; i++) {
            for (int j = -2; j < 3; j++) {
                if(map[x+i][y+j]!=Tile.EMPTY) return false;
            }
        }
        return true;
    }

    public boolean tileNotPassable(float x, float y) {
        if (x < 0 || x >= ScreenManager.MAP_WIDTH || y < 0) return true;
        if (y >= ScreenManager.SCREEN_HEIGHT) return false;
        return ( map [(int)(x/TILE_SIZE)][(int)(y/TILE_SIZE)] != Tile.EMPTY &&
                map [(int)(x/TILE_SIZE)][(int)(y/TILE_SIZE)] != Tile.USED);
    }

    private boolean outsideScreen (float x, float y, float margin) {
        return x + margin < 0 || x >= ScreenManager.SCREEN_WIDTH || y + margin < 0 || y > ScreenManager.SCREEN_HEIGHT;
    }

    public boolean outsideScreen (Vector2 position, float margin) {
        return outsideScreen(position.x, position.y, margin);
    }

    public void blockPlace (float x, float y) {
        if(!outsideScreen(x,y,0)) {
            int i = (int)(x/TILE_SIZE);
            int j = (int)(y/TILE_SIZE);
            if(map [i][j] == Tile.EMPTY) map [i][j] = Tile.USED;
        }
    }

    public void blockPlace (Vector2 position) {
        blockPlace(position.x, position.y);
    }

    public void unblockPlace (float x, float y) {
        if(!outsideScreen(x, y,0)) {
            int i = (int) (x / TILE_SIZE);
            int j = (int) (y / TILE_SIZE);
            if (map[i][j] == Tile.USED) map[i][j] = Tile.EMPTY;
        }
    }

    public void unblockPlace (Vector2 position) {
        unblockPlace(position.x, position.y);
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                batch.draw(map[i][j].texture, i * TILE_SIZE, j * TILE_SIZE);
            }
        }
    }

    public float nearestFloor(float x, float y) {
        int i = (int) x / TILE_SIZE;
        int j = Math.min((int) (y / TILE_SIZE - 1), MAP_HEIGHT_IN_TILES - 1);
        for ( ; j >= 0; j--) {
            if (map[i][j] == Tile.SURFACE) return (j + 1) * TILE_SIZE;
        }
        //заглушка
        return 0;
    }

    public Vector2 getRandomEmptyFloorTile () {
        while (true) {
            int x = MathUtils.random(0,MAP_WIDTH_IN_TILES - 1);
            int y = MathUtils.random(1, MAP_HEIGHT_IN_TILES - 1);
            if(map[x][y]==Tile.EMPTY && map[x][y-1]==Tile.SURFACE)
                return new Vector2(x * TILE_SIZE + TILE_SIZE / 2,y * TILE_SIZE);
        }
    }
}