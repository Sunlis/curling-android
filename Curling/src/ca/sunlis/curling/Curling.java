package ca.sunlis.curling;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Curling implements ApplicationListener {
	static OrthographicCamera camera;
	static SpriteBatch batch;
	static float screenW;
	static float screenH;
	
	//Box2D
	static World world;
	Box2DDebugRenderer debugRenderer;
	static final float BOX_STEP = 1/60f;
    static final int BOX_VELOCITY_ITERATIONS = 6;
    static final int BOX_POSITION_ITERATIONS = 2;
    static final float WORLD_TO_BOX = 1/55; //TODO
    static final float BOX_TO_WORLD = 55; //TODO
	
	// Textures
	static Texture redRock;
	static Texture blueRock;
	
	// Stones
	static List<Stone> stones;
    
    static boolean mouseDown = false;
    
	public static int dontNeedRender;
	
	@Override
	public void create() {
		screenW = Gdx.graphics.getWidth();
		screenH = Gdx.graphics.getHeight();
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, screenW, screenH);
		batch = new SpriteBatch();
		
		world = new World(new Vector2(100,100), true);
		debugRenderer = new Box2DDebugRenderer();
		
		createScreenBox();
        
        //Dynamic Body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DynamicBody;
        bodyDef.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2);
        Body body = world.createBody(bodyDef);
        CircleShape dynamicCircle = new CircleShape();
        dynamicCircle.setRadius(1*BOX_TO_WORLD);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = dynamicCircle;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 1.0f;
        body.createFixture(fixtureDef);
		
		redRock = new Texture(Gdx.files.internal("redRock.png"));
		blueRock = new Texture(Gdx.files.internal("blueRock.png"));
		
		stones = new ArrayList<Stone>();
		for(int i = 0; i < 4; i++) {
			stones.add(new Stone(((i%4)+1) * (screenW/5), (float) ((Math.floor(i/4)+1) * (screenH/5)), i%2 == 0));
		}
		
//		Gdx.graphics.setContinuousRendering(false);
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
			Gdx.gl.glClearColor(0.9f, 1, 1, 1);
			Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
			camera.update();
			for (Stone stone : stones) {
				stone.draw();
			}
		}
		
		debugRenderer.render(world, camera.combined);  
        world.step(BOX_STEP, BOX_VELOCITY_ITERATIONS, BOX_POSITION_ITERATIONS);
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

	private void createScreenBox() {
		//bottom
        BodyDef groundBodyDef=new BodyDef();
        groundBodyDef.position.set(new Vector2(0, 0));
        Body groundBody = world.createBody(groundBodyDef);
        PolygonShape groundBox = new PolygonShape();
        groundBox.setAsBox(camera.viewportWidth, 0);
        groundBody.createFixture(groundBox, 0.0f);
        //top
        BodyDef topBodyDef=new BodyDef();
        topBodyDef.position.set(new Vector2(0, camera.viewportHeight));
        Body topBody = world.createBody(topBodyDef);
        PolygonShape topBox = new PolygonShape();
        topBox.setAsBox(camera.viewportWidth, 0);
        topBody.createFixture(topBox, 0.0f);
        // left
        BodyDef leftBodyDef=new BodyDef();
        leftBodyDef.position.set(new Vector2(0, 0));
        Body leftBody = world.createBody(leftBodyDef);
        PolygonShape leftBox = new PolygonShape();
        leftBox.setAsBox(0, camera.viewportHeight);
        leftBody.createFixture(leftBox, 0.0f);
        // right
        BodyDef rightBodyDef=new BodyDef();
        rightBodyDef.position.set(new Vector2(camera.viewportWidth, 0));
        Body rightBody = world.createBody(rightBodyDef);
        PolygonShape rightBox = new PolygonShape();
        rightBox.setAsBox(0, camera.viewportHeight);
        rightBody.createFixture(rightBox, 0.0f);
	}
}
