package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import sun.rmi.runtime.Log;
import com.mygdx.game.ModelLoader;
public class Player {

    public PerspectiveCamera camera;
    private Vector3 position;
    public float x;
    public float y;
    public float z;

    public Player() {

        camera = new PerspectiveCamera(150, 1920, 1080);
        camera.near = 0.1f;
        camera.far = 10000f;

        position = new Vector3(0, 80f, 60f);

        camera.position.set(0f,0f,0f); // Set camera position to player's position initially

        camera.lookAt(0f,100f,0f); // Make camera look at player's position

        camera.update();
    }

    public void update() {

        x = position.x;
        y = position.y;
        z = position.z;
        //System.out.println(position.x);
    }

    public void move(Vector3 movement) {
        position.add(movement); // Update player's position
    }

    public void rotate(float angle, Vector3 axis) {
        camera.rotate(axis, angle); // Rotate camera
        camera.update();
    }

    public Camera getCamera() {
        return camera;
    }

    public Vector3 getPosition() {
        return position;
    }

}

/*
public class MyGame extends ApplicationAdapter {

    private PerspectiveCamera camera;
    private ModelBatch modelBatch;
    private ModelInstance playerInstance;

    private Vector3 playerPosition = new Vector3(0, 0, 0); // Example player position
    private float playerRotation = 0; // Example player rotation in degrees

    private float cameraDistance = 5; // Distance from player
    private float cameraHeight = 2; // Height offset from player

    @Override
    public void create() {
        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0, 0, 0); // Initial camera position (will be set in render)
        camera.lookAt(playerPosition); // Camera looks at the player
        camera.near = 1f;
        camera.far = 300f;

        modelBatch = new ModelBatch();

        // Initialize your player model instance
        // playerInstance = new ModelInstance(...); // Set up your player model here

        Gdx.input.setInputProcessor(new CameraInputController(camera));
    }

    @Override
    public void render() {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        // Update camera position relative to player
        updateCameraPosition();

        // Set camera matrices
        camera.update();

        modelBatch.begin(camera);
        // Render your player model instance
        // modelBatch.render(playerInstance);
        modelBatch.end();
    }

    private void updateCameraPosition() {
        // Calculate camera position relative to player
        float offsetX = cameraDistance * MathUtils.sinDeg(playerRotation);
        float offsetZ = cameraDistance * MathUtils.cosDeg(playerRotation);

        float cameraX = playerPosition.x - offsetX;
        float cameraY = playerPosition.y + cameraHeight;
        float cameraZ = playerPosition.z - offsetZ;

        // Set camera position
        camera.position.set(cameraX, cameraY, cameraZ);
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
    }
}*/