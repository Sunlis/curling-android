package ca.sunlis.curling;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class Stone {
	static float radius = 26;
	Texture tex;
	Sprite sprite;
	Body body;
	boolean moving = false;
	
	public Stone(float x, float y, boolean red) {
		this.tex = red ? Curling.redRock : Curling.blueRock;
		sprite = new Sprite(this.tex);
		this.body = Curling.createBody(x,y);
	}
	
	public void update() {
		sprite.setPosition(this.body.getPosition().x - Stone.radius, this.body.getPosition().y - Stone.radius);
		sprite.setRotation((float) ((this.body.getAngle() * 180) / Math.PI));
		this.moving = !this.body.getLinearVelocity().epsilonEquals(new Vector2(), 0.01f);
		
		if (this.moving) {
			double angle = Math.atan2(this.body.getLinearVelocity().y, this.body.getLinearVelocity().x);
			// TODO: Fine-tune this hacky, ball-park kinetic friction
			float v = 0.1f;
			this.body.setLinearVelocity(this.body.getLinearVelocity().sub(v*(float)Math.cos(angle),v*(float)Math.sin(angle)));
		}
	}
	
	public void draw() {
		this.update();
		sprite.draw(Curling.batch);
	}
	
}
