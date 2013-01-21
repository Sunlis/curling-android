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
	static float radius;
	Texture tex;
	Sprite sprite;
	Body body;
	boolean moving = false;
	
	public Stone(float x, float y, boolean red) {
		this.tex = red ? Curling.redRock : Curling.blueRock;
		Stone.radius = this.tex.getWidth()/2;
		sprite = new Sprite(this.tex);
		this.body = Curling.createBody(x,y);
	}
	
	public void update() {
		sprite.setPosition(this.body.getPosition().x - Stone.radius, this.body.getPosition().y - Stone.radius);
		sprite.setRotation((float) ((this.body.getAngle() * 180) / Math.PI));
//		this.moving = this.body.isAwake();
		this.moving = !this.body.getLinearVelocity().epsilonEquals(new Vector2(), 0.05f);
		
		double angle = Math.atan2(this.body.getLinearVelocity().y, this.body.getLinearVelocity().x);
		if (this.moving) {
			// TODO: Fine-tune this hacky, ball-park kinetic friction
			float v = 0.15f;
			this.body.setLinearVelocity(this.body.getLinearVelocity().sub(v*(float)Math.cos(angle),v*(float)Math.sin(angle)));
		} else {
			this.body.setLinearVelocity(0, 0);
		}
		
		if (Math.abs(this.body.getAngularVelocity()) > 0.05 && this.body.getType() == BodyType.DynamicBody) {
			// TODO: Also fine-tune these curl values
			float	v = this.body.getAngularVelocity()/20,
					damping = 1-(1/(10*(this.body.getLinearVelocity().len()+0.01f)));
			
			// get spin direction
			int direction = this.body.getAngularVelocity() > 0 ? -1 : 1;
			// get the angle that the curl will be pushing at
			angle += direction*(Math.PI/2);
			Vector2 curlForce = new Vector2(v*(float)Math.cos(angle), v*(float)Math.sin(angle));
			this.body.setLinearVelocity(this.body.getLinearVelocity().sub(curlForce));
			this.body.setAngularVelocity(this.body.getAngularVelocity()*damping);
		} else if (this.body.getType() == BodyType.DynamicBody && !this.moving) {
			this.body.setAngularVelocity(0);
			this.body.setAwake(false);
		}
	}
	
	public void draw() {
		this.update();
		sprite.draw(Curling.batch);
	}
	
}
