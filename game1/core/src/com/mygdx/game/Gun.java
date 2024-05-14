package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.UBJsonReader;

public class Gun {

    private ModelInstance gunInstance;

    public Gun() {
        // Load gun model
        UBJsonReader jsonReader = new UBJsonReader();
        G3dModelLoader modelLoader = new G3dModelLoader(jsonReader);
        Model gunModel = modelLoader.loadModel(Gdx.files.internal("models/gun.g3db"));
        gunInstance = new ModelInstance(gunModel);
    }

    public ModelInstance getGunInstance() {
        return gunInstance;
    }

    public void updatePosition(Matrix4 playerTransform) {
        // Apply the player's transform to the gun
        Matrix4 gunTransform = new Matrix4(playerTransform);
        Vector3 gunOffset = new Vector3(0.5f, -0.5f, 1f); // Adjust as necessary
        gunTransform.translate(gunOffset);
        gunInstance.transform.set(gunTransform);
    }
}