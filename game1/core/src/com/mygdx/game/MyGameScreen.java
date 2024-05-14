package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;


public class MyGameScreen extends ScreenAdapter {

    private final Vector3 tmpVec = new Vector3();
    private MyEnvironment environment;
    private Player player;
    private ModelBatch modelBatch;
    private boolean isMouseVisible = true;
    private ModelLoader modelLoader;
    private ModelInstance playerInstance;

    private Vector3 playerPosition = new Vector3(0, 0, 0);
    private Vector3 cameraPosition = new Vector3(0f, 0f, 0f);
    private final float rotateSpeed = 10f; // Adjust camera rotation speed

    public MyGameScreen() {
        environment = new MyEnvironment();
        player = new Player();
        modelBatch = new ModelBatch();

        Gdx.input.setCursorCatched(true);
        Gdx.input.setCursorPosition(700, 400);

        modelLoader = new ModelLoader(new World(new Vector2(0, -9.8f), false));
        playerInstance = modelLoader.getPlayerInstance();
        playerPosition = playerInstance.transform.getTranslation(new Vector3());

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                handleInput();
                return true;
            }

            @Override
            public boolean keyUp(int keycode) {
                handleInput();
                return true;
            }

            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                handleMouse(screenX, screenY);
                return true;
            }
        });
    }

    private void handleInput() {
        float moveSpeed = 3f;

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (isMouseVisible) {
                Gdx.input.setCursorCatched(true); // Hide cursor
                isMouseVisible = false;
            } else {
                Gdx.input.setCursorCatched(false); // Show cursor
                isMouseVisible = true;
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            playerInstance.transform.translate(0f, 0f, -moveSpeed * Gdx.graphics.getDeltaTime());
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            playerInstance.transform.translate(0f, 0f, moveSpeed * Gdx.graphics.getDeltaTime());
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            playerInstance.transform.translate(-moveSpeed * Gdx.graphics.getDeltaTime(), 0f, 0f);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            playerInstance.transform.translate(moveSpeed * Gdx.graphics.getDeltaTime(), 0f, 0f);
        }
    }

    private void handleMouse(int screenX, int screenY) {
        float deltaX = -Gdx.input.getDeltaX() * rotateSpeed;
        float deltaY = -Gdx.input.getDeltaY() * rotateSpeed;

        // Rotate the player instance around the Y-axis based on mouse X movement
        playerInstance.transform.rotate(Vector3.Y, deltaX);

        Vector3 right = player.getCamera().direction.cpy().crs(Vector3.Y).nor();
        player.getCamera().direction.rotate(right, deltaY);

        // Normalize camera direction and update the camera
        player.getCamera().direction.nor();
        player.getCamera().up.set(Vector3.Y);

        player.getCamera().update();
    }

    private void updateCameraPosition() {
        // Get the player's current position and orientation
        Vector3 playerPosition = playerInstance.transform.getTranslation(new Vector3());

        // Calculate the camera position behind the player
        float distanceBehind = -5f; // Adjust this distance as needed
        float offsetHeight = 2f; // Adjust the height offset from the player's position

        // Transform the camera position based on the player's orientation
        Vector3 cameraOffset = new Vector3(0, offsetHeight, -distanceBehind);
        playerInstance.transform.getRotation(new Quaternion()).transform(cameraOffset);

        cameraPosition.set(playerPosition).add(cameraOffset);

        // Update the camera's position and look at the player
        player.getCamera().position.set(cameraPosition);
        player.getCamera().lookAt(playerPosition);
        player.getCamera().up.set(Vector3.Y);
    }

    private void cameraOrientation() {
        // Rotate the camera based on mouse movement
        float deltaX = -Gdx.input.getDeltaX() * rotateSpeed;
        float deltaY = -Gdx.input.getDeltaY() * rotateSpeed;

        // Rotate the player instance around the Y-axis based on mouse X movement
        playerInstance.transform.rotate(Vector3.Y, deltaX);

        // Rotate the camera around its right vector (pitch) based on mouse Y movement
        Vector3 right = player.getCamera().direction.cpy().crs(Vector3.Y).nor();
        player.getCamera().direction.rotate(right, deltaY);

        // Normalize camera direction and update the camera
        player.getCamera().direction.nor();
        player.getCamera().up.set(Vector3.Y);

        player.getCamera().update();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.6f, 0.8f, 1f, 1f); // R, G, B, alpha
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

        handleInput();

        // Update the player's position
        playerPosition = playerInstance.transform.getTranslation(new Vector3());

        // Calculate the camera's position and orientation
        updateCameraPosition();

        // Orient the camera to follow the player's position and direction
        cameraOrientation();

        player.getCamera().update(); // Update the camera

        // Render the environment and player model
        environment.render(modelBatch, player.getCamera());

        if (playerInstance != null) {
            modelBatch.begin(player.getCamera());
            modelBatch.render(playerInstance);
            modelBatch.end();
        }
    }

    public void show() {
        super.show();
        // Hide the mouse cursor and lock it within the game window
        Gdx.input.setCursorCatched(false);
        //Gdx.input.setCursorCatched(true);
        isMouseVisible = false;
    }

    @Override
    public void hide() {
        super.hide();
        // Restore the mouse cursor when the game is not active
        Gdx.input.setCursorCatched(false);
        isMouseVisible = true;
    }

    @Override
    public void dispose() {
        super.dispose();
        modelBatch.dispose();
    }
}