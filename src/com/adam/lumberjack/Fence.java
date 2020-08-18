package com.adam.lumberjack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class Fence extends GameObject {
    private int loot;
    private int cutDamage = 0;

    public Fence(World world, float x, float y, TextureRegion texture, TextureRegion destroyed, float widthRatio, float heightRatio, Hitbox hitbox, int maxHealth, int loot, Chunk ownerChunk) {
        super(texture, destroyed, widthRatio, heightRatio, world, hitbox, maxHealth, x, y, ownerChunk);
        this.loot = loot;
        this.pos.y -= this.getHeight() / 50;
    }

    @Override
    public void update() {
        if (cutDamage > 0) {
            takeDamage(cutDamage);
        }
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
        fixtureDef.filter.categoryBits = ObjectCategory.FENCE.getValue();
        fixtureDef.filter.maskBits = (short) (ObjectCategory.PLAYER_SENSOR.getValue() | ObjectCategory.PLAYER.getValue() | ObjectCategory.MOB.getValue());

        this.fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);
    }
    public void dropLoot(float radius) {
        try {
            getOwnerChunk().addObjects(GameUtils.dropLoot(loot, this.body.getPosition().x - radius, this.body.getPosition().y  - radius / 2,
                    this.body.getPosition().x + radius, this.body.getPosition().y + radius / 2, this.body.getWorld(), this.getOwnerChunk()));
        } catch (NullPointerException e){
            System.out.println("cant find ownerChunk");
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        this.setActiveTextureIndex(0, 1);
        dropLoot(2f);
        this.body.setAwake(false);
        this.fixture.setSensor(true);
    }
}
