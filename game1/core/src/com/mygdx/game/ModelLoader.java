package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.UBJsonReader;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class ModelLoader {

    private Body groundBody;
    private Body playerBody;
    private Player player;
    private ModelInstance groundInstance;
    private ModelInstance playerInstance;
    private World world;


    public ModelLoader(World world) {
        this.world = world;
        this.player = new Player();
        world.setGravity(new Vector2(0, -9.8f)); // Set gravity to Earth-like gravity (downward)
        // Load ground model (assuming "ground.g3db" exists in assets folder)
        UBJsonReader jsonReader = new UBJsonReader();
        G3dModelLoader modelLoader = new G3dModelLoader(jsonReader);
        Model groundModel = modelLoader.loadModel(Gdx.files.internal("models/LP.g3db"));
        groundInstance = new ModelInstance(groundModel);

        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.type = BodyDef.BodyType.StaticBody;
        groundBodyDef.position.set(0, 0); // Set position of the ground

        // Create ground body in the Box2D world
        groundBody = world.createBody(groundBodyDef);

        // Define ground shape (box shape)
        PolygonShape groundShape = new PolygonShape();
        groundShape.setAsBox(25f / 2f, 1f / 2f); // Set half-width and half-height of the ground box

        // Create fixture definition for the ground
        FixtureDef groundFixtureDef = new FixtureDef();
        groundFixtureDef.shape = groundShape;
        groundFixtureDef.density = 1.0f; // Density (irrelevant for static bodies)
        groundFixtureDef.friction = 0.3f; // Friction
        groundFixtureDef.restitution = 0.5f; // Restitution (bounciness)

        // Attach the fixture to the ground body
        groundBody.createFixture(groundFixtureDef);

        // Clean up the ground shape
        groundShape.dispose();



        groundInstance.transform.setToTranslation(0f, 0f, 0f);


        // Load player model (assuming "player.g3db" exists in assets folder)
        Model playerModel = modelLoader.loadModel(Gdx.files.internal("player.g3db"));
        playerInstance = new ModelInstance(playerModel);

        BodyDef playerBodyDef = new BodyDef();
        playerBodyDef.type = BodyDef.BodyType.DynamicBody;
        playerBodyDef.position.set(0, 10); // Set initial position of the player

        // Create player body in the Box2D world
        playerBody = world.createBody(playerBodyDef);

        // Define player shape (box shape)
        PolygonShape playerShape = new PolygonShape();
        playerShape.setAsBox(5f / 2f, 10f / 2f); // Set half-width and half-height of the player box

        // Create fixture definition for the player
        FixtureDef playerFixtureDef = new FixtureDef();
        playerFixtureDef.shape = playerShape;
        playerFixtureDef.density = 1.0f; // Density
        playerFixtureDef.friction = 0.3f; // Friction
        playerFixtureDef.restitution = 0.2f; // Restitution

        // Attach the fixture to the player body
        playerBody.createFixture(playerFixtureDef);

        // Clean up the player shape
        playerShape.dispose();

        // Position the player slightly above the ground
        playerInstance.transform.translate(0, 200f, 0);

        // Scale the player model
        playerInstance.transform.scale(100f, 100f, 100f);
        groundInstance.transform.scale(25f, 1f, 18f); // Adjust the scale factor as needed
    }

    public void render(ModelBatch modelBatch) {
        // Update player logic (e.g., movement)

        player.update();

        // Sync camera position and orientation with player's position and orientation
        player.getCamera().position.set(player.getPosition()); // Update camera position
        player.getCamera().update(); // Update camera

        // Render player and ground instances

        modelBatch.render(groundInstance);

        modelBatch.render(playerInstance);
        

        // Apply gravity to the player's body
        playerBody.applyForceToCenter(0, -9.8f * playerBody.getMass(), true);
    }

    public void update(){
        player.update();
        playerBody.applyForceToCenter(0, -playerBody.getMass() * world.getGravity().y, true);
    }
}