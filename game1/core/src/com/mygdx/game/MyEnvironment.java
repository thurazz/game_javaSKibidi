package com.mygdx.game;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class MyEnvironment {

    private Environment environment;
    private ModelLoader modelLoader;

    public MyEnvironment() {

        environment = new Environment();
        // Customize environment settings (lighting, fog, etc.) if needed

        modelLoader = new ModelLoader(new World(new Vector2(0, -9.8f),true));
    }

    public void render(ModelBatch modelBatch, Camera camera) {

        modelBatch.begin(camera);

        modelLoader.render(modelBatch);

        modelBatch.end();
    }
}
