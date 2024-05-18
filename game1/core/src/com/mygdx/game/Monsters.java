package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.UBJsonReader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Monsters {
    private Model monsterModel;
    public List<ModelInstance> monsterInstances;
    public List<BoundingBox> boundingBoxes;
    public List<Float> monsterHealth;
    private ShapeRenderer shapeRenderer;
    private MyGameScreen player;
    private List<Vector3> healthBarPositions;
    private int monsterkilled;
    private float monsterSpeed;
    SpriteBatch batch ;
    BitmapFont font;
    private Bullet gun;

    private float Healt;
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

        batch = new SpriteBatch();
        font = new BitmapFont();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font/font.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 50;
        font = generator.generateFont(parameter);
        generator.dispose();
        Healt = 100f;
        monsterSpeed = 400f;
    }

    public void monstermovment() {
        Vector3 playerPosition = player.getPlayerPosition();

        for (int i = 0; i < monsterInstances.size(); i++) {
            ModelInstance instance = monsterInstances.get(i);
            Vector3 monsterPosition = instance.transform.getTranslation(new Vector3());

            // Calculate the direction vector from the monster to the player
            Vector3 direction = new Vector3(playerPosition).sub(monsterPosition).nor();

            //move the monster to player location
            monsterPosition.add(direction.scl(monsterSpeed * Gdx.graphics.getDeltaTime()));

            //update VEctor 3 monster to player
            instance.transform.setTranslation(monsterPosition);

            updateHealthBarPositions();
        }
    }

    public void removeDeadMonsters() {
        Iterator<ModelInstance> instanceIterator = monsterInstances.iterator();
        Iterator<BoundingBox> boxIterator = boundingBoxes.iterator();
        Iterator<Float> healthIterator = monsterHealth.iterator();

        while (instanceIterator.hasNext()) {
            ModelInstance instance = instanceIterator.next();
            BoundingBox box = boxIterator.next();
            float health = healthIterator.next();

            if (health <= 0) {
                // Remove instance, box, and health
                Sound sound = Gdx.audio.newSound(Gdx.files.internal("SKibidiSound/dead.mp3"));
                sound.play(0.4f);
                instanceIterator.remove();
                boxIterator.remove();
                healthIterator.remove();
                monsterkilled ++;
                if(monsterkilled % 3  == 0){
                    Healt = Healt+10;
                    monsterSpeed = monsterSpeed +20f;
                }
                if(monsterkilled % 5 == 0 && player.spawnInterval >=2){
                    player.spawnInterval --;
                }
            }
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
            float barZ = (boundingBox.min.z + boundingBox.max.z) / 2 ; // Centered along the z-axis

            healthBarPositions.add(new Vector3(barX, barY, boundingBox.min.z));
        }
    }

    public void spawnMonster() {
        float x = MathUtils.random(-7000f, 7000f);
        float z = MathUtils.random(-7000f, 7000f);

        Sound sound = Gdx.audio.newSound(Gdx.files.internal("SkibidiSound/spawn.mp3"));
        sound.play(1.0f);

        ModelInstance instance = new ModelInstance(monsterModel);
        instance.transform.setToTranslation(x,1,z);
        instance.transform.scale(0.1f, 0.1f, 0.1f);

        monsterInstances.add(instance);
        monsterHealth.add(Healt);

        // Create and store the hitbox for this monster
        BoundingBox boundingBox = new BoundingBox();
        instance.calculateBoundingBox(boundingBox);
        boundingBox.mul(instance.transform);
        boundingBoxes.add(boundingBox);
        updateHealthBarPositions();
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

        batch.begin();
        font.draw(batch, "SKibidi uccisi: " +""+monsterkilled, 1500, 950);
        batch.end();
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
    public List<Float> getMonsterHealt(){
        return monsterHealth;
    }

    public void updateMonsterHealth(int index, float health) {
        setMonsterHealth(index, Math.max(0, health)); // Ensure health doesn't go below 0

    }

    public List<BoundingBox> getBoundingBoxes() {
        return boundingBoxes;
    }

    public void setMonsterHealth(int index, float health) {
        if (index >= 0 && index < monsterHealth.size()) {
            monsterHealth.set(index, health);
        } else {
            System.out.println("Invalid monster index.");
        }
    }

    public void dispose() {
        monsterModel.dispose();
        shapeRenderer.dispose();
    }
}