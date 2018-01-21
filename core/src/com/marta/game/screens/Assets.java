package com.marta.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class Assets {

    private AssetManager assetManager;
    private TextureAtlas atlas;
    private Skin skin;

    public TextureAtlas getAtlas () { return  atlas;}
    public Skin getSkin () {return skin;}
    public AssetManager getAssetManager () { return  assetManager;}

    private Assets () { assetManager = new AssetManager(); }
    private static final Assets ourInstance = new Assets();
    public static Assets getInstance () {return  ourInstance;}

    void loadAssets () {

        loadFont(120, Color.WHITE, 5, "maxWhite.ttf");
        loadFont(60, Color.NAVY, 3, "medBlue.ttf");
        loadFont(60, Color.WHITE, 0, "medWhite.ttf");
        loadFont(48, Color.WHITE, 0, "minWhite.ttf");
        assetManager.load("xmasPack.pack", TextureAtlas.class);


    }


    // конструкцию с загрузкой разных ресурсов под разные экраны оставляю на память,
    // но у меня все экраны примерно одники и теми же ресурсами пользуются, поэтому загрузку делаю одну на всю игру
    void loadAssets (ScreenManager.ScreenType type) {

        switch (type) {
            case MENU:
                loadFont(60, Color.NAVY, 3, "medBlue.ttf");
                assetManager.load("xmasPack.pack", TextureAtlas.class);
                break;
            case GAME:
                loadFont(60, Color.WHITE, 0, "medWhite.ttf");
                assetManager.load("xmasPack.pack", TextureAtlas.class);
                break;
            case SHOP:
                loadFont(48, Color.WHITE, 0, "minWhite.ttf");
                loadFont(60, Color.WHITE, 0, "medhite.ttf");
                loadFont(120, Color.WHITE, 5, "maxWhite.ttf");

                assetManager.load("xmasPack.pack", TextureAtlas.class);
                break;
        }
        assetManager.finishLoading();
        atlas = assetManager.get("xmasPack.pack", TextureAtlas.class);
    }

    void makeLinks () {
//        assetManager.finishLoading(); // загрузка проверяется загрузочным экраном
        atlas = assetManager.get("xmasPack.pack", TextureAtlas.class);
        this.skin = new Skin();
        skin.addRegions(atlas);

        ImageButton.ImageButtonStyle imageButtonStyle = new ImageButton.ImageButtonStyle();
        skin.add("default", imageButtonStyle);

        TextButton.TextButtonStyle textButtonStyle= new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.getDrawable("buttonUp");
        textButtonStyle.down = skin.getDrawable("buttonDown");
        textButtonStyle.font = assetManager.get("medBlue.ttf", BitmapFont.class);
        skin.add("default", textButtonStyle);
    }

    private void loadFont(int size, Color color, int shadow, String name) {
        FileHandleResolver resolver = new InternalFileHandleResolver();
        assetManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        assetManager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
        FreetypeFontLoader.FreeTypeFontLoaderParameter params = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        params.fontFileName ="Amatic-Bold.ttf";
        params.fontParameters.size = size;
        params.fontParameters.color = color;
        params.fontParameters.borderWidth = 0;
        params.fontParameters.borderColor = Color.LIGHT_GRAY;
        params.fontParameters.shadowOffsetX = shadow;
        params.fontParameters.shadowOffsetY = shadow;
        params.fontParameters.shadowColor = Color.LIGHT_GRAY;
        assetManager.load(name, BitmapFont.class, params);
    }

    public void clear() { assetManager.clear(); }

    public void dispose() { assetManager.dispose(); }


}
