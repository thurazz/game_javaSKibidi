package com.mygdx.game;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;

public class Player {

    private PerspectiveCamera camera;
    private Vector3 position;

    public Player() {
        camera = new PerspectiveCamera(80, 800, 600);
        camera.near = 0.1f;
        camera.far = 10000f;

        position = new Vector3(0, 0, 0);
        camera.position.set(position); // Set camera position to player's position initially
        camera.lookAt(position); // Make camera look at player's position
        camera.update();
    }

    public void update() {
        // Update player logic based on input or game state
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
