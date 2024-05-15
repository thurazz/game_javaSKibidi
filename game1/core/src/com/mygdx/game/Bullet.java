package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.UBJsonReader;
import java.util.ArrayList;
import java.util.Iterator;

public class Bullet {
    private ModelInstance bulletInstance;
    private Vector3 direction;
    private Vector3 position;

    private float speed = 400f; // Adjust speed as needed

    private BoundingBox boundingBox;

    public Bullet(Vector3 position, Vector3 direction) {
        // Load bullet model
        UBJsonReader jsonReader = new UBJsonReader();
        G3dModelLoader modelLoader = new G3dModelLoader(jsonReader);
        Model bulletModel = modelLoader.loadModel(Gdx.files.internal("skinmodel1/player.g3db"));

        bulletInstance = new ModelInstance(bulletModel);

        bulletInstance.transform.scale(100f,100f,100f);

        this.position = new Vector3(position);

        this.direction = new Vector3(direction).nor(); // Normalize direction

        bulletInstance.transform.setTranslation(this.position);

        boundingBox = new BoundingBox();
        bulletInstance.calculateBoundingBox(boundingBox);
        boundingBox.mul(bulletInstance.transform);
    }

    public void update(float deltaTime) {
        // Move the bullet in the direction it's facing

        Vector3 movement = new Vector3(direction.x, 0f, direction.z).scl(speed * deltaTime);

        position.add(movement);

        bulletInstance.transform.setTranslation(position);
        //update of the box coollider
        boundingBox.set(bulletInstance.calculateBoundingBox(new BoundingBox()).mul(bulletInstance.transform));
    }

    public ModelInstance getBulletInstance() {
        return bulletInstance;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public boolean isOutOfBounds() {
        // Define out-of-bounds logic, e.g., if the bullet is too far from the player or game world
        return position.len() > 10000; // Example condition
    }
}