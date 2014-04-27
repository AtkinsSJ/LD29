package uk.co.samatkins.ld29;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import uk.co.samatkins.Entity;
import uk.co.samatkins.Scene;
import uk.co.samatkins.components.graphics.SpriteComponent;
import uk.co.samatkins.geom.Rectangle;
import uk.co.samatkins.ui.ExtendedButton;

/**
 * Scene for actually playing the game
 */
public class PlayScene extends Scene<SwanGame> {
    public static final String endFixtureID = "End",
                                playerFixtureID = "Player",
                                frogFixtureID = "Frog",
                                duckFixtureID = "Duck",
                                waterFixtureID = "Water";
    public static final Color SKY_COLOR = Color.valueOf("5BCEFF"),
                            GRASS_COLOR = Color.valueOf("5BCE00"),
                            WATER_COLOR = Color.valueOf("3A5EFF");

    private World world;
    private Array<Body> waterBodies;
    private float particleRadius;
    private Box2DDebugRenderer debugRenderer;
    public static final float TIME_STEP = 1f / 30f;
    private float physicsCounter = 0;
    private FPSLogger fpsLogger;
    private Entity entity;

    private boolean gameOver = false;
    private float timeTaken = 0;

    private static final boolean DRAW_DEBUG = false;
    private float halfWorldWidth;
    private float halfWorldHeight;

    public PlayScene(SwanGame game) {
        super(game);
        Gdx.gl.glClearColor(SKY_COLOR.r, SKY_COLOR.g, SKY_COLOR.b, 1f);
        this.fpsLogger = new FPSLogger();

        this.world = new World(new Vector2(0, -10), true);
        this.world.setContactListener(new SwanContactListener(this));
        if (DRAW_DEBUG) {
            this.debugRenderer = new Box2DDebugRenderer();
        }

        buildWorld();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        getCamera().position.set(0, getCamera().viewportHeight / 2, 0);
    }

    private void buildWorld() {
        halfWorldWidth = 300;
        halfWorldHeight = 200;
        int halfWallThickness = 10;

        Label instructionsLabel = new Label("INSTRUCTIONS:\n" +
                "Cross the lake!\n" +
                "Q and W to bend hip\n" +
                "O and P to bend knee\n" +
                "Escape to restart",
                game.getSkin(), "white"
        );
        instructionsLabel.setPosition(-halfWorldWidth *2, 230);
        addActor(instructionsLabel);

        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.position.set(0, 0);

        Body groundBody = world.createBody(groundBodyDef);
        PolygonShape shape = new PolygonShape();

        shape.setAsBox(halfWorldWidth, halfWallThickness);
        groundBody.createFixture(shape, 0);

        shape.setAsBox(halfWallThickness, halfWorldHeight, new Vector2(-halfWorldWidth - halfWallThickness, halfWorldHeight), 0);
        groundBody.createFixture(shape, 0);
        shape.setAsBox(halfWallThickness, halfWorldHeight, new Vector2(halfWorldWidth + halfWallThickness, halfWorldHeight), 0);
        Fixture endLine = groundBody.createFixture(shape, 0);
        endLine.setUserData(endFixtureID);

        shape.dispose();

        createWater(-halfWorldWidth, halfWallThickness, halfWorldWidth *2, 60, 8);
        createSwan(-halfWorldWidth +50, 100);
        createDecorations(-halfWorldWidth, 80, halfWorldWidth*2);
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
                body.createFixture(fixtureDef).setUserData(waterFixtureID);
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
        entity = new Entity(
                x, y,
                new SpriteComponent(
                        textureRegion,
                        new Vector2(-60,-10)
                ),
                new SwanControllerComponent(world),
                null
        );
        entity.setSize(textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
        addEntity(entity);
    }

    private void createDecorations(float x, float y, float width) {

        CircleShape shape = new CircleShape();
        shape.setRadius(8);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 2f;
        fixtureDef.friction = 0.1f;
        fixtureDef.restitution = 0.6f;

        TextureRegion textureRegion;
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        Body body;
        Entity entity;

        // FROG!
        textureRegion = game.getSkin().getRegion("frog");
        bodyDef.position.x = x + (width/3f);
        bodyDef.position.y = y;
        body = world.createBody(bodyDef);
        body.createFixture(fixtureDef).setUserData(frogFixtureID);
        entity = new Entity(
                x, y,
                new SpriteComponent(
                        textureRegion,
                        true, true
                ),
                new DecorationController(body),
                null
        );
        entity.setSize(textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
        addEntity(entity);

        // DUCKLING!
        textureRegion = game.getSkin().getRegion("duckling");
        bodyDef.position.x = x + (2*width/3f);
        bodyDef.position.y = y;
        body = world.createBody(bodyDef);
        body.createFixture(fixtureDef).setUserData(duckFixtureID);
        entity = new Entity(
                x, y,
                new SpriteComponent(
                        textureRegion,
                        true, true
                ),
                new DecorationController(body),
                null
        );
        entity.setSize(textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
        addEntity(entity);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        timeTaken += delta;

        // Basic physics loop
        physicsCounter += delta;
        while (physicsCounter > TIME_STEP) {
            world.step(TIME_STEP, 6, 2);
            physicsCounter -= TIME_STEP;
        }

        if (!gameOver) {
            if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
                gameLost();
            }
        }
    }

    @Override
    public void draw() {
        getCamera().position.set(entity.getX(), entity.getY(), 0);

        // Draw background and water
        ShapeRenderer shapeRenderer = getShapeRenderer();
        shapeRenderer.setProjectionMatrix(getCamera().combined.scl(2));
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(WATER_COLOR);
        for (Body body: waterBodies) {
            shapeRenderer.circle(body.getPosition().x, body.getPosition().y, particleRadius);
        }

        shapeRenderer.setColor(GRASS_COLOR);
        float left = -halfWorldWidth,
            bottom = -200,
            lakeWidth = halfWorldWidth * 2;
        shapeRenderer.rect(left, bottom, lakeWidth, 210);
        shapeRenderer.rect(left-200, bottom, 200, 300);
        shapeRenderer.rect(halfWorldWidth, bottom, 200, 300);

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
        if (!gameOver) showGameOverWindow(true);
    }

    public void gameLost() {
        if (!gameOver) game.setScene(new PlayScene(game));
    }

    private void showGameOverWindow(boolean won) {
        entity.pause();
        gameOver = true;

        Skin skin = game.getSkin();
        Rectangle cameraRect = getCameraRect();
        Entity entity = new Entity(
                cameraRect.getLeft(), cameraRect.getBottom(),
                null, null, null
        );
        entity.setSize(cameraRect.getWidth(), cameraRect.getHeight());
        addEntity(entity);

        Table table = new Table(skin);
        if (won) {
            table.add("You won!").row();
            table.add("It only took you:").row();
            table.add(timeTaken + " seconds").row();
            table.add("to cross the lake!").row();
            table.add("But could you do better?").row();
        } else {
            table.add("You failed!").row();
            table.add("Better luck next time?").row();
        }
        ExtendedButton retryButton = new ExtendedButton("Try again", skin);
        retryButton.setShortcuts(new char[]{' ', Input.Keys.ENTER});
        retryButton.setListener(new ExtendedButton.ButtonListener() {
            @Override
            public void triggered() {
                game.setScene(new PlayScene(game));
            }
        });
        table.add(retryButton);
        table.pack();
        table.setFillParent(true);
        table.center();
        entity.addActor(table);
        setKeyboardFocus(entity);
    }

    @Override
    protected void onBackButtonPressed() {
        game.setScene(new MainMenuScene(game));
    }
}
