package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;

public class MyGameScreen extends ScreenAdapter {

    private MyEnvironment environment;
    private Player player;
    private ModelBatch modelBatch;
    private boolean isMouseVisible = true;
    private int keycode;

    public MyGameScreen() {
        environment = new MyEnvironment();
        player = new Player();
        modelBatch = new ModelBatch();

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
        Vector3 moveDirection = new Vector3();

        if (keycode == 111) {
            if (isMouseVisible) {
                Gdx.input.setCursorCatched(true);
                isMouseVisible = false;
            } else {
                Gdx.input.setCursorCatched(false);
                isMouseVisible = true;
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            player.getPosition().add(player.getCamera().direction.cpy().scl(moveSpeed));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            player.getPosition().sub(player.getCamera().direction.cpy().scl(moveSpeed));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            player.getPosition().add(player.getCamera().direction.cpy().crs(Vector3.Y).nor().scl(-moveSpeed));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            player.getPosition().add(player.getCamera().direction.cpy().crs(Vector3.Y).nor().scl(moveSpeed));
        }

        // Update camera orientation based on mouse movement
        handleMouse(Gdx.input.getX(), Gdx.input.getY());

    }

    private void handleMouse(int screenX, int screenY) {
        float rotateSpeed = 0.2f;

        // Horizontal rotation around the Y-axis based on mouse X movement
        player.getCamera().rotate(Vector3.Y, -Gdx.input.getDeltaX() * rotateSpeed);
        player.getCamera().direction.rotate(player.getCamera().up, -Gdx.input.getDeltaX() * rotateSpeed);

        // Calculate the right vector (camera's right direction)
        Vector3 right = player.getCamera().direction.cpy().crs(player.getCamera().up).nor();

        // Vertical rotation around the calculated right vector based on mouse Y movement
        player.getCamera().direction.rotate(right, -Gdx.input.getDeltaY() * rotateSpeed);

        // Ensure the camera's direction remains normalized
        player.getCamera().direction.nor();

        // Update the camera to apply the changes
        player.getCamera().update();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.6f, 0.8f, 1f, 1f); // R, G, B, alpha
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

        // Handle player input and update logic
        handleInput();
        player.update();

        // Update camera position to match player position
        player.getCamera().position.set(player.getPosition());
        player.getCamera().update();

        // Render the environment with the camera
        environment.render(modelBatch, player.getCamera());
    }

    public void show() {
        super.show();
        // Hide the mouse cursor and lock it within the game window
        Gdx.input.setCursorCatched(true);
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