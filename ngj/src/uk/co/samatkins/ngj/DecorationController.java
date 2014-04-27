package uk.co.samatkins.ngj;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import uk.co.samatkins.components.ControllerComponent;

public class DecorationController extends ControllerComponent {

    private Body body;

    public DecorationController(Body body) {
        this.body = body;
    }

    @Override
    public void act(float delta) {
        entity.setPosition(body.getPosition().x*2, body.getPosition().y*2);
        entity.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
    }
}
