package uk.co.samatkins.ngj;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import uk.co.samatkins.Entity;
import uk.co.samatkins.Scene;
import uk.co.samatkins.components.ControllerComponent;
import uk.co.samatkins.components.graphics.SpriteComponent;

/**
 * Code for controlling the swan (the player)
 */
public class SwanControllerComponent extends ControllerComponent {
    private World world;
    private RevoluteJoint thighJoint;
    private RevoluteJoint kneeJoint;
    private Body swanBody;

    private Entity legEntity, footEntity;

    private float startX, startY;
    private Body thighBody;
    private Body footBody;
    private float thighLength;

    public SwanControllerComponent(World world) {
        this.world = world;
    }

    @Override
    public void onAddedToScene(Scene scene) {
        super.onAddedToScene(scene);
        buildBodies();
        startX = entity.getX();
        startY = entity.getY();

        legEntity = new Entity(
                0,0,
                new SpriteComponent(
                        scene.getGame().getSkin().getRegion("leg"),
                        new Vector2(-4, -thighLength*2)
                ),
                null, null
        );
        footEntity = new Entity(
                0,-thighLength*2,
                new SpriteComponent(
                        scene.getGame().getSkin().getRegion("foot"),
                        new Vector2(-1.5f, -20)
                ),
                null, null
        );
        entity.addActor(legEntity);
        legEntity.addActor(footEntity);
    }

    private void buildBodies() {
        thighLength = 16;

        // Swan torso/whatever
        BodyDef swanBodyDef = new BodyDef();
        swanBodyDef.type = BodyDef.BodyType.DynamicBody;
        float x = entity.getX();
        float y = entity.getY();
        swanBodyDef.position.set(x, y);
        swanBody = world.createBody(swanBodyDef);
        PolygonShape shape = new PolygonShape();

        shape.setAsBox(18.5f, 12.75f, new Vector2(-11.5f, 7.75f), 0);
        FixtureDef swanFixtureDef = new FixtureDef();
        swanFixtureDef.shape = shape;
        swanFixtureDef.filter.groupIndex = -1;
        swanFixtureDef.density = 8f;
        swanBody.createFixture(swanFixtureDef);

        shape.setAsBox(11.5f, 25.5f, new Vector2(18.5f, 20.5f), 0);
        swanFixtureDef.density = 2;
        swanBody.createFixture(swanFixtureDef);

        // First leg!
        BodyDef thighDef = new BodyDef();
        thighDef.type = BodyDef.BodyType.DynamicBody;
        thighDef.position.set(x, y);
        thighBody = world.createBody(thighDef);

        shape.setAsBox(2, thighLength/2f, new Vector2(0, -thighLength/2), 0);
        FixtureDef thighFD = new FixtureDef();
        thighFD.shape = shape;
        thighFD.density = 2f;
        thighFD.filter.groupIndex = -1;
        thighBody.createFixture(thighFD);

        // Body/leg joint
        RevoluteJointDef thighJointDef = new RevoluteJointDef();
        thighJointDef.initialize(swanBody, thighBody, new Vector2(x,y));
        thighJointDef.enableMotor = true;
        thighJointDef.maxMotorTorque = 5000000f;
        thighJointDef.enableLimit = true;
        thighJointDef.lowerAngle = -0.5f * MathUtils.PI;
        thighJointDef.upperAngle = 0.5f * MathUtils.PI;

        thighJoint = (RevoluteJoint) world.createJoint(thighJointDef);

        // Lower half of first leg
        BodyDef footDef = new BodyDef();
        footDef.type = BodyDef.BodyType.DynamicBody;
        footDef.position.set(x+2f, y- thighLength);
        footBody = world.createBody(footDef);
        shape.setAsBox(2.5f, 5f, new Vector2(0, -5f), 0);
        footBody.createFixture(thighFD);

        // Leg/foot joint
        RevoluteJointDef kneeJointDef = new RevoluteJointDef();
        kneeJointDef.initialize(thighBody, footBody, new Vector2(x, y- thighLength));
        kneeJointDef.enableMotor = true;
        kneeJointDef.maxMotorTorque = 5000000f;
        kneeJointDef.enableLimit = true;
        kneeJointDef.lowerAngle = -MathUtils.PI;
        kneeJointDef.upperAngle = 0;

        kneeJoint = (RevoluteJoint) world.createJoint(kneeJointDef);

        shape.dispose();
    }

    @Override
    public void act(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            thighJoint.setMotorSpeed(2f);
        } else if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            thighJoint.setMotorSpeed(-2f);
        } else {
            thighJoint.setMotorSpeed(0);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.O)) {
            kneeJoint.setMotorSpeed(2f);
        } else if (Gdx.input.isKeyPressed(Input.Keys.P)) {
            kneeJoint.setMotorSpeed(-2f);
        } else {
            kneeJoint.setMotorSpeed(0);
        }

        if (Gdx.input.justTouched()) {
            swanBody.setTransform(startX, startY, 0);
        }

        // Position body visually
        entity.setPosition(swanBody.getPosition().x*2, swanBody.getPosition().y*2);
        float swanAngle = swanBody.getAngle() * MathUtils.radiansToDegrees,
              thighAngle = (thighBody.getAngle() * MathUtils.radiansToDegrees) - swanAngle,
              footAngle = (kneeJoint.getJointAngle() * MathUtils.radiansToDegrees);

        entity.setRotation(swanAngle);
        legEntity.setRotation(thighAngle);
        footEntity.setRotation(footAngle);
    }
}
