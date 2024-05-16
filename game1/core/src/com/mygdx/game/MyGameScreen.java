package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MyGameScreen extends ScreenAdapter {
    private final Vector3 tmpVec = new Vector3();
    private MyEnvironment environment;
    private Player player;
    private ModelBatch modelBatch;
    private boolean isMouseVisible = true;
    private ModelLoader modelLoader;
    private ModelInstance playerInstance;
    private AnimationController animationController;
    private Vector3 playerPosition = new Vector3(0, 0, 0);
    private Vector3 cameraPosition = new Vector3(0f, 0f, 0f);
    private final float rotateSpeed = 10f;
    private List<Bullet> bullets;
    private float spawnTimer = 0;
    private final float spawnInterval = 5f;
    private Monsters monsters;

    public MyGameScreen() {
        environment = new MyEnvironment();
        player = new Player();
        modelBatch = new ModelBatch();
        bullets = new ArrayList<>();

        Gdx.input.setCursorCatched(true);
        Gdx.input.setCursorPosition(700, 400);

        modelLoader = new ModelLoader(new World(new Vector2(0, -9.8f), false));
        playerInstance = modelLoader.getPlayerInstance();

        animationController = new AnimationController(playerInstance);
        playerPosition = playerInstance.transform.getTranslation(new Vector3());

        monsters = new Monsters("monsters/Skibidi.g3db");

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

            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (button == Input.Buttons.LEFT) {
                    shootBullet();
                }
                return true;
            }
        });
    }

    private void handleInput() {
        float moveSpeed = 3f;

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (isMouseVisible) {
                Gdx.input.setCursorCatched(true);
                isMouseVisible = false;
            } else {
                Gdx.input.setCursorCatched(false);
                isMouseVisible = true;
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            playerInstance.transform.translate(0f, 0f, -moveSpeed * Gdx.graphics.getDeltaTime());
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            playerInstance.transform.translate(0f, 0f, moveSpeed * Gdx.graphics.getDeltaTime());
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            playerInstance.transform.translate(-moveSpeed * Gdx.graphics.getDeltaTime(), 0f, 0f);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            playerInstance.transform.translate(moveSpeed * Gdx.graphics.getDeltaTime(), 0f, 0f);
        }
    }

    private void updateCameraPosition(float deltaTime) {
        Vector3 playerPosition = playerInstance.transform.getTranslation(new Vector3());
        float distanceBehind = -2f;
        float offsetHeight = 1f;
        Vector3 cameraOffset = new Vector3(0, offsetHeight, -distanceBehind);
        Quaternion playerRotation = playerInstance.transform.getRotation(new Quaternion());
        playerRotation.transform(cameraOffset);
        Vector3 cameraPosition = new Vector3(playerPosition).add(cameraOffset);
        player.getCamera().position.set(cameraPosition);
        player.getCamera().lookAt(playerPosition);
        player.getCamera().up.set(Vector3.Y);
    }

    private void handleMouse(int screenX, int screenY) {
        float deltaX = -Gdx.input.getDeltaX() * rotateSpeed;
        float deltaY = -Gdx.input.getDeltaY() * rotateSpeed;
        playerInstance.transform.rotate(Vector3.Y, deltaX);
        Vector3 right = player.camera.direction.cpy().crs(Vector3.Y).nor();
        player.camera.direction.rotate(deltaY, right.x, right.y, right.z);
        player.getCamera().direction.nor();
        player.getCamera().up.set(Vector3.Y);
        player.getCamera().update();
    }

    private void shootBullet() {
        Vector3 playerPosition = playerInstance.transform.getTranslation(new Vector3());
        Vector3 bulletDirection = player.camera.direction.cpy();
        bullets.add(new Bullet(playerPosition, bulletDirection));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.6f, 0.8f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

        animationController.update(Gdx.graphics.getDeltaTime());

        handleInput();
        playerPosition = playerInstance.transform.getTranslation(new Vector3());
        updateCameraPosition(Gdx.graphics.getDeltaTime());
        handleMouse(1920, 1080);
        player.getCamera().update();

        // Spawn monsters at intervals
        spawnTimer += delta;
        if (spawnTimer >= spawnInterval) {
            monsters.spawnMonster();
            spawnTimer = 0;
        }

        // Update monster hitboxes
        monsters.updateHitboxes();

        // Render environment, player, bullets, and monsters
        environment.render(modelBatch, player.getCamera());
        modelBatch.begin(player.getCamera());
        if (playerInstance != null) {
            modelBatch.render(playerInstance);
        }
        for (Bullet bullet : bullets) {
            bullet.update(delta);
            modelBatch.render(bullet.getBulletInstance());
        }
        modelBatch.end();
        monsters.render(modelBatch, player.getCamera());
    }

    @Override
    public void show() {
        super.show();
        Gdx.input.setCursorCatched(false);
        isMouseVisible = false;
    }

    @Override
    public void hide() {
        super.hide();
        Gdx.input.setCursorCatched(false);
        isMouseVisible = true;
    }

    @Override
    public void dispose() {
        super.dispose();
        modelBatch.dispose();
        monsters.dispose();
    }
}