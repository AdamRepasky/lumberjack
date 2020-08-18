package com.adam.lumberjack;

import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;

public class Hitbox {
    public Shape shape;
    public float width;
    public float height;

    public Hitbox(float width, float height) {
        PolygonShape r = new PolygonShape();
        r.setAsBox(width /2, height / 2);
        this.shape = r;
        this.width = width;
        this.height = height;
    }
    public Hitbox(float width, float height, float radius) {
        this.shape = new CircleShape();
        shape.setRadius(radius);
        this.width = width;
        this.height = height;
    }
}
