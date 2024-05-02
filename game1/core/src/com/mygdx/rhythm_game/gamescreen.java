package com.mygdx.rhythm_game;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.InputAdapter;

public class gamescreen extends ScreenAdapter {

    private MyGame game; // Reference to the Game instance
    private SpriteBatch batch;
    private Texture spriteTexture;
    private float x, y; // Sprite position

    public gamescreen(MyGame game) {
        this.game = game; // Store reference to the Game instance

        batch = new SpriteBatch();
        spriteTexture = new Texture("Ship.png");
        x = 100;
        y = 200;
        // Set input processor
        Gdx.input.setInputProcessor(new InputAdapter() {

            public boolean keyDown(int keycode) {
                handleInput(keycode);
                return true; // Return true to indicate that the input was handled
            }
        });
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(spriteTexture, x, y);
        batch.end();
    }

    private void handleInput(int keycode) {
        System.out.println("Key pressed: " + keycode); // Print the keycode
        switch (keycode) {
            case 32:
                x = 100;
                y = 200;
                break;
            case 34:
                x = 200;
                y = 200;
                break;
            case 38:
                x = 400;
                y = 200;
                break;
            case 39:
                x = 500;
                y = 200;
                break;
        }
    }

    private void update(float delta) {
        // Additional update logic can go here
    }

    @Override
    public void dispose() {

        batch.dispose();
        spriteTexture.dispose();

    }
}
