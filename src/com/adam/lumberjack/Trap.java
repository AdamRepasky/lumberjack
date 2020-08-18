package com.adam.lumberjack;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class Trap extends GameObject {
    boolean active;
    private int cutDamage = 0;
    private int damage;
    private Mob target;
    public Trap(World world, float x, float y, TextureRegion active, TextureRegion inactive, float widthRatio, float heightRatio, Hitbox hitbox, int maxHealth, int damage, Chunk ownerChunk) {
        super(active, inactive, widthRatio, heightRatio, world, hitbox, maxHealth, x, y, ownerChunk);
        this.active = true;
        this.damage = damage;
        this.setDrawPriority(0);
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
        fixtureDef.isSensor = true;
        fixtureDef.shape = hitbox.shape;
        fixtureDef.filter.categoryBits = ObjectCategory.TRAP.getValue();
        fixtureDef.filter.maskBits = (short) (ObjectCategory.PLAYER_SENSOR.getValue() | ObjectCategory.PLAYER.getValue());

        this.fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);
        fixture.setSensor(true);
    }
    public void activate(int selfDamage, Player p) {
        if (this.alive) {
            p.takeDamage(damage);
            this.takeDamage(selfDamage);
        }
    }
    public void activate(Player p) {
        activate(0, p);
    }
    @Override
    public void destroy() {
        super.destroy();
        this.setActiveTextureIndex(0, 1);
    }
}
