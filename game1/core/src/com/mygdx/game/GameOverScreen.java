package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameOverScreen extends ScreenAdapter {
    private SpriteBatch spriteBatch;
    private BitmapFont font;

    public GameOverScreen() {
        spriteBatch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2f);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        spriteBatch.begin();
        font.draw(spriteBatch, "Hai Fallito nella missione GLi skibidi toilet vincono", Gdx.graphics.getWidth() / 2f - 50, Gdx.graphics.getHeight() / 2f);
        spriteBatch.end();
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        font.dispose();
    }
}