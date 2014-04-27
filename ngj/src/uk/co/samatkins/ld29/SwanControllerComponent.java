package uk.co.samatkins.ld29;

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

    private Body thighBody;
    private Body footBody;
    private float thighLength;
    private Vector2 legOffset;

    public SwanControllerComponent(World world) {
        this.world = world;
        legOffset = new Vector2(-10,0);
        thighLength = 8;
    }

    @Override
    public void onAddedToScene(Scene scene) {
        super.onAddedToScene(scene);
        buildBodies();

        legEntity = new Entity(
                legOffset.x*2,legOffset.y*2,
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

        // Swan torso/whatever
        BodyDef swanBodyDef = new BodyDef();
        swanBodyDef.type = BodyDef.BodyType.DynamicBody;
        float x = entity.getX();
        float y = entity.getY();
        swanBodyDef.position.set(x, y);
        swanBody = world.createBody(swanBodyDef);
        PolygonShape shape = new PolygonShape();

        FixtureDef swanFixtureDef = new FixtureDef();
        swanFixtureDef.shape = shape;
        swanFixtureDef.filter.groupIndex = -1;
        swanFixtureDef.density = 8f;

        // Torso
        Vector2 offset = new Vector2(-30f, -5f);
        float[] vertices = {
                0, 14,
                14, 0,
                51, 0,
                58, 7,
                58, 15,
                37, 25,
                10, 25
        };
        offsetVertices(vertices, offset);
        shape.set(vertices);
        swanBody.createFixture(swanFixtureDef).setUserData(PlayScene.playerFixtureID);

        // Neck
        vertices = new float[] {
                46,20,
                58,15,
                43,41,
                36,41
        };
        offsetVertices(vertices, offset);
        shape.set(vertices);
        swanFixtureDef.density = 6f;
        swanBody.createFixture(swanFixtureDef).setUserData(PlayScene.playerFixtureID);

        // Head
        vertices = new float[] {
                36,41,
                59,41,
                50,51,
                41,51
        };
        offsetVertices(vertices, offset);
        shape.set(vertices);
        swanFixtureDef.density = 6f;
        swanBody.createFixture(swanFixtureDef).setUserData(PlayScene.playerFixtureID);

        // First leg!
        BodyDef thighDef = new BodyDef();
        thighDef.type = BodyDef.BodyType.DynamicBody;
        thighDef.position.set(x+ legOffset.x, y+ legOffset.y);
        thighBody = world.createBody(thighDef);

        shape.setAsBox(2, thighLength/2f, new Vector2(0, -thighLength/2), 0);
        FixtureDef thighFD = new FixtureDef();
        thighFD.shape = shape;
        thighFD.density = 2f;
        thighFD.filter.groupIndex = -1;
        thighBody.createFixture(thighFD).setUserData(PlayScene.playerFixtureID);

        // Body/leg joint
        RevoluteJointDef thighJointDef = new RevoluteJointDef();
        thighJointDef.initialize(swanBody, thighBody, new Vector2(x+ legOffset.x,y+ legOffset.y));
        thighJointDef.enableMotor = true;
        thighJointDef.maxMotorTorque = 5000000f;
        thighJointDef.enableLimit = true;
        thighJointDef.lowerAngle = -0.5f * MathUtils.PI;
        thighJointDef.upperAngle = 0.5f * MathUtils.PI;

        thighJoint = (RevoluteJoint) world.createJoint(thighJointDef);

        // Foot
        BodyDef footDef = new BodyDef();
        footDef.type = BodyDef.BodyType.DynamicBody;
        footDef.position.set(x+ legOffset.x+2f, y+ legOffset.y-thighLength);
        footBody = world.createBody(footDef);
        shape.setAsBox(2.5f, 5f, new Vector2(0, -5f), 0);
        footBody.createFixture(thighFD).setUserData(PlayScene.playerFixtureID);

        // Knee joint
        RevoluteJointDef kneeJointDef = new RevoluteJointDef();
        kneeJointDef.initialize(thighBody, footBody, new Vector2(x+ legOffset.x, y+ legOffset.y-thighLength));
        kneeJointDef.enableMotor = true;
        kneeJointDef.maxMotorTorque = 5000000f;
        kneeJointDef.enableLimit = true;
        kneeJointDef.lowerAngle = 0;
        kneeJointDef.upperAngle = MathUtils.PI;

        kneeJoint = (RevoluteJoint) world.createJoint(kneeJointDef);

        shape.dispose();
    }

    private void offsetVertices(float[] vertices, Vector2 offset) {
        for (int i=0; i<vertices.length; i+=2) {
            vertices[i] += offset.x;
            vertices[i+1] += offset.y;
        }
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
