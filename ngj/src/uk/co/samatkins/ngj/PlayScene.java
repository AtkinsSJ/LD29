package uk.co.samatkins.ngj;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import uk.co.samatkins.Entity;
import uk.co.samatkins.Scene;
import uk.co.samatkins.components.graphics.SpriteComponent;

/**
 * Scene for actually playing the game
 */
public class PlayScene extends Scene<NGJGame> {
    public static final String endFixtureID = "End",
                                playerFixtureID = "Player";
    public static final Color SKY_COLOR = Color.valueOf("5BCEFF"),
                            WATER_COLOR = Color.valueOf("3A5EFF");

    private World world;
    private Array<Body> waterBodies;
    private float particleRadius;
    private Box2DDebugRenderer debugRenderer;
    public static final float TIME_STEP = 1f / 30f;
    private float physicsCounter = 0;
    private FPSLogger fpsLogger;
    private Entity swanEntity;

    private static final boolean DRAW_DEBUG = true;

    public PlayScene(NGJGame game) {
        super(game);
        Gdx.gl.glClearColor(SKY_COLOR.r, SKY_COLOR.g, SKY_COLOR.b, 1f);
        this.fpsLogger = new FPSLogger();

        this.world = new World(new Vector2(0, -10), true);
        this.debugRenderer = new Box2DDebugRenderer();

        buildWorld();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        getCamera().position.set(0, getCamera().viewportHeight / 2, 0);
    }

    private void buildWorld() {
        float halfWorldWidth = 300,
              halfWorldHeight = 200;
        int halfWallThickness = 10;

        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.position.set(0, 0);

        Body groundBody = world.createBody(groundBodyDef);
        PolygonShape shape = new PolygonShape();

        shape.setAsBox(halfWorldWidth, halfWallThickness);
        groundBody.createFixture(shape, 0);

        shape.setAsBox(halfWallThickness, halfWorldHeight, new Vector2(-halfWorldWidth- halfWallThickness, halfWorldHeight), 0);
        groundBody.createFixture(shape, 0);
        shape.setAsBox(halfWallThickness, halfWorldHeight, new Vector2(halfWorldWidth+ halfWallThickness, halfWorldHeight), 0);
        Fixture endLine = groundBody.createFixture(shape, 0);
        endLine.setUserData(endFixtureID);

        shape.dispose();

        createWater(-halfWorldWidth, halfWallThickness, halfWorldWidth*2, 60, 8);
        createSwan(-halfWorldWidth+50, 100);
    }

    /**
     * Create a whole bunch of water particles, to fill the rectangle given
     * @param x x
     * @param y y
     * @param width width
     * @param height height
     * @param particleRadius Radius of each water particle
     */
    private void createWater(float x, float y, float width, float height, float particleRadius) {

        this.waterBodies = new Array<Body>();

        CircleShape shape = new CircleShape();
        shape.setRadius(particleRadius);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 10f;
        fixtureDef.friction = 0.1f;
        fixtureDef.restitution = 0.6f;

        this.particleRadius = particleRadius;
        float diameter = particleRadius * 2;
        int across = (int) (width/diameter),
            up = (int) Math.ceil(height/diameter);

        float oddOffset = width % diameter;
        float bodyY;

        for (int iy=0; iy<up; iy++) {
            bodyY = ((0.5f + iy) * diameter) + y;
            for (int ix=0; ix<across; ix++) {
                BodyDef bodyDef = new BodyDef();
                bodyDef.type = BodyDef.BodyType.DynamicBody;
                bodyDef.position.x = ((0.5f + ix) * diameter) + x;
                if (iy % 2 == 1) bodyDef.position.x += oddOffset;
                bodyDef.position.y = bodyY;

                Body body = world.createBody(bodyDef);
                body.createFixture(fixtureDef);
                waterBodies.add(body);
            }
        }

        shape.dispose();
    }

    /**
     * Create the swan entity along with its physics representation
     * @param x X
     * @param y Y
     */
    private void createSwan(float x, float y) {
        TextureRegion textureRegion = game.getSkin().getRegion("swan");
        swanEntity = new Entity(
                x, y,
                new SpriteComponent(
                        textureRegion,
                        new Vector2(-60,-10)
                ),
                new SwanControllerComponent(world),
                null
        );
        swanEntity.setSize(textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
        addEntity(swanEntity);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        // Basic physics loop
        physicsCounter += delta;
        while (physicsCounter > TIME_STEP) {
            world.step(TIME_STEP, 6, 2);
            physicsCounter -= TIME_STEP;
        }
    }

    @Override
    public void draw() {
        getCamera().position.set(swanEntity.getX(), swanEntity.getY(), 0);

        // Draw background and water
        ShapeRenderer shapeRenderer = getShapeRenderer();
        shapeRenderer.setProjectionMatrix(getCamera().combined.scl(2));
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(WATER_COLOR);

        for (Body body: waterBodies) {
            shapeRenderer.circle(body.getPosition().x, body.getPosition().y, particleRadius);
        }

        shapeRenderer.end();
        getCamera().combined.scl(0.5f);

        super.draw();

        if (DRAW_DEBUG) {
            this.debugRenderer.render(world, getCamera().combined.scl(2));
            getCamera().combined.scl(0.5f);
            fpsLogger.log();
        }
    }

    public void gameWon() {

    }

    public void gameLost() {

    }

    @Override
    protected void onBackButtonPressed() {
        game.setScene(new MainMenuScene(game));
    }
}
