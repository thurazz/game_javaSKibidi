package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;
import sun.rmi.runtime.Log;

public class Player {

    private PerspectiveCamera camera;
    private Vector3 position;
    public float x;
    public float y;
    public float z;

    public Player() {
        camera = new PerspectiveCamera(70, 800, 600);
        camera.near = 0.1f;
        camera.far = 10000f;

        position = new Vector3(0, 180f, 105);
        camera.position.set(position); // Set camera position to player's position initially
        camera.lookAt(position); // Make camera look at player's position
        camera.update();
    }

    public void update() {
        x = position.x;
        y = position.y;
        z = position.z;
        //System.out.println(x);
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
