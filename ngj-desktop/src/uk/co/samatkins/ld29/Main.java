package uk.co.samatkins.ld29;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "LD29";
		cfg.useGL20 = false;
		cfg.width = 640;
		cfg.height = 480;
		
		new LwjglApplication(new SwanGame(), cfg);
	}
}
