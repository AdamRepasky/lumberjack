package com.adam.lumberjack;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class River extends GameObject {
    public River(TextureRegion texture, float widthRatio, float heightRatio, World world, Hitbox hitbox, int maxHealth, float x, float y, Chunk ownerChunk) {
        super(texture, widthRatio, heightRatio, world, hitbox, maxHealth, x, y, ownerChunk);
    }
    @Override
    protected void createBody(World world, float x, float y) {
        BodyDef def1 = new BodyDef();
        def1.type = BodyDef.BodyType.StaticBody;
        def1.position.set(x, y);
        this.body = world.createBody(def1);
        body.setUserData(this);

        this.fixtureDef = new FixtureDef();
        fixtureDef.shape = hitbox.shape;
        fixtureDef.filter.categoryBits = ObjectCategory.RIVER.getValue();
        fixtureDef.filter.maskBits = (short) (ObjectCategory.MOB.getValue() | ObjectCategory.PLAYER.getValue() | ObjectCategory.BUILD_SENSOR.getValue());

        this.fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);
    }
}
