package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
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
    private List<BoundingBox> boundingBoxes;
    private List<Float> monsterHealth;
    private ShapeRenderer shapeRenderer;
    private MyGameScreen player;
    private List<Vector3> healthBarPositions;
    private float monsterSpeed = 150.0f;

    public Monsters(MyGameScreen player) {
        UBJsonReader jsonReader = new UBJsonReader();
        G3dModelLoader modelLoader = new G3dModelLoader(jsonReader);
        monsterModel = modelLoader.loadModel(Gdx.files.internal("monsters/Skibidi.g3db"));
        monsterInstances = new ArrayList<>();
        boundingBoxes = new ArrayList<>();
        monsterHealth = new ArrayList<>();
        shapeRenderer = new ShapeRenderer();
        healthBarPositions = new ArrayList<>();
        this.player = player;
    }

    public void monstermovment() {
        Vector3 playerPosition = player.getPlayerPosition();

        for (int i = 0; i < monsterInstances.size(); i++) {
            ModelInstance instance = monsterInstances.get(i);
            Vector3 monsterPosition = instance.transform.getTranslation(new Vector3());

            // Calculate the direction vector from the monster to the player
            Vector3 direction = new Vector3(playerPosition).sub(monsterPosition).nor();

            // Move the monster towards the player
            monsterPosition.add(direction.scl(monsterSpeed * Gdx.graphics.getDeltaTime()));

            // Update the monster's transformation
            instance.transform.setTranslation(monsterPosition);
            updateHealthBarPositions();
        }
    }

    private void updateHealthBarPositions() {
        healthBarPositions.clear(); // Clear existing positions

        for (ModelInstance instance : monsterInstances) {
            BoundingBox boundingBox = new BoundingBox();
            instance.calculateBoundingBox(boundingBox);
            boundingBox.mul(instance.transform);

            // Calculate the position for the health bar (top center of the bounding box)
            float barX = (boundingBox.min.x + boundingBox.max.x) / 2;
            float barY = boundingBox.max.y + 0.5f; // Slightly above the bounding box
            float barZ = (boundingBox.min.z + boundingBox.max.z) / 2; // Centered along the z-axis

            healthBarPositions.add(new Vector3(barX, barY, barZ));
        }
    }

    public void spawnMonster() {
        float x = MathUtils.random(-2000f, 2000f);
        float z = MathUtils.random(-2000f, 2000f);

        ModelInstance instance = new ModelInstance(monsterModel);

        instance.transform.setTranslation(x,-75f,z);
        if(x < 0 && z < 0){
            instance.transform.rotate(Vector3.Y,180);
        }
        if(x>0 && z<0){
            instance.transform.rotate(Vector3.Y,180);
        }


        System.out.println(instance.transform);
        System.out.println(x);
        System.out.println(z);

        //instance.transform.setToTranslation(x, -100f, z);

        instance.transform.scale(0.1f, 0.1f, 0.1f);

        monsterInstances.add(instance);
        monsterHealth.add(100f);

        // Create and store the hitbox for this monster
        BoundingBox boundingBox = new BoundingBox();
        instance.calculateBoundingBox(boundingBox);
        boundingBox.mul(instance.transform);
        boundingBoxes.add(boundingBox);
    }

    public void updateHitboxes() {
        for (int i = 0; i < monsterInstances.size(); i++) {
            BoundingBox boundingBox = boundingBoxes.get(i);
            ModelInstance instance = monsterInstances.get(i);
            instance.calculateBoundingBox(boundingBox);
            boundingBox.mul(instance.transform);
        }
    }

    public void render(ModelBatch modelBatch, Camera camera) {
        for (ModelInstance instance : monsterInstances) {
            modelBatch.render(instance);
        }

        // Render hitboxes
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        for (BoundingBox box : boundingBoxes) {
            renderBoundingBox(box);
        }
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < monsterInstances.size(); i++) {
            renderHealthBar(boundingBoxes.get(i), monsterHealth.get(i));
        }
        shapeRenderer.end();
    }

    private void renderHealthBar(BoundingBox box, float health) {
        Vector3 min = box.min;
        Vector3 max = box.max;

        // Calculate the position for the health bar
        float barX = (min.x + max.x) / 2;
        float barY = max.y + 0.5f; // Slightly above the bounding box
        float barZ = (min.z + max.z) / 2;

        float barWidth = 500.0f;
        float barHeight = 70f;
        float healthPercentage = health / 100f;

        // Render the background of the health bar (gray)
        shapeRenderer.setColor(Color.GRAY);
        shapeRenderer.rect(barX - barWidth / 2, barY, barWidth, barHeight);

        // Render the foreground of the health bar (green)
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(barX - barWidth / 2, barY, barWidth * healthPercentage, barHeight);
    }

    private void renderBoundingBox(BoundingBox box) {
        Vector3 min = box.min;
        Vector3 max = box.max;

        // Draw lines between the corners of the bounding box
        shapeRenderer.line(min.x, min.y, min.z, max.x, min.y, min.z);
        shapeRenderer.line(min.x, min.y, min.z, min.x, max.y, min.z);
        shapeRenderer.line(min.x, min.y, min.z, min.x, min.y, max.z);

        shapeRenderer.line(max.x, max.y, max.z, min.x, max.y, max.z);
        shapeRenderer.line(max.x, max.y, max.z, max.x, min.y, max.z);
        shapeRenderer.line(max.x, max.y, max.z, max.x, max.y, min.z);

        shapeRenderer.line(min.x, max.y, min.z, max.x, max.y, min.z);
        shapeRenderer.line(min.x, max.y, min.z, min.x, max.y, max.z);

        shapeRenderer.line(max.x, min.y, min.z, max.x, max.y, min.z);
        shapeRenderer.line(max.x, min.y, min.z, max.x, min.y, max.z);

        shapeRenderer.line(min.x, min.y, max.z, max.x, min.y, max.z);
        shapeRenderer.line(min.x, min.y, max.z, min.x, max.y, max.z);
    }

    public List<ModelInstance> getMonsters() {
        return monsterInstances;
    }

    public List<BoundingBox> getBoundingBoxes() {
        return boundingBoxes;
    }

    public void dispose() {
        monsterModel.dispose();
        shapeRenderer.dispose();
    }
}