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
    private List<BoundingBox> hitboxes;
    private Random random;

    public Monsters(String modelPath) {
        UBJsonReader jsonReader = new UBJsonReader();
        G3dModelLoader modelLoader = new G3dModelLoader(jsonReader);
        monsterModel = modelLoader.loadModel(Gdx.files.internal(modelPath));
        monsterInstances = new ArrayList<>();
        hitboxes = new ArrayList<>();
        random = new Random();
    }

    public void spawnMonster() {

        ModelInstance instance = new ModelInstance(monsterModel);

        float x = random.nextFloat() * 50f - 25f; // Random X position within a range

        float z = random.nextFloat() * 50f - 25f; // Random Z position within a range

        instance.transform.setToTranslation(x, -100, z);
        instance.transform.scale(0.1f,0.1f,0.1f);
        monsterInstances.add(instance);

        // Create and store the hitbox for this monster
        BoundingBox hitbox = new BoundingBox();
        instance.calculateBoundingBox(hitbox);
        hitbox.mul(instance.transform);
        hitboxes.add(hitbox);
    }

    public void updateHitboxes() {
        for (int i = 0; i < monsterInstances.size(); i++) {
            ModelInstance instance = monsterInstances.get(i);
            BoundingBox hitbox = hitboxes.get(i);
            instance.calculateBoundingBox(hitbox);
            hitbox.mul(instance.transform);
        }
    }

    public void render(ModelBatch modelBatch, Camera camera) {
        modelBatch.begin(camera);
        for (ModelInstance instance : monsterInstances) {
            modelBatch.render(instance);
        }
        modelBatch.end();
    }

    public List<ModelInstance> getMonsters() {
        return monsterInstances;
    }

    public List<BoundingBox> getHitboxes() {
        return hitboxes;
    }

    public void dispose() {
        monsterModel.dispose();
    }
}