package com.marta.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.marta.game.screens.ScreenManager;

public class MerryChristmas extends Game {

	@Override
	public void create () {
		ScreenManager.getInstance().init(this);
		PlayerStats.createPlayerStatsFileIfNone();
		PlayerStats.loadPlayerStatsFile();
		PlayerStats.getCurrentPlayer();
		ScreenManager.getInstance().switchScreenTo(ScreenManager.ScreenType.MENU);
	}

	@Override
	public void render () {
		float dt = Gdx.graphics.getDeltaTime();
		getScreen().render(dt);
	}

	@Override
	public void dispose () {
		ScreenManager.getInstance().dispose();
	}
}
