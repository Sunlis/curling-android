package ca.sunlis.curling;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Curling implements ApplicationListener {
	static OrthographicCamera camera;
	static SpriteBatch batch;
	static Texture redRock;
	static Texture blueRock;
	static List<Stone> stones;
	
	static float screenW;
	static float screenH;
    
    static boolean mouseDown = false;
    
	public static int dontNeedRender;
	
	@Override
	public void create() {
		screenW = Gdx.graphics.getWidth();
		screenH = Gdx.graphics.getHeight();
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, screenW, screenH);
		batch = new SpriteBatch();
		
		redRock = new Texture(Gdx.files.internal("redRock.png"));
		blueRock = new Texture(Gdx.files.internal("blueRock.png"));
		
		stones = new ArrayList<Stone>();
		for(int i = 0; i < 3; i++) {
			stones.add(new Stone(((i%4)+1) * (screenW/5), (float) ((Math.floor(i/4)+1) * (screenH/5)), i%2 == 0));
		}
		
		Gdx.graphics.setContinuousRendering(false);
	    Gdx.graphics.requestRendering();
	}

	@Override
	public void dispose() {
		batch.dispose();
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		camera.update();
		
		for (int i = 0; i < stones.size(); i++) {
			for (int j = i+1; j < stones.size(); j++) {
				if (stones.get(i).colliding(stones.get(j))) {
					stones.get(i).handleCollision(stones.get(j));
				}
			}
		}
		
		if (!Gdx.input.isTouched() && mouseDown) {
			 for (Stone stone : stones) {
				 stone.ranDir();
			 }
		}
		mouseDown = Gdx.input.isTouched();
		
		dontNeedRender = 0;
		for (Stone stone : stones) {
			stone.draw();
		}
		
		if (dontNeedRender != stones.size() || mouseDown) {
			Gdx.graphics.requestRendering();
		} else {
			Gdx.gl.glClearColor(0, 1, 1, 1);
			Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
			camera.update();
			for (Stone stone : stones) {
				stone.draw();
			}
		}
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
