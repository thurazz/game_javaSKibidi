package com.mygdx.game;

import com.badlogic.gdx.math.Vector3;

public class Bullet {

    private Vector3 position;
    private Vector3 direction;
    private float speed = 50f;
    private float range = 1000f;
    private float traveledDistance = 0f;

    public Bullet(Vector3 position, Vector3 direction) {
        this.position = position;
        this.direction = direction.nor();
    }

    public void update(float delta) {
        float distance = speed * delta;
        position.add(direction.cpy().scl(distance));
        traveledDistance += distance;
    }

    public Vector3 getPosition() {
        return position;
    }

    public boolean isOutOfRange() {
        return traveledDistance >= range;
    }
}
