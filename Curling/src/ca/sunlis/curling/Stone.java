package ca.sunlis.curling;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;

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
