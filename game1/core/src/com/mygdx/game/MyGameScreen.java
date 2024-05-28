package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.dongbat.jbump.util.MathUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.dongbat.jbump.util.MathUtils.random;

public class MyGameScreen extends ScreenAdapter {
    private final Vector3 tmpVec = new Vector3();
    private MyEnvironment environment;
    private Player player;
    private ModelBatch modelBatch;
    private boolean isMouseVisible = true;
    private ModelLoader modelLoader;
    private ModelInstance playerInstance;
    private AnimationController animationController;
    private Bullet gun;
    private Vector3 playerPosition = new Vector3(0, 0, 0);
    private Vector3 cameraPosition = new Vector3(0f, 0f, 0f);

    public Quaternion playerPositionQuaternion = new Quaternion();
    public Matrix4 playerPositionMatrix4 = new Matrix4();
    private final float rotateSpeed = 0.2f;
    private List<Bullet> bullets;

    private float spawnTimer = 0;

    public float spawnInterval = 6f;

    private Monsters monsters;
    public boolean gameOver = false;
    private MyGame game;

    private int c=0;

    //private Monsters monsters;

    public MyGameScreen() {
        environment = new MyEnvironment();
        player = new Player();
        modelBatch = new ModelBatch();
        bullets = new ArrayList<>();
        game = new MyGame();
        gun = new Bullet();
        Gdx.input.setCursorCatched(true);
        Gdx.input.setCursorPosition(700, 400);

        modelLoader = new ModelLoader(new World(new Vector2(0, -9.8f), false));

        playerInstance = modelLoader.getPlayerInstance();

        animationController = new AnimationController(playerInstance);

        playerPosition = playerInstance.transform.getTranslation(new Vector3());
        playerPositionQuaternion = playerInstance.transform.getRotation(new Quaternion());
        playerPositionMatrix4.set(playerPositionQuaternion);
        monsters = new Monsters(this);

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

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (button == Input.Buttons.LEFT) {
                    shootBullet();
                }
                return true;
            }
        });
    }
    public Vector3 getPlayerPosition(){
        Vector3 position = new Vector3(playerInstance.transform.getTranslation(new Vector3()));
        position.x += 10f;
        position.y  = 1f;
        return position;
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

        player.camera.transform(playerPositionMatrix4);
    }
    public int getRotation(){
        return Gdx.input.getDeltaX();
    }
    private void handleMouse(int screenX, int screenY) {
        float deltaX = -Gdx.input.getDeltaX() * rotateSpeed;
        float deltaY = -Gdx.input.getDeltaY() * rotateSpeed;
        playerInstance.transform.rotate(Vector3.Y, deltaX);
        Vector3 right = player.getCamera().direction.cpy().crs(Vector3.Y).nor();
        player.getCamera().direction.rotate(deltaY, right.x, right.y, right.z);
        player.getCamera().direction.nor();
        player.getCamera().up.set(Vector3.Y);
        player.getCamera().update();

    }

    private void shootBullet() {
        Vector3 playerPosition = playerInstance.transform.getTranslation(new Vector3());
        Vector3 bulletDirection = player.getCamera().direction.cpy();
        bullets.add(new Bullet(playerPosition, bulletDirection));
    }
    @Override
    public void render(float delta) {
        if (gameOver) {
            SpriteBatch batch = new SpriteBatch();
            Texture img = new Texture("SkibidiSound/SkibiFail.png");

            Sound sound = Gdx.audio.newSound(Gdx.files.internal("SkibidiSound/fnaf.mp3"));
            if(c==0){
                sound.play(0.5f);
                c=1;
            }
            batch.begin();
            batch.draw(img, 0, 0);
            batch.end();
            return;
        }

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
        monsters.monstermovment();

        if (checkPlayerMonsterCollision()) {
            gameOver = true;
            System.out.println("Skibiiii");
        }
        checkBulletCollisions();
        checkPlayerMonsterCollision();

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
        monsters.render(modelBatch, player.getCamera());
        modelBatch.end();

    }

    private void checkBulletCollisions() {

        for (Iterator<Bullet> bulletIterator = bullets.iterator(); bulletIterator.hasNext();) {
            Bullet bullet = bulletIterator.next();
            BoundingBox bulletBox = bullet.getBoundingBox();

            // check sui mostri
            for (int i = 0; i < monsters.getMonsters().size(); i++) {
                BoundingBox monsterBox = monsters.getBoundingBoxes().get(i);

                // Check for collision
                if (bulletBox.intersects(monsterBox)) {
                    // Collision detected
                    monsters.updateMonsterHealth(i, monsters.getMonsterHealt().get(i) - gun.dmg); // Reduce health by 5
                    bulletIterator.remove(); // Remove bullet
                    monsters.removeDeadMonsters();
                    break; //stop ai check
                }
            }
        }
    }

    private boolean checkPlayerMonsterCollision() {
        BoundingBox playerBox = new BoundingBox();
        playerInstance.calculateBoundingBox(playerBox);
        playerBox.mul(playerInstance.transform);

        //defines if the monster is in the player AREA
        Vector3 offset = new Vector3(260f, 5f, 265f);
        BoundingBox playerSpace = new BoundingBox(playerBox.min.cpy().sub(offset), playerBox.max.cpy().add(offset));

        // Check su tutti i mostri
        for (BoundingBox monsterBox : monsters.getBoundingBoxes()) {
            // Check for collision
            if (playerSpace.intersects(monsterBox)) {
                return true; // Collision detected
            }
        }
        return false;
    }

    public void resize(int width, int height) {
        player.getCamera().viewportWidth = width;
        player.getCamera().viewportHeight = height;
        player.getCamera().update();
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