package com.adam.lumberjack;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;

import java.util.LinkedList;
import java.util.List;

public class BuildSensor {
    public Body body;
    public TextureRegion[] textures;
    public List<GameObject> overlaping;
    private boolean bridgeMode = false;

    BuildSensor(Body body, Texture t) {
        this.body = body;
        textures = TextureRegion.split(t , t.getWidth() / 2, t.getHeight())[0];
        overlaping = new LinkedList<>();
    }
    public void addOverlaping(GameObject object) {
        overlaping.add(object);
    }
    public void removeOverlaping(GameObject object) {
        overlaping.remove(object);
    }
    public void draw(SpriteBatch batch) {
        int i;
        i = overlaping.isEmpty() ? 0 : 1;
        if (bridgeMode) {
            i = i == 0 ? 1 : 0;
        }
        if (body.getPosition().y > Constants.CHUNK_SIZE) {
            i = 1;
        }
        batch.draw(textures[i], body.getPosition().x - Constants.TILE_SIZE / 2, body.getPosition().y - Constants.TILE_SIZE / 2, Constants.TILE_SIZE / 5 * 4, Constants.TILE_SIZE / 5 * 4);
    }
    public void setBridgeMode() {
        Filter f1 = body.getFixtureList().first().getFilterData();
        f1.maskBits = (short) (ObjectCategory.RIVER.getValue());
        this.body.getFixtureList().first().setFilterData(f1);
        bridgeMode = true;
    }
    public void setDefaultMode() {
        Filter f1 = body.getFixtureList().first().getFilterData();
        //biggest short value to match all...
        f1.maskBits = 32767;
        this.body.getFixtureList().first().setFilterData(f1);
        bridgeMode = false;
    }
    public Body getBody() {
        return body;
    }
}
