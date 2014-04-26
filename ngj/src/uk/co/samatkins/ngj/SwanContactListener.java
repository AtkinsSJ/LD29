package uk.co.samatkins.ngj;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;

/**
 * Created by Sam on 26/04/14.
 */
public class SwanContactListener implements com.badlogic.gdx.physics.box2d.ContactListener {

    private PlayScene playScene;

    public SwanContactListener(PlayScene playScene) {

        this.playScene = playScene;
    }

    @Override
    public void beginContact(Contact contact) {
        if (collisionIsPlayerEnd(contact)) {
            playScene.gameWon();
        }
    }

    private boolean collisionIsPlayerEnd(Contact contact) {
        if (PlayScene.endFixtureID.equals(contact.getFixtureA().getUserData())
            && PlayScene.playerFixtureID.equals(contact.getFixtureB().getUserData())) {
            return true;
        }
        if (PlayScene.endFixtureID.equals(contact.getFixtureB().getUserData())
            && PlayScene.playerFixtureID.equals(contact.getFixtureA().getUserData())) {
            return true;
        }
        return false;
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
