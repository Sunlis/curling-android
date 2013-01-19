package ca.sunlis.curling;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;

public class Stone {
	static double mass = 18; //(kg)
	Circle circle;
	Texture tex;
	double velX = 0;
	double velY = 0;
	
	public Stone(float x, float y, boolean red) {
		this.tex = red ? Curling.redRock : Curling.blueRock;
		
		this.circle = new Circle();
		this.circle.radius = 26;
		this.circle.x = x;
		this.circle.y = y;
		
		this.ranDir();
	}
	
	public void ranDir() {
		int amplitude = 10;
		this.velX = (Math.random()*amplitude) - (amplitude/2);
		this.velY = (Math.random()*amplitude) - (amplitude/2);
	}
	
	public void update() {
		// Screen edges
		if (this.circle.x + this.velX + this.circle.radius >= Curling.screenW || this.circle.x + this.velX - this.circle.radius < 0) {
			this.velX *= -1;
		}
		if (this.circle.y + this.velY + this.circle.radius >= Curling.screenH || this.circle.y + this.velY - this.circle.radius < 0) {
			this.velY *= -1;
		}
		
		this.circle.x += this.velX;
		this.circle.y += this.velY;
		
		// TODO: Friction! =D
		this.velX *= 0.98;
		this.velY *= 0.98;
		if (Math.abs(this.velX) < 0.02 && Math.abs(this.velY) < 0.02) {
			this.velX = 0;
			this.velY = 0;
		}
		
		
		if (this.velX == 0 && this.velY == 0) {
			Curling.dontNeedRender++;
		}
	}
	
	public boolean colliding(Stone that) {
	    float xd = this.circle.x - that.circle.x;
	    float yd = this.circle.y - that.circle.y;

	    float sumRadius = this.circle.radius + that.circle.radius;
	    float sqrRadius = sumRadius * sumRadius;

	    float distSqr = (xd * xd) + (yd * yd);

	    return (distSqr <= sqrRadius);
	}
	
	public void handleCollision(Stone that) {
		// credit to http://stackoverflow.com/questions/345838/ball-to-ball-collision-detection-and-handling for collision handling
		
		Vector2 position = new Vector2();
		position.x = this.circle.x;
		position.y = this.circle.y;
		Vector2 thatPosition = new Vector2();
		thatPosition.x = that.circle.x;
		thatPosition.y = that.circle.y;
		
		Vector2 velocity = new Vector2();
		velocity.x = (float) this.velX;
		velocity.y = (float) this.velY;
		Vector2 thatVelocity = new Vector2();
		thatVelocity.x = (float) that.velX;
		thatVelocity.y = (float) that.velY;
		
		// get the mtd
		Vector2 delta = new Vector2(position).sub(thatPosition);
		double d = delta.len();
		
		// minimum translation distance to push balls apart after intersecting
		Vector2 mtd = new Vector2(delta).mul((float) (((this.circle.radius + that.circle.radius)-d)/d)); 
		
		
		// resolve intersection --
		// inverse mass quantities
		float im1 = (float) (1 / Stone.mass); 
		float im2 = (float) (1 / Stone.mass);
		
		// push-pull them apart based off their mass
		position.add(new Vector2(mtd).mul(im1 / (im1 + im2)));
		thatPosition.sub(new Vector2(mtd).mul(im2 / (im1 + im2)));
		
		// impact speed
		Vector2 v = new Vector2(velocity).sub(thatVelocity);
		float vn = v.dot(new Vector2(mtd).nor());
		
		// sphere intersecting but moving away from each other already
		if (vn > 0.0f) return;
		// collision impulse
		float i = (float) ((-(1.0f + 0.95) * vn) / (im1 + im2));
		Vector2 impulse = new Vector2(mtd).mul(i);
		
		// change in momentum
		velocity.add(new Vector2(impulse).mul(im1));
		thatVelocity.sub(new Vector2(impulse).mul(im2));
		
		this.circle.x = position.x;
		this.circle.y = position.y;
		this.velX = velocity.x;
		this.velY = velocity.y;
		that.circle.x = thatPosition.x;
		that.circle.y = thatPosition.y;
		that.velX = thatVelocity.x;
		that.velY = thatVelocity.y;
	}
	
	public double futureX() {
		return this.circle.x + this.velX;
	}
	public double futureY() {
		return this.circle.y + this.velY;
	}
	
	public void draw() {
		this.update();
		
		Curling.batch.setProjectionMatrix(Curling.camera.combined);
		Curling.batch.begin();
		Curling.batch.draw(this.tex, this.circle.x - this.circle.radius, this.circle.y - this.circle.radius);
		Curling.batch.end();
	}
	
}
