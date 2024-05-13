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
