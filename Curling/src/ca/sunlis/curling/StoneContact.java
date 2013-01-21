package ca.sunlis.curling;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

public class StoneContact implements ContactListener {

	@Override
	public void beginContact(Contact contact) {
		if (contact.getFixtureA().getBody().getLinearVelocity().len2() == 0) {
			slowDown(contact.getFixtureB().getBody());
		} else if (contact.getFixtureB().getBody().getLinearVelocity().len2() == 0) {
			slowDown(contact.getFixtureA().getBody());
		} else {
			slowDown(contact.getFixtureB().getBody());
			slowDown(contact.getFixtureA().getBody());
		}
	}

	private void slowDown(Body body) {
		if (body.getType() != BodyType.DynamicBody) return;
		
		double angle = Math.atan2(body.getLinearVelocity().y, body.getLinearVelocity().x);
		// TODO: Fine-tune this hacky, ball-park static friction
		float v = 12f;
		body.setLinearVelocity(body.getLinearVelocity().sub(v*(float)Math.cos(angle),v*(float)Math.sin(angle)));
	}

	@Override
	public void endContact(Contact contact) {
		return;
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		return;
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		return;
	}

}
