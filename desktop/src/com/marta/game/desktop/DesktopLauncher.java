package com.marta.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.marta.game.MerryChristmas;
import com.marta.game.screens.ScreenManager;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = ScreenManager.SCREEN_WIDTH * 3 / 4;
		config.height = ScreenManager.SCREEN_HEIGHT * 3 / 4;
		new LwjglApplication(new MerryChristmas(), config);
	}
}
