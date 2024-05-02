package com.mygdx.rhythm_game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.audio.Music;
public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	
	@Override
	public void create () {
		/*batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");*/
		Music audio = Gdx.audio.newMusic(Gdx.files.internal("assets/Patatum.mp3"));
		audio.play();
	}

	@Override
	public void render () {
		ScreenUtils.clear(1, 1, 1, 1);
		/*batch.begin();
		batch.draw(img, 0, 0);
		batch.end();*/
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}
