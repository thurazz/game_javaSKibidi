package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.UBJsonReader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Monsters {
    private Model monsterModel;

    private List<ModelInstance> monsterInstances;

    public ModelInstance instance;

    private List<BoundingBox> boundingBoxes;

    private Random random;

    public Monsters() {
        UBJsonReader jsonReader = new UBJsonReader();
        G3dModelLoader modelLoader = new G3dModelLoader(jsonReader);
        monsterModel = modelLoader.loadModel(Gdx.files.internal("monsters/Skibidi.g3db"));
        monsterInstances = new ArrayList<>();
        boundingBoxes = new ArrayList<>();
        random = new Random();
    }

    public void spawnMonster() {

        instance = new ModelInstance(monsterModel);

        float x = random.nextFloat() * 50f - 25f; // Random X position within a range

        float z = random.nextFloat() * 50f - 25f; // Random Z position within a range

        instance.transform.setToTranslation(x, -100, z);

        instance.transform.scale(0.1f,0.1f,0.1f);

        monsterInstances.add(instance);

        // Create and store the hitbox for this monster
        BoundingBox boundingBox = new BoundingBox();
        instance.calculateBoundingBox(boundingBox);
        boundingBox.mul(instance.transform);
        boundingBoxes.add(boundingBox);
    }

    public void updateHitboxes() {
        boundingBoxes.clear();
        for (ModelInstance instance : monsterInstances) {
            BoundingBox boundingBox = new BoundingBox();
            instance.calculateBoundingBox(boundingBox);
            boundingBox.mul(instance.transform);
            boundingBoxes.add(boundingBox);
        }
    }

    public void render(ModelBatch modelBatch) {
        for (ModelInstance instance : monsterInstances) {
            modelBatch.render(instance);
        }
    }

    public List<ModelInstance> getMonsters() {
        return monsterInstances;
    }

    public List<BoundingBox> getBoundingBoxes() {
        return boundingBoxes;
    }

    public void dispose() {
        monsterModel.dispose();
    }
}