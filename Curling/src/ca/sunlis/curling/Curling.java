package ca.sunlis.curling;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Curling implements ApplicationListener {
	static OrthographicCamera camera;
	static SpriteBatch batch;
	static float screenW;
	static float screenH;
	
	ShapeRenderer shapeRenderer;
	
	//Box2D
	static World world;
	Box2DDebugRenderer debugRenderer;
	StoneContact stoneContact;
	Body hitBody;
	static float TOUCH_OFFSET = 26f;
	static final float BOX_STEP = 1/60f;
    static final int BOX_VELOCITY_ITERATIONS = 6;
    static final int BOX_POSITION_ITERATIONS = 2;
    static float BOX_TO_WORLD = 26; //TODO
    static float WORLD_TO_BOX = 1/BOX_TO_WORLD; //TODO
    static float BOX_STONE_RADIUS = 26;
	
	// Textures
	static Texture redRock;
	static Texture blueRock;
	static Texture house;
	static Circle topHouse;
	static Circle bottomHouse;
	
	// Stones
	static List<Stone> stones;
    
    static boolean mouseDown = false;
    Vector2 touchPoint;
    
	public static int dontNeedRender;
	
	@Override
	public void create() {
		String imageExtension = (Gdx.graphics.getWidth() < 800) ? "_36" : "_72";
		screenW = (Gdx.graphics.getWidth() < 800) ? 400 : 800;
		screenH = screenW * ((float)Gdx.graphics.getHeight()/(float)Gdx.graphics.getWidth());
		
		if (Gdx.graphics.getWidth() < 800) {
			screenW = 400;
			imageExtension = "_36";
			Curling.BOX_STONE_RADIUS = 13;
//			Curling.BOX_TO_WORLD = 1/13;
//			Curling.WORLD_TO_BOX = 1/Curling.BOX_TO_WORLD;
		}
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, screenW, screenH);
		batch = new SpriteBatch();
		
		world = new World(new Vector2(0,0), true);
		debugRenderer = new Box2DDebugRenderer();
		stoneContact = new StoneContact();
		world.setContactListener(stoneContact);
		
		shapeRenderer = new ShapeRenderer();
		
		createScreenBox();
		
		
		redRock = new Texture(Gdx.files.internal("redRock"+imageExtension+".png"));
		blueRock = new Texture(Gdx.files.internal("blueRock"+imageExtension+".png"));
		house = new Texture(Gdx.files.internal("house"+imageExtension+".png"));
		
		topHouse = new Circle();
		topHouse.radius = house.getWidth()/2;
		topHouse.x = screenW/2;
		topHouse.y = screenH/2;
		
		stones = new ArrayList<Stone>();
		for(int i = 0; i < 4; i++) {
			float x = ((i%4)+1) * (screenW/5);
			float y = (float) ((Math.floor(i/4)+1) * (screenH/5));
			stones.add(new Stone(x, y, i%2 == 0));
		}
		
		Gdx.graphics.setContinuousRendering(false);
	    Gdx.graphics.requestRendering();
	}

	@Override
	public void dispose() {
		batch.dispose();
		debugRenderer.dispose();
	}

	@Override
	public void render() {
		update();
		
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		camera.update();
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
		batch.draw(house, topHouse.x - topHouse.radius, topHouse.y - topHouse.radius);
//		sheetRect.x++;
//		sheetRect.y++;
		
		boolean wantRender = false;
		for (Stone stone : stones) {
			stone.draw();
			if (!wantRender && stone.moving) wantRender = true;
		}		
		batch.end();
		
		shapeRenderer.begin(ShapeType.Rectangle);
		shapeRenderer.setColor(0, 1, 0, 1);
		shapeRenderer.rect(0, 0, 100, 100);
		shapeRenderer.end();
		
		if (Gdx.input.isTouched() && hitBody != null) {
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.setColor(0, 1, 0, 1);
			shapeRenderer.line(touchPoint.x, touchPoint.y, Gdx.input.getX(), screenH-Gdx.input.getY());
			shapeRenderer.end();
		}
		
		debugRenderer.render(world, camera.combined);  
        world.step(BOX_STEP, BOX_VELOCITY_ITERATIONS, BOX_POSITION_ITERATIONS);
		
		if (wantRender) Gdx.graphics.requestRendering();
	}
	
	public void update() {
		if (Gdx.input.isTouched() && !mouseDown) {
			// mouse just pressed
			touchPoint = new Vector2(Gdx.input.getX(), screenH-Gdx.input.getY());
			hitBody = null;
			Iterator<Body> bodyIterator = world.getBodies();
			while (bodyIterator.hasNext()) {
				Body body = bodyIterator.next();
				if (body.getType() != BodyType.DynamicBody) continue;
				if (body.getPosition().sub(touchPoint).len() < TOUCH_OFFSET) {
					hitBody = body;
					break;
				}
			}
			if (hitBody != null) {
				hitBody.setTransform(touchPoint, 0f);
				hitBody.setLinearVelocity(0,0);
				hitBody.setAngularVelocity(0f);
				hitBody.setType(BodyType.StaticBody);
			}
		} else if (Gdx.input.isTouched() && mouseDown && hitBody != null) {
			// mouse down, was down previously
		} else if (!Gdx.input.isTouched() && mouseDown && hitBody != null) {
			// mouse just released
			hitBody.setType(BodyType.DynamicBody);
			Vector2 vel = new Vector2(Gdx.input.getX(), screenH-Gdx.input.getY()).sub(touchPoint);
			hitBody.setLinearVelocity(vel);
			if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
				hitBody.setAngularVelocity(1);
			} else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
				hitBody.setAngularVelocity(-1);
			}
		}
		mouseDown = Gdx.input.isTouched();
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
	
	public static Body createBody(float x, float y) {
		//Dynamic Body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(x, y);
        Body body = world.createBody(bodyDef);
        CircleShape dynamicCircle = new CircleShape();
        dynamicCircle.setRadius(BOX_STONE_RADIUS);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = dynamicCircle;
        fixtureDef.density = 23f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 1.0f;
        body.createFixture(fixtureDef);
        return body;
	}

	private void createScreenBox() {
		//bottom
        BodyDef groundBodyDef=new BodyDef();
        groundBodyDef.position.set(new Vector2(0, -1));
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
        rightBodyDef.position.set(new Vector2(camera.viewportWidth+1, 0));
        Body rightBody = world.createBody(rightBodyDef);
        PolygonShape rightBox = new PolygonShape();
        rightBox.setAsBox(0, camera.viewportHeight);
        rightBody.createFixture(rightBox, 0.0f);
	}
}
