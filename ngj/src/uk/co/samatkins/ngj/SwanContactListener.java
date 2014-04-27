package uk.co.samatkins.ngj;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import uk.co.samatkins.AudioManager;

public class SwanContactListener implements com.badlogic.gdx.physics.box2d.ContactListener {

    private PlayScene playScene;

    public SwanContactListener(PlayScene playScene) {

        this.playScene = playScene;
    }

    @Override
    public void beginContact(Contact contact) {
        if (contactBetween(PlayScene.playerFixtureID, PlayScene.endFixtureID, contact)) {
            playScene.gameWon();
        } else if (contactBetween(PlayScene.playerFixtureID, PlayScene.duckFixtureID, contact)) {
            AudioManager.playSound("quack");
        } else if (contactBetween(PlayScene.playerFixtureID, PlayScene.frogFixtureID, contact)) {
            AudioManager.playSound("ribbit");
        }
    }

    private boolean contactBetween(String a, String b, Contact contact) {
        if (a.equals(contact.getFixtureA().getUserData())
                && b.equals(contact.getFixtureB().getUserData())) {
            return true;
        }
        if (a.equals(contact.getFixtureB().getUserData())
                && b.equals(contact.getFixtureA().getUserData())) {
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
