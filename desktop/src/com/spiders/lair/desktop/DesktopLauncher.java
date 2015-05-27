package com.spiders.lair.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.spiders.lair.SpidersLair;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.title = "Spider's Lair";
		config.width = 800;
		config.height = 480;
		
		new LwjglApplication(new SpidersLair(), config);
	}
}
