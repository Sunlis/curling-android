package ca.sunlis.curling;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Curling";
		cfg.useGL20 = true;
		cfg.width = 400;
		cfg.height = 640;
		
		new LwjglApplication(new Curling(), cfg);
	}
}
