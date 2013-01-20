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
		Body movingBody,stillBody;
		if (contact.getFixtureA().getBody().getLinearVelocity().len2() == 0) {
			movingBody = contact.getFixtureB().getBody();
			stillBody = contact.getFixtureA().getBody();
		} else if (contact.getFixtureB().getBody().getLinearVelocity().len2() == 0) {
			movingBody = contact.getFixtureA().getBody();
			stillBody = contact.getFixtureB().getBody();
		} else {
			return;
		}
		if (stillBody.getType() != BodyType.DynamicBody) {
			return;
		}
		double angle = Math.atan2(movingBody.getLinearVelocity().y, movingBody.getLinearVelocity().x);
		// TODO: Fine-tune this hacky, ball-park static friction
		float v = 10f;
		movingBody.setLinearVelocity(movingBody.getLinearVelocity().sub(v*(float)Math.cos(angle),v*(float)Math.sin(angle)));
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
