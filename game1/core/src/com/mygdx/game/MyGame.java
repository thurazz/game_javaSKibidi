package com.mygdx.game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
//import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class MyGame extends Game{

    private MyGameScreen gameScreen;
    private GameOverScreen gameover;

    @Override
    public void create() {

        gameScreen = new MyGameScreen();
    }

    @Override
    public void render() {
        gameScreen.render(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void resize(int width, int height) {
        gameScreen.resize(width, height);
    }

    @Override
    public void pause() {
        // Pause logic
    }

    @Override
    public void resume() {
        // Resume logic
    }

    @Override
    public void dispose() {
        gameScreen.dispose();
    }

}
