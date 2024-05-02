package com.mygdx.rhythm_game;

import com.badlogic.gdx.Game;
import com.mygdx.rhythm_game.gamescreen;

public class MyGame extends Game {

    @Override
    public void create() {
        setScreen(new gamescreen(this)); // Pass the Game instance to the screen
    }

    @Override
    public void dispose() {
        super.dispose();
        getScreen().dispose();
    }
}
